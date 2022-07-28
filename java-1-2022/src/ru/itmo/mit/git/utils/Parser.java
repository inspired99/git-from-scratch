package ru.itmo.mit.git.utils;

/*
Класс, который парсит args и возвращает команду и список аргументов, при этом
обрабатывая только команды вида "git <command> <arg1, ..., argN>" - иначе GitException в случае, если
отсутствует команда после git или нет ключевого слова git
 */

import com.beust.jcommander.JCommander;
import ru.itmo.mit.git.GitException;

import java.util.List;

public class Parser {
    public List<String> parse(String[] args) throws GitException {
        ParserArgs parserArgs = new ParserArgs();
        JCommander jParser = JCommander.newBuilder()
                .addObject(parserArgs)
                .build();
        try {
            jParser.parse(args);
        } catch (RuntimeException e) {
            throw new GitException("Could not parse git command", e);
        }

        return parserArgs.getGitArgs();
    }
}
