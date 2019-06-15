package com.trainex.model;

public class MySessionsBookings {
    private String orderID;
    private int price;
    private String session;
    private String trainer;
    private String dateTime;
    private String phoneNumber;
    public MySessionsBookings() {
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public MySessionsBookings(String orderID, int price, String session, String trainer, String dateTime, String phoneNumber) {

        this.orderID = orderID;
        this.price = price;
        this.session = session;
        this.trainer = trainer;
        this.dateTime = dateTime;
        this.phoneNumber = phoneNumber;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getTrainer() {
        return trainer;
    }

    public void setTrainer(String trainer) {
        this.trainer = trainer;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public MySessionsBookings(int price, String session, String trainer, String dateTime) {
        this.price = price;
        this.session = session;
        this.trainer = trainer;
        this.dateTime = dateTime;
    }
}
