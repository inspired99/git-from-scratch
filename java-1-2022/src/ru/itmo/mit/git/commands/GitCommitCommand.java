package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.GitManager;
import ru.itmo.mit.git.utils.CommandMessages;

import java.util.HashMap;
import java.util.List;

public class GitCommitCommand extends GitAbstractCommand {
    private HashMap<String, String> changedFiles = new HashMap<>();

    public GitCommitCommand(GitManager gitManager) {
        super(gitManager);
    }

    @Override
    public void execute(@NotNull List<String> args) throws GitException {
        gitManager.loadGitState();
        changedFiles = gitManager.commitToRepo(CommandMessages.COMMIT_DEFAULT_AUTHOR.getMessage(),
                String.join(" ", args));
        gitManager.saveGitState(gitManager.getHead());
    }

    @Override
    public void prettyPrint(List<String> args) {
        StringBuilder message = new StringBuilder();
        if (changedFiles.isEmpty()) {
            message.append(CommandMessages.COMMIT_FAIL_MESSAGE.getMessage());
        } else {
            message.append(CommandMessages.COMMIT_MESSAGE.getMessage())
                    .append("\n").append(changedFiles.entrySet().size())
                    .append(" ").append(CommandMessages.COMMIT_CHANGED_FILES.getMessage());
        }
        gitManager.getPrintStream().println(message);
    }

    @Override
    public void checkArgs(@NotNull List<String> args) throws GitException {
        if (args.isEmpty()) {
            throw new GitException("No commit message provided ");
        }
    }
}
