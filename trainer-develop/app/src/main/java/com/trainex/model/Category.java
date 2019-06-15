package com.trainex.model;

public class Category {
    private int id;
    private String name;
    private String resImage;

    public Category() {
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResImage() {
        return resImage;
    }

    public void setResImage(String resImage) {
        this.resImage = resImage;
    }

    public Category(String name, String resImage) {

        this.name = name;
        this.resImage = resImage;
    }

    public Category(int id, String name, String resImage) {

        this.id = id;
        this.name = name;
        this.resImage = resImage;
    }
}
