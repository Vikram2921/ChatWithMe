package com.nobodyknows.chatwithme.Activities.Signup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;
import com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard;
import com.nobodyknows.chatwithme.Activities.Login;
import com.nobodyknows.chatwithme.MainActivity;
import com.nobodyknows.chatwithme.Models.Users;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.FirebaseService;

import java.io.File;
import java.time.LocalDateTime;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateUser extends AppCompatActivity {

    ImageView back,info;
    EditText name,password;
    private FirebaseService firebaseService;
    CircleImageView profilepic;
    private String profilePath = "NO_PROFILE";
    private String number = "";
    private Button continueButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        getSupportActionBar().hide();
        number = getIntent().getStringExtra("number");
        firebaseService = new FirebaseService();
        init();
    }
    private void init() {
        Activity activity = this;
        back = findViewById(R.id.backtomain);
        info = findViewById(R.id.info);
        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        profilepic = findViewById(R.id.profilepic);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
        continueButton = findViewById(R.id.continuelogin);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = name.getText().toString();
                String passwordText = password.getText().toString();
                if(fullname != null && fullname.length() > 0) {
                    if(passwordText != null && passwordText.length() > 0) {
                        createUser(fullname,passwordText);
                    } else {
                        Toast.makeText(getApplicationContext(),"Invalid password provided.",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Invalid name provided.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(activity)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
    }

    private void createUser(String fullname,String password) {
        continueButton.setText("Creating account...");
        continueButton.setEnabled(false);
        Users users = new Users();
        users.setName(fullname);
        users.setContactNumber(number);
        users.setPassword(password);
        if(!profilePath.equalsIgnoreCase("NO_PROFILE")) {
            UploadTask uploadTask = firebaseService.uploadFromUri(number+"_profile","Profiles",profilePath);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return firebaseService.getStorageRef(number+"_profile","Profiles").getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                users.setProfileUrl(downloadUri.toString());
                                patch(users);
                            } else {
                                Toast.makeText(getApplicationContext(),"Failed to upload profile pic",Toast.LENGTH_SHORT).show();
                                continueButton.setEnabled(true);
                                continueButton.setText("Finish and login");
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Failed to upload profile pic",Toast.LENGTH_SHORT).show();
                    continueButton.setEnabled(true);
                    continueButton.setText("Finish and login");
                }
            });
        } else {
            users.setProfileUrl(profilePath);
            patch(users);
        }
    }

    private void patch(Users users) {
        firebaseService.saveToFireStore("Users").document(users.getContactNumber()).set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Account created !",Toast.LENGTH_SHORT).show();
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
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            File file = ImagePicker.Companion.getFile(data);
            profilePath = file.getPath();
            Glide.with(getApplicationContext()).load(uri).into(profilepic);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}