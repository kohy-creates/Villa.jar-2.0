package xyz.kohara.music;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;

public class MusicSlashCommands {

    public static final ArrayList<CommandData> MUSIC_COMMANDS = new ArrayList<>();
    static {
        MUSIC_COMMANDS.add(
                Commands.slash("play", "Play a song from a YouTube link or search")
                        .addOption(OptionType.STRING, "query", "YouTube link or search keywords", true)
        );

        MUSIC_COMMANDS.add(
                Commands.slash("skip", "Skip the current song")
                        .addOption(OptionType.INTEGER, "amount", "How many songs to skip", false)
        );

        MUSIC_COMMANDS.add(
                Commands.slash("stop", "Stop the music and clear the queue")
        );

        MUSIC_COMMANDS.add(
                Commands.slash("pause", "Pauses or unpauses the current song")
        );

        MUSIC_COMMANDS.add(
                Commands.slash("nowplaying", "Shows info about the currently playing track")
        );

        MUSIC_COMMANDS.add(
                Commands.slash("join", "Makes the bot join your voice channel")
        );

        MUSIC_COMMANDS.add(
                Commands.slash("queue", "Displays the current queue")
        );

        MUSIC_COMMANDS.add(
                Commands.slash("shuffle", "Shuffles the tracks in the queue")
        );

        MUSIC_COMMANDS.add(
                Commands.slash("loop", "Loops the current track or the entire queue")
                        .addOption(OptionType.STRING, "type", "'queue' or 'track'", false)
        );

        MUSIC_COMMANDS.add(
                Commands.slash("volume", "Sets the playback volume")
                        .addOption(OptionType.INTEGER, "volume", "0 - 200", true)
        );

        MUSIC_COMMANDS.add(
                Commands.slash("replay", "Restarts the current song from the beginning")
        );

        MUSIC_COMMANDS.add(
                Commands.slash("history", "Shows recently played songs")
        );
    }
}