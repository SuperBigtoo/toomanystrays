package com.example.toomanystrays.models;

public class Pin {
    int id;
    String pin_name;
    String details;
    double latitude;
    double longitude;
    String created_time;
    String user_id;

    public int getId() { return id; }
    public String getPin_name() { return pin_name; }
    public String getDetails() { return details; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getCreated_time() { return created_time; }
    public String getUser_id() { return user_id; }

    public void setId(int id) { this.id = id; }
    public void setPin_name(String pin_name) { this.pin_name = pin_name; }
    public void setDetails(String details) { this.details = details; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setCreated_time(String created_time) { this.created_time = created_time; }
    public void setUser_id(String user_id) { this.user_id = user_id; }
}
