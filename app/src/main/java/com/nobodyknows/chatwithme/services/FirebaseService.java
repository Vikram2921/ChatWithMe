package com.nobodyknows.chatwithme.services;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.NobodyKnows.chatlayoutview.Constants.MessageStatus;
import com.NobodyKnows.chatlayoutview.Constants.MessageType;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nobodyknows.chatwithme.DTOS.FreindRequestSaveDTO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.databaseHelper;
import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.databaseHelperChat;
import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.firebaseService;
import static com.nobodyknows.chatwithme.Fragments.ChatFragment.recyclerViewAdapter;
import static com.nobodyknows.chatwithme.Fragments.ChatFragment.userListItemDTOMap;
import static com.nobodyknows.chatwithme.Fragments.ChatFragment.userListItems;

public class FirebaseService {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    public FirebaseService() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.clearPersistence();
        firebaseFirestore.setFirestoreSettings(settings);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseDatabase = FirebaseDatabase.getInstance("https://chatwithme-97538-default-rtdb.firebaseio.com/");
        databaseReference = firebaseDatabase.getReference();
    }

    public void enableNetwork() {
        firebaseFirestore.enableNetwork();
        firebaseDatabase.goOnline();
    }

    public void disableNetwork() {
        firebaseFirestore.disableNetwork();
        firebaseDatabase.goOffline();
    }

    public CollectionReference saveToFireStore(String collectionName) {
        return firebaseFirestore.collection(collectionName);
    }

    public CollectionReference readFromFireStore(String collectionName) {
        return firebaseFirestore.collection(collectionName);
    }

    public UploadTask uploadFromStream(String filename, String folder, String url) throws FileNotFoundException {
        InputStream stream = new FileInputStream(new File(url));
        return storageReference.child(folder).child(filename).putStream(stream);
    }

    public UploadTask uploadFromUri(String filename, String folder, String path)  {
        Uri file = Uri.fromFile(new File(path));
        return storageReference.child(folder).child(filename).putFile(file);
    }

    public UploadTask uploadFromUri(String filename, String folder, Uri uri)  {
        return storageReference.child(folder).child(filename).putFile(uri);
    }

    public StorageReference getStorageRef(String filename, String folder) {
        return storageReference.child(folder).child(filename);
    }

    public UploadTask uploadFromImageView(String filename, String folder, CircleImageView circleImageView) {
        circleImageView.setDrawingCacheEnabled(true);
        circleImageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) circleImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        return storageReference.child(folder).child(filename).putBytes(data);
    }



    public void updateLastMessage(String myUsername,String fusernam,Message message) {
        uploadLastMessage(myUsername,fusernam,message);
        uploadLastMessage(fusernam,myUsername,message);
    }

    private Task<Void> uploadLastMessage(String sender, String receiver, Message message) {
        return saveToFireStore("Users").document(sender).collection("AccountInfo").document("RecentChats").collection("History").document(receiver).set(message);
    }

    public DatabaseReference getDatabaseRef(String bucket) {
        return databaseReference.child(bucket);
    }

    public void unfreind(String username, Activity activity) {
        String mynumber = MessageMaker.getMyNumber();
        User user = databaseHelper.getUser(username);
        KProgressHUD pop = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel("Removing "+user.getName()+" from your freind list.")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        saveToFireStore("Users").document(mynumber).collection("Freinds").document(username).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                saveToFireStore("Users").document(username).collection("Freinds").document(mynumber).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        firebaseService.readFromFireStore("Users").document(MessageMaker.getMyNumber()).collection("AccountInfo").document("RecentChats").collection("History").document(username).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                firebaseService.readFromFireStore("Users").document(username).collection("AccountInfo").document("RecentChats").collection("History").document(MessageMaker.getMyNumber()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        firebaseService.saveToFireStore("Chats").document(MessageMaker.createRoomId(username)).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                databaseHelperChat.deleteMessagesOf(MessageMaker.createRoomId(username));
                                                databaseHelper.deleteRecentChat(username);
                                                databaseHelper.deleteUser(username);
                                                MessageMaker.removeFromRecentChatUI(username);
                                                pop.dismiss();
                                                activity.finish();
                                            }
                                        });
                                    }
                                });
                            }
                        });

                    }
                });
            }
        });
    }

    public void block(String username,String roomid) {
        String mynumber = MessageMaker.getMyNumber();
        saveToFireStore("Users").document(mynumber).collection("Freinds").document(username).update("blocked",true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Message message  = new Message();
                message.setMessageId("BLOCKED_"+username);
                message.setReceiver(username);
                message.setSender(mynumber);
                message.setRoomId(roomid);
                message.setMessage("BLOCKED BY "+mynumber);
                message.setMessageType(MessageType.BLOCKED);
                saveToFireStore("Chats").document(roomid).collection("Messages").document(message.getMessageId()).set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        databaseHelper.setBlockStatus(username,mynumber,true);
                        firebaseService.updateLastMessage(mynumber,username,message);
                    }
                });
            }
        });
    }

    public void unblock(String username,String roomid) {
        String mynumber = MessageMaker.getMyNumber();
        saveToFireStore("Users").document(mynumber).collection("Freinds").document(username).update("blocked",false).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Message message  = new Message();
                message.setMessageId("UNBLOCKED_"+username);
                message.setReceiver(username);
                message.setSender(mynumber);
                message.setRoomId(roomid);
                message.setMessage("UNBLOCKED BY "+mynumber);
                message.setMessageType(MessageType.UNBLOCKED);
                saveToFireStore("Chats").document(roomid).collection("Messages").document(message.getMessageId()).set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        databaseHelper.setBlockStatus(username,mynumber,false);
                        firebaseService.updateLastMessage(mynumber,username,message);
                    }
                });
            }
        });
    }

    public void muteChat(String username) {
        databaseHelper.setMuteStatus(username,true);
    }

    public void unmuteChat(String username) {
        databaseHelper.setMuteStatus(username,false);
    }
}
