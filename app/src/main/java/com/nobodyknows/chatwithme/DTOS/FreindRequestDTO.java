package com.nobodyknows.chatwithme.DTOS;

import com.NobodyKnows.chatlayoutview.Model.Message;

import java.util.Date;

public class FreindRequestDTO {
    private String name = "Testing Bot";
    private String status= "Hi i am using chatme";
    private String contactNumber ="TestingNum";
    private String profileUrl ="NO_PROFILE";
    private Boolean verified = false;
    private Date requestSentAt = new Date();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Date getRequestSentAt() {
        return requestSentAt;
    }

    public void setRequestSentAt(Date requestSentAt) {
        this.requestSentAt = requestSentAt;
    }

}
