package com.nobodyknows.chatwithme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.nobodyknows.chatwithme.Activities.Signup.CreateUser;
import com.nobodyknows.chatwithme.MainActivity;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.FirebaseService;

public class Login extends AppCompatActivity {

    FirebaseService firebaseService;
    ImageView back,info;
    EditText number;
    Button continueButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        firebaseService = new FirebaseService();
        init();
    }

    private void init() {
        back = findViewById(R.id.backtomain);
        info = findViewById(R.id.info);
        number = findViewById(R.id.number);
        continueButton = findViewById(R.id.continuelogin);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contact = number.getText().toString();
                if(contact != null && contact.length() > 0 && contact.length() <=15) {
                    validContact(contact);
                } else {
                    Toast.makeText(getApplicationContext(),"Invalid Mobile number.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void validContact(String number) {
        continueButton.setText("Checking");
        continueButton.setEnabled(false);
        firebaseService.readFromFireStore("Users").document(number).collection("AccountInfo").document("PersonalInfo").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    if(task.getResult().exists()) {
                        Intent intent = new Intent(getApplicationContext(), LoginContinue.class);
                        intent.putExtra("number",number);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), CreateUser.class);
                        intent.putExtra("number",number);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    continueButton.setText("Continue");
                    continueButton.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"Unable to process your request",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}