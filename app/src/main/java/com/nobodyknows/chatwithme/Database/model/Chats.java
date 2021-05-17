package com.nobodyknows.chatwithme.Database.model;

public class Chats {
    public static final String TABLE_NAME = "ChatDB";

    private int id;
    private String messageId;
    private String repliedMessageId;
    private String sender;
    private String receiver;
    private String roomId;
    private String message;
    private Integer messageType;
    private Integer messageStatus;
    private String createdTimestamp;
    private String updatedTimestamp;
    private String sentAt;
    private String receiveAt;
    private String seenAt;
    private Integer isRepliedMessage;

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_MESSAGE_ID = "messageId";
    public static final String COLUMN_REPLY_MESSAGE_ID = "repliedMessageId";
    public static final String COLUMN_SENDER = "sender";
    public static final String COLUMN_RECEIVER = "receiver";
    public static final String COLUMN_ROOM_ID = "roomId";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_MESSAGE_TYPE = "messageType";
    public static final String COLUMN_MESSAGE_STATUS = "messageStatus";
    public static final String COLUMN_CREATED_TIME = "createdTimestamp";
    public static final String COLUMN_UPDATED_TIME = "updatedTimestamp";
    public static final String COLUMN_SENTAT = "sentAt";
    public static final String COLUMN_RECEIVEAT = "receiveAt";
    public static final String COLUMN_SEENAT = "seenAt";
    public static final String COLUMN_IS_REPLY_MESSAGE = "isRepliedMessage";


    public static final String getTableName(String roomId) {
        return TABLE_NAME+"_"+roomId;
    }

    public static final String getCreateTableQuery(String roomId) {
        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+getTableName(roomId)+"("
                + COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_MESSAGE_ID +" TEXT NOT NULL UNIQUE,"
                + COLUMN_REPLY_MESSAGE_ID +" TEXT,"
                + COLUMN_SENDER +" TEXT NOT NULL,"
                + COLUMN_RECEIVER +" TEXT NOT NULL,"
                + COLUMN_ROOM_ID +" TEXT NOT NULL,"
                + COLUMN_MESSAGE +" TEXT,"
                + COLUMN_MESSAGE_TYPE +" INTEGER NOT NULL,"
                + COLUMN_MESSAGE_STATUS +" INTEGER NOT NULL,"
                + COLUMN_CREATED_TIME +" TEXT NOT NULL,"
                + COLUMN_UPDATED_TIME +" TEXT,"
                + COLUMN_SENTAT +" TEXT,"
                + COLUMN_RECEIVEAT +" TEXT,"
                + COLUMN_IS_REPLY_MESSAGE +" INTEGER,"
                + COLUMN_SEENAT +" TEXT"
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

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public String getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(String createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(String updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    public String getReceiveAt() {
        return receiveAt;
    }

    public void setReceiveAt(String receiveAt) {
        this.receiveAt = receiveAt;
    }

    public String getSeenAt() {
        return seenAt;
    }

    public void setSeenAt(String seenAt) {
        this.seenAt = seenAt;
    }

    public Integer getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(Integer messageStatus) {
        this.messageStatus = messageStatus;
    }

    public Integer getIsRepliedMessage() {
        return isRepliedMessage;
    }

    public void setIsRepliedMessage(Integer isRepliedMessage) {
        this.isRepliedMessage = isRepliedMessage;
    }

    public String getRepliedMessageId() {
        return repliedMessageId;
    }

    public void setRepliedMessageId(String repliedMessageId) {
        this.repliedMessageId = repliedMessageId;
    }
}
