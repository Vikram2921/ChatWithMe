package com.nobodyknows.chatwithme.Activities.Dashboard.Interfaces;

import com.NobodyKnows.chatlayoutview.Model.User;

import java.util.List;

public interface SelectListener {
    public void onContactSelected(List<User> selectedContacts);
    public void onStartChat(User users);
}
