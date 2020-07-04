package com.eddelacruz.wishmaker.Models;

import com.google.gson.annotations.SerializedName;

public class Settings {
    @SerializedName("searchable")
    private Boolean searchable;

    @SerializedName("last_date_changed")
    private Long last_date_changed;

    @SerializedName("uid")
    private String uid;

    public Settings() {

    }

    public Settings(String uid, Boolean searchable, Long last_date_changed) {
        this.uid = uid;
        this.searchable = searchable;
        this.last_date_changed = last_date_changed;
    }

    public Settings(Boolean searchable, Long last_date_changed) {
        this.searchable = searchable;
        this.last_date_changed = last_date_changed;
    }


    public Boolean getSearchable() {
        return searchable;
    }

    public String getUid() {
        return uid;
    }

    public Long getLast_date_changed() { return last_date_changed; }

    public void setLast_date_changed(Long last_date_changed) { this.last_date_changed = last_date_changed; }

    public void setSearchable(Boolean searchable) {
        this.searchable = searchable;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
