package ru.itmo.mit.git.utils;

public enum CommandMessages {
    INIT_MESSAGE("Initialized a repository in: "),
    ADD_MESSAGE("Added file(s): "),
    REMOVE_MESSAGE("Removed file(s): "),
    COMMIT_MESSAGE("Committed staged changes "),
    COMMIT_FAIL_MESSAGE("Nothing to commit: everything up-to-date "),
    STATUS_STAGED("Changes to be committed: "),
    STATUS_NOT_STAGED("Changes not staged for commit: "),
    BRANCH_CREATE_MESSAGE("Created branch: "),
    BRANCH_REMOVE_MESSAGE("Removed branch: "),
    MERGE_MESSAGE("Merged branch: ");

    private final String message;

    CommandMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
