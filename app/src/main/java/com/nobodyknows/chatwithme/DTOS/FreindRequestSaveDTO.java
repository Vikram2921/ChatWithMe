package com.nobodyknows.chatwithme.DTOS;

import java.util.Date;

public class FreindRequestSaveDTO {
    private String contactNumber ="";
    private String requestSentBy = "";
    private Date requestSentAt = new Date();
    private Date requestAcceptedAt = new Date();
    private Boolean blocked = false;
    private Boolean muted = false;

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public Boolean getMuted() {
        return muted;
    }

    public void setMuted(Boolean muted) {
        this.muted = muted;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Date getRequestSentAt() {
        return requestSentAt;
    }

    public void setRequestSentAt(Date requestSentAt) {
        this.requestSentAt = requestSentAt;
    }

    public Date getRequestAcceptedAt() {
        return requestAcceptedAt;
    }

    public void setRequestAcceptedAt(Date requestAcceptedAt) {
        this.requestAcceptedAt = requestAcceptedAt;
    }

    public String getRequestSentBy() {
        return requestSentBy;
    }

    public void setRequestSentBy(String requestSentBy) {
        this.requestSentBy = requestSentBy;
    }
}
