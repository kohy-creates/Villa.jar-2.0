package xyz.kohara.autoreact;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class AutoReact extends ListenerAdapter {
    private static final String CONFIG_PATH = "data/auto_reactions.json";
    private static final Map<String, List<String>> AUTO_REACTIONS = new HashMap<>();

    static {
        try (FileReader reader = new FileReader(CONFIG_PATH)) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<Integer, Map<String, Object>>>() {
            }.getType();
            Map<Integer, Map<String, Object>> rawConfig = gson.fromJson(reader, type);

            for (Map.Entry<Integer, Map<String, Object>> entry : rawConfig.entrySet()) {
                List<String> phrases = (List<String>) entry.getValue().get("phrases");
                List<String> reactions = (List<String>) entry.getValue().get("reactions");

                for (String phrase : phrases) {
                    AUTO_REACTIONS.put(phrase, reactions);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        String messageContent = event.getMessage().getContentRaw();
        for (String key : AUTO_REACTIONS.keySet()) {
            if ((key.indexOf("regex:") == 0 && Pattern.compile(key.substring(6)).matcher(messageContent).matches()) || messageContent.contains(key)) {
                for (String emoji : AUTO_REACTIONS.get(key)) {
                    Emoji reaction = Emoji.fromFormatted(emoji);
                    event.getMessage().addReaction(reaction).queue();
                }
            }
        }
    }
}
