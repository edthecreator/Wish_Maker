package com.eddelacruz.wishmaker.Models;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

public class NewUser {

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("image")
    private String image;

    @SerializedName("email")
    private String email;

    @SerializedName("uid")
    private String uid;

    @SerializedName("status")
    private String status;

    @SerializedName("date")
    private Long date;

    @SerializedName("fullName")
    private String fullName;


    public NewUser() {

    }



    public NewUser(String email, String image, String lastName, String firstName, String uid, String status, Long date, String fullName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.uid = uid;
        this.status = status;
        this.date = date;
        this.fullName = fullName;
    }

    public Long getDate() {
        return date;
    }

    public String getFullName() {
        return fullName;
    }

    public String getStatus() {
        return status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() { return uid; }

    public String getEmail() { return email; }

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

}
