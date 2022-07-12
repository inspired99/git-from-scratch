package ru.itmo.mit.git;

import ru.itmo.mit.git.utils.Parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            Parser parser = new Parser();
            List<String> parsedArgs = parser.parse(args);
            String commandName = parsedArgs.get(0);
            List<String> commandArgs = new ArrayList<>();
            if (parsedArgs.size() > 1) {
                commandArgs = parsedArgs.subList(1, parsedArgs.size());
            }
            GitCliImpl gitCli = new GitCliImpl(System.getProperty("user.dir") + File.separator + args[0]);
            gitCli.runCommand(commandName, commandArgs);
        } catch (GitException e) {
            StringBuilder message = new StringBuilder(e.getMessage());
            Throwable cause;
            Throwable result = e;
            while (null != (cause = result.getCause()) && (result != cause)) {
                message.append("\n");
                message.append(cause.getMessage());
                result = cause;
            }
            System.err.println(message);
        }
    }
}
