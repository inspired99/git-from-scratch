package ru.itmo.mit.git;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.body.GitCommandsManager;
import ru.itmo.mit.git.body.context.GitContext;
import ru.itmo.mit.git.commands.GitAbstractCommand;

import java.io.PrintStream;
import java.util.List;

public class GitCliImpl implements GitCli {
    private final GitCommandsManager gitCommandsManager;
    private final GitContext gitContext;
    private PrintStream outputStream = System.out;

    public GitCliImpl(String workingDir) {
        this.gitContext = new GitContext();
        this.gitCommandsManager = new GitCommandsManager(gitContext, workingDir);
    }

    @Override
    public void runCommand(@NotNull String command, @NotNull List<@NotNull String> arguments) throws GitException {
        GitAbstractCommand gitAbstractCommand = gitCommandsManager.getCommand(command);
        gitAbstractCommand.setOutputStream(outputStream);
        gitAbstractCommand.checkArgs(arguments);
        gitAbstractCommand.execute(arguments);
        gitAbstractCommand.prettyPrint(arguments);
    }

    @Override
    public void setOutputStream(@NotNull PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public @NotNull String getRelativeRevisionFromHead(int n) throws GitException {
        return gitContext.getGitRepoManager().getHashRevision(gitContext, n);
    }
}
