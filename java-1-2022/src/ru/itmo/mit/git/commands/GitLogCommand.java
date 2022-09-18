package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.context.GitContext;
import ru.itmo.mit.git.body.context.GitImmutableOperation;
import ru.itmo.mit.git.entities.GitCommit;
import ru.itmo.mit.git.utils.CommandMessages;

import java.util.ArrayList;
import java.util.List;

public class GitLogCommand extends GitAbstractCommand {
    private final List<GitCommit> commitsHashes = new ArrayList<>();

    public GitLogCommand(GitContext gitContext) {
        super(gitContext);
    }

    @Override
    public void execute(@NotNull List<String> args) throws GitException {
        loadState();
        String commitToStart = gitContext.getGitRepoManager().getHead().getHash();
        if (!args.isEmpty()) {
            commitToStart = args.get(0);
        }
        LogCommits.execute(gitContext, commitToStart);
    }

    @Override
    public void prettyPrint(@NotNull List<String> args) {
        StringBuilder message = new StringBuilder();
        for (var commit : commitsHashes) {
            message.append(CommandMessages.LOG_COMMIT_MESSAGE.getMessage()).append(commit.getHash()).append("\n")
                    .append(CommandMessages.LOG_AUTHOR_MESSAGE.getMessage()).append(commit.getAuthorCommit())
                    .append("\n").append(CommandMessages.LOG_DATE_MESSAGE.getMessage()).append(commit.getDateCommit())
                    .append("\n").append(commit.getMessageCommit()).append("\n")
                    .append(CommandMessages.LOG_SEPARATOR.getMessage()).append("\n");
        }
        outputStream.println(message);
    }

    @Override
    public void checkArgs(@NotNull List<String> args) throws GitException {
        if (args.size() > 1) {
            throw new GitException("Wrong number of arguments for log command ");
        }
    }

    GitImmutableOperation LogCommits = (gitContext, commitHash) -> {
        while (!commitHash.equals("0")) {
            GitCommit nextCommit = gitContext.getGitFileManager().getGitCommitFileManager().readByHash(commitHash);
            commitsHashes.add(nextCommit);
            commitHash = nextCommit.getParentCommitHash();
        }
    };
}
