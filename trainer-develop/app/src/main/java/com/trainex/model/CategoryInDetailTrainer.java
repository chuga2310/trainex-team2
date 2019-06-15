package com.trainex.model;

import java.util.ArrayList;

public class CategoryInDetailTrainer {
    private int idCategory;
    private String nameCategory;
    private ArrayList<Session> listSession;
    private int idSession;
    private boolean isShow;

    public CategoryInDetailTrainer(int idCategory, String nameCategory, ArrayList<Session> listSession, int idSession, boolean isShow) {
        this.idCategory = idCategory;
        this.nameCategory = nameCategory;
        this.listSession = listSession;
        this.idSession = idSession;
        this.isShow = isShow;
    }

    public int getIdSession() {
        return idSession;
    }

    public void setIdSession(int idSession) {
        this.idSession = idSession;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public int getIdCategory() {

        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public String getNameCategory() {
        return nameCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }

    public ArrayList<Session> getListSession() {
        return listSession;
    }

    public void setListSession(ArrayList<Session> listSession) {
        this.listSession = listSession;
    }

    public CategoryInDetailTrainer() {

    }

}
