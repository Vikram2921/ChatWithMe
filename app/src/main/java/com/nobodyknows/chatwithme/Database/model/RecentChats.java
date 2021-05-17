package com.nobodyknows.chatwithme.Database.model;

public class RecentChats {
    public static final String TABLE_NAME = "RecentChatsDB";

    private int id;
    private String username;
    private String lastMessageId;
    private String lastMessageDate;

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_LAST_MESSAGE_ID = "lastMessageId";
    public static final String COLUMN_LAST_MESSAGE_DATE = "lastMessageDate";


    public static final String getTableName() {
        return TABLE_NAME;
    }

    public static final String getCreateTableQuery() {
        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+getTableName()+"("
                + COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME +" TEXT NOT NULL UNIQUE,"
                + COLUMN_LAST_MESSAGE_DATE +" TEXT,"
                + COLUMN_LAST_MESSAGE_ID +" TEXT"
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

    public String getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(String lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public String getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(String lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }
}
