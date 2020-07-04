package com.eddelacruz.wishmaker.Managers;


import com.eddelacruz.wishmaker.Models.Settings;
import com.eddelacruz.wishmaker.Models.User;

import java.util.ArrayList;


public class DataManager {

    static DataManager dm;

    private final String TAG = "DATA MANAGER";

    private ArrayList<String> fragments;
    private String UserId;
    private User user;
    private Settings settings;


    private DataManager(){
    }

    public static  DataManager getInstance(){
        if(dm == null){
            dm = new DataManager();
        }

        return dm;

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Settings getSettings() { return settings; }

    public void setSettings(Settings settings) { this.settings = settings; }

    public void setUserId(String userId) {
        this.UserId = userId;
    }

    public String getUserId() {
        return UserId;
    }

    public ArrayList<String> getFragments() {
        return fragments;
    }

    public void setFragments(ArrayList<String> fragments) {
        this.fragments = fragments;
    }



}
