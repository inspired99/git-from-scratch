package ru.itmo.mit.git.entities;

public class GitBlob extends GitEntity {
    private final String contentBlob;
    private final String nameBlob;

    public GitBlob(String contentBlob, String nameBlob) {
        super(GitEntityType.BLOB);
        this.contentBlob = contentBlob;
        this.nameBlob = nameBlob;
        countHash(typeEntity.getType() + nameBlob + contentBlob);
    }

    public String getContentBlob() {
        return contentBlob;
    }

    public String getNameBlob() {
        return nameBlob;
    }
}
