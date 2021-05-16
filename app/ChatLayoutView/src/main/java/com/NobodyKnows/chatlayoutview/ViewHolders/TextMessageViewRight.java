package com.NobodyKnows.chatlayoutview.ViewHolders;

import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.R;
import com.NobodyKnows.chatlayoutview.Services.LayoutService;


public class TextMessageViewRight extends RecyclerView.ViewHolder {
    View view;
    TextView textView;
    public TextMessageViewRight(@NonNull View itemView) {
        super(itemView);
        view = itemView;
    }

    public void initalize(Message message) {
        TextView messageText = view.findViewById(R.id.messagetext);
        TextView messageTime = view.findViewById(R.id.datetext);
        messageText.setAutoLinkMask(Linkify.ALL);
        messageText.setText(message.getMessage());
        messageTime.setText(LayoutService.getFormatedDate("hh:mm aa", message.getSentAt()));
        ImageView messageStatus = view.findViewById(R.id.status);
        LayoutService.updateMessageStatus(message.getMessageStatus(),messageStatus);
    }
}
