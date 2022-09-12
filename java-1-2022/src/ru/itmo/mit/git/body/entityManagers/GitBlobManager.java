package ru.itmo.mit.git.body.entityManagers;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import ru.itmo.mit.git.body.GitFileSystemManager;
import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.entities.GitBlob;
import ru.itmo.mit.git.utils.JsonFileManager;

import java.nio.file.Path;
import java.util.HashMap;

public class GitBlobManager extends GitEntityManager<GitBlob> {

    public GitBlobManager(GitFileSystemManager gitFileSystemManager) {
        super(gitFileSystemManager);
    }

    @Override
    public void writeEntity(@NotNull GitBlob entity) throws GitException {
        HashMap<String, String> jsonMap = new HashMap<>();
        jsonMap.put(GitConstants.TYPE, entity.getTypeEntity().getType());
        jsonMap.put(GitConstants.HASH, entity.getHash());
        jsonMap.put(GitConstants.NAME, entity.getNameBlob());
        jsonMap.put(GitConstants.CONTENT, entity.getContentBlob());
        JsonFileManager.writeToJsonFile(preparePath(entity.getHash()), new JSONObject(jsonMap));
    }

    @Override
    public GitBlob readByHash(String hash) throws GitException {
        JSONObject json = JsonFileManager.readFromJsonFile(Path.of(gitFileSystemManager.constructPathByHash(hash)));
        org.json.JSONObject jsonObject = new org.json.JSONObject(json);
        if (!(jsonObject.has(GitConstants.HASH) && jsonObject.has(GitConstants.TYPE)
                && jsonObject.has(GitConstants.NAME) && jsonObject.has(GitConstants.CONTENT))) {
            throw new GitException("Could not read blob with hash " + hash);
        }
        return new GitBlob(jsonObject.getString(GitConstants.CONTENT), jsonObject.getString(GitConstants.NAME));
    }
}
