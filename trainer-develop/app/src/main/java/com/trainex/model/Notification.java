package com.trainex.model;

public class Notification {
    private int id;
    private String content;
    private String trainer;
    private String date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTrainer() {
        return trainer;
    }

    public void setTrainer(String trainer) {
        this.trainer = trainer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Notification() {

    }

    public Notification(String content, String trainer, String date) {

        this.content = content;
        this.trainer = trainer;
        this.date = date;
    }

    public Notification(int id, String content, String trainer, String date) {

        this.id = id;
        this.content = content;
        this.trainer = trainer;
        this.date = date;
    }
}
