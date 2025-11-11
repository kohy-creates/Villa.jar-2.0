package xyz.kohara.features.moderation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Member;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModerationSaveData {

    private static final String SAVE_LOCATION = "data/moderation/";

    /*
            Warnings
     */
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

    /*
            Moderation history
     */
    public static void saveModerationAction(Member member, ActionType actionType, String reason, Member responsibleStaff) {
        // Build path: data/moderation/history/<user_id>.json
        String userId = member.getId();
        File historyFile = new File(SAVE_LOCATION + "history/" + userId + ".json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        historyFile.getParentFile().mkdirs();

        HistoryWrapper historyData;
        try (FileReader reader = new FileReader(historyFile)) {
            historyData = gson.fromJson(reader, HistoryWrapper.class);
        } catch (IOException e) {
            historyData = null;
        }

        if (historyData == null) {
            historyData = new HistoryWrapper(new ArrayList<>());
        }

        long unixTime = System.currentTimeMillis() / 1000L;
        ModerationAction action = new ModerationAction(actionType, reason, responsibleStaff.getId(), unixTime);

        historyData.history().add(action);

        try (FileWriter writer = new FileWriter(historyFile)) {
            gson.toJson(historyData, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<ModerationAction> getHistory(Member member) {
        File historyFile = new File(SAVE_LOCATION + "history/" + member.getId() + ".json");
        if (!historyFile.exists()) return new ArrayList<>();

        try (FileReader reader = new FileReader(historyFile)) {
            HistoryWrapper wrapper = new Gson().fromJson(reader, HistoryWrapper.class);
            return wrapper != null ? wrapper.history() : new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /// Records
    // Single warning
    public record Warning(long date, String reason, String responsible) {
    }

    // JSON container record
    public record HistoryWrapper(List<ModerationAction> history) {
    }

    // Single moderation entry
    public record ModerationAction(ActionType actionType, String reason, String responsible, long date) {
    }

    public enum ActionType {
        KICK("kick"),
        BAN("ban"),
        TIMEOUT("timeout"),
        WARN("warn");

        private final String type;

        ActionType(String type) {
            this.type = type;
        }

        public String get() {
            return this.type;
        }
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
