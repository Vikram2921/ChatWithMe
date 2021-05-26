package com.nobodyknows.chatwithme.Activities.Dashboard.Interfaces;

import com.NobodyKnows.chatlayoutview.Model.User;

import java.util.List;

public interface AddCallListener {
    public void onVideoCall(User user);
    public void onAudioCall(User user);
}
