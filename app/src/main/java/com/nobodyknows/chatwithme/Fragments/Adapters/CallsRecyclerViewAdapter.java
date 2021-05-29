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
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.NobodyKnows.chatlayoutview.Model.User;
import com.nobodyknows.chatwithme.Activities.AudioCall;
import com.nobodyknows.chatwithme.Activities.Dashboard.ViewContact;
import com.nobodyknows.chatwithme.DTOS.CallModel;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.MessageMaker;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.databaseHelper;

public class CallsRecyclerViewAdapter extends RecyclerView.Adapter<CallsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<CallModel> callModels;
    private Context context;
    private Activity activity;
    private String DOT = "    ";
    public CallsRecyclerViewAdapter(Activity activity,Context context, ArrayList<CallModel> callModels) {
        this.context = context;
        this.activity = activity;
        this.callModels = callModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View item = layoutInflater.inflate(R.layout.call_list_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(item);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CallModel callModel = callModels.get(position);
        User user = databaseHelper.getUser(callModel.getUsername());
        if(user != null) {
            MessageMaker.loadProfile(context,user.getProfileUrl(),holder.profile);
            holder.name.setText(user.getName());
            holder.profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewContact.class);
                    intent.putExtra("username",user.getContactNumber());
                    intent.putExtra("isFromChat",false);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,v,"profile");
                    context.startActivity(intent,activityOptionsCompat.toBundle());
                }
            });
            if(callModel.getCalltype().equalsIgnoreCase("Video")) {
                holder.calltype.setImageResource(R.drawable.video);
                holder.calltype.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, AudioCall.class);
                        intent.putExtra("username",user.getContactNumber());
                        intent.putExtra("making",true);
                        intent.putExtra("video",true);
                        context.startActivity(intent);
                    }
                });
            } else {
                holder.calltype.setImageResource(R.drawable.audio);
                holder.calltype.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, AudioCall.class);
                        intent.putExtra("username",user.getContactNumber());
                        intent.putExtra("making",true);
                        intent.putExtra("video",false);
                        context.startActivity(intent);
                    }
                });
            }
        } else {
            holder.calltype.setVisibility(View.GONE);
            holder.name.setText(callModel.getUsername());
        }
        switch (callModel.getEndCause()) {
            case "NONE":
                if(callModel.getIncomingCall()) {
                    changeDrawable(holder.status,R.drawable.ic_baseline_call_missed_24,getText(callModel,"Missed Call"));
                } else {
                    changeDrawable(holder.status,R.drawable.ic_baseline_call_missed_outgoing_24,getText(callModel,"Missed Call"));
                }
                break;
            case "TIMEOUT":
                if(callModel.getIncomingCall()) {
                    changeDrawable(holder.status,R.drawable.ic_baseline_call_missed_24,getText(callModel,"Missed Call"));
                } else {
                    changeDrawable(holder.status,R.drawable.ic_baseline_call_missed_outgoing_24,getText(callModel,"Missed Call"));
                }
                break;
            case "DENIED":
                if(callModel.getIncomingCall()) {
                    changeDrawable(holder.status,R.drawable.ic_baseline_call_missed_24,getText(callModel,"Missed Call"));
                } else {
                    changeDrawable(holder.status,R.drawable.ic_baseline_call_missed_outgoing_24,getText(callModel,"Missed Call"));
                }
                break;
            case "NO_ANSWER":
                if(callModel.getIncomingCall()) {
                    changeDrawable(holder.status,R.drawable.ic_baseline_call_missed_24,getText(callModel,"Missed Call"));
                } else {
                    changeDrawable(holder.status,R.drawable.ic_baseline_call_missed_outgoing_24,getText(callModel,"Missed Call"));
                }
                break;
            case "FAILURE":
                if(callModel.getIncomingCall()) {
                    changeDrawable(holder.status,R.drawable.ic_baseline_call_missed_24,getText(callModel,"Missed Call"));
                } else {
                    changeDrawable(holder.status,R.drawable.ic_baseline_call_missed_outgoing_24,getText(callModel,"Missed Call"));
                }
                break;
            case "HUNG_UP":
                if(callModel.getIncomingCall()) {
                    changeDrawable(holder.status,R.drawable.ic_baseline_call_received_24,getText(callModel,"Incoming Call"));
                } else {
                    changeDrawable(holder.status,R.drawable.ic_baseline_call_made_13,getText(callModel,"Outgoing Call"));
                }
                break;
            case "CANCELED":
                if(callModel.getIncomingCall()) {
                    changeDrawable(holder.status,R.drawable.ic_baseline_call_missed_24,getText(callModel,"Incoming Call"));
                } else {
                    changeDrawable(holder.status,R.drawable.ic_baseline_call_missed_outgoing_24,getText(callModel,"Outgoing Call"));
                }
                break;
            case "OTHER_DEVICE_ANSWERED":
                if(callModel.getIncomingCall()) {
                    changeDrawable(holder.status,R.drawable.ic_baseline_call_received_24,getText(callModel,"Incoming Call"));
                } else {
                    changeDrawable(holder.status,R.drawable.ic_baseline_call_made_13,getText(callModel,"Outgoing Call"));
                }
                break;
            default:
                if(callModel.getIncomingCall()) {
                    changeDrawable(holder.status,R.drawable.ic_baseline_call_missed_24,getText(callModel,"Missed Call"));
                } else {
                    changeDrawable(holder.status,R.drawable.ic_baseline_call_missed_outgoing_24,getText(callModel,"Missed Call"));
                }
                break;

        }
    }


    private void changeDrawable(TextView textView,int code,String text) {
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(code,0,0,0);
        textView.setText(text);
    }

    private String getText(CallModel callModel, String state) {
        String status = "";
        try {
            status+=MessageMaker.formatDate(MessageMaker.longToDate(callModel.getStartedTime()),"dd MMMM, hh:mm a");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return status;
    }


    @Override
    public int getItemCount() {
        return callModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView profile;
        public TextView name;
        public ImageView calltype;
        public TextView status;
        public ViewHolder(View itemView) {
            super(itemView);
            this.profile = itemView.findViewById(R.id.profile);
            this.name = itemView.findViewById(R.id.name);
            this.calltype = itemView.findViewById(R.id.calltype);
            this.status = itemView.findViewById(R.id.status);
        }
    }
}
