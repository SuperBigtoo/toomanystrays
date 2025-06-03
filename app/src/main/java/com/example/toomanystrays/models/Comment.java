package com.example.toomanystrays.models;

public class Comment {
    int id;
    int pin_id;
    String text;
    String created_time;
    String username;
    String user_id;

    public Comment(int id, int pin_id, String text, String created_time, String username, String user_id) {
        this.id = id;
        this.pin_id = pin_id;
        this.text = text;
        this.created_time = created_time;
        this.username = username;
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPin_id() {
        return pin_id;
    }

    public void setPin_id(int pin_id) {
        this.pin_id = pin_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
