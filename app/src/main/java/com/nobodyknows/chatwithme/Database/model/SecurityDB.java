package com.nobodyknows.chatwithme.Database.model;

public class SecurityDB {
    public static final String TABLE_NAME = "SecurityDB";

    private int id;
    private String roomid;
    private String securityKey;
    private String lastChangedBy;
    private String createdOn;

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ROOM_ID = "roomid";
    public static final String COLUMN_SECURITY_KEY = "securitykey";
    public static final String COLUMN_LAST_CHANGED_BY = "lastChangedBy";
    public static final String COLUMN_CREATED_ON = "createdOn";


    public static final String getTableName() {
        return TABLE_NAME;
    }

    public static final String getCreateTableQuery() {
        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+getTableName()+"("
                + COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ROOM_ID +" TEXT NOT NULL,"
                + COLUMN_SECURITY_KEY +" TEXT NOT NULL,"
                + COLUMN_LAST_CHANGED_BY +" TEXT NOT NULL,"
                + COLUMN_CREATED_ON +" TEXT"
                +")";
        return CREATE_TABLE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    public String getLastChangedBy() {
        return lastChangedBy;
    }

    public void setLastChangedBy(String lastChangedBy) {
        this.lastChangedBy = lastChangedBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }
}
