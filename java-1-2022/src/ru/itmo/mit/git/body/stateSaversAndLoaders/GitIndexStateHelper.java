package ru.itmo.mit.git.body.stateSaversAndLoaders;

/*
Класс для управления состояниями staging area
 */

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.context.GitContext;
import ru.itmo.mit.git.body.context.GitIndexManager;
import ru.itmo.mit.git.utils.FileManager;

import java.io.File;
import java.nio.file.Path;

public class GitIndexStateHelper implements GitStateHelper {
    @Override
    public void loadState(@NotNull GitContext gitContext) throws GitException {
        String content = FileManager.readFromFile(Path.of(gitContext.getGitFileManager().getPathToIndex()));
        String[] splitString = content.split("\n");
        for (var str : splitString) {
            readIndex(gitContext.getGitIndex(), str);
        }
    }

    @Override
    public void saveState(@NotNull GitContext gitContext) throws GitException {
        StringBuilder tracked = new StringBuilder();
        for (var file : gitContext.getGitIndex().getGitIndexTracked().entrySet()) {
            tracked.append(file.getKey()).append(" ").append(file.getValue()).append("\n");
        }
        FileManager.writeFileByPath(Path.of(gitContext.getGitFileManager().getPathToIndex()), tracked.toString());
    }

    private void readIndex(@NotNull GitIndexManager index, @NotNull String content) {
        if (content.length() <= 1) {
            return;
        }
        String[] split = content.split(" ");
        if (split.length > 1) {
            index.getGitIndexTracked().put(split[0], split[1]);
        }
    }

    public void loadIndexStateFromRepo(@NotNull GitContext gitContext) throws GitException {
        gitContext.getGitIndex().getGitIndexTracked().clear();
        for (var file : gitContext.getGitRepoManager().makeRootTreeSnapshot(gitContext).getBlobsHashes().entrySet()) {
            new File(file.getKey()).mkdirs();
            gitContext.getGitIndex().addToIndex().execute(gitContext, file.getKey());
        }
    }
}
