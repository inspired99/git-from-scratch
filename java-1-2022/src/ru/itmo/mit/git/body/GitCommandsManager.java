package ru.itmo.mit.git.body;

import java.util.*;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.body.context.GitContext;
import ru.itmo.mit.git.commands.*;

/*
Класс для управления экземплярами команд гита
 */

public class GitCommandsManager {
    private final Map<String, GitAbstractCommand> commandsMap;
    private final GitContext gitContext;

    public GitCommandsManager(@NotNull GitContext gitContext, String workingDir) {
        this.gitContext = gitContext;
        this.gitContext.setGitPathHolder(new GitFileSystemManager(workingDir));
        commandsMap = new HashMap<>();
        fillCommandsMap();
    }

    private void fillCommandsMap() {
        commandsMap.put(GitConstants.INIT, new GitInitCommand(gitContext));
        commandsMap.put(GitConstants.ADD, new GitAddCommand(gitContext));
        commandsMap.put(GitConstants.RM, new GitRmCommand(gitContext));
        commandsMap.put(GitConstants.STATUS, new GitStatusCommand(gitContext));
        commandsMap.put(GitConstants.COMMIT, new GitCommitCommand(gitContext));
        commandsMap.put(GitConstants.RESET, new GitResetCommand(gitContext));
        commandsMap.put(GitConstants.LOG, new GitLogCommand(gitContext));
        commandsMap.put(GitConstants.CHECKOUT, new GitCheckoutCommand(gitContext));
        commandsMap.put(GitConstants.BRANCH_CREATE, new GitBranchCreateCommand(gitContext));
        commandsMap.put(GitConstants.BRANCH_REMOVE, new GitBranchRemoveCommand(gitContext));
        commandsMap.put(GitConstants.SHOW_BRANCHES, new GitShowBranchesCommand(gitContext));
        commandsMap.put(GitConstants.MERGE, new GitMergeBranchCommand(gitContext));
    }

    public GitAbstractCommand getCommand(@NotNull String commandName) throws GitException {
        if (!commandsMap.containsKey(commandName)) {
            throw new GitException("No such git command supported ");
        }
        return commandsMap.get(commandName);
    }
}
