package com.trainex.model;

public class Event {
    private int id;
    private String resImage;
    private String title;
    private String trainer;
    private int price;
    private String date;
    private String time;
    private String location;
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResImage() {
        return resImage;
    }

    public void setResImage(String resImage) {
        this.resImage = resImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTrainer() {
        return trainer;
    }

    public void setTrainer(String trainer) {
        this.trainer = trainer;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Event() {
    }

    public Event(int id, String resImage, String title, String trainer, int price, String date, String time, String location, String content) {
        this.id = id;
        this.resImage = resImage;
        this.title = title;
        this.trainer = trainer;
        this.price = price;
        this.date = date;
        this.time = time;
        this.location = location;
        this.content = content;
    }

    public Event(String resImage, String title, String trainer, int price, String date, String time, String location, String content) {
        this.resImage = resImage;
        this.title = title;
        this.trainer = trainer;
        this.price = price;
        this.date = date;
        this.time = time;
        this.location = location;
        this.content = content;
    }
}
