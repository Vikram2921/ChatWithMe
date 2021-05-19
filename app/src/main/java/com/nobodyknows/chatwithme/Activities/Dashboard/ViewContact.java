package com.nobodyknows.chatwithme.Activities.Dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.NobodyKnows.chatlayoutview.Model.User;
import com.nobodyknows.chatwithme.Activities.ChatRoom;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.MessageMaker;

import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.databaseHelper;

public class ViewContact extends AppCompatActivity {

    private ImageView back,verified,profile,chat,audio,video;
    private TextView name,status,statusupdatedate,mobilenumber;
    protected ConstraintLayout report,unfreind,block;
    private String username = "";
    private Boolean isFromChat = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact);
        username = getIntent().getStringExtra("username");
        isFromChat = getIntent().getBooleanExtra("isFromChat",false);
        getSupportActionBar().hide();
        init();
    }

    private void init() {
        back = findViewById(R.id.back);
        name = findViewById(R.id.name);
        status = findViewById(R.id.status);
        chat = findViewById(R.id.chat);
        audio = findViewById(R.id.audio);
        video = findViewById(R.id.video);
        statusupdatedate = findViewById(R.id.statusupdatedate);
        mobilenumber = findViewById(R.id.mobilenumber);
        verified = findViewById(R.id.verified);
        profile = findViewById(R.id.profile);
        report = findViewById(R.id.report);
        unfreind = findViewById(R.id.unfreind);
        block = findViewById(R.id.block);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        load();
    }

    private void load() {
        User user = databaseHelper.getUser(username);
        name.setText(user.getName());
        status.setText(user.getStatus());
        mobilenumber.setText(user.getContactNumber());
        if(user.getVerified()) {
            verified.setVisibility(View.VISIBLE);
        } else {
            verified.setVisibility(View.GONE);
        }
        MessageMaker.loadProfile(getApplicationContext(),user.getProfileUrl(),profile);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFromChat) {
                    Intent intent = new Intent(getApplicationContext(), ChatRoom.class);
                    intent.putExtra("username",user.getContactNumber());
                    intent.putExtra("name",user.getName());
                    intent.putExtra("lastOnlineStatus",user.getCurrentStatus());
                    intent.putExtra("verified",user.getVerified());
                    intent.putExtra("roomid", MessageMaker.createRoomId(getApplicationContext(),user.getContactNumber()));
                    intent.putExtra("profile",user.getProfileUrl());
                    startActivity(intent);
                }
                finish();
            }
        });
    }
}