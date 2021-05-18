package com.nobodyknows.chatwithme.Activities.Dashboard.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NobodyKnows.chatlayoutview.Model.User;
import com.bumptech.glide.Glide;
import com.nobodyknows.chatwithme.Activities.Dashboard.Interfaces.SelectListener;
import com.nobodyknows.chatwithme.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<ContactsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<User> contacts;
    private Context context;
    protected SelectListener selectListener;
    public ContactsRecyclerViewAdapter(Context context, ArrayList<User> contacts,SelectListener selectListener) {
        this.context = context;
        this.selectListener = selectListener;
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View item = layoutInflater.inflate(R.layout.contact_user_list_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(item);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = contacts.get(position);
        if(user.getName() != null && user.getName().length() > 0) {
            holder.name.setText(user.getName());
        } else {
            holder.name.setText(user.getContactNumber());
        }
        if(user.getStatus() != null && user.getStatus().length() > 0) {
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setText(user.getStatus());
        }
        if(user.getProfileUrl() != null && user.getProfileUrl().length() > 0 && !user.getProfileUrl().equals("NO_PROFILE")){
            Glide.with(context).load(user.getProfileUrl()).placeholder(R.drawable.profile).override(200).into(holder.profile);
        } else {
            Glide.with(context).load(R.drawable.profile).into(holder.profile);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectListener.onStartChat(user);
            }
        });

    }



    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView profile;
        public TextView name;
        public TextView status;
        public ViewHolder(View itemView) {
            super(itemView);
            this.profile = itemView.findViewById(R.id.circleImageView);
            this.name = itemView.findViewById(R.id.name);
            this.status = itemView.findViewById(R.id.status);
        }
    }
}
