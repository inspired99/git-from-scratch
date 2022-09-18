package ru.itmo.mit.git.body.context;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.GitFileSystemManager;
import ru.itmo.mit.git.entities.GitBlob;
import ru.itmo.mit.git.utils.FileManager;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

/*
Класс для управления staging area - index
 */

public class GitIndexManager {
    private final HashMap<String, String> gitIndexTracked;

    public GitIndexManager() {
        this.gitIndexTracked = new HashMap<>();
    }

    public GitMutableOperation addToIndex() {
        return AddToIndex;
    }

    private final GitMutableOperation AddToIndex = new GitMutableOperation() {
        @Override
        public void execute(@NotNull GitContext gitContext, String arg) throws GitException {
            GitFileSystemManager gitFileManager = gitContext.getGitFileManager();
            Path pathToFile = Path.of(arg);
            HashMap<String, String> tracked = gitContext.getGitIndex().getGitIndexTracked();
            if (!FileManager.checkFileExists(gitFileManager.pathInGitDir(pathToFile))) {
                if (tracked.containsKey(gitFileManager.pathInGitDir(pathToFile).toString())) {
                    tracked.remove(gitFileManager.pathInGitDir(pathToFile).toString());
                    return;
                } else {
                    throw new GitException("No such file can be added: " + arg);
                }
            }
            List<File> dirs = FileManager.listDirsByPath(gitFileManager.pathInGitDir(pathToFile));
            List<File> files = FileManager.listFilesByPath(gitFileManager.pathInGitDir(pathToFile));
            if (!files.isEmpty()) {
                for (var fileName : files) {
                    String absPath = fileName.getAbsolutePath();
                    GitBlob newBlob = new GitBlob(FileManager.readFromFile(Path.of(absPath)),
                            fileName.getName());
                    String hash = newBlob.getHash();
                    if (tracked.containsKey(absPath)) {
                        if (tracked.get(absPath).equals(hash)) {
                            if (FileManager.checkFileExists(Path.of(gitFileManager.constructPathByHash(hash)))) {
                                continue;
                            }
                        }
                    }
                    tracked.put(absPath, hash);
                    gitFileManager.getGitBlobFileManager().writeEntity(newBlob);
                }
            }
            if (dirs.size() > 1) {
                for (int i = 1; i < dirs.size(); ++i) {
                    execute(gitContext, arg + File.separator + dirs.get(i).getName());
                }
            }
        }
    };

    public HashMap<String, String> getGitIndexTracked() {
        return gitIndexTracked;
    }
}
