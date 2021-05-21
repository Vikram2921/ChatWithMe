package com.nobodyknows.chatwithme.Activities.Dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.NobodyKnows.chatlayoutview.Model.Message;
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
import com.nobodyknows.chatwithme.Activities.SearchFreinds;
import com.nobodyknows.chatwithme.Activities.SyncContacts;
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
    private Button search,sync;
    private ConstraintLayout notfound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_chat);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        recyclerView = findViewById(R.id.contactList);
        notfound = findViewById(R.id.notfound);
        sync = findViewById(R.id.synccontact);
        search = findViewById(R.id.searchFreind);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchFreinds.class);
                startActivity(intent);
            }
        });

        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent(getApplicationContext(), SyncContacts.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        finish();
                    }
                };
                TedPermission.with(getApplicationContext())
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS)
                        .check();
            }
        });
        recyclerViewAdapter = new ContactsRecyclerViewAdapter(getApplicationContext(), contacts, new SelectListener() {
            @Override
            public void onContactSelected(List<User> selectedContacts) {

            }

            @Override
            public void onStartChat(User user) {
                MessageMaker.startChatroom(getApplicationContext(),user.getContactNumber());
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

    }

    private void loadUsers() {
        contacts.clear();
        ArrayList<User> contactsTemp = databaseHelper.getAllUsers();
        if(contactsTemp.size() > 0) {
            MessageMaker.hideNotFound(notfound);
            for (User user : contactsTemp) {
                if (!contactsAdded.contains(user.getContactNumber())) {
                    contacts.add(user);
                    contactsAdded.add(user.getContactNumber());
                    recyclerViewAdapter.notifyItemInserted(contacts.size() - 1);
                }
            }
        }
    }
}