package xyz.kohara.status;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import net.dv8tion.jda.api.entities.Activity;
import xyz.kohara.Aroki;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class BotActivity {

    private static final List<Activity> STATUS_LIST = new ArrayList<>();

    static {
        Gson gson = new Gson();
        try {
            String content = String.join("\n", Files.readAllLines(Paths.get("data/status.json")));
            StatusData statusData = gson.fromJson(content, StatusData.class);
            Map<String, List<String>> activityMap = statusData.getAllActivities();
            System.out.println(activityMap);
            for (String key : activityMap.keySet()) {
                List<String> text = activityMap.get(key);
                for (String entry : text) {
                    Activity.ActivityType type = null;
                    boolean streaming = false;
                    switch (key) {
                        case "watching" -> type = Activity.ActivityType.WATCHING;
                        case "normal" -> type = Activity.ActivityType.CUSTOM_STATUS;
                        case "playing" -> type = Activity.ActivityType.PLAYING;
                        case "listening" -> type = Activity.ActivityType.LISTENING;
                        case "streaming" -> {
                            // Streaming gets special treatment because it requires a URL address
                            streaming = true;
                            type = Activity.ActivityType.STREAMING;
                        }
                        case "competing" -> type = Activity.ActivityType.COMPETING;
                    }
                    assert type != null;
                    Activity status = (!streaming) ? Activity.of(type, entry) : Activity.of(type, entry, statusData.getStreamingURL());
                    STATUS_LIST.add(status);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void schedule() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Activity randomActivity = STATUS_LIST.get(new Random().nextInt(STATUS_LIST.size()));
                Aroki.getBot().getPresence().setActivity(randomActivity);
            }
        }, 0, 10 * 1000);
    }
}
