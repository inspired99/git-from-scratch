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
Класс для чтения и записи JSON представления гит-объектов
 */

public class JsonFileManager {

    public static void writeToFile(@NotNull Path path, @NotNull JSONObject obj) throws GitException {
        try (FileWriter fw = new FileWriter(path.toString())) {
            fw.write(obj.toJSONString());
            fw.flush();
        } catch (IOException e) {
            throw new GitException("Could not write to file: " + path, e);
        }
    }

    public static JSONObject readFromFile(@NotNull Path path) throws GitException {
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
}
