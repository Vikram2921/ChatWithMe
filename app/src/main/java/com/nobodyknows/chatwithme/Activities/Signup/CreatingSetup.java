package com.nobodyknows.chatwithme.Activities.Signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.FirebaseService;
import com.nobodyknows.chatwithme.services.MessageMaker;

import java.util.HashMap;
import java.util.Map;

public class CreatingSetup extends AppCompatActivity {

    private FirebaseService firebaseService;
    private Map<String ,Object> dummyMap= new HashMap<>();
    String myUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_setup);
        getSupportActionBar().hide();
        myUsername = MessageMaker.getFromSharedPrefrences(getApplicationContext(),"number");
        firebaseService = new FirebaseService();
        startSetup();
    }

    private void startSetup() {
        firebaseService.readFromFireStore("Users").document(myUsername).collection("AccountInfo").document("RecentChats").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    if(!task.getResult().exists()) {
                        firebaseService.readFromFireStore("Users").document(myUsername).collection("AccountInfo").document("RecentChats").set(dummyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                createFreindRequestFolder("Receive");
                            }
                        });
                    } else {
                        createFreindRequestFolder("Receive");
                    }
                }
            }
        });
    }


    private void createFreindRequestFolder(String innerFolder) {
        firebaseService.readFromFireStore("Users").document(myUsername).collection("FreindRequests").document(innerFolder).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    if(!task.getResult().exists()) {
                        firebaseService.readFromFireStore("Users").document(myUsername).collection("FreindRequests").document(innerFolder).set(dummyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(innerFolder.equals("Sent")) {
                                    changeScreen();
                                } else {
                                    createFreindRequestFolder("Sent");
                                }
                            }
                        });
                    } else {
                        if(innerFolder.equals("Sent")) {
                            changeScreen();
                        } else {
                            createFreindRequestFolder("Sent");
                        }
                    }
                }
            }
        });
    }


    private void changeScreen() {
        SharedPreferences sharedPreferences = getSharedPreferences("ChatWithMe",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("setupDone",true);
        editor.apply();
        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
        startActivity(intent);
        finish();
    }
}