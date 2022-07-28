package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.GitManager;
import ru.itmo.mit.git.body.GitPathsHolder;
import ru.itmo.mit.git.objects.GitCommit;
import ru.itmo.mit.git.utils.CommandMessages;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GitLogCommand extends GitAbstractCommand {
    private final List<GitCommit> commitsHashes = new ArrayList<>();

    public GitLogCommand(GitManager gitManager) {
        super(gitManager);
    }

    @Override
    public void execute(@NotNull List<String> args) throws GitException {
        gitManager.loadGitState();
        String hashCommit;
        if (!args.isEmpty()) {
            hashCommit = args.get(0);
        } else {
            hashCommit = gitManager.getHead().getHashObject();
        }
        while (!hashCommit.equals("0")) {
            GitPathsHolder gitFileManager = gitManager.getGitPathsHolder();
            GitCommit nextCommit = gitFileManager.getCommitByPath(Path.of(gitFileManager.getPathByHash(hashCommit)));
            commitsHashes.add(nextCommit);
            hashCommit = nextCommit.getParentCommitHash();
        }
    }

    @Override
    public void prettyPrint(List<String> args) {
        StringBuilder message = new StringBuilder();
        for (var commit : commitsHashes) {
            message.append(CommandMessages.LOG_COMMIT_MESSAGE.getMessage()).append(commit.getHashObject()).append("\n")
                    .append(CommandMessages.LOG_AUTHOR_MESSAGE.getMessage()).append(commit.getAuthorCommit())
                    .append("\n").append(CommandMessages.LOG_DATE_MESSAGE.getMessage()).append(commit.getDateCommit())
                    .append("\n").append(commit.getMessageCommit()).append("\n")
                    .append(CommandMessages.LOG_SEPARATOR.getMessage()).append("\n");
        }
        gitManager.getPrintStream().println(message);
    }

    @Override
    public void checkArgs(@NotNull List<String> args) throws GitException {
        if (args.size() > 1) {
            throw new GitException("Wrong number of arguments for log command ");
        }
    }
}
