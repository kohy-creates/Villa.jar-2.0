// By SkyKing_PX
// Thankss uωu <3
//
// Edited by kohy
package xyz.kohara.features;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class LogUploader extends ListenerAdapter {

    private static final List<String> DISCONTINUED_VERSIONS = List.of(
            "1.20.*"
    );

    private static final File LOGS_FOLDER;

    static {
        LOGS_FOLDER = new File("logs_temp");
        if (!LOGS_FOLDER.exists()) {
            LOGS_FOLDER.mkdir();
        }
        for (File file : Objects.requireNonNull(LOGS_FOLDER.listFiles())) {
            file.delete();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        Map<File, String> fileMap = new HashMap<>();
        AtomicInteger iter = new AtomicInteger();
        event.getMessage().getAttachments().stream()
                .filter(attachment -> attachment.getFileName().matches(".*\\.(log|txt|gz)$"))
                .forEach(attachment -> {
                    String extension = attachment.getFileExtension();
                    assert extension != null;
                    extension = (extension.equals("gz")) ? "log" : extension;
                    File tempFile = new File(LOGS_FOLDER, "temp-" + Math.random() * 1_000_001 + "." + extension);
                    fileMap.put(tempFile, attachment.getFileName());
                    attachment.getProxy().download().thenAccept(inputStream -> {
                        event.getChannel().sendTyping().queue();
                        try {
                            if (Objects.equals(attachment.getFileExtension(), "gz")) {
                                saveInputStreamToFile(inputStream, new File(tempFile + ".gz"));
                                decompressGzipFile(tempFile + ".gz", tempFile.toString());
                            } else {
                                saveInputStreamToFile(inputStream, tempFile);
                            }
                            iter.addAndGet(1);
                            if (iter.get() == event.getMessage().getAttachments().size()) {
                                CompletableFuture.runAsync(() -> {
                                    uploadAndSendLinks(fileMap, event);
                                });
                            }

                        } catch (IOException e) {
                            event.getChannel().sendMessage("❌ Error saving file `" + attachment.getFileName() + "`").queue();
                            tempFile.delete();
                            e.printStackTrace();
                        }
                    });
                });
    }

    private void saveInputStreamToFile(InputStream inputStream, File file) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    private void uploadAndSendLinks(Map<File, String> fileMap, MessageReceivedEvent event) {
        Map<String, List<String>> uploads = new HashMap<>();
        for (Map.Entry<File, String> entry : fileMap.entrySet()) {
            File tempFile = entry.getKey();
            String originalName = entry.getValue();
            try {
                List<String> url = uploadToMclogs(tempFile);
                uploads.put(originalName, url);
                tempFile.delete();
            } catch (IOException e) {
                event.getChannel().sendMessage("❌ Error uploading file `" + originalName + "`").queue();
                e.printStackTrace();
            }
        }

        if (!uploads.isEmpty()) {

            MessageCreateBuilder builder = new MessageCreateBuilder();
            builder.setContent("");
            // 'key' is the original file name
            for (String key : uploads.keySet()) {
                ArrayList<Button> buttons = new ArrayList<>();
                List<String> data = uploads.get(key);
                String url = data.getFirst();
                buttons.add(Button.link(url, key).withEmoji(Emoji.fromFormatted("<:mclogs:1359506468344299530>")));
                /*
                    If it isn't a crash report or a log, size of the list will be 2 (look at 'uploadToMcLogs')
                    We don't add the 2nd button with quick info if it's a random ass txt file.
                */
                if (data.size() > 2) {
                    String name = data.get(1), type = data.get(2), version = data.get(3);
                    // This could also be inlined but I left it like this so that it's at least slightly easier to work with
                    String label = name + " " + type + " (" + version + ")";
                    if (isDiscontinued(version)) {
                        buttons.add(
                                Button.danger(key, label)
                                        .withEmoji(Emoji.fromFormatted("📜"))
                                        .asDisabled()
                        );
                    } else {
                        buttons.add(
                                Button.primary(key, label)
                                        .withEmoji(Emoji.fromFormatted("📜"))
                                        .asDisabled()
                        );
                    }
                }
                builder.addActionRow(buttons);
            }
            event.getChannel().sendMessage(builder.build()).queue();
        }
    }

    private List<String> uploadToMclogs(File file) throws IOException {
        String logContent = Files.readString(file.toPath()).trim().replace("§", "");
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("https://api.mclo.gs/1/log");
            post.setEntity(MultipartEntityBuilder.create().addTextBody("content", logContent, ContentType.APPLICATION_FORM_URLENCODED).build());
            try (CloseableHttpResponse response = client.execute(post)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String result = new String(entity.getContent().readAllBytes());
                    JsonNode jsonNode = new ObjectMapper().readTree(result);
                    String url = jsonNode.has("url") ? jsonNode.get("url").asText() : "Error uploading file";
                    String id = jsonNode.has("id") ? jsonNode.get("id").asText() : "Error uploading file";
                    List<String> resultList = new ArrayList<>();
                    resultList.add(url);
                    resultList.addAll(getLogTitle(id));
                    /*
                        The result is either a 2 or 4 element list
                        4 if the upload is a log/crash report
                        2 if anything else

                         0 = url
                         1 - 3 = name, type, version

                         If it isn't a log/crash report, the 2nd element (id 1) will be null
                    */
                    return resultList;
                }
            }
        }
        throw new RuntimeException();
    }

    private List<String> getLogTitle(String string) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet("https://api.mclo.gs/1/insights/" + string);
            try (CloseableHttpResponse response = client.execute(get)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String result = new String(entity.getContent().readAllBytes());
                    JsonNode jsonNode = new ObjectMapper().readTree(result);
                    String name, type, version;
                    String UNKNOWN_NAME = "Unknown", UNKNOWN_TYPE = "Log", UNKNOWN_VERSION = "Unknown version";
                    name = !jsonNodeField(jsonNode, "name").equals("null") ? jsonNodeField(jsonNode, "name") : UNKNOWN_NAME;
                    type = !jsonNodeField(jsonNode, "type").equals("Unknown Log") ? jsonNodeField(jsonNode, "type") : UNKNOWN_TYPE;
                    version = !jsonNodeField(jsonNode, "version").equals("null") ? jsonNodeField(jsonNode, "version") : UNKNOWN_VERSION;
                    if (!(type.equals(UNKNOWN_TYPE) && name.equals(UNKNOWN_NAME) && version.equals(UNKNOWN_VERSION)))
                        return List.of(
                                name, type, version
                        );
                    else return List.of("null");
                }
            }
        }
        throw new RuntimeException();
    }

    private String jsonNodeField(JsonNode jsonNode, String name) {
        return jsonNode.get(name).asText();
    }

    private void decompressGzipFile(String gzipFile, String outputFile) {
        File gzip = new File(gzipFile);
        try (
                FileInputStream fileInputStream = new FileInputStream(gzip);
                GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
                FileOutputStream fileOutputStream = new FileOutputStream(outputFile)
        ) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = gzipInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        gzip.delete();
    }

    public static boolean isDiscontinued(String version) {
        for (String discontinuedPattern : DISCONTINUED_VERSIONS) {
            String regex = discontinuedPattern.replace("*", ".*");
            Pattern pattern = Pattern.compile(regex);
            if (pattern.matcher(version).find()) {
                return true;
            }
        }
        return false;
    }
}
