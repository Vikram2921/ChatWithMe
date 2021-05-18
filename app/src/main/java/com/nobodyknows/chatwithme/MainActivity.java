package com.nobodyknows.chatwithme;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard;
import com.nobodyknows.chatwithme.Activities.Login;
import com.nobodyknows.chatwithme.Activities.Signup.CreatingSetup;
import com.nobodyknows.chatwithme.services.MessageHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = getSharedPreferences("ChatWithMe",MODE_PRIVATE);
        if(sharedPreferences.contains("number")) {
            if(sharedPreferences.getBoolean("setupDone",false)) {
                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(getApplicationContext(), CreatingSetup.class);
                startActivity(intent);
                finish();
            }
        } else {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
    }
}