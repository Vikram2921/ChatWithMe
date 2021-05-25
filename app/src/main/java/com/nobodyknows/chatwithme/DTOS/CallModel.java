package com.nobodyknows.chatwithme.DTOS;

public class CallModel {

    private String username;
    private String calltype;
    private Integer callDuration;
    private String endCause;
    private Long endedTime;
    private Long establishedTime;
    private Long startedTime;
    private String error;
    private String callId;
    private Boolean isIncomingCall = true;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCalltype() {
        return calltype;
    }

    public void setCalltype(String calltype) {
        this.calltype = calltype;
    }

    public Integer getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(Integer callDuration) {
        this.callDuration = callDuration;
    }

    public String getEndCause() {
        return endCause;
    }

    public void setEndCause(String endCause) {
        this.endCause = endCause;
    }

    public Long getEndedTime() {
        return endedTime;
    }

    public void setEndedTime(Long endedTime) {
        this.endedTime = endedTime;
    }

    public Long getEstablishedTime() {
        return establishedTime;
    }

    public void setEstablishedTime(Long establishedTime) {
        this.establishedTime = establishedTime;
    }

    public Long getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(Long startedTime) {
        this.startedTime = startedTime;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public Boolean getIncomingCall() {
        return isIncomingCall;
    }

    public void setIncomingCall(Boolean incomingCall) {
        isIncomingCall = incomingCall;
    }
}
