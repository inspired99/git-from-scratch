package ru.itmo.mit.git.objects;

public class GitTree extends GitObject {

    public GitTree(GitObjectType typeObject) {
        super(GitObjectType.TREE);
    }
}
