package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.GitManager;
import ru.itmo.mit.git.body.GitPathsHolder;
import ru.itmo.mit.git.objects.GitBranch;
import ru.itmo.mit.git.objects.GitCommit;
import ru.itmo.mit.git.utils.CommandMessages;

import java.util.List;

public class GitInitCommand extends GitAbstractCommand {
    public GitInitCommand(GitManager gitManager) {
        super(gitManager);
    }

    @Override
    public void execute(@NotNull List<String> args) throws GitException {
        GitPathsHolder gitPathsHolder = gitManager.getGitPathsHolder();
        gitPathsHolder.createGitDirs();
        GitCommit initCommit = new GitCommit("Java-2022", "Initial commit", null, null);
        gitPathsHolder.writeCommit(initCommit);
        GitBranch master = new GitBranch(GitConstants.MASTER, initCommit.getHashObject());
        gitPathsHolder.writeBranch(master);
        gitManager.setCurrentBranch(master);
        gitManager.updateHead(gitManager.getCurrentBranch());
    }

    @Override
    public void prettyPrint(List<String> args) {
        gitManager.getPrintStream().println(CommandMessages.INIT_MESSAGE.getMessage() + gitManager.getWorkingDir());
    }

    @Override
    public void checkArgs(@NotNull List<String> args) throws GitException {
        if (!args.isEmpty()) {
            throw new GitException("Wrong number of arguments for init command ");
        }
    }
}
