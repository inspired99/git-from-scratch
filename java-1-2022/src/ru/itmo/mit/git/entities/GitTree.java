package ru.itmo.mit.git.entities;

import java.util.HashMap;

public class GitTree extends GitEntity {
    private String path = "";
    private HashMap<String, String> blobsHashes = new HashMap<>();

    public GitTree() {
        super(GitEntityType.TREE);
        updateTreeHash();
    }

    public GitTree(String path, HashMap<String, String> blobsHashes) {
        super(GitEntityType.TREE);
        this.path = path;
        this.blobsHashes = blobsHashes;
        updateTreeHash();
    }

    private void updateTreeHash() {
        countHash(typeEntity.getType() + path + blobsHashes);
    }

    public String getPath() {
        return path;
    }

    public HashMap<String, String> getBlobsHashes() {
        return blobsHashes;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
