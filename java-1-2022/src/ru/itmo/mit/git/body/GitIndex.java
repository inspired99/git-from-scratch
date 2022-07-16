package ru.itmo.mit.git.body;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.objects.GitTree;

public class GitIndex {
    private final GitIndexType gitIndexAdded;
    private final GitIndexType gitIndexRemoved;
    private final GitIndexType gitIndexModified;
    private final GitIndexType gitIndexUntracked;
    private final GitManager gitManager;
    private GitTree rootTree;

    public GitIndex(GitManager gitManager) {
        this.gitIndexAdded = GitIndexType.ADDED;
        this.gitIndexRemoved = GitIndexType.REMOVED;
        this.gitIndexModified = GitIndexType.MODIFIED;
        this.gitIndexUntracked = GitIndexType.UNTRACKED;
        this.gitManager = gitManager;
        this.rootTree = new GitTree();
    }

    public void addToIndex(@NotNull String file) throws GitException {
        gitManager.getGitPathsHolder().constructTreesByPath(rootTree, file);
        gitIndexAdded.addFile(file, rootTree.getHashObject());
    }

    public GitIndexType getGitIndexAdded() {
        return gitIndexAdded;
    }

    public GitIndexType getGitIndexRemoved() {
        return gitIndexRemoved;
    }

    public GitIndexType getGitIndexModified() {
        return gitIndexModified;
    }

    public GitIndexType getGitIndexUntracked() {
        return gitIndexUntracked;
    }

    public GitManager getGitManager() {
        return gitManager;
    }

    public GitTree getRootTree() {
        return rootTree;
    }
}
