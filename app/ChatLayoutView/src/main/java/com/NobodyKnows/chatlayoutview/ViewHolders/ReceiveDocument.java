package com.NobodyKnows.chatlayoutview.ViewHolders;

import android.content.Context;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NobodyKnows.chatlayoutview.Interfaces.ChatLayoutListener;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.R;
import com.NobodyKnows.chatlayoutview.Services.LayoutService;


public class ReceiveDocument extends RecyclerView.ViewHolder {
    View view;
    public ReceiveDocument(@NonNull View itemView) {
        super(itemView);
        view = itemView;
    }

    public void initalize(Message message, Context context, String myId, ChatLayoutListener chatLayoutListener) {
        LayoutService.loadDocumentViewSingle(context,message,chatLayoutListener,view,myId);
    }
}
