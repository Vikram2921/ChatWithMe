package com.NobodyKnows.chatlayoutview.ViewHolders;

import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NobodyKnows.chatlayoutview.Interfaces.ChatLayoutListener;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.NobodyKnows.chatlayoutview.R;
import com.NobodyKnows.chatlayoutview.Services.LayoutService;


public class TextMessageLinkViewLeft extends RecyclerView.ViewHolder {
    View view;
    TextView textView;
    public TextMessageLinkViewLeft(@NonNull View itemView) {
        super(itemView);
        view = itemView;
    }

    public void initalize(Message message, User user, ChatLayoutListener chatLayoutListener) {
        TextView messageText = view.findViewById(R.id.messagetext);
        messageText.setAutoLinkMask(Linkify.ALL);
        messageText.setText(message.getMessage());
        LayoutService.setUpSenderName(view,user,message,chatLayoutListener);
        LayoutService.updateLinkView(message.getMessage(),view.findViewById(R.id.linkview));
    }
}
