//package xyz.kohara.features.moderation.commands;
//
//import net.dv8tion.jda.api.hooks.ListenerAdapter;
//
//public class BanCommand extends ListenerAdapter implements ModerationCommand {
//
////    @Override
////    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
////
////        if (houldStop(event.getMember())) return;
////
////        final Message message = ctx.getMessage();
////        List<Member> targetMembers = message.getMentionedMembers();
////
////        if (targetMembers.isEmpty()) {
////            ctx.reply("Please @mention the member(s) you want to ban!");
////            return;
////        }
////
////        // Split content at last member mention
////        String[] split = message.getContentRaw().split(targetMembers.get(targetMembers.size() - 1).getId() + ">");
////        final String reason = (split.length > 1)
////                ? String.join(" ", split[1].split("\\s+")).trim()
////                : "No reason provided";
////
////        targetMembers
////                .stream()
////                // Filter out members with which bot and command author can interact
////                .filter(target -> ModerationUtils.canInteract(ctx.getMember(), target, "ban", ctx.getChannel()))
////                .forEach(member -> ModerationUtils.ban(message, member, reason));
////
////    }
//
//}
