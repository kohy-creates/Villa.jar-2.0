package xyz.kohara.features.moderation.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import xyz.kohara.features.moderation.ModerationSaveData;
import xyz.kohara.features.moderation.ModerationUtils;

public class DelWarnCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        Member responsible = event.getMember();
        if (ModerationUtils.shouldStop(responsible, event, "delwarn")) return;

        Member member = event.getOption("member", null, OptionMapping::getAsMember);
        if (member.getUser().isBot()) {
            event.reply(":x: **Can't warn a bot, so can't delete its warning**").setEphemeral(true).queue();
            return;
        }

        int warning = event.getOption("warning", null, OptionMapping::getAsInt);

        ModerationSaveData.removeWarning(member, warning);

        event.reply("**:white_check_mark: Deleted warning " + warning + " of " + member.getAsMention() + "**").setEphemeral(true).queue();
    }

}
