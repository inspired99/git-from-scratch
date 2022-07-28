package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.GitManager;
import ru.itmo.mit.git.body.GitStatusInfo;
import ru.itmo.mit.git.utils.CommandMessages;
import ru.itmo.mit.git.utils.FileManager;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class GitStatusCommand extends GitAbstractCommand {
    private final GitStatusInfo statusInfo = new GitStatusInfo();

    public GitStatusCommand(GitManager gitManager) {
        super(gitManager);
    }

    @Override
    public void execute(@NotNull List<String> args) throws GitException {
        gitManager.loadGitState();
        statusInfo.fillHashes(gitManager.getWorkingDirSnapshot(),
                gitManager.getGitIndex().getGitIndexTracked(),
                gitManager.getRootTreeSnapshot().getBlobsHashes());
    }

    @Override
    public void prettyPrint(List<String> args) {
        StringBuilder message = new StringBuilder();
        message.append(getHeadMessage()).append("\n");
        String stagedFiles = getStagedFiles();
        String untrackedFiles = getUntrackedFiles();
        if (statusInfo.isEmpty()) {
            message.append(CommandMessages.COMMIT_FAIL_MESSAGE.getMessage());
        }
        if (!stagedFiles.isEmpty()) {
            message.append(CommandMessages.STATUS_STAGED_MESSAGE.getMessage())
                    .append("\n")
                    .append(CommandMessages.STATUS_SEPARATOR.getMessage())
                    .append("\n")
                    .append(stagedFiles)
                    .append("\n");
        }
        if (!untrackedFiles.isEmpty()) {
            message.append(CommandMessages.STATUS_NOT_STAGED_MESSAGE.getMessage())
                    .append("\n")
                    .append(CommandMessages.STATUS_SEPARATOR.getMessage())
                    .append("\n")
                    .append(untrackedFiles)
                    .append("\n");
        }
        gitManager.getPrintStream().println(message);
    }

    private @NotNull String getHeadMessage() {
        StringBuilder message = new StringBuilder();
        if (gitManager.isInDetachedHeadState()) {
            message.append(CommandMessages.STATUS_DETACHED_MESSAGE.getMessage());
            message.append(gitManager.getHead().getHashObject());
        } else {
            message.append(CommandMessages.STATUS_CUR_BRANCH_MESSAGE.getMessage());
            message.append((gitManager.getCurrentBranch().getBranchName()));
        }
        message.append("\n");
        return message.toString();
    }

    @Override
    public void checkArgs(@NotNull List<String> args) throws GitException {
        if (!args.isEmpty()) {
            throw new GitException("Wrong number of arguments for status command ");
        }
    }

    private @NotNull String getStagedFiles() {
        StringBuilder stagedMessage = new StringBuilder();
        fillStagedFiles();
        stagedMessage.append(FileManager.prettyPrintMap(statusInfo.getStagedAdded().getFiles(),
                GitConstants.ADDED, false));
        stagedMessage.append(FileManager.prettyPrintMap(statusInfo.getStagedModified().getFiles(),
                GitConstants.MODIFIED, false));
        stagedMessage.append(FileManager.prettyPrintMap(statusInfo.getStagedRemoved().getFiles(),
                GitConstants.REMOVED, false));
        return stagedMessage.toString();
    }

    private void fillStagedFiles() {
        HashMap<String, String> hashInd = statusInfo.getHashInCurInd();
        HashMap<String, String> hashRepo = statusInfo.getHashInCurRepo();
        for (var trackFile : hashInd.entrySet()) {
            if (hashRepo.containsKey(trackFile.getKey())) {
                if (!hashRepo.get(trackFile.getKey()).equals(trackFile.getValue())) {
                    statusInfo.addStageModified(trackFile);
                }
            } else {
                if (FileManager.checkFileExists(Path.of(trackFile.getKey()))) {
                    statusInfo.addStageAdded(trackFile);
                }
            }
        }
        for (var repoFile : hashRepo.entrySet()) {
            if (!statusInfo.getStagedModified().getFiles().containsKey(repoFile.getKey()) &&
                    !statusInfo.getStagedAdded().getFiles().containsKey(repoFile.getKey()) &&
                    !hashInd.containsKey(repoFile.getKey())) {
                statusInfo.addStageRemoved(repoFile);
            }
        }
    }

    private @NotNull String getUntrackedFiles() {
        StringBuilder untrackedMessage = new StringBuilder();
        fillUntrackedFiles();
        untrackedMessage.append(FileManager.prettyPrintMap(statusInfo.getUntrackedAdded().getFiles(),
                GitConstants.ADDED, false));
        untrackedMessage.append(FileManager.prettyPrintMap(statusInfo.getUntrackedModified().getFiles(),
                GitConstants.MODIFIED, false));
        untrackedMessage.append(FileManager.prettyPrintMap(statusInfo.getUntrackedRemoved().getFiles(),
                GitConstants.REMOVED, false));
        return untrackedMessage.toString();
    }

    private void fillUntrackedFiles() {
        HashMap<String, String> hashInd = statusInfo.getHashInCurInd();
        HashMap<String, String> hashDir = statusInfo.getHashInWorkDir();
        for (var trackFile : hashInd.entrySet()) {
            if (hashDir.containsKey(trackFile.getKey())) {
                if (!hashDir.get(trackFile.getKey()).equals(trackFile.getValue())) {
                    statusInfo.addUntrackedModified(trackFile);
                }
            } else {
                statusInfo.addUntrackedRemoved(trackFile);
            }
        }
        for (var dirFile : hashDir.entrySet()) {
            if (!statusInfo.getUntrackedModified().getFiles().containsKey(dirFile.getKey()) &&
                    !statusInfo.getUntrackedRemoved().getFiles().containsKey(dirFile.getKey()) &&
                    !hashInd.containsKey(dirFile.getKey())) {
                statusInfo.addUntrackedAdded(dirFile);
            }
        }
    }
}
