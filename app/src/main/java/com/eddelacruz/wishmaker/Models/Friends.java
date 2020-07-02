package com.eddelacruz.wishmaker.Models;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

public class Friends {
    @SerializedName("image")
    private String image;

    @SerializedName("name")
    private String name;

    @SerializedName("uid")
    private String uid;

    @SerializedName("bitmap_profile")
    private Bitmap bitmap_profile;

    public Friends() {

    }
    public Friends(String image, String name, String uid) {
        this.image = image;
        this.name = name;
        this.uid = uid;
    }

    public String getImage() { return image; }

    public String getName() { return name; }

    public String getUid() { return uid; }

    public Bitmap getBitmap_profile() { return bitmap_profile; }

    public void setBitmap_profile(Bitmap bitmap_profile) {
        this.bitmap_profile = bitmap_profile;
    }

    public void setUid(String uid) { this.uid = uid; }

    public void setImage(String image) { this.image = image; }

    public void setName(String name) { this.name = name; }
}

