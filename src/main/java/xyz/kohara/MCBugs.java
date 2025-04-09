package xyz.kohara;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MCBugs extends ListenerAdapter {

    private final List<String> ALLOWED_CATEGORIES = List.of(
            "MC", "MCPE", "REALMS"
    );

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        String content = event.getMessage().getContentRaw();
        Matcher matcher = Pattern.compile("\\[([A-Za-z]+-\\d+)]").matcher(content);
        if (matcher.find()) {
            String group = matcher.group(1).replace("[", "").replace("]", "");
            String[] extracted = group.split("-");
            if (ALLOWED_CATEGORIES.contains(extracted[0])) {
                extracted[1] = extracted[1].replaceAll("^0+", "");
                if (extracted[1].isEmpty()) return;
                group = extracted[0] + "-" + extracted[1];
                event.getChannel()
                        .sendMessage("")
                        .addActionRow(
                                Button.link("https://bugs.mojang.com/browse/" + extracted[0] + "/issues/" + group, group).withEmoji(Emoji.fromFormatted("<:mojira:1359506375842988185>"))
                        )
                        .setMessageReference(event.getMessage())
                        .mentionRepliedUser(false)
                        .queue();
            }
        }
    }
}
