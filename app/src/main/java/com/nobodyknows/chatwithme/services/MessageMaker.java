package com.nobodyknows.chatwithme.services;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.NobodyKnows.chatlayoutview.Constants.MessageStatus;
import com.NobodyKnows.chatlayoutview.Constants.MessageType;
import com.NobodyKnows.chatlayoutview.Interfaces.LastMessageUpdateListener;
import com.NobodyKnows.chatlayoutview.Model.Contact;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nobodyknows.chatwithme.Activities.AudioCall;
import com.nobodyknows.chatwithme.Activities.ChatRoom;
import com.nobodyknows.chatwithme.DTOS.CallModel;
import com.nobodyknows.chatwithme.DTOS.UserListItemDTO;
import com.nobodyknows.chatwithme.Database.DatabaseHelper;
import com.nobodyknows.chatwithme.R;
import com.sinch.android.rtc.calling.Call;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.callClient;
import static com.nobodyknows.chatwithme.Fragments.CallsFragment.callIds;
import static com.nobodyknows.chatwithme.Fragments.CallsFragment.callNotFound;
import static com.nobodyknows.chatwithme.Fragments.CallsFragment.calls;
import static com.nobodyknows.chatwithme.Fragments.CallsFragment.callsRecyclerViewAdapter;
import static com.nobodyknows.chatwithme.Fragments.ChatFragment.notfound;
import static com.nobodyknows.chatwithme.Fragments.ChatFragment.recyclerViewAdapter;
import static com.nobodyknows.chatwithme.Fragments.ChatFragment.userListItemDTOMap;
import static com.nobodyknows.chatwithme.Fragments.ChatFragment.userListItems;

public class MessageMaker {

    private static MediaPlayer mp;
    private static String DOT = "|29DOT21|";
    private static String DOLLOR = "|29DOLLOR21|";
    private static String LSQUARE = "|29LSQUARE21|";
    private static String RSQUARE = "|29RSQUARE21|";
    private static String HASH = "|29HASH21|";
    private static String SLASH = "|29SLASH21|";
    private static String SPACE = "|29SPACE21|";
    private static String myNumber ="";
    private static String mySecurityKey ="";
    private static Call currentCallRef;
    private static Context context;
    private static View myVideoView,remoteVideoView;
    private static String currentCallId = "";
    private static FirebaseService firebaseService;
    private static Map<String, UploadTask> uploadTaskMap = new HashMap<>();
    private static DatabaseHelper databaseHelper;
    private static com.NobodyKnows.chatlayoutview.DatabaseHelper.DatabaseHelper databaseHelperChat;
    private static Boolean isCallMuted = false,isOnSpeaker = false,isVideoOn = false,isCallStarted = false,isVideoViewSwitched = false;
    public static String createMessageId(String myid) {
        String id =""+new Date().getTime();
        return id;
    }

    public static String createFileId() {
        String id = UUID.randomUUID().toString();
        return id;
    }

    public static FirebaseService getFirebaseService() {
        if(firebaseService == null) {
            firebaseService = new FirebaseService();
        }
        return firebaseService;
    }

    public static DatabaseHelper getDatabaseHelper() {
        if(databaseHelper == null) {
            databaseHelper = new DatabaseHelper(getContext());
        }
        return databaseHelper;
    }

    public static com.NobodyKnows.chatlayoutview.DatabaseHelper.DatabaseHelper getDatabaseHelperChat() {
        if(databaseHelperChat == null) {
            databaseHelperChat = new com.NobodyKnows.chatlayoutview.DatabaseHelper.DatabaseHelper(getContext(), new LastMessageUpdateListener() {
                @Override
                public void onLastMessageAdded(Message message, String roomid) {
                    getDatabaseHelper().updateUserLastMessage(message);
                }
            });
        }
        return databaseHelperChat;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        MessageMaker.context = context;
    }

    public static void setMySecurityKey(String mySecurityKey) {
        MessageMaker.mySecurityKey = mySecurityKey;
    }

    public static View getMyVideoView() {
        return myVideoView;
    }

