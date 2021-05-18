package com.nobodyknows.chatwithme.services;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.NobodyKnows.chatlayoutview.Model.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FirebaseService {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    public FirebaseService() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseDatabase = FirebaseDatabase.getInstance("https://chatwithme-97538-default-rtdb.firebaseio.com/");
        databaseReference = firebaseDatabase.getReference();
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
        Map<String ,Object> dummyMap= new HashMap<>();
        uploadLastMessage(myUsername,fusernam,message);
        uploadLastMessage(fusernam,myUsername,message);
        firebaseFirestore.collection("Users").document(myUsername).collection("AccountInfo").document("RecentChats").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    if(!task.getResult().exists()) {
                        firebaseFirestore.collection("Users").document(myUsername).collection("AccountInfo").document("RecentChats").set(dummyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                uploadLastMessage(myUsername,fusernam,message);
                            }
                        });
                    } else {
                        uploadLastMessage(myUsername,fusernam,message);
                        firebaseFirestore.collection("Users").document(fusernam).collection("AccountInfo").document("RecentChats").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()) {
                                    if(!task.getResult().exists()) {
                                        firebaseFirestore.collection("Users").document(fusernam).collection("AccountInfo").document("RecentChats").set(dummyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                uploadLastMessage(fusernam,myUsername,message);
                                            }
                                        });
                                    } else {
                                        uploadLastMessage(fusernam,myUsername,message);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private Task<Void> uploadLastMessage(String sender, String receiver, Message message) {
        return saveToFireStore("Users").document(sender).collection("AccountInfo").document("RecentChats").collection("History").document(receiver).set(message);
    }

    public DatabaseReference getDatabaseRef(String bucket) {
        return databaseReference.child(bucket);
    }

    //https://cdn-api.co-vin.in/api/v2/appointment/sessions/calendarByDistrict?district_id=507&date=17-05-2021
}
