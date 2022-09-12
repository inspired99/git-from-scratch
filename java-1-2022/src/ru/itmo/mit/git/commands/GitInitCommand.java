package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.context.GitContext;
import ru.itmo.mit.git.body.context.GitMutableOperation;
import ru.itmo.mit.git.entities.GitBranch;
import ru.itmo.mit.git.entities.GitCommit;
import ru.itmo.mit.git.utils.CommandMessages;

import java.util.List;

public class GitInitCommand extends GitAbstractCommand {
    public GitInitCommand(GitContext gitContext) {
        super(gitContext);
    }

    @Override
    public void execute(@NotNull List<String> args) throws GitException {
        InitializeGit.execute(gitContext, null);
        updateState(gitContext.getGitRepoManager().getCurrentBranch());
    }

    @Override
    public void prettyPrint(@NotNull List<String> args) {
        outputStream.println(CommandMessages.INIT_MESSAGE.getMessage() +
                gitContext.getGitFileManager().getPathToWorkDir());
    }

    @Override
    public void checkArgs(@NotNull List<String> args) throws GitException {
        if (!args.isEmpty()) {
            throw new GitException("Wrong number of arguments for init command ");
        }
    }

    private final GitMutableOperation InitializeGit = (gitContext, arg) -> {
        gitContext.getGitFileManager().createGitDirs();
        GitCommit initCommit = new GitCommit(CommandMessages.COMMIT_DEFAULT_AUTHOR.getMessage(), "Initial commit",
                "0", "0");
        gitContext.getGitFileManager().getGitCommitFileManager().writeEntity(initCommit);
        GitBranch master = new GitBranch(GitConstants.MASTER, initCommit.getHash());
        gitContext.getGitRepoManager().setCurrentBranch(master);
        gitContext.getGitRepoManager().setHead(master);
    };
}
