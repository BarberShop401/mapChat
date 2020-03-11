package com.lucasjwilber.mapchat;

import java.util.List;

public class Comment<R> {
    private String title;
    private String text;
    private double lat;
    private double lng;
    private long timestamp;
    List<Reply<String>> replies;

    public Comment(String title, String text, double lat, double lng, long timestamp) {
        this.title = title;
        this.text = text;
        this.lat = lat;
        this.lng = lng;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


}
