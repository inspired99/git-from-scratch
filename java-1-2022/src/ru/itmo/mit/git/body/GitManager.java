package ru.itmo.mit.git.body;

/*
Централизованный класс для управления гитом и его внутренними составляющими
 */

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.objects.*;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class GitManager {
    private final String workingDir;
    private final GitPathsHolder gitPathsHolder;
    private final GitCommandsManager gitCommandsManager;
    private final GitIndex gitIndex;
    private final GitStateManager gitStateManager;
    private PrintStream printStream;
    private GitObject head;
    private GitTree rootTreeSnapshot;
    private GitBranch currentBranch;

    public GitManager(String workingDir) {
        this.workingDir = workingDir;
        this.gitPathsHolder = new GitPathsHolder(this);
        this.gitCommandsManager = new GitCommandsManager(this);
        this.gitIndex = new GitIndex(this);
        this.gitStateManager = new GitStateManager(this);
        this.printStream = System.out;
        this.rootTreeSnapshot = new GitTree();
        this.rootTreeSnapshot.setPath(workingDir);
    }

    public @NotNull String getRevisionFromHead(int n) throws GitException {
        if (n < 0) {
            throw new GitException("Number of commits should be non-negative ");
        }
        loadGitState();
        String hashResult = head.getHashObject();
        while (!hashResult.equals("0")) {
            if (n == 0) {
                break;
            }
            GitCommit commit = gitPathsHolder.getCommitByPath(Path.of(getGitPathsHolder().getPathByHash(hashResult)));
            hashResult = commit.getParentCommitHash();
            n--;
        }
        if (n > 0) {
            throw new GitException("Not enough commits to get revision ");
        }
        return hashResult;
    }

    public void removeFromIndex(@NotNull String file) throws GitException {
        gitIndex.removeFromIndex(file);
    }

    public void addToIndex(@NotNull String file) throws GitException {
        gitIndex.addToIndex(file);
    }

    public void saveGitState(@NotNull GitObject object) throws GitException {
        gitStateManager.saveIndexState(gitIndex);
        gitStateManager.updateHeadState(object);
    }

    public void removeBranchFromGit(@NotNull String branchName) throws GitException {
        gitStateManager.clearBranchState(branchName);
    }

    public void loadGitState() throws GitException {
        gitStateManager.loadIndexState(this);
        gitStateManager.loadHeadState();
    }

    public List<String> mergeBranchWithCurrent(@NotNull String branchName) throws GitException {
        return gitStateManager.mergeBranchStateWithCurrent(branchName);
    }

    public void addBranchToGit(@NotNull String branchName) throws GitException {
        gitStateManager.addNewBranchState(branchName);
    }

    public void checkoutFile(@NotNull String fileName) throws GitException {
        gitStateManager.loadFileStateFromRepo(fileName);
    }

    public List<String> getAllBranches() {
        return gitStateManager.loadBranchesState();
    }

    public void setIndexAndDirStateToRepo(boolean isSafe) throws GitException {
        gitStateManager.setIndexAndDirStateToRepo(isSafe);
    }

    public void checkoutToRevision(@NotNull String revisionValue) throws GitException {
        GitObject nextHead;
        if (gitPathsHolder.checkBranchExists(revisionValue)) {
            nextHead = gitPathsHolder.getBranchByName(revisionValue);
        } else {
            nextHead = gitPathsHolder.getCommitByPath(Path.of(gitPathsHolder.getPathByHash(revisionValue)));
        }
        setHead(nextHead);
        gitStateManager.setIndexAndDirStateToRepo(true);
    }

    public void setHead(GitObject head) {
        this.head = head;
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

    public void updateBranch(@NotNull String branchName, @NotNull String hash) throws GitException {
        gitStateManager.updateBranchState(branchName, hash);
    }

    public void moveHeadAndBranchTo(@NotNull String hash) throws GitException {
        moveHeadTo(hash);
        if (head.getTypeObject().equals(GitObjectType.BRANCH)) {
            GitBranch branch = (GitBranch) head;
            updateBranch(branch.getBranchName(), branch.getHashObject());
        }
    }

    public void moveHeadTo(@NotNull String hash) throws GitException {
        if (head.getTypeObject().equals(GitObjectType.BRANCH)) {
            GitBranch branch = (GitBranch) head;
            branch.setHashObject(hash);
            setHead(branch);
        }
        if (head.getTypeObject().equals(GitObjectType.COMMIT)) {
            setHead(gitPathsHolder.getCommitByPath(Path.of(gitPathsHolder.getPathByHash(hash))));
        }
    }

    public void initState() throws GitException {
        gitStateManager.initState();
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

    public GitTree getRootTreeSnapshot() throws GitException {
        return gitStateManager.getRootTreeSnapshot();
    }

    public HashMap<String, String> commitToRepo(@NotNull String author, @NotNull String message) throws GitException {
        return gitStateManager.saveNewRepoState(author, message);
    }

    public HashMap<String, String> getWorkingDirSnapshot() throws GitException {
        return gitStateManager.getWorkingDirSnapshot();
    }

    public void setRootTreeSnapshot(GitTree rootTreeSnapshot) {
        this.rootTreeSnapshot = rootTreeSnapshot;
    }

    public GitIndex getGitIndex() {
        return gitIndex;
    }
}
