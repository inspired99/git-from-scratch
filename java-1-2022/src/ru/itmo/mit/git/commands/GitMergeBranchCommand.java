package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.GitManager;
import ru.itmo.mit.git.utils.CommandMessages;

import java.util.ArrayList;
import java.util.List;

public class GitMergeBranchCommand extends GitAbstractCommand {
    private List<String> mergeConflicts = new ArrayList<>();

    public GitMergeBranchCommand(GitManager gitManager) {
        super(gitManager);
    }

    @Override
    public void execute(@NotNull List<String> args) throws GitException {
        gitManager.loadGitState();
        mergeConflicts = gitManager.mergeBranchWithCurrent(args.get(0));
        gitManager.saveGitState(gitManager.getHead());
    }

    @Override
    public void prettyPrint(@NotNull List<String> args) {
        StringBuilder message = new StringBuilder();
        message.append(CommandMessages.MERGE_MESSAGE.getMessage()).append(args.get(0))
                .append(CommandMessages.MERGE_INTO_MESSAGE.getMessage())
                .append(gitManager.getCurrentBranch().getBranchName());
        if (!mergeConflicts.isEmpty()) {
            message.append("\n").append(CommandMessages.MERGE_CONFLICT_MESSAGE.getMessage())
                    .append(String.join("\n", mergeConflicts));
        }
        gitManager.getPrintStream().println(message);
    }

    @Override
    public void checkArgs(@NotNull List<String> args) throws GitException {
        if (args.isEmpty()) {
            throw new GitException("Wrong number of arguments for merge command ");
        }
    }

}
