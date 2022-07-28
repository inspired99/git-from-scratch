package ru.itmo.mit.git.objects;

import java.util.HashMap;

public class GitTree extends GitObject {
    private String path = "";
    private HashMap<String, String> blobsHashes = new HashMap<>();

    public GitTree() {
        super(GitObjectType.TREE);
        updateTreeHash();
    }

    public GitTree(String path, HashMap<String, String> blobsHashes) {
        super(GitObjectType.TREE);
        this.path = path;
        this.blobsHashes = blobsHashes;
        updateTreeHash();
    }

    private void updateTreeHash() {
        countHash(typeObject.getType() + path + blobsHashes);
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
