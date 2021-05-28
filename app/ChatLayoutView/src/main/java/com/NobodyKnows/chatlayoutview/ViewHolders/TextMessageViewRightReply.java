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

import static com.NobodyKnows.chatlayoutview.ChatLayoutView.helper;


public class TextMessageViewRightReply extends RecyclerView.ViewHolder {
    View view;
    TextView textView;
    public TextMessageViewRightReply(@NonNull View itemView) {
        super(itemView);
        view = itemView;
    }

    public void initalize(Message message,User user, ChatLayoutListener chatLayoutListener) {
        TextView messageText = view.findViewById(R.id.messagetext);
        TextView status = view.findViewById(R.id.status);
        messageText.setAutoLinkMask(Linkify.ALL);
        messageText.setText(message.getMessage());
        LayoutService.updateMessageStatus(message,status);
        LayoutService.updateReplyView(message.getReplyMessage(),view.findViewById(R.id.replyview));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status.getVisibility() == View.VISIBLE) {
                    status.setVisibility(View.GONE);
                } else {
                    status.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
