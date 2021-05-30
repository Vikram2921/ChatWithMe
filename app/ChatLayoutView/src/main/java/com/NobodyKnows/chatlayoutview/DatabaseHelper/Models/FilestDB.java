package com.NobodyKnows.chatlayoutview.DatabaseHelper.Models;

public class FilestDB {
    public static final String TABLE_NAME = "FilesDB";

    private int id;
    private String messageId;
    private String roomId;
    private String fileId;
    private String url;
    private String localPath;
    private String previewUrl;
    private String name;
    private String extensions;
    private String fileInfo;
    private int size;
    private int duration;
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_MESSAGE_ID = "messageId";
    public static final String COLUMN_ROOM_ID = "roomid";
    public static final String COLUMN_FILE_ID = "fileId";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_LOCALPATH = "localPath";
    public static final String COLUMN_PREVIEW_URL = "previewUrl";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EXTENSION = "extensions";
    public static final String COLUMN_FILEINFO = "fileInfo";
    public static final String COLUMN_SIZE = "size";
    public static final String COLUMN_DURATION = "duration";


    public static final String getTableName() {
        return TABLE_NAME;
    }

    public static final String getCreateTableQuery() {
        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+getTableName()+"("
                + COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_MESSAGE_ID +" TEXT NOT NULL,"
                + COLUMN_ROOM_ID +" TEXT NOT NULL,"
                + COLUMN_FILE_ID +" TEXT NOT NULL,"
                + COLUMN_URL +" TEXT NOT NULL,"
                + COLUMN_LOCALPATH +" TEXT,"
                + COLUMN_PREVIEW_URL +" TEXT,"
                + COLUMN_NAME +" TEXT,"
                + COLUMN_EXTENSION +" TEXT,"
                + COLUMN_FILEINFO +" TEXT,"
                + COLUMN_SIZE +" INTEGER,"
                + COLUMN_DURATION +" INTEGER"
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

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtensions() {
        return extensions;
    }

    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }

    public String getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(String fileInfo) {
        this.fileInfo = fileInfo;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
