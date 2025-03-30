package xyz.kohara;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.GatewayIntent;
import xyz.kohara.tags.MessageListener;
import xyz.kohara.tags.ReloadCommandListener;
import xyz.kohara.tags.Tags;

import java.util.Arrays;

public class VillaJar {

    private static final String token = System.getenv("token");
    private static final GatewayIntent[] intents = {GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS};

    public static JDA BOT;
    public static final String BOT_NAME = "Arfy";
    public static Guild BASEMENT;
    public static Role STAFF_ROLE;

    public static void main(String[] args) throws Exception {

        BOT = JDABuilder
                .createDefault(token)
                .enableIntents(Arrays.asList(intents))
                .setActivity(Activity.customStatus("owo"))
                .build();

        BOT.addEventListener(new MessageListener());
        BOT.addEventListener(new ReloadCommandListener());
        Tags.createTagMap();

        BOT.awaitReady();
        System.out.println("Bot " + BOT_NAME + " is online!");

        BASEMENT = BOT.getGuildById(System.getenv("server"));
        STAFF_ROLE = BOT.getRoleById(System.getenv("staff_role"));

        SlashCommands.register();
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
}
