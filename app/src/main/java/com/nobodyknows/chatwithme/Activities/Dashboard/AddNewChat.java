package com.nobodyknows.chatwithme.Activities.Dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.NobodyKnows.chatlayoutview.Model.User;
import com.bumptech.glide.Glide;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nobodyknows.chatwithme.Activities.Dashboard.Adapters.ContactsRecyclerViewAdapter;
import com.nobodyknows.chatwithme.Activities.Dashboard.Interfaces.SelectListener;
import com.nobodyknows.chatwithme.Fragments.Adapters.RecyclerViewAdapter;
import com.nobodyknows.chatwithme.Models.Users;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.FirebaseService;

import java.util.ArrayList;
import java.util.List;

public class AddNewChat extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Users> contacts = new ArrayList<>();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_done:
               createChatRoom();
                break;
            default:
                break;
        }
        return true;
    }

    private void createChatRoom() {
    }

    private void init() {
        firebaseService = new FirebaseService();
        recyclerView = findViewById(R.id.contactList);
        recyclerViewAdapter = new ContactsRecyclerViewAdapter(getApplicationContext(), contacts, firebaseService, new SelectListener() {
            @Override
            public void onContactSelected(List<Users> selectedContacts) {
                Log.d("TAGCON", "onPermissionGranted: "+selectedContacts.size());
                if(selectedContacts.size() > 0 ){
                    if(selectedContacts.size() == 1) {
                        getSupportActionBar().setTitle("1 contact selected");
                    } else {
                        getSupportActionBar().setTitle(selectedContacts.size()+" contacts selected");
                    }
                } else {
                    getSupportActionBar().setTitle("Add New Chat");
                }
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

    private void checkAndAdd(Contact contact) {
        String number =  "";
        if(contact.getPhoneNumbers() != null && contact.getPhoneNumbers().size() > 0 ){
            number = contact.getPhoneNumbers().get(0).getNormalizedNumber();
            if(number != null && number.charAt(0) == '+') {
                number = number.replace("+91","");
            }
        }
        if(number != null && number.length() > 0) {
            firebaseService.readFromFireStore("Users").document(number).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        if(task.getResult().exists()) {
                            Users users = task.getResult().toObject(Users.class);
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