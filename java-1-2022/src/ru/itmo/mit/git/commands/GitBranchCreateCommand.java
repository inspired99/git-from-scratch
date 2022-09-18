package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.context.GitContext;
import ru.itmo.mit.git.body.context.GitMutableOperation;
import ru.itmo.mit.git.entities.GitBranch;
import ru.itmo.mit.git.utils.CommandMessages;

import java.util.List;

public class GitBranchCreateCommand extends GitAbstractCommand {
    public GitBranchCreateCommand(GitContext gitContext) {
        super(gitContext);
    }

    @Override
    public void execute(@NotNull List<String> args) throws GitException {
        loadState();
        CreateBranch.execute(gitContext, args.get(0));
        updateState(gitContext.getGitRepoManager().getHead());
    }

    @Override
    public void prettyPrint(@NotNull List<String> args) {
        outputStream.println(CommandMessages.BRANCH_CREATE_MESSAGE.getMessage() + args.get(0));
    }

    @Override
    public void checkArgs(@NotNull List<String> args) throws GitException {
        if (args.size() != 1) {
            throw new GitException("Wrong number of arguments for branch-create command");
        }
    }

    private final GitMutableOperation CreateBranch = (gitContext, arg) -> {
        GitBranch newBranch = new GitBranch(arg, gitContext.getGitRepoManager().getHead().getHash());
        gitContext.getGitFileManager().getGitBranchFileManager().writeEntity(newBranch);
        gitContext.getGitRepoManager().setHead(newBranch);
        gitContext.getGitRepoManager().setCurrentBranch(newBranch);
    };
}
