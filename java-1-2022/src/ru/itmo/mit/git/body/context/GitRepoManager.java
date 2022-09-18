package ru.itmo.mit.git.body.context;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.entities.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
Класс для управления репозиторием
 */

public class GitRepoManager {
    private final String authorRepo;
    private GitTree rootTreeSnapshot;
    private GitEntity head;
    private HashMap<String, String> changesInCommit;
    private GitBranch currentBranch;

    public GitRepoManager(String authorRepo) {
        this.authorRepo = authorRepo;
    }


    public boolean isInDetachedHeadState() {
        if (head == null) {
            return false;
        }
        return head.getTypeEntity().equals(GitEntityType.COMMIT);
    }

    public HashMap<String, String> getLatestChanges() {
        return changesInCommit;
    }

    public GitBranch getCurrentBranch() {
        return currentBranch;
    }

    public GitTree getRootTreeSnapshot() {
        return rootTreeSnapshot;
    }

    public GitTree makeRootTreeSnapshot(@NotNull GitContext gitContext) throws GitException {
        return gitContext.getGitRepoStateManager().currentStateSnapshot(gitContext);
    }

    public GitEntity getHead() {
        return head;
    }

    public void setHead(GitEntity head) {
        this.head = head;
    }

    public void setCurrentBranch(GitBranch currentBranch) {
        this.currentBranch = currentBranch;
    }

    public GitMutableOperation commitToRepo() {
        return CommitToRepo;
    }

    private final GitMutableOperation CommitToRepo = new GitMutableOperation() {
        @Override
        public void execute(@NotNull GitContext gitContext, String arg) throws GitException {
            changesInCommit = commitToRepo(gitContext, arg);
        }

        private @NotNull HashMap<String, String> commitToRepo(@NotNull GitContext gitContext,
                                                              @NotNull String commitMessage) throws GitException {
            String prevHash = head.getHash();
            GitIndexManager index = gitContext.getGitIndex();
            HashMap<String, String> filesInRepo = gitContext.getGitRepoManager().
                    makeRootTreeSnapshot(gitContext).getBlobsHashes();
            HashMap<String, String> filesStaged = index.getGitIndexTracked();
            HashMap<String, String> filesToBeCommitted = new HashMap<>();
            List<String> toBeRemoved = new ArrayList<>();
            for (var fileInRepo : filesInRepo.entrySet()) {
                if (filesStaged.containsKey(fileInRepo.getKey())) {
                    if (!filesStaged.get(fileInRepo.getKey()).equals(fileInRepo.getValue())) {
                        filesToBeCommitted.put(fileInRepo.getKey(), filesStaged.get(fileInRepo.getKey()));
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
                GitTree gitTree = new GitTree(gitContext.getGitFileManager().getPathToWorkDir(), filesInRepo);
                gitContext.getGitFileManager().getGitTreeFileManager().writeEntity(gitTree);
                GitCommit commit = new GitCommit(authorRepo, commitMessage, prevHash, gitTree.getHash());
                gitContext.getGitFileManager().getGitCommitFileManager().writeEntity(commit);
                rootTreeSnapshot = gitTree;
                if (!isInDetachedHeadState()) {
                    GitBranch branch = (GitBranch) head;
                    branch.setBranchHash(commit.getHash());
                    gitContext.getGitFileManager().getGitBranchFileManager().updateBranch(branch.getBranchName(), branch.getHash());
                    gitContext.getGitRepoStateManager().updateState(gitContext, branch);
                } else {
                    gitContext.getGitRepoStateManager().updateState(gitContext, commit);
                }
                index.getGitIndexTracked().clear();
                index.getGitIndexTracked().putAll(filesInRepo);
            }
            return filesToBeCommitted;
        }
    };

    public @NotNull String getHashRevision(GitContext gitContext, int n) throws GitException {
        if (n < 0) {
            throw new GitException("Number of commits should be non-negative ");
        }
        gitContext.getGitIndexStateManager().loadState(gitContext);
        gitContext.getGitRepoStateManager().loadState(gitContext);
        String hashResult = gitContext.getGitRepoManager().getHead().getHash();
        while (!hashResult.equals("0")) {
            if (n == 0) {
                break;
            }
            GitCommit commit = gitContext.getGitFileManager().getGitCommitFileManager().readByHash(hashResult);
            hashResult = commit.getParentCommitHash();
            n--;
        }
        if (n > 0) {
            throw new GitException("Not enough commits to get revision ");
        }
        return hashResult;
    }
}
