package ru.itmo.mit.git.body;

/*
Класс для управления состояниями гита - загрузки и сохранения индекса и рабочей директории, репозитория.
 */

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.objects.*;
import ru.itmo.mit.git.utils.CommandMessages;
import ru.itmo.mit.git.utils.FileManager;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class GitStateManager {
    private final GitManager gitManager;

    public GitStateManager(GitManager gitManager) {
        this.gitManager = gitManager;
    }
    public void saveHeadState(@NotNull String content) throws GitException {
        FileManager.writeFileByPath(Path.of(gitManager.getGitPathsHolder().getPathToHead()), content);
    }

    public List<String> loadBranchesState() {
        return gitManager.getGitPathsHolder().readAllBranches();
    }

    public void clearBranchState(@NotNull String branchName) throws GitException {
        if (gitManager.getCurrentBranch() != null) {
            if (gitManager.getCurrentBranch().getBranchName().equals(branchName)) {
                throw new GitException("Could not remove checked out branch: " + branchName);
            }
        }
        FileManager.deleteFileByPath(gitManager.getGitPathsHolder().pathToBranchByName(branchName));
    }

    public @NotNull List<String> mergeBranchStateWithCurrent(@NotNull String branchName) throws GitException {
        if (gitManager.isInDetachedHeadState()) {
            throw new GitException("Could not merge: currently is in detached HEAD state");
        }
        if (gitManager.getCurrentBranch() != null) {
            if (gitManager.getCurrentBranch().getBranchName().equals(branchName)) {
                throw new GitException("Could not merge: the branch is already current");
            }
        }
        List<String> mergeConflicts = new ArrayList<>();
        HashMap<String, String> conflictsSolved = new HashMap<>();
        GitPathsHolder gitFileManager = gitManager.getGitPathsHolder();
        GitIndex index = gitManager.getGitIndex();
        GitBranch branchToMerge = gitFileManager.getBranchByName(branchName);
        GitCommit commitToMerge = gitFileManager.getCommitByPath(Path.of(gitFileManager.getPathByHash(branchToMerge
                .getHashObject())));
        HashMap<String, String> filesInCommit = gitFileManager.getTreeByPath(Path.of(gitFileManager.
                getPathByHash(commitToMerge.getRootTreeHash()))).getBlobsHashes();
        HashMap<String, String> filesInRepo = getRootTreeSnapshot().getBlobsHashes();
        gitManager.setCurrentBranch((GitBranch) gitManager.getHead());
        for (var file : filesInRepo.entrySet()) {
            if (!filesInCommit.containsKey(file.getKey())) {
                index.getGitIndexTracked().put(file.getKey(), file.getValue());
            } else {
                if (!filesInCommit.get(file.getKey()).equals(file.getValue())) {
                    String newHash = fixConflictMergeStates(file.getValue(), filesInCommit.get(file.getKey()),
                            file.getKey());
                    mergeConflicts.add(file.getKey());
                    conflictsSolved.put(file.getKey(), file.getValue());
                    getRootTreeSnapshot().getBlobsHashes().put(file.getKey(), newHash);
                    index.getGitIndexTracked().put(file.getKey(), newHash);
                    filesInRepo.put(file.getKey(), newHash);
                }
            }
        }
        for (var file : filesInCommit.entrySet()) {
            if (!filesInRepo.containsKey(file.getKey())) {
                index.getGitIndexTracked().put(file.getKey(), file.getValue());
            }
        }
        getRootTreeSnapshot().getBlobsHashes().putAll(conflictsSolved);
        saveNewRepoState(CommandMessages.COMMIT_DEFAULT_AUTHOR.getMessage(), "Merge " + branchName +
                " into " + gitManager.getCurrentBranch().getBranchName());
        return mergeConflicts;
    }

    public String fixConflictMergeStates(@NotNull String hashFirst, @NotNull String hashSecond, @NotNull String
            pathFile) throws GitException {
        GitPathsHolder gitFileManager = gitManager.getGitPathsHolder();
        String contentFirst = (String) FileManager
                .readFromJsonFile(Path.of(gitFileManager.getPathByHash(hashFirst))).get(GitConstants.CONTENT);
        String contentSecond = (String) FileManager
                .readFromJsonFile(Path.of(gitFileManager.getPathByHash(hashSecond))).get(GitConstants.CONTENT);
        FileManager.deleteFileByPath(Path.of(pathFile));
        GitBlob blob = new GitBlob(contentFirst + "\n" + contentSecond,
                Path.of(pathFile).getFileName().toString());
        FileManager.writeFileByPath(Path.of(pathFile), contentFirst + "\n" + contentSecond);
        gitFileManager.writeBlob(blob);
        return blob.getHashObject();
    }

    public void initState() throws GitException {
        GitPathsHolder gitPathsHolder = gitManager.getGitPathsHolder();
        gitPathsHolder.createGitDirs();
        GitCommit initCommit = new GitCommit(CommandMessages.COMMIT_DEFAULT_AUTHOR.getMessage(), "Initial commit",
                "0", "0");
        gitPathsHolder.writeCommit(initCommit);
        GitBranch master = new GitBranch(GitConstants.MASTER, initCommit.getHashObject());
        gitManager.setCurrentBranch(master);
    }

    public void saveIndexState(@NotNull GitIndex gitIndex) throws GitException {
        StringBuilder tracked = new StringBuilder();
        for (var file : gitIndex.getGitIndexTracked().entrySet()) {
            tracked.append(file.getKey()).append(" ").append(file.getValue()).append("\n");
        }
        FileManager.writeFileByPath(Path.of(gitManager.getGitPathsHolder().getPathToIndex()), tracked.toString());
    }

    public void loadIndexState(@NotNull GitManager gitManager) throws GitException {
        String content = FileManager.readFromFile(Path.of(gitManager.getGitPathsHolder().getPathToIndex()));
        String[] splitString = content.split("\n");
        for (var str : splitString) {
            gitManager.getGitPathsHolder().readIndex(gitManager.getGitIndex(), str);
        }
    }

    public void loadHeadState() throws GitException {
        GitPathsHolder gitPathsHolder = gitManager.getGitPathsHolder();
        String headContent = FileManager.readFromFile(Path.of(gitPathsHolder.getPathToHead())).strip();
        GitObject head;
        if (gitPathsHolder.checkBranchExists(headContent)) {
            head = gitPathsHolder.getBranchByName(headContent);
            gitManager.setCurrentBranch((GitBranch) head);
        } else {
            head = gitPathsHolder.getCommitByPath(Path.of(gitPathsHolder.getPathByHash(headContent)));
        }
        gitManager.setHead(head);
    }

    public void setIndexAndDirStateToRepo(boolean isSafe) throws GitException {
        HashMap<String, String> files = getRootTreeSnapshot().getBlobsHashes();
        GitIndex index = gitManager.getGitIndex();
        File[] dirs = new File(gitManager.getWorkingDir()).listFiles();
        List<File> subDirs = new ArrayList<>();
        if (dirs != null) {
            if (isSafe) {
                subDirs = Arrays.stream(dirs)
                        .filter(x -> !x.getAbsolutePath().equals(gitManager.getGitPathsHolder().getMainDir()))
                        .filter(x -> files.containsKey(x.getAbsolutePath()))
                        .collect(Collectors.toList());
            } else {
                subDirs = Arrays.stream(dirs)
                        .filter(x -> !x.getAbsolutePath().equals(gitManager.getGitPathsHolder().getMainDir()))
                        .collect(Collectors.toList());
            }
        }
        index.clearIndex();
        FileManager.deleteAll(subDirs);
        for (var file : files.entrySet()) {
            new File(file.getKey()).mkdirs();
            FileManager.createFileByPath(Path.of(file.getKey()));
            GitBlob blob = gitManager.getGitPathsHolder().readBlob(file.getValue());
            FileManager.writeFileByPath(Path.of(file.getKey()), blob.getContentBlob().strip());
            gitManager.addToIndex(file.getKey());
        }
    }

    public @NotNull HashMap<String, String> getWorkingDirSnapshot() throws GitException {
        HashMap<String, String> workingDirHashes = new HashMap<>();
        GitPathsHolder gitPathManager = gitManager.getGitPathsHolder();
        gitPathManager.traverseBlobsToMap(workingDirHashes, gitPathManager.getWorkingDir());
        return workingDirHashes;
    }

    public GitTree getRootTreeSnapshot() throws GitException {
        GitPathsHolder gitPathsManager = gitManager.getGitPathsHolder();
        GitObject head = gitManager.getHead();
        String hash = head.getHashObject();
        String hashTree;
        if (head.getTypeObject().equals(GitObjectType.COMMIT)) {
            hashTree = ((GitCommit) head).getRootTreeHash();
        } else {
            GitCommit commit = gitPathsManager.getCommitByPath(Path.of(gitPathsManager.getPathByHash(hash)));
            hashTree = commit.getRootTreeHash();
        }
        if (hashTree.equals("0")) {
            GitTree treeInit = new GitTree();
            treeInit.setPath(gitPathsManager.getWorkingDir());
            return treeInit;
        }
        return gitPathsManager.getTreeByPath(Path.of(gitPathsManager.getPathByHash(hashTree)));
    }

    public @NotNull HashMap<String, String> saveNewRepoState(@NotNull String commitAuthor,
                                                             @NotNull String commitMessage) throws GitException {
        GitObject head = gitManager.getHead();
        String prevHash = head.getHashObject();
        GitIndex index = gitManager.getGitIndex();
        HashMap<String, String> filesInRepo = gitManager.getRootTreeSnapshot().getBlobsHashes();
        HashMap<String, String> filesStaged = index.getGitIndexTracked();
        HashMap<String, String> filesToBeCommitted = new HashMap<>();
        List<String> toBeRemoved = new ArrayList<>();
        for (var fileInRepo : filesInRepo.entrySet()) {
            if (filesStaged.containsKey(fileInRepo.getKey())) {
                if (!filesStaged.get(fileInRepo.getKey()).equals(fileInRepo.getValue())) {
                    filesToBeCommitted.put(fileInRepo.getKey(), fileInRepo.getValue());
                }
            } else {
                toBeRemoved.add(fileInRepo.getKey());
                filesToBeCommitted.put(fileInRepo.getKey(), fileInRepo.getValue());
            }
        }
        for (var fileStaged : filesStaged.entrySet()) {
            if (!filesInRepo.containsKey(fileStaged.getKey())) {
                filesToBeCommitted.put(fileStaged.getKey(), fileStaged.getValue());
            }
        }
        if (!filesToBeCommitted.isEmpty()) {
            HashMap<String, String> realFiles = new HashMap<>(filesToBeCommitted);
            for (var oddFile : toBeRemoved) {
                realFiles.remove(oddFile);
                filesInRepo.remove(oddFile);
            }
            filesInRepo.putAll(realFiles);
            GitTree gitTree = new GitTree(gitManager.getWorkingDir(), filesInRepo);
            gitManager.getGitPathsHolder().writeTree(gitTree);
            GitCommit commit = new GitCommit(commitAuthor, commitMessage, prevHash, gitTree.getHashObject());
            gitManager.getGitPathsHolder().writeCommit(commit);
            gitManager.setRootTreeSnapshot(gitTree);
            if (!gitManager.isInDetachedHeadState()) {
                GitBranch branch = (GitBranch) gitManager.getHead();
                branch.setHashObject(commit.getHashObject());
                updateBranchState(branch.getBranchName(), branch.getHashObject());
                updateHeadState(branch);
            } else {
                gitManager.setHead(commit);
                updateHeadState(commit);
            }
            index.getGitIndexTracked().clear();
            index.getGitIndexTracked().putAll(filesInRepo);
        }
        return filesToBeCommitted;
    }

    public void updateBranchState(@NotNull String branchName, @NotNull String hash) throws GitException {
        GitBranch branch = gitManager.getGitPathsHolder().getBranchByName(branchName);
        branch.setHashObject(hash);
        gitManager.getGitPathsHolder().writeBranch(branch);
    }

    public void addNewBranchState(@NotNull String name) throws GitException {
        GitBranch newBranch = new GitBranch(name, gitManager.getHead().getHashObject());
        gitManager.getGitPathsHolder().writeBranch(newBranch);
        gitManager.setHead(newBranch);
        gitManager.setCurrentBranch(newBranch);
    }

    public void loadFileStateFromRepo(@NotNull String fileName) throws GitException {
        GitPathsHolder gitFileManager = gitManager.getGitPathsHolder();
        String pathToFile = gitFileManager.pathInGitDir(Path.of(fileName)).toString();
        GitIndex index = gitManager.getGitIndex();
        HashMap<String, String> commitFiles = gitManager.getRootTreeSnapshot().getBlobsHashes();
        if (!commitFiles.containsKey(pathToFile)) {
            throw new GitException("No file version to checkout ");
        }
        if (index.getGitIndexTracked().containsKey(pathToFile)) {
            index.getGitIndexTracked().put(pathToFile, commitFiles.get(pathToFile));
        }
        FileManager.deleteFileByPath(Path.of(pathToFile));
        GitBlob blob = gitFileManager.readBlob(commitFiles.get(pathToFile));
        FileManager.writeFileByPath(Path.of(pathToFile), blob.getContentBlob());
    }

    public void updateHeadState(@NotNull GitObject object) throws GitException {
        if (object.getTypeObject().equals(GitObjectType.COMMIT)) {
            if (object instanceof GitCommit) {
                saveHeadState(object.getHashObject());
                gitManager.setHead(object);
                gitManager.getGitPathsHolder().writeCommit((GitCommit) object);
            }
        } else if (object.getTypeObject().equals(GitObjectType.BRANCH)) {
            if (object instanceof GitBranch) {
                saveHeadState(((GitBranch) object).getBranchName());
                gitManager.setHead(object);
                gitManager.getGitPathsHolder().writeBranch((GitBranch) object);
            }
        }
    }
}
