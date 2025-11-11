package xyz.kohara.features.moderation.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;
import xyz.kohara.Aroki;
import xyz.kohara.features.moderation.ModerationSaveData;
import xyz.kohara.features.moderation.ModerationUtils;

import java.awt.*;
import java.util.List;

public class WarnCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        Member responsible = event.getMember();
        if (ModerationUtils.shouldStop(responsible, event, "warn")) return;

        Member member = event.getOption("member", null, OptionMapping::getAsMember);
        if (member.getUser().isBot()) {
            event.reply(":x: **Can't warn a bot**").setEphemeral(true).queue();
            return;
        }

        OptionMapping opt = event.getOption("reason");
        String reason = (opt == null) ? "*No reason provided*" : "*" + opt.getAsString() + "*";

        ModerationSaveData.saveWarning(member, reason, responsible);
        List<ModerationSaveData.Warning> warnings = ModerationSaveData.getWarnings(member);

        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setDescription(
                        "***<@" + member.getId() + "> was warned.*** | " + reason + "\n" +
                                "This is their " + Aroki.ordinal(warnings.size()) + " warning."
                );

        event.reply("").addEmbeds(builder.build()).queue();

        String text = "**You were warned in " + Aroki.getServer().getName() + "**\n> Reason: " + reason + " ~*" + responsible.getEffectiveName() + "*";
        Aroki.sendDM(member, text);
    }

}
