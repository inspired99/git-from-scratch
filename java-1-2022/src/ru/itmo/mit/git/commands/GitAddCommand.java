package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.context.GitContext;
import ru.itmo.mit.git.utils.CommandMessages;

import java.util.List;

public class GitAddCommand extends GitAbstractCommand {
    public GitAddCommand(GitContext gitContext) {
        super(gitContext);
    }

    @Override
    public void execute(@NotNull List<String> args) throws GitException {
        loadState();
        for (var file : args) {
            gitContext.getGitIndexManager().addToIndex().execute(gitContext, file);
        }
        updateState(gitContext.getGitRepoManager().getHead());
    }

    @Override
    public void prettyPrint(@NotNull List<String> args) {
        StringBuilder message = new StringBuilder(CommandMessages.ADD_MESSAGE.getMessage());
        for (var file : args) {
            message.append("\n").append(file);
        }
        outputStream.println(message);
    }

    @Override
    public void checkArgs(@NotNull List<String> args) throws GitException {
        if (args.isEmpty()) {
            throw new GitException("Wrong number of arguments for add command ");
        }
    }
}
