package ru.itmo.mit.git.utils;

import org.jetbrains.annotations.NotNull;
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
