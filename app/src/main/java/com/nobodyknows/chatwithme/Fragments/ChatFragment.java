package com.nobodyknows.chatwithme.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.NobodyKnows.chatlayoutview.Constants.MessageStatus;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.nobodyknows.chatwithme.DTOS.UserListItemDTO;
import com.nobodyknows.chatwithme.Fragments.Adapters.RecyclerViewAdapter;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.FirebaseService;
import com.nobodyknows.chatwithme.services.MessageMaker;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.databaseHelper;
import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.firebaseService;
public class ChatFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private ArrayList<UserListItemDTO> userListItems = new ArrayList<>();
    private RecyclerViewAdapter recyclerViewAdapter;
    private String myNumber = "";
    private Map<String,UserListItemDTO> userListItemDTOMap = new HashMap<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        this.view = view;
        myNumber = MessageMaker.getFromSharedPrefrences(getContext(),"number");
        init();
        return view;
    }

    private void init() {
        setupRecyclerView();
        startListener();
    }

    private void setupRecyclerView() {
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(),userListItems,getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(false);
        layoutManager.setItemPrefetchEnabled(true);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setInitialPrefetchItemCount(5);
        layoutManager.setRecycleChildrenOnDetach(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void startListener() {
        loadPrevioudUsers();
        firebaseService.readFromFireStore("Users").document(myNumber).collection("AccountInfo").document("RecentChats").collection("History").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error == null) {
                    for(DocumentChange doc:value.getDocumentChanges()) {
                        Message message = doc.getDocument().toObject(Message.class);
                        switch (doc.getType()) {
                            case ADDED:
                                addNewItem(doc.getDocument().getId(),message);
                                break;
                            case MODIFIED:
                                updateItem(doc.getDocument().getId(),message);
                                break;
                            case REMOVED:
                                break;
                        }
                    }
                }
            }
        });
    }

    private void loadPrevioudUsers() {
        ArrayList<UserListItemDTO> list = databaseHelper.getRecentChatUsers();
        for(UserListItemDTO userListItemDTO:list) {
            addNewChat(userListItemDTO);
        }
    }

    private void updateItem(String username,Message message) {
        int index = userListItems.indexOf(userListItemDTOMap.get(username));
        UserListItemDTO userListItemDTO = userListItems.get(index);
        userListItemDTO.setLastMessage(message);
        userListItems.remove(index);
        userListItems.add(0,userListItemDTO);
        recyclerViewAdapter.notifyItemMoved(index,0);
        if(message.getMessageStatus() == MessageStatus.SENT && !message.getSender().equalsIgnoreCase(myNumber)) {
            message.setMessageStatus(MessageStatus.RECEIVED);
            message.setReceivedAt(new Date());
            firebaseService.saveToFireStore("Chats").document(message.getRoomId()).collection("Messages").document(message.getMessageId()).set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            });
        }
    }

    private void addNewItem(String username, Message lastMessage) {
        UserListItemDTO userListItemDTO = new UserListItemDTO();
        userListItemDTO.setContactNumber(username);
        userListItemDTO.setLastMessage(lastMessage);
        firebaseService.readFromFireStore("Users").document(username).collection("AccountInfo").document("PersonalInfo").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    if(task.getResult().exists()) {
                        User user = task.getResult().toObject(User.class);
                        userListItemDTO.setCurrentStatus(user.getCurrentStatus());
                        userListItemDTO.setName(user.getName());
                        userListItemDTO.setProfileUrl(user.getProfileUrl());
                        userListItemDTO.setStatus(user.getStatus());
                        userListItemDTO.setVerified(user.getVerified());
                        userListItemDTO.setLastOnline(user.getLastOnline());
                        databaseHelper.insertInUser(user);
                        addNewChat(userListItemDTO);
                    }
                }
            }
        });
    }

    private void addNewChat(UserListItemDTO userListItem) {
        if(!userListItemDTOMap.containsKey(userListItem.getContactNumber())) {
            userListItems.add(0,userListItem);
            recyclerViewAdapter.notifyItemInserted(0);
            userListItemDTOMap.put(userListItem.getContactNumber(),userListItem);
            if(userListItem.getLastMessage() != null) {
                databaseHelper.insertInRecentChats(userListItem.getContactNumber(),userListItem.getLastMessage().getMessageId(),userListItem.getLastMessage().getSentAt());
            }
        }
    }
}
