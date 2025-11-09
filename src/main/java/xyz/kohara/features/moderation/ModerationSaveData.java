package xyz.kohara.features.moderation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Member;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModerationSaveData {

    private static final String SAVE_LOCATION = "data/moderation/";

    private static File createIfMissing(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return file;
    }

    public static void saveWarning(Member member, String reason, Member responsibleStaff) {
        File saveFile = createIfMissing(SaveLocation.WARNINGS.path);

        Map<String, List<Warning>> warnings = new HashMap<>();

        String memberId = member.getId(); // from JDA
        long unixTime = System.currentTimeMillis() / 1000L;
        String staffID = responsibleStaff.getId();
        Warning newWarn = new Warning(unixTime, reason, staffID);

        warnings.computeIfAbsent(memberId, k -> new ArrayList<>()).add(newWarn);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(saveFile)) {
            gson.toJson(warnings, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Warning> getWarnings(Member member) {
        Map<String, List<Warning>> warnings = new HashMap<>();
        try (FileReader reader = new FileReader(SaveLocation.WARNINGS.path)) {
            warnings = new Gson().fromJson(reader, new TypeToken<Map<String, List<Warning>>>(){}.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (warnings.isEmpty()) {
            return new ArrayList<>();
        }
        else {
            return warnings.get(member.getId());
        }
    }

    // Records
    public record Warning(long date, String reason, String responsible) {
    }

    // Save locations as enums because I can do that
    private enum SaveLocation {
        WARNINGS("warnings");

        private final String path;

        SaveLocation(String path) {
            this.path = SAVE_LOCATION + path + ".json";
        }
    }
}
