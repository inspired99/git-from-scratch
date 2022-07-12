package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.GitManager;

import java.util.List;

/*
Абстрактный класс для команды
 */

public abstract class GitAbstractCommand {
    protected final GitManager gitManager;

    public GitAbstractCommand(GitManager gitManager) {
        this.gitManager = gitManager;
    }

    public abstract void execute(@NotNull List<String> args);

    public abstract void prettyPrint();

    public void checkArgs(@NotNull List<String> args) throws GitException {}
}
