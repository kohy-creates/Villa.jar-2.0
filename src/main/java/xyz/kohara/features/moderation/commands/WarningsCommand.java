//package xyz.kohara.features.moderation.commands;
//
//import bot.database.objects.WarnLogs;
//import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
//import com.jagrosh.jdautilities.menu.Paginator;
//import net.dv8tion.jda.api.Permission;
//import net.dv8tion.jda.api.entities.Member;
//import net.dv8tion.jda.api.entities.Message;
//import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
//import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import org.jetbrains.annotations.NotNull;
//
//import java.awt.*;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//public class WarningsCommand extends ListenerAdapter {
//
//    @Override
//    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
//        final Message message = ctx.getMessage();
//
//        if (message.getMentionedMembers().isEmpty()) {
//            ctx.reply("Please @mention the user you want to warn!");
//            return;
//        }
//
//        final Member target = message.getMentionedMembers().get(0);
//
//        List<WarnLogs> warnLogs = DataSource.INS.getWarnLogs(target);
//
//        int page = 1;
//        pBuilder.clearItems();
//
//        if (!warnLogs.isEmpty()) {
//            warnLogs.forEach((m) -> pBuilder.addItems("**ModName:** `" + m.modName + "`\n"
//                    + "**Reason:** `" + (m.modReason == null || m.modReason.equals("") ? "Not Specified" : m.modReason) + "`\n"
//                    + "**Timestamp:** `" + m.timeStamp + "`\n"));
//
//            Paginator p = pBuilder.addUsers(ctx.getAuthor()).setText("Warnings received by `" + target.getUser().getAsTag() + "`")
//                    .setColor(new Color(54, 57, 63)).build();
//
//            p.paginate(ctx.getChannel(), page);
//
//        } else
//            ctx.reply("No warnings for `" + target.getUser().getAsTag() + "`");
//
//    }
//
//}
