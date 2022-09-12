package ru.itmo.mit.git.utils;

public enum CommandMessages {
    INIT_MESSAGE("Initialized a repository in: "),
    ADD_MESSAGE("Added file(s): "),
    REMOVE_MESSAGE("Removed file(s): "),
    COMMIT_MESSAGE("Successfully committed staged changes: "),
    COMMIT_UP_TO_DATE_MESSAGE("Nothing to commit: everything is up-to-date "),
    COMMIT_DEFAULT_AUTHOR("Java-git"),
    COMMIT_CHANGED_FILES("file(s) changed "),
    STATUS_SEPARATOR("-------------------------"),
    STATUS_CUR_BRANCH_MESSAGE("Current branch is: "),
    STATUS_DETACHED_MESSAGE("HEAD is on: "),
    STATUS_STAGED_MESSAGE("Changes to be committed: "),
    STATUS_NOT_STAGED_MESSAGE("Changes not staged for commit: "),
    RESET_MESSAGE("Completed reset to revision: "),
    LOG_COMMIT_MESSAGE("Commit: "),
    LOG_AUTHOR_MESSAGE("Author: "),
    LOG_DATE_MESSAGE("Date: "),
    LOG_SEPARATOR("-------------------------"),
    CHECKOUT_FILES_MESSAGE("Checked out files: "),
    CHECKOUT_MESSAGE("Completed checkout to "),
    BRANCH_CREATE_MESSAGE("Created branch: "),
    BRANCH_REMOVE_MESSAGE("Removed branch: "),
    SHOW_BRANCHES_MESSAGE("Available branches: "),
    MERGE_MESSAGE("Merged branch: "),
    MERGE_CONFLICT_MESSAGE("Found merge conflict: "),
    MERGE_INTO_MESSAGE("into branch: ");

    private final String message;

    CommandMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
