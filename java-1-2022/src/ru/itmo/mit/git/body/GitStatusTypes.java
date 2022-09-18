package ru.itmo.mit.git.body;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public enum GitStatusTypes {
    STAGED_ADDED,
    STAGED_REMOVED,
    STAGED_MODIFIED,
    UNTRACKED_ADDED,
    UNTRACKED_MODIFIED,
    UNTRACKED_REMOVED;

    private final HashMap<String, String> files;

    GitStatusTypes() {
        this.files = new HashMap<>();
    }

    public HashMap<String, String> getFiles() {
        return files;
    }
}
