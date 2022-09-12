package ru.itmo.mit.git.utils;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class ParserArgs {
    @Parameter(
            names = "git",
            description = "Key word",
            variableArity = true,
            required = true
    )
    private List<String> gitArgs;

    ParserArgs() {
        gitArgs = new ArrayList<>();
    }

    public List<String> getGitArgs() {
        return gitArgs;
    }
}