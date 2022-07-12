package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.GitManager;

import java.util.List;

public class GitCommitCommand extends GitAbstractCommand{
    public GitCommitCommand(GitManager gitManager) {
        super(gitManager);
    }

    @Override
    public void execute(@NotNull List<String> args) {

    }

    @Override
    public void prettyPrint() {

    }

    @Override
    public void checkArgs(@NotNull List<String> args) throws GitException {
        if (args.isEmpty()) {
            throw new GitException("No commit message provided ");
        }
    }
}
