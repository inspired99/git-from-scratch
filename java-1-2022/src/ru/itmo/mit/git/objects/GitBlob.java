package ru.itmo.mit.git.objects;

public class GitBlob extends GitObject {
    private final String contentBlob;
    private final String nameBlob;

    public GitBlob(String contentBlob, String nameBlob) {
        super(GitObjectType.BLOB);
        this.contentBlob = contentBlob;
        this.nameBlob = nameBlob;
        countHash(typeObject.getType() + nameBlob + contentBlob);
    }

    public String getContentBlob() {
        return contentBlob;
    }

    public String getNameBlob() {
        return nameBlob;
    }
}
