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

public class WarningsCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        Member responsible = event.getMember();
        if (ModerationUtils.shouldStop(responsible, event, "warnings")) return;

        Member target = event.getOption("member", null, OptionMapping::getAsMember);
        List<ModerationSaveData.Warning> warnings = ModerationSaveData.getWarnings(target);

        boolean isPublic = event.getOption("public", false, OptionMapping::getAsBoolean);
        if (warnings.isEmpty()) {
            event.reply("**" + target.getAsMention() + "** has no warnings").setEphemeral(!isPublic).queue();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTimestamp(Instant.now())
                .setTitle(target.getEffectiveName() + "'s warnings");

        StringBuilder description = new StringBuilder();
        int i = 0;
        for (ModerationSaveData.Warning warning : warnings) {
            i++;
            String line = i + ". <@" + warning.responsible() + "> - " + warning.reason() + " (" + "<t:" + warning.date() + ":f>)";
            if (i != warnings.size()) {
                line = line + "\n";
            }
            description.append(line);
        }
        builder.setDescription(description);

        event.reply("").addEmbeds(builder.build()).setEphemeral(!isPublic).queue();
    }
}
