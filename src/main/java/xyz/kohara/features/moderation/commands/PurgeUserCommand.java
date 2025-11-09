//package xyz.kohara.features.moderation.commands;
//
//import bot.data.PurgeType;
//import net.dv8tion.jda.api.Permission;
//import net.dv8tion.jda.api.entities.Member;
//import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
//import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import org.jetbrains.annotations.NotNull;
//
//public class PurgeUserCommand extends ListenerAdapter {
//
//    public PurgeUserCommand() {
//        this.name = "purgeuser";
//        this.help = "deletes the specified amount of messages for the mentioned user";
//        this.usage = "<@user> <amount>";
//        this.minArgsCount = 2;
//        this.userPermissions = new Permission[]{Permission.MESSAGE_MANAGE, Permission.MESSAGE_HISTORY};
//        this.botPermissions = new Permission[]{Permission.MESSAGE_MANAGE, Permission.MESSAGE_HISTORY};
//        this.category = CommandCategory.MODERATION;
//    }
//
//    @Override
//    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
//        final int amount = ModerationUtils.checkPurgeAmount(ctx, 1);
//        if (amount == 0)
//            return;
//
//        if (ctx.getMessage().getMentionedMembers().isEmpty()) {
//            ctx.reply("No users mentioned");
//            return;
//        }
//
//        Member target = ctx.getMessage().getMentionedMembers().get(0);
//        ModerationUtils.purge(ctx, PurgeType.USER, amount, target.getId());
//
//    }
//}
