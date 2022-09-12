package ru.itmo.mit.git.body.stateSaversAndLoaders;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.context.GitContext;

/*
Интерфейс, реализующий функции считывания и сохранения состояний составляющих гита (индекса, репозитория или
рабочей директории)
 */

public interface GitStateHelper {
    void loadState(@NotNull GitContext gitContext) throws GitException;

    default void saveState(@NotNull GitContext gitContext) throws GitException {}
}
