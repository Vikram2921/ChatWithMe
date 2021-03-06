package com.NobodyKnows.chatlayoutview.ViewHolders;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.R;
import com.NobodyKnows.chatlayoutview.Services.LayoutService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;


public class GIFViewRightReply extends RecyclerView.ViewHolder {
    View view;
    TextView textView;
    public GIFViewRightReply(@NonNull View itemView) {
        super(itemView);
        view = itemView;
    }

    public void initalize(Message message, Context context) {
        ImageView imageView = view.findViewById(R.id.gif);
        TextView status = view.findViewById(R.id.status);
        LayoutService.loadGifAndSticker(context,message.getMessage(),imageView);
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
