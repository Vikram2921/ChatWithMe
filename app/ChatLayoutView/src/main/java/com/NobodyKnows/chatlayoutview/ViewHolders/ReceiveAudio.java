package com.NobodyKnows.chatlayoutview.ViewHolders;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NobodyKnows.chatlayoutview.Interfaces.ChatLayoutListener;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Services.LayoutService;


public class ReceiveAudio extends RecyclerView.ViewHolder {
    View view;
    public ReceiveAudio(@NonNull View itemView) {
        super(itemView);
        view = itemView;
    }

    public void initalize(Message message, Context context, String myId, ChatLayoutListener chatLayoutListener) {
        LayoutService.loadAudioViewSingle(context,message,chatLayoutListener,view,myId);
    }
}
