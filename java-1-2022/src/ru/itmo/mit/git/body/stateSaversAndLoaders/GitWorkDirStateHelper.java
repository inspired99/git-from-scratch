package ru.itmo.mit.git.body.stateSaversAndLoaders;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.context.GitContext;
import ru.itmo.mit.git.entities.GitBlob;
import ru.itmo.mit.git.utils.FileManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class GitWorkDirStateHelper implements GitStateHelper {
    private final HashMap<String, String> workDirSnapshot = new HashMap<>();

    @Override
    public void loadState(@NotNull GitContext gitContext) throws GitException {
        gitContext.getGitFileManager().getGitTreeFileManager().traverseBlobsToTree(workDirSnapshot,
                gitContext.getGitFileManager().getPathToWorkDir());
    }

    public HashMap<String, String> getWorkDirSnapshot() {
        return workDirSnapshot;
    }

    public void loadWorkDirStateFromRepo(@NotNull GitContext gitContext) throws GitException {
        HashMap<String, String> files = gitContext.getGitRepoManager().makeRootTreeSnapshot(gitContext).getBlobsHashes();
        File[] dirs = new File(gitContext.getGitFileManager().getPathToWorkDir()).listFiles();
        List<File> subDirs = new ArrayList<>();
        if (dirs != null) {
            subDirs = Arrays.stream(dirs)
                    .filter(x -> !x.getAbsolutePath().equals(gitContext.getGitFileManager().getPathToMainDir()))
                    .collect(Collectors.toList());
        }
        clearWorkDirState(subDirs);
        for (var file : files.entrySet()) {
            new File(file.getKey()).mkdirs();
            FileManager.createFileByPath(Path.of(file.getKey()));
            GitBlob blob = gitContext.getGitFileManager().getGitBlobFileManager().readByHash(file.getValue());
            FileManager.writeFileByPath(Path.of(file.getKey()), blob.getContentBlob().strip());
            gitContext.getGitIndex().addToIndex().execute(gitContext, file.getKey());
        }
    }

    private void clearWorkDirState(@NotNull List<File> files) throws GitException {
        try {
            for (var file : files) {
                if (file.isDirectory()) {
                    FileUtils.deleteDirectory(file);
                } else {
                    FileManager.deleteFileByPath(Path.of(file.getAbsolutePath()));
                }
            }
        } catch (IOException e) {
            throw new GitException("Could not delete all files ");
        }
    }
}
