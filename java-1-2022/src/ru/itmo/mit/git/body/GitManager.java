package ru.itmo.mit.git.body;

/*
Основной класс для управления состояниями репозитория гита
 */

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.objects.GitBranch;
import ru.itmo.mit.git.objects.GitObject;
import ru.itmo.mit.git.objects.GitObjectType;

import java.io.PrintStream;

public class GitManager {
    private final String workingDir;
    private final GitPathsHolder gitPathsHolder;
    private final GitCommandsManager gitCommandsManager;
    private final GitIndex gitIndex;
    private PrintStream printStream;
    private GitObject head;
    private GitBranch currentBranch;

    public GitManager(String workingDir) {
        this.workingDir = workingDir;
        this.gitPathsHolder = new GitPathsHolder(workingDir);
        this.gitCommandsManager = new GitCommandsManager(this);
        this.gitIndex = new GitIndex(this);
        this.printStream = System.out;
    }

    public void updateIndex() throws GitException {
        gitPathsHolder.writeIndex(gitIndex);
    }

    public @NotNull String getRevisionFromHead(int n) throws GitException {
        return "";
    }

    public boolean isInDetachedHeadState() {
        if (head == null) {
            return false;
        }
        return head.getTypeObject().equals(GitObjectType.COMMIT);
    }

    public void setCurrentBranch(GitBranch currentBranch) {
        this.currentBranch = currentBranch;
    }

    public PrintStream getPrintStream() {
        return printStream;
    }

    public void setOutputStream(PrintStream outputStream) {
        this.printStream = outputStream;
    }

    public void updateHead(@NotNull GitObject object) throws GitException {
        if (object.getTypeObject().equals(GitObjectType.COMMIT)) {
            gitPathsHolder.updateHeadFile(object.getHashObject());
            head = object;
        } else if (object.getTypeObject().equals(GitObjectType.BRANCH)) {
            if (object instanceof GitBranch) {
                gitPathsHolder.updateHeadFile(((GitBranch) object).getBranchName());
                head = object;
            }
        }
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public GitPathsHolder getGitPathsHolder() {
        return gitPathsHolder;
    }

    public GitCommandsManager getGitCommandsManager() {
        return gitCommandsManager;
    }

    public GitObject getHead() {
        return head;
    }

    public GitBranch getCurrentBranch() {
        return currentBranch;
    }

    public GitIndex getGitIndex() {
        return gitIndex;
    }
}
