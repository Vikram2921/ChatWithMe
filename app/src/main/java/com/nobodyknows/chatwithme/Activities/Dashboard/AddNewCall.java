package com.nobodyknows.chatwithme.Activities.Dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.NobodyKnows.chatlayoutview.Model.User;
import com.google.firebase.firestore.ListenerRegistration;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nobodyknows.chatwithme.Activities.AudioCall;
import com.nobodyknows.chatwithme.Activities.Dashboard.Adapters.AddCallRecyclerViewAdapter;
import com.nobodyknows.chatwithme.Activities.Dashboard.Adapters.ContactsRecyclerViewAdapter;
import com.nobodyknows.chatwithme.Activities.Dashboard.Interfaces.AddCallListener;
import com.nobodyknows.chatwithme.Activities.Dashboard.Interfaces.SelectListener;
import com.nobodyknows.chatwithme.Activities.SearchFreinds;
import com.nobodyknows.chatwithme.Activities.SyncContacts;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.MessageMaker;

import java.util.ArrayList;
import java.util.List;

import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.databaseHelper;

public class AddNewCall extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<User> contacts = new ArrayList<>();
    private ArrayList<String> contactsAdded = new ArrayList<>();
    private AddCallRecyclerViewAdapter recyclerViewAdapter;
    private ConstraintLayout notfound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_call);
        getSupportActionBar().setTitle("Add new call");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }

    private void init() {
        recyclerView = findViewById(R.id.contactList);
        notfound = findViewById(R.id.notfound);
        recyclerViewAdapter = new AddCallRecyclerViewAdapter(AddNewCall.this,getApplicationContext(), contacts, new AddCallListener() {
            @Override
            public void onVideoCall(User user) {
                Intent intent = new Intent(getApplicationContext(), AudioCall.class);
                intent.putExtra("username",user.getContactNumber());
                intent.putExtra("making",true);
                intent.putExtra("video",true);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAudioCall(User user) {
                Intent intent = new Intent(getApplicationContext(), AudioCall.class);
                intent.putExtra("username",user.getContactNumber());
                intent.putExtra("making",true);
                intent.putExtra("video",false);
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
}