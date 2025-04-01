package xyz.kohara;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import xyz.kohara.commands.ServerCommandListener;

import java.util.ArrayList;
import java.util.Map;

public class SlashCommands {

    public static final ArrayList<CommandData> COMMANDS = new ArrayList<>();

    static {
        COMMANDS.add(Commands.slash("ping", "Pong!"));

        OptionData serverOption = new OptionData(OptionType.STRING, "server", "Discord server", true);
        for (Map.Entry<String, String> entry : ServerCommandListener.SERVER_LIST.entrySet()) {
            serverOption.addChoice(entry.getKey(), entry.getValue());
        }

        COMMANDS.add(Commands.slash("discord", "Get links for other Discord servers")
                .addOptions(serverOption,
                        new OptionData(OptionType.BOOLEAN, "public", "Send the answer publicly?", false)));

        COMMANDS.add(Commands.slash("tags","List all registered tags")
                .addOption(OptionType.BOOLEAN, "public", "Send the answer publicly?", false)
        );
    }
}
