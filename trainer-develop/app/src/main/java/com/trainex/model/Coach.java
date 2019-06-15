package com.trainex.model;

public class Coach {
    private int id;
    private int idCategory;

    public Coach() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }
    public Coach(int id, String name, String location, float rating, boolean isFavorites, boolean isFeatured, String resAvatar) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.rating = rating;
        this.isFavorites = isFavorites;
        this.isFeatured = isFeatured;
        this.resAvatar = resAvatar;
    }
    public Coach(int id, String name, String location, float rating, boolean isFavorites, boolean isFeatured, String resAvatar, int idCategory) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.rating = rating;
        this.isFavorites = isFavorites;
        this.isFeatured = isFeatured;
        this.resAvatar = resAvatar;
        this.idCategory = idCategory;
    }

    public Coach(int id, String name, String prefferedTime, String location, String prefferedLocations, String about, String qualification, String specialities, float rating, boolean isFavorites, boolean isFeatured, String resAvatar, int idCategory) {

        this.id = id;
        this.name = name;
        this.prefferedTime = prefferedTime;
        this.location = location;
        this.prefferedLocations = prefferedLocations;
        this.about = about;
        this.qualification = qualification;
        this.specialities = specialities;
        this.rating = rating;
        this.isFavorites = isFavorites;
        this.isFeatured = isFeatured;
        this.resAvatar = resAvatar;
        this.idCategory = idCategory;
    }

    private String name;
    private String prefferedTime;
    private String location;
    private String prefferedLocations;
    private String about;
    private String qualification;
    private String specialities;
    private float rating;
    private boolean isFavorites;
    private boolean isFeatured;
    private String resAvatar;

    public String getResAvatar() {
        return resAvatar;
    }

    public void setResAvatar(String resAvatar) {
        this.resAvatar = resAvatar;
    }

    public Coach(String name, String prefferedTime, String location, String prefferedLocations, String about, String qualification, String specialities, float rating, boolean isFavorites, boolean isFeatured, String resAvatar) {
        this.name = name;
        this.prefferedTime = prefferedTime;
        this.location = location;
        this.prefferedLocations = prefferedLocations;
        this.about = about;
        this.qualification = qualification;
        this.specialities = specialities;
        this.rating = rating;
        this.isFavorites = isFavorites;
        this.isFeatured = isFeatured;
        this.resAvatar = resAvatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefferedTime() {
        return prefferedTime;
    }

    public void setPrefferedTime(String prefferedTime) {
        this.prefferedTime = prefferedTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPrefferedLocations() {
        return prefferedLocations;
    }

    public void setPrefferedLocations(String prefferedLocations) {
        this.prefferedLocations = prefferedLocations;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getSpecialities() {
        return specialities;
    }

    public void setSpecialities(String specialities) {
        this.specialities = specialities;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean isFavorites() {
        return isFavorites;
    }

    public void setFavorites(boolean favorites) {
        isFavorites = favorites;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }
}
