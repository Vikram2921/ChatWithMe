package com.nobodyknows.chatwithme.services;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class MessageMaker {

    public static String createMessageId(String myid) {
        String id = myid+""+new Date().getTime();
        return id;
    }

    public static String getFromSharedPrefrences(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ChatWithMe",MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
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


}
