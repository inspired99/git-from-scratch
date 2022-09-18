package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.context.GitContext;
import ru.itmo.mit.git.body.context.GitMutableOperation;
import ru.itmo.mit.git.utils.CommandMessages;
import ru.itmo.mit.git.utils.FileManager;

import java.nio.file.Path;
import java.util.List;

public class GitBranchRemoveCommand extends GitAbstractCommand {
    public GitBranchRemoveCommand(GitContext gitContext) {
        super(gitContext);
    }

    @Override
    public void execute(@NotNull List<String> args) throws GitException {
        loadState();
        RemoveBranch.execute(gitContext, args.get(0));
        updateState(gitContext.getGitRepoManager().getHead());
    }

    @Override
    public void prettyPrint(@NotNull List<String> args) {
        outputStream.println(CommandMessages.BRANCH_REMOVE_MESSAGE.getMessage() + args.get(0));
    }

    @Override
    public void checkArgs(@NotNull List<String> args) throws GitException {
        if (args.isEmpty()) {
            throw new GitException("Wrong number of arguments for rm command ");
        }
    }

    private final GitMutableOperation RemoveBranch = (gitContext, arg) -> {
        if (gitContext.getGitRepoManager().getCurrentBranch() != null) {
            if (gitContext.getGitRepoManager().getCurrentBranch().getBranchName().equals(arg)) {
                throw new GitException("Could not remove checked out branch: " + arg);
            }
        }
        FileManager.deleteFileByPath(Path.of(gitContext.getGitFileManager().getPathToBranches()).resolve(arg));
    };
}
