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
//public class SetNickCommand extends ListenerAdapter {
//
//    public SetNickCommand() {
//        this.name = "setnick";
//        this.help = "change the mentioned user's nickname on this guild";
//        this.usage = "<@member> <new-name>";
//        this.minArgsCount = 2;
//        this.userPermissions = new Permission[]{Permission.NICKNAME_MANAGE};
//        this.botPermissions = new Permission[]{Permission.NICKNAME_MANAGE};
//        this.category = CommandCategory.MODERATION;
//    }
//
//    @Override
//    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
//        final Message message = ctx.getMessage();
//        final List<String> args = ctx.getArgs();
//
//        if (message.getMentionedMembers().isEmpty()) {
//            ctx.reply("Please @mention the user you want to change the nickname!");
//            return;
//        }
//
//        final Member target = message.getMentionedMembers().get(0);
//
//        if (!ModerationUtils.canInteract(ctx.getMember(), target, "change nick of", ctx.getChannel())) {
//            return;
//        }
//
//        final String newName = String.join(" ", args.subList(1, args.size()));
//
//        ModerationUtils.setNick(message, target, newName);
//
//    }
//
//}
