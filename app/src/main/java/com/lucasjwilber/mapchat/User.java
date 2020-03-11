package com.lucasjwilber.mapchat;

import java.util.List;

public class User {
    private String username;
    private String email;
    private List<Comment<Reply<String>>> comments;

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public User(String username, String email, List<Comment<Reply<String>>> comments) {
        this.username = username;
        this.email = email;
        this.comments = comments;
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

    public List<Comment<Reply<String>>> getComments() {
        return comments;
    }

    public void setComments(List<Comment<Reply<String>>> comments) {
        this.comments = comments;
    }
}