    public static void setMyVideoView(View myVideoView) {
        MessageMaker.myVideoView = myVideoView;
    }

    public static View getRemoteVideoView() {
        return remoteVideoView;
    }

    public static void setRemoteVideoView(View remoteVideoView) {
        MessageMaker.remoteVideoView = remoteVideoView;
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

    public static int getFromSharedPrefrencesInt(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ChatWithMe",MODE_PRIVATE);
        return sharedPreferences.getInt(key,0);
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
                getDatabaseHelper().setBlockStatus(message.getSender(),message.getSender(),true);
            }
            return null;
        } else if(message.getMessageType() == MessageType.UNBLOCKED) {
            if(!message.getSender().equalsIgnoreCase(myNumber)) {
                getDatabaseHelper().setBlockStatus(message.getSender(),message.getSender(),false);
            }
            return null;
        } 
        return message;
    }

    public static void startChatroom(Context context, String username) {
        User user =getDatabaseHelper().getUser(username);
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

    public static CallModel updateCallModel(CallModel callModel, Call call) {
        if(call != null) {
            callModel.setCallId(call.getCallId());
            callModel.setStartedTime(call.getDetails().getStartedTime());
            callModel.setEstablishedTime(call.getDetails().getEstablishedTime());
            callModel.setCallDuration(call.getDetails().getDuration());
            callModel.setEndedTime(call.getDetails().getEndedTime());
            callModel.setEndCause(call.getDetails().getEndCause().name());
            callModel.setCalltype(call.getDetails().isVideoOffered()?"Video":"Audio");
        } else {
            callModel.setCallId(createMessageId("CALL299221"));
        }
        return callModel;
    }

    public static void handleIncomingCall(Context applicationContext, Call call) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                currentCallRef = call;
                currentCallId = call.getCallId();
                CallModel callModel = new CallModel();
                callModel.setIncomingCall(true);
                callModel.setUsername(call.getRemoteUserId());
                updateCallModel(callModel,call);
                getDatabaseHelper().insertInCalls(callModel);
                playRingtone(applicationContext);
                Intent intent = new Intent(applicationContext, AudioCall.class);
                intent.putExtra("username",call.getRemoteUserId());
                intent.putExtra("making",false);
                intent.putExtra("video",call.getDetails().isVideoOffered());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                applicationContext.startActivity(intent);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
            }
        };
        TedPermission.with(applicationContext)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,Manifest.permission.MODIFY_AUDIO_SETTINGS,Manifest.permission.READ_PHONE_STATE)
                .check();

    }

    public static void updateCallInfo(Call call,Boolean updateinlist) {
        CallModel callModel = getDatabaseHelper().getCallObject(call.getCallId());
        updateCallModel(callModel,call);
        getDatabaseHelper().updateCallInfo(callModel);
        if(updateinlist) {
            if(callsRecyclerViewAdapter != null) {
                if(!callIds.contains(callModel.getCallId())) {
                    calls.add(0,callModel);
                    callIds.add(0,callModel.getCallId());
                    callsRecyclerViewAdapter.notifyItemInserted(0);
                    if(callModel.getEndCause().equalsIgnoreCase("NO_ANSWER") || callModel.getEndCause().equalsIgnoreCase("FAILURE") || callModel.getEndCause().equalsIgnoreCase("CANCELED") || callModel.getEndCause().equalsIgnoreCase("TIMEOUT")) {
                        Message message = getDefaultObject(myNumber,callModel.getUsername(),createRoomId(callModel.getUsername()));
                        message.setMessageStatus(MessageStatus.SENT);
                        message.setMessageType(callModel.getCalltype().equalsIgnoreCase("Video")?MessageType.MISSED_VIDEO_CALL:MessageType.MISSED_AUDIO_CALL);
                        sendMessageNow(message);
                    }
                } else {
                    int index = callIds.indexOf(callModel.getCallId());
                    calls.remove(index);
                    calls.add(index,callModel);
                    callsRecyclerViewAdapter.notifyItemChanged(index);
                }
                callNotFound.setVisibility(View.GONE);
            }
        } else {
            if(callModel.getEndCause().equalsIgnoreCase("NO_ANSWER") || callModel.getEndCause().equalsIgnoreCase("FAILURE") || callModel.getEndCause().equalsIgnoreCase("CANCELED") || callModel.getEndCause().equalsIgnoreCase("TIMEOUT")) {
                Message message = getDefaultObject(myNumber,callModel.getUsername(),createRoomId(callModel.getUsername()));
                message.setMessageStatus(MessageStatus.SENT);
                message.setMessageType(callModel.getCalltype().equalsIgnoreCase("Video")?MessageType.MISSED_VIDEO_CALL:MessageType.MISSED_AUDIO_CALL);
                sendMessageNow(message);
            }
        }
    }

    private static void sendMessageNow(Message message) {
        firebaseService.saveToFireStore("Chats").document(message.getRoomId()).collection("Messages").document(message.getMessageId()).set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseService.updateLastMessage(message.getSender(),message.getReceiver(),message);
            }
        });
    }

    public static void setCurrentCallRef(Call currentCallRef) {
        if(currentCallRef == null) {
            isCallMuted = false;
            isOnSpeaker = false;
            isVideoOn = false;
            isCallStarted = false;
            if(mp != null && mp.isPlaying()) {
                stopRingtone();
            }
        }
        MessageMaker.currentCallRef = currentCallRef;
    }

    public static void audioCall(String username) {
        currentCallRef = callClient.callUser(username);
        CallModel callModel = new CallModel();
        callModel.setIncomingCall(false);
        callModel.setUsername(username);
        updateCallModel(callModel,currentCallRef);
        getDatabaseHelper().insertInCalls(callModel);
    }

    public static void audioConferenceCall(String username) {
        currentCallRef = callClient.callConference(username);
        CallModel callModel = new CallModel();
        callModel.setIncomingCall(false);
        callModel.setUsername(username);
        updateCallModel(callModel,currentCallRef);
        getDatabaseHelper().insertInCalls(callModel);
    }

    public static void videoCall(String username) {
        currentCallRef = callClient.callUserVideo(username);
        CallModel callModel = new CallModel();
        callModel.setIncomingCall(false);
        callModel.setUsername(username);
        updateCallModel(callModel,currentCallRef);
        getDatabaseHelper().insertInCalls(callModel);
    }

    public static void hangup() {
        stopRingtone();
        isCallMuted = false;
        isOnSpeaker = false;
        isVideoOn = false;
        isCallStarted = false;
        if(currentCallRef != null) {
            updateCallInfo(currentCallRef,true);
            currentCallRef.hangup();
            currentCallRef = null;
        }
    }

    public static void answer() {
        isCallStarted = true;
        if(currentCallRef != null) {
            currentCallRef.answer();
        }
    }


    public static void playRingtone(Context context) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mp = MediaPlayer.create(context, notification);
        mp.setLooping(true);
        mp.start();
    }

    public static void stopRingtone() {
        if(mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    public static Boolean getIsCallMuted() {
        return isCallMuted;
    }

    public static void setIsCallMuted(Boolean isCallMuted) {
        MessageMaker.isCallMuted = isCallMuted;
    }

    public static Boolean getIsOnSpeaker() {
        return isOnSpeaker;
    }

    public static void setIsOnSpeaker(Boolean isOnSpeaker) {
        MessageMaker.isOnSpeaker = isOnSpeaker;
    }

    public static Boolean getIsVideoOn() {
        return isVideoOn;
    }

    public static void setIsVideoOn(Boolean isVideoOn) {
        MessageMaker.isVideoOn = isVideoOn;
    }

    public static Boolean getIsCallStarted() {
        return isCallStarted;
    }

    public static void setIsCallStarted(Boolean isCallStarted) {
        MessageMaker.isCallStarted = isCallStarted;
    }

    public static Boolean getIsVideoViewSwitched() {
        return isVideoViewSwitched;
    }

    public static void setIsVideoViewSwitched(Boolean isVideoViewSwitched) {
        MessageMaker.isVideoViewSwitched = isVideoViewSwitched;
    }

    public static String getCurrentCallId() {
        return currentCallId;
    }

    public static void setCurrentCallId(String currentCallId) {
        MessageMaker.currentCallId = currentCallId;
    }

    public static String getFullTimeFromSeconds(int seconds) {
        int p1 = seconds % 60;
        int p2 = seconds / 60;
        int p3 = p2 % 60;
        p2 = p2 / 60;
        return p2 + ":" + p3 + ":" + p1;
    }

    public static String formatDate(Date date,String format) {
        if(date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            return simpleDateFormat.format(date);
        }
        return "";
    }

    public static Date StringToDate(String stringdate,String format) {
        Date date = new Date();
        SimpleDateFormat formate = new SimpleDateFormat(format);
        try {
            date = formate.parse(stringdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date longToDate(long longdate) throws ParseException {
        return new Date(longdate * 1000);
    }

    public static String generateSecurityKey(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }

    public static Message getDefaultObject(String myUsername,String username,String roomid) {
        Message message = new Message();
        message.setMessageId(MessageMaker.createMessageId(myUsername));
        message.setReceiver(username);
        message.setSender(myUsername);
        message.setRoomId(roomid);
        message.setMessageStatus(MessageStatus.SENDING);
        return message;
    }

    public static void playRingSound(Context context) {
        mp = null;
        mp= MediaPlayer.create(context,R.raw.ringeffect);
        mp.start();
    }

    public static void initializeDatabase() {
        firebaseService = new FirebaseService();
        databaseHelper = new DatabaseHelper(getContext());
        databaseHelperChat = new com.NobodyKnows.chatlayoutview.DatabaseHelper.DatabaseHelper(getContext(), new LastMessageUpdateListener() {
            @Override
            public void onLastMessageAdded(Message message, String roomid) {
                getDatabaseHelper().updateUserLastMessage(message);
            }
        });
    }

    public static String encryptForFirebaseKey(String value) {
        if(value.contains(".")) {
            value = value.replace(".",DOT);
        }
        if(value.contains("$")) {
            value = value.replace("$",DOLLOR);
        }
        if(value.contains("[")) {
            value = value.replace("[",LSQUARE);
        }
        if(value.contains("]")) {
            value = value.replace("]",RSQUARE);
        }
        if(value.contains("#")) {
            value = value.replace("#",HASH);
        }
        if(value.contains("/")) {
            value = value.replace("/",SLASH);
        }
        if(value.contains(" ")) {
            value = value.replace(" ",SPACE);
        }
        return value;
    }

    public static String decryptForFirebaseKey(String value) {
        if(value.contains(DOT)) {
            value = value.replace(DOT,".");
        }
        if(value.contains(DOLLOR)) {
            value = value.replace(DOLLOR,"$");
        }
        if(value.contains(LSQUARE)) {
            value = value.replace(LSQUARE,"[");
        }
        if(value.contains(RSQUARE)) {
            value = value.replace(RSQUARE,"]");
        }
        if(value.contains(HASH)) {
            value = value.replace(HASH,"#");
        }
        if(value.contains(SLASH)) {
            value = value.replace(SLASH,"/");
        }
        if(value.contains(SPACE)) {
            value = value.replace(SPACE," ");
        }
        return value;
    }

    public static UploadTask getUploadTask(String messageId, String roomId, String sharedFileId) {
        return uploadTaskMap.get(messageId+"]-]"+roomId+"]-]"+sharedFileId);
    }

    public static void addInUploadTaskMap(String messageId, String roomId,String sharedFileId,UploadTask uploadTask) {
        uploadTaskMap.put(messageId+"]-]"+roomId+"]-]"+sharedFileId,uploadTask);
    }

    public static void removeUploadTaskMap(String messageId, String roomId,String sharedFileId) {
        uploadTaskMap.remove(messageId+"]-]"+roomId+"]-]"+sharedFileId);
    }
}
