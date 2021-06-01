package com.nobodyknows.chatwithme.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.NobodyKnows.chatlayoutview.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.nobodyknows.chatwithme.Activities.Signup.CreateUser;
import com.nobodyknows.chatwithme.DTOS.FreindRequestDTO;
import com.nobodyknows.chatwithme.DTOS.FreindRequestSaveDTO;
import com.nobodyknows.chatwithme.Fragments.Adapters.FreindsRequestCreateRecyclerViewAdapter;
import com.nobodyknows.chatwithme.Fragments.Interfaces.FreindsOptionListener;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.MessageMaker;

import java.util.ArrayList;
import java.util.Date;

public class SearchFreinds extends AppCompatActivity {

    private EditText searchbox;
    private Button searchnow;
    private RecyclerView recyclerView;
    private ArrayList<User> users = new ArrayList<>();
    private String mynumber= "";
    private ArrayList<String> added = new ArrayList<>();
    private ConstraintLayout notfound;
    private ImageView icon;
    private ProgressBar progressBar;
    private TextView text;
    private FreindsRequestCreateRecyclerViewAdapter recyclerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_freinds);
        getSupportActionBar().setTitle("Search new freinds");
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
        icon = findViewById(R.id.resulticon);
        text = findViewById(R.id.resulttext);
        progressBar = findViewById(R.id.progress);

        searchbox = findViewById(R.id.searchbox);
        searchnow = findViewById(R.id.search);
        recyclerView = findViewById(R.id.searchlist);
        setupRecyclerView();
        searchnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = searchbox.getText().toString();
                if(name != null && name.length() > 0) {
                    searchFreinds(name);
                }
            }
        });
    }

    private void setupRecyclerView() {
        recyclerViewAdapter = new FreindsRequestCreateRecyclerViewAdapter(getApplicationContext(), users,new FreindsOptionListener() {
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
    }

    private void sendRequest(User user) {
        FreindRequestSaveDTO freindRequestSaveDTO = new FreindRequestSaveDTO();
        freindRequestSaveDTO.setRequestSentBy(mynumber);
        freindRequestSaveDTO.setRequestSentAt(new Date());
        freindRequestSaveDTO.setContactNumber(user.getContactNumber());
        MessageMaker.getFirebaseService().saveToFireStore("Users").document(mynumber).collection("FreindRequests").document("Sent").collection("List").document(user.getContactNumber()).set(freindRequestSaveDTO).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                freindRequestSaveDTO.setContactNumber(mynumber);
                MessageMaker.getFirebaseService().saveToFireStore("Users").document(user.getContactNumber()).collection("FreindRequests").document("Receive").collection("List").document(mynumber).set(freindRequestSaveDTO).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"Freind Request Sent to "+user.getName(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void searchFreinds(String name) {
        added.clear();
        users.clear();
        MessageMaker.showNotFound(notfound);
        progressBar.setVisibility(View.VISIBLE);
        icon.setVisibility(View.GONE);
        text.setText("Searching for '"+name+"'");
        recyclerViewAdapter.notifyDataSetChanged();
        MessageMaker.getFirebaseService().getDatabaseRef("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    if(snapshot.getValue().toString().toLowerCase().contains(name.toLowerCase())) {
                        String key = snapshot.getKey();
                        if(!key.equals(mynumber) && !MessageMaker.getDatabaseHelper().isUserExist(key) && !added.contains(key)) {
                            added.add(key);
                            MessageMaker.getFirebaseService().readFromFireStore("Users").document(key).collection("AccountInfo").document("PersonalInfo").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        if(task.getResult().exists()) {
                                            User user = task.getResult().toObject(User.class);
                                            users.add(user);
                                            recyclerViewAdapter.notifyItemInserted(users.size() -1);
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
                if(added.size() > 0) {
                    MessageMaker.hideNotFound(notfound);
                } else {
                    progressBar.setVisibility(View.GONE);
                    icon.setVisibility(View.VISIBLE);
                    text.setText("Sorry we didn't find any results matching this search.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}