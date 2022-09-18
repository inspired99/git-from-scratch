package ru.itmo.mit.git.body.context;

import ru.itmo.mit.git.body.GitFileSystemManager;
import ru.itmo.mit.git.body.stateSaversAndLoaders.GitIndexStateHelper;
import ru.itmo.mit.git.body.stateSaversAndLoaders.GitRepoStateHelper;
import ru.itmo.mit.git.body.stateSaversAndLoaders.GitWorkDirStateHelper;
import ru.itmo.mit.git.utils.CommandMessages;

/*
Контекст, описывающий состояние гита
 */

public class GitContext {
    private final GitIndexManager gitIndexManager;
    private final GitRepoManager gitRepoManager;
    private final GitIndexStateHelper gitIndexStateHelper;
    private final GitRepoStateHelper gitRepoStateHelper;
    private final GitWorkDirStateHelper gitWorkDirStateHelper;
    private GitFileSystemManager gitFileManager = new GitFileSystemManager("");

    public GitContext() {
        this.gitIndexManager = new GitIndexManager();
        this.gitRepoManager = new GitRepoManager(CommandMessages.COMMIT_DEFAULT_AUTHOR.getMessage());
        this.gitIndexStateHelper = new GitIndexStateHelper();
        this.gitRepoStateHelper = new GitRepoStateHelper();
        this.gitWorkDirStateHelper = new GitWorkDirStateHelper();
    }

    public GitRepoManager getGitRepoManager() {
        return gitRepoManager;
    }

    public GitFileSystemManager getGitFileManager() {
        return gitFileManager;
    }

    public void setGitPathHolder(GitFileSystemManager gitFileSystemManager) {
        this.gitFileManager = gitFileSystemManager;
    }

    public GitIndexManager getGitIndexManager() {
        return gitIndexManager;
    }

    public GitIndexStateHelper getGitIndexStateManager() {
        return gitIndexStateHelper;
    }

    public GitRepoStateHelper getGitRepoStateManager() {
        return gitRepoStateHelper;
    }

    public GitIndexManager getGitIndex() {
        return gitIndexManager;
    }

    public GitWorkDirStateHelper getGitWorkDirStateHelper() {
        return gitWorkDirStateHelper;
    }
}
