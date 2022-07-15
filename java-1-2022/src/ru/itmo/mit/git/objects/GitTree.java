package ru.itmo.mit.git.objects;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class GitTree extends GitObject {
    private String path;
    private HashMap<String, String> blobsHashes = new HashMap<>();
    private HashMap<String, String> treesHashes = new HashMap<>();

    public GitTree() {
        super(GitObjectType.TREE);
    }

    public GitTree(String path, HashMap<String, String> blobsHashes, HashMap<String, String> treesHashes) {
        super(GitObjectType.TREE);
        this.path = path;
        this.blobsHashes = blobsHashes;
        this.treesHashes = treesHashes;
        updateTreeHash();
    }

    public void updateTreeHash() {
        countHash(typeObject.getType() + path + blobsHashes + treesHashes);
    }

    public void addSubTree(@NotNull GitTree tree) {
        blobsHashes.putAll(tree.getBlobsHashes());
        treesHashes.putAll(tree.getTreesHashes());
        updateTreeHash();
    }

    public String getPath() {
        return path;
    }

    public HashMap<String, String> getBlobsHashes() {
        return blobsHashes;
    }

    public HashMap<String, String> getTreesHashes() {
        return treesHashes;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
