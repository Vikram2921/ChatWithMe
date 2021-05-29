package com.NobodyKnows.chatlayoutview.ViewHolders;

import android.content.Context;
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


public class GifViewLeftReply extends RecyclerView.ViewHolder {
    View view;
    TextView textView;
    public GifViewLeftReply(@NonNull View itemView) {
        super(itemView);
        view = itemView;
    }

    public void initalize(Message message, Context context) {
        ImageView imageView = view.findViewById(R.id.gif);
        Glide.with(context).load(message.getMessage()).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC)).into(imageView);
        LayoutService.updateReplyView(message.getReplyMessage(),view.findViewById(R.id.replyview));
    }
}
