package xyz.kohara.tags;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.kohara.VillaJar;

import java.io.IOException;
import java.util.Objects;

public class ReloadCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        System.out.println(event.getName());
        if (event.getName().equals("reload_tags")) {
            System.out.println(VillaJar.isStaff(Objects.requireNonNull(event.getMember())));
            if (VillaJar.isStaff(Objects.requireNonNull(event.getMember()))) {
                event.deferReply().queue();
                try {
                    Tags.createTagMap();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                event.getHook().sendMessage("Reloaded tags").queue();
            }
        }
    }
}
