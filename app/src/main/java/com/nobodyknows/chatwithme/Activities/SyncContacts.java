package com.nobodyknows.chatwithme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.NobodyKnows.chatlayoutview.Model.User;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.PhoneNumber;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.nobodyknows.chatwithme.DTOS.FreindRequestDTO;
import com.nobodyknows.chatwithme.DTOS.FreindRequestSaveDTO;
import com.nobodyknows.chatwithme.Fragments.Adapters.FreindsRequestCreateRecyclerViewAdapter;
import com.nobodyknows.chatwithme.Fragments.Adapters.FreindsRequestRecyclerViewAdapter;
import com.nobodyknows.chatwithme.Fragments.Interfaces.FreindsOptionListener;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.FirebaseService;
import com.nobodyknows.chatwithme.services.MessageMaker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.databaseHelper;
import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.firebaseService;
public class SyncContacts extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ConstraintLayout constraintLayout;
    private ArrayList<String> contactsToSearch = new ArrayList<>();
    private ArrayList<String> contactSearched = new ArrayList<>();
    private ArrayList<User> foundList = new ArrayList<>();
    private String mynumber= "";
    private ConstraintLayout notfound;
    private Button action;
    private FreindsRequestCreateRecyclerViewAdapter recyclerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_contacts);
        getSupportActionBar().setTitle("Freinds From Contacts");
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mynumber = MessageMaker.getMyNumber();
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
        notfound = findViewById(R.id.notfound);
        action = findViewById(R.id.action);
        constraintLayout = findViewById(R.id.loading);
        recyclerView = findViewById(R.id.recyclerview);
        showLoading();
        setupRecyclerView();
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageMaker.hideNotFound(notfound);
                showLoading();
                startSync();
            }
        });
    }

    private void showLoading() {
        getSupportActionBar().hide();
        constraintLayout.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        getSupportActionBar().show();
        constraintLayout.setVisibility(View.GONE);
    }

    private void setupRecyclerView() {
        recyclerViewAdapter = new FreindsRequestCreateRecyclerViewAdapter(getApplicationContext(), foundList,new FreindsOptionListener() {
            @Override
            public void onConfirm(FreindRequestDTO freindRequestDTO) {

            }

            @Override
            public void onSendFreindRequest(User user) {
                sendRequest(user);
            }

            @Override
            public void onDelete(FreindRequestDTO freindRequestDTO) {

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
        startSync();
    }

    private void sendRequest(User user) {
        FreindRequestSaveDTO freindRequestSaveDTO = new FreindRequestSaveDTO();
        freindRequestSaveDTO.setRequestSentBy(mynumber);
        freindRequestSaveDTO.setRequestSentAt(new Date());
        freindRequestSaveDTO.setContactNumber(user.getContactNumber());
        firebaseService.saveToFireStore("Users").document(mynumber).collection("FreindRequests").document("Sent").collection("List").document(user.getContactNumber()).set(freindRequestSaveDTO).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                freindRequestSaveDTO.setContactNumber(mynumber);
                firebaseService.saveToFireStore("Users").document(user.getContactNumber()).collection("FreindRequests").document("Receive").collection("List").document(mynumber).set(freindRequestSaveDTO).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"Freind Request Sent to "+user.getName(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void startSync() {
        List<Contact> contacts = Contacts.getQuery().find();
        String number= "";
        contactSearched.clear();
        contactsToSearch.clear();
        for(Contact contact:contacts) {
            if(contact.getPhoneNumbers() != null && contact.getPhoneNumbers().size() > 0) {
                for(PhoneNumber phoneNumber:contact.getPhoneNumbers()) {
                    number = phoneNumber.getNormalizedNumber();
                    if(number != null && number.charAt(0) == '+') {
                        number = number.replace("+91","");
                    }
                    if(number != null && number.length() > 0 && !databaseHelper.isUserExist(number) && !mynumber.equals(number) && !contactsToSearch.contains(number)) {
                        contactsToSearch.add(number);
                    }
                }
            }
        }
        for(String num:contactsToSearch) {
            checkAndAdd(num);
        }
    }

    private void checkAndAdd(String number) {
        firebaseService.getDatabaseRef("Users").child(number).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    firebaseService.readFromFireStore("Users").document(number).collection("AccountInfo").document("PersonalInfo").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()) {
                                User users = task.getResult().toObject(User.class);
                                foundList.add(users);
                                recyclerViewAdapter.notifyItemInserted(foundList.size() -1);
                                addinSearched(number);
                            }
                        }
                    });
                } else {
                    addinSearched(number);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addinSearched(String number) {
        contactSearched.add(number);
        if(contactSearched.size() == contactsToSearch.size()) {
            hideLoading();
            if(foundList.size() == 0) {
                MessageMaker.showNotFound(notfound);
            }
        }
    }
}