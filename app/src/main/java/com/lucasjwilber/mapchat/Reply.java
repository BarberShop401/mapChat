package com.lucasjwilber.mapchat;

public class Reply {
    private String username;
    private String body;
    private long timestamp;

    public Reply(){};
    public Reply(String username, String body, long timestamp) {
        this.username = username;
        this.body = body;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
