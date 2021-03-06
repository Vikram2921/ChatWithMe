package com.nobodyknows.chatwithme.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.QuerySnapshot;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nobodyknows.chatwithme.Activities.ChatRoom;
import com.nobodyknows.chatwithme.Activities.Dashboard.AddNewChat;
import com.nobodyknows.chatwithme.Activities.SearchFreinds;
import com.nobodyknows.chatwithme.Activities.Signup.CreatingSetup;
import com.nobodyknows.chatwithme.DTOS.FreindRequestDTO;
import com.nobodyknows.chatwithme.DTOS.FreindRequestSaveDTO;
import com.nobodyknows.chatwithme.DTOS.SecurityDTO;
import com.nobodyknows.chatwithme.Fragments.Adapters.FreindsRequestRecyclerViewAdapter;
import com.nobodyknows.chatwithme.Fragments.Adapters.RecyclerViewAdapter;
import com.nobodyknows.chatwithme.Fragments.Interfaces.FreindsOptionListener;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.FirebaseService;
import com.nobodyknows.chatwithme.services.MessageMaker;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.Date;


public class FreindsFragment extends Fragment {

    private View view;
    private Button search,see;
    private TextView count;
    private RecyclerView recyclerView;
    private FreindsRequestRecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<String> added = new ArrayList<>();
    private ConstraintLayout notfound;
    private ArrayList<FreindRequestDTO> freindRequestDTOS = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_freinds, container, false);
        this.view = view;
        init();
        return view;
    }

    private void init() {
        notfound = view.findViewById(R.id.notfound);
        count = view.findViewById(R.id.requestcount);
        search = view.findViewById(R.id.searchnewfreind);
        see = view.findViewById(R.id.seeallfreinds);
        recyclerView = view.findViewById(R.id.requestlist);
        see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddNewChat.class);
                intent.putExtra("title","All Freinds");
                startActivity(intent);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchFreinds.class);
                startActivity(intent);
            }
        });
        setupRecyclerView();
        syncFreindList();
    }

    private void setupRecyclerView() {
        recyclerViewAdapter = new FreindsRequestRecyclerViewAdapter(getContext(), freindRequestDTOS, getActivity(), new FreindsOptionListener() {
            @Override
            public void onConfirm(FreindRequestDTO freindRequestDTO) {
                confirmFreind(freindRequestDTO);
            }

            @Override
            public void onSendFreindRequest(User user) {

            }

            @Override
            public void onDelete(FreindRequestDTO freindRequestDTO) {
                deleteRequest(freindRequestDTO,false);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(false);
        layoutManager.setItemPrefetchEnabled(true);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setInitialPrefetchItemCount(5);
        layoutManager.setRecycleChildrenOnDetach(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerViewAdapter);
        loadPrevious();
    }

    private void confirmFreind(FreindRequestDTO freindRequestDTO) {
        KProgressHUD popup = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        String mynumber= MessageMaker.getMyNumber();
        FreindRequestSaveDTO fdto = new FreindRequestSaveDTO();
        fdto.setContactNumber(freindRequestDTO.getContactNumber());
        fdto.setRequestSentAt(freindRequestDTO.getRequestSentAt());
        fdto.setRequestSentBy(freindRequestDTO.getContactNumber());
        fdto.setRequestAcceptedAt(new Date());
        MessageMaker.getFirebaseService().saveToFireStore("Users").document(mynumber).collection("Freinds").document(freindRequestDTO.getContactNumber()).set(fdto).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    FreindRequestSaveDTO myDto = new FreindRequestSaveDTO();
                    myDto.setContactNumber(mynumber);
                    myDto.setRequestSentAt(freindRequestDTO.getRequestSentAt());
                    myDto.setRequestSentBy(freindRequestDTO.getContactNumber());
                    myDto.setRequestAcceptedAt(new Date());
                    MessageMaker.getFirebaseService().saveToFireStore("Users").document(freindRequestDTO.getContactNumber()).collection("Freinds").document(mynumber).set(myDto).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String roomid= MessageMaker.createRoomId(freindRequestDTO.getContactNumber());
                            String key = MessageMaker.generateSecurityKey(32);
                            SecurityDTO securityDTO = new SecurityDTO();
                            securityDTO.setLastChangedBy(mynumber);
                            securityDTO.setSecurityKey(key);
                            MessageMaker.getFirebaseService().saveToFireStore("Chats").document(roomid).collection("Infos").document("SecurityInfo").set(securityDTO).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if(task.isSuccessful()) {
                                        MessageMaker.getDatabaseHelper().insertInSecurity(roomid,securityDTO);
                                        deleteRequest(freindRequestDTO,true);
                                        popup.dismiss();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void deleteRequest(FreindRequestDTO freindRequestDTO,Boolean updateInDatabase) {
        MessageMaker.getFirebaseService().readFromFireStore("Users").document(MessageMaker.getMyNumber()).collection("FreindRequests").document("Receive").collection("List").document(freindRequestDTO.getContactNumber()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                MessageMaker.getFirebaseService().readFromFireStore("Users").document(freindRequestDTO.getContactNumber()).collection("FreindRequests").document("Sent").collection("List").document(MessageMaker.getMyNumber()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(updateInDatabase) {
                            updateInDatabase(freindRequestDTO.getContactNumber());
                        }
                    }
                });
            }
        });
    }

    private void updateInDatabase(String username) {
        if(!MessageMaker.getDatabaseHelper().isUserExist(username)) {
            MessageMaker.getFirebaseService().readFromFireStore("Users").document(username).collection("AccountInfo").document("PersonalInfo").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot != null) {
                        User users = documentSnapshot.toObject(User.class);
                        MessageMaker.getDatabaseHelper().insertInUser(users);
                        String roomid = MessageMaker.createRoomId(users.getContactNumber());
                        MessageMaker.getFirebaseService().saveToFireStore("Chats").document(roomid).collection("Infos").document("SecurityInfo").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot != null) {
                                    SecurityDTO securityDTO = documentSnapshot.toObject(SecurityDTO.class);
                                    if(securityDTO != null) {
                                        MessageMaker.getDatabaseHelper().insertInSecurity(roomid,securityDTO);
                                        Alerter.create(getActivity())
                                                .setTitle("New Freind Added")
                                                .setIcon(R.drawable.freinds)
                                                .setText(users.getName()+ " accepted your freind request. Now you can chat and call "+users.getName())
                                                .addButton("Chat Now", R.style.AlertButton, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        MessageMaker.startChatroom(getContext(),users.getContactNumber());
                                                    }
                                                })
                                                .setDuration(10000)
                                                .setBackgroundColorRes(R.color.purple_500)
                                                .show();
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void loadPrevious() {
        syncOnline();
    }

    private void updateCount() {
        if(freindRequestDTOS.size() > 0) {
            MessageMaker.hideNotFound(notfound);
        } else {
            MessageMaker.showNotFound(notfound);
        }
        count.setText(freindRequestDTOS.size()+"");
    }

    private void syncOnline() {
        updateCount();
        MessageMaker.getFirebaseService().readFromFireStore("Users").document(MessageMaker.getMyNumber()).collection("FreindRequests").document("Receive").collection("List").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error == null) {
                    for(DocumentChange documentChange:value.getDocumentChanges()) {
                        FreindRequestSaveDTO freindRequestSaveDTO = documentChange.getDocument().toObject(FreindRequestSaveDTO.class);
                        switch (documentChange.getType()) {
                            case ADDED:
                                addRequest(freindRequestSaveDTO);
                                break;
                            case MODIFIED:
                                break;
                            case REMOVED:
                                removeFromList(freindRequestSaveDTO);
                                break;
                        }
                    }
                }
            }
        });
    }

    private void removeFromList(FreindRequestSaveDTO freindRequestSaveDTO) {
        int index = added.indexOf(freindRequestSaveDTO.getContactNumber());
        freindRequestDTOS.remove(index);
        recyclerViewAdapter.notifyItemRemoved(index);
        updateCount();
    }

    private void addRequest(FreindRequestSaveDTO freindRequestSaveDTO) {
        if(!added.contains(freindRequestSaveDTO.getContactNumber())) {
            added.add(freindRequestSaveDTO.getContactNumber());
            FreindRequestDTO freindRequestDTO = new FreindRequestDTO();
            freindRequestDTO.setContactNumber(freindRequestSaveDTO.getContactNumber());
            freindRequestDTO.setRequestSentAt(freindRequestSaveDTO.getRequestSentAt());
            MessageMaker.getFirebaseService().readFromFireStore("Users").document(freindRequestSaveDTO.getContactNumber()).collection("AccountInfo").document("PersonalInfo").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        if(task.getResult().exists()) {
                            User user = task.getResult().toObject(User.class);
                            freindRequestDTO.setName(user.getName());
                            freindRequestDTO.setVerified(user.getVerified());
                            freindRequestDTO.setStatus(user.getStatus());
                            freindRequestDTO.setProfileUrl(user.getProfileUrl());
                            freindRequestDTOS.add(freindRequestDTO);
                            recyclerViewAdapter.notifyItemInserted(freindRequestDTOS.size() -1);
                            updateCount();
                        }
                    }
                }
            });
        }

    }

    private void readSent() {
        MessageMaker.getFirebaseService().readFromFireStore("Users").document(MessageMaker.getMyNumber()).collection("FreindRequests").document("Sent").collection("List").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error == null) {
                    for(DocumentChange documentChange:value.getDocumentChanges()) {
                        switch (documentChange.getType()) {
                            case ADDED:
                                break;
                            case MODIFIED:
                                break;
                            case REMOVED:
                                break;
                        }
                    }
                }
            }
        });
    }

    private void syncFreindList() {
        MessageMaker.getFirebaseService().readFromFireStore("Users").document(MessageMaker.getMyNumber()).collection("Freinds").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error == null) {
                    for(DocumentChange documentChange:value.getDocumentChanges()) {
                        FreindRequestSaveDTO freindRequestSaveDTO = documentChange.getDocument().toObject(FreindRequestSaveDTO.class);
                        switch (documentChange.getType()) {
                            case ADDED:
                                updateInDatabase(freindRequestSaveDTO.getContactNumber());
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
        MessageMaker.getFirebaseService().readFromFireStore("Users").document(MessageMaker.getMyNumber()).collection("AccountInfo").document("RecentChats").collection("History").document(freindRequestSaveDTO.getContactNumber()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                MessageMaker.getFirebaseService().saveToFireStore("Chats").document(MessageMaker.createRoomId(freindRequestSaveDTO.getContactNumber())).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        MessageMaker.getDatabaseHelperChat().deleteMessagesOf(MessageMaker.createRoomId(freindRequestSaveDTO.getContactNumber()));
                        MessageMaker.getDatabaseHelper().deleteRecentChat(freindRequestSaveDTO.getContactNumber());
                        MessageMaker.getDatabaseHelper().deleteUser(freindRequestSaveDTO.getContactNumber());
                        MessageMaker.getDatabaseHelper().deleteSecurityInfo(MessageMaker.createRoomId(freindRequestSaveDTO.getContactNumber()));
                        MessageMaker.removeFromRecentChatUI(freindRequestSaveDTO.getContactNumber());
                    }
                });
            }
        });

    }

}
