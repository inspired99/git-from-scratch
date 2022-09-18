package ru.itmo.mit.git.entities;

public class GitBranch extends GitEntity {
    private final String branchName;

    public GitBranch(String branchName, String latestCommitHash) {
        super(GitEntityType.BRANCH);
        this.branchName = branchName;
        this.hashEntity = latestCommitHash;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchHash(String hash) {
        this.hashEntity = hash;
    }
}
