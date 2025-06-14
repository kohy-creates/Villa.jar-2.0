package xyz.kohara.support;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.entities.Member;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ForumData {

    private static final File FORUM_DATA = new File("data/forum_data.json");
    private static final Gson GSON = new Gson();

    static {
        if (!FORUM_DATA.exists()) {
            try {
                if (FORUM_DATA.createNewFile())
                    try (FileWriter writer = new FileWriter(FORUM_DATA)) {
                        writer.write("{}");
                    }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static JsonObject loadJson() throws IOException {
        try (Reader reader = new FileReader(FORUM_DATA)) {
            return GSON.fromJson(reader, JsonObject.class);
        }
    }

    private static void saveJson(JsonObject jsonObject) throws IOException {
        try (Writer writer = new FileWriter(FORUM_DATA)) {
            GSON.toJson(jsonObject, writer);
        }
    }

    public static void addEntry(String key, String op, long creationDate) throws IOException {
        JsonObject json = loadJson();

        JsonObject entry = new JsonObject();
        entry.addProperty("op", op);
        entry.addProperty("creation_date", creationDate);
        entry.addProperty("last_reminded", creationDate);

        json.add(key, entry);
        saveJson(json);
    }

    public static void removeEntry(String key) throws IOException {
        JsonObject json = loadJson();
        if (json.has(key)) {
            json.remove(key);
            saveJson(json);
        }
    }

    public static boolean entryExists(String key) throws IOException {
        JsonObject json = loadJson();
        return json.has(key);
    }

    public static String getEntryValue(String key, String field) throws IOException {
        JsonObject json = loadJson();
        return json.getAsJsonObject(key).get(field).getAsString();
    }

    public static void setEntryValue(String key, String field, long value) throws IOException {
        JsonObject json = loadJson();
        json.getAsJsonObject(key).addProperty(field, value);
        saveJson(json);
    }

    public static List<String> getAllThreads() throws IOException {
        JsonObject json = loadJson();
        List<String> entryIds = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            entryIds.add(entry.getKey());
        }
        return entryIds;

    }

    public static List<String> findThreads(Member op) throws IOException {
        String id = op.getId();
        List<String> openThreads = new ArrayList<>();

        JsonObject json = loadJson();

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String key = entry.getKey();
            JsonObject data = (JsonObject) entry.getValue();

            if (data.has("op")) {
                if (data.get("op").getAsString().equals(id)) openThreads.add(key);
            }
        }
        return openThreads;
    }
}
