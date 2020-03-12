package com.lucasjwilber.mapchat;

import java.util.LinkedList;
import java.util.List;

public class User {
    private String username;
    private String email;
    private List<Comment> comments;

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.comments = new LinkedList<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
