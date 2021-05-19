package com.NobodyKnows.chatlayoutview.Interfaces;

import android.view.View;

import com.NobodyKnows.chatlayoutview.Model.Message;


public interface LastMessageUpdateListener {
    public void onLastMessageAdded(Message message, String roomid);
}
