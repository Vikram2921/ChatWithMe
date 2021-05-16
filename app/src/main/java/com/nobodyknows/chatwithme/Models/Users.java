package com.nobodyknows.chatwithme.Models;

import com.nobodyknows.chatwithme.Models.Helper.DateTimeModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Users {
    private String name;
    private String password;
    private String status = "Hey there ! i am using chat with me.";
    private String contactNumber;
    private String profileUrl;
    private Date lastOnline = new Date();
    private Date accountCreated = new Date();
    private List<UserStory> stories = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public Date getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(Date lastOnline) {
        this.lastOnline = lastOnline;
    }

    public Date getAccountCreated() {
        return accountCreated;
    }

    public void setAccountCreated(Date accountCreated) {
        this.accountCreated = accountCreated;
    }

    public List<UserStory> getStories() {
        return stories;
    }

    public void setStories(List<UserStory> stories) {
        this.stories = stories;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
