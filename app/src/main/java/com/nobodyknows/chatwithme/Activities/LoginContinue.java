package com.nobodyknows.chatwithme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard;
import com.nobodyknows.chatwithme.Models.Users;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.FirebaseService;

public class LoginContinue extends AppCompatActivity {

    private FirebaseService firebaseService;
    private String number;
    private EditText password;
    private Button continueLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_continue);
        getSupportActionBar().hide();
        number = getIntent().getStringExtra("number");
        firebaseService = new FirebaseService();
        init();
    }

    public void init() {
        password = findViewById(R.id.password);
        continueLogin = findViewById(R.id.continuelogin);
        continueLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = password.getText().toString();
                if(!pass.isEmpty()) {
                    checkLogin(pass,number);
                } else {
                    Toast.makeText(getApplicationContext(),"Invalid Password",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkLogin(String pass, String number) {
        continueLogin.setText("Checking");
        continueLogin.setEnabled(false);
        firebaseService.readFromFireStore("Users").document(number).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot != null) {
                    Users users = documentSnapshot.toObject(Users.class);
                    if(users.getPassword().equals(pass)) {
                        SharedPreferences sharedPreferences = getSharedPreferences("ChatWithMe",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("number",users.getContactNumber());
                        editor.putString("name",users.getName());
                        editor.putString("profile",users.getProfileUrl());
                        editor.putString("status",users.getStatus());
                        editor.commit();
                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                        startActivity(intent);
                        finish();
                    } else {
                        continueLogin.setText("Let me in");
                        continueLogin.setEnabled(true);
                        Toast.makeText(getApplicationContext(),"Incorrect Password",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    continueLogin.setText("Let me in");
                    continueLogin.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"Unable to process your request",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}