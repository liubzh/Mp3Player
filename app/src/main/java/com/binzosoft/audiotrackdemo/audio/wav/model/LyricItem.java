package com.binzosoft.audiotrackdemo.audio.wav.model;

public class LyricItem {

    private String content;
    private int timestamp;

    public LyricItem(int timestamp, String content) {
        this.timestamp = timestamp;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{ ")
                .append("timestamp:").append(timestamp)
                .append(", content:").append(content)
                .append(" }");
        return sb.toString();
    }
}
