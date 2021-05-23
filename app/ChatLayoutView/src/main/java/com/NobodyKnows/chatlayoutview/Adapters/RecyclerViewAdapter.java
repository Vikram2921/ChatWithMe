package com.NobodyKnows.chatlayoutview.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NobodyKnows.chatlayoutview.Constants.MessageType;
import com.NobodyKnows.chatlayoutview.Interfaces.ChatLayoutListener;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.NobodyKnows.chatlayoutview.R;
import com.NobodyKnows.chatlayoutview.ViewHolders.ContactMultipleLeft;
import com.NobodyKnows.chatlayoutview.ViewHolders.ContactMultipleRight;
import com.NobodyKnows.chatlayoutview.ViewHolders.ContactSingleLeft;
import com.NobodyKnows.chatlayoutview.ViewHolders.ContactSingleRight;
import com.NobodyKnows.chatlayoutview.ViewHolders.InfoView;
import com.NobodyKnows.chatlayoutview.ViewHolders.DateView;
import com.NobodyKnows.chatlayoutview.ViewHolders.TextMessageViewLeft;
import com.NobodyKnows.chatlayoutview.ViewHolders.TextMessageViewRight;

import java.util.ArrayList;
import java.util.Map;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Message> messages;
    private Context context;
    private Map<String, User> userMap;
    private Map<MessageType,String> downloadPath;

    //*************EXTRAS **************//
    private final int INFO_MESSAGE = 00;
    private final int DATE_MESSAGE = 2921;
    private final int WARNNIG_MESSAGE = 01;
    //***********TEXT ***************//
    private final int SENT_TEXT_MESSAGE = 11;
    private final int SENT_LINK_TEXT_MESSAGE = 12;
    private final int RECEIVE_TEXT_MESSAGE = 13;
    private final int RECEIVE_LINK_TEXT_MESSAGE = 14;
    //**************IMAGES *************//
    private final int SENT_SINGLE_IMAGE = 21;
    private final int SENT_MULTIPLE_IMAGES = 22;
    private final int RECEIVE_SINGLE_IMAGE = 23;
    private final int RECEIVE_MULTIPLE_IMAGES = 24;
    //**************VIDEOS *************//
    private final int SENT_SINGLE_VIDEO = 31;
    private final int SENT_MULTIPLE_VIDEOS = 32;
    private final int RECEIVE_SINGLE_VIDEO = 33;
    private final int RECEIVE_MULTIPLE_VIDEOS = 34;
    //**************DOCUMENT *************//
    private final int SENT_DOCUMENT = 41;
    private final int RECEIVE_DOCUMENT = 42;
    //**************AUDIO FILES *************//
    private final int SENT_AUDIO = 51;
    private final int RECEIVE_AUDIO = 52;
    //**************RECORDING*************//
    private final int SENT_RECORDING = 61;
    private final int RECEIVE_RECORDING = 62;
    //**************GIF*************//
    private final int SENT_GIF = 71;
    private final int RECEIVE_GIF = 72;
    //**************STICKER*************//
    private final int SENT_STICKER = 81;
    private final int RECEIVE_STICKER = 82;
    //**************CONTACTS*************//
    private final int SENT_CONTACT_SINGLE = 91;
    private final int SENT_CONTACT_MULTIPLE = 92;
    private final int RECEIVE_CONTACT_SINGLE = 93;
    private final int RECEIVE_CONTACT_MULTIPLE = 94;

    private String myId = "";
    private ChatLayoutListener chatLayoutListener;
    public RecyclerViewAdapter(Context context, ArrayList<Message> messages, Map<String, User> userMap, Map<MessageType,String> downloadPaths, String myId, ChatLayoutListener chatLayoutListener) {
        this.context = context;
        this.messages = messages;
        this.userMap = userMap;
        this.myId = myId;
        this.chatLayoutListener = chatLayoutListener;
        this.downloadPath = downloadPaths;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View item = null;
        RecyclerView.ViewHolder  viewHolder = null;
        switch (viewType) {
            case INFO_MESSAGE:
                item = layoutInflater.inflate(R.layout.infoview,parent,false);
                viewHolder = new InfoView(item);
                break;
            case DATE_MESSAGE:
                item = layoutInflater.inflate(R.layout.dateview,parent,false);
                viewHolder = new DateView(item);
                break;
            case SENT_TEXT_MESSAGE:
                item = layoutInflater.inflate(R.layout.message_right_text,parent,false);
                viewHolder = new TextMessageViewRight(item);
                break;
            case RECEIVE_TEXT_MESSAGE:
                item = layoutInflater.inflate(R.layout.message_left_text,parent,false);
                viewHolder = new TextMessageViewLeft(item);
                break;
            case SENT_CONTACT_SINGLE:
                item = layoutInflater.inflate(R.layout.message_right_contact_single,parent,false);
                viewHolder = new ContactSingleRight(item);
                break;
            case SENT_CONTACT_MULTIPLE:
                item = layoutInflater.inflate(R.layout.message_right_contact_multiple,parent,false);
                viewHolder = new ContactMultipleRight(item);
                break;
            case RECEIVE_CONTACT_SINGLE:
                item = layoutInflater.inflate(R.layout.message_left_contact_single,parent,false);
                viewHolder = new ContactSingleLeft(item);
                break;
            case RECEIVE_CONTACT_MULTIPLE:
                item = layoutInflater.inflate(R.layout.message_left_contact_multiple,parent,false);
                viewHolder = new ContactMultipleLeft(item);
                break;
            default:
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.setIsRecyclable(true);
        switch (holder.getItemViewType()){
            case INFO_MESSAGE:
                ((InfoView) holder).initalize(message.getMessage());
                break;
            case DATE_MESSAGE:
                ((DateView) holder).initalize(message.getMessage());
                break;
            case SENT_TEXT_MESSAGE:
                ((TextMessageViewRight) holder).initalize(message);
                break;
            case RECEIVE_TEXT_MESSAGE:
                ((TextMessageViewLeft) holder).initalize(message,userMap.get(message.getSender()),chatLayoutListener);
                break;
            case SENT_CONTACT_SINGLE:
                ((ContactSingleRight) holder).initalize(context,message);
                break;
            case SENT_CONTACT_MULTIPLE:
                ((ContactMultipleRight) holder).initalize(context,message);
                break;
            case RECEIVE_CONTACT_SINGLE:
                ((ContactSingleLeft) holder).initalize(context,message,userMap.get(message.getSender()),chatLayoutListener);
                break;
            case RECEIVE_CONTACT_MULTIPLE:
                ((ContactMultipleLeft) holder).initalize(context,message,userMap.get(message.getSender()),chatLayoutListener);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
       Message message = messages.get(position);
       int type = -1;
       if(message.getMessageType() == MessageType.INFO) {
           type =  INFO_MESSAGE;
       } else if(message.getMessageType() == MessageType.DATE) {
           type =  DATE_MESSAGE;
       } else if(message.getMessageType() == MessageType.WARNING) {
           type =  WARNNIG_MESSAGE;
       } else {
           if(message.getSender() != null && message.getSender().length() > 0 && message.getSender().equalsIgnoreCase(myId)) {
               switch (message.getMessageType()) {
                   case TEXT:
                       type =  SENT_TEXT_MESSAGE;
                       break;
                   case CONTACT_SINGLE:
                       type =  SENT_CONTACT_SINGLE;
                       break;
                   case CONTACT_MULTIPLE:
                       type =  SENT_CONTACT_MULTIPLE;
                       break;
                   default:
                       break;
               }
           } else {
               switch (message.getMessageType()) {
                   case TEXT:
                       type =  RECEIVE_TEXT_MESSAGE;
                       break;
                   case CONTACT_SINGLE:
                       type =  RECEIVE_CONTACT_SINGLE;
                       break;
                   case CONTACT_MULTIPLE:
                       type =  RECEIVE_CONTACT_MULTIPLE;
                       break;
                   default:
                       break;
               }
           }
       }
       return type;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

}
