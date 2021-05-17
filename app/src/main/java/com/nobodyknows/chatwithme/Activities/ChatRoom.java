package com.nobodyknows.chatwithme.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.NobodyKnows.chatlayoutview.ChatLayoutView;
import com.NobodyKnows.chatlayoutview.Constants.MessageStatus;
import com.NobodyKnows.chatlayoutview.Interfaces.ChatLayoutListener;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.nobodyknows.chatwithme.Activities.Signup.CreateUser;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.FirebaseService;
import com.nobodyknows.chatwithme.services.MessageMaker;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRoom extends AppCompatActivity {

    private String username,name,profile,lastOnlineStatus;
    private ChatLayoutView chatLayoutView;
    private CircleImageView profileView;
    private TextView nameView,statusView;
    private ImageView verified,audio,video,emoji,send;
    private EditText messageBox;
    private Boolean isVerfied = false;
    private String myUsername = "",roomid = "";
    private FirebaseService firebaseService;
    private ImageView backgroundImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        username = getIntent().getStringExtra("username");
        name = getIntent().getStringExtra("name");
        lastOnlineStatus = getIntent().getStringExtra("lastOnlineStatus");
        profile = getIntent().getStringExtra("profile");
        roomid = getIntent().getStringExtra("roomid");
        isVerfied = getIntent().getBooleanExtra("verified",false);
        SharedPreferences sharedPreferences = getSharedPreferences("ChatWithMe",MODE_PRIVATE);
        myUsername = sharedPreferences.getString("number","0000000000");
        getSupportActionBar().hide();
        firebaseService = new FirebaseService();
        init();
    }

    private void init() {
        backgroundImage = findViewById(R.id.background);
        chatLayoutView = findViewById(R.id.chatlayoutview);
        profileView = findViewById(R.id.profile);
        nameView = findViewById(R.id.name);
        statusView = findViewById(R.id.status);
        verified = findViewById(R.id.verified);
        audio = findViewById(R.id.audiocall);
        video = findViewById(R.id.videocall);
        emoji = findViewById(R.id.emoji);
        send = findViewById(R.id.send);
        messageBox = findViewById(R.id.messagebox);
        //chatLayoutView.setBackgroundImage("https://mumbaimirror.indiatimes.com/photo/76678905.cms");
        updateNameView();
        setupChatLayoutView();
        setupMessageBoxWork();
        startListening();
    }

    private void startListening() {
        firebaseService.readFromFireStore("Chats").document(roomid).collection("Messages").orderBy("messageId", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error == null) {
                    for(DocumentChange doc:value.getDocumentChanges()) {
                        Message message = doc.getDocument().toObject(Message.class);
                        switch (doc.getType()) {
                            case ADDED:
                                chatLayoutView.addMessage(message);
                                break;
                            case MODIFIED:
                                chatLayoutView.updateMessage(message);
                                break;
                            case REMOVED:
                                chatLayoutView.deleteMessage(message);
                                break;
                        }
                    }
                }
            }
        });
    }

    private void setupMessageBoxWork() {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageBox.getText().toString().trim();
                if(message != null && message.length() > 0) {
                    sendMessage(message);
                }
                messageBox.setText("");
            }
        });
    }

    private void sendMessage(String messageText) {
        Message message = getDefaultObject();
        message.setMessage(messageText);
        sendNow(message);
    }

    private Message getDefaultObject() {
        Message message = new Message();
        message.setMessageId(MessageMaker.createMessageId(myUsername));
        message.setReceiver(username);
        message.setSender(myUsername);
        message.setRoomId(roomid);
        return message;
    }

    private void sendNow(Message message) {
        chatLayoutView.addMessage(message);
        firebaseService.saveToFireStore("Chats").document(roomid).collection("Messages").document(message.getMessageId()).set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                chatLayoutView.updateMessage(message);
                firebaseService.updateLastMessage(myUsername,username,message);
            }
        });
    }

    private void setupChatLayoutView() {
        chatLayoutView.setup(myUsername, roomid, true, new ChatLayoutListener() {
            @Override
            public void onSwipeToReply(Message message, View replyView) {

            }

            @Override
            public void onUploadRetry(Message message) {

            }

            @Override
            public void onMessageSeenConfirmed(Message message) {
                firebaseService.saveToFireStore("Chats").document(roomid).collection("Messages").document(message.getMessageId()).delete();
            }

            @Override
            public void onMessageSeen(Message message) {
                message.setMessageStatus(MessageStatus.SEEN);
                message.setSeenAt(new Date());
                firebaseService.saveToFireStore("Chats").document(roomid).collection("Messages").document(message.getMessageId()).set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        chatLayoutView.updateMessage(message);
                    }
                });
            }
        });
        User user1 = new User();
        user1.setName("Vikram");
        user1.setContactNumber(myUsername);
        chatLayoutView.addUser(user1);

        User user2 = new User();
        user2.setName("Vikram");
        user2.setCurrentStatus(username);
        chatLayoutView.addUser(user2);
        Glide.with(getApplicationContext()).load(R.drawable.background).into(backgroundImage);
    }

    private void updateNameView() {
        nameView.setText(name);
        if(profile != null && !profile.equalsIgnoreCase("NO_PROFILE") && profile.length() > 0) {
            Glide.with(getApplicationContext()).load(profile).into(profileView);
        }
        statusView.setText(lastOnlineStatus);
        if(isVerfied) {
            verified.setVisibility(View.VISIBLE);
        }
    }

}