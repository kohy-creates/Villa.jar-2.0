package xyz.kohara.features;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.kohara.Aroki;
import xyz.kohara.Config;

public class AutoRole extends ListenerAdapter {

    private static final Role MORTALS_ROLE = Aroki.getServer().getRoleById(Config.getOption("mortals_role"));
    private static final Role BOT_ROLE = Aroki.getServer().getRoleById(Config.getOption("mortals_role"));

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Guild guild = event.getGuild();
        Role role = MORTALS_ROLE;
        if (event.getUser().isBot()) {
            role = BOT_ROLE;
        }
        guild.addRoleToMember(event.getMember(), role).queue();
    }

}
