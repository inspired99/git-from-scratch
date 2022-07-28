package ru.itmo.mit.git.body;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.objects.*;
import ru.itmo.mit.git.utils.FileManager;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/*
Класс для более низкоуровневой работы с файлами гита как объектами Blob, Tree, Commit, Branch,
а также для хранения гит директорий
 */

public class GitPathsHolder {
    private final String workingDir;
    private final String mainDir;
    private final String pathToObjects;
    private final String pathToIndex;
    private final String pathToHead;
    private final String pathToBranches;

    public GitPathsHolder(@NotNull GitManager gitManager) {
        this.workingDir = gitManager.getWorkingDir();
        this.mainDir = workingDir + File.separator + "." + GitConstants.GIT;
        this.pathToHead = mainDir + File.separator + GitConstants.HEAD;
        this.pathToObjects = mainDir + File.separator + GitConstants.OBJECTS;
        this.pathToIndex = mainDir + File.separator + GitConstants.INDEX;
        this.pathToBranches = mainDir + File.separator + GitConstants.BRANCHES;
    }

    public void createGitDirs() throws GitException {
        FileManager.createDirByPath(Path.of(mainDir));
        FileManager.createDirByPath(Path.of(pathToObjects));
        FileManager.createDirByPath(Path.of(pathToBranches));
        FileManager.createFileByPath(Path.of(pathToIndex));
        FileManager.createFileByPath(Path.of(pathToHead));
    }

    public Path pathInGitDir(@NotNull Path path) {
        return Path.of(workingDir).resolve(path);
    }

    public boolean checkBranchExists(@NotNull String name) {
        return FileManager.checkFileExists(Path.of(pathToBranches).resolve(Path.of(name)));
    }

    private @NotNull HashMap<String, String> parseTreeContent(@NotNull String content) {
        String mapSeparator = ",";
        String[] objects = content.split(mapSeparator);
        HashMap<String, String> res = new HashMap<>();
        for (var obj : objects) {
            String mapKeyValueSeparator = "=";
            String[] innerObj = obj.split(mapKeyValueSeparator);
            if (innerObj.length > 1) {
                res.put(innerObj[0].strip(), innerObj[1]);
            }
        }
        return res;
    }

    public List<String> readAllBranches() {
        return FileManager.listFilesByPath(Path.of(pathToBranches))
                .stream()
                .map(File::getName)
                .collect(Collectors.toList());
    }

    public void readIndex(@NotNull GitIndex gitIndex, @NotNull String content) {
        if (content.length() <= 1) {
            return;
        }
        String[] split = content.split(" ");
        if (split.length > 1) {
            gitIndex.getGitIndexTracked().put(split[0], split[1]);
        }
    }

    public GitBlob readBlob(@NotNull String hash) throws GitException {
        JSONObject json = FileManager.readFromJsonFile(Path.of(getPathByHash(hash)));
        org.json.JSONObject jsonObject = new org.json.JSONObject(json);
        if (!(jsonObject.has(GitConstants.HASH) && jsonObject.has(GitConstants.TYPE)
                && jsonObject.has(GitConstants.NAME) && jsonObject.has(GitConstants.CONTENT))) {
            throw new GitException("Could not read blob ");
        }
        return new GitBlob(jsonObject.getString(GitConstants.CONTENT), jsonObject.getString(GitConstants.NAME));
    }

    public GitTree getTreeByPath(@NotNull Path pathTree) throws GitException {
        JSONObject json = FileManager.readFromJsonFile(pathTree);
        org.json.JSONObject jsonObject = new org.json.JSONObject(json);
        if (!(jsonObject.has(GitConstants.HASH) && jsonObject.has(GitConstants.PATH) &&
                jsonObject.has(GitConstants.BLOBS) &&
                jsonObject.has(GitConstants.TYPE))) {
            throw new GitException("Could not read tree from file: " + pathTree);
        }
        String path = jsonObject.getString(GitConstants.PATH);
        String blobs = jsonObject.getString(GitConstants.BLOBS);
        blobs = blobs.substring(1, blobs.length() - 1);
        return new GitTree(path, parseTreeContent(blobs));
    }

    public @NotNull String getPathByHash(@NotNull String hash) {
        return Path.of(pathToObjects).resolve(hash.substring(0, 2)).resolve(hash.substring(2)).toString();
    }

    public GitBranch getBranchByName(@NotNull String name) throws GitException {
        String hash = FileManager.readFromFile(Path.of(pathToBranches).resolve(Path.of(name)));
        return new GitBranch(name, hash);
    }

    public @NotNull Path pathToBranchByName(@NotNull String name) {
        return Path.of(pathToBranches).resolve(name);
    }

    public void writeTree(@NotNull GitTree gitTree) throws GitException {
        String hashObj = gitTree.getHashObject();
        Path finalPath = preparePath(hashObj);
        HashMap<String, String> jsonMap = new HashMap<>();
        jsonMap.put(GitConstants.TYPE, gitTree.getTypeObject().getType());
        jsonMap.put(GitConstants.HASH, gitTree.getHashObject());
        jsonMap.put(GitConstants.PATH, gitTree.getPath());
        jsonMap.put(GitConstants.BLOBS, gitTree.getBlobsHashes().toString());
        FileManager.writeFileByPath(finalPath, new org.json.JSONObject(jsonMap).toString());
    }

