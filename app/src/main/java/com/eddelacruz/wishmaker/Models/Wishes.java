package com.eddelacruz.wishmaker.Models;

import com.google.gson.annotations.SerializedName;

public class Wishes {

    @SerializedName("image")
    private String image;

    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private Double price;

    @SerializedName("link")
    private String link;

    @SerializedName("created_at")
    private Long created_At;

    public Wishes() {
    }

    public Wishes(String image, String name, Double price, String link, Long created_at) {
        this.image = image;
        this.name = name;
        this.price = price;
        this.link = link;
        this.created_At = created_at;
    }

    public String getImage() { return image; }

    public String getName() { return name; }

    public Double getPrice() { return price; }

    public String getLink() { return link; }

    public Long getCreated_At() {
        return created_At;
    }

    public void setCreated_At(Long created_At) {
        this.created_At = created_At;
    }

    public void setLink(String link) { this.link = link; }

    public void setImage(String image) { this.image = image; }

    public void setName(String name) { this.name = name; }

    public void setPrice(Double price) { this.price = price; }
}

