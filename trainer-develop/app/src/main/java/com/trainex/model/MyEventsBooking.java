package com.trainex.model;

public class MyEventsBooking {
    private String orderID;
    private String title;
    private String resImage;
    private int price;
    private String dateTime;
    private String location;
    private String content;
    private int idEvent;
    private String phone;
    public MyEventsBooking() {
    }

    public String getOrderID() {

        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResImage() {
        return resImage;
    }

    public void setResImage(String resImage) {
        this.resImage = resImage;
    }

    public int getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
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

    public MyEventsBooking(String title, String resImage, int price, String dateTime, String location, String content) {

        this.title = title;
        this.resImage = resImage;
        this.price = price;
        this.dateTime = dateTime;
        this.location = location;
        this.content = content;
    }

    public int getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(int idEvent) {
        this.idEvent = idEvent;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public MyEventsBooking(String orderID, String title, String resImage, int price, String dateTime, String location, String content, int idEvent, String phone) {

        this.orderID = orderID;
        this.title = title;
        this.resImage = resImage;
        this.price = price;
        this.dateTime = dateTime;
        this.location = location;
        this.content = content;
        this.idEvent = idEvent;
        this.phone = phone;
    }
}
