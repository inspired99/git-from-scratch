package ru.itmo.mit.git.utils;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.itmo.mit.git.GitException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
Utility класс для работы с файловым I/O
 */

public class FileManager {
    public static void writeToJsonFile(@NotNull Path path, @NotNull JSONObject obj) throws GitException {
        try (FileWriter fw = new FileWriter(path.toString())) {
            fw.write(obj.toString());
            fw.flush();
        } catch (IOException e) {
            throw new GitException("Could not write to file: ", e);
        }
    }

    public static JSONObject readFromJsonFile(@NotNull Path path) throws GitException {
        JSONParser jsonParser = new JSONParser();
        JSONObject json;
        try (FileReader fr = new FileReader(path.toString().strip())) {
            json = (JSONObject) jsonParser.parse(fr);
        } catch (IOException e) {
            throw new GitException("Could not read from file: ", e);
        } catch (ParseException e) {
            throw new GitException("Could not parse from JSON file: ", e);
        }
        return json;
    }

    public static boolean checkFileExists(@NotNull Path path) {
        return Files.exists(path);
    }

    public static void createDirByPath(@NotNull Path path) throws GitException {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new GitException("Directory was not created", e);
        }
    }

    public static void createFileByPath(@NotNull Path path) throws GitException {
        if (checkFileExists(path)) {
            return;
        }
        File file = new File(path.toString());
        try {
            if (!file.createNewFile()) {
                throw new GitException("Could not create file: " + file);
            }
        } catch (IOException e) {
            throw new GitException("Could not create file", e);
        }
    }

    public static @NotNull String prettyPrintMap(@NotNull HashMap<String, String> hashMap, @NotNull String title,
                                                 boolean printTitle) {
        if (!hashMap.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            if (!printTitle) {
                sb.append(title).append(":").append("\n");
            }
            for (var entry : hashMap.entrySet()) {
                if (printTitle) {
                    sb.append(title).append(" ").append(entry.getKey()).append(" ")
                            .append(entry.getValue()).append("\n");
                } else {
                    sb.append(entry.getKey()).append(" ").append("\n");
                }
            }
            return sb.toString();
        }
        return "";
    }

    public static @NotNull List<File> listDirsByPath(@NotNull Path path) {
        if (!checkFileExists(path)) {
            return Collections.emptyList();
        }
        File thisFile = new File(path.toString());
        List<File> res = new ArrayList<>();
        if (thisFile.isDirectory()) {
            res.add(thisFile);
        }
        File[] files = new File(path.toString()).listFiles();
        if (files == null) {
            return res;
        }
        res.addAll(Stream.of(files)
                .filter(File::isDirectory)
                .collect(Collectors.toList()));
        return res;
    }

    public static @NotNull List<File> listFilesByPath(@NotNull Path path) {
        if (!checkFileExists(path)) {
            return Collections.emptyList();
        }
        File thisFile = new File(path.toString());
        List<File> res = new ArrayList<>();
        if (!thisFile.isDirectory()) {
            res.add(thisFile);
        }
        File[] files = thisFile.listFiles();
        if (files == null) {
            return res;
        }
        res.addAll(Stream.of(files)
                .filter(file -> !file.isDirectory())
                .collect(Collectors.toList()));
        return res;
    }

    public static void writeFileByPath(@NotNull Path path, @NotNull String content) throws GitException {
        if (checkFileExists(path)) {
            deleteFileByPath(path);
        }
        try (FileWriter fw = new FileWriter(path.toString())) {
            fw.write(content);
            fw.flush();
        } catch (IOException e) {
            throw new GitException("Could not write to file: ", e);
        }
    }

    public static void deleteFileByPath(@NotNull Path path) throws GitException {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new GitException("Could not delete file/directory: ", e);
        }
    }

    public static void deleteAll(@NotNull List<File> files) throws GitException {
        try {
            for (var file : files) {
                if (file.isDirectory()) {
                    FileUtils.deleteDirectory(file);
                } else {
                    deleteFileByPath(Path.of(file.getAbsolutePath()));
                }
            }
        } catch (IOException e) {
            throw new GitException("Could not delete files recursively ");
        }
    }

    public static @NotNull String readFromFile(@NotNull Path path) throws GitException {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream
                     = Files.lines(path, StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            throw new GitException("Could not read from file: ", e);
        }
        return contentBuilder.toString();
    }
}
