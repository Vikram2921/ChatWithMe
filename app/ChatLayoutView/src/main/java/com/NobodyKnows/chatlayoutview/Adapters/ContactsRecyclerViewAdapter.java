package com.NobodyKnows.chatlayoutview.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NobodyKnows.chatlayoutview.Constants.MessageType;
import com.NobodyKnows.chatlayoutview.Interfaces.ChatLayoutListener;
import com.NobodyKnows.chatlayoutview.Model.Contact;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.NobodyKnows.chatlayoutview.R;
import com.NobodyKnows.chatlayoutview.ViewHolders.ContactMultipleLeft;
import com.NobodyKnows.chatlayoutview.ViewHolders.ContactMultipleRight;
import com.NobodyKnows.chatlayoutview.ViewHolders.ContactSingleLeft;
import com.NobodyKnows.chatlayoutview.ViewHolders.ContactSingleRight;
import com.NobodyKnows.chatlayoutview.ViewHolders.DateView;
import com.NobodyKnows.chatlayoutview.ViewHolders.InfoView;
import com.NobodyKnows.chatlayoutview.ViewHolders.TextMessageViewLeft;
import com.NobodyKnows.chatlayoutview.ViewHolders.TextMessageViewRight;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<ContactsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Contact> contacts;
    private Context context;
    private ChatLayoutListener chatLayoutListener;
    private Activity activity;
    public ContactsRecyclerViewAdapter(Context context, ArrayList<Contact> contacts, ChatLayoutListener chatLayoutListener,Activity activity) {
        this.context = context;
        this.contacts = contacts;
        this.activity = activity;
        this.chatLayoutListener = chatLayoutListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View item = layoutInflater.inflate(R.layout.contact_list_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(item);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        String profile_url = contact.getProfileUrl();
        if(profile_url != null && profile_url.length() > 0 && !profile_url.equalsIgnoreCase("NO_PROFILE")) {
            Glide.with(context).load(profile_url).into(holder.profile);
        } else {
            Glide.with(context).load(R.drawable.profile).into(holder.profile);
        }
        holder.number.setText(contact.getContactNumbers());
        holder.name.setText(contact.getName());
        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatLayoutListener.onClickChatFromContactMessage(contact);
                activity.finish();
            }
        });
        holder.addcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatLayoutListener.onClickAddContactFromContactMessage(contact);
                activity.finish();
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
        public TextView number;
        ImageView chat;
        ImageView addcontact;
        public ViewHolder(View itemView) {
            super(itemView);
            this.profile = itemView.findViewById(R.id.profile);
            this.name = itemView.findViewById(R.id.name);
            this.number = itemView.findViewById(R.id.contactnumber);
            this.chat = itemView.findViewById(R.id.chat);
            this.addcontact = itemView.findViewById(R.id.addcontact);
        }
    }

}
