package com.eddelacruz.wishmaker.Models;

import com.google.gson.annotations.SerializedName;

public class Lists {

    @SerializedName("image")
    private String image;

    @SerializedName("name")
    private String name;

    @SerializedName("created_at")
    private Long created_at;

    public Lists() {
    }

    public Lists(String image, String name, Long created_at) {
        this.image = image;
        this.name = name;
        this.created_at = created_at;
    }


    public String getImage() { return image; }

    public String getName() { return name; }

    public Long getCreated_at() {
        return created_at;
    }


    public void setCreated_at(Long created_at) {
        this.created_at = created_at;
    }

    public void setImage(String image) { this.image = image; }

    public void setName(String name) { this.name = name; }
}
