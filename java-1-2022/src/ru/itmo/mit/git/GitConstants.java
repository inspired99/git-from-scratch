package ru.itmo.mit.git;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class GitConstants {
    private GitConstants() {
    }

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
    public static final @NotNull Set<String> COMMANDS = Set.of(INIT, COMMIT, RESET, LOG, CHECKOUT, STATUS, ADD, RM,
            BRANCH_CREATE, BRANCH_REMOVE, SHOW_BRANCHES, MERGE);

    public static final @NotNull String MASTER = "master";

    public static final @NotNull String BLOB = "blob";
    public static final @NotNull String TREE = "tree";
}
