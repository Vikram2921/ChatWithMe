package com.NobodyKnows.chatlayoutview.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.NobodyKnows.chatlayoutview.Adapters.ContactsRecyclerViewAdapter;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.NobodyKnows.chatlayoutview.R;

import java.util.List;

import static com.NobodyKnows.chatlayoutview.ChatLayoutView.chatLayoutListener;
import static com.NobodyKnows.chatlayoutview.ChatLayoutView.databaseHelper;

public class ViewAllContacts extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ContactsRecyclerViewAdapter recyclerViewAdapter;
    private Message message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_contacts);
        getSupportActionBar().setTitle("View All Contacts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String messageId = getIntent().getStringExtra("messageId");
        String roomId = getIntent().getStringExtra("roomid");
        message = databaseHelper.getMessage(messageId,roomId);
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
        recyclerView = findViewById(R.id.contactlist);
        recyclerViewAdapter = new ContactsRecyclerViewAdapter(getApplicationContext(), message.getContacts(),chatLayoutListener,ViewAllContacts.this);
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
}