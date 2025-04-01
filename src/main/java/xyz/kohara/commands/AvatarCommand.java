package xyz.kohara.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;
import java.util.Objects;

public class AvatarCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("avatar")) {
            User user = Objects.requireNonNull(event.getOption("user")).getAsUser();

            EmbedBuilder embed = new EmbedBuilder();

            embed.setTitle("Avatar of @" + user.getAsTag().replace("#0000", ""));

            String PNG = user.getEffectiveAvatarUrl() + "?size=1024";
            String JPG = user.getEffectiveAvatarUrl().replace(".png",".jpg") + "?size=1024";
            String WEBP = user.getEffectiveAvatarUrl().replace(".png",".webp") + "?size=1024";

            embed.setDescription("**Link as:** [`.png`](" + PNG + ") **|** [`.jpg`](" + JPG + ") **|** [`.webp`](" + WEBP + ")");
            embed.setImage(PNG);
            embed.setTimestamp(Instant.now());
            embed.setColor(Color.ORANGE);

            boolean replyPublicly = event.getOption("public") != null && Objects.requireNonNull(event.getOption("public")).getAsBoolean();
            if (replyPublicly) {
                embed.setFooter("Requested by @" + event.getUser().getAsTag().replace("#0000", ""), event.getUser().getEffectiveAvatarUrl());
                event.replyEmbeds(embed.build()).queue();
            }
            else {
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
            }
        }
    }
}
