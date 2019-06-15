package com.trainex.model;

public class Report {
    private  int id;
    private String content;

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

    public Report() {

    }

    public Report(int id, String content) {

        this.id = id;
        this.content = content;
    }
}
