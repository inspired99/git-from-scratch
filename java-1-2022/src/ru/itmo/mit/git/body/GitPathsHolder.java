package ru.itmo.mit.git.body;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.objects.GitBlob;
import ru.itmo.mit.git.objects.GitBranch;
import ru.itmo.mit.git.objects.GitCommit;
import ru.itmo.mit.git.objects.GitTree;
import ru.itmo.mit.git.utils.FileManager;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;

/*
Класс для низкоуровневой работы с файлами гита - чтение и запись, хранение путей до директорий
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

    public boolean checkFileInGitDir(@NotNull Path path) {
        return FileManager.checkFileExists(Path.of(workingDir).resolve(path));
    }

    public void updateHeadFile(@NotNull String content) throws GitException {
        FileManager.writeFileByPath(Path.of(pathToHead), content);
    }

    public void writeIndex(@NotNull GitIndex gitIndex) throws GitException {
        GitIndexType added = gitIndex.getGitIndexAdded();
        GitIndexType removed = gitIndex.getGitIndexRemoved();
        GitIndexType untracked = gitIndex.getGitIndexUntracked();
        GitIndexType modified = gitIndex.getGitIndexModified();
        modified.addFile("1", "2");
        HashMap<String, String> jsonMap = new HashMap<>();
        jsonMap.put(added.getType(), added.getFiles().entrySet().toString());
        jsonMap.put(removed.getType(), removed.getFiles().toString());
        jsonMap.put(untracked.getType(), untracked.getFiles().toString());
        jsonMap.put(modified.getType(), modified.getFiles().toString());
        FileManager.writeFileByPath(Path.of(pathToIndex), jsonMap.toString());
    }

    private @NotNull HashMap<String, String> parseTreeContent(@NotNull String content) {
        String mapSeparator = ",";
        String[] objects = content.split(mapSeparator);
        HashMap<String, String> res = new HashMap<>();
        for (var obj : objects) {
            String mapKeyValueSeparator = "=";
            String[] innerObj = obj.split(mapKeyValueSeparator);
            res.put(innerObj[0], innerObj[1]);
        }
        return res;
    }

    public GitBlob getBlobByPath(@NotNull Path path) throws GitException {
        String inside = FileManager.readFromFile(path);
        return new GitBlob(inside, path.getFileName().toString());
    }

    public GitTree getTreeByPath(@NotNull Path pathTree) throws GitException {
        JSONObject json = FileManager.readFromJsonFile(pathTree);
        org.json.JSONObject jsonObject = new org.json.JSONObject(json);
        if (!(jsonObject.has(GitConstants.HASH) && jsonObject.has(GitConstants.PATH) &&
                jsonObject.has(GitConstants.BLOB) && jsonObject.has(GitConstants.TREE))) {
            throw new GitException("Could not read tree from file: " + pathTree);
        }
        String path = jsonObject.getString(GitConstants.PATH);
        String blobs = jsonObject.getString(GitConstants.BLOBS);
        String trees = jsonObject.getString(GitConstants.TREES);
        blobs = blobs.substring(1, blobs.length() - 1);
        trees = trees.substring(1, trees.length() - 1);
        return new GitTree(path, parseTreeContent(blobs), parseTreeContent(trees));
    }

    private @NotNull Path getPathToObjFromHash(@NotNull String hash) {
        return Path.of(pathToObjects).resolve(hash.substring(0, 2)).resolve(hash.substring(2));
    }

    public GitTree constructTreesByPath(@NotNull GitTree gitTree, @NotNull String pathTree) throws GitException {
        String[] pathsSeparated = pathTree.split(File.separator);
        StringBuilder curPath = new StringBuilder();
        if (pathsSeparated.length > 2) {
            curPath.append(pathsSeparated[0]);
            curPath.append(File.separator);
            String realPath = pathTree.substring(curPath.length());
            GitTree anotherTree = new GitTree();
            GitTree newTree;
            if (!gitTree.getTreesHashes().containsKey(pathsSeparated[0])) {
                newTree = constructTreesByPath(anotherTree, realPath);
                newTree.setPath(pathsSeparated[0]);
                gitTree.getTreesHashes().put(newTree.getPath(), newTree.getHashObject());
            } else {
                String subTreeHash = gitTree.getTreesHashes().get(pathTree);
                anotherTree = getTreeByPath(getPathToObjFromHash(subTreeHash));
                newTree = constructTreesByPath(anotherTree, realPath);
                gitTree.getTreesHashes().remove(anotherTree.getPath());
                gitTree.getTreesHashes().put(newTree.getPath(), newTree.getHashObject());
            }
        } else {
            GitBlob newBlob = getBlobByPath(Path.of(pathTree));
            writeBlob(newBlob);
            gitTree.getBlobsHashes().remove(newBlob.getNameBlob());
            gitTree.getBlobsHashes().put(newBlob.getNameBlob(), newBlob.getHashObject());
        }
        gitTree.updateTreeHash();
        writeTree(gitTree);
        return gitTree;
    }

    public void writeTree(@NotNull GitTree gitTree) throws GitException {
        String hashObj = gitTree.getHashObject();
        Path finalPath = preparePath(hashObj);
        HashMap<String, String> jsonMap = new HashMap<>();
        jsonMap.put(GitConstants.TYPE, gitTree.getTypeObject().getType());
        jsonMap.put(GitConstants.HASH, gitTree.getHashObject());
        jsonMap.put(GitConstants.PATH, gitTree.getPath());
        jsonMap.put(GitConstants.BLOBS, gitTree.getBlobsHashes().toString());
        jsonMap.put(GitConstants.TREES, gitTree.getTreesHashes().toString());
        FileManager.writeToJsonFile(finalPath, new JSONObject(jsonMap));
    }

    private @NotNull Path preparePath(@NotNull String hash) throws GitException {
        String dirHashName = hash.substring(0, 2);
        Path pathToObj = Path.of(pathToObjects + File.separator + dirHashName);
        if (!FileManager.checkFileExists(pathToObj)) {
            FileManager.createDirByPath(pathToObj);
        }
        Path finalPath = pathToObj.resolve(Path.of(hash.substring(2)));
        if (FileManager.checkFileExists(finalPath)) {
            FileManager.deleteFileByPath(finalPath);
        }
        return finalPath;
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
