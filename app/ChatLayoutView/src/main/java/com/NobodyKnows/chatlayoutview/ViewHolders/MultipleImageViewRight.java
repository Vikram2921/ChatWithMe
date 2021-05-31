package com.NobodyKnows.chatlayoutview.ViewHolders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NobodyKnows.chatlayoutview.Constants.MessageStatus;
import com.NobodyKnows.chatlayoutview.Constants.UploadStatus;
import com.NobodyKnows.chatlayoutview.Interfaces.ChatLayoutListener;
import com.NobodyKnows.chatlayoutview.Interfaces.ProgressClickListener;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.ProgressButton;
import com.NobodyKnows.chatlayoutview.R;
import com.NobodyKnows.chatlayoutview.Services.LayoutService;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import static com.NobodyKnows.chatlayoutview.ChatLayoutView.databaseHelper;


public class MultipleImageViewRight extends RecyclerView.ViewHolder {
    View view;
    TextView textView;
    public MultipleImageViewRight(@NonNull View itemView) {
        super(itemView);
        view = itemView;
    }

    public void initalize(Message message, Context context,String mynumber, ChatLayoutListener chatLayoutListener) {
       LayoutService.loadMediaViewMultiple(context,message,chatLayoutListener,view,mynumber);
        TextView status = view.findViewById(R.id.status);
        LayoutService.updateMessageStatus(message,status);
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
