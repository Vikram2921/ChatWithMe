package com.nobodyknows.chatwithme.Activities.Dashboard;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.nobodyknows.chatwithme.Activities.AudioCall;
import com.nobodyknows.chatwithme.Activities.ChatRoom;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.MessageMaker;
import com.vistrav.pop.Pop;

import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.databaseHelper;
import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.firebaseService;

public class ViewContact extends AppCompatActivity {

    private ImageView back,verified,profile,chat,audio,video;
    private TextView name,status,statusupdatedate,mobilenumber;
    protected ConstraintLayout report,unfreind,block;
    private String username = "";
    private Boolean isFromChat = false,isBlocked = false;
    private TextView blocktext;
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
        blocktext = findViewById(R.id.blocktext);
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
        isBlocked = user.getBlocked();
        if(isBlocked && user.getBlockedBy().equalsIgnoreCase(MessageMaker.getMyNumber())) {
            blocktext.setText("Unblock");
        }
        name.setText(user.getName());
        status.setText(user.getStatus());
        mobilenumber.setText(user.getContactNumber());
        if(user.getVerified()) {
            verified.setVisibility(View.VISIBLE);
        } else {
            verified.setVisibility(View.GONE);
        }
        MessageMaker.loadProfile(getApplicationContext(),user.getProfileUrl(),profile);
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AudioCall.class);
                intent.putExtra("username",username);
                intent.putExtra("making",true);
                intent.putExtra("video",false);
                startActivity(intent);
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AudioCall.class);
                intent.putExtra("username",username);
                intent.putExtra("making",true);
                intent.putExtra("video",true);
                startActivity(intent);
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFromChat) {
                    MessageMaker.startChatroom(getApplicationContext(),user.getContactNumber());
                }
                finish();
            }
        });
        unfreind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pop.on(ViewContact.this).with()
                        .cancelable(true)
                        .body("Are you sure you want to remove "+user.getName()+" from your freind list ?")
                        .when(R.string.unfreind,new Pop.Yah() {
                            @Override
                            public void clicked(DialogInterface dialog, @Nullable View view) {
                                firebaseService.unfreind(username,ViewContact.this);
                            }
                        }).when(new Pop.Nah() {
                                @Override
                                public void clicked(DialogInterface dialog, @Nullable View view) {
                                }
                        }).show();
            }
        });
        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(blocktext.getText().equals("Unblock")) {
                    Pop.on(ViewContact.this).with()
                            .cancelable(true)
                            .body("Are you sure you want to unblock "+user.getName()+" ?")
                            .when(R.string.unblockstring,new Pop.Yah() {
                                @Override
                                public void clicked(DialogInterface dialog, @Nullable View view) {
                                    firebaseService.unblock(username, MessageMaker.createRoomId(username));
                                    blocktext.setText("Block");
                                }
                            }).when(new Pop.Nah() {
                        @Override
                        public void clicked(DialogInterface dialog, @Nullable View view) {
                        }
                    }).show();
                } else {
                    Pop.on(ViewContact.this).with()
                            .cancelable(true)
                            .body("Are you sure you want to block "+user.getName()+" from your freind list ?")
                            .when(R.string.blockstring,new Pop.Yah() {
                                @Override
                                public void clicked(DialogInterface dialog, @Nullable View view) {
                                    firebaseService.block(username, MessageMaker.createRoomId(username));
                                    blocktext.setText("Unblock");
                                }
                            }).when(new Pop.Nah() {
                        @Override
                        public void clicked(DialogInterface dialog, @Nullable View view) {
                        }
                    }).show();
                }
            }
        });
    }
}