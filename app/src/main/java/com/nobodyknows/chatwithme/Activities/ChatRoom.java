package com.nobodyknows.chatwithme.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.NobodyKnows.chatlayoutview.ChatLayoutView;
import com.NobodyKnows.chatlayoutview.Constants.MessageStatus;
import com.NobodyKnows.chatlayoutview.Constants.MessageType;
import com.NobodyKnows.chatlayoutview.Constants.UploadStatus;
import com.NobodyKnows.chatlayoutview.Interfaces.ChatLayoutListener;
import com.NobodyKnows.chatlayoutview.Model.Contact;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.SharedFile;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.NobodyKnows.chatlayoutview.ProgressButton;
import com.NobodyKnows.chatlayoutview.Services.LayoutService;
import com.bumptech.glide.Glide;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.ui.GPHContentType;
import com.giphy.sdk.ui.GPHSettings;
import com.giphy.sdk.ui.views.GiphyDialogFragment;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.kbeanie.multipicker.api.AudioPicker;
import com.kbeanie.multipicker.api.FilePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.AudioPickerCallback;
import com.kbeanie.multipicker.api.callbacks.FilePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenAudio;
import com.kbeanie.multipicker.api.entity.ChosenFile;
import com.nobodyknows.chatwithme.Activities.Dashboard.ViewContact;
import com.nobodyknows.chatwithme.DTOS.FreindRequestSaveDTO;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.MessageMaker;
import com.scottyab.aescrypt.AESCrypt;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.google.GoogleEmojiProvider;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;
import com.vistrav.pop.Pop;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;
import com.wafflecopter.multicontactpicker.RxContacts.PhoneNumber;

import org.jetbrains.annotations.NotNull;
import org.michaelbel.bottomsheet.BottomSheet;

