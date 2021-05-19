package com.nobodyknows.chatwithme.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.nobodyknows.chatwithme.Activities.Dashboard.ViewContact;
import com.nobodyknows.chatwithme.Activities.Signup.CreateUser;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.FirebaseService;
import com.nobodyknows.chatwithme.services.MessageMaker;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.databaseHelper;
import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.databaseHelperChat;
import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.firebaseService;
public class ChatRoom extends AppCompatActivity {

    private String username,name,profile,lastOnlineStatus;
    private ChatLayoutView chatLayoutView;
    private CircleImageView profileView;
    private TextView nameView,statusView;
    private ImageView verified,emoji,send;
    private EditText messageBox;
    private Boolean isVerfied = false;
    private String myUsername = "",roomid = "";
    private ImageView backgroundImage;
    private View actionBarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.chatroom_toolbar_view);
        actionBarView = getSupportActionBar().getCustomView();
        getSupportActionBar().setElevation(0);
        username = getIntent().getStringExtra("username");
        name = getIntent().getStringExtra("name");
        lastOnlineStatus = getIntent().getStringExtra("lastOnlineStatus");
        profile = getIntent().getStringExtra("profile");
        roomid = getIntent().getStringExtra("roomid");
        isVerfied = getIntent().getBooleanExtra("verified",false);
        SharedPreferences sharedPreferences = getSharedPreferences("ChatWithMe",MODE_PRIVATE);
        myUsername = sharedPreferences.getString("number","0000000000");
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_audio_call:
                audioCall();
                break;
            case R.id.menu_video_call:
                videoCall();
                break;
            case R.id.menu_view_contact:
                openViewContact();
                break;
            case R.id.unfreind:
                unfreind();
                break;
            case android.R.id.home:
               finish();
                break;
            default:
                break;
        }
        return true;
    }

    private void openViewContact() {
        Intent intent = new Intent(getApplicationContext(), ViewContact.class);
        intent.putExtra("username",username);
        intent.putExtra("isFromChat",true);
        startActivity(intent);
    }

    private void unfreind() {
    }

    private void videoCall() {
        Toast.makeText(getApplicationContext(),"This is not available right now",Toast.LENGTH_SHORT).show();
    }

    private void audioCall() {
        Toast.makeText(getApplicationContext(),"This is not available right now",Toast.LENGTH_SHORT).show();
    }

    private void init() {
        backgroundImage = findViewById(R.id.background);
        chatLayoutView = findViewById(R.id.chatlayoutview);
        profileView = actionBarView.findViewById(R.id.profile);
        nameView = actionBarView.findViewById(R.id.name);
        statusView = actionBarView.findViewById(R.id.status);
        verified = actionBarView.findViewById(R.id.verified);
        emoji = findViewById(R.id.emoji);
        send = findViewById(R.id.send);
        messageBox = findViewById(R.id.messagebox);
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
        chatLayoutView.setup(myUsername, roomid, true,databaseHelperChat, new ChatLayoutListener() {
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
        User myUser = new User();
        myUser.setName(MessageMaker.getFromSharedPrefrences(getApplicationContext(),"name"));
        myUser.setContactNumber(myUsername);
        chatLayoutView.addUser(myUser);
        User freinduser = databaseHelper.getUser(username);
        chatLayoutView.addUser(freinduser);
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