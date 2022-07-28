package ru.itmo.mit.git.objects;

import ru.itmo.mit.git.GitConstants;

public enum GitObjectType {
    BLOB(GitConstants.BLOB),
    COMMIT(GitConstants.COMMIT),
    TREE(GitConstants.TREE),
    BRANCH(GitConstants.BRANCH);

    private final String type;

    GitObjectType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
