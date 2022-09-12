package ru.itmo.mit.git.body.entityManagers;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import ru.itmo.mit.git.body.GitFileSystemManager;
import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.entities.GitBlob;
import ru.itmo.mit.git.entities.GitTree;
import ru.itmo.mit.git.utils.FileManager;
import ru.itmo.mit.git.utils.JsonFileManager;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class GitTreeManager extends GitEntityManager<GitTree> {

    public GitTreeManager(GitFileSystemManager gitFileSystemManager) {
        super(gitFileSystemManager);
    }

    @Override
    public void writeEntity(@NotNull GitTree entity) throws GitException {
        String hashObj = entity.getHash();
        HashMap<String, String> jsonMap = new HashMap<>();
        jsonMap.put(GitConstants.TYPE, entity.getTypeEntity().getType());
        jsonMap.put(GitConstants.HASH, entity.getHash());
        jsonMap.put(GitConstants.PATH, entity.getPath());
        jsonMap.put(GitConstants.BLOBS, entity.getBlobsHashes().toString());
        FileManager.writeFileByPath(preparePath(hashObj), new org.json.JSONObject(jsonMap).toString());
    }

    @Override
    public GitTree readByHash(String hash) throws GitException {
        Path pathTree = Path.of(gitFileSystemManager.constructPathByHash(hash));
        JSONObject json = JsonFileManager.readFromJsonFile(pathTree);
        org.json.JSONObject jsonObject = new org.json.JSONObject(json);
        if (!(jsonObject.has(GitConstants.HASH) && jsonObject.has(GitConstants.PATH) &&
                jsonObject.has(GitConstants.BLOBS) &&
                jsonObject.has(GitConstants.TYPE))) {
            throw new GitException("Could not read tree with hash: " + hash);
        }
        String blobs = jsonObject.getString(GitConstants.BLOBS);
        blobs = blobs.substring(1, blobs.length() - 1);
        return new GitTree(jsonObject.getString(GitConstants.PATH), parseTreeContent(blobs));
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

    public void traverseBlobsToTree(@NotNull HashMap<String, String> hashes, @NotNull String path) throws GitException {
        List<File> filesInDir = FileManager.listFilesByPath(Path.of(path))
                .stream().filter(x -> !x.getAbsolutePath().startsWith(gitFileSystemManager.getPathToMainDir()))
                .collect(Collectors.toList());
        List<File> dirs = FileManager.listDirsByPath(Path.of(path))
                .stream().filter(x -> !x.getAbsolutePath().startsWith(gitFileSystemManager.getPathToMainDir()))
                .collect(Collectors.toList());
        if (!filesInDir.isEmpty()) {
            for (var fileInDir : filesInDir) {
                GitBlob newBlob = new GitBlob(FileManager.readFromFile(Path.of(fileInDir.getAbsolutePath())),
                        fileInDir.getName());
                hashes.put(fileInDir.getAbsolutePath(), newBlob.getHash());
            }
        }
        if (dirs.size() > 1) {
            for (int i = 1; i < dirs.size(); ++i) {
                traverseBlobsToTree(hashes, dirs.get(i).getAbsolutePath());
            }
        }
    }
}
