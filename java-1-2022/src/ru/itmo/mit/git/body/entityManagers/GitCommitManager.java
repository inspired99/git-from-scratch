package ru.itmo.mit.git.body.entityManagers;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.GitFileSystemManager;
import ru.itmo.mit.git.entities.GitCommit;
import ru.itmo.mit.git.utils.JsonFileManager;

import java.nio.file.Path;
import java.util.HashMap;

public class GitCommitManager extends GitEntityManager<GitCommit> {
    public GitCommitManager(GitFileSystemManager gitFileSystemManager) {
        super(gitFileSystemManager);
    }

    @Override
    public void writeEntity(@NotNull GitCommit entity) throws GitException {
        HashMap<String, String> jsonMap = new HashMap<>();
        jsonMap.put(GitConstants.TYPE, entity.getTypeEntity().getType());
        jsonMap.put(GitConstants.HASH, entity.getHash());
        jsonMap.put(GitConstants.COMMIT_MESSAGE, entity.getMessageCommit());
        jsonMap.put(GitConstants.COMMIT_AUTHOR, entity.getAuthorCommit());
        jsonMap.put(GitConstants.DATE, entity.getDateCommit());
        jsonMap.put(GitConstants.PARENT_COMMIT, entity.getParentCommitHash());
        jsonMap.put(GitConstants.ROOT_TREE, entity.getRootTreeHash());
        JsonFileManager.writeToJsonFile(preparePath(entity.getHash()), new JSONObject(jsonMap));
    }

    @Override
    public GitCommit readByHash(String hash) throws GitException {
        JSONObject json = JsonFileManager.readFromJsonFile(Path.of(gitFileSystemManager.constructPathByHash(hash)));
        org.json.JSONObject jsonObject = new org.json.JSONObject(json);
        if (!(jsonObject.has(GitConstants.HASH) && jsonObject.has(GitConstants.TYPE) &&
                jsonObject.has(GitConstants.COMMIT_MESSAGE) && jsonObject.has(GitConstants.COMMIT_AUTHOR) &&
                jsonObject.has(GitConstants.DATE) && jsonObject.has(GitConstants.PARENT_COMMIT) &&
                jsonObject.has(GitConstants.ROOT_TREE))) {
            throw new GitException("Could not read commit with hash: " + hash);
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
}
