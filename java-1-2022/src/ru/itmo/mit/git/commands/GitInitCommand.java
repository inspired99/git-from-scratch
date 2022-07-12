package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.GitManager;
import ru.itmo.mit.git.utils.CommandMessages;

import java.util.List;

public class GitInitCommand extends GitAbstractCommand {
    public GitInitCommand(GitManager gitManager) {
        super(gitManager);
    }

    @Override
    public void execute(@NotNull List<String> args) {
    }

    @Override
    public void prettyPrint() {
        System.out.println(CommandMessages.INIT_MESSAGE.getMessage() + gitManager.getWorkingDir());
    }

    @Override
    public void checkArgs(@NotNull List<String> args) throws GitException {
        if (!args.isEmpty()) {
            throw new GitException("Wrong number of arguments for init command ");
        }
    }
}
