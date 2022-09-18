package ru.itmo.mit.git.body;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/*
Класс для реализации статуса файла относительно рабочей директории, репозитория и индекса
 */

public class GitStatusInfo {
    private final GitStatusTypes stagedAdded = GitStatusTypes.STAGED_ADDED;
    private final GitStatusTypes stagedRemoved = GitStatusTypes.STAGED_REMOVED;
    private final GitStatusTypes stagedModified = GitStatusTypes.STAGED_MODIFIED;
    private final GitStatusTypes untrackedAdded = GitStatusTypes.UNTRACKED_ADDED;
    private final GitStatusTypes untrackedRemoved = GitStatusTypes.UNTRACKED_REMOVED;
    private final GitStatusTypes untrackedModified = GitStatusTypes.UNTRACKED_MODIFIED;

    private HashMap<String, String> hashInWorkDir;
    private HashMap<String, String> hashInCurRepo;
    private HashMap<String, String> hashInCurInd;

    public GitStatusInfo() {
        hashInCurInd = new HashMap<>();
        hashInCurRepo = new HashMap<>();
        hashInWorkDir = new HashMap<>();
    }

    public void fillHashes(HashMap<String, String> curDir, HashMap<String, String> curInd,
                           HashMap<String, String> curRep) {
        hashInWorkDir = curDir;
        hashInCurRepo = curRep;
        hashInCurInd = curInd;
    }

    public boolean isEmpty() {
        return (stagedAdded.getFiles().isEmpty() && stagedRemoved.getFiles().isEmpty() &&
                stagedModified.getFiles().isEmpty() && untrackedModified.getFiles().isEmpty() &&
                untrackedRemoved.getFiles().isEmpty() && untrackedAdded.getFiles().isEmpty());
    }

    public GitStatusTypes getStagedAdded() {
        return stagedAdded;
    }

    public GitStatusTypes getStagedRemoved() {
        return stagedRemoved;
    }

    public GitStatusTypes getStagedModified() {
        return stagedModified;
    }

    public GitStatusTypes getUntrackedAdded() {
        return untrackedAdded;
    }

    public GitStatusTypes getUntrackedRemoved() {
        return untrackedRemoved;
    }

    public GitStatusTypes getUntrackedModified() {
        return untrackedModified;
    }

    public HashMap<String, String> getHashInWorkDir() {
        return hashInWorkDir;
    }

    public HashMap<String, String> getHashInCurRepo() {
        return hashInCurRepo;
    }

    public HashMap<String, String> getHashInCurInd() {
        return hashInCurInd;
    }

    public void addStageModified(Map.@NotNull Entry<String, String> file) {
        stagedModified.getFiles().put(file.getKey(), file.getValue());
    }

    public void addStageAdded(Map.@NotNull Entry<String, String> file) {
        stagedAdded.getFiles().put(file.getKey(), file.getValue());
    }

    public void addStageRemoved(Map.@NotNull Entry<String, String> file) {
        stagedRemoved.getFiles().put(file.getKey(), file.getValue());
    }

    public void addUntrackedAdded(Map.@NotNull Entry<String, String> file) {
        untrackedAdded.getFiles().put(file.getKey(), file.getValue());
    }

    public void addUntrackedModified(Map.@NotNull Entry<String, String> file) {
        untrackedModified.getFiles().put(file.getKey(), file.getValue());
    }

    public void addUntrackedRemoved(Map.@NotNull Entry<String, String> file) {
        untrackedRemoved.getFiles().put(file.getKey(), file.getValue());
    }

    public @NotNull String prettyPrintStatusMap(@NotNull HashMap<String, String> hashMap, @NotNull String title,
                                                boolean printTitle) {
        if (!hashMap.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            if (!printTitle) {
                sb.append(title).append(":").append("\n");
            }
            for (var entry : hashMap.entrySet()) {
                if (printTitle) {
                    sb.append(title).append(" ").append(entry.getKey()).append(" ")
                            .append(entry.getValue()).append("\n");
                } else {
                    sb.append(entry.getKey()).append(" ").append("\n");
                }
            }
            return sb.toString();
        }
        return "";
    }
}
