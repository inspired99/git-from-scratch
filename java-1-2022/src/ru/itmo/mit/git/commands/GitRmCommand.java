package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.GitManager;
import ru.itmo.mit.git.utils.CommandMessages;

import java.util.List;

public class GitRmCommand extends GitAbstractCommand{
    public GitRmCommand(GitManager gitManager) {
        super(gitManager);
    }

    @Override
    public void execute(@NotNull List<String> args) {

    }

    @Override
    public void prettyPrint(@NotNull List<String> args) {
        StringBuilder message = new StringBuilder(CommandMessages.REMOVE_MESSAGE.getMessage());
        for (var file : args) {
            message.append("\n");
            message.append(file);
        }
        gitManager.getPrintStream().println(message);
    }

    @Override
    public void checkArgs(@NotNull List<String> args) throws GitException {
        if (args.isEmpty()) {
            throw new GitException("Wrong number of arguments for rm command ");
        }
    }
}
