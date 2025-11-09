//package xyz.kohara.features.moderation.commands;
//
//import net.dv8tion.jda.api.Permission;
//import net.dv8tion.jda.api.entities.Member;
//import net.dv8tion.jda.api.entities.Message;
//import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
//import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.List;
//
//public class KickCommand extends ListenerAdapter {
//
//    public KickCommand() {
//        this.name = "kick";
//        this.help = "Kick a member off the server";
//        this.usage = "<@member(s)> [reason]";
//        this.minArgsCount = 1;
//        this.userPermissions = new Permission[]{Permission.KICK_MEMBERS};
//        this.botPermissions = new Permission[]{Permission.KICK_MEMBERS};
//        this.category = CommandCategory.MODERATION;
//    }
//
//    @Override
//    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
//        final Message message = ctx.getMessage();
//        List<Member> targetMembers = message.getMentionedMembers();
//
//        if (targetMembers.isEmpty()) {
//            ctx.reply("Please @mention the member(s) you want to kick!");
//            return;
//        }
//
//        // Split content at last member mention
//        String[] split = message.getContentRaw().split(targetMembers.get(targetMembers.size() - 1).getId() + ">");
//        final String reason = (split.length > 1)
//                ? String.join(" ", split[1].split("\\s+")).trim()
//                : "No reason provided";
//
//        targetMembers
//                .stream()
//                // Filter out members with which bot and command author can interact
//                .filter(target -> ModerationUtils.canInteract(ctx.getMember(), target, "kick", ctx.getChannel()))
//                .forEach(member -> ModerationUtils.kick(message, member, reason));
//
//    }
//
//}
