package ru.itmo.mit.git.body;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.commands.*;

/*
Класс для создания инстансов команд
 */

public class GitCommandsManager {
    private final Map<String, GitAbstractCommand> commandsMap;
    private final GitManager gitManager;

    public GitCommandsManager(GitManager gitManager) {
        this.gitManager = gitManager;
        commandsMap = new HashMap<>();
        fillCommandsMap();
    }

    private void fillCommandsMap() {
        commandsMap.put(GitConstants.INIT, new GitInitCommand(gitManager));
        commandsMap.put(GitConstants.ADD, new GitAddCommand(gitManager));
        commandsMap.put(GitConstants.RM, new GitRmCommand(gitManager));
        commandsMap.put(GitConstants.STATUS, new GitStatusCommand(gitManager));
        commandsMap.put(GitConstants.COMMIT, new GitCommitCommand(gitManager));
    }

    public GitAbstractCommand getCommand(@NotNull String commandName) throws GitException {
        if (!commandsMap.containsKey(commandName)) {
            throw new GitException("No such git command supported ");
        }
        return commandsMap.get(commandName);
    }

}
