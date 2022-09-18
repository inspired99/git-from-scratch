package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.context.GitContext;
import ru.itmo.mit.git.body.context.GitImmutableOperation;
import ru.itmo.mit.git.utils.CommandMessages;
import ru.itmo.mit.git.utils.FileManager;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GitShowBranchesCommand extends GitAbstractCommand {
    private List<String> allBranches = new ArrayList<>();

    public GitShowBranchesCommand(GitContext gitContext) {
        super(gitContext);
    }

    @Override
    public void execute(@NotNull List<String> args) throws GitException {
        loadState();
        GetAllBranches.execute(gitContext, null);
    }

    @Override
    public void prettyPrint(@NotNull List<String> args) {
        StringBuilder message = new StringBuilder(CommandMessages.SHOW_BRANCHES_MESSAGE.getMessage());
        for (var branch : allBranches) {
            message.append("\n").append(branch);
        }
        outputStream.println(message);
    }

    @Override
    public void checkArgs(@NotNull List<String> args) throws GitException {
        if (!args.isEmpty()) {
            throw new GitException("Wrong number of arguments for show-branches command ");
        }
    }

    private final GitImmutableOperation GetAllBranches = (gitContext, argument) -> allBranches =
            (FileManager.listFilesByPath(Path.of(gitContext.getGitFileManager().getPathToBranches()))
                    .stream()
                    .map(File::getName)
                    .collect(Collectors.toList()));
}
