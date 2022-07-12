package ru.itmo.mit.git.body;

import java.util.HashMap;
import java.util.Map;

public class GitIndex {
    private Map<String, String> added;
    private Map<String, String> removed;
    private Map<String, String> untracked;
    private Map<String, String> modified;

    public GitIndex() {
        added = new HashMap<>();
        removed = new HashMap<>();
        untracked = new HashMap<>();
        modified = new HashMap<>();
    }

    public void loadIndexState() {

    }

}
