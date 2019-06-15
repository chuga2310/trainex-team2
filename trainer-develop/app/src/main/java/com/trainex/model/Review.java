package com.trainex.model;

public class Review {
    private int userID;
    private String userName;
    private float rating;
    private String comment;
    private String resAvatar;
    private String time;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getResAvatar() {
        return resAvatar;
    }

    public void setResAvatar(String resAvatar) {
        this.resAvatar = resAvatar;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Review() {

    }

    public Review(int userID, String userName, float rating, String comment, String resAvatar, String time) {
        this.userID = userID;
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.resAvatar = resAvatar;
        this.time = time;
    }
}
