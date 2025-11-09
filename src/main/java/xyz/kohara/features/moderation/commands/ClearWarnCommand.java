//package xyz.kohara.features.moderation.commands;
//
//import net.dv8tion.jda.api.Permission;
//import net.dv8tion.jda.api.entities.Member;
//import net.dv8tion.jda.api.entities.Message;
//import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
//import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import org.jetbrains.annotations.NotNull;
//
//public class ClearWarnCommand extends ListenerAdapter {
//
//    public ClearWarnCommand() {
//        this.name = "clearwarnings";
//        this.help = "clears previous warnings received by a user";
//        this.minArgsCount = 1;
//        this.usage = "<@member>";
//        this.userPermissions = new Permission[]{Permission.KICK_MEMBERS};
//        this.category = CommandCategory.MODERATION;
//    }
//
//    @Override
//    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
//        final Message message = ctx.getMessage();
//
//        if (message.getMentionedMembers().isEmpty()) {
//            ctx.reply("Please @mention the user you want to clear warnings for!");
//            return;
//        }
//
//        final Member target = message.getMentionedMembers().get(0);
//
//        if (!ModerationUtils.canInteract(ctx.getMember(), target, "clean warnings of", ctx.getChannel())) {
//            return;
//        }
//
//        DataSource.INS.deleteWarnings(target);
//        ctx.reply("All warnings for " + target.getUser().getAsTag() + " are cleared!");
//
//    }
//
//}
