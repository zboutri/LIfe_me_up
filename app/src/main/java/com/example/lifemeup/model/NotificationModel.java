package com.example.lifemeup.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class NotificationModel {

    String id, notification, image;

    @ServerTimestamp
    Date time;

    public NotificationModel() {
    }

    public NotificationModel(String id, String notification, Date time, String image) {
        this.id = id;
        this.notification = notification;
        this.time = time;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
