package com.nobodyknows.chatwithme.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nobodyknows.chatwithme.Activities.Dashboard.AddNewChat;
import com.nobodyknows.chatwithme.DTOS.UserListItemDTO;
import com.nobodyknows.chatwithme.Fragments.Adapters.RecyclerViewAdapter;
import com.nobodyknows.chatwithme.R;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private ArrayList<UserListItemDTO> userListItems = new ArrayList<>();
    private RecyclerViewAdapter recyclerViewAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        this.view = view;
        init();
        return view;
    }

    private void init() {
        setupRecyclerView();
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
        addNewChat(new UserListItemDTO());
    }

    private void addNewChat(UserListItemDTO userListItem) {
        userListItems.add(0,userListItem);
        recyclerViewAdapter.notifyItemInserted(0);
    }
}
