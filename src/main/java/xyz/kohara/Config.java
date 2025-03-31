package xyz.kohara;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Config {

    public static Map<String, String> CONFIG;

    static {
        String configFile = "data/config.json";
        try (FileReader reader = new FileReader(configFile)) {
            Type type = new TypeToken<HashMap<String, String>>() {}.getType();
            CONFIG = new Gson().fromJson(reader, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getOption(String config) {
        return CONFIG.get(config);
    }
}
