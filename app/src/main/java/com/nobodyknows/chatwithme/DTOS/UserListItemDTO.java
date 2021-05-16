package com.nobodyknows.chatwithme.DTOS;

import com.nobodyknows.chatwithme.Models.Message;

public class UserListItemDTO {
    private String name = "DUMMY";
    private String contactNumber = "8290879124";
    private String profile = "NO_PROFILE";
    private Message lastMessage;

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
}
