package ru.itmo.mit.git.objects;

import org.apache.commons.codec.digest.DigestUtils;

/*
Абстрактный класс для объектов гита
 */

public abstract class GitObject {
    protected String hashObject;
    protected GitObjectType typeObject;

    public GitObject(GitObjectType typeObject) {
        this.typeObject = typeObject;
    }

    protected void countHash(String data) {
        this.hashObject = DigestUtils.sha1Hex(data);
    }

    public GitObjectType getTypeObject() {
        return typeObject;
    }

    public String getHashObject() {
        return hashObject;
    }
}
