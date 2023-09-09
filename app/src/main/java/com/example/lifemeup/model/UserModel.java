package com.example.lifemeup.model;

public class UserModel {

    private String email, name, image_prof, uid, status;

    public UserModel() {
    }

    public UserModel(String email, String name, String image_prof, String uid, String status) {
        this.email = email;
        this.name = name;
        this.image_prof = image_prof;
        this.uid = uid;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_prof() {
        return image_prof;
    }

    public void setImage_prof(String image_prof) {
        this.image_prof = image_prof;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
