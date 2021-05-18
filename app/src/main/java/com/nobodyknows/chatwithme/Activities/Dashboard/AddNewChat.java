package com.nobodyknows.chatwithme.Activities.Dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.NobodyKnows.chatlayoutview.Model.User;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nobodyknows.chatwithme.Activities.ChatRoom;
import com.nobodyknows.chatwithme.Activities.Dashboard.Adapters.ContactsRecyclerViewAdapter;
import com.nobodyknows.chatwithme.Activities.Dashboard.Interfaces.SelectListener;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.FirebaseService;
import com.nobodyknows.chatwithme.services.MessageMaker;

import java.util.ArrayList;
import java.util.List;

import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.databaseHelper;

public class AddNewChat extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<User> contacts = new ArrayList<>();
    private ArrayList<String> contactsAdded = new ArrayList<>();
    private ContactsRecyclerViewAdapter recyclerViewAdapter;
    private FirebaseService firebaseService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_chat);
        getSupportActionBar().setTitle("Add New Chat");
        init();
    }


    private void init() {
        firebaseService = new FirebaseService();
        recyclerView = findViewById(R.id.contactList);
        recyclerViewAdapter = new ContactsRecyclerViewAdapter(getApplicationContext(), contacts, new SelectListener() {
            @Override
            public void onContactSelected(List<User> selectedContacts) {

            }

            @Override
            public void onStartChat(User user) {
                Intent intent = new Intent(getApplicationContext(), ChatRoom.class);
                intent.putExtra("username",user.getContactNumber());
                intent.putExtra("name",user.getName());
                intent.putExtra("lastOnlineStatus",user.getCurrentStatus());
                intent.putExtra("verified",user.getVerified());
                intent.putExtra("roomid", MessageMaker.createRoomId(getApplicationContext(),user.getContactNumber()));
                intent.putExtra("profile",user.getProfileUrl());
                startActivity(intent);
                finish();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(false);
        layoutManager.setItemPrefetchEnabled(true);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setInitialPrefetchItemCount(5);
        layoutManager.setRecycleChildrenOnDetach(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerViewAdapter);
        loadUsers();
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                List<Contact> contacts = Contacts.getQuery().find();
                for(Contact contact:contacts) {
                    checkAndAdd(contact);
                }
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                finish();
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS)
                .check();
    }

    private void loadUsers() {
        contacts.clear();
        ArrayList<User> contactsTemp  = databaseHelper.getAllUsers();
        for(User user:contactsTemp) {
            if(!contactsAdded.contains(user.getContactNumber())) {
                contacts.add(user);
                contactsAdded.add(user.getContactNumber());
                recyclerViewAdapter.notifyItemInserted(contacts.size() -1);
            }
        }
    }

    private void checkAndAdd(Contact contact) {
        String number =  "";
        if(contact.getPhoneNumbers() != null && contact.getPhoneNumbers().size() > 0 ){
            number = contact.getPhoneNumbers().get(0).getNormalizedNumber();
            if(number != null && number.charAt(0) == '+') {
                number = number.replace("+91","");
            }
        }
        if(number != null && number.length() > 0) {
            firebaseService.readFromFireStore("Users").document(number).collection("AccountInfo").document("PersonalInfo").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        if(task.getResult().exists()) {
                            User users = task.getResult().toObject(User.class);
                            if(!contactsAdded.contains(users.getContactNumber())) {
                                contacts.add(users);
                                contactsAdded.add(users.getContactNumber());
                                recyclerViewAdapter.notifyItemInserted(contacts.size() -1);
                            }
                        }
                    }
                }
            });
        }
    }
}