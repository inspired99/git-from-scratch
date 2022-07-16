package ru.itmo.mit.git.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.itmo.mit.git.GitException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
Utility класс для работы с файловым I/O: чтения и записи файлов и директорий, в том числе JSON-файлов
 */

public class FileManager {

    public static @NotNull String countHashForFile(@NotNull Path path) throws GitException {
        try {
            return DigestUtils.sha1Hex(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new GitException("Could not compute hash for file: " + path, e);
        }
    }

    public static void writeToJsonFile(@NotNull Path path, @NotNull JSONObject obj) throws GitException {
        try (FileWriter fw = new FileWriter(path.toString())) {
            fw.write(obj.toString());
            fw.flush();
        } catch (IOException e) {
            throw new GitException("Could not write to file: " + path, e);
        }
    }

    public static JSONObject readFromJsonFile(@NotNull Path path) throws GitException {
        JSONParser jsonParser = new JSONParser();
        JSONObject json;
        try (FileReader fr = new FileReader(path.toString())) {
            json = (JSONObject) jsonParser.parse(fr);
        } catch (IOException e) {
            throw new GitException("Could not read from file: " + path, e);
        } catch (ParseException e) {
            throw new GitException("Could not parse from JSON file: " + path, e);
        }
        return json;
    }

    public static boolean checkFileExists(@NotNull Path path) {
        Path pathToFile = Paths.get(String.valueOf(path));
        return Files.exists(pathToFile);
    }

    public static void createDirByPath(@NotNull Path path) throws GitException {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new GitException("Directory was not created" + path, e);
        }
    }

    public static void createFileByPath(@NotNull Path path) throws GitException {
        if (checkFileExists(path)) {
            return;
        }
        File file = new File(path.toString());
        try {
            if (!file.createNewFile()) {
                throw new GitException("Could not create file: " + path);
            }
        } catch (IOException e) {
            throw new GitException("Could not create file" + path, e);
        }
    }

    public static List<File> listDirsByPath(@NotNull Path path) {
        if (!checkFileExists(path)) {
            return Collections.emptyList();
        }
        File[] files = new File(path.toString()).listFiles();
        if (files == null) {
            return Collections.emptyList();
        }
        return Stream.of(files)
                .filter(File::isDirectory)
                .collect(Collectors.toList());
    }

    public static List<File> listFilesByPath(@NotNull Path path) {
        if (!checkFileExists(path)) {
            return Collections.emptyList();
        }
        File[] files = new File(path.toString()).listFiles();
        if (files == null) {
            return Collections.emptyList();
        }
        return Stream.of(files)
                .filter(file -> !file.isDirectory())
                .collect(Collectors.toList());
    }

    public static void writeFileByPath(@NotNull Path path, @NotNull String content) throws GitException {
        if (checkFileExists(path)) {
            deleteFileByPath(path);
        }
        try (FileWriter fw = new FileWriter(path.toString())) {
            fw.write(content);
            fw.flush();
        } catch (IOException e) {
            throw new GitException("Could not write to file: " + path, e);
        }
    }

    public static @NotNull String readFileByPath(@NotNull Path path) throws GitException {
        try {
            List<String> content = Files.readAllLines(path);
            return String.join("\n", content);
        } catch (IOException e) {
            throw new GitException("Could not read from file" + path, e);
        }
    }

    public static void deleteFileByPath(@NotNull Path path) throws GitException {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new GitException("Could not delete file/directory: " + path, e);
        }
    }

    public static @NotNull String readFromFile(@NotNull Path path) throws GitException {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream
                     = Files.lines(path, StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            throw new GitException("Could not read from file: " + path, e);
        }
        return contentBuilder.toString();
    }
}
