package xyz.kohara;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.GatewayIntent;
import xyz.kohara.features.*;
import xyz.kohara.features.autoreact.AutoReact;
import xyz.kohara.commands.AvatarCommand;
import xyz.kohara.commands.ServerCommand;
import xyz.kohara.commands.TagListCommand;
import xyz.kohara.features.music.MusicPlayer;
import xyz.kohara.status.BotActivity;
import xyz.kohara.features.support.ForumManager;
import xyz.kohara.features.tags.MessageListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Aroki {

    private static final String token = Config.getOption("token");
    private static final GatewayIntent[] intents = {GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS};

    public static JDA BOT;
    public static final String BOT_NAME = Config.getOption("bot_name");
    public static Guild BASEMENT;
    public static Role STAFF_ROLE, DEV_ROLE;

    public static void main(String[] args) throws Exception {

        BOT = JDABuilder
                .createDefault(token)
                .enableIntents(Arrays.asList(intents))
                .build();

        BOT.awaitReady();

        BASEMENT = BOT.getGuildById(Config.getOption("server_id"));
        STAFF_ROLE = BOT.getRoleById(Config.getOption("staff_role_id"));
        DEV_ROLE = BOT.getRoleById(Config.getOption("dev_role_id"));

        List<Object> listeners = List.of(
                new MessageListener(),
                new ServerCommand(),
                new TagListCommand(),
                new AvatarCommand(),
                new LogUploader(),
                new AutoReact(),
                new ForumManager(),
                new MusicPlayer(),
                new MCBugs(),
                new AutoRole(),
                new Quote()
        );
        listeners.forEach(BOT::addEventListener);

        Aroki.BASEMENT.updateCommands().addCommands(SlashCommands.COMMANDS).queue();
        ForumManager.scheduleReminderCheck();
        BotActivity.schedule();

        log(Aroki.class.getName(), "Bot " + BOT_NAME + " has successfully finished startup", Level.INFO);
    }

    public static Guild getServer() {
        return BASEMENT;
    }
    public static JDA getBot() {
        return BOT;
    }

    public static boolean isStaff(Member member) {
        return member.getRoles().contains(STAFF_ROLE);
    }
    public static boolean isDev(Member member) {
        return member.getRoles().contains(DEV_ROLE);
    }

    public static String toSmallUnicode(String s) {
        Map<Character, Character> map = new HashMap<>();
        String[] mappings = {"aᴀ", "bʙ", "cᴄ", "dᴅ", "eᴇ", "fꜰ", "gɢ", "hʜ", "iɪ", "jᴊ", "kᴋ", "lʟ", "mᴍ", "nɴ", "oᴏ", "pᴘ", "rʀ", "sѕ", "tᴛ", "uᴜ", "wᴡ", "xх", "yʏ", "zᴢ"};
        for (String pair : mappings) {
            map.put(pair.charAt(0), pair.charAt(1));
        }
        StringBuilder result = new StringBuilder();
        for (char c : s.toCharArray()) {
            result.append(map.getOrDefault(c, c));
        }
        return result.toString();
    }

    public static void log(String CLASS, String text, Level level) {
        Logger.getLogger(CLASS).log(level, text);
    }
}
