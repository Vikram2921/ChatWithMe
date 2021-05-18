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
import com.nobodyknows.chatwithme.Activities.Dashboard.AddNewChat;
import com.nobodyknows.chatwithme.Activities.Signup.CreatingSetup;
import com.nobodyknows.chatwithme.DTOS.FreindRequestDTO;
import com.nobodyknows.chatwithme.DTOS.FreindRequestSaveDTO;
import com.nobodyknows.chatwithme.Fragments.Adapters.FreindsRequestRecyclerViewAdapter;
import com.nobodyknows.chatwithme.Fragments.Adapters.RecyclerViewAdapter;
import com.nobodyknows.chatwithme.Fragments.Interfaces.FreindsOptionListener;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.FirebaseService;
import com.nobodyknows.chatwithme.services.MessageMaker;

import java.util.ArrayList;
import java.util.Date;

import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.databaseHelper;

public class FreindsFragment extends Fragment {

    private View view;
    private Button search,see;
    private TextView count;
    private FirebaseService firebaseService;
    private RecyclerView recyclerView;
    private FreindsRequestRecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<FreindRequestDTO> freindRequestDTOS = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_freinds, container, false);
        this.view = view;
        init();
        return view;
    }

    private void init() {
        firebaseService = new FirebaseService();
        count = view.findViewById(R.id.requestcount);
        search = view.findViewById(R.id.searchnewfreind);
        see = view.findViewById(R.id.seeallfreinds);
        recyclerView = view.findViewById(R.id.requestlist);
        see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddNewChat.class);
                startActivity(intent);
            }
        });
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        recyclerViewAdapter = new FreindsRequestRecyclerViewAdapter(getContext(), freindRequestDTOS, getActivity(), new FreindsOptionListener() {
            @Override
            public void onConfirm(FreindRequestDTO freindRequestDTO) {
                confirmFreind(freindRequestDTO);
            }

            @Override
            public void onDelete(FreindRequestDTO freindRequestDTO) {

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
        String mynumber= MessageMaker.getFromSharedPrefrences(getContext(),"number");
        FreindRequestSaveDTO fdto = new FreindRequestSaveDTO();
        fdto.setContactNumber(freindRequestDTO.getContactNumber());
        fdto.setRequestSentAt(freindRequestDTO.getRequestSentAt());
        fdto.setRequestSentBy(freindRequestDTO.getContactNumber());
        fdto.setRequestAcceptedAt(new Date());
        firebaseService.saveToFireStore("Users").document(mynumber).collection("Freinds").document("List").collection(freindRequestDTO.getContactNumber()).document("Object").set(fdto).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    FreindRequestSaveDTO myDto = new FreindRequestSaveDTO();
                    myDto.setContactNumber(mynumber);
                    myDto.setRequestSentAt(freindRequestDTO.getRequestSentAt());
                    myDto.setRequestSentBy(freindRequestDTO.getContactNumber());
                    myDto.setRequestAcceptedAt(new Date());
                    firebaseService.saveToFireStore("Users").document(freindRequestDTO.getContactNumber()).collection("Freinds").document("List").collection(mynumber).document("Object").set(myDto).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                deleteRequest(freindRequestDTO);
                            }
                        }
                    });
                }
            }
        });
    }

    private void deleteRequest(FreindRequestDTO freindRequestDTO) {
        firebaseService.readFromFireStore("Users").document(MessageMaker.getFromSharedPrefrences(getContext(),"number")).collection("FreindRequests").document("Receive").collection("List").document(freindRequestDTO.getContactNumber()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                firebaseService.readFromFireStore("Users").document(freindRequestDTO.getContactNumber()).collection("FreindRequests").document("Sent").collection("List").document(MessageMaker.getFromSharedPrefrences(getContext(),"number")).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateInDatabase(freindRequestDTO.getContactNumber());
                    }
                });
            }
        });
    }

    private void updateInDatabase(String username) {
        firebaseService.readFromFireStore("Users").document(username).collection("AccountInfo").document("PersonalInfo").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot != null) {
                    User users = documentSnapshot.toObject(User.class);
                    Toast.makeText(getContext(),users.getName()+" is added as freind successfully",Toast.LENGTH_SHORT).show();
                    databaseHelper.insertInUser(users);
                }
            }
        });
    }

    private void loadPrevious() {
        FreindRequestDTO freindRequestDTO = new FreindRequestDTO();
        freindRequestDTO.setContactNumber("8290879124");
        freindRequestDTOS.add(freindRequestDTO);
        freindRequestDTOS.add(freindRequestDTO);
        freindRequestDTOS.add(freindRequestDTO);
        freindRequestDTOS.add(freindRequestDTO);
        freindRequestDTOS.add(freindRequestDTO);
        recyclerViewAdapter.notifyDataSetChanged();
        syncOnline();
    }

    private void updateCount() {
        count.setText(freindRequestDTOS.size()+"");
    }

    private void syncOnline() {
        updateCount();
        firebaseService.readFromFireStore("Users").document(MessageMaker.getFromSharedPrefrences(getContext(),"number")).collection("FreindRequests").document("Receive").collection("List").addSnapshotListener(new EventListener<QuerySnapshot>() {
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

    private void readSent() {
        firebaseService.readFromFireStore("Users").document(MessageMaker.getFromSharedPrefrences(getContext(),"number")).collection("FreindRequests").document("Sent").collection("List").addSnapshotListener(new EventListener<QuerySnapshot>() {
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

}
