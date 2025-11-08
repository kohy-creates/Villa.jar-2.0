package xyz.kohara.features.autoreact;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.kohara.Aroki;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
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
                @SuppressWarnings("unchecked")
                List<String> phrases = (List<String>) entry.getValue().get("phrases");
                @SuppressWarnings("unchecked")
                List<String> reactions = (List<String>) entry.getValue().get("reactions");

                for (String phrase : phrases) {
                    AUTO_REACTIONS.put(phrase, reactions);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        String content = event.getMessage().getContentRaw().toLowerCase();
        if (content.contains(Aroki.getServer().getSelfMember().getAsMention())) {
            event.getMessage().addReaction(Emoji.fromFormatted("👋")).queue();
        }
        for (String key : AUTO_REACTIONS.keySet()) {
            if ((key.indexOf("regex:") == 0 && Pattern.compile(key.substring("regex:".length())).matcher(content).find()) || content.contains(key)) {
                for (String emoji : AUTO_REACTIONS.get(key)) {
                    if (emoji.indexOf("author|") == 0) {
                        System.out.println(emoji);
                        Matcher matcher = Pattern.compile("^author\\|(\\d+):(.+)$").matcher(emoji);
                        if (!matcher.matches()) continue;
                        String author = matcher.group(1);
                        if (!Objects.equals(author, event.getAuthor().getId())) continue;
                        emoji = matcher.group(2);
                    }
                    Emoji reaction = Emoji.fromFormatted(emoji);
                    event.getMessage().addReaction(reaction).queue();
                }
            }
        }
    }
}
