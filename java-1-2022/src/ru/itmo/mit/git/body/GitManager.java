package ru.itmo.mit.git.body;

/*
Основной класс для управления состояниями гита
 */

public class GitManager {
    private final String workingDir;
    private final GitPathsHolder gitPathsHolder;
    private final GitCommandsFactory gitCommandsFactory;
    private String head;
    private String currentBranch;

    public GitManager(String workingDir) {
        this.workingDir = workingDir;
        this.gitPathsHolder = new GitPathsHolder(workingDir);
        this.gitCommandsFactory = new GitCommandsFactory(this);
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public GitPathsHolder getGitPathsHolder() {
        return gitPathsHolder;
    }

    public GitCommandsFactory getGitCommandsFactory() {
        return gitCommandsFactory;
    }

    public String getHead() {
        return head;
    }

    public String getCurrentBranch() {
        return currentBranch;
    }
}
