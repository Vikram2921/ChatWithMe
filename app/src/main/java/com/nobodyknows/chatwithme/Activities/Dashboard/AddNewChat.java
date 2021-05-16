package com.nobodyknows.chatwithme.Activities.Dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.nobodyknows.chatwithme.R;

public class AddNewChat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_chat);
        getSupportActionBar().hide();
    }
}