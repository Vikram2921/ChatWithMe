package com.nobodyknows.chatwithme.services;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class FirebaseService {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    public FirebaseService() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
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

}
