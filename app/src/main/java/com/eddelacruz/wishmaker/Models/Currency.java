package com.eddelacruz.wishmaker.Models;

import android.graphics.drawable.Drawable;

import com.google.gson.annotations.SerializedName;

public class Currency {
    @SerializedName("symbol")
    private Drawable symbol;

    @SerializedName("name")
    private String name;

    public Currency() {
    }

    public Currency(Drawable symbol, String name) {
        this.symbol = symbol;
        this.name = name;
    }

    public Drawable getSymbol() { return symbol; }

    public String getName() { return name; }

    public void setSymbol(Drawable symbol) { this.symbol = symbol; }

    public void setName(String name) { this.name = name; }
}
