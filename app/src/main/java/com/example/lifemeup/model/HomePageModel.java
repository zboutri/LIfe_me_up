package com.example.lifemeup.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class HomePageModel {

    private String username, profImage, imageUrl, uid, description, id;
    @ServerTimestamp
    private Date time;
    private List<String> congratsCounter;


    public HomePageModel() {
    }

    public HomePageModel(String username, String profImage, String imageUrl,
                         String uid, String description, String id, Date time,
                         List<String> congratsCounter) {
        this.username = username;
        this.profImage = profImage;
        this.imageUrl = imageUrl;
        this.uid = uid;
        this.description = description;
        this.id = id;
        this.time = time;
        this.congratsCounter = congratsCounter;
    }

    public List<String> getCongratsCounter() {
        return congratsCounter;
    }

    public void setCongratsCounter(List<String> congratsCounter) {
        this.congratsCounter = congratsCounter;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getProfImage() {
        return profImage;
    }

    public void setProfImage(String profImage) {
        this.profImage = profImage;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
