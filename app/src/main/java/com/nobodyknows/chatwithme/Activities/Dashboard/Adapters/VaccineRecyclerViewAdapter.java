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
import com.nobodyknows.chatwithme.DTOS.VaccineCenter;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.MessageMaker;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class VaccineRecyclerViewAdapter extends RecyclerView.Adapter<VaccineRecyclerViewAdapter.ViewHolder> {

    private ArrayList<VaccineCenter> centers;
    private Context context;
    public VaccineRecyclerViewAdapter(Context context, ArrayList<VaccineCenter> contacts) {
        this.context = context;
        this.centers = contacts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View item = layoutInflater.inflate(R.layout.vaccine_center_list_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(item);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VaccineCenter center = centers.get(position);
        holder.name.setText(center.getName());
        holder.timming.setText("Timmings : "+center.getFrom()+" - "+center.getTo());
        holder.totalavail.setText("Total Available : "+center.getTotalAvailable());
        holder.feetype.setText(center.getFeeType());
        if(center.getFeeType().equalsIgnoreCase("Free")) {
            holder.feetype.setBackgroundResource(R.drawable.free);
        } else {
            holder.feetype.setBackgroundResource(R.drawable.paid);
        }
    }



    @Override
    public int getItemCount() {
        return centers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView feetype;
        public TextView totalavail;
        public TextView timming;
        public ViewHolder(View itemView) {
            super(itemView);
            this.feetype = itemView.findViewById(R.id.feetype);
            this.name = itemView.findViewById(R.id.centername);
            this.totalavail = itemView.findViewById(R.id.total);
            this.timming = itemView.findViewById(R.id.timming);
        }
    }
}
