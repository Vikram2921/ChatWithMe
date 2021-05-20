package com.nobodyknows.chatwithme.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.NobodyKnows.chatlayoutview.Model.Message;
import com.bumptech.glide.Glide;
import com.nobodyknows.chatwithme.R;

import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class MessageMaker {

    public static String createMessageId(String myid) {
        String id =""+new Date().getTime();
        return id;
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
        return message;
    }
}
