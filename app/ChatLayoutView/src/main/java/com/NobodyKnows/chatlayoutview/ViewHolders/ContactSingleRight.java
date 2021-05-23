package com.NobodyKnows.chatlayoutview.ViewHolders;

import android.content.Context;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NobodyKnows.chatlayoutview.Model.Contact;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.R;
import com.NobodyKnows.chatlayoutview.Services.LayoutService;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;


public class ContactSingleRight extends RecyclerView.ViewHolder {
    View view;
    TextView textView;
    public ContactSingleRight(@NonNull View itemView) {
        super(itemView);
        view = itemView;
    }

    public void initalize(Context context,Message message) {
        TextView name = view.findViewById(R.id.name);
        CircleImageView profile = view.findViewById(R.id.profile);
        TextView number = view.findViewById(R.id.contactnumber);
        ImageView chat = view.findViewById(R.id.chat);
        ImageView addcontact = view.findViewById(R.id.addcontact);
        String profile_url = message.getContacts().get(0).getProfileUrl();
        if(profile_url != null && profile_url.length() > 0 && !profile_url.equalsIgnoreCase("NO_PROFILE")) {
            Glide.with(context).load(profile_url).into(profile);
        } else {
            Glide.with(context).load(R.drawable.profile).into(profile);
        }
        number.setText(message.getContacts().get(0).getContactNumbers());
        name.setText(message.getContacts().get(0).getName());
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        addcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        TextView status = view.findViewById(R.id.status);
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
