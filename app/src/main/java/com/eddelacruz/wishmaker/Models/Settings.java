package com.eddelacruz.wishmaker.Models;

import com.google.gson.annotations.SerializedName;

public class Settings {
    @SerializedName("searchable")
    private Boolean searchable;

    @SerializedName("uid")
    private String uid;

    public Settings() {

    }

    public Settings(String uid, Boolean searchable) {
        this.uid = uid;
        this.searchable = searchable;
    }

    public Boolean getSearchable() {
        return searchable;
    }

    public String getUid() {
        return uid;
    }

    public void setSearchable(Boolean searchable) {
        this.searchable = searchable;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
