package com.eddelacruz.wishmaker.Models;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("image")
    private String image;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("email")
    private String email;


    @SerializedName("uid")
    private String uid;

    @SerializedName("status")
    private String status;

    @SerializedName("date")
    private Long date;


    @SerializedName("bitmap_profile")
    private Bitmap bitmap_profile;

    public User() {

    }



    public User(String email, String lastName, String firstName, String uid, String status, Long date) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.uid = uid;
        this.status = status;
        this.date = date;
    }

    public User(String image, String firstName, String lastName, String uid, String status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = image;
        this.uid = uid;
        this.status = status;
    }

    public String getImage() { return image; }

    public String getUid() { return uid; }

    public String getstatus() {
        return status;
    }

    public void setstatus(String status) {
        this.status = status;
    }

    public String getEmail() { return email; }


    public Bitmap getBitmap_profile() { return bitmap_profile; }

    public void setBitmap_profile(Bitmap bitmap_profile) {
        this.bitmap_profile = bitmap_profile;
    }

    public void setEmail(String email) { this.email = email; }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUid(String uid) { this.uid = uid; }

    public void setImage(String image) { this.image = image; }

}
