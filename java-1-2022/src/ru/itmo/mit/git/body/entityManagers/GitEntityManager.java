package ru.itmo.mit.git.body.entityManagers;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.GitFileSystemManager;
import ru.itmo.mit.git.entities.GitEntity;
import ru.itmo.mit.git.utils.FileManager;

import java.io.File;
import java.nio.file.Path;

/*
Абстрактный класс для работы с сущностями гита на уровне файлов - каждая сущность (дерево, блоб, ветка или коммит)
имеет собственную реализацию абстрактных методов записи и чтения, также дополнительную функциональность по
необходимости (удаление, изменение и т.д.)
 */

public abstract class GitEntityManager<T extends GitEntity> {
    protected GitFileSystemManager gitFileSystemManager;

    public GitEntityManager(GitFileSystemManager gitFileSystemManager) {
        this.gitFileSystemManager = gitFileSystemManager;
    }

    public abstract void writeEntity(@NotNull T entity) throws GitException;

    public abstract T readByHash(String hash) throws GitException;

    protected @NotNull Path preparePath(@NotNull String hash) throws GitException {
        String dirHashName = hash.substring(0, 2);
        Path pathToObj = Path.of(gitFileSystemManager.getPathToObjects() + File.separator + dirHashName);
        if (!FileManager.checkFileExists(pathToObj)) {
            FileManager.createDirByPath(pathToObj);
        }
        Path finalPath = Path.of(gitFileSystemManager.constructPathByHash(hash));
        if (FileManager.checkFileExists(finalPath)) {
            FileManager.deleteFileByPath(finalPath);
        }
        return finalPath;
    }
}
