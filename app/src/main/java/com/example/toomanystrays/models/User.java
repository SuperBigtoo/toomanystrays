package com.example.toomanystrays.models;

public class User {
    String id;
    String username;
    String email;

    public String getId() {
        return id;
    }
    public String getUsername() { return username; }
    public String getEmail() {
        return email;
    }

    public void setId(String id) {this.id = id;}
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) {
        this.email = email;
    }
}
