package xyz.kohara;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.GatewayIntent;
import xyz.kohara.autoreact.AutoReact;
import xyz.kohara.commands.AvatarCommand;
import xyz.kohara.commands.ServerCommand;
import xyz.kohara.commands.TagListCommand;
import xyz.kohara.support.ForumManager;
import xyz.kohara.tags.MessageListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class VillaJar {

    private static final String token = Config.getOption("token");
    private static final GatewayIntent[] intents = {GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS};

    public static JDA BOT;
    public static final String BOT_NAME = Config.getOption("bot_name");
    public static Guild BASEMENT;
    public static Role STAFF_ROLE;

    public static void main(String[] args) throws Exception {

        BOT = JDABuilder
                .createDefault(token)
                .enableIntents(Arrays.asList(intents))
                .setActivity(Activity.customStatus("owo"))
                .build();

        BOT.awaitReady();
        System.out.println("Bot " + BOT_NAME + " is online!");

        BASEMENT = BOT.getGuildById(Config.getOption("server_id"));
        STAFF_ROLE = BOT.getRoleById(Config.getOption("staff_role_id"));

        BOT.addEventListener(new MessageListener());
        BOT.addEventListener(new ServerCommand());
        BOT.addEventListener(new TagListCommand());
        BOT.addEventListener(new AvatarCommand());
        BOT.addEventListener(new LogUploader());
        BOT.addEventListener(new AutoReact());
        BOT.addEventListener(new ForumManager());

        VillaJar.BASEMENT.updateCommands().addCommands(SlashCommands.COMMANDS).queue();
        ForumManager.scheduleReminderCheck();
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
}
