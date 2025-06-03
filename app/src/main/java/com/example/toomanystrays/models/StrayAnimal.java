package com.example.toomanystrays.models;

public class StrayAnimal {
    int id;
    String stray_name;
    String details;
    String image_url;
    int pin_id;
    int category_id;

    public StrayAnimal(int id, String stray_name, String details, String image_url, int pin_id, int category_id) {
        this.id = id;
        this.stray_name = stray_name;
        this.details = details;
        this.image_url = image_url;
        this.pin_id = pin_id;
        this.category_id = category_id;
    }

    public int getId() {
        return id;
    }
    public String getStray_name() {
        return stray_name;
    }

    public String getDetails() {
        return details;
    }
    public String getImage_url() {
        return image_url;
    }
    public int getPin_id() {
        return pin_id;
    }
    public int getCategory_id() {
        return category_id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setStray_name(String stray_name) {
        this.stray_name = stray_name;
    }

    public void setDetails(String details) {
        this.details = details;
    }
    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
    public void setPin_id(int pin_id) {
        this.pin_id = pin_id;
    }
    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }
}
