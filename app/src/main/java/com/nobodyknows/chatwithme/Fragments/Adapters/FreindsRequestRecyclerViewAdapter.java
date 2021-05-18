package com.nobodyknows.chatwithme.Fragments.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.nobodyknows.chatwithme.DTOS.FreindRequestDTO;
import com.nobodyknows.chatwithme.Fragments.Interfaces.FreindsOptionListener;
import com.nobodyknows.chatwithme.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FreindsRequestRecyclerViewAdapter extends RecyclerView.Adapter<FreindsRequestRecyclerViewAdapter.ViewHolder> {

    private ArrayList<FreindRequestDTO> freindRequestDTOArrayList;
    private Context context;
    private Activity activity;
    private FreindsOptionListener freindsOptionListener;
    public FreindsRequestRecyclerViewAdapter(Context context, ArrayList<FreindRequestDTO> requests, Activity activity,FreindsOptionListener freindsOptionListener) {
        this.context = context;
        this.freindsOptionListener = freindsOptionListener;
        this.freindRequestDTOArrayList = requests;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View item = layoutInflater.inflate(R.layout.freind_request_list_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(item);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FreindRequestDTO request = freindRequestDTOArrayList.get(position);
        if(request.getName() != null && request.getName().length() > 0) {
            holder.name.setText(request.getName());
        } else {
            holder.name.setText(request.getContactNumber());
        }
        if(request.getProfileUrl() != null && request.getProfileUrl().length() > 0 && !request.getProfileUrl().equalsIgnoreCase("NO_PROFILE")){
            Glide.with(context).load(request.getProfileUrl()).placeholder(R.drawable.profile).override(200).into(holder.profile);
        } else {
            Glide.with(context).load(R.drawable.profile).into(holder.profile);
        }
        holder.status.setText(request.getStatus());
        holder.sentat.setReferenceTime(request.getRequestSentAt().getTime());
        holder.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                freindsOptionListener.onConfirm(request);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                freindsOptionListener.onDelete(request);
            }
        });
    }



    @Override
    public int getItemCount() {
        return freindRequestDTOArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView profile;
        public TextView name;
        public Button confirm;
        public Button delete;
        public TextView status;
        public RelativeTimeTextView sentat;
        public ViewHolder(View itemView) {
            super(itemView);
            this.profile = itemView.findViewById(R.id.profile);
            this.name = itemView.findViewById(R.id.name);
            this.confirm = itemView.findViewById(R.id.confirm);
            this.sentat = itemView.findViewById(R.id.sentat);
            this.status = itemView.findViewById(R.id.status);
            this.delete = itemView.findViewById(R.id.delete);
        }
    }
}
