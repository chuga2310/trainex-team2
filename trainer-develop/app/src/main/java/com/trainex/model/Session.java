package com.trainex.model;

import java.io.Serializable;

public class Session implements Serializable {
    private int idSession;
    private int price;
    private String typeSession;

    public Session(int idSession, int price, String typeSession) {
        this.idSession = idSession;
        this.price = price;
        this.typeSession = typeSession;
    }

    public int getIdSession() {
        return idSession;
    }

    public void setIdSession(int idSession) {
        this.idSession = idSession;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getTypeSession() {
        return typeSession;
    }

    public void setTypeSession(String typeSession) {
        this.typeSession = typeSession;
    }

    public Session() {

    }
}
