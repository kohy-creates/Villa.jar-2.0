package xyz.kohara.status;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusData {
    // Each field corresponds to a specific status
    // There is probably a simpler way to do it, but I couldn't get it to work
    private List<String> listening;
    private List<String> playing;
    private List<String> watching;
    private List<String> streaming;
    private List<String> competing;

    @SerializedName("streaming_url")
    private String streamingURL;

    private List<String> normal;

    public List<String> getListening() {
        return listening;
    }

    public void setListening(List<String> listening) {
        this.listening = listening;
    }

    public List<String> getCompeting() {
        return competing;
    }

    public void setCompeting(List<String> competing) {
        this.competing = competing;
    }

    public List<String> getPlaying() {
        return playing;
    }

    public void setPlaying(List<String> playing) {
        this.playing = playing;
    }

    public List<String> getWatching() {
        return watching;
    }

    public void setWatching(List<String> watching) {
        this.watching = watching;
    }

    public List<String> getStreaming() {
        return streaming;
    }

    public void setStreaming(List<String> streaming) {
        this.streaming = streaming;
    }

    public String getStreamingURL() {
        return streamingURL;
    }

    public void setStreamingURL(String streamingURL) {
        this.streamingURL = streamingURL;
    }

    public List<String> getNormal() {
        return normal;
    }

    public void setNormal(List<String> normal) {
        this.normal = normal;
    }

    // Literally the only thing we care about
    public Map<String, List<String>> getAllActivities() {
        Map<String, List<String>> map = new HashMap<>();

        map.put("listening", getListening());
        map.put("watching", getWatching());
        map.put("streaming", getStreaming());
        map.put("normal", getNormal());
        map.put("playing", getPlaying());
        map.put("competing", getCompeting());

        return map;
    }
}
