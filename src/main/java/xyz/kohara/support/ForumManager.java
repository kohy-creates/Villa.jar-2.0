package xyz.kohara.support;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.entities.channel.forums.ForumTagSnowflake;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.managers.channel.concrete.ThreadChannelManager;
import xyz.kohara.Config;
import xyz.kohara.Aroki;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class ForumManager extends ListenerAdapter {

    private static final ForumChannel SUPPORT_CHANNEL = Aroki.getServer().getForumChannelById(Config.getOption("support_channel"));

    private static class Tag {
        public static final Long OPEN;
        public static final Long RESOLVED;
        public static final Long INVALID;
        public static final Long TO_DO;


        static {
            OPEN = parseConfig("open_tag_id");
            RESOLVED = parseConfig("resolved_tag_id");
            INVALID = parseConfig("invalid_tag_id");
            TO_DO = parseConfig("to_do_tag_id");

        }

        private static Long parseConfig(String key) {
            String value = Config.getOption(key);
            if (value == null) {
                throw new RuntimeException("Config key '" + key + "' is missing or null!");
            }
            return Long.parseLong(value);
        }
    }

    private static String CLOSE_COMMAND;

    static {
        Aroki.getServer().retrieveCommands().queue(
                commands -> {
                    for (Command command : commands) {
                        if (command.getName().equals("close")) {
                            CLOSE_COMMAND = command.getAsMention();
                            break;
                        }
                    }
                }
        );
    }

    private static boolean hasThreadManagementPerms(Member member) {
        return (Aroki.isDev(member) || Aroki.isStaff(member));
    }

    private static String REMINDER_MESSAGE(String member, boolean addReminder) throws IOException {
        String text = Files.readString(Paths.get("data/forum/reminder_message.md")).trim();
        text = text.replace("{MEMBER}", "<@" + member + ">");
        text = text.replace("{CLOSE}", CLOSE_COMMAND);

        int splitIndex = text.lastIndexOf(">");
        String[] parts = new String[]{text.substring(0, splitIndex - 1), text.substring(splitIndex - 1)};
        return (addReminder) ? parts[0] + "\n" + parts[1] : parts[0];

    }

    private static String INVALID_MESSAGE(boolean DM) throws IOException {
        return Files.readString(Paths.get((DM) ? "data/forum/invalid_dm.md" : "data/forum/invalid.md")).trim();
    }

    private static EmbedBuilder supportEmbed(Member member) throws IOException {
        EmbedBuilder embed = new EmbedBuilder();
        String iconUrl;
        iconUrl = (Aroki.getServer().getIcon() != null) ? Aroki.getServer().getIcon().getUrl() : null;
        embed.setAuthor(Aroki.getServer().getName(), null, iconUrl);

        String content = Files.readString(Paths.get("data/forum/embed.md"));
        content = content.replace("{CLOSE}", CLOSE_COMMAND);
        content = content.replace("{MEMBER}", member.getAsMention());

        int firstSectionIndex = content.indexOf("###");
        String description = content.substring(0, firstSectionIndex).trim();
        embed.setDescription(description);

        Pattern sectionPattern = Pattern.compile("(?=^### )", Pattern.MULTILINE);
        String[] sections = sectionPattern.split(content.substring(firstSectionIndex));
        for (String section : sections) {
            String[] lines = section.split("\n", 2);
            String title = lines[0].replace("### ", "").trim();
            String body = lines.length > 1 ? lines[1].trim() : "";
            embed.addField(title, body, false);
        }
        return embed;
    }

    private static void closePost(ThreadChannel thread, String reason) throws IOException {
        List<Long> currentTagIds = new ArrayList<>(thread.getAppliedTags()
                .stream()
                .map(ForumTag::getIdLong)
                .toList());
        switch (reason) {
            case "invalid" -> currentTagIds.add(Tag.INVALID);
            case "resolved" -> currentTagIds.add(Tag.RESOLVED);
        }
        currentTagIds.remove(Tag.OPEN);
        currentTagIds.remove(Tag.TO_DO);

        ArrayList<ForumTagSnowflake> snowflakes = new ArrayList<>();
        for (Long id : currentTagIds) {
            snowflakes.add(ForumTagSnowflake.fromId(id));
        }

        thread.getManager()
                .setAppliedTags(snowflakes)
                .setLocked(true)
                .setArchived(true)
                .queue();

        ForumData.removeEntry(thread.getId());
    }

    private boolean isSupportThread(ThreadChannel thread) {
        assert SUPPORT_CHANNEL != null;
        List<ThreadChannel> supportThreads = SUPPORT_CHANNEL.getThreadChannels();
        return supportThreads.contains(thread);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.GUILD_PUBLIC_THREAD) return;
        ThreadChannel channel = event.getChannel().asThreadChannel();
        if (event.getAuthor().isBot() || !isSupportThread(channel)) return;
        try {
            if (channel.isLocked()) {
                if (channel.isPinned()) return;
                Thread.sleep(500);
                channel.getManager().setArchived(true).queue();
            } else {
                String id = channel.getId();
                String content = event.getMessage().getContentRaw();
                if (content.equals("✅") || content.equals("🎗️")) {
                    String op = ForumData.getEntryValue(id, "op");
                    switch (content) {
                        case "✅" -> {
                            if (op.equals(Objects.requireNonNull(event.getMember()).getId()) || hasThreadManagementPerms(event.getMember())) {
                                event.getMessage().addReaction(Emoji.fromFormatted("✅")).queue();
                                event.getChannel().sendMessage("✅").queue();
                                closePost(channel, "resolved");
                            }
                        }
                        case "🎗️" -> {
                            long currentTime = System.currentTimeMillis();
                            long lastReminded = Long.parseLong(ForumData.getEntryValue(id, "last_reminded"));
                            if (currentTime - lastReminded >= 30 * 1000) {
                                event.getMessage().addReaction(Emoji.fromFormatted("🎗️")).queue();
                                ForumData.setEntryValue(id, "last_reminded", currentTime);
                                channel.sendMessage(REMINDER_MESSAGE(op, false)).queue();
                            }
                        }
                    }
                } else if (!ForumData.entryExists(id)) {
                    ForumData.addEntry(id, Objects.requireNonNull(event.getMember()).getId(), System.currentTimeMillis());
                    channel.sendMessage("").addEmbeds(supportEmbed(event.getMember()).build()).queue(
                            message -> message.pin().queue(
                                    success -> channel.getHistory().retrievePast(1).queue(messages -> {
                                        Message lastMessage = messages.getFirst();
                                        if (lastMessage.getType() == MessageType.CHANNEL_PINNED_ADD) {
                                            lastMessage.delete().queue();
                                        }
                                    })
                            )
                    );
                    List<Long> currentTagIds = new ArrayList<>(channel.getAppliedTags()
                            .stream()
                            .map(ForumTag::getIdLong)
                            .toList());
                    currentTagIds.add(Tag.OPEN);
                    ArrayList<ForumTagSnowflake> snowflakes = new ArrayList<>();
                    for (Long tagid : currentTagIds) {
                        snowflakes.add(ForumTagSnowflake.fromId(tagid));
                    }

                    ThreadChannelManager manager = channel.getManager();
                    manager.setAppliedTags(snowflakes).queue();
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onChannelDelete(ChannelDeleteEvent event) {
        String channelId = event.getChannel().asThreadChannel().getId();
        try {
            if (ForumData.entryExists(channelId)) {
                ForumData.removeEntry(channelId);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        if (event.getUser().isBot()) return;
        try {
            assert event.getMember() != null;
            List<String> threadList = ForumData.findThreads(event.getMember());
            for (String id : threadList) {
                ThreadChannel thread = Aroki.getServer().getChannelById(ThreadChannel.class, id);
                assert thread != null;
                closePost(thread, "invalid");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("close")) {
            if (event.getChannel().getType() != ChannelType.GUILD_PUBLIC_THREAD && !isSupportThread(event.getChannel().asThreadChannel())) {
                event.reply(":x: **This command can only be used in <#" + Config.getOption("support_channel_id") + "> threads!**").setEphemeral(true).queue();
                return;
            }
            try {
                ThreadChannel thread = event.getChannel().asThreadChannel();
                String id = thread.getId();
                OptionMapping option = event.getOption("action");
                String action = option != null ? option.getAsString() : "resolve";
                String op = ForumData.getEntryValue(id, "op");
                boolean isOP = op.equals(Objects.requireNonNull(event.getMember()).getId());
                boolean staff = hasThreadManagementPerms(event.getMember());
                if ((action.equals("resolve") && isOP) || ((action.equals("resolve") && staff))) {
                    event.reply(":white_check_mark:").queue();
                    closePost(thread, "resolved");
                } else if (action.equals("invalid") && staff) {
                    Aroki.getBot().retrieveUserById(op).queue(
                            user -> user.openPrivateChannel().queue(
                                    privateChannel -> {
                                        try {
                                            String text = INVALID_MESSAGE(true);
                                            text = text.replace("{THREAD}", thread.getAsMention());
                                            privateChannel
                                                    .sendMessage(text)
                                                    .setActionRow(
                                                            Button.of(
                                                                            ButtonStyle.PRIMARY,
                                                                            "sent_from",
                                                                            "Sent from " + Aroki.getServer().getName(), Emoji.fromFormatted("<:paper_plane:1358007565614710785>")
                                                                    )
                                                                    .asDisabled()
                                                    )
                                                    .queue();
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                            )
                    );
                    event.reply(INVALID_MESSAGE(false)).queue();
                    closePost(thread, "invalid");
                } else {
                    event.reply(":x: **This is not your support thread or you don't have permission to do that**").setEphemeral(true).queue();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void scheduleReminderCheck() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                List<String> threads;
                try {
                    threads = ForumData.getAllThreads();
                    for (String entry : threads) {
                        long lastReminded;
                        ThreadChannel thread = Objects.requireNonNull(Aroki.getServer().getThreadChannelById(entry));
                        List<Long> tags = new ArrayList<>(thread.getAppliedTags()
                                .stream()
                                .map(ForumTag::getIdLong)
                                .toList());
                        if (tags.contains(Tag.TO_DO) || thread.isPinned()) continue;
                        lastReminded = Long.parseLong(ForumData.getEntryValue(entry, "last_reminded"));
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastReminded >= 72 * 60 * 60 * 1000) {
                            ForumData.setEntryValue(entry, "last_reminded", currentTime);
                            String op = ForumData.getEntryValue(entry, "op");
                            thread.sendMessage(REMINDER_MESSAGE(op, true)).queue();
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }, 100, 15 * 60 * 1000);
    }
}
