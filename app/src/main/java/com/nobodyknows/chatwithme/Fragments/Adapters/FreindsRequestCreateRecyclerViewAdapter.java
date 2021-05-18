package com.nobodyknows.chatwithme.Fragments.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NobodyKnows.chatlayoutview.Model.User;
import com.bumptech.glide.Glide;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.nobodyknows.chatwithme.DTOS.FreindRequestDTO;
import com.nobodyknows.chatwithme.Fragments.Interfaces.FreindsOptionListener;
import com.nobodyknows.chatwithme.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FreindsRequestCreateRecyclerViewAdapter extends RecyclerView.Adapter<FreindsRequestCreateRecyclerViewAdapter.ViewHolder> {

    private ArrayList<User> users;
    private Context context;
    private FreindsOptionListener freindsOptionListener;
    public FreindsRequestCreateRecyclerViewAdapter(Context context, ArrayList<User> users, FreindsOptionListener freindsOptionListener) {
        this.context = context;
        this.freindsOptionListener = freindsOptionListener;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View item = layoutInflater.inflate(R.layout.freind_request_create_list_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(item);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
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
        holder.status.setText(user.getStatus());
        holder.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                freindsOptionListener.onSendFreindRequest(user);
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
        public Button send;
        public TextView status;
        public ViewHolder(View itemView) {
            super(itemView);
            this.profile = itemView.findViewById(R.id.profile);
            this.name = itemView.findViewById(R.id.name);
            this.send = itemView.findViewById(R.id.send);
            this.status = itemView.findViewById(R.id.status);
        }
    }
}
