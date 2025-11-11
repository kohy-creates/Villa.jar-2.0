package xyz.kohara;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    private static final Map<String, String> CONFIG;
    private static final String configPath = "data/config.json";

    private static final List<String> configEntries = List.of(
            "token", "CHANGE_ME",
            "server_id", "0000",
            "staff_role_id", "0000",
            "dev_role_id", "0000",
            "bot_name", "Aroki",
            "support_channel", "0000",
            "invalid_tag_id", "0000",
            "open_tag_id", "0000",
            "resolved_tag_id", "0000",
            "to_do_tag_id", "0000",
            "duplicate_tag_id", "0000",
            "tag_prefix", "!",
            "mortals_role", "0000",
            "invite", "https://example.com/"
    );

    static {
        tryCreateConfigFile();

        // Load existing config (or empty if new file)
        Map<String, String> loaded;
        try (FileReader reader = new FileReader(configPath)) {
            Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            loaded = new Gson().fromJson(reader, type);
            if (loaded == null) loaded = new HashMap<>();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Fill missing entries from configEntries list
        boolean updated = false;
        for (int i = 0; i < configEntries.size(); i += 2) {
            String key = configEntries.get(i);
            String value = configEntries.get(i + 1);
            if (!loaded.containsKey(key)) {
                loaded.put(key, value);
                updated = true;
            }
        }

        // Save back if new entries were added
        if (updated) {
            try (FileWriter writer = new FileWriter(configPath)) {
                new GsonBuilder()
                        .setPrettyPrinting().create()
                        .toJson(loaded, writer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        CONFIG = loaded;
    }


    private static void tryCreateConfigFile() {
        File file = new File(configPath);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String getOption(String config) {
        return CONFIG.get(config);
    }
}
