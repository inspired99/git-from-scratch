package ru.itmo.mit.git.body.context;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;

/*
Интерфейс для мутирующих операций над контекстом
 */

public interface GitMutableOperation {
    void execute(@NotNull GitContext gitContext, String arg) throws GitException;
}
