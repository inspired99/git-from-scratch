package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.GitFileSystemManager;
import ru.itmo.mit.git.body.context.GitContext;
import ru.itmo.mit.git.body.context.GitIndexManager;
import ru.itmo.mit.git.body.context.GitMutableOperation;
import ru.itmo.mit.git.entities.GitBlob;
import ru.itmo.mit.git.entities.GitBranch;
import ru.itmo.mit.git.entities.GitCommit;
import ru.itmo.mit.git.utils.CommandMessages;
import ru.itmo.mit.git.utils.FileManager;
import ru.itmo.mit.git.utils.JsonFileManager;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GitMergeBranchCommand extends GitAbstractCommand {
    private final List<String> mergeConflicts = new ArrayList<>();

    public GitMergeBranchCommand(GitContext gitContext) {
        super(gitContext);
    }

    @Override
    public void execute(@NotNull List<String> args) throws GitException {
        loadState();
        MergeBranchWithCurrent.execute(gitContext, args.get(0));
        updateState(gitContext.getGitRepoManager().getHead());
    }

    @Override
    public void prettyPrint(@NotNull List<String> args) {
        StringBuilder message = new StringBuilder();
        message.append(CommandMessages.MERGE_MESSAGE.getMessage()).append(args.get(0))
                .append("\n").append(CommandMessages.MERGE_INTO_MESSAGE.getMessage())
                .append(gitContext.getGitRepoManager().getCurrentBranch().getBranchName());
        if (!mergeConflicts.isEmpty()) {
            message.append("\n").append(CommandMessages.MERGE_CONFLICT_MESSAGE.getMessage())
                    .append(String.join("\n", mergeConflicts));
        }
        outputStream.println(message);
    }

    @Override
    public void checkArgs(@NotNull List<String> args) throws GitException {
        if (args.isEmpty()) {
            throw new GitException("Wrong number of arguments for merge command ");
        }
    }

    private final GitMutableOperation MergeBranchWithCurrent = new GitMutableOperation() {
        private String fixMergeConflicts(@NotNull String hashFirst, @NotNull String hashSecond, @NotNull String
                pathFile) throws GitException {
            GitFileSystemManager gitFileManager = gitContext.getGitFileManager();
            String contentFirst = (String) JsonFileManager
                    .readFromJsonFile(Path.of(gitFileManager.constructPathByHash(hashFirst))).get(GitConstants.CONTENT);
            String contentSecond = (String) JsonFileManager
                    .readFromJsonFile(Path.of(gitFileManager.constructPathByHash(hashSecond))).get(GitConstants.CONTENT);
            FileManager.deleteFileByPath(Path.of(pathFile));
            GitBlob blob = new GitBlob(contentFirst + "\n" + contentSecond,
                    Path.of(pathFile).getFileName().toString());
            FileManager.writeFileByPath(Path.of(pathFile), contentFirst + "\n" + contentSecond);
            gitFileManager.getGitBlobFileManager().writeEntity(blob);
            return blob.getHash();
        }

        @Override
        public void execute(@NotNull GitContext gitContext, String arg) throws GitException {
            if (gitContext.getGitRepoManager().isInDetachedHeadState()) {
                throw new GitException("Could not merge: currently is in detached HEAD state");
            }
            if (gitContext.getGitRepoManager().getCurrentBranch() != null) {
                if (gitContext.getGitRepoManager().getCurrentBranch().getBranchName().equals(arg)) {
                    throw new GitException("Could not merge: the branch is already current");
                }
            }
            HashMap<String, String> conflictsSolved = new HashMap<>();
            GitFileSystemManager gitFileManager = gitContext.getGitFileManager();
            GitIndexManager index = gitContext.getGitIndex();
            GitBranch branchToMerge = gitFileManager.getGitBranchFileManager().getBranchByName(arg);
            GitCommit commitToMerge = gitFileManager.getGitCommitFileManager().readByHash(branchToMerge.getHash());
            HashMap<String, String> filesInCommit = gitFileManager.getGitTreeFileManager().
                    readByHash(commitToMerge.getRootTreeHash()).getBlobsHashes();
            HashMap<String, String> filesInRepo = gitContext.getGitRepoManager().makeRootTreeSnapshot(gitContext)
                    .getBlobsHashes();
            gitContext.getGitRepoManager().setCurrentBranch((GitBranch) gitContext.getGitRepoManager().getHead());
            for (var file : filesInRepo.entrySet()) {
                if (!filesInCommit.containsKey(file.getKey())) {
                    index.getGitIndexTracked().put(file.getKey(), file.getValue());
                } else {
                    if (!filesInCommit.get(file.getKey()).equals(file.getValue())) {
                        String newHash = fixMergeConflicts(file.getValue(), filesInCommit.get(file.getKey()),
                                file.getKey());
                        mergeConflicts.add(file.getKey());
                        conflictsSolved.put(file.getKey(), file.getValue());
                        gitContext.getGitRepoManager().makeRootTreeSnapshot(gitContext).getBlobsHashes()
                                .put(file.getKey(), newHash);
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
            gitContext.getGitRepoManager().makeRootTreeSnapshot(gitContext).getBlobsHashes().putAll(conflictsSolved);
            gitContext.getGitRepoManager().commitToRepo().execute(gitContext, "Merge " + arg +
                    " into " + gitContext.getGitRepoManager().getCurrentBranch().getBranchName());
        }
    };
}
