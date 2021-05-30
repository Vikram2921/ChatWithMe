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

    public void initalize(Message message, Context context, ChatLayoutListener chatLayoutListener) {
        RoundedImageView image1 = view.findViewById(R.id.image1);
        RoundedImageView image2 = view.findViewById(R.id.image2);
        RoundedImageView image3 = view.findViewById(R.id.image3);
        RoundedImageView image4 = view.findViewById(R.id.image4);
        ProgressButton progressButton = view.findViewById(R.id.progressbutton);
        if(message.getMessageStatus() == MessageStatus.SENDING) {
            progressButton.initalize();
            progressButton.setUploadType();
            Glide.with(context).load(message.getSharedFiles().get(0).getLocalPath()).into(image1);
            Glide.with(context).load(message.getSharedFiles().get(1).getLocalPath()).into(image2);
            Glide.with(context).load(message.getSharedFiles().get(2).getLocalPath()).into(image3);
            Glide.with(context).load(message.getSharedFiles().get(3).getLocalPath()).into(image4);
            if(message.getUploadStatus() == UploadStatus.NOT_STARTED) {
                chatLayoutListener.onUpload(message,progressButton);
                LayoutService.addUploadView(message.getMessageId(),message.getRoomId(),view);
                progressButton.setProgress(0);
            } else {
                if(message.getUploadStatus() == UploadStatus.FAILED) {
                    progressButton.setLabel("Retry");
                }
            }
            progressButton.setProgressClickListener(new ProgressClickListener() {
                @Override
                public void onStart() {
                    chatLayoutListener.onUpload(message,progressButton);
                    LayoutService.addUploadView(message.getMessageId(),message.getRoomId(),view);
                }

                @Override
                public void onCancel() {
                    databaseHelper.updateMessageUploadStatus(message.getRoomId(),message.getMessageId(),UploadStatus.CANCELED);
                }
            });
        } else {
            Glide.with(context).load(message.getSharedFiles().get(0).getLocalPath()).into(image1);
            Glide.with(context).load(message.getSharedFiles().get(1).getLocalPath()).into(image2);
            Glide.with(context).load(message.getSharedFiles().get(2).getLocalPath()).into(image3);
            Glide.with(context).load(message.getSharedFiles().get(3).getLocalPath()).into(image4);
        }
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
