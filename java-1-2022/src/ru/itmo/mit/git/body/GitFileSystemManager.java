package ru.itmo.mit.git.body;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.entityManagers.GitBlobManager;
import ru.itmo.mit.git.body.entityManagers.GitBranchManager;
import ru.itmo.mit.git.body.entityManagers.GitCommitManager;
import ru.itmo.mit.git.body.entityManagers.GitTreeManager;
import ru.itmo.mit.git.utils.FileManager;

import java.io.File;
import java.nio.file.Path;

public class GitFileSystemManager {
    private final GitBranchManager gitBranchFileManager;
    private final GitBlobManager gitBlobFileManager;
    private final GitCommitManager gitCommitFileManager;
    private final GitTreeManager gitTreeFileManager;
    private final String pathToWorkDir;
    private final String pathToMainDir;
    private final String pathToHead;
    private final String pathToObjects;
    private final String pathToIndex;
    private final String pathToBranches;

    public GitFileSystemManager(String pathToWorkDir) {
        this.gitBlobFileManager = new GitBlobManager(this);
        this.gitBranchFileManager = new GitBranchManager(this);
        this.gitTreeFileManager = new GitTreeManager(this);
        this.gitCommitFileManager = new GitCommitManager(this);
        this.pathToWorkDir = pathToWorkDir;
        this.pathToMainDir = pathToWorkDir + File.separator + "." + GitConstants.GIT;
        this.pathToHead = pathToMainDir + File.separator + GitConstants.HEAD;
        this.pathToObjects = pathToMainDir + File.separator + GitConstants.OBJECTS;
        this.pathToIndex = pathToMainDir + File.separator + GitConstants.INDEX;
        this.pathToBranches = pathToMainDir + File.separator + GitConstants.BRANCHES;
    }

    public Path pathInGitDir(@NotNull Path path) {
        return Path.of(pathToWorkDir).resolve(path);
    }

    public @NotNull String constructPathByHash(@NotNull String hash) {
        return Path.of(pathToObjects).resolve(hash.substring(0, 2))
                .resolve(hash.substring(2)).toString();
    }

    public void createGitDirs() throws GitException {
        FileManager.createDirByPath(Path.of(pathToMainDir));
        FileManager.createDirByPath(Path.of(pathToObjects));
        FileManager.createDirByPath(Path.of(pathToBranches));
        FileManager.createFileByPath(Path.of(pathToIndex));
        FileManager.createFileByPath(Path.of(pathToHead));
    }

    public String getPathToMainDir() {
        return pathToMainDir;
    }

    public String getPathToHead() {
        return pathToHead;
    }

    public String getPathToObjects() {
        return pathToObjects;
    }

    public String getPathToIndex() {
        return pathToIndex;
    }

    public String getPathToBranches() {
        return pathToBranches;
    }

    public GitBranchManager getGitBranchFileManager() {
        return gitBranchFileManager;
    }

    public GitBlobManager getGitBlobFileManager() {
        return gitBlobFileManager;
    }

    public GitCommitManager getGitCommitFileManager() {
        return gitCommitFileManager;
    }

    public GitTreeManager getGitTreeFileManager() {
        return gitTreeFileManager;
    }

    public String getPathToWorkDir() {
        return pathToWorkDir;
    }
}
