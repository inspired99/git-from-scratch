package ru.itmo.mit.git.body;

import java.util.HashMap;
import java.util.Map;

public enum GitIndexType {
    ADDED("added"),
    REMOVED("removed"),
    MODIFIED("modified"),
    UNTRACKED("untracked");

    private final String type;
    private final Map<String, String> files;

    GitIndexType(String type) {
        this.files = new HashMap<>();
        this.type = type;
    }

    public void addFile(String file, String hash) {
        files.put(file, hash);
    }

    public Map<String, String> getFiles() {
        return files;
    }

    public String getType() {
        return type;
    }
}
