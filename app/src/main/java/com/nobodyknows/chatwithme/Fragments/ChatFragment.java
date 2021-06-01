package com.nobodyknows.chatwithme.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.NobodyKnows.chatlayoutview.Constants.MessageStatus;
import com.NobodyKnows.chatlayoutview.Constants.MessageType;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.nobodyknows.chatwithme.Activities.Dashboard.AddNewChat;
import com.nobodyknows.chatwithme.DTOS.SecurityDTO;
import com.nobodyknows.chatwithme.DTOS.UserListItemDTO;
import com.nobodyknows.chatwithme.DTOS.userListenerHolder;
import com.nobodyknows.chatwithme.Fragments.Adapters.RecyclerViewAdapter;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.MessageMaker;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    public static ArrayList<UserListItemDTO> userListItems = new ArrayList<>();
    public static RecyclerViewAdapter recyclerViewAdapter;
    private String myNumber = "";
    public static Map<String, userListenerHolder> userListItemDTOMap = new HashMap<>();
    public static ConstraintLayout notfound;
    private Button action;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        this.view = view;
        myNumber = MessageMaker.getMyNumber();
        init();
        return view;
    }


    private void init() {
        notfound = view.findViewById(R.id.notfound);
        action = view.findViewById(R.id.action);
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddNewChat.class);
                intent.putExtra("title","Add New Chat");
                startActivity(intent);
            }
        });
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
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void startListener() {
        loadPrevioudUsers();
        MessageMaker.getFirebaseService().readFromFireStore("Users").document(myNumber).collection("AccountInfo").document("RecentChats").collection("History").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error == null) {
                    for(DocumentChange doc:value.getDocumentChanges()) {
                        Message message = doc.getDocument().toObject(Message.class);
                        Message messageUpdate = MessageMaker.filterMessage(message);
                        if(messageUpdate != null) {
                            switch (doc.getType()) {
                                case ADDED:
                                    addNewItem(doc.getDocument().getId(),message);
                                    break;
                                case MODIFIED:
                                    //updateItem(doc.getDocument().getId(),message);
                                    break;
                                case REMOVED:
                                    break;
                            }
                        } else {
                            if(message.getMessageType() == MessageType.UNFREIND) {
                                String number="";
                                if(!message.getSender().equalsIgnoreCase(myNumber)) {
                                    number = message.getSender();
                                } else {
                                    number = message.getReceiver();
                                }
                                String finalNumber = number;
                                MessageMaker.getFirebaseService().readFromFireStore("Users").document(myNumber).collection("AccountInfo").document("RecentChats").collection("History").document(number).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        int index = userListItems.indexOf(userListItemDTOMap.get(finalNumber));
                                        userListItemDTOMap.remove(finalNumber);
                                        userListItems.remove(index);
                                        recyclerViewAdapter.notifyItemRemoved(index);
                                    }
                                });

                            }
                        }

                    }
                }
            }
        });
    }

    private void loadPrevioudUsers() {
        ArrayList<UserListItemDTO> list = MessageMaker.getDatabaseHelper().getRecentChatUsers(getContext());
        for(UserListItemDTO userListItemDTO:list) {
            addNewChat(userListItemDTO);
        }
    }

    private void updateItem(String username,Message message) {
        int index = userListItems.indexOf(userListItemDTOMap.get(username));
        UserListItemDTO userListItemDTO = userListItems.get(index);
        if(!userListItemDTO.isBlocked()) {
            userListItemDTO.setLastMessage(message);
            userListItems.remove(index);
            userListItems.add(0,userListItemDTO);
            recyclerViewAdapter.notifyItemMoved(index,0);
            if(message.getMessageStatus() == MessageStatus.SENT && !message.getSender().equalsIgnoreCase(myNumber)) {
                message.setMessageStatus(MessageStatus.RECEIVED);
                message.setReceivedAt(new Date());
//                MessageMaker.getFirebaseService().saveToFireStore("Chats").document(message.getRoomId()).collection("Messages").document(message.getMessageId()).set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                    }
//                });
            }
        }
    }

    private void addNewItem(String username, Message lastMessage) {
        UserListItemDTO userListItemDTO = new UserListItemDTO();
        userListItemDTO.setContactNumber(username);
        userListItemDTO.setLastMessage(lastMessage);
        User user = MessageMaker.getDatabaseHelper().getUser(username);
        if(user == null) {
            MessageMaker.getFirebaseService().readFromFireStore("Users").document(username).collection("AccountInfo").document("PersonalInfo").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        if(task.getResult().exists()) {
                            User user = task.getResult().toObject(User.class);
                            String roomid = MessageMaker.createRoomId(user.getContactNumber());
                            MessageMaker.getFirebaseService().saveToFireStore("Chats").document(roomid).collection("Infos").document("SecurityInfo").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot != null) {
                                        SecurityDTO securityDTO = documentSnapshot.toObject(SecurityDTO.class);
                                        MessageMaker.getDatabaseHelper().insertInSecurity(roomid,securityDTO);
                                        userListItemDTO.setCurrentStatus(user.getCurrentStatus());
                                        userListItemDTO.setName(user.getName());
                                        userListItemDTO.setProfileUrl(user.getProfileUrl());
                                        userListItemDTO.setStatus(user.getStatus());
                                        userListItemDTO.setVerified(user.getVerified());
                                        userListItemDTO.setLastOnline(user.getLastOnline());
                                        userListItemDTO.setBlocked(user.getBlocked());
                                        MessageMaker.getDatabaseHelper().insertInUser(user);
                                        MessageMaker.getDatabaseHelperChat().insertInMessage(lastMessage,lastMessage.getRoomId());
                                        addNewChat(userListItemDTO);
                                    }
                                }
                            });
                        }
                    }
                }
            });
        } else {
            userListItemDTO.setCurrentStatus(user.getCurrentStatus());
            userListItemDTO.setName(user.getName());
            userListItemDTO.setProfileUrl(user.getProfileUrl());
            userListItemDTO.setStatus(user.getStatus());
            userListItemDTO.setVerified(user.getVerified());
            userListItemDTO.setLastOnline(user.getLastOnline());
            userListItemDTO.setBlocked(user.getBlocked());
            userListItemDTO.setMuted(user.getMuted());
            MessageMaker.getDatabaseHelperChat().insertInMessage(lastMessage,lastMessage.getRoomId());
            addNewChat(userListItemDTO);
        }
    }

    private void addNewChat(UserListItemDTO userListItem) {
        MessageMaker.hideNotFound(notfound);
        if(!userListItemDTOMap.containsKey(userListItem.getContactNumber())) {
            userListItems.add(0,userListItem);
            recyclerViewAdapter.notifyItemInserted(0);
            userListenerHolder userListenerHolder = new userListenerHolder();
            userListenerHolder.setUserListItemDTO(userListItem);
            userListItemDTOMap.put(userListItem.getContactNumber(),userListenerHolder);
            if(userListItem.getLastMessage() != null) {
                MessageMaker.getDatabaseHelper().insertInRecentChats(userListItem.getContactNumber(),userListItem.getLastMessage().getMessageId(),userListItem.getLastMessage().getSentAt());
            }
            attachListener(userListItem.getContactNumber());
        }
    }

    private void attachListener(String username) {
//        ListenerRegistration listenerRegistration = MessageMaker.getFirebaseService().readFromFireStore("Users").document(username).collection("AccountInfo").document("PersonalInfo").addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                User user = value.toObject(User.class);
//                UserListItemDTO userListItemDTO = userListItemDTOMap.get(username).getUserListItemDTO();
//                int index = userListItems.indexOf(userListItemDTO);
//                userListItemDTO.setName(user.getName());
//                userListItemDTO.setVerified(user.getVerified());
//                userListItemDTO.setLastOnline(user.getLastOnline());
//                userListItemDTO.setStatus(user.getStatus());
//                userListItemDTO.setProfileUrl(user.getProfileUrl());
//                userListItemDTO.setMuted(user.getMuted());
//                userListItemDTO.setBlocked(user.getBlocked());
//                userListItemDTO.setCurrentStatus(user.getCurrentStatus());
//                userListItems.remove(index);
//                userListItems.add(index,userListItemDTO);
//                recyclerViewAdapter.notifyItemChanged(index);
//                MessageMaker.getDatabaseHelper().updateUserInfo(user);
//            }
//        });
//        userListItemDTOMap.get(username).setListenerRegistration(listenerRegistration);
    }
}
