package xyz.kohara.features.commands;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ServerCommand extends ListenerAdapter {

    public static Map<String, String> SERVER_LIST;

    static {
        String configFile = "data/servers.json";
        try (FileReader reader = new FileReader(configFile)) {
            Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            SERVER_LIST = new Gson().fromJson(reader, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("discord")) {
            String server = Objects.requireNonNull(event.getOption("server")).getAsString();
            boolean replyPublicly = event.getOption("public") != null && Objects.requireNonNull(event.getOption("public")).getAsBoolean();
            if (replyPublicly) {
                event.reply(server).queue();
            }
            else {
                event.reply(server).setEphemeral(true).queue();
            }
        }
    }
}