    private @NotNull Path preparePath(@NotNull String hash) throws GitException {
        String dirHashName = hash.substring(0, 2);
        Path pathToObj = Path.of(pathToObjects + File.separator + dirHashName);
        if (!FileManager.checkFileExists(pathToObj)) {
            FileManager.createDirByPath(pathToObj);
        }
        Path finalPath = Path.of(getPathByHash(hash));
        if (FileManager.checkFileExists(finalPath)) {
            FileManager.deleteFileByPath(finalPath);
        }
        return finalPath;
    }

    public void traverseBlobsToMap(@NotNull HashMap<String, String> hashes, @NotNull String path) throws GitException {
        List<File> filesInDir = FileManager.listFilesByPath(Path.of(path))
                .stream().filter(x -> !x.getAbsolutePath().startsWith(mainDir)).collect(Collectors.toList());
        List<File> dirs = FileManager.listDirsByPath(Path.of(path))
                .stream().filter(x -> !x.getAbsolutePath().startsWith(mainDir)).collect(Collectors.toList());
        if (!filesInDir.isEmpty()) {
            for (var fileInDir : filesInDir) {
                GitBlob newBlob = new GitBlob(FileManager.readFromFile(Path.of(fileInDir.getAbsolutePath())),
                        fileInDir.getName());
                hashes.put(fileInDir.getAbsolutePath(), newBlob.getHashObject());
            }
        }
        if (dirs.size() > 1) {
            for (int i = 1; i < dirs.size(); ++i) {
                traverseBlobsToMap(hashes, dirs.get(i).getAbsolutePath());
            }
        }
    }

    public void writeCommit(@NotNull GitCommit commit) throws GitException {
        Path finalPath = preparePath(commit.getHashObject());
        HashMap<String, String> jsonMap = new HashMap<>();
        jsonMap.put(GitConstants.TYPE, commit.getTypeObject().getType());
        jsonMap.put(GitConstants.HASH, commit.getHashObject());
        jsonMap.put(GitConstants.COMMIT_MESSAGE, commit.getMessageCommit());
        jsonMap.put(GitConstants.COMMIT_AUTHOR, commit.getAuthorCommit());
        jsonMap.put(GitConstants.DATE, commit.getDateCommit());
        jsonMap.put(GitConstants.PARENT_COMMIT, commit.getParentCommitHash());
        jsonMap.put(GitConstants.ROOT_TREE, commit.getRootTreeHash());
        FileManager.writeToJsonFile(finalPath, new JSONObject(jsonMap));
    }

    public GitCommit getCommitByPath(@NotNull Path path) throws GitException {
        JSONObject json = FileManager.readFromJsonFile(path);
        org.json.JSONObject jsonObject = new org.json.JSONObject(json);
        if (!(jsonObject.has(GitConstants.HASH) && jsonObject.has(GitConstants.TYPE) &&
                jsonObject.has(GitConstants.COMMIT_MESSAGE) && jsonObject.has(GitConstants.COMMIT_AUTHOR) &&
                jsonObject.has(GitConstants.DATE) && jsonObject.has(GitConstants.PARENT_COMMIT) &&
                jsonObject.has(GitConstants.ROOT_TREE))) {
            throw new GitException("Could not read tree from file: " + path);
        }
        String message = jsonObject.getString(GitConstants.COMMIT_MESSAGE);
        String author = jsonObject.getString(GitConstants.COMMIT_AUTHOR);
        String date = jsonObject.getString(GitConstants.DATE);
        String parentCommit = jsonObject.getString(GitConstants.PARENT_COMMIT);
        String rootTree = jsonObject.getString(GitConstants.ROOT_TREE);
        GitCommit commit = new GitCommit(author, message, parentCommit, rootTree);
        commit.setDateCommit(date);
        commit.updateHash();
        return commit;
    }

    public void writeBranch(@NotNull GitBranch branch) throws GitException {
        Path pathToWrite = Path.of(pathToBranches).resolve(branch.getBranchName());
        if (FileManager.checkFileExists(pathToWrite)) {
            FileManager.deleteFileByPath(pathToWrite);
        }
        FileManager.writeFileByPath(pathToWrite, branch.getHashObject());
    }

    public void writeBlob(@NotNull GitBlob blob) throws GitException {
        Path finalPath = preparePath(blob.getHashObject());
        HashMap<String, String> jsonMap = new HashMap<>();
        jsonMap.put(GitConstants.TYPE, blob.getTypeObject().getType());
        jsonMap.put(GitConstants.HASH, blob.getHashObject());
        jsonMap.put(GitConstants.NAME, blob.getNameBlob());
        jsonMap.put(GitConstants.CONTENT, blob.getContentBlob());
        FileManager.writeToJsonFile(finalPath, new JSONObject(jsonMap));
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public String getMainDir() {
        return mainDir;
    }

    public String getPathToIndex() {
        return pathToIndex;
    }

    public String getPathToHead() {
        return pathToHead;
    }
}
