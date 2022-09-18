package ru.itmo.mit.git.entities;

import org.apache.commons.codec.digest.DigestUtils;

/*
Абстрактный класс для объектов гита: Tree, Commit, Blob, Branch
 */

public abstract class GitEntity {
    protected String hashEntity;
    protected GitEntityType typeEntity;

    public GitEntity(GitEntityType typeEntity) {
        this.typeEntity = typeEntity;
    }

    protected void countHash(String data) {
        this.hashEntity = DigestUtils.sha1Hex(data);
    }

    public GitEntityType getTypeEntity() {
        return typeEntity;
    }

    public String getHash() {
        return hashEntity;
    }
}
