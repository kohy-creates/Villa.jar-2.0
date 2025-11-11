package xyz.kohara.features.moderation.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import xyz.kohara.features.moderation.ModerationSaveData;
import xyz.kohara.features.moderation.ModerationUtils;

import java.awt.*;
import java.time.Instant;
import java.util.List;

public class ModerationHistoryCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        Member responsible = event.getMember();
        if (ModerationUtils.shouldStop(responsible, event, "modhistory")) return;

        Member target = event.getOption("member", null, OptionMapping::getAsMember);
        List<ModerationSaveData.ModerationAction> history = ModerationSaveData.getHistory(target);

        boolean isPublic = event.getOption("public", false, OptionMapping::getAsBoolean);
        if (history.isEmpty()) {
            event.reply("**" + target.getAsMention() + "** has no moderation history").setEphemeral(!isPublic).queue();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTimestamp(Instant.now())
                .setTitle(target.getEffectiveName() + "'s moderation history");

        StringBuilder description = new StringBuilder();
        int i = 0;
        for (ModerationSaveData.ModerationAction action : history) {
            i++;
            String line = i + ". <@" + action.responsible() + "> - " + action.actionType() + " - " + action.reason() + " (" + "<t:" + action.date() + ":f>)";
            if (i != history.size()) {
                line = line + "\n";
            }
            description.append(line);
        }
        builder.setDescription(description);

        event.reply("").addEmbeds(builder.build()).setEphemeral(!isPublic).queue();
    }
}
