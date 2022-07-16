package ru.itmo.mit.git;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.body.GitManager;
import ru.itmo.mit.git.commands.GitAbstractCommand;

import java.io.PrintStream;
import java.util.List;

public class GitCliImpl implements GitCli {
    private final GitManager gitManager;

    public GitCliImpl(String workingDir) {
        this.gitManager = new GitManager(workingDir);
    }

    @Override
    public void runCommand(@NotNull String command, @NotNull List<@NotNull String> arguments) throws GitException {
        GitAbstractCommand gitAbstractCommand = gitManager.getGitCommandsManager().getCommand(command);
        gitAbstractCommand.checkArgs(arguments);
        gitAbstractCommand.execute(arguments);
        gitAbstractCommand.prettyPrint(arguments);
    }

    @Override
    public void setOutputStream(@NotNull PrintStream outputStream) {
        gitManager.setOutputStream(outputStream);
    }

    @Override
    public @NotNull String getRelativeRevisionFromHead(int n) throws GitException {
        return gitManager.getRevisionFromHead(n);
    }
}
