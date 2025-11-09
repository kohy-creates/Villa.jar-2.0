//package xyz.kohara.features.moderation.commands;
//
//import bot.data.PurgeType;
//import net.dv8tion.jda.api.Permission;
//import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
//import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import org.jetbrains.annotations.NotNull;
//
//public class PurgeCommand extends ListenerAdapter {
//
//    public PurgeCommand() {
//        this.name = "purge";
//        this.help = "deletes the specified amount of messages";
//        this.usage = "<amount>";
//        this.minArgsCount = 1;
//        this.userPermissions = new Permission[]{Permission.MESSAGE_MANAGE, Permission.MESSAGE_HISTORY};
//        this.botPermissions = new Permission[]{Permission.MESSAGE_MANAGE, Permission.MESSAGE_HISTORY};
//        this.category = CommandCategory.MODERATION;
//    }
//
//    @Override
//    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
//        final int amount = ModerationUtils.checkPurgeAmount(ctx, 0);
//        if (amount != 0)
//            ModerationUtils.purge(ctx, PurgeType.ALL, amount, null);
//
//    }
//}
