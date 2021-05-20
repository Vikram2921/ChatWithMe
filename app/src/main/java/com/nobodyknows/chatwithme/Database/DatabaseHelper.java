package com.nobodyknows.chatwithme.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.NobodyKnows.chatlayoutview.Services.Helper;
import com.nobodyknows.chatwithme.DTOS.UserListItemDTO;
import com.nobodyknows.chatwithme.Database.model.RecentChats;
import com.nobodyknows.chatwithme.Database.model.UsersDB;
import com.nobodyknows.chatwithme.services.MessageMaker;

import java.util.ArrayList;
import java.util.Date;

import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.databaseHelperChat;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "NOBODYKNOW_CHATS";
    private String roomId;
    private Helper helper;
    private Context context;
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context= context;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setHelper(Helper helper) {
        this.helper = helper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    public void deleteDatabase()  {
        context.deleteDatabase(DATABASE_NAME);
    }

    public void createTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(RecentChats.getCreateTableQuery());
        db.execSQL(UsersDB.getCreateTableQuery());
    }
    public void createTable(String roomId) {
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RecentChats.getTableName());
        db.execSQL("DROP TABLE IF EXISTS " + UsersDB.getTableName());
        onCreate(db);
    }

    public void delete(SQLiteDatabase db,String tableName) {
        db.execSQL("DROP THistoryABLE IF EXISTS " + tableName);
    }

    public void clear(SQLiteDatabase db,String tableName) {
        db.execSQL("DELETE FROM " + tableName);
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        delete(db,RecentChats.getTableName());
        delete(db,UsersDB.getTableName());
    }

    public void clearAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        clear(db,RecentChats.getTableName());
        clear(db,UsersDB.getTableName());
    }

    /**
     * Recent Chats CRUD Operations Starts HERE
     * @param username
     * @return
     */
    public long insertInRecentChats(String username, String lastMessageId, Date lastMessageDate) {
        SQLiteDatabase db  = this.getWritableDatabase();
        long id = 0;
        if(!isRecentChatExist(username,db)) {
            id = db.insert(RecentChats.getTableName(),null,getRecentChatContentValue(username,lastMessageId,lastMessageDate));
        }
        db.close();
        return id;
    }

    private Boolean isRecentChatExist(String username,SQLiteDatabase db) {
        String selectQuery = "SELECT  * FROM " + RecentChats.getTableName() + " WHERE " +
                RecentChats.COLUMN_USERNAME + " = '"+username+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.getCount() <=0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    private ContentValues getRecentChatContentValue(String username, String lastMessageId, Date lastMessageDate) {
        ContentValues values = new ContentValues();
        values.put(RecentChats.COLUMN_USERNAME,username);
        values.put(RecentChats.COLUMN_LAST_MESSAGE_ID,lastMessageId);
        values.put(RecentChats.COLUMN_LAST_MESSAGE_DATE,MessageMaker.getConvertedDate(lastMessageDate));
        return values;
    }

    public ArrayList<UserListItemDTO> getRecentChatUsers(Context context) {
        ArrayList<UserListItemDTO> recentChatList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + RecentChats.getTableName() + " ORDER BY " +
                RecentChats.COLUMN_LAST_MESSAGE_DATE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        UserListItemDTO userListItemDTO  = null;
        User user = null;
        if (cursor.moveToFirst()) {
            do {
                userListItemDTO = new UserListItemDTO();
                userListItemDTO.setContactNumber(cursor.getString(cursor.getColumnIndex(RecentChats.COLUMN_USERNAME)));
                user = getUser(userListItemDTO.getContactNumber());
                userListItemDTO.setName(user.getName());
                userListItemDTO.setVerified(user.getVerified());
                userListItemDTO.setLastOnline(user.getLastOnline());
                userListItemDTO.setStatus(user.getStatus());
                userListItemDTO.setProfileUrl(user.getProfileUrl());
                userListItemDTO.setCurrentStatus(user.getCurrentStatus());
                String lastMessageId = cursor.getString(cursor.getColumnIndex(RecentChats.COLUMN_LAST_MESSAGE_ID));
                userListItemDTO.setLastMessage(databaseHelperChat.getMessage(lastMessageId,MessageMaker.createRoomId(context,userListItemDTO.getContactNumber())));
                recentChatList.add(userListItemDTO);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recentChatList;
    }

    public Boolean deleteRecentChat(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        Boolean isDeleted = db.delete(RecentChats.getTableName(),RecentChats.COLUMN_USERNAME+"=?",new String[]{username}) > 0;
        return isDeleted;
    }

    // Recent Chats CRUD Ends Here
    /**
     * UserDb CRUD Start Here
     */
    public long insertInUser(User user) {
        SQLiteDatabase db  = this.getWritableDatabase();
        long id = 0;
        if(!isUserExist(user.getContactNumber(),db)) {
            id = db.insert(UsersDB.getTableName(),null,getUserContentValue(user));
        }
        db.close();
        return id;
    }

    private Boolean isUserExist(String username,SQLiteDatabase db) {
        String selectQuery = "SELECT  * FROM " + UsersDB.getTableName() + " WHERE " +
                UsersDB.COLUMN_CONTACT_NUMBER + " = '"+username+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.getCount() <=0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public Boolean isUserExist(String username) {
        SQLiteDatabase db  = this.getWritableDatabase();
        return isUserExist(username,db);
    }

    private ContentValues getUserContentValue(User user) {
        ContentValues values = new ContentValues();
        values.put(UsersDB.COLUMN_CONTACT_NUMBER,user.getContactNumber());
        values.put(UsersDB.COLUMN_NAME,user.getName());
        values.put(UsersDB.COLUMN_STATUS,user.getStatus());
        values.put(UsersDB.COLUMN_PROFILE_URL,user.getProfileUrl());
        values.put(UsersDB.COLUMN_COLOR_CODE,user.getColorCode());
        values.put(UsersDB.COLUMN_VERIFIED,MessageMaker.convertBoolean(user.getVerified()));
        values.put(UsersDB.COLUMN_MUTED,MessageMaker.convertBoolean(user.getMuted()));
        values.put(UsersDB.COLUMN_BLOCKED,MessageMaker.convertBoolean(user.getBlocked()));
        values.put(UsersDB.COLUMN_LAST_ONLINE, MessageMaker.getConvertedDate(user.getLastOnline()));
        values.put(UsersDB.COLUMN_CURRENT_STATUS,user.getCurrentStatus());
        return values;
    }

    public User getUser(String contactNumber) {
        String selectQuery = "SELECT  * FROM " + UsersDB.getTableName() + " WHERE " +
                UsersDB.COLUMN_CONTACT_NUMBER + " = '"+contactNumber+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        User user  = null;
        if (cursor.moveToFirst()) {
            do {
                user = convertToUser(cursor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return user;
    }

    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + UsersDB.getTableName();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        User user  = null;
        if (cursor.moveToFirst()) {
            do {
                user = convertToUser(cursor);
                users.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return users;
    }

    private User convertToUser(Cursor cursor) {
        User user = new User();
        user.setContactNumber(cursor.getString(cursor.getColumnIndex(UsersDB.COLUMN_CONTACT_NUMBER)));
        user.setName(cursor.getString(cursor.getColumnIndex(UsersDB.COLUMN_NAME)));
        user.setStatus(cursor.getString(cursor.getColumnIndex(UsersDB.COLUMN_STATUS)));
        user.setProfileUrl(cursor.getString(cursor.getColumnIndex(UsersDB.COLUMN_PROFILE_URL)));
        user.setColorCode(cursor.getInt(cursor.getColumnIndex(UsersDB.COLUMN_COLOR_CODE)));
        user.setVerified(MessageMaker.convertBoolean(cursor.getString(cursor.getColumnIndex(UsersDB.COLUMN_VERIFIED))));
        user.setMuted(MessageMaker.convertBoolean(cursor.getString(cursor.getColumnIndex(UsersDB.COLUMN_MUTED))));
        user.setBlocked(MessageMaker.convertBoolean(cursor.getString(cursor.getColumnIndex(UsersDB.COLUMN_BLOCKED))));
        user.setLastOnline(MessageMaker.getConvertedDate(cursor.getString(cursor.getColumnIndex(UsersDB.COLUMN_LAST_ONLINE))));
        user.setCurrentStatus(cursor.getString(cursor.getColumnIndex(UsersDB.COLUMN_CURRENT_STATUS)));
        return user;
    }

    public void updateUserLastMessage(Message message) {
        SQLiteDatabase db  = this.getWritableDatabase();
        String strSQL = "UPDATE "+RecentChats.getTableName()+" SET "+RecentChats.COLUMN_LAST_MESSAGE_ID+" = '"+message.getMessageId()+"',"+RecentChats.COLUMN_LAST_MESSAGE_DATE+"='"+message.getSentAt().getTime()+"' WHERE "+RecentChats.COLUMN_USERNAME+" = "+ message.getReceiver();
        db.execSQL(strSQL);
        db.close();
    }

    public Boolean deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        Boolean isDeleted = db.delete(UsersDB.getTableName(),UsersDB.COLUMN_CONTACT_NUMBER+"=?",new String[]{username}) > 0;
        return isDeleted;
    }

    public void clearLastMessage(String username) {
        SQLiteDatabase db  = this.getWritableDatabase();
        String strSQL = "UPDATE "+RecentChats.getTableName()+" SET "+RecentChats.COLUMN_LAST_MESSAGE_ID+" = '',"+RecentChats.COLUMN_LAST_MESSAGE_DATE+"='' WHERE "+RecentChats.COLUMN_USERNAME+" = "+ username;
        db.execSQL(strSQL);
        db.close();

    }


    //USER DB CRUD STOP HERE


}
