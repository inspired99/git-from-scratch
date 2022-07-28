package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.GitManager;
import ru.itmo.mit.git.utils.CommandMessages;

import java.util.ArrayList;
import java.util.List;

public class GitShowBranchesCommand extends GitAbstractCommand {
    private List<String> allBranches = new ArrayList<>();

    public GitShowBranchesCommand(GitManager gitManager) {
        super(gitManager);
    }

    @Override
    public void execute(@NotNull List<String> args) throws GitException {
        gitManager.loadGitState();
        allBranches = gitManager.getAllBranches();
    }

    @Override
    public void prettyPrint(List<String> args) {
        StringBuilder message = new StringBuilder(CommandMessages.SHOW_BRANCHES_MESSAGE.getMessage());
        for (var branch : allBranches) {
            message.append("\n").append(branch);
        }
        gitManager.getPrintStream().println(message);
    }

    @Override
    public void checkArgs(@NotNull List<String> args) throws GitException {
        if (!args.isEmpty()) {
            throw new GitException("Wrong number of arguments for show-branches command ");
        }
    }
}
