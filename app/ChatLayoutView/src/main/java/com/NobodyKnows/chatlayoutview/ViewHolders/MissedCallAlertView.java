package com.NobodyKnows.chatlayoutview.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NobodyKnows.chatlayoutview.Constants.MessageType;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.NobodyKnows.chatlayoutview.R;
import com.NobodyKnows.chatlayoutview.Services.LayoutService;

import java.util.Map;


public class MissedCallAlertView extends RecyclerView.ViewHolder {
    View view;
    TextView textView;
    public MissedCallAlertView(@NonNull View itemView) {
        super(itemView);
        view = itemView;
    }

    public void initalize(Message message,String myId, Map<String, User> userMap) {
        textView = view.findViewById(R.id.infotext);
        String info = "";
        int icon = -1;
        if(message.getSender().equalsIgnoreCase(myId)) {
            icon = R.drawable.ic_baseline_call_missed_outgoing_24;
            if(message.getMessageType() == MessageType.MISSED_AUDIO_CALL) {
                info = userMap.get(message.getReceiver()).getName()+" missed audio call from you.";
            } else if(message.getMessageType() == MessageType.MISSED_VIDEO_CALL) {
                info = userMap.get(message.getReceiver()).getName()+" missed video call from you.";
            }
        } else {
            icon = R.drawable.ic_baseline_call_missed_24;
            if(message.getMessageType() == MessageType.MISSED_AUDIO_CALL) {
                info = "You missed audio call from "+userMap.get(message.getReceiver()).getName()+".";
            } else if(message.getMessageType() == MessageType.MISSED_VIDEO_CALL) {
                info ="You missed video call from "+userMap.get(message.getReceiver()).getName()+".";
            }
        }
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(icon,0,0,0);
        textView.setText(info);
    }
}
