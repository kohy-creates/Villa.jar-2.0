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

    public static void saveWarning(Member member, String reason, Member responsibleStaff) {
        File saveFile = new File(SaveLocation.WARNINGS.path);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, List<Warning>> warnings;

        try (FileReader reader = new FileReader(saveFile)) {
            warnings = gson.fromJson(reader, new TypeToken<Map<String, List<Warning>>>() {
            }.getType());
        } catch (IOException e) {
            warnings = new HashMap<>();
        }

        if (warnings == null) {
            warnings = new HashMap<>();
        }

        String memberId = member.getId();
        long unixTime = System.currentTimeMillis() / 1000L;
        String staffID = responsibleStaff.getId();
        Warning newWarn = new Warning(unixTime, reason, staffID);

        warnings.computeIfAbsent(memberId, k -> new ArrayList<>()).add(newWarn);

        try (FileWriter writer = new FileWriter(saveFile)) {
            gson.toJson(warnings, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeWarning(Member member, int index) {
        File saveFile = new File(SaveLocation.WARNINGS.path);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Map<String, List<Warning>> warnings;

        try (FileReader reader = new FileReader(saveFile)) {
            warnings = gson.fromJson(reader, new TypeToken<Map<String, List<Warning>>>() {
            }.getType());
        } catch (IOException e) {
            warnings = new HashMap<>();
        }

        if (warnings == null) {
            warnings = new HashMap<>();
        }

        String memberId = member.getId();
        List<Warning> memberWarnings = warnings.get(memberId);
        if (memberWarnings == null || memberWarnings.isEmpty()) {
            return;
        }

        if (index < 1 || index > memberWarnings.size()) {
            return;
        }

        memberWarnings.remove(index - 1);
        if (memberWarnings.isEmpty()) {
            warnings.remove(memberId);
        }
        try (FileWriter writer = new FileWriter(saveFile)) {
            gson.toJson(warnings, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public static List<Warning> getWarnings(Member member) {
        return getWarnings(member.getId());
    }

    public static List<Warning> getWarnings(String id) {
        Map<String, List<Warning>> warnings;
        try (FileReader reader = new FileReader(SaveLocation.WARNINGS.path)) {
            warnings = new Gson().fromJson(reader, new TypeToken<Map<String, List<Warning>>>() {
            }.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (warnings == null) {
            return new ArrayList<>();
        } else {
            return warnings.get(id);
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
            createIfMissing(this.path);
        }

        private void createIfMissing(String path) {
            File file = new File(path);
            if (!file.exists()) {
                try {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
