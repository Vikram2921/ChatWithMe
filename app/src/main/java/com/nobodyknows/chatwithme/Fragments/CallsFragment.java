package com.nobodyknows.chatwithme.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.nobodyknows.chatwithme.R;

public class CallsFragment extends Fragment {

    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calls, container, false);
        this.view = view;
        init();
        return view;
    }

    private void init() {

    }

}
