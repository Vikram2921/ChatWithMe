package com.NobodyKnows.chatlayoutview.DatabaseHelper.Models;

public class ContactDB {
    public static final String TABLE_NAME = "ContactsDB";

    private int id;
    private String messageId;
    private String roomId;
    private String name;
    private String contactNumbers;
    private String profileUrl;
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_MESSAGE_ID = "messageId";
    public static final String COLUMN_ROOM_ID = "roomid";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CONTACTNUMBER = "number";
    public static final String COLUMN_PROFILE_URL = "profile";


    public static final String getTableName() {
        return TABLE_NAME;
    }

    public static final String getCreateTableQuery() {
        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+getTableName()+"("
                + COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_MESSAGE_ID +" TEXT NOT NULL,"
                + COLUMN_ROOM_ID +" TEXT NOT NULL,"
                + COLUMN_NAME +" TEXT,"
                + COLUMN_CONTACTNUMBER +" TEXT,"
                + COLUMN_PROFILE_URL +" TEXT"
                +")";
        return CREATE_TABLE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumbers() {
        return contactNumbers;
    }

    public void setContactNumbers(String contactNumbers) {
        this.contactNumbers = contactNumbers;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
