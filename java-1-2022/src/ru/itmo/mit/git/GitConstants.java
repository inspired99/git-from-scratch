package ru.itmo.mit.git;

import org.jetbrains.annotations.NotNull;

public final class GitConstants {
    private GitConstants() {}

    public static final @NotNull String INIT = "init";
    public static final @NotNull String COMMIT = "commit";
    public static final @NotNull String RESET = "reset";
    public static final @NotNull String LOG = "log";
    public static final @NotNull String CHECKOUT = "checkout";
    public static final @NotNull String STATUS = "status";
    public static final @NotNull String ADD = "add";
    public static final @NotNull String RM = "rm";
    public static final @NotNull String BRANCH_CREATE = "branch-create";
    public static final @NotNull String BRANCH_REMOVE = "branch-remove";
    public static final @NotNull String SHOW_BRANCHES = "show-branches";
    public static final @NotNull String MERGE = "merge";

    public static final @NotNull String MASTER = "master";
    public static final @NotNull String BLOB = "blob";
    public static final @NotNull String TREE = "tree";
    public static final @NotNull String HASH = "hash";
    public static final @NotNull String TYPE = "type";
    public static final @NotNull String COMMIT_MESSAGE = "commit message";
    public static final @NotNull String COMMIT_AUTHOR = "author";
    public static final @NotNull String DATE = "date";
    public static final @NotNull String PARENT_COMMIT = "parent commit";
    public static final @NotNull String ROOT_TREE = "root tree";
    public static final @NotNull String NAME = "name";
    public static final @NotNull String CONTENT = "content";
    public static final @NotNull String PATH = "path";

    public static final @NotNull String HEAD = "HEAD";
    public static final @NotNull String GIT = "git";
    public static final @NotNull String BRANCHES = "branches";
    public static final @NotNull String INDEX = "index";
    public static final @NotNull String OBJECTS = "objects";
    public static final @NotNull String BLOBS = "blobs";
    public static final @NotNull String TREES = "trees";
}
