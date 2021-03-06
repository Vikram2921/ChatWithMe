package com.NobodyKnows.chatlayoutview.Model;

import android.view.View;


import com.NobodyKnows.chatlayoutview.Constants.MessageStatus;
import com.NobodyKnows.chatlayoutview.Constants.MessageType;
import com.NobodyKnows.chatlayoutview.Constants.UploadStatus;

import java.util.ArrayList;
import java.util.Date;

public class Message implements Cloneable {
    private String messageId;
    private String sender;
    private String receiver;
    private String message;
    private String roomId;
    private UploadStatus uploadStatus = UploadStatus.NOT_STARTED;
    private MessageType messageType = MessageType.TEXT;
    private Date createdTimestamp = new Date();
    private Date updateTimestamp = new Date();
    private Date sentAt = new Date();
    private Date receivedAt;
    private Date seenAt;
    private Boolean isRepliedMessage = false;
    private String repliedMessageId = "";
    private MessageStatus messageStatus = MessageStatus.SENT;
    private MessageConfiguration messageConfiguration;
    private ArrayList<SharedFile> sharedFiles = new ArrayList<>();
    private ArrayList<Contact> contacts = new ArrayList<>();
    private Message replyMessage = null;

    public UploadStatus getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(UploadStatus uploadStatus) {
        this.uploadStatus = uploadStatus;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<SharedFile> getSharedFiles() {
        return sharedFiles;
    }

    public void setSharedFiles(ArrayList<SharedFile> sharedFiles) {
        this.sharedFiles = sharedFiles;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Date getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Date updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public Date getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Date receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Date getSeenAt() {
        return seenAt;
    }

    public void setSeenAt(Date seenAt) {
        this.seenAt = seenAt;
    }

    public MessageConfiguration getMessageConfiguration() {
        return messageConfiguration;
    }

    public void setMessageConfiguration(MessageConfiguration messageConfiguration) {
        this.messageConfiguration = messageConfiguration;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void addSharedFile(SharedFile sharedFile) {
        this.sharedFiles.add(sharedFile);
    }

    public void addContact(Contact contact) {
        this.contacts.add(contact);
    }

    public MessageStatus getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

    public Boolean getIsRepliedMessage() {
        return isRepliedMessage;
    }

    public void setIsRepliedMessage(Boolean repliedMessage) {
        isRepliedMessage = repliedMessage;
    }

    public String getRepliedMessageId() {
        return repliedMessageId;
    }

    public void setRepliedMessageId(String repliedMessageId) {
        this.repliedMessageId = repliedMessageId;
    }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    public Message getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(Message replyMessage) {
        this.replyMessage = replyMessage;
    }

    public Message clone()
    {
        try
        {
            return (Message) super.clone();
        }
        catch( CloneNotSupportedException e )
        {
            return null;
        }
    }
}
