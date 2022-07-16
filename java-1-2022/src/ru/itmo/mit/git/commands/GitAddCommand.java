package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.GitManager;
import ru.itmo.mit.git.body.GitPathsHolder;
import ru.itmo.mit.git.utils.CommandMessages;

import java.nio.file.Path;
import java.util.List;

public class GitAddCommand extends GitAbstractCommand {
    public GitAddCommand(GitManager gitManager) {
        super(gitManager);
    }

    @Override
    public void execute(@NotNull List<String> args) throws GitException {
        GitPathsHolder pathsHolder = gitManager.getGitPathsHolder();
        for (var file : args) {
            if (!pathsHolder.checkFileInGitDir(Path.of(file))) {
                throw new GitException("No such file can be added: " + file);
            }
            String filePath = gitManager.getGitPathsHolder().pathInGitDir(Path.of(file)).toString();
            gitManager.getGitIndex().addToIndex(filePath);
            gitManager.getGitPathsHolder().writeIndex(gitManager.getGitIndex());
        }
    }

    @Override
    public void prettyPrint(@NotNull List<String> args) {
        StringBuilder message = new StringBuilder(CommandMessages.ADD_MESSAGE.getMessage());
        for (var file : args) {
            message.append("\n");
            message.append(file);
        }
        gitManager.getPrintStream().println(message);
    }

    @Override
    public void checkArgs(@NotNull List<String> args) throws GitException {
        if (args.isEmpty()) {
            throw new GitException("Wrong number of arguments for add command ");
        }
    }
}
