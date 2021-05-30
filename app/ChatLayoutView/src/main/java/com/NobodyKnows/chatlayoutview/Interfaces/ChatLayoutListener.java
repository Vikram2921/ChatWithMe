package com.NobodyKnows.chatlayoutview.Interfaces;

import android.view.View;

import com.NobodyKnows.chatlayoutview.Model.Contact;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.NobodyKnows.chatlayoutview.ProgressButton;


public interface ChatLayoutListener {
    public void onSwipeToReply(Message message, View replyView);
    public void onUpload(Message message, ProgressButton progressButton);
    public void onMessageSeen(Message message);
    public void onMessageSeenConfirmed(Message message);
    public void onSenderNameClicked(User user, Message message);
    public void onClickChatFromContactMessage(Contact contact);
    public void onClickAddContactFromContactMessage(Contact contact);
}
