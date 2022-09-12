package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.context.GitContext;
import ru.itmo.mit.git.utils.CommandMessages;

import java.util.HashMap;
import java.util.List;

public class GitCommitCommand extends GitAbstractCommand {
    private HashMap<String, String> changedFiles = new HashMap<>();

    public GitCommitCommand(GitContext gitContext) {
        super(gitContext);
    }

    @Override
    public void execute(@NotNull List<String> args) throws GitException {
        loadState();
        gitContext.getGitRepoManager().commitToRepo().execute(gitContext, String.join(" ", args));
        changedFiles = gitContext.getGitRepoManager().getLatestChanges();
        updateState(gitContext.getGitRepoManager().getHead());
    }

    @Override
    public void prettyPrint(@NotNull List<String> args) {
        StringBuilder message = new StringBuilder();
        if (changedFiles.isEmpty()) {
            message.append(CommandMessages.COMMIT_UP_TO_DATE_MESSAGE.getMessage());
        } else {
            message.append(CommandMessages.COMMIT_MESSAGE.getMessage())
                    .append("\n").append(changedFiles.entrySet().size())
                    .append(" ").append(CommandMessages.COMMIT_CHANGED_FILES.getMessage());
        }
        outputStream.println(message);
    }

    @Override
    public void checkArgs(@NotNull List<String> args) throws GitException {
        if (args.isEmpty()) {
            throw new GitException("No commit message provided ");
        }
    }
}
