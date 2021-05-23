package com.NobodyKnows.chatlayoutview.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import com.NobodyKnows.chatlayoutview.Constants.MessageStatus;
import com.NobodyKnows.chatlayoutview.Constants.MessageType;
import com.NobodyKnows.chatlayoutview.DatabaseHelper.Models.ContactDB;
import com.NobodyKnows.chatlayoutview.DatabaseHelper.Models.MessageDB;
import com.NobodyKnows.chatlayoutview.Interfaces.LastMessageUpdateListener;
import com.NobodyKnows.chatlayoutview.Model.Contact;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Services.LayoutService;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "NOBODYKNOW_CHATS";
    private Context context;
    private LastMessageUpdateListener lastMessageUpdateListener;
    public DatabaseHelper(@Nullable Context context,LastMessageUpdateListener lastMessageUpdateListener) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.lastMessageUpdateListener = lastMessageUpdateListener;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    public void deleteDatabase()  {
        context.deleteDatabase(DATABASE_NAME);
    }

    public void createTable(String roomId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(MessageDB.getCreateTableQuery(roomId));
        db.execSQL(ContactDB.getCreateTableQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void delete(SQLiteDatabase db,String tableName) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
    }

    public void clear(SQLiteDatabase db,String tableName) {
        db.execSQL("DELETE FROM " + tableName);
    }

    public void deleteAll(String roomId) {
        SQLiteDatabase db = this.getWritableDatabase();
        delete(db, MessageDB.getTableName(roomId));
        delete(db, ContactDB.getTableName());
    }

    public void clearAll(String roomId) {
        SQLiteDatabase db = this.getWritableDatabase();
        clear(db, MessageDB.getTableName(roomId));
        clear(db, ContactDB.getTableName());
    }

    /**
     * Recent Chats CRUD Operations Starts HERE
     * @param message
     * @return
     */
    public long insertInMessage(Message message,String roomId) {
        SQLiteDatabase db  = this.getWritableDatabase();
        long id = 0;
        createTable(roomId);
        if(!isMessageExists(message,roomId,db)) {
            id = db.insert(MessageDB.getTableName(roomId),null, getMessageContentValue(message));
            if(message.getMessageType() == MessageType.CONTACT_MULTIPLE || message.getMessageType() == MessageType.CONTACT_SINGLE) {
                insertInContacts(message.getContacts(),message.getRoomId(),message.getMessageId());
            }
        }
        db.close();
        this.lastMessageUpdateListener.onLastMessageAdded(message,roomId);
        return id;
    }

    private Boolean isMessageExists(Message message,String roomId, SQLiteDatabase db) {
        String selectQuery = "SELECT  * FROM " + MessageDB.getTableName(roomId) + " WHERE " +
                MessageDB.COLUMN_MESSAGE_ID + " = '"+message.getMessageId()+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.getCount() <=0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    private ContentValues getMessageContentValue(Message message) {
        ContentValues values = new ContentValues();
        values.put(MessageDB.COLUMN_MESSAGE_ID,message.getMessageId());
        values.put(MessageDB.COLUMN_SENDER,message.getSender());
        values.put(MessageDB.COLUMN_RECEIVER,message.getReceiver());
        values.put(MessageDB.COLUMN_MESSAGE,message.getMessage());
        values.put(MessageDB.COLUMN_ROOM_ID,message.getRoomId());
        values.put(MessageDB.COLUMN_MESSAGE_TYPE,message.getMessageType().ordinal());
        values.put(MessageDB.COLUMN_CREATED_TIME, LayoutService.getConvertedDate(message.getCreatedTimestamp()));
        values.put(MessageDB.COLUMN_UPDATED_TIME,LayoutService.getConvertedDate(message.getUpdateTimestamp()));
        values.put(MessageDB.COLUMN_SENTAT,LayoutService.getConvertedDate(message.getSentAt()));
        values.put(MessageDB.COLUMN_RECEIVEAT,LayoutService.getConvertedDate(message.getReceivedAt()));
        values.put(MessageDB.COLUMN_SEENAT,LayoutService.getConvertedDate(message.getSeenAt()));
        values.put(MessageDB.COLUMN_IS_REPLY_MESSAGE,LayoutService.convertBoolean(message.getIsRepliedMessage()));
        values.put(MessageDB.COLUMN_REPLY_MESSAGE_ID,message.getRepliedMessageId());
        values.put(MessageDB.COLUMN_MESSAGE_STATUS,message.getMessageStatus().ordinal());
        return values;
    }

    private Message convertToMessage(Cursor cursor) {
        Message message = new Message();
        message.setMessageId(cursor.getString(cursor.getColumnIndex(MessageDB.COLUMN_MESSAGE_ID)));
        message.setSender(cursor.getString(cursor.getColumnIndex(MessageDB.COLUMN_SENDER)));
        message.setReceiver(cursor.getString(cursor.getColumnIndex(MessageDB.COLUMN_RECEIVER)));
        message.setMessage(cursor.getString(cursor.getColumnIndex(MessageDB.COLUMN_MESSAGE)));
        message.setRoomId(cursor.getString(cursor.getColumnIndex(MessageDB.COLUMN_ROOM_ID)));
        message.setMessageType(MessageType.values()[cursor.getInt(cursor.getColumnIndex(MessageDB.COLUMN_MESSAGE_TYPE))]);
        message.setCreatedTimestamp(LayoutService.getConvertedDate(cursor.getString(cursor.getColumnIndex(MessageDB.COLUMN_CREATED_TIME))));
        message.setUpdateTimestamp(LayoutService.getConvertedDate(cursor.getString(cursor.getColumnIndex(MessageDB.COLUMN_UPDATED_TIME))));
        message.setSentAt(LayoutService.getConvertedDate(cursor.getString(cursor.getColumnIndex(MessageDB.COLUMN_SENTAT))));
        message.setReceivedAt(LayoutService.getConvertedDate(cursor.getString(cursor.getColumnIndex(MessageDB.COLUMN_RECEIVEAT))));
        message.setSeenAt(LayoutService.getConvertedDate(cursor.getString(cursor.getColumnIndex(MessageDB.COLUMN_SEENAT))));
        message.setIsRepliedMessage(LayoutService.convertBoolean(cursor.getString(cursor.getColumnIndex(MessageDB.COLUMN_IS_REPLY_MESSAGE))));
        message.setRepliedMessageId(cursor.getString(cursor.getColumnIndex(MessageDB.COLUMN_REPLY_MESSAGE_ID)));
        message.setMessageStatus(MessageStatus.values()[cursor.getInt(cursor.getColumnIndex(MessageDB.COLUMN_MESSAGE_STATUS))]);
        if(message.getMessageType() == MessageType.CONTACT_MULTIPLE || message.getMessageType() == MessageType.CONTACT_SINGLE) {
            message.setContacts(getContactListFor(message.getRoomId(),message.getMessageId()));
        }
        return message;
    }

    public Boolean updateMessage(Message message, String roomid) {
        SQLiteDatabase db  = this.getWritableDatabase();
        Boolean isUpdated = db.update(MessageDB.getTableName(roomid), getMessageContentValue(message),MessageDB.COLUMN_MESSAGE_ID+"=?",new String[]{message.getMessageId()}) > 0;
        if(db.isOpen()) {
            db.close();
        }
        return isUpdated;
    }

    public boolean deleteMessage(String messageid, String roomid) {
        SQLiteDatabase db  = this.getWritableDatabase();
        Boolean isDeleted = db.delete(MessageDB.getTableName(roomid),MessageDB.COLUMN_MESSAGE_ID+"=?",new String[]{messageid}) > 0;
        if(db.isOpen()) {
            db.close();
        }
        return isDeleted;
    }

    public ArrayList<Message> getAllMessages(String roomid) {
        ArrayList<Message> messages = new ArrayList<>();
        SQLiteDatabase db  = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + MessageDB.getTableName(roomid) + " ORDER BY " +
                MessageDB.COLUMN_MESSAGE_ID;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do{
                messages.add(convertToMessage(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return messages;
    }

    public Message getMessage(String messageId, String roomId) {
        SQLiteDatabase db  = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + MessageDB.getTableName(roomId) + " WHERE " +
                MessageDB.COLUMN_MESSAGE_ID + " = '"+messageId+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        Message message = null;
        if(cursor.moveToFirst()) {
            do{
               message = convertToMessage(cursor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return message;
    }

    public void deleteMessagesOf(String roomid) {
        SQLiteDatabase db = this.getWritableDatabase();
        delete(db,MessageDB.getTableName(roomid));
    }

    //Message CRUD ends HEre

    private long insertInContacts(List<Contact> contacts, String roomId,String messageId) {
        SQLiteDatabase db  = this.getWritableDatabase();
        long id = 0;
        for(Contact contact:contacts) {
            id = db.insert(ContactDB.getTableName(),null, getContactContentValue(contact,messageId,roomId));
        }
        db.close();
        return id;
    }

    private ContentValues getContactContentValue(Contact contact,String messageId,String roomId) {
        ContentValues values = new ContentValues();
        values.put(ContactDB.COLUMN_MESSAGE_ID,messageId);
        values.put(ContactDB.COLUMN_ROOM_ID,roomId);
        values.put(ContactDB.COLUMN_CONTACTNUMBER,contact.getContactNumbers());
        values.put(ContactDB.COLUMN_NAME,contact.getName());
        values.put(ContactDB.COLUMN_PROFILE_URL,contact.getProfileUrl());
        return values;
    }

    private Contact convertToContact(Cursor cursor) {
        Contact contact = new Contact();
        contact.setProfileUrl(cursor.getString(cursor.getColumnIndex(ContactDB.COLUMN_PROFILE_URL)));
        contact.setContactNumbers(cursor.getString(cursor.getColumnIndex(ContactDB.COLUMN_CONTACTNUMBER)));
        contact.setName(cursor.getString(cursor.getColumnIndex(ContactDB.COLUMN_NAME)));
        return contact;
    }

    private ArrayList<Contact> getContactListFor(String roomId, String messageId) {
        ArrayList<Contact> contacts = new ArrayList<>();
        SQLiteDatabase db  = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + ContactDB.getTableName() + " WHERE " +
                ContactDB.COLUMN_MESSAGE_ID + " = '"+messageId+"' AND "+ContactDB.COLUMN_ROOM_ID+" = '"+roomId+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do{
                contacts.add(convertToContact(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contacts;
    }


}
