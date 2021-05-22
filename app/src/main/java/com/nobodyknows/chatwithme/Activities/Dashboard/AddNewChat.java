package com.nobodyknows.chatwithme.Activities.Dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nobodyknows.chatwithme.Activities.ChatRoom;
import com.nobodyknows.chatwithme.Activities.Dashboard.Adapters.ContactsRecyclerViewAdapter;
import com.nobodyknows.chatwithme.Activities.Dashboard.Interfaces.SelectListener;
import com.nobodyknows.chatwithme.Activities.SearchFreinds;
import com.nobodyknows.chatwithme.Activities.SyncContacts;
import com.nobodyknows.chatwithme.DTOS.FreindRequestSaveDTO;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.FirebaseService;
import com.nobodyknows.chatwithme.services.MessageMaker;

import java.util.ArrayList;
import java.util.List;

import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.databaseHelper;
import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.firebaseService;

public class AddNewChat extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<User> contacts = new ArrayList<>();
    private ArrayList<String> contactsAdded = new ArrayList<>();
    private ContactsRecyclerViewAdapter recyclerViewAdapter;
    private Button search,sync;
    private ListenerRegistration listener;
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
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(listener != null) {
            listener.remove();
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

    private void syncOnline() {
        listener = firebaseService.readFromFireStore("Users").document(MessageMaker.getMyNumber()).collection("Freinds").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error == null) {
                    for(DocumentChange documentChange:value.getDocumentChanges()) {
                        FreindRequestSaveDTO freindRequestSaveDTO = documentChange.getDocument().toObject(FreindRequestSaveDTO.class);
                        switch (documentChange.getType()) {
                            case ADDED:
                               addFreind(freindRequestSaveDTO);
                                break;
                            case MODIFIED:
                                break;
                            case REMOVED:
                                removeFreind(freindRequestSaveDTO);
                                break;
                        }
                    }
                }
            }
        });
    }

    private void removeFreind(FreindRequestSaveDTO freindRequestSaveDTO) {
        int index = contactsAdded.indexOf(freindRequestSaveDTO.getContactNumber());
        contacts.remove(index);
        contactsAdded.remove(index);
        recyclerViewAdapter.notifyItemRemoved(index);
    }

    private void addFreind(FreindRequestSaveDTO freindRequestSaveDTO) {
        firebaseService.readFromFireStore("Users").document(freindRequestSaveDTO.getContactNumber()).collection("AccountInfo").document("PersonalInfo").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot != null) {
                    User user = documentSnapshot.toObject(User.class);
                    databaseHelper.insertInUser(user);
                    if (!contactsAdded.contains(user.getContactNumber())) {
                        MessageMaker.hideNotFound(notfound);
                        contacts.add(user);
                        contactsAdded.add(user.getContactNumber());
                        recyclerViewAdapter.notifyItemInserted(contacts.size() - 1);
                    }
                }
            }
        });
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
        syncOnline();

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