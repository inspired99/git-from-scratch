package ru.itmo.mit.git.utils;

public enum CommandMessages {
    INIT_MESSAGE("Initialized a repository in: "),
    ADD_MESSAGE("Added file(s): "),
    REMOVE_MESSAGE("Removed file(s): "),
    COMMIT_MESSAGE("Committed file(s): ");

    private final String message;

    CommandMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
