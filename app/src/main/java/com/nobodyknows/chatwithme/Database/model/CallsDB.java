package com.nobodyknows.chatwithme.Database.model;

public class CallsDB {
    public static final String TABLE_NAME = "CallDB";

    private int id;
    private String username;
    private String calltype;
    private Integer callDuration;
    private String endCause;
    private String endedTime;
    private String establishedTime;
    private String startedTime;
    private String error;
    private String callId;
    private String isIncomingCall;

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_CALL_TYPE = "calltype";
    public static final String COLUMN_CALL_DURATION = "callDuration";
    public static final String COLUMN_END_CAUSE = "endCause";
    public static final String COLUMN_ENDED_TIME = "endedTime";
    public static final String COLUMN_ESTABLISHED_TIME = "establishedTime";
    public static final String COLUMN_STARTED_TIME = "startedTime";
    public static final String COLUMN_ERROR = "error";
    public static final String COLUMN_CALL_ID = "callId";
    public static final String COLUMN_IS_INCOMING_CALL = "isIncomingCall";


    public static final String getTableName() {
        return TABLE_NAME;
    }

    public static final String getCreateTableQuery() {
        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+getTableName()+"("
                + COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME +" TEXT NOT NULL,"
                + COLUMN_CALL_TYPE +" TEXT,"
                + COLUMN_CALL_DURATION +" INTEGER,"
                + COLUMN_END_CAUSE +" TEXT,"
                + COLUMN_ENDED_TIME +" TEXT,"
                + COLUMN_ESTABLISHED_TIME +" TEXT,"
                + COLUMN_STARTED_TIME +" TEXT,"
                + COLUMN_ERROR +" TEXT,"
                + COLUMN_CALL_ID +" TEXT NOT NULL,"
                + COLUMN_IS_INCOMING_CALL +" TEXT"
                +")";
        return CREATE_TABLE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getEndedTime() {
        return endedTime;
    }

    public void setEndedTime(String endedTime) {
        this.endedTime = endedTime;
    }

    public String getEstablishedTime() {
        return establishedTime;
    }

    public void setEstablishedTime(String establishedTime) {
        this.establishedTime = establishedTime;
    }

    public String getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(String startedTime) {
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

    public String getIsIncomingCall() {
        return isIncomingCall;
    }

    public void setIsIncomingCall(String isIncomingCall) {
        this.isIncomingCall = isIncomingCall;
    }
}
