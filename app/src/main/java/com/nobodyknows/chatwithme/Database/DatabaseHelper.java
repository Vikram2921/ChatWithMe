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
import com.nobodyknows.chatwithme.DTOS.CallModel;
import com.nobodyknows.chatwithme.DTOS.SecurityDTO;
import com.nobodyknows.chatwithme.DTOS.UserListItemDTO;
import com.nobodyknows.chatwithme.Database.model.CallsDB;
import com.nobodyknows.chatwithme.Database.model.RecentChats;
import com.nobodyknows.chatwithme.Database.model.SecurityDB;
import com.nobodyknows.chatwithme.Database.model.UsersDB;
import com.nobodyknows.chatwithme.services.MessageMaker;

import java.util.ArrayList;
import java.util.Date;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "NOBODYKNOW_CHATS";
    private String roomId;
    private Helper helper;
    private Context context;
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context= context;
        createTable();
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
        db.execSQL(CallsDB.getCreateTableQuery());
        db.execSQL(SecurityDB.getCreateTableQuery());
    }
    public void createTable(String roomId) {
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RecentChats.getTableName());
        db.execSQL("DROP TABLE IF EXISTS " + UsersDB.getTableName());
        db.execSQL("DROP TABLE IF EXISTS " + CallsDB.getTableName());
        db.execSQL("DROP TABLE IF EXISTS " + SecurityDB.getTableName());
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
        delete(db,CallsDB.getTableName());
        delete(db,SecurityDB.getTableName());
    }

    public void clearAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        clear(db,RecentChats.getTableName());
        clear(db,UsersDB.getTableName());
        clear(db,CallsDB.getTableName());
        clear(db,SecurityDB.getTableName());
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
                userListItemDTO.setMuted(user.getMuted());
                userListItemDTO.setBlocked(user.getBlocked());
                userListItemDTO.setCurrentStatus(user.getCurrentStatus());
                String lastMessageId = cursor.getString(cursor.getColumnIndex(RecentChats.COLUMN_LAST_MESSAGE_ID));
                userListItemDTO.setLastMessage(MessageMaker.getDatabaseHelperChat().getMessage(lastMessageId,MessageMaker.createRoomId(userListItemDTO.getContactNumber())));
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
        values.put(UsersDB.COLUMN_USERNAME,user.getUsername());
        values.put(UsersDB.COLUMN_BIO,user.getBio());
        values.put(UsersDB.COLUMN_DOB,MessageMaker.formatDate(user.getDateOfBirth(),"yyyy-MM-dd"));
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
        user.setUsername(cursor.getString(cursor.getColumnIndex(UsersDB.COLUMN_USERNAME)));
        user.setBio(cursor.getString(cursor.getColumnIndex(UsersDB.COLUMN_BIO)));
        user.setDateOfBirth(MessageMaker.StringToDate(cursor.getString(cursor.getColumnIndex(UsersDB.COLUMN_DOB)),"yyyy-MM-dd"));
        user.setVerified(MessageMaker.convertBoolean(cursor.getString(cursor.getColumnIndex(UsersDB.COLUMN_VERIFIED))));
        user.setMuted(MessageMaker.convertBoolean(cursor.getString(cursor.getColumnIndex(UsersDB.COLUMN_MUTED))));
        user.setBlocked(MessageMaker.convertBoolean(cursor.getString(cursor.getColumnIndex(UsersDB.COLUMN_BLOCKED))));
        user.setBlockedBy(cursor.getString(cursor.getColumnIndex(UsersDB.COLUMN_BLOCKED_BY)));
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

    public Boolean updateUserInfo(User user) {
        SQLiteDatabase db  = this.getWritableDatabase();
        Boolean isUpdated = db.update(UsersDB.getTableName(), getUserContentValue(user),UsersDB.COLUMN_CONTACT_NUMBER+"=?",new String[]{user.getContactNumber()}) > 0;
        if(db.isOpen()) {
            db.close();
        }
        return isUpdated;
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

    public void setBlockStatus(String blockNumber, String blockBy,Boolean blockStatus) {
        SQLiteDatabase db  = this.getWritableDatabase();
        String strSQL = "UPDATE "+UsersDB.getTableName()+" SET "+UsersDB.COLUMN_BLOCKED+" = '"+MessageMaker.convertBoolean(blockStatus)+"',"+UsersDB.COLUMN_BLOCKED_BY+"='"+blockBy+"' WHERE "+UsersDB.COLUMN_CONTACT_NUMBER+" = "+ blockNumber;
        db.execSQL(strSQL);
        db.close();
    }

    public void setMuteStatus(String username,Boolean muted) {
        SQLiteDatabase db  = this.getWritableDatabase();
        String strSQL = "UPDATE "+UsersDB.getTableName()+" SET "+UsersDB.COLUMN_MUTED+" = '"+MessageMaker.convertBoolean(muted)+"' WHERE "+UsersDB.COLUMN_CONTACT_NUMBER+" = "+ username;
        db.execSQL(strSQL);
        db.close();
    }


    //Calls DB CRUD STOP HERE

    public long insertInCalls(CallModel callModel) {
        SQLiteDatabase db  = this.getWritableDatabase();
        long id = 0;
        id = db.insert(CallsDB.getTableName(),null,getCallContentValue(callModel));
        db.close();
        return id;
    }


    private ContentValues getCallContentValue(CallModel callModel) {
        ContentValues values = new ContentValues();
        values.put(CallsDB.COLUMN_USERNAME,callModel.getUsername());
        values.put(CallsDB.COLUMN_CALL_TYPE,callModel.getCalltype());
        values.put(CallsDB.COLUMN_CALL_DURATION,callModel.getCallDuration());
        values.put(CallsDB.COLUMN_END_CAUSE,callModel.getEndCause());
        values.put(CallsDB.COLUMN_ENDED_TIME,callModel.getEndedTime());
        values.put(CallsDB.COLUMN_ESTABLISHED_TIME,callModel.getEstablishedTime());
        values.put(CallsDB.COLUMN_STARTED_TIME,callModel.getStartedTime());
        values.put(CallsDB.COLUMN_ERROR,callModel.getError());
        values.put(CallsDB.COLUMN_CALL_ID,callModel.getCallId());
        values.put(CallsDB.COLUMN_IS_INCOMING_CALL,MessageMaker.convertBoolean(callModel.getIncomingCall()));
        return values;
    }

    private CallModel convertToCall(Cursor cursor) {
        CallModel callModel = new CallModel();
        callModel.setUsername(cursor.getString(cursor.getColumnIndex(CallsDB.COLUMN_USERNAME)));
        callModel.setCalltype(cursor.getString(cursor.getColumnIndex(CallsDB.COLUMN_CALL_TYPE)));
        callModel.setCallDuration(cursor.getInt(cursor.getColumnIndex(CallsDB.COLUMN_CALL_DURATION)));
        callModel.setEndCause(cursor.getString(cursor.getColumnIndex(CallsDB.COLUMN_END_CAUSE)));
        callModel.setEndedTime(cursor.getLong(cursor.getColumnIndex(CallsDB.COLUMN_ENDED_TIME)));
        callModel.setEstablishedTime(cursor.getLong(cursor.getColumnIndex(CallsDB.COLUMN_ESTABLISHED_TIME)));
        callModel.setStartedTime(cursor.getLong(cursor.getColumnIndex(CallsDB.COLUMN_STARTED_TIME)));
        callModel.setError(cursor.getString(cursor.getColumnIndex(CallsDB.COLUMN_ERROR)));
        callModel.setCallId(cursor.getString(cursor.getColumnIndex(CallsDB.COLUMN_CALL_ID)));
        callModel.setIncomingCall(MessageMaker.convertBoolean(cursor.getString(cursor.getColumnIndex(CallsDB.COLUMN_IS_INCOMING_CALL))));
        return callModel;
    }

    public Boolean updateCallInfo(CallModel callModel) {
        SQLiteDatabase db  = this.getWritableDatabase();
        Boolean isUpdated = db.update(CallsDB.getTableName(), getCallContentValue(callModel),CallsDB.COLUMN_CALL_ID+"=?",new String[]{callModel.getCallId()}) > 0;
        if(db.isOpen()) {
            db.close();
        }
        return isUpdated;
    }

    public CallModel getCallObject(String callId) {
        String selectQuery = "SELECT  * FROM " + CallsDB.getTableName() + " WHERE " +
                CallsDB.COLUMN_CALL_ID + " = '"+callId+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        CallModel callModel  = null;
        if (cursor.moveToFirst()) {
            do {
                callModel = convertToCall(cursor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return callModel;
    }

    public ArrayList<CallModel> getAllCalls() {
        ArrayList<CallModel> callModels = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + CallsDB.getTableName()+" ORDER BY "+CallsDB.COLUMN_ID+" DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        CallModel callModel  = null;
        if (cursor.moveToFirst()) {
            do {
                callModel = convertToCall(cursor);
                callModels.add(callModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return callModels;
    }

    /**Security update **/

    public long insertInSecurity(String roomId, SecurityDTO securityDTO) {
        long id = 0;
        if(securityDTO != null) {
            SQLiteDatabase db  = this.getWritableDatabase();
            if(!isSecurityInfoExist(roomId,db)) {
                id = db.insert(SecurityDB.getTableName(),null,getSecurityContentValue(roomId,securityDTO));
            }
            db.close();
        }
        return id;
    }

    private boolean isSecurityInfoExist(String roomId, SQLiteDatabase db) {
        String selectQuery = "SELECT  * FROM " + SecurityDB.getTableName() + " WHERE " +
                SecurityDB.COLUMN_ROOM_ID + " = '"+roomId+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.getCount() <=0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }


    private ContentValues getSecurityContentValue(String roomId, SecurityDTO securityDTO) {
        ContentValues values = new ContentValues();
        values.put(SecurityDB.COLUMN_ROOM_ID,roomId);
        values.put(SecurityDB.COLUMN_SECURITY_KEY,securityDTO.getSecurityKey());
        values.put(SecurityDB.COLUMN_LAST_CHANGED_BY,securityDTO.getLastChangedBy());
        values.put(SecurityDB.COLUMN_CREATED_ON,MessageMaker.getConvertedDate(securityDTO.getCreatedOn()));
        return values;
    }

    public String getSecurityKey(String roomId) {
        String key= "";
        String selectQuery = "SELECT  * FROM " + SecurityDB.getTableName() + " WHERE " +
                SecurityDB.COLUMN_ROOM_ID + " = '"+roomId+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                key  = cursor.getString(cursor.getColumnIndex(SecurityDB.COLUMN_SECURITY_KEY));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return key;
    }

    public Boolean deleteSecurityInfo(String roomId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Boolean isDeleted = db.delete(SecurityDB.getTableName(),SecurityDB.COLUMN_ROOM_ID+"=?",new String[]{roomId}) > 0;
        return isDeleted;
    }
}
