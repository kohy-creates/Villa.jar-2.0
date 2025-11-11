package xyz.kohara;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.reflections.Reflections;
import xyz.kohara.features.commands.SlashCommands;
import xyz.kohara.features.support.ForumManager;
import xyz.kohara.status.BotActivity;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Aroki {

    private static final String token = Config.getOption("token");
    private static final GatewayIntent[] intents = {GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS};

    private static JDA BOT;
    private static final String BOT_NAME = Config.getOption("bot_name");
    private static Guild BASEMENT;
    private static Role STAFF_ROLE, DEV_ROLE;

    public static void main(String[] args) throws Exception {

        BOT = JDABuilder
                .createDefault(token)
                .enableIntents(Arrays.asList(intents))
                .build();

        BOT.awaitReady();

        BASEMENT = BOT.getGuildById(Config.getOption("server_id"));
        STAFF_ROLE = BOT.getRoleById(Config.getOption("staff_role_id"));
        DEV_ROLE = BOT.getRoleById(Config.getOption("dev_role_id"));

        // Add all listeners dynamically through a reflection
        getAllListeners().forEach(listenerAdapter -> {
            BOT.addEventListener(listenerAdapter);
            log("Added listener " + listenerAdapter.toString().split("@")[0] + " to bot " + BOT_NAME);
        });

        Aroki.BASEMENT.updateCommands().addCommands(SlashCommands.COMMANDS).queue();
        ForumManager.scheduleReminderCheck();
        BotActivity.schedule();

        log(BOT_NAME + " has successfully finished startup", Level.INFO);
    }

    private static List<ListenerAdapter> getAllListeners() {
        List<ListenerAdapter> listeners = new ArrayList<>();

        Reflections reflections = new Reflections("xyz.kohara.features");
        Set<Class<? extends ListenerAdapter>> classes = reflections.getSubTypesOf(ListenerAdapter.class);

        for (Class<? extends ListenerAdapter> clazz : classes) {
            try {
                listeners.add(clazz.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                log("Failed to load listener: " + clazz.getName(), Level.SEVERE);
                throw new RuntimeException(e);
            }
        }

        return listeners;
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

    public static void sendDM(User member, String text) {
        member.openPrivateChannel().queue(privateChannel -> {
            privateChannel.sendMessage(text)
                    .setActionRow(
                            Button.of(
                                    ButtonStyle.PRIMARY,
                                    "sent_from", "Sent from " + Aroki.getServer().getName(), Emoji.fromFormatted("<:paper_plane:1358007565614710785>")
                            ).asDisabled()
                    )
                    .queue();
        });
    }

    public static void sendDM(Member member, String text) {
        sendDM(member.getUser(), text);
    }

    public static String ordinal(int i) {
        String[] suffixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        return switch (i % 100) {
            case 11, 12, 13 -> i + "th";
            default -> i + suffixes[i % 10];
        };
    }

    public static String smallUnicode(String s) {
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

    public static void log(String text) {
        log(text, Level.INFO);
    }

    public static void log(String text, Level level) {
        Logger.getLogger(
                StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass().getName()
        ).log(level, text);
    }
}
