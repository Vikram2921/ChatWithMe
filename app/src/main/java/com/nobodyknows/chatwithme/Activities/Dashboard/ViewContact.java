package com.nobodyknows.chatwithme.Activities.Dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nobodyknows.chatwithme.Activities.AudioCall;
import com.nobodyknows.chatwithme.Activities.ChatRoom;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.MessageMaker;
import com.vistrav.pop.Pop;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewContact extends AppCompatActivity {

    private ImageView verified,profile,chat,audio,video;
    private TextView name,mobilenumber,bio,dob,usernametext,status,statusupdatedate;
    protected ConstraintLayout defaultView;
    private Button editProfile,block,unfreind;
    private String username = "";
    private AlertDialog pop;
    private ImageView editStatus;
    private Boolean isFromChat = false,isBlocked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact);
        username = getIntent().getStringExtra("username");
        isFromChat = getIntent().getBooleanExtra("isFromChat",false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setElevation(0);
        init();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    private void init() {
        defaultView = findViewById(R.id.defaultView);
        editStatus = findViewById(R.id.editstatus);
        editProfile = findViewById(R.id.editprofile);
        name = findViewById(R.id.name);
        usernametext = findViewById(R.id.username);
        mobilenumber = findViewById(R.id.number);
        dob = findViewById(R.id.dob);
        bio = findViewById(R.id.bio);
        chat = findViewById(R.id.chat);
        status = findViewById(R.id.status);
        statusupdatedate= findViewById(R.id.statusupdatedate);
        audio = findViewById(R.id.audio);
        video = findViewById(R.id.video);
        verified = findViewById(R.id.verified);
        profile = findViewById(R.id.profile);
        unfreind = findViewById(R.id.unfreind);
        block = findViewById(R.id.block);
        load();
    }

    private void load() {
        User user = null;
        if(username.equals(MessageMaker.getMyNumber())) {
            defaultView.setVisibility(View.GONE);
            chat.setVisibility(View.GONE);
            editStatus.setVisibility(View.VISIBLE);
            video.setVisibility(View.GONE);
            audio.setVisibility(View.GONE);
            editProfile.setVisibility(View.VISIBLE);
            user = new User();
            user.setName(MessageMaker.getFromSharedPrefrences(getApplicationContext(),"name"));
            user.setProfileUrl(MessageMaker.getFromSharedPrefrences(getApplicationContext(),"profile"));
            user.setStatus(MessageMaker.getFromSharedPrefrences(getApplicationContext(),"status"));
            user.setBio(MessageMaker.getFromSharedPrefrences(getApplicationContext(),"bio"));
            user.setUsername(MessageMaker.getFromSharedPrefrences(getApplicationContext(),"username"));
            user.setDateOfBirth(MessageMaker.StringToDate(MessageMaker.getFromSharedPrefrences(getApplicationContext(),"dob"),"yyyy-MM-dd"));
            user.setStatusUpdateDate(MessageMaker.StringToDate(MessageMaker.getFromSharedPrefrences(getApplicationContext(),"statusupdatedate"),"yyyy-MM-dd"));
            user.setContactNumber(username);
            user.setColorCode(MessageMaker.getFromSharedPrefrencesInt(getApplicationContext(),"colorCode"));
            editProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),UpdateProfile.class);
                    startActivityForResult(intent,1122);
                }
            });
            editStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pop = Pop.on(ViewContact.this).with()
                            .cancelable(true)
                            .layout(R.layout.updatestatusview).show(new Pop.View() {
                        @Override
                        public void prepare(@Nullable View view) {
                            EditText statustext = view.findViewById(R.id.status);
                            statustext.setText(MessageMaker.getFromSharedPrefrences(getApplicationContext(),"status"));
                            Button save = view.findViewById(R.id.save);
                            Button cancel = view.findViewById(R.id.cancel);
                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    KProgressHUD kpop = KProgressHUD.create(ViewContact.this)
                                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                            .setLabel("Please wait")
                                            .setDetailsLabel("Updating your profile.")
                                            .setCancellable(false)
                                            .setAnimationSpeed(2)
                                            .setDimAmount(0.5f)
                                            .show();
                                    Map<String,Object> update = new HashMap<>();
                                    update.put("status",statustext.getText().toString());
                                    update.put("statusUpdateDate",new Date());
                                    MessageMaker.getFirebaseService().readFromFireStore("Users").document(MessageMaker.getMyNumber()).collection("AccountInfo").document("PersonalInfo").update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            SharedPreferences sharedPreferences = getSharedPreferences("ChatWithMe",MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("status",statustext.getText().toString());
                                            editor.putString("statusupdatedate",MessageMaker.formatDate(new Date(),"yyyy-MM-dd"));
                                            editor.apply();
                                            status.setText(statustext.getText().toString());
                                            statusupdatedate.setText(MessageMaker.formatDate(new Date(),"dd MMMM yyyy"));
                                            kpop.dismiss();
                                            pop.dismiss();
                                        }
                                    });
                                }
                            });
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pop.dismiss();
                                }
                            });
                        }
                    });
                }
            });
        } else {
            defaultView.setVisibility(View.VISIBLE);
            editProfile.setVisibility(View.GONE);
            user = MessageMaker.getDatabaseHelper().getUser(username);
        }
        user.setUsername(MessageMaker.decryptForFirebaseKey(user.getUsername()));
        isBlocked = user.getBlocked();
        if(isBlocked && user.getBlockedBy().equalsIgnoreCase(MessageMaker.getMyNumber())) {
            block.setText("Unblock");
        }
        name.setText(user.getName());
        if(user.getBio() == null || user.getBio().length() == 0) {
            bio.setHint(user.getName()+" have not setup bio profile.");
        } else {
            bio.setText(user.getBio());
        }
        dob.setText(MessageMaker.formatDate(user.getDateOfBirth(),"dd MMMM yyyy"));
        if(user.getUsername() == null || user.getUsername().length() == 0) {
           usernametext.setVisibility(View.GONE);
        } else {
            usernametext.setText(user.getUsername());
        }
        mobilenumber.setText(user.getContactNumber());
        if(user.getVerified()) {
            verified.setVisibility(View.VISIBLE);
        } else {
            verified.setVisibility(View.GONE);
        }
        status.setText(user.getStatus());
        statusupdatedate.setText(MessageMaker.formatDate(user.getStatusUpdateDate(),"dd MMMM yyyy"));
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
        User finalUser = user;
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFromChat) {
                    MessageMaker.startChatroom(getApplicationContext(), finalUser.getContactNumber());
                }
                finish();
            }
        });
        unfreind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pop.on(ViewContact.this).with()
                        .cancelable(true)
                        .body("Are you sure you want to remove "+ finalUser.getName()+" from your freind list ?")
                        .when(R.string.unfreind,new Pop.Yah() {
                            @Override
                            public void clicked(DialogInterface dialog, @Nullable View view) {
                                MessageMaker.getFirebaseService().unfreind(username,ViewContact.this);
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
                if(block.getText().equals("Unblock")) {
                    Pop.on(ViewContact.this).with()
                            .cancelable(true)
                            .body("Are you sure you want to unblock "+ finalUser.getName()+" ?")
                            .when(R.string.unblockstring,new Pop.Yah() {
                                @Override
                                public void clicked(DialogInterface dialog, @Nullable View view) {
                                    MessageMaker.getFirebaseService().unblock(username, MessageMaker.createRoomId(username));
                                    block.setText("Block");
                                }
                            }).when(new Pop.Nah() {
                        @Override
                        public void clicked(DialogInterface dialog, @Nullable View view) {
                        }
                    }).show();
                } else {
                    Pop.on(ViewContact.this).with()
                            .cancelable(true)
                            .body("Are you sure you want to block "+ finalUser.getName()+" from your freind list ?")
                            .when(R.string.blockstring,new Pop.Yah() {
                                @Override
                                public void clicked(DialogInterface dialog, @Nullable View view) {
                                    MessageMaker.getFirebaseService().block(username, MessageMaker.createRoomId(username));
                                    block.setText("Unblock");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == 1122){
               Boolean done = data.getBooleanExtra("done",false);
               if(done) {
                   load();
               }
            }
        }
    }
}