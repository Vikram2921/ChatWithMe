package com.NobodyKnows.chatlayoutview.ViewHolders;

import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.NobodyKnows.chatlayoutview.R;
import com.NobodyKnows.chatlayoutview.Services.LayoutService;


public class TextMessageViewLeft extends RecyclerView.ViewHolder {
    View view;
    TextView textView;
    public TextMessageViewLeft(@NonNull View itemView) {
        super(itemView);
        view = itemView;
    }

    public void initalize(Message message, User user) {
        TextView messageText = view.findViewById(R.id.messagetext);
        TextView messageTime = view.findViewById(R.id.datetext);
        messageText.setAutoLinkMask(Linkify.ALL);
        messageText.setText(message.getMessage());
        messageTime.setText(LayoutService.getFormatedDate("hh:mm aa", message.getSentAt()));
    }
}
