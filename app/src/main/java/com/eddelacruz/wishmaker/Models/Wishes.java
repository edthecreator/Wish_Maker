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

    public Wishes() {
    }

    public Wishes(String image, String name, Double price, String link) {
        this.image = image;
        this.name = name;
        this.price = price;
        this.link = link;
    }

    public String getImage() { return image; }

    public String getName() { return name; }

    public Double getPrice() { return price; }

    public String getLink() { return link; }

    public void setLink(String link) { this.link = link; }

    public void setImage(String image) { this.image = image; }

    public void setName(String name) { this.name = name; }

    public void setPrice(Double price) { this.price = price; }
}

