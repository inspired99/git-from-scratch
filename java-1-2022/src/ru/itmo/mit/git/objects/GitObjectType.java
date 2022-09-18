package ru.itmo.mit.git.objects;

public enum GitObjectType {
    BLOB("blob"),
    COMMIT("commit"),
    TREE("tree"),
    BRANCH("branch");

    private final String type;

    GitObjectType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
