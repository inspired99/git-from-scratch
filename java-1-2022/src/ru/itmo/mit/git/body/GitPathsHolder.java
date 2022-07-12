package ru.itmo.mit.git.body;

import java.io.File;

/*
Класс, для управления путями до директорий гита
 */

public class GitPathsHolder {
    private final String workingDir;
    private final String mainDir;
    private final String pathToObjects;
    private final String pathToIndex;
    private final String pathToHead;
    private final String pathToBranches;

    public GitPathsHolder(String workingDir) {
        this.workingDir = workingDir;
        this.mainDir = workingDir + File.separator + ".git";
        this.pathToHead = mainDir + File.separator + "HEAD";
        this.pathToObjects = mainDir + File.separator + "objects";
        this.pathToIndex = mainDir + File.separator + "index";
        this.pathToBranches = mainDir + File.separator + "branches";
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public String getMainDir() {
        return mainDir;
    }

    public String getPathToObjects() {
        return pathToObjects;
    }

    public String getPathToIndex() {
        return pathToIndex;
    }

    public String getPathToHead() {
        return pathToHead;
    }

    public String getPathToBranches() {
        return pathToBranches;
    }
}
