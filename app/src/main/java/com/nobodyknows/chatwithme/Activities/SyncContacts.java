package com.nobodyknows.chatwithme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.NobodyKnows.chatlayoutview.Model.User;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.nobodyknows.chatwithme.DTOS.FreindRequestDTO;
import com.nobodyknows.chatwithme.Fragments.Adapters.FreindsRequestCreateRecyclerViewAdapter;
import com.nobodyknows.chatwithme.Fragments.Adapters.FreindsRequestRecyclerViewAdapter;
import com.nobodyknows.chatwithme.Fragments.Interfaces.FreindsOptionListener;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.FirebaseService;
import com.nobodyknows.chatwithme.services.MessageMaker;

import java.util.ArrayList;
import java.util.List;

import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.databaseHelper;
import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.firebaseService;
public class SyncContacts extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ConstraintLayout constraintLayout;
    private ArrayList<String> added = new ArrayList<>();
    private ArrayList<User> foundList = new ArrayList<>();
    private String mynumber= "";
    private FreindsRequestCreateRecyclerViewAdapter recyclerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_contacts);
        getSupportActionBar().setTitle("Freinds From Contacts");
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mynumber = MessageMaker.getFromSharedPrefrences(getApplicationContext(),"number");
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
        constraintLayout = findViewById(R.id.loading);
        recyclerView = findViewById(R.id.recyclerview);
        showLoading();
        setupRecyclerView();
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
        List<Contact> contacts = Contacts.getQuery().find();
        for(Contact contact:contacts) {
            checkAndAdd(contact);
        }
    }

    private void checkAndAdd(Contact contact) {
        String number =  "";
        foundList.clear();
        if(contact.getPhoneNumbers() != null && contact.getPhoneNumbers().size() > 0 ){
            number = contact.getPhoneNumbers().get(0).getNormalizedNumber();
            if(number != null && number.charAt(0) == '+') {
                number = number.replace("+91","");
            }
        }
        if(number != null && number.length() > 0 && !databaseHelper.isUserExist(number) && !added.contains(number) && !mynumber.equals(number)) {
            added.add(number);
            firebaseService.readFromFireStore("Users").document(number).collection("AccountInfo").document("PersonalInfo").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        if(task.getResult().exists()) {
                            User users = task.getResult().toObject(User.class);
                            foundList.add(users);
                            if(constraintLayout.getVisibility() == View.VISIBLE) {
                               hideLoading();
                            }
                            recyclerViewAdapter.notifyItemInserted(foundList.size() -1);
                        }
                    }
                }
            });
        }
    }
}