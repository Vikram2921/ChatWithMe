package com.nobodyknows.chatwithme.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.transition.TransitionManager;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.NobodyKnows.chatlayoutview.ChatLayoutView;
import com.NobodyKnows.chatlayoutview.Constants.MessageStatus;
import com.NobodyKnows.chatlayoutview.Constants.MessageType;
import com.NobodyKnows.chatlayoutview.Interfaces.ChatLayoutListener;
import com.NobodyKnows.chatlayoutview.Model.Contact;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nobodyknows.chatwithme.Activities.Dashboard.ViewContact;
import com.nobodyknows.chatwithme.Activities.Signup.CreateUser;
import com.nobodyknows.chatwithme.DTOS.FreindRequestSaveDTO;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.FirebaseService;
import com.nobodyknows.chatwithme.services.MessageMaker;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.emoji.Emoji;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;
import com.vistrav.pop.Pop;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;
import com.wafflecopter.multicontactpicker.RxContacts.PhoneNumber;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.markushi.ui.CircleButton;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.databaseHelper;
import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.databaseHelperChat;
import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.firebaseService;
public class ChatRoom extends AppCompatActivity {

    private static final int CONTACT_PICKER_REQUEST = 2921;
    private String username,name,profile,lastOnlineStatus;
    private ChatLayoutView chatLayoutView;
    private CircleImageView profileView;
    private TextView nameView,statusView;
    private ConstraintLayout attachemntPane;
    private ImageView verified,emoji,send,attachment;
    private EmojiEditText messageBox;
    private Boolean isVerfied = false,isBlocked = false;
    private String myUsername = "",roomid = "";
    private ImageView backgroundImage;
    private ListenerRegistration listner,listneruseraccount;
    private EmojiPopup emojiPopup;
    private Date lastOnlineDate;
    private View actionBarView;
    private Menu menu;
    private View rootView;
    private String blockedBy = "";
    private boolean isMuted = false;
    private CircleButton contacts;
    private Boolean isIamTyping = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.chatroom_toolbar_view);
        actionBarView = getSupportActionBar().getCustomView();
        actionBarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openViewContact();
            }
        });
        getSupportActionBar().setElevation(0);
        username = getIntent().getStringExtra("username");
        name = getIntent().getStringExtra("name");
        lastOnlineStatus = getIntent().getStringExtra("lastOnlineStatus");
        profile = getIntent().getStringExtra("profile");
        roomid = getIntent().getStringExtra("roomid");
        isVerfied = getIntent().getBooleanExtra("verified",false);
        isBlocked = getIntent().getBooleanExtra("blocked",false);
        isMuted = getIntent().getBooleanExtra("muted",false);
        blockedBy = getIntent().getStringExtra("blockedBy");
        SharedPreferences sharedPreferences = getSharedPreferences("ChatWithMe",MODE_PRIVATE);
        myUsername = sharedPreferences.getString("number","0000000000");
        init();
        updateUserInfoSync();
    }

    private void updateStatusViewColor() {
        if(statusView.getText().toString().equalsIgnoreCase("typing ...")) {
            statusView.setTextColor(getResources().getColor(R.color.typing));
        } else {
            statusView.setTextColor(getResources().getColor(R.color.defaulttext));
        }
    }

    private void updateUserInfoSync() {
        listneruseraccount = firebaseService.readFromFireStore("Users").document(username).collection("AccountInfo").document("PersonalInfo").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                User user = value.toObject(User.class);
                MessageMaker.loadProfile(getApplicationContext(),user.getProfileUrl(),profileView);
                nameView.setText(user.getName());
                lastOnlineDate = user.getLastOnline();
                lastOnlineStatus = user.getCurrentStatus();
                statusView.setText(MessageMaker.laodOnlineStatus(lastOnlineStatus,lastOnlineDate));
                updateStatusViewColor();
                isVerfied = user.getVerified();
                if(isVerfied) {
                    verified.setVisibility(View.VISIBLE);
                }
                databaseHelper.updateUserInfo(user);
            }
        });
    }

    private void updateStatus(Boolean istyping) {
        String newstatus = "Online";
        if(istyping) {
            newstatus = "typing]-]"+username;
        }
        firebaseService.readFromFireStore("Users").document(MessageMaker.getMyNumber()).collection("AccountInfo").document("PersonalInfo").update("currentStatus",newstatus).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeListener();
        if(listneruseraccount != null) {
            listneruseraccount.remove();
            listneruseraccount = null;
        }
        updateStatus(false);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = databaseHelper.getUser(username);
        if(user == null) {
            finish();
        } else {
            isBlocked = user.getBlocked();
            blockedBy = user.getBlockedBy();
            setBlockStatus();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_room, menu);
        this.menu = menu;
        setBlockStatus();
        setMuteStatus();
        return true;
    }

    private void setBlockStatus() {
        if(menu != null) {
            MenuItem item = menu.findItem(R.id.block);
            if(isBlocked) {
                if(blockedBy.equalsIgnoreCase(myUsername)) {
                    item.setTitle("Unblock");
                    removeListener();
                }
            } else {
                item.setTitle("Block");
            }
        }
    }

    private void setMuteStatus() {
        if(menu != null) {
            MenuItem item = menu.findItem(R.id.menu_mute);
            if(isMuted) {
                item.setTitle("Unmute");
            } else {
                item.setTitle("Mute Notification");
            }
            MessageMaker.muteFromRecentChatUI(username,isMuted);
        }
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
            case R.id.menu_wallpaper:
                changeWallpaper();
                break;
            case R.id.menu_mute:
                if(item.getTitle().equals("Unmute")) {
                    unmuteNotification();
                } else {
                    muteNotification();
                }
                break;
            case R.id.block:
                if(item.getTitle().equals("Unblock")) {
                    unblock();
                } else {
                    block();
                }
                break;
            case R.id.clear:
                clearChat();
                break;
            case android.R.id.home:
               finish();
                break;
            default:
                break;
        }
        return true;
    }

    private void muteNotification() {
        Pop.on(ChatRoom.this).with()
                .cancelable(true)
                .body("Are you sure you want to mute all notification from chat with "+name+" ?")
                .when(R.string.mute,new Pop.Yah() {
                    @Override
                    public void clicked(DialogInterface dialog, @Nullable View view) {
                       firebaseService.muteChat(username);
                       isMuted = true;
                       setMuteStatus();
                    }
                }).when(new Pop.Nah() {
            @Override
            public void clicked(DialogInterface dialog, @Nullable View view) {
            }
        }).show();
    }

    private void unmuteNotification() {
        Pop.on(ChatRoom.this).with()
                .cancelable(true)
                .body("Are you sure you want to unmute all notification from chat with "+name+" ?")
                .when(R.string.unmute,new Pop.Yah() {
                    @Override
                    public void clicked(DialogInterface dialog, @Nullable View view) {
                        firebaseService.unmuteChat(username);
                        isMuted = false;
                        setMuteStatus();
                    }
                }).when(new Pop.Nah() {
            @Override
            public void clicked(DialogInterface dialog, @Nullable View view) {
            }
        }).show();
    }

    private void clearChat() {
        Pop.on(ChatRoom.this).with()
                .cancelable(true)
                .body("Are you sure you want to clear all your chat with "+name+" ?")
                .when(R.string.clear,new Pop.Yah() {
                    @Override
                    public void clicked(DialogInterface dialog, @Nullable View view) {
                        databaseHelperChat.clearAll(roomid);
                        databaseHelper.clearLastMessage(username);
                        chatLayoutView.reload();
                    }
                }).when(new Pop.Nah() {
            @Override
            public void clicked(DialogInterface dialog, @Nullable View view) {
            }
        }).show();
    }

    private void removeListener() {
        if(listner != null) {
            listner.remove();
            listner = null;
        }
    }

    private void block() {
        Pop.on(ChatRoom.this).with()
                .cancelable(true)
                .body("Are you sure you want to block "+name+" from your freind list ?")
                .when(R.string.blockstring,new Pop.Yah() {
                    @Override
                    public void clicked(DialogInterface dialog, @Nullable View view) {
                        firebaseService.block(username,roomid);
                        isBlocked = true;
                        blockedBy = myUsername;
                        setBlockStatus();
                    }
                }).when(new Pop.Nah() {
            @Override
            public void clicked(DialogInterface dialog, @Nullable View view) {
            }
        }).show();
    }

    private void unfreind() {
        Pop.on(ChatRoom.this).with()
                .cancelable(true)
                .body("Are you sure you want to unfreind "+name+" from your freind list ?")
                .when(R.string.unfreind,new Pop.Yah() {
                    @Override
                    public void clicked(DialogInterface dialog, @Nullable View view) {
                        firebaseService.unfreind(username,ChatRoom.this);
                    }
                }).when(new Pop.Nah() {
            @Override
            public void clicked(DialogInterface dialog, @Nullable View view) {
            }
        }).show();
    }

    private void unblock() {
        Pop.on(ChatRoom.this).with()
                .cancelable(true)
                .body("Are you sure you want to unblock "+name+" ?")
                .when(R.string.unblockstring,new Pop.Yah() {
                    @Override
                    public void clicked(DialogInterface dialog, @Nullable View view) {
                        firebaseService.saveToFireStore("Chats").document(roomid).collection("Messages").document("BLOCKED_"+username).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                firebaseService.unblock(username,roomid);
                                isBlocked = false;
                                blockedBy = myUsername;
                                if(listner == null) {
                                    startListening();
                                }
                                setBlockStatus();
                            }
                        });
                    }
                }).when(new Pop.Nah() {
            @Override
            public void clicked(DialogInterface dialog, @Nullable View view) {
            }
        }).show();
    }

    private void changeWallpaper() {
        ImagePicker.Companion.with(ChatRoom.this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == CONTACT_PICKER_REQUEST){
                if(resultCode == RESULT_OK) {
                    List<ContactResult> results = MultiContactPicker.obtainResult(data);
                    sendContacts(results);
                } else if(resultCode == RESULT_CANCELED){
                    System.out.println("User closed the picker without selecting items.");
                }
            } else {
                Uri uri = data.getData();
                File file = ImagePicker.Companion.getFile(data);
                String profilePath = file.getPath();
                SharedPreferences sharedPreferences = getSharedPreferences("ChatWithMe",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("backgroundPath"+username,profilePath);
                editor.apply();
                loadBackgroundImage();
            }
        }
    }

    private void sendContacts(List<ContactResult> results) {
        Message message = getDefaultObject();
        message.setMessage("");
        message.setMessageType((results.size() > 1)?MessageType.CONTACT_MULTIPLE:MessageType.CONTACT_SINGLE);
        for(ContactResult contactResult:results) {
            message.addContact(convertToContact(contactResult));
        }
        sendNow(message);
    }

    private Contact convertToContact(ContactResult contactResult) {
        Contact contact = new Contact();
        contact.setName(contactResult.getDisplayName());
        User user = null;
        for(PhoneNumber phoneNumber:contactResult.getPhoneNumbers()) {
            contact.setContactNumbers(MessageMaker.getNormalizedPhoneNumber(phoneNumber.getNumber()));
            user = databaseHelper.getUser(contact.getContactNumbers());
            if(user != null) {
                contact.setProfileUrl(user.getProfileUrl());
            }
        }
        return contact;
    }

    private void openViewContact() {
        Intent intent = new Intent(getApplicationContext(), ViewContact.class);
        intent.putExtra("username",username);
        intent.putExtra("isFromChat",true);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,profileView,"profile");
        startActivity(intent,activityOptionsCompat.toBundle());
    }


    private void videoCall() {
        Intent intent = new Intent(getApplicationContext(),AudioCall.class);
        intent.putExtra("username",username);
        intent.putExtra("making",true);
        intent.putExtra("video",true);
        startActivity(intent);
    }

    private void audioCall() {
        Intent intent = new Intent(getApplicationContext(),AudioCall.class);
        intent.putExtra("username",username);
        intent.putExtra("making",true);
        intent.putExtra("video",false);
        startActivity(intent);
    }


    private void init() {
        attachemntPane = findViewById(R.id.attachmentpane);
        attachment = findViewById(R.id.attachment);
        contacts = findViewById(R.id.contact);
        attachemntPane.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    attachemntPane.setVisibility(View.GONE);
                }
            }
        });
        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(attachemntPane.getVisibility() == View.GONE) {
                    attachemntPane.setVisibility(View.VISIBLE);
                } else {
                    attachemntPane.setVisibility(View.GONE);
                }
            }
        });
        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachemntPane.setVisibility(View.GONE);
                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        new MultiContactPicker.Builder(ChatRoom.this) //Activity/fragment context
                                .theme(R.style.MyCustomPickerTheme)
                                .hideScrollbar(false) //Optional - default: false
                                .showTrack(true) //Optional - default: true
                                .searchIconColor(Color.WHITE) //Option - default: White
                                .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
                                .handleColor(ContextCompat.getColor(ChatRoom.this, R.color.purple_500)) //Optional - default: Azure Blue
                                .bubbleColor(ContextCompat.getColor(ChatRoom.this, R.color.purple_500)) //Optional - default: Azure Blue
                                .bubbleTextColor(Color.WHITE) //Optional - default: White
                                .setTitleText("Select Contacts") //Optional - default: Select Contact
                                .setLoadingType(MultiContactPicker.LOAD_SYNC) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)
                                .limitToColumn(LimitColumn.NONE) //Optional - default NONE (Include phone + email, limiting to one can improve loading time)
                                .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                                        android.R.anim.fade_in,
                                        android.R.anim.fade_out) //Optional - default: No animation overrides
                                .showPickerForResult(CONTACT_PICKER_REQUEST);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                    }
                };
                TedPermission.with(getApplicationContext())
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS)
                        .check();
            }
        });
        backgroundImage = findViewById(R.id.background);
        chatLayoutView = findViewById(R.id.chatlayoutview);
        profileView = actionBarView.findViewById(R.id.profile);
        rootView = findViewById(R.id.root);
        nameView = actionBarView.findViewById(R.id.name);
        statusView = actionBarView.findViewById(R.id.status);
        statusView.setText(lastOnlineStatus);
        verified = actionBarView.findViewById(R.id.verified);
        emoji = findViewById(R.id.emoji);
        send = findViewById(R.id.send);
        messageBox = findViewById(R.id.messagebox);
        messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString() != null && s.toString().length() > 0) {
                    if(!isIamTyping) {
                        isIamTyping = true;
                        updateStatus(true);
                    }
                } else {
                    if(isIamTyping) {
                        isIamTyping = false;
                        updateStatus(false);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView)
                .setSelectedIconColor(getResources().getColor(R.color.purple_500))
                .setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
                    @Override
                    public void onKeyboardClose() {
                        dismissKeyboard();
                    }
                }).build(messageBox);
        emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emojiPopup.isShowing()) {
                    emoji.setImageResource(R.drawable.ic_baseline_emoji_emotions_24);
                } else {
                    emoji.setImageResource(R.drawable.ic_baseline_keyboard_24);
                }
                emojiPopup.toggle();

            }
        });
        updateNameView();
        setupChatLayoutView();
        setupMessageBoxWork();
        startListening();
    }

    private void dismissKeyboard() {
        if(emojiPopup.isShowing()) {
            emojiPopup.toggle();
            emoji.setImageResource(R.drawable.ic_baseline_emoji_emotions_24);
        }
    }


    private void startListening() {
        if(listner == null && (!isBlocked || (isBlocked && !blockedBy.equalsIgnoreCase(myUsername)))) {
            listner = firebaseService.readFromFireStore("Chats").document(roomid).collection("Messages").orderBy("messageId", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error == null) {
                        for(DocumentChange doc:value.getDocumentChanges()) {
                            Message message = doc.getDocument().toObject(Message.class);
                            Message messageUpdate = MessageMaker.filterMessage(message);
                            if(messageUpdate != null) {
                                switch (doc.getType()) {
                                    case ADDED:
                                        chatLayoutView.addMessage(messageUpdate);
                                        break;
                                    case MODIFIED:
                                        chatLayoutView.updateMessage(messageUpdate);
                                        break;
                                    case REMOVED:
                                        break;
                                }
                            } else {
                                if(message.getMessageType() == MessageType.BLOCKED) {
                                    isBlocked = true;
                                    blockedBy = message.getSender();
                                    setBlockStatus();
                                } else if(message.getMessageType() == MessageType.UNBLOCKED) {
                                    isBlocked = false;
                                    blockedBy = message.getSender();
                                    setBlockStatus();
                                    firebaseService.saveToFireStore("Chats").document(roomid).collection("Messages").document("UNBLOCKED_"+username).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            });

        }
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
        if(!isBlocked) {
            firebaseService.saveToFireStore("Chats").document(roomid).collection("Messages").document(message.getMessageId()).set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    chatLayoutView.updateMessage(message);
                    firebaseService.updateLastMessage(myUsername,username,message);
                }
            });
        }
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
            public void onSenderNameClicked(User user, Message message) {

            }

            @Override
            public void onClickChatFromContactMessage(Contact contact) {
                OpenInChat(contact);
            }

            @Override
            public void onClickAddContactFromContactMessage(Contact contact){
                addContactLocal(contact);
            }

            @Override
            public void onMessageSeenConfirmed(Message message) {
                if(!isBlocked) {
                    firebaseService.saveToFireStore("Chats").document(roomid).collection("Messages").document(message.getMessageId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
                }
            }

            @Override
            public void onMessageSeen(Message message) {
                if(!isBlocked) {
                    if(message.getReceivedAt() == null) {
                        message.setReceivedAt(new Date());
                    }
                    message.setMessageStatus(MessageStatus.SEEN);
                    message.setSeenAt(new Date());
                    firebaseService.saveToFireStore("Chats").document(roomid).collection("Messages").document(message.getMessageId()).set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            chatLayoutView.updateMessage(message);
                        }
                    });
                }
            }
        });
        User myUser = new User();
        myUser.setName(MessageMaker.getFromSharedPrefrences(getApplicationContext(),"name"));
        myUser.setContactNumber(myUsername);
        chatLayoutView.addUser(myUser);
        User freinduser = databaseHelper.getUser(username);
        chatLayoutView.addUser(freinduser);
        loadBackgroundImage();
    }

    private void addContactLocal(Contact contact) {
        MessageMaker.openAddContact(getApplicationContext(),contact);
    }

    private void sendRequest(User user) {
        FreindRequestSaveDTO freindRequestSaveDTO = new FreindRequestSaveDTO();
        freindRequestSaveDTO.setRequestSentBy(myUsername);
        freindRequestSaveDTO.setRequestSentAt(new Date());
        freindRequestSaveDTO.setContactNumber(user.getContactNumber());
        firebaseService.saveToFireStore("Users").document(myUsername).collection("FreindRequests").document("Sent").collection("List").document(user.getContactNumber()).set(freindRequestSaveDTO).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                freindRequestSaveDTO.setContactNumber(myUsername);
                firebaseService.saveToFireStore("Users").document(user.getContactNumber()).collection("FreindRequests").document("Receive").collection("List").document(myUsername).set(freindRequestSaveDTO).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"Freind Request Sent to "+user.getName(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void OpenInChat(Contact contact) {
        Boolean userExist = databaseHelper.isUserExist(contact.getContactNumbers());
        if(userExist) {
            MessageMaker.startChatroom(getApplicationContext(),contact.getContactNumbers());
            finish();
        } else {
            if(!contact.getContactNumbers().equalsIgnoreCase(myUsername)) {
                KProgressHUD popup = KProgressHUD.create(ChatRoom.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("Please wait")
                        .setCancellable(false)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f)
                        .show();
                final AlertDialog[] pop = new AlertDialog[1];
                firebaseService.readFromFireStore("Users").document(contact.getContactNumbers()).collection("AccountInfo").document("PersonalInfo").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            popup.dismiss();
                            if(task.getResult().exists()) {
                                User user = task.getResult().toObject(User.class);
                                pop[0] = Pop.on(ChatRoom.this).with()
                                        .cancelable(true)
                                        .layout(R.layout.inviteuser).show(new Pop.View() {
                                            @Override
                                            public void prepare(@Nullable View view) {
                                                TextView title = view.findViewById(R.id.title);
                                                TextView desc = view.findViewById(R.id.desc);
                                                Button invite = view.findViewById(R.id.invite);
                                                Button cancel = view.findViewById(R.id.cancel);
                                                invite.setText("Send Freind Request");
                                                title.setText("Send Freind Request");
                                                desc.setText(user.getName()+" is not in your freind list.Send freinds request to start chat.");
                                                invite.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        sendRequest(user);
                                                        pop[0].dismiss();
                                                    }
                                                });
                                                cancel.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        pop[0].dismiss();
                                                    }
                                                });
                                            }
                                        });
                            } else {
                                pop[0] = Pop.on(ChatRoom.this).with()
                                        .cancelable(true)
                                        .layout(R.layout.inviteuser).show(new Pop.View() {
                                            @Override
                                            public void prepare(@Nullable View view) {
                                                TextView title = view.findViewById(R.id.title);
                                                TextView desc = view.findViewById(R.id.desc);
                                                Button invite = view.findViewById(R.id.invite);
                                                Button cancel = view.findViewById(R.id.cancel);
                                                title.setText("Invite "+contact.getName());
                                                desc.setText(contact.getName()+" is not on Chat With Me. Invite "+contact.getName()+" to start chat.");
                                                invite.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        MessageMaker.invite(getApplicationContext(),contact.getContactNumbers());
                                                        pop[0].dismiss();
                                                    }
                                                });
                                                cancel.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        pop[0].dismiss();
                                                    }
                                                });
                                            }
                                        });
                            }
                        }
                    }
                });
            }
        }
    }

    private void loadBackgroundImage() {
        String imageurl = MessageMaker.getFromSharedPrefrences(getApplicationContext(),"backgroundPath"+username);
        if(imageurl != null && imageurl.length() > 0) {
            Glide.with(getApplicationContext()).load(imageurl).into(backgroundImage);
        } else {
            Glide.with(getApplicationContext()).load(R.drawable.background).into(backgroundImage);
        }
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