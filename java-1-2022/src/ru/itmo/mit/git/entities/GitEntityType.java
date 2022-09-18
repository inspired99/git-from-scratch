package ru.itmo.mit.git.entities;

import ru.itmo.mit.git.GitConstants;

public enum GitEntityType {
    BLOB(GitConstants.BLOB),
    COMMIT(GitConstants.COMMIT),
    TREE(GitConstants.TREE),
    BRANCH(GitConstants.BRANCH);

    private final String type;

    GitEntityType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