import java.io.File;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.markushi.ui.CircleButton;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.GIPHY_KEY;
public class ChatRoom extends AppCompatActivity implements GiphyDialogFragment.GifSelectionListener {

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
    private ArrayList<String> selectedUrls = new ArrayList<>();
    private View rootView;
    private String blockedBy = "";
    private boolean isMuted = false;
    private CircleButton contacts,gif,imagebutton,videobutton,audioButton,documentButton;
    private Boolean isIamTyping = false;
    private Message repliedMessage = null;
    private String roomSecurityKey = "";
    private View replyview;
    private ImageView cancel;
    private Boolean isWithLink = false;
    private AudioPicker audioPicker;
    private FilePicker filePicker;

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
        roomSecurityKey = MessageMaker.getDatabaseHelper().getSecurityKey(roomid);
        init();
        updateUserInfoSync();
    }

    private void addInfoMessage() {
        Message message = MessageMaker.getDefaultObject(myUsername,username,roomid);
        message.setMessageType(MessageType.INFO);
        message.setMessageId("0");
        message.setMessage("Messages and calls are end-to-end encrypted. No one outside of this chat, not even Chat With Me, can read or listen to them.");
        chatLayoutView.addTopMessage(message);
    }

    private void updateStatusViewColor() {
        if(statusView.getText().toString().equalsIgnoreCase("typing ...")) {
            statusView.setTextColor(getResources().getColor(R.color.typing));
        } else {
            statusView.setTextColor(getResources().getColor(R.color.defaulttext));
        }
    }

    private void updateUserInfoSync() {
        listneruseraccount = MessageMaker.getFirebaseService().readFromFireStore("Users").document(username).collection("AccountInfo").document("PersonalInfo").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                User user = value.toObject(User.class);
                if(user != null) {
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
                    MessageMaker.getDatabaseHelper().updateUserInfo(user);
                }
            }
        });
    }

    private void showBottomSheetForWallpaper() {
        CharSequence cs[] = {"Remove Wallpaper","Change Wallpaper"};
        Drawable drawable[] = {getResources().getDrawable(R.drawable.ic_baseline_delete_forever_24),getResources().getDrawable(R.drawable.ic_baseline_wallpaper_24)};
        BottomSheet.Builder builder = new BottomSheet.Builder(ChatRoom.this);
        builder.setItems(cs, drawable, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0) {
                            SharedPreferences sharedPreferences = getSharedPreferences("ChatWithMe",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.remove("backgroundPath"+username);
                            editor.apply();
                            loadBackgroundImage();
                        } else if(which == 1) {
                            changeWallpaper();
                        }
                        dialog.dismiss();
                    }
                }).show();
    }

    private void openGiphymenu() {
        GPHSettings settings = new GPHSettings();
        GiphyDialogFragment.Companion.newInstance(settings,GIPHY_KEY,true).show(getSupportFragmentManager(),"giphy");
    }

    private void updateStatus(Boolean istyping) {
        String newstatus = "Online";
        if(istyping) {
            newstatus = "typing]-]"+username;
        }
        MessageMaker.getFirebaseService().readFromFireStore("Users").document(MessageMaker.getMyNumber()).collection("AccountInfo").document("PersonalInfo").update("currentStatus",newstatus).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        User user = MessageMaker.getDatabaseHelper().getUser(username);
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
                showBottomSheetForWallpaper();
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
                       MessageMaker.getFirebaseService().muteChat(username);
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
                        MessageMaker.getFirebaseService().unmuteChat(username);
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
                        MessageMaker.getDatabaseHelperChat().clearAll(roomid);
                        MessageMaker.getDatabaseHelper().clearLastMessage(username);
                        chatLayoutView.reload();
                        addInfoMessage();
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
                        MessageMaker.getFirebaseService().block(username,roomid);
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
                        MessageMaker.getFirebaseService().unfreind(username,ChatRoom.this);
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
                        MessageMaker.getFirebaseService().saveToFireStore("Chats").document(roomid).collection("Messages").document("BLOCKED_"+username).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                MessageMaker.getFirebaseService().unblock(username,roomid);
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
            } else if (resultCode == Activity.RESULT_OK && requestCode == 12345) {
                selectedUrls = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                sendMediaMessage(selectedUrls,"IMAGE");
            } else if (resultCode == Activity.RESULT_OK && requestCode == 12346) {
                selectedUrls = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                sendMediaMessage(selectedUrls,"VIDEO");
            } else if (resultCode == Activity.RESULT_OK && requestCode == Picker.PICK_AUDIO) {
                if(audioPicker != null) {
                    audioPicker.submit(data);
                }
            } else if (resultCode == Activity.RESULT_OK && requestCode == Picker.PICK_FILE) {
                if(filePicker != null) {
                    filePicker.submit(data);
                }
            } else if(requestCode==299221) {
                String url=data.getStringExtra("url");
                String type=data.getStringExtra("type");
                if(type.equalsIgnoreCase("GIF")) {
                    sendGif(url);
                } else {
                    sendSticker(url);
                }
            }   else {
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

    private void sendAudioFile(ChosenAudio uri) {
        Message message = MessageMaker.getDefaultObject(myUsername,username,roomid);
        message.setMessageType(MessageType.AUDIO);
        message.addSharedFile(getSharedFileObject(uri.getOriginalPath(),"AUDIO"));
        chatLayoutView.addMessage(message);
    }

    private void sendDocumentFile(ChosenFile chosenFile) {
        Message message = MessageMaker.getDefaultObject(myUsername,username,roomid);
        message.setMessageType(MessageType.DOCUMENT);
        message.addSharedFile(getSharedFileObject(chosenFile.getOriginalPath(),"DOCUMENT"));
        chatLayoutView.addMessage(message);
    }

    private void sendMediaMessage(ArrayList<String> selectedUrls, String type) {
        Message message;
        SharedFile sharedFile = null;
        if(selectedUrls.size() > 0 && selectedUrls.size() <=3) {
            for(String url:selectedUrls) {
                sharedFile = getSharedFileObject(url,type);
                if(sharedFile != null) {
                    message = MessageMaker.getDefaultObject(myUsername,username,roomid);
                    message.addSharedFile(sharedFile);
                    if(type.equalsIgnoreCase("IMAGE")) {
                        message.setMessageType(MessageType.IMAGE);
                    } else if(type.equalsIgnoreCase("VIDEO")) {
                        message.setMessageType(MessageType.VIDEO);
                    } else {
                        message.setMessageType(MessageType.DOCUMENT);
                    }
                    chatLayoutView.addMessage(message);
                }
            }
        } else {
            message = MessageMaker.getDefaultObject(myUsername,username,roomid);
            for(String url:selectedUrls) {
                sharedFile = getSharedFileObject(url,type);
                if(sharedFile != null) {
                    message.addSharedFile(sharedFile);
                }
            }
            if(message.getSharedFiles().size() > 0) {
                if(type.equalsIgnoreCase("IMAGE")) {
                    message.setMessageType(MessageType.IMAGE);
                } else if(type.equalsIgnoreCase("VIDEO")) {
                    message.setMessageType(MessageType.VIDEO);
                } else {
                    message.setMessageType(MessageType.DOCUMENT);
                }
                chatLayoutView.addMessage(message);
            }
        }
        selectedUrls.clear();
    }

    private SharedFile getSharedFileObject(String filepath,String type) {
        SharedFile sharedFile = new SharedFile();
        File file = new File(filepath);
        sharedFile.setFileId(MessageMaker.createFileId());
        sharedFile.setLocalPath(filepath);
        sharedFile.setName(file.getName());
        int index = sharedFile.getName().lastIndexOf('.');
        if(index > 0) {
            String extension = sharedFile.getName().substring(index + 1);
            sharedFile.setExtension(extension);
        }
        if(type.equalsIgnoreCase("VIDEO")) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(getApplicationContext(), Uri.fromFile(file));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInMillisec = Long.parseLong(time );
            retriever.release();
            sharedFile.setDuration(timeInMillisec);
        }
        sharedFile.setSize(file.length());
        sharedFile.setPreviewUrl("");
        sharedFile.setUrl("");
        return sharedFile;
    }

    private void sendGif(String uri) {
        Message message = MessageMaker.getDefaultObject(myUsername,username,roomid);
        message.setMessageType(MessageType.GIF);
        message.setMessage(uri);
        sendNow(message,true);
    }

    private void sendSticker(String uri) {
        Message message = MessageMaker.getDefaultObject(myUsername,username,roomid);
        message.setMessageType(MessageType.STICKER);
        message.setMessage(uri);
        sendNow(message,true);
    }

    private void sendContacts(List<ContactResult> results) {
        Message message =MessageMaker.getDefaultObject(myUsername,username,roomid);
        message.setMessage("");
        message.setMessageType((results.size() > 1)?MessageType.CONTACT_MULTIPLE:MessageType.CONTACT_SINGLE);
        for(ContactResult contactResult:results) {
            message.addContact(convertToContact(contactResult));
        }
        sendNow(message,true);
    }

    private Contact convertToContact(ContactResult contactResult) {
        Contact contact = new Contact();
        contact.setName(contactResult.getDisplayName());
        User user = null;
        for(PhoneNumber phoneNumber:contactResult.getPhoneNumbers()) {
            contact.setContactNumbers(MessageMaker.getNormalizedPhoneNumber(phoneNumber.getNumber()));
            user = MessageMaker.getDatabaseHelper().getUser(contact.getContactNumbers());
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


    private void toggleAttachmentPanel() {
        if(attachemntPane.getVisibility() == View.GONE) {
            attachment.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
            attachemntPane.setVisibility(View.VISIBLE);
        } else {
            attachment.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
            attachemntPane.setVisibility(View.GONE);
        }
    }


    private void init() {
        attachemntPane = findViewById(R.id.attachmentpane);
        attachment = findViewById(R.id.attachment);
        replyview = findViewById(R.id.replyview);
        contacts = findViewById(R.id.contact);
        gif = findViewById(R.id.gif);
        cancel = findViewById(R.id.cancel_reply);
        imagebutton = findViewById(R.id.image);
        audioButton = findViewById(R.id.audio);
        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAttachmentPanel();
                audioPicker = new AudioPicker(ChatRoom.this);
                audioPicker.allowMultiple();
                audioPicker.setAudioPickerCallback(new AudioPickerCallback() {
                    @Override
                    public void onAudiosChosen(List<ChosenAudio> list) {
                        for(ChosenAudio chosenAudio:list) {
                            sendAudioFile(chosenAudio);
                        }
                    }

                    @Override
                    public void onError(String s) {

                    }
                });
                audioPicker.pickAudio();
            }
        });
         documentButton = findViewById(R.id.document);
        documentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAttachmentPanel();
                filePicker = new FilePicker(ChatRoom.this);
                filePicker.allowMultiple();
                filePicker.setFilePickerCallback(new FilePickerCallback() {
                    @Override
                    public void onError(String s) {

                    }

                    @Override
                    public void onFilesChosen(List<ChosenFile> list) {
                        for(ChosenFile chosenFile:list) {
                            sendDocumentFile(chosenFile);
                        }
                    }
                });
                filePicker.pickFile();
            }
        });
        imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAttachmentPanel();
                Options options = Options.init()
                        .setRequestCode(12345)                                           //Request code for activity results
                        .setCount(30)//Number of images to restict selection count
                        .setFrontfacing(false)                                         //Front Facing camera on start
                        .setPreSelectedUrls(selectedUrls)                               //Pre selected Image Urls
                        .setSpanCount(4)                                               //Span count for gallery min 1 & max 5
                        .setMode(Options.Mode.Picture)                                     //Option to select only pictures or videos or both
                        .setVideoDurationLimitinSeconds(30)                            //Duration for video recording
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                        .setPath("/pix/images");                                       //Custom Path For media Storage
                Pix.start(ChatRoom.this, options);
            }
        });

        CircleButton videobutton = findViewById(R.id.video);
        videobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAttachmentPanel();
                Options options = Options.init()
                        .setRequestCode(12346)//Request code for activity results
                        .setCount(30)//Number of images to restict selection count
                        .setFrontfacing(false)                                         //Front Facing camera on start
                        .setPreSelectedUrls(selectedUrls)                               //Pre selected Image Urls
                        .setSpanCount(4)                                               //Span count for gallery min 1 & max 5
                        .setMode(Options.Mode.Video)                                     //Option to select only pictures or videos or both
                        .setVideoDurationLimitinSeconds(30)                            //Duration for video recording
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                        .setPath("/pix/images");                                       //Custom Path For media Storage
                Pix.start(ChatRoom.this, options);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repliedMessage = null;
                replyview.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
            }
        });
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
                toggleAttachmentPanel();
            }
        });
        gif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAttachmentPanel();
                openGiphymenu();
            }
        });
        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAttachmentPanel();
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
                if(LayoutService.containsURL(s.toString()) && repliedMessage == null) {
                    isWithLink = true;
                } else {
                    isWithLink = false;
                }
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
            listner = MessageMaker.getFirebaseService().readFromFireStore("Chats").document(roomid).collection("Messages").orderBy("messageId", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error == null) {
                        for(DocumentChange doc:value.getDocumentChanges()) {
                            Message message = doc.getDocument().toObject(Message.class);
                            Message messageUpdate = MessageMaker.filterMessage(message);
                            if(messageUpdate != null) {
                                switch (doc.getType()) {
                                    case ADDED:
                                        decryptMessage(messageUpdate,false);
                                        break;
                                    case MODIFIED:
                                        decryptMessage(messageUpdate,true);
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
                                    MessageMaker.getFirebaseService().saveToFireStore("Chats").document(roomid).collection("Messages").document("UNBLOCKED_"+username).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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

    private void decryptMessage(Message messageUpdate,Boolean update) {
        try {
            if(messageUpdate.getMessage() != null && messageUpdate.getMessage().length() > 0) {
                messageUpdate.setMessage(AESCrypt.decrypt(roomSecurityKey,messageUpdate.getMessage()));
            }
            if(update) {
                chatLayoutView.updateMessage(messageUpdate);
            } else {
                chatLayoutView.addMessage(messageUpdate);
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
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

            }
        });
    }

    private void sendMessage(String messageText) {
        Message message = MessageMaker.getDefaultObject(myUsername,username,roomid);
        message.setMessage(messageText);
        if(isWithLink) {
            message.setMessageType(MessageType.LINK);
        }
        sendNow(message,true);
    }


    private void sendNow(Message message,Boolean addInLayout) {
        if(message.getMessageType() == MessageType.TEXT || message.getMessageType() == MessageType.LINK) {
            messageBox.setText("");
        }
        if(repliedMessage != null ){
            message.setIsRepliedMessage(true);
            message.setReplyMessage(repliedMessage);
            message.setRepliedMessageId(repliedMessage.getMessageId());
        }
        if(addInLayout) {
            Message message1 = message.clone();
            chatLayoutView.addMessage(message1);
        }
        repliedMessage = null;
        replyview.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        try {
            if(!isBlocked) {
                if(message.getMessage() != null && message.getMessage().length() > 0 ) {
                    message.setMessage(AESCrypt.encrypt(roomSecurityKey,message.getMessage()));
                }
                message.setMessageStatus(MessageStatus.SENT);
                MessageMaker.getFirebaseService().saveToFireStore("Chats").document(roomid).collection("Messages").document(message.getMessageId()).set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        decryptMessage(message,true);
                        MessageMaker.getFirebaseService().updateLastMessage(myUsername,username,message);
                    }
                });
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private void setupChatLayoutView() {
        chatLayoutView.setup(myUsername, roomid, true,MessageMaker.getDatabaseHelperChat(), new ChatLayoutListener() {
            @Override
            public void onSwipeToReply(Message message, View replyView) {
                if(replyview != null) {
                    replyview.setVisibility(View.VISIBLE);
                    LayoutService.updateReplyView(message,replyview);
                    repliedMessage = message;
                    cancel.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onUpload(Message message, ProgressButton progressButton) {
                startUploading(message);
            }

            @Override
            public void onDownload(Message message, ProgressButton progressButton) {

            }

            @Override
            public void onUploadCanceled(Message message, ProgressButton progressButton) {

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
            public void onPlayAudio(SharedFile sharedFile) {

            }

            @Override
            public void onMessageSeenConfirmed(Message message) {
                if(!isBlocked) {
                    MessageMaker.getFirebaseService().saveToFireStore("Chats").document(roomid).collection("Messages").document(message.getMessageId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
                }
            }

            @Override
            public void onMessageSeen(Message message) {
                if(!isBlocked) {
                    Map<String,Object> mapToUpdate = new HashMap<>();
                    if(message.getReceivedAt() == null) {
                        mapToUpdate.put("receivedAt",new Date());
                    }
                    mapToUpdate.put("messageStatus",MessageStatus.SEEN);
                    mapToUpdate.put("seenAt",new Date());
                    MessageMaker.getFirebaseService().saveToFireStore("Chats").document(roomid).collection("Messages").document(message.getMessageId()).update(mapToUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        myUser.setColorCode(MessageMaker.getFromSharedPrefrencesInt(getApplicationContext(),"colorCode"));
        chatLayoutView.addUser(myUser);
        User freinduser = MessageMaker.getDatabaseHelper().getUser(username);
        chatLayoutView.addUser(freinduser);
        chatLayoutView.loadSavedChat();
        loadBackgroundImage();
        addInfoMessage();
    }

    private void uploadPreview(Message message, SharedFile sharedFileWithUrl, ProgressButton progressButton,int finalI) {
        Bitmap thumb = null;
        if(message.getMessageType() == MessageType.VIDEO) {
            MediaMetadataRetriever m = new MediaMetadataRetriever();
            m.setDataSource(sharedFileWithUrl.getLocalPath());
            thumb = m.getFrameAtTime();
        } else if(message.getMessageType() == MessageType.IMAGE) {
            thumb = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(sharedFileWithUrl.getLocalPath()),50,50);
        }
        if(thumb != null) {
             UploadTask uploadTask = MessageMaker.getFirebaseService().uploadFromBitmap(sharedFileWithUrl.getFileId(),message.getRoomId()+"_Previews",thumb);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return MessageMaker.getFirebaseService().getStorageRef(message.getSharedFiles().get(finalI).getFileId(),message.getRoomId()).getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                SharedFile sharedFile = message.getSharedFiles().get(finalI);
                                sharedFile.setUrl(sharedFileWithUrl.getUrl());
                                sharedFile.setPreviewUrl(downloadUri.toString());
                                message.getSharedFiles().remove(finalI);
                                message.getSharedFiles().add(finalI,sharedFile);
                                chatLayoutView.getDatabaseHelper().updateSharedFileUrls(sharedFile.getFileId(),message.getMessageId(),message.getRoomId(),sharedFile.getUrl(),sharedFile.getPreviewUrl());
                                checkCompleted(message,progressButton);

                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    chatLayoutView.getDatabaseHelper().updateMessageUploadStatus(message.getRoomId(),message.getMessageId(), UploadStatus.FAILED);
                }
            });
        } else {
            SharedFile sharedFile = message.getSharedFiles().get(finalI);
            sharedFile.setUrl(sharedFileWithUrl.getUrl());
            sharedFile.setPreviewUrl(sharedFileWithUrl.getUrl());
            message.getSharedFiles().remove(finalI);
            message.getSharedFiles().add(finalI,sharedFile);
            chatLayoutView.getDatabaseHelper().updateSharedFileUrls(sharedFile.getFileId(),message.getMessageId(),message.getRoomId(),sharedFile.getUrl(),sharedFile.getPreviewUrl());
            checkCompleted(message,progressButton);
        }
    }

    private void startUploading(Message message) {
        for(int i=0;i<message.getSharedFiles().size();i++) {
            if(MessageMaker.getUploadTask(message.getMessageId(),message.getRoomId(),message.getSharedFiles().get(i).getFileId()) == null && (message.getSharedFiles().get(i).getUrl() == null || message.getSharedFiles().get(i).getUrl().length() == 0)) {
                UploadTask uploadTask = MessageMaker.getFirebaseService().uploadFromUri(message.getSharedFiles().get(i).getFileId(),message.getRoomId(),message.getSharedFiles().get(i).getLocalPath());
                int finalI = i;
                MessageMaker.addInUploadTaskMap(message.getMessageId(),message.getRoomId(),message.getSharedFiles().get(i).getFileId(),uploadTask);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return MessageMaker.getFirebaseService().getStorageRef(message.getSharedFiles().get(finalI).getFileId(),message.getRoomId()).getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    SharedFile sharedFile = message.getSharedFiles().get(finalI);
                                    sharedFile.setUrl(downloadUri.toString());
                                    uploadPreview(message,sharedFile,LayoutService.getUploadViewProgressButton(message.getMessageId(),message.getRoomId()),finalI);
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        chatLayoutView.getDatabaseHelper().updateMessageUploadStatus(message.getRoomId(),message.getMessageId(), UploadStatus.FAILED);
                        LayoutService.getUploadViewProgressButton(message.getMessageId(),message.getRoomId()).setUploadType();
                        LayoutService.getUploadViewProgressButton(message.getMessageId(),message.getRoomId()).setLabel("Retry");
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        if(message.getSharedFiles().size() == 1) {
                            double progress = ((double) snapshot.getBytesTransferred() /(double) snapshot.getTotalByteCount()) * 100;
                            LayoutService.getUploadViewProgressButton(message.getMessageId(),message.getRoomId()).setProgress(progress);
                        }
                    }
                });
            } else {

            }
        }
    }

    private void checkCompleted(Message message,ProgressButton progressButton) {
        Boolean isCompleted = true;
        int total = message.getSharedFiles().size();
        int completed = 0;
        double progress = 0;
        for(SharedFile sharedFile:message.getSharedFiles()) {
            if(sharedFile.getUrl() == null || sharedFile.getUrl().length() == 0) {
                isCompleted = false;
            } else {
                completed ++;
                progress = ((double) completed / (double) total) * 100;
                progressButton.setProgress(progress);
            }
        }
        if(isCompleted) {
            sendNow(message,false);
        }
    }

    private void addContactLocal(Contact contact) {
        MessageMaker.openAddContact(getApplicationContext(),contact);
    }

    private void sendRequest(User user) {
        FreindRequestSaveDTO freindRequestSaveDTO = new FreindRequestSaveDTO();
        freindRequestSaveDTO.setRequestSentBy(myUsername);
        freindRequestSaveDTO.setRequestSentAt(new Date());
        freindRequestSaveDTO.setContactNumber(user.getContactNumber());
        MessageMaker.getFirebaseService().saveToFireStore("Users").document(myUsername).collection("FreindRequests").document("Sent").collection("List").document(user.getContactNumber()).set(freindRequestSaveDTO).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                freindRequestSaveDTO.setContactNumber(myUsername);
                MessageMaker.getFirebaseService().saveToFireStore("Users").document(user.getContactNumber()).collection("FreindRequests").document("Receive").collection("List").document(myUsername).set(freindRequestSaveDTO).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"Freind Request Sent to "+user.getName(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void OpenInChat(Contact contact) {
        Boolean userExist = MessageMaker.getDatabaseHelper().isUserExist(contact.getContactNumbers());
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
                MessageMaker.getFirebaseService().readFromFireStore("Users").document(contact.getContactNumbers()).collection("AccountInfo").document("PersonalInfo").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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

    @Override
    public void didSearchTerm(@NotNull String s) {

    }

    @Override
    public void onDismissed(@NotNull GPHContentType gphContentType) {

    }

    @Override
    public void onGifSelected(@NotNull Media media, @org.jetbrains.annotations.Nullable String s, @NotNull GPHContentType gphContentType) {
        String type = gphContentType.getMediaType().name();
        String url = media.getEmbedUrl();
        url = url.replaceAll("https://giphy.com/embed","https://media.giphy.com/media");
        url += "/giphy.gif";
        if(type.equalsIgnoreCase("gif")) {
            sendGif(url);
        } else if(type.equalsIgnoreCase("sticker") || type.equalsIgnoreCase("text")) {
            sendSticker(url);
        }
    }

}