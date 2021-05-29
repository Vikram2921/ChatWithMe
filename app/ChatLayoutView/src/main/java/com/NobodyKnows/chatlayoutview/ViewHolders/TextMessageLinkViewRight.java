package com.NobodyKnows.chatlayoutview.ViewHolders;

import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.R;
import com.NobodyKnows.chatlayoutview.Services.LayoutService;


public class TextMessageLinkViewRight extends RecyclerView.ViewHolder {
    View view;
    TextView textView;
    public TextMessageLinkViewRight(@NonNull View itemView) {
        super(itemView);
        view = itemView;
    }

    public void initalize(Message message) {
        TextView messageText = view.findViewById(R.id.messagetext);
        TextView status = view.findViewById(R.id.status);
        messageText.setAutoLinkMask(Linkify.ALL);
        messageText.setText(message.getMessage());
        LayoutService.updateMessageStatus(message,status);
        LayoutService.updateLinkView(message.getMessage(),view.findViewById(R.id.linkview));
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
