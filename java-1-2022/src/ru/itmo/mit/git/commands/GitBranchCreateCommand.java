package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.GitManager;
import ru.itmo.mit.git.utils.CommandMessages;

import java.util.List;

public class GitBranchCreateCommand extends GitAbstractCommand {
    public GitBranchCreateCommand(GitManager gitManager) {
        super(gitManager);
    }

    @Override
    public void execute(@NotNull List<String> args) throws GitException {
        gitManager.loadGitState();
        gitManager.addBranchToGit(args.get(0));
        gitManager.saveGitState(gitManager.getHead());
    }

    @Override
    public void prettyPrint(@NotNull List<String> args) {
        gitManager.getPrintStream().println(CommandMessages.BRANCH_CREATE_MESSAGE.getMessage() + args.get(0));
    }

    @Override
    public void checkArgs(@NotNull List<String> args) throws GitException {
        if (args.size() != 1) {
            throw new GitException("Wrong number of arguments for branch-create command");
        }
    }
}
