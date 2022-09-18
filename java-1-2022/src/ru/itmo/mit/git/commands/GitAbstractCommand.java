package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.context.GitContext;
import ru.itmo.mit.git.entities.GitEntity;

import java.io.PrintStream;
import java.util.List;

/*
Абстрактный класс для git command - каждая команда является применением мутирующей либо немутирующей операции
над контекстом
 */

public abstract class GitAbstractCommand {
    protected final GitContext gitContext;
    protected PrintStream outputStream;

    public GitAbstractCommand(GitContext gitContext) {
        this.gitContext = gitContext;
    }

    public abstract void execute(@NotNull List<String> args) throws GitException;

    public abstract void prettyPrint(@NotNull List<String> args);

    public void checkArgs(@NotNull List<String> args) throws GitException {
    }

    public void setOutputStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    protected void loadState() throws GitException {
        gitContext.getGitIndexStateManager().loadState(gitContext);
        gitContext.getGitRepoStateManager().loadState(gitContext);
    }

    protected void updateState(GitEntity nextHead) throws GitException {
        gitContext.getGitIndexStateManager().saveState(gitContext);
        gitContext.getGitRepoStateManager().updateState(gitContext, nextHead);
    }
}
