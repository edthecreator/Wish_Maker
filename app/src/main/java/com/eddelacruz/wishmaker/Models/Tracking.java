package com.eddelacruz.wishmaker.Models;

import com.google.gson.annotations.SerializedName;

public class Tracking {

    @SerializedName("last_date")
    private Long last_date;

    @SerializedName("reload_friends")
    private String reload_friends;

    @SerializedName("reload_settings")
    private String reload_settings;

    @SerializedName("reload_profile")
    private String reload_profile;

    @SerializedName("uid")
    private String uid;

    public Tracking() {

    }

    public Tracking(String uid, Long last_date, String reload_friends, String reload_settings, String reload_profile) {
        this.last_date = last_date;
        this.reload_friends = reload_friends;
        this.reload_profile = reload_profile;
        this.reload_settings = reload_settings;

    }

    public void setLast_date(Long last_date) {
        this.last_date = last_date;
    }

    public void setReload_friends(String reload_friends) {
        this.reload_friends = reload_friends;
    }

    public void setReload_profile(String reload_profile) {
        this.reload_profile = reload_profile;
    }

    public void setReload_settings(String reload_settings) {
        this.reload_settings = reload_settings;
    }

    public Long getLast_date() {
        return last_date;
    }

    public String getReload_friends() {
        return reload_friends;
    }

    public String getReload_profile() {
        return reload_profile;
    }

    public String getReload_settings() {
        return reload_settings;
    }
}
