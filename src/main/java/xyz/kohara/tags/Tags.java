package xyz.kohara.tags;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import okio.Path;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import xyz.kohara.VillaJar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tags {

    public final static String TAGS_FOLDER = "data/tags/";

    public static Map<String, String> TAGS = new HashMap<>();
    public static Map<String, ArrayList<String>> CATEGORIES = new HashMap<>();

    static {
        try {
            createTagMap();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getTag(String id) {
        return TAGS.get(id);
    }

    public static void createTagMap() throws IOException {
        File folder = new File(TAGS_FOLDER);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String tagName = tagName(file);
                    String tagContent = readMarkdownWithoutYaml(file);
                    TAGS.put(tagName, tagContent);
                    BufferedReader reader = new BufferedReader(new FileReader(file));

                    StringBuilder yamlContent = new StringBuilder();
                    String line;
                    boolean inYamlBlock = false;

                    while ((line = reader.readLine()) != null) {
                        if (line.trim().equals("---")) {
                            if (inYamlBlock) break;
                            else inYamlBlock = true;
                            continue;
                        }
                        if (inYamlBlock) yamlContent.append(line).append("\n");
                    }

                    if (!yamlContent.isEmpty()) {
                        Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
                        Map<String, Object> metadata = yaml.load(yamlContent.toString());

                        metadata.putIfAbsent("category", "none") ;
                        metadata.forEach((key, value) -> {
                            String object = value.toString();
                            switch (key) {
                                case "category" -> {
                                    ArrayList<String> tags = new ArrayList<>();
                                    if (CATEGORIES.get(object) != null) {
                                        tags = CATEGORIES.get(object);
                                    }
                                    tags.add(tagName);
                                    CATEGORIES.put(object, tags);
                                }
                                case "aliases" -> {
                                    String[] array = object.split(",");
                                    for (String item : array) {
                                        TAGS.put(item, tagContent);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    private static String tagName(File file) {
        String name = file.getName();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex > 0) {
            return name.substring(0, dotIndex);
        } else {
            return name;
        }
    }

    private static String readMarkdownWithoutYaml(File file) throws IOException {
        List<String> lines = Files.readAllLines(Path.get(file).toNioPath());
        StringBuilder content = new StringBuilder();
        boolean insideYamlMetadata = false;
        for (String line : lines) {
            if (line.trim().equals("---")) {
                insideYamlMetadata = !insideYamlMetadata;
                continue;
            }
            if (!insideYamlMetadata) {
                content.append(line).append("\n");
            }
        }
        return content.toString().trim();
    }
}
