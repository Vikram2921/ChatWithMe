package com.nobodyknows.chatwithme.Fragments.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.nobodyknows.chatwithme.Activities.ChatRoom;
import com.nobodyknows.chatwithme.DTOS.UserListItemDTO;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.MessageMaker;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<UserListItemDTO> users;
    private Context context;
    private Activity activity;
    public RecyclerViewAdapter(Context context, ArrayList<UserListItemDTO> users, Activity activity) {
        this.context = context;
        this.users = users;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View item = layoutInflater.inflate(R.layout.user_list_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(item);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserListItemDTO user = users.get(position);
        holder.unreadMessageCount.setVisibility(View.GONE);
        if(user.getName() != null && user.getName().length() > 0) {
            holder.name.setText(user.getName());
        } else {
            holder.name.setText(user.getContactNumber());
        }
        if(user.getProfileUrl() != null && user.getProfileUrl().length() > 0 && !user.getProfileUrl().equalsIgnoreCase("NO_PROFILE")){
            Glide.with(context).load(user.getProfileUrl()).placeholder(R.drawable.profile).override(200).into(holder.profile);
        } else {
            Glide.with(context).load(R.drawable.profile).into(holder.profile);
        }
        if(user.getLastMessage() != null) {
            holder.lastMessage.setText(user.getLastMessage().getMessage());
            holder.lastDate.setReferenceTime(user.getLastMessage().getSentAt().getTime());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatRoom.class);
                intent.putExtra("username",user.getContactNumber());
                intent.putExtra("name",user.getName());
                intent.putExtra("lastOnlineStatus",user.getCurrentStatus());
                intent.putExtra("verified",user.getVerified());
                intent.putExtra("roomid", MessageMaker.createRoomId(context,user.getContactNumber()));
                intent.putExtra("profile",user.getProfileUrl());
                context.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView profile;
        public TextView name;
        public TextView lastMessage;
        public TextView unreadMessageCount;
        public ImageView status;
        public RelativeTimeTextView lastDate;
        public ViewHolder(View itemView) {
            super(itemView);
            this.profile = itemView.findViewById(R.id.circleImageView);
            this.name = itemView.findViewById(R.id.name);
            this.lastMessage = itemView.findViewById(R.id.lastemessage);
            this.lastDate = itemView.findViewById(R.id.lastdate);
            this.status = itemView.findViewById(R.id.status);
            this.unreadMessageCount = itemView.findViewById(R.id.unreadmessagecount);
        }
    }
}
