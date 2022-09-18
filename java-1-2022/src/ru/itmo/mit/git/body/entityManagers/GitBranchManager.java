package ru.itmo.mit.git.body.entityManagers;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.GitFileSystemManager;
import ru.itmo.mit.git.entities.GitBranch;
import ru.itmo.mit.git.utils.FileManager;

import java.nio.file.Path;

public class GitBranchManager extends GitEntityManager<GitBranch> {
    public GitBranchManager(GitFileSystemManager gitFileSystemManager) {
        super(gitFileSystemManager);
    }

    @Override
    public void writeEntity(@NotNull GitBranch entity) throws GitException {
        Path pathToWrite = Path.of(gitFileSystemManager.getPathToBranches()).resolve(entity.getBranchName());
        if (FileManager.checkFileExists(pathToWrite)) {
            FileManager.deleteFileByPath(pathToWrite);
        }
        FileManager.writeFileByPath(pathToWrite, entity.getHash());
    }

    @Override
    public GitBranch readByHash(String hash) throws GitException {
        throw new UnsupportedOperationException("Branch can be read only by name");
    }

    public void updateBranch(@NotNull String branchName, @NotNull String hash) throws GitException {
        GitBranch branch = getBranchByName(branchName);
        branch.setBranchHash(hash);
        writeEntity(branch);
    }

    public GitBranch getBranchByName(@NotNull String name) throws GitException {
        String hash = FileManager.readFromFile(Path.of(gitFileSystemManager.getPathToBranches())
                .resolve(Path.of(name)));
        return new GitBranch(name, hash);
    }

    public boolean checkBranchExists(@NotNull String name) {
        return FileManager.checkFileExists(Path.of(gitFileSystemManager.getPathToBranches()).resolve(Path.of(name)));
    }
}
