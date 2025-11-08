package xyz.kohara.features.commands;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import xyz.kohara.features.commands.slash.ServerCommand;
import xyz.kohara.features.music.MusicSlashCommands;

import java.util.ArrayList;
import java.util.Map;

public class SlashCommands {

    public static final ArrayList<CommandData> COMMANDS = new ArrayList<>();
    private static final String SEND_PUBLICLY_TIP = "Share the output with everyone?";

    static {
        COMMANDS.add(Commands.slash("ping", "Pong!"));

        OptionData serverOption = new OptionData(OptionType.STRING, "server", "Discord server", true);
        for (Map.Entry<String, String> entry : ServerCommand.SERVER_LIST.entrySet()) {
            serverOption.addChoice(entry.getKey(), entry.getValue());
        }

        COMMANDS.add(Commands.slash("discord", "Get links for other Discord servers")
                .addOptions(serverOption,
                        new OptionData(OptionType.BOOLEAN, "public", SEND_PUBLICLY_TIP, false)));

        COMMANDS.add(Commands.slash("tags", "List all registered tags")
                .addOption(OptionType.BOOLEAN, "public", SEND_PUBLICLY_TIP, false)
        );

        COMMANDS.add(Commands.slash("avatar", "Returns the avatar (profile picture) of the chosen user")
                .addOption(OptionType.USER, "user", "User", true)
                .addOption(OptionType.BOOLEAN, "public", SEND_PUBLICLY_TIP, false)
        );

        COMMANDS.add(Commands.slash("close", "Closes the support thread")
                .addOptions(
                        new OptionData(OptionType.STRING, "action", "Resolution type", false)
                                .addChoice("Resolve", "resolve")
                                .addChoice("Invalidate", "invalid")
                                .addChoice("Duplicate", "duplicate"),
                        new OptionData(OptionType.CHANNEL, "duplicate_of", "What thread does this duplicate? Only applies if resolution type is 'Duplicate'", false)
                )
        );

        COMMANDS.addAll(MusicSlashCommands.MUSIC_COMMANDS);
    }
}
