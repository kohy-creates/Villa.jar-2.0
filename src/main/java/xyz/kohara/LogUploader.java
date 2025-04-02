// By SkyKing_PX
// Thankss uωu <3
//
// Edited by kohy
package xyz.kohara;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

public class LogUploader extends ListenerAdapter {

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
                    event.getChannel().sendTyping().queue();
                    String extension = attachment.getFileExtension();
                    assert extension != null;
                    extension = (extension.equals("gz")) ? "log" : extension;
                    File tempFile = new File(LOGS_FOLDER, "temp-" + Math.random() * 1_000_001 + "." + extension);
                    fileMap.put(tempFile, attachment.getFileName());
                    attachment.getProxy().download().thenAccept(inputStream -> {
                        try {
                            iter.addAndGet(1);
                            if (Objects.equals(attachment.getFileExtension(), "gz")) {
                                saveInputStreamToFile(inputStream, new File(tempFile + ".gz"));
                                decompressGzipFile(tempFile + ".gz", tempFile.toString());
                            } else {
                                saveInputStreamToFile(inputStream, tempFile);
                            }

                            if (iter.get() == event.getMessage().getAttachments().size()) {
                                CompletableFuture.runAsync(() -> uploadAndSendLinks(fileMap, event));
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
        Map<String, String> uploadedUrls = new HashMap<>();

        for (Map.Entry<File, String> entry : fileMap.entrySet()) {
            File tempFile = entry.getKey();
            String originalName = entry.getValue();

            try {
                String url = uploadToMclogs(tempFile);
                uploadedUrls.put(originalName, url);
                tempFile.delete();
            } catch (IOException e) {
                event.getChannel().sendMessage("❌ Error uploading file `" + originalName + "`").queue();
                e.printStackTrace();
            }
        }

        if (!uploadedUrls.isEmpty()) {
            ArrayList<Button> buttons = new ArrayList<>();
            for (String key : uploadedUrls.keySet()) {
                buttons.add(Button.link(uploadedUrls.get(key), key).withEmoji(Emoji.fromUnicode("📜")));
            }

            event.getChannel().sendMessage("").addActionRow(buttons).queue();
        }
    }

    private String uploadToMclogs(File file) throws IOException {
        StringBuilder logContent = new StringBuilder();

        // Read file safely
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logContent.append(line).append("\n");
            }
        } catch (IOException e) {
            return "Error reading file";
        }

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("https://api.mclo.gs/1/log");
            post.setEntity(MultipartEntityBuilder.create().addTextBody("content", logContent.toString()).build());

            try (CloseableHttpResponse response = client.execute(post)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String result = new String(entity.getContent().readAllBytes());
                    JsonNode jsonNode = new ObjectMapper().readTree(result);
                    return jsonNode.has("url") ? jsonNode.get("url").asText() : "Error uploading file";
                }
            }
        }
        return "Error uploading file";
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
}
