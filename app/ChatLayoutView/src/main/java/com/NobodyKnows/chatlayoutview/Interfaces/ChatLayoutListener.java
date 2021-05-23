package com.NobodyKnows.chatlayoutview.Interfaces;

import android.view.View;

import com.NobodyKnows.chatlayoutview.Model.Contact;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.User;


public interface ChatLayoutListener {
    public void onSwipeToReply(Message message, View replyView);
    public void onUploadRetry(Message message);
    public void onMessageSeen(Message message);
    public void onMessageSeenConfirmed(Message message);
    public void onSenderNameClicked(User user, Message message);

    void onClickChatFromContactMessage(Contact contact);

    void onClickAddContactFromContactMessage(Contact contact);
}
