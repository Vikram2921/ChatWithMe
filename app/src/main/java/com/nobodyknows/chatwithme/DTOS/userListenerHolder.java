package com.nobodyknows.chatwithme.DTOS;

import com.google.firebase.firestore.ListenerRegistration;

public class userListenerHolder {

    private UserListItemDTO userListItemDTO;
    private ListenerRegistration listenerRegistration;

    public UserListItemDTO getUserListItemDTO() {
        return userListItemDTO;
    }

    public void setUserListItemDTO(UserListItemDTO userListItemDTO) {
        this.userListItemDTO = userListItemDTO;
    }

    public ListenerRegistration getListenerRegistration() {
        return listenerRegistration;
    }

    public void setListenerRegistration(ListenerRegistration listenerRegistration) {
        this.listenerRegistration = listenerRegistration;
    }
}
