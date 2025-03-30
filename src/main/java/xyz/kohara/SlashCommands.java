package xyz.kohara;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlashCommands {

    private static final ArrayList<CommandData> SLASH_COMMANDS = new ArrayList<>();

    static {
        SLASH_COMMANDS.add(Commands.slash("reload_tags", "Reloads tags"));
        SLASH_COMMANDS.add(Commands.slash("ping", "Pong!"));
    }

    public static void register() {
        VillaJar.getServer().updateCommands().addCommands(SLASH_COMMANDS).queue();
    }
}
