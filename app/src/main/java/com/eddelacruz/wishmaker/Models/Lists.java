package com.eddelacruz.wishmaker.Models;

import com.google.gson.annotations.SerializedName;

public class Lists {

    @SerializedName("image")
    private String image;

    @SerializedName("name")
    private String name;

    public Lists() {
    }

    public Lists(String image, String name) {
        this.image = image;
        this.name = name;
    }

    public String getImage() { return image; }

    public String getName() { return name; }

    public void setImage(String image) { this.image = image; }

    public void setName(String name) { this.name = name; }
}
