package com.nobodyknows.chatwithme.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.NobodyKnows.chatlayoutview.Constants.MessageType;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.bumptech.glide.Glide;
import com.nobodyknows.chatwithme.Activities.ChatRoom;
import com.nobodyknows.chatwithme.R;

import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.databaseHelper;
import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.databaseHelperChat;

public class MessageMaker {

    private static String myNumber ="";
    public static String createMessageId(String myid) {
        String id =""+new Date().getTime();
        return id;
    }

    public static String getMyNumber() {
        return myNumber;
    }

    public static void setMyNumber(String myNumber) {
        MessageMaker.myNumber = myNumber;
    }

    public static String getFromSharedPrefrences(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ChatWithMe",MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }

    public static Boolean getFromSharedPrefrencesBoolean(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ChatWithMe",MODE_PRIVATE);
        return sharedPreferences.getBoolean(key,false);
    }

    public static String createRoomId(Context context, String freindusername) {
        String myusername = getFromSharedPrefrences(context,"number");
        String sub1=myusername.substring(myusername.length() - 5);
        String sub2=freindusername.substring(freindusername.length() - 5);
        int mn= Integer.parseInt(sub1);
        int fn= Integer.parseInt(sub2);
        if(fn<mn) {
            return freindusername+myusername;
        }
        return myusername+freindusername;
    }

    public static String getConvertedDate(Date date) {
        if(date != null) {
            return date.toString();
        }
        return "";
    }

    public static Date getConvertedDate(String date) {
        if(date.length() > 0) {
            Date newdate = new Date(date);
            return newdate;
        }
        return null;
    }

    public static boolean convertBoolean(String value) {
        if(value.equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public static String convertBoolean(Boolean value) {
        if(value) {
            return "true";
        }
        return "false";
    }


    public static void showNotFound(ConstraintLayout constraintLayout) {
        if(constraintLayout.getVisibility() == View.GONE) {
            constraintLayout.setVisibility(View.VISIBLE);
        }
    }

    public static void hideNotFound(ConstraintLayout constraintLayout) {
        if(constraintLayout.getVisibility() == View.VISIBLE) {
            constraintLayout.setVisibility(View.GONE);
        }
    }

    public static void loadProfile(Context context,String profileUrl, ImageView profile) {
        if(profileUrl != null && profileUrl.length() > 0 && !profileUrl.equalsIgnoreCase("NO_PROFILE")) {
            Glide.with(context).load(profileUrl).into(profile);
        } else {
            Glide.with(context).load(R.drawable.profile).into(profile);
        }
    }

    public static Message filterMessage(Message message) {
        if(message.getMessageType() == MessageType.BLOCKED) {
            if(!message.getSender().equalsIgnoreCase(myNumber)) {
                databaseHelper.setBlockStatus(message.getSender(),message.getSender(),true);
            }
            return null;
        } else if(message.getMessageType() == MessageType.UNBLOCKED) {
            if(!message.getSender().equalsIgnoreCase(myNumber)) {
                databaseHelper.setBlockStatus(message.getSender(),message.getSender(),false);
            }
            return null;
        } else if(message.getMessageType() == MessageType.UNFREIND) {
            if(!message.getSender().equalsIgnoreCase(myNumber)) {
                databaseHelper.deleteUser(message.getSender());
                databaseHelperChat.deleteMessagesOf(message.getRoomId());
                databaseHelper.deleteRecentChat(message.getSender());
            }
            return null;
        }
        return message;
    }

    public static void startChatroom(Context context, String username) {
        User user =databaseHelper.getUser(username);
        Intent intent = new Intent(context, ChatRoom.class);
        intent.putExtra("username",user.getContactNumber());
        intent.putExtra("name",user.getName());
        intent.putExtra("lastOnlineStatus",user.getCurrentStatus());
        intent.putExtra("verified",user.getVerified());
        intent.putExtra("blocked",user.getBlocked());
        intent.putExtra("roomid", MessageMaker.createRoomId(context,user.getContactNumber()));
        intent.putExtra("profile",user.getProfileUrl());
        context.startActivity(intent);
    }
}
