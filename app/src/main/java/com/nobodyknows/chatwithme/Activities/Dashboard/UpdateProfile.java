package com.nobodyknows.chatwithme.Activities.Dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.NobodyKnows.chatlayoutview.Model.Message;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.MessageMaker;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfile extends AppCompatActivity {

    private ImageView close,done;
    private CircleImageView profile;
    private EditText name,username,bio;
    private DatePicker datePicker;
    private String profilePath = "";
    private Button changePic;
    private String nameOld,usernameOld,bioOld;
    final Calendar c = Calendar.getInstance();
    int maxYear = c.get(Calendar.YEAR); // this year ( 2011 ) - 20 = 1991
    int maxMonth = c.get(Calendar.MONTH);
    int maxDay = c.get(Calendar.DAY_OF_MONTH);
    int minYear = 1960;
    int minMonth = 0; // january
    private KProgressHUD pop;
    int minDay = 25;
    private Date selecteddob;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        getSupportActionBar().hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        nameOld = MessageMaker.getFromSharedPrefrences(getApplicationContext(),"name");
        usernameOld = MessageMaker.getFromSharedPrefrences(getApplicationContext(),"username");
        bioOld = MessageMaker.getFromSharedPrefrences(getApplicationContext(),"bio");
        profilePath = MessageMaker.getFromSharedPrefrences(getApplicationContext(),"profile");
        String dobold = MessageMaker.getFromSharedPrefrences(getApplicationContext(),"dob");
        if(nameOld == null) {
            nameOld = "";
        }
        if(usernameOld == null) {
            usernameOld = "";
        }
        if(bioOld == null) {
            bioOld = "";
        }
        if(dobold == null) {
            selecteddob = new Date();
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                selecteddob = format.parse(dobold);
                selecteddob.setYear(selecteddob.getYear() + 1900);
                maxYear = selecteddob.getYear();
                maxMonth = selecteddob.getMonth();
                maxDay = selecteddob.getDate();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //selecteddob = new Date(dobold);
        }
        if(profilePath == null) {
            profilePath = "NO_PROFILE";
        }
        init();
    }

    private void init() {
        close = findViewById(R.id.close);
        done = findViewById(R.id.done);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity(false);
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfo();
            }
        });
        profile = findViewById(R.id.profilepic);
        changePic = findViewById(R.id.changepic);
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        bio = findViewById(R.id.bio);
        datePicker = findViewById(R.id.dob);
        datePicker.init(maxYear, maxMonth, maxDay, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                selecteddob = new Date(year,monthOfYear,dayOfMonth);
            }
        });
        name.setText(nameOld);
        username.setText(usernameOld);
        bio.setText(bioOld);
        MessageMaker.loadProfile(getApplicationContext(),profilePath,profile);
        changePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(UpdateProfile.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

    }

    private void updateInfo() {
        String number = MessageMaker.getMyNumber();
        String newname = name.getText().toString();
        String newusernae = username.getText().toString();
        String newbio = bio.getText().toString();
        if(newname == null || newname.length() == 0) {
            Toast.makeText(getApplicationContext(),"Invalid name provied",Toast.LENGTH_SHORT).show();
        } else {
            if(newusernae == null || newusernae.length() == 0) {
                Toast.makeText(getApplicationContext(),"Invalid username provied",Toast.LENGTH_SHORT).show();
            } else {
                pop = KProgressHUD.create(UpdateProfile.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("Please wait")
                        .setDetailsLabel("Updating your profile.")
                        .setCancellable(false)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f)
                        .show();
               if(MessageMaker.getFromSharedPrefrences(getApplicationContext(),"profile").equals(profilePath)) {
                   patch(newusernae,newname,newbio,profilePath);
               } else {
                   UploadTask uploadTask = MessageMaker.getFirebaseService().uploadFromUri(number+"_profile","Profiles",profilePath);
                   uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                               @Override
                               public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                   if (!task.isSuccessful()) {
                                       throw task.getException();
                                   }
                                   return MessageMaker.getFirebaseService().getStorageRef(number+"_profile","Profiles").getDownloadUrl();
                               }
                           }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                               @Override
                               public void onComplete(@NonNull Task<Uri> task) {
                                   if (task.isSuccessful()) {
                                       Uri downloadUri = task.getResult();
                                       patch(newusernae,newname,newbio,downloadUri.toString());
                                   } else {
                                       Toast.makeText(getApplicationContext(),"Failed to upload profile pic",Toast.LENGTH_SHORT).show();
                                   }
                               }
                           });
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(getApplicationContext(),"Failed to upload profile pic",Toast.LENGTH_SHORT).show();
                       }
                   });
               }
            }
        }
    }

    private void patch(String username,String name,String bio,String profile) {
        selecteddob.setYear(selecteddob.getYear() - 1900);
        Map<String,Object> update = new HashMap<>();
        update.put("name",name);
        update.put("profileUrl",profile);
        update.put("bio",bio);
        update.put("dateOfBirth",selecteddob);
        update.put("username",username);
        MessageMaker.getFirebaseService().readFromFireStore("Users").document(MessageMaker.getMyNumber()).collection("AccountInfo").document("PersonalInfo").update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                SharedPreferences sharedPreferences = getSharedPreferences("ChatWithMe",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name",name);
                editor.putString("username",username);
                editor.putString("bio",bio);
                editor.putString("dob",MessageMaker.formatDate(selecteddob,"yyyy-MM-dd"));
                editor.putString("profile",profile);
                editor.apply();
                pop.dismiss();
                closeActivity(true);
            }
        });
    }

    private void closeActivity(Boolean isdone) {
        Intent intent=new Intent();
        intent.putExtra("done",isdone);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            File file = ImagePicker.Companion.getFile(data);
            profilePath = file.getPath();
            Glide.with(getApplicationContext()).load(profilePath).into(profile);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}