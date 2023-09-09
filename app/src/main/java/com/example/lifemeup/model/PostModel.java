package com.example.lifemeup.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class PostModel {

    private String imageUrl, id, description, uid;

    @ServerTimestamp
    private Date time;

    public PostModel() {
    }

    public PostModel(String imageUrl, String id, String description, String uid, Date time) {
        this.imageUrl = imageUrl;
        this.id = id;
        this.description = description;
        this.time = time;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date timestamp) {
        this.time = timestamp;
    }
}