package ru.itmo.mit.git.utils;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.itmo.mit.git.GitException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

/*
Utility класс для работы с JSON-файлами
 */

public class JsonFileManager {
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
}
