package com.nobodyknows.chatwithme.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.NobodyKnows.chatlayoutview.Constants.MessageType;
import com.NobodyKnows.chatlayoutview.Model.Contact;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.bumptech.glide.Glide;
import com.nobodyknows.chatwithme.Activities.AudioCall;
import com.nobodyknows.chatwithme.Activities.ChatRoom;
import com.nobodyknows.chatwithme.DTOS.UserListItemDTO;
import com.nobodyknows.chatwithme.R;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.callClient;
import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.databaseHelper;
import static com.nobodyknows.chatwithme.Fragments.ChatFragment.notfound;
import static com.nobodyknows.chatwithme.Fragments.ChatFragment.recyclerViewAdapter;
import static com.nobodyknows.chatwithme.Fragments.ChatFragment.userListItemDTOMap;
import static com.nobodyknows.chatwithme.Fragments.ChatFragment.userListItems;

public class MessageMaker {

    private static String myNumber ="";
    private static Call currentCallRef;
    public static String createMessageId(String myid) {
        String id =""+new Date().getTime();
        return id;
    }

    public static Call getCurrentCallRef() {
        return currentCallRef;
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

    public static String createRoomId(String freindusername) {
        String sub1=myNumber.substring(myNumber.length() - 5);
        String sub2=freindusername.substring(freindusername.length() - 5);
        int mn= Integer.parseInt(sub1);
        int fn= Integer.parseInt(sub2);
        if(fn<mn) {
            return freindusername+myNumber;
        }
        return myNumber+freindusername;
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
    public static void loadProfile(Context context,String profileUrl, CircleImageView profile) {
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
        } 
        return message;
    }

    public static void startChatroom(Context context, String username) {
        User user =databaseHelper.getUser(username);
        Intent intent = new Intent(context, ChatRoom.class);
        intent.putExtra("username",user.getContactNumber());
        intent.putExtra("name",user.getName());
        intent.putExtra("lastOnlineStatus",laodOnlineStatus(user.getCurrentStatus(),user.getLastOnline()));
        intent.putExtra("verified",user.getVerified());
        intent.putExtra("blocked",user.getBlocked());
        intent.putExtra("muted",user.getMuted());
        intent.putExtra("roomid", createRoomId(username));
        intent.putExtra("profile",user.getProfileUrl());
        intent.putExtra("blockedBy",user.getBlockedBy());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static String laodOnlineStatus(String status, Date lastOnline) {
        String lastStatus = status;
        if (status.equalsIgnoreCase("typing]-]"+myNumber)) {
            return "typing ...";
        } else {
            if(status.equalsIgnoreCase("Offline")) {
                Calendar calendar = Calendar.getInstance();
                Date date = calendar.getTime();
                String stringdate = "";
                if(date.equals(lastOnline)) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
                    stringdate = simpleDateFormat.format(lastOnline);
                } else  {
                    calendar.add(Calendar.DATE, -1);
                    date = calendar.getTime();
                    if(date.equals(lastOnline)) {
                        stringdate = "yesterday.";
                    } else {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
                        stringdate = simpleDateFormat.format(lastOnline);
                    }
                }
                lastStatus = "Last online at "+stringdate;
            }
        }
        return lastStatus;
    }

    public static void removeFromRecentChatUI(String username) {
        int index = userListItems.indexOf(userListItemDTOMap.get(username).getUserListItemDTO());
        if(index >= 0) {
            userListItems.remove(userListItemDTOMap.get(username).getUserListItemDTO());
            if( userListItemDTOMap.get(username).getListenerRegistration() != null) {
                userListItemDTOMap.get(username).getListenerRegistration().remove();
            }
            userListItemDTOMap.remove(username);
            recyclerViewAdapter.notifyItemRemoved(index);
            if(userListItems.size() == 0) {
                showNotFound(notfound);
            }
        }
    }

    public static void muteFromRecentChatUI(String username,Boolean muted) {
        int index = userListItems.indexOf(userListItemDTOMap.get(username));
        if(index >= 0) {
            UserListItemDTO userListItemDTO = userListItems.get(index);
            userListItemDTO.setMuted(muted);
            userListItems.remove(index);
            userListItems.add(index,userListItemDTO);
            userListItemDTOMap.get(username).setUserListItemDTO(userListItemDTO);
            recyclerViewAdapter.notifyItemChanged(index);
        }
    }

    public static String getNormalizedPhoneNumber(String number) {
        if(number != null && number.charAt(0) == '+') {
            number = number.replace("+91","");
        }
       return number;
    }

    public static void invite(Context context,String contactNumbers) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", contactNumbers);
        smsIntent.putExtra("sms_body","Let's chat on Chat With Me ! It's fast,simple and secure app we can use to message and call each other for free. Get it at ");
        smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(smsIntent);
    }

    public static void openAddContact(Context applicationContext, Contact contact) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, contact.getName());
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, contact.getContactNumbers());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        applicationContext.startActivity(intent);
    }

    public static void handleIncomingCall(Context applicationContext, Call call) {
        currentCallRef = call;
        Toast.makeText(applicationContext,"Incoming Call"+call.getRemoteUserId(),Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(applicationContext, AudioCall.class);
        intent.putExtra("username",call.getRemoteUserId());
        intent.putExtra("making",false);
        applicationContext.startActivity(intent);
    }

    public static void setCurrentCallRef(Call currentCallRef) {
        MessageMaker.currentCallRef = currentCallRef;
    }

    public static void audioCall(String username) {
        currentCallRef = callClient.callUser(username);
    }

    public static void audioConferenceCall(String username) {
        currentCallRef = callClient.callConference(username);
    }

    public static void videoCall(String username) {
    }
}
