package xyz.kohara.features.moderation;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;

public class ModerationSlashCommands {

    private static SlashCommandData slashCommand(String name, String description) {
        return Commands.slash(name, description);
    }

    public static final ArrayList<CommandData> MODERATION_COMMANDS = new ArrayList<>();
    static {
//        MODERATION_COMMANDS.add(
//                slashCommand("warn", "Warn a user. Self-explanatory")
//                        .addOption(OptionType.USER, "member", "Member to warn", true)
//                        .addOption(OptionType.STRING, "reason", "Warning reason")
//        );
    }
}
