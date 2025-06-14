package xyz.kohara.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import xyz.kohara.features.tags.Tags;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class TagListCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("tags")) {
            EmbedBuilder embed = new EmbedBuilder();

            embed.setTitle("Tags");

            Map<String, ArrayList<String>> categories = Tags.CATEGORIES;

            for (String category : categories.keySet()) {
                if (category.equals("none")) continue;
                ArrayList<String> entries = categories.get(category);
                String tags = "`" + StringUtils.join(entries, "`, `") + "`";

                embed.addField(WordUtils.capitalizeFully(category), tags, true);
            }
            embed.setDescription("`" + StringUtils.join(categories.get("none"), "`, `") + "`");
            boolean replyPublicly = event.getOption("public") != null && Objects.requireNonNull(event.getOption("public")).getAsBoolean();
            if (replyPublicly) {
                event.replyEmbeds(embed.build()).queue();
            }
            else {
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
            }
        }
    }
}
