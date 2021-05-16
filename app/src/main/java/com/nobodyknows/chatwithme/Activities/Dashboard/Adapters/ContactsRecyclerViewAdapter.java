package com.nobodyknows.chatwithme.Activities.Dashboard.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.github.tamir7.contacts.Contact;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.nobodyknows.chatwithme.Activities.ChatRoom;
import com.nobodyknows.chatwithme.Activities.Dashboard.Interfaces.SelectListener;
import com.nobodyknows.chatwithme.Activities.LoginContinue;
import com.nobodyknows.chatwithme.Activities.Signup.CreateUser;
import com.nobodyknows.chatwithme.DTOS.UserListItemDTO;
import com.nobodyknows.chatwithme.Models.Users;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.FirebaseService;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<ContactsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Users> contacts;
    private Context context;
    private SelectListener selectListener;
    private FirebaseService firebaseService;
    private List<Users> selectedContacts = new ArrayList<>();
    public ContactsRecyclerViewAdapter(Context context, ArrayList<Users> contacts, FirebaseService firebaseService, SelectListener selectListener) {
        this.context = context;
        this.firebaseService = firebaseService;
        this.contacts = contacts;
        this.selectListener = selectListener;
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
        Users users = contacts.get(position);
        if(users.getName() != null && users.getName().length() > 0) {
            holder.name.setText(users.getName());
        } else {
            holder.name.setText(users.getContactNumber());
        }
        if(users.getProfileUrl() != null && users.getProfileUrl().length() > 0 && !users.getProfileUrl().equals("NO_PROFILE")){
            Glide.with(context).load(users.getProfileUrl()).placeholder(R.drawable.profile).override(200).into(holder.profile);
        } else {
            Glide.with(context).load(R.drawable.profile).into(holder.profile);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedContacts.contains(users)) {
                    selectedContacts.remove(users);
                    holder.selected.setVisibility(View.GONE);
                } else {
                    selectedContacts.add(users);
                    holder.selected.setVisibility(View.VISIBLE);
                }
                selectListener.onContactSelected(selectedContacts);
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
        private ImageView selected;
        public ViewHolder(View itemView) {
            super(itemView);
            this.selected = itemView.findViewById(R.id.selected);
            this.profile = itemView.findViewById(R.id.circleImageView);
            this.name = itemView.findViewById(R.id.name);
        }
    }
}
