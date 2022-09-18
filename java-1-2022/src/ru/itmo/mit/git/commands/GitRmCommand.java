package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.context.GitContext;
import ru.itmo.mit.git.body.context.GitMutableOperation;
import ru.itmo.mit.git.utils.CommandMessages;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class GitRmCommand extends GitAbstractCommand {

    public GitRmCommand(GitContext gitContext) {
        super(gitContext);
    }

    @Override
    public void execute(@NotNull List<String> args) throws GitException {
        loadState();
        for (var file : args) {
            RemoveFromIndex.execute(gitContext, file);
        }
        updateState(gitContext.getGitRepoManager().getHead());
    }

    @Override
    public void prettyPrint(@NotNull List<String> args) {
        StringBuilder message = new StringBuilder(CommandMessages.REMOVE_MESSAGE.getMessage());
        for (var file : args) {
            message.append("\n").append(file);
        }
        outputStream.println(message);
    }

    @Override
    public void checkArgs(@NotNull List<String> args) throws GitException {
        if (args.isEmpty()) {
            throw new GitException("Wrong number of arguments for rm command ");
        }
    }

    private final GitMutableOperation RemoveFromIndex = (gitContext, arg) -> {
        String absPath = gitContext.getGitFileManager().pathInGitDir(Path.of(arg)).toString();
        if (new File(absPath).isDirectory()) {
            throw new GitException("Cannot remove directories, specify file(s) ");
        }
        if ((gitContext.getGitIndex().getGitIndexTracked().remove(absPath) == null)) {
            throw new GitException("No such file can be removed: " + arg);
        }
    };
}
