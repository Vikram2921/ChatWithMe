package com.NobodyKnows.chatlayoutview.Model;

import com.NobodyKnows.chatlayoutview.Services.LayoutService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {

    private String name;
    private String password;
    private String status = "Hey there ! i am using chat with me.";
    private String contactNumber;
    private String profileUrl;
    private Integer colorCode = LayoutService.generateUserColorCode();
    private Boolean verified = false;
    private Boolean muted = false;
    private Boolean blocked = false;
    private Date lastOnline = new Date();
    private String currentStatus = "Offline";
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Integer getColorCode() {
        return colorCode;
    }

    public void setColorCode(Integer colorCode) {
        this.colorCode = colorCode;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Date getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(Date lastOnline) {
        this.lastOnline = lastOnline;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
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

    public Boolean getMuted() {
        return muted;
    }

    public void setMuted(Boolean muted) {
        this.muted = muted;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }
}
