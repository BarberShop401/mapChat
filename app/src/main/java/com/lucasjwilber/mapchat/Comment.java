package com.lucasjwilber.mapchat;

import java.util.LinkedList;
import java.util.List;

public class Comment {
    private String id;
    private String title;
    private String text;
    private double lat;
    private double lng;
    private long timestamp;
    public List<Reply> replies;

    // zero-arg constructor for firestore .toObject method
    public Comment(){};
    public Comment(String title, String text, double lat, double lng, long timestamp) {
        this.title = title;
        this.text = text;
        this.lat = lat;
        this.lng = lng;
        this.timestamp = timestamp;
        this.replies = new LinkedList<>();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
