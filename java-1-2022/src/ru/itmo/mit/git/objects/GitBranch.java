package ru.itmo.mit.git.objects;

public class GitBranch extends GitObject{
    private final String branchName;

    public GitBranch(String branchName, String latestCommitHash) {
        super(GitObjectType.BRANCH);
        this.branchName = branchName;
        this.hashObject = latestCommitHash;
    }

    public String getBranchName() {
        return branchName;
    }
}
