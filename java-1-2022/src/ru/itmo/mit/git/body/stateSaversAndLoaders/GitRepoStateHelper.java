package ru.itmo.mit.git.body.stateSaversAndLoaders;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.GitFileSystemManager;
import ru.itmo.mit.git.body.context.GitContext;
import ru.itmo.mit.git.entities.*;
import ru.itmo.mit.git.utils.FileManager;

import java.nio.file.Path;

public class GitRepoStateHelper implements GitStateHelper {

    @Override
    public void loadState(@NotNull GitContext gitContext) throws GitException {
        GitFileSystemManager gitFileSystemManager = gitContext.getGitFileManager();
        String headContent = FileManager.readFromFile(Path.of(gitFileSystemManager.getPathToHead())).strip();
        GitEntity head;
        if (gitFileSystemManager.getGitBranchFileManager().checkBranchExists(headContent)) {
            head = gitFileSystemManager.getGitBranchFileManager().getBranchByName(headContent);
            gitContext.getGitRepoManager().setCurrentBranch((GitBranch) head);
        } else {
            head = gitFileSystemManager.getGitCommitFileManager().readByHash(headContent);
        }
        gitContext.getGitRepoManager().setHead(head);
    }

    public void updateState(@NotNull GitContext gitContext, @NotNull GitEntity nextHead) throws GitException {
        if (nextHead.getTypeEntity().equals(GitEntityType.COMMIT)) {
            if (nextHead instanceof GitCommit) {
                saveHeadState(nextHead.getHash(), gitContext);
                gitContext.getGitRepoManager().setHead(nextHead);
                gitContext.getGitFileManager().getGitCommitFileManager().writeEntity((GitCommit) nextHead);
            }
        } else if (nextHead.getTypeEntity().equals(GitEntityType.BRANCH)) {
            if (nextHead instanceof GitBranch) {
                saveHeadState(((GitBranch) nextHead).getBranchName(), gitContext);
                gitContext.getGitRepoManager().setHead(nextHead);
                gitContext.getGitFileManager().getGitBranchFileManager().writeEntity((GitBranch) nextHead);
            }
        }
    }

    private void saveHeadState(@NotNull String content, @NotNull GitContext gitContext) throws GitException {
        FileManager.writeFileByPath(Path.of(gitContext.getGitFileManager().getPathToHead()), content);
    }

    public GitTree currentStateSnapshot(@NotNull GitContext gitContext) throws GitException {
        GitFileSystemManager gitPathsManager = gitContext.getGitFileManager();
        GitEntity head = gitContext.getGitRepoManager().getHead();
        String hash = head.getHash();
        String hashTree;
        if (head.getTypeEntity().equals(GitEntityType.COMMIT)) {
            hashTree = ((GitCommit) head).getRootTreeHash();
        } else {
            GitCommit commit = gitPathsManager.getGitCommitFileManager().readByHash(hash);
            hashTree = commit.getRootTreeHash();
        }
        if (hashTree.equals("0")) {
            GitTree treeInit = new GitTree();
            treeInit.setPath(gitPathsManager.getPathToWorkDir());
            return treeInit;
        }
        return gitPathsManager.getGitTreeFileManager().readByHash(hashTree);
    }
}
