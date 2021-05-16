package com.NobodyKnows.chatlayoutview.Interfaces;

import android.view.View;

import com.NobodyKnows.chatlayoutview.Model.Message;


public interface ChatLayoutListener {
    public void onSwipeToReply(Message message, View replyView);
    public void onUploadRetry(Message message);
    public void onMessageSeen(Message message);
}
