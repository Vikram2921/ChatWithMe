package com.nobodyknows.chatwithme.Activities.Signup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.nobodyknows.chatwithme.R;

public class MobileVerification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verification);
        getSupportActionBar().hide();
    }
}