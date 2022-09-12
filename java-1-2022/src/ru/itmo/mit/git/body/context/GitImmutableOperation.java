package ru.itmo.mit.git.body.context;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;

/*
Интерфейс для немутирующих операций над контекстом
 */

public interface GitImmutableOperation {
    void execute(@NotNull GitContext gitContext, String argument) throws GitException;
}
