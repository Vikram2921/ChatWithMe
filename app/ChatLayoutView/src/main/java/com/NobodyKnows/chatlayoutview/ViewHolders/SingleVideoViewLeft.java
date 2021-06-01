package com.NobodyKnows.chatlayoutview.ViewHolders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NobodyKnows.chatlayoutview.Interfaces.ChatLayoutListener;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Services.LayoutService;


public class SingleVideoViewLeft extends RecyclerView.ViewHolder {
    View view;
    TextView textView;
    public SingleVideoViewLeft(@NonNull View itemView) {
        super(itemView);
        view = itemView;
    }

    public void initalize(Message message, Context context,String mynumber, ChatLayoutListener chatLayoutListener) {
        LayoutService.loadMediaViewSingle(context,message,chatLayoutListener,view,mynumber);
    }
}
