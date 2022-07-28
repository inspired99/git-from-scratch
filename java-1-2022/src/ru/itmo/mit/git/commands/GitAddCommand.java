package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.GitManager;
import ru.itmo.mit.git.utils.CommandMessages;

import java.util.List;

public class GitAddCommand extends GitAbstractCommand {
    public GitAddCommand(GitManager gitManager) {
        super(gitManager);
    }

    @Override
    public void execute(@NotNull List<String> args) throws GitException {
        gitManager.loadGitState();
        for (var file : args) {
            gitManager.addToIndex(file);
        }
        gitManager.saveGitState(gitManager.getHead());
    }

    @Override
    public void prettyPrint(@NotNull List<String> args) {
        StringBuilder message = new StringBuilder(CommandMessages.ADD_MESSAGE.getMessage());
        for (var file : args) {
            message.append("\n").append(file);
        }
        gitManager.getPrintStream().println(message);
    }

    @Override
    public void checkArgs(@NotNull List<String> args) throws GitException {
        if (args.isEmpty()) {
            throw new GitException("Wrong number of arguments for add command ");
        }
    }
}
