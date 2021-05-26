package com.nobodyknows.chatwithme.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nobodyknows.chatwithme.Activities.Dashboard.AddNewCall;
import com.nobodyknows.chatwithme.DTOS.CallModel;
import com.nobodyknows.chatwithme.DTOS.UserListItemDTO;
import com.nobodyknows.chatwithme.DTOS.userListenerHolder;
import com.nobodyknows.chatwithme.Fragments.Adapters.CallsRecyclerViewAdapter;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.MessageMaker;

import java.util.ArrayList;

import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.databaseHelper;

public class CallsFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    public static ArrayList<CallModel> calls = new ArrayList<>();
    public static ArrayList<String> callIds = new ArrayList<>();
    public static CallsRecyclerViewAdapter callsRecyclerViewAdapter;
    public static ConstraintLayout callNotFound;
    private Button action;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calls, container, false);
        this.view = view;
        init();
        return view;
    }

    private void init() {
        callNotFound = view.findViewById(R.id.notfound);
        recyclerView = view.findViewById(R.id.recyclerview);
        action = view.findViewById(R.id.action);
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddNewCall.class);
                startActivity(intent);
            }
        });
        callsRecyclerViewAdapter = new CallsRecyclerViewAdapter(getActivity(),getContext(),calls);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(false);
        layoutManager.setItemPrefetchEnabled(true);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setInitialPrefetchItemCount(5);
        layoutManager.setRecycleChildrenOnDetach(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(callsRecyclerViewAdapter);
        loadList();
    }

    private void loadList() {
        calls.clear();
        callsRecyclerViewAdapter.notifyDataSetChanged();
        ArrayList<CallModel> allCalls = databaseHelper.getAllCalls();
        for(CallModel callModel:allCalls) {
            addCall(callModel);
        }
        if(allCalls.size() > 0) {
            callNotFound.setVisibility(View.GONE);
        } else {
            callNotFound.setVisibility(View.VISIBLE);
        }
    }

    private void addCall(CallModel callModel) {
        if(!callIds.contains(callModel.getCallId())) {
            calls.add(callModel);
            callIds.add(callModel.getCallId());
            callsRecyclerViewAdapter.notifyItemInserted(calls.size() - 1);
        }
    }

}
