package ru.itmo.mit.git.body;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.objects.GitBlob;
import ru.itmo.mit.git.utils.FileManager;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

/*
Класс для управления staging area
 */

public class GitIndex {
    private final HashMap<String, String> gitIndexTracked;
    private final GitManager gitManager;

    public GitIndex(@NotNull GitManager gitManager) {
        this.gitIndexTracked = new HashMap<>();
        this.gitManager = gitManager;
    }

    public void removeFromIndex(@NotNull String file) throws GitException {
        GitPathsHolder gitPathsHolder = gitManager.getGitPathsHolder();
        String absPath = gitPathsHolder.pathInGitDir(Path.of(file)).toString();
        if (new File(absPath).isDirectory()) {
            throw new GitException("Cannot remove directories, specify file(s) ");
        }
        if ((gitIndexTracked.remove(absPath) == null)) {
            throw new GitException("No such file can be removed: " + file);
        }
    }

    public void addToIndex(@NotNull String file) throws GitException {
        GitPathsHolder gitPathsHolder = gitManager.getGitPathsHolder();
        Path pathToFile = Path.of(file);
        if (!FileManager.checkFileExists(gitPathsHolder.pathInGitDir(pathToFile))) {
            if (gitIndexTracked.containsKey(gitPathsHolder.pathInGitDir(pathToFile).toString())) {
                gitIndexTracked.remove(gitPathsHolder.pathInGitDir(pathToFile).toString());
                return;
            } else {
                throw new GitException("No such file can be added: " + file);
            }
        }
        List<File> dirs = FileManager.listDirsByPath(gitPathsHolder.pathInGitDir(pathToFile));
        List<File> files = FileManager.listFilesByPath(gitPathsHolder.pathInGitDir(pathToFile));
        if (!files.isEmpty()) {
            for (var fileName : files) {
                String absPath = fileName.getAbsolutePath();
                GitBlob newBlob = new GitBlob(FileManager.readFromFile(Path.of(absPath)),
                        fileName.getName());
                String hash = newBlob.getHashObject();
                if (gitIndexTracked.containsKey(absPath)) {
                    if (gitIndexTracked.get(absPath).equals(hash)) {
                        if (FileManager.checkFileExists(Path.of(gitPathsHolder.getPathByHash(hash)))) {
                            continue;
                        }
                    }
                }
                gitIndexTracked.put(absPath, hash);
                gitPathsHolder.writeBlob(newBlob);
            }
        }
        if (dirs.size() > 1) {
            for (int i = 1; i < dirs.size(); ++i) {
                addToIndex(file + File.separator + dirs.get(i).getName());
            }
        }
    }

    public GitManager getGitManager() {
        return gitManager;
    }

    public HashMap<String, String> getGitIndexTracked() {
        return gitIndexTracked;
    }

    public void clearIndex() {
        gitIndexTracked.clear();
    }
}
