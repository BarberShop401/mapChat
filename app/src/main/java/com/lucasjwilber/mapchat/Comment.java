package com.lucasjwilber.mapchat;

public class Comment {
    public String title;
    public String text;
    public double lat;
    public double lng;
    public Comment(String title, String text, double lat, double lng) {
        this.title = title;
        this.text = text;
        this.lat = lat;
        this.lng = lng;
    }
}
