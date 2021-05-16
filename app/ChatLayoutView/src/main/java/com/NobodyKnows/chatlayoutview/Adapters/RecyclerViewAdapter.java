package com.NobodyKnows.chatlayoutview.Adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NobodyKnows.chatlayoutview.Constants.MessageType;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.NobodyKnows.chatlayoutview.R;
import com.NobodyKnows.chatlayoutview.ViewHolders.DateAndInfoView;
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
    private final int DATE_AND_INFO_MESSAGE = 00;
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
    private final int SENT_CONTACT = 91;
    private final int RECEIVE_CONTACT = 92;

    private String myId = "";

    public RecyclerViewAdapter(Context context, ArrayList<Message> messages, Map<String, User> userMap, Map<MessageType,String> downloadPaths,String myId) {
        this.context = context;
        this.messages = messages;
        this.userMap = userMap;
        this.myId = myId;
        this.downloadPath = downloadPaths;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View item = null;
        RecyclerView.ViewHolder  viewHolder = null;
        switch (viewType) {
            case DATE_AND_INFO_MESSAGE:
                item = layoutInflater.inflate(R.layout.infoview,parent,false);
                viewHolder = new DateAndInfoView(item);
                break;
//            case WARNNIG_MESSAGE:
//                item = layoutInflater.inflate(R.layout.warning_view,parent,false);
//                viewHolder = new WarningView(item);
//                break;
            case SENT_TEXT_MESSAGE:
                item = layoutInflater.inflate(R.layout.message_right_text,parent,false);
                viewHolder = new TextMessageViewRight(item);
                break;
            case RECEIVE_TEXT_MESSAGE:
                item = layoutInflater.inflate(R.layout.message_left_text,parent,false);
                viewHolder = new TextMessageViewLeft(item);
                break;
//            case SENT_LINK_TEXT_MESSAGE:
//                item = layoutInflater.inflate(R.layout.messageview_right_linktext,parent,false);
//                viewHolder = new LinkTextMessageViewRight(item);
//                break;
//            case RECEIVE_LINK_TEXT_MESSAGE:
//                item = layoutInflater.inflate(R.layout.messageview_left_linktext,parent,false);
//                viewHolder = new LinkTextMessageViewLeft(item);
//                break;
//            case SENT_SINGLE_IMAGE:
//                item = layoutInflater.inflate(R.layout.messageview_right_single_image,parent,false);
//                viewHolder = new SentSingleImage(item);
//                break;
//            case RECEIVE_SINGLE_IMAGE:
//                item = layoutInflater.inflate(R.layout.messageview_left_single_image,parent,false);
//                viewHolder = new ReceiveSingleImage(item);
//                break;
//            case SENT_SINGLE_VIDEO:
//                item = layoutInflater.inflate(R.layout.messageview_right_single_video,parent,false);
//                viewHolder = new SentSingleVideo(item);
//                break;
//            case RECEIVE_SINGLE_VIDEO:
//                item = layoutInflater.inflate(R.layout.messageview_left_single_video,parent,false);
//                viewHolder = new ReceiveSingleVideo(item);
//                break;
//            case SENT_MULTIPLE_IMAGES:
//                item = layoutInflater.inflate(R.layout.messageview_right_multiple_image,parent,false);
//                viewHolder = new SentMultipleImages(item);
//                break;
//            case RECEIVE_MULTIPLE_IMAGES:
//                item = layoutInflater.inflate(R.layout.messageview_left_mutiple_image,parent,false);
//                viewHolder = new ReceiveMultipleImages(item);
//                break;
//            case SENT_MULTIPLE_VIDEOS:
//                item = layoutInflater.inflate(R.layout.messageview_right_multiple_videos,parent,false);
//                viewHolder = new SentMultipleVideos(item);
//                break;
//            case RECEIVE_MULTIPLE_VIDEOS:
//                item = layoutInflater.inflate(R.layout.messageview_left_mutiple_video,parent,false);
//                viewHolder = new ReceiveMultipleVideos(item);
//                break;
//            case SENT_DOCUMENT:
//                item = layoutInflater.inflate(R.layout.messageview_right_document,parent,false);
//                viewHolder = new DocumentMessageViewRight(item);
//                break;
//            case RECEIVE_DOCUMENT:
//                item = layoutInflater.inflate(R.layout.messageview_left_document,parent,false);
//                viewHolder = new DocumentMessageViewLeft(item);
//                break;
//            case SENT_AUDIO:
//                item = layoutInflater.inflate(R.layout.messageview_right_audio,parent,false);
//                viewHolder = new SentAudio(item);
//                break;
//            case RECEIVE_AUDIO:
//                item = layoutInflater.inflate(R.layout.messageview_left_audio,parent,false);
//                viewHolder = new ReceiveAudio(item);
//                break;
//            case SENT_RECORDING:
//                item = layoutInflater.inflate(R.layout.messageview_right_recording,parent,false);
//                viewHolder = new SentRecording(item);
//                break;
//            case RECEIVE_RECORDING:
//                item = layoutInflater.inflate(R.layout.messageview_left_recording,parent,false);
//                viewHolder = new ReceiveRecording(item);
//                break;
//            case SENT_GIF:
//                item = layoutInflater.inflate(R.layout.messageview_right_gif,parent,false);
//                viewHolder = new SentGif(item);
//                break;
//            case RECEIVE_GIF:
//                item = layoutInflater.inflate(R.layout.messageview_left_gif,parent,false);
//                viewHolder = new ReceiveGif(item);
//                break;
//            case SENT_STICKER:
//                item = layoutInflater.inflate(R.layout.messageview_right_sticker,parent,false);
//                viewHolder = new SentSticker(item);
//                break;
//            case RECEIVE_STICKER:
//                item = layoutInflater.inflate(R.layout.messageview_left_sticker,parent,false);
//                viewHolder = new ReceiveSticker(item);
//                break;
//            case SENT_CONTACT:
//                item = layoutInflater.inflate(R.layout.messageview_right_contact,parent,false);
//                viewHolder = new SentContact(item);
//                break;
//            case RECEIVE_CONTACT:
//                item = layoutInflater.inflate(R.layout.messageview_left_contact,parent,false);
//                viewHolder = new ReceiveContact(item);
//                break;
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
            case DATE_AND_INFO_MESSAGE:
                ((DateAndInfoView) holder).initalize(message.getMessage());
                break;
//            case WARNNIG_MESSAGE:
//                ((WarningView) holder).initalize(message.getMessage());
//                break;
            case SENT_TEXT_MESSAGE:
                ((TextMessageViewRight) holder).initalize(message);
                break;
            case RECEIVE_TEXT_MESSAGE:
                ((TextMessageViewLeft) holder).initalize(message,userMap.get(message.getSender()));
                break;
//            case SENT_LINK_TEXT_MESSAGE:
//                ((LinkTextMessageViewRight) holder).initalize(message);
//                break;
//            case RECEIVE_LINK_TEXT_MESSAGE:
//                ((LinkTextMessageViewLeft) holder).initalize(message,userMap.get(message.getSender()));
//                break;
//            case SENT_SINGLE_IMAGE:
//                ((SentSingleImage) holder).initalize(context,message);
//                break;
//            case RECEIVE_SINGLE_IMAGE:
//                ((ReceiveSingleImage) holder).initalize(context,message,userMap.get(message.getSender()));
//                break;
//            case SENT_SINGLE_VIDEO:
//                ((SentSingleVideo) holder).initalize(context,message);
//                break;
//            case RECEIVE_SINGLE_VIDEO:
//                ((ReceiveSingleVideo) holder).initalize(context,message,userMap.get(message.getSender()));
//                break;
//            case SENT_MULTIPLE_IMAGES:
//                ((SentMultipleImages) holder).initalize(context,message);
//                break;
//            case RECEIVE_MULTIPLE_IMAGES:
//                ((ReceiveMultipleImages) holder).initalize(context,message,userMap.get(message.getSender()));
//                break;
//            case SENT_MULTIPLE_VIDEOS:
//                ((SentMultipleVideos) holder).initalize(context,message);
//                break;
//            case RECEIVE_MULTIPLE_VIDEOS:
//                ((ReceiveMultipleVideos) holder).initalize(context,message,userMap.get(message.getSender()));
//                break;
//            case SENT_DOCUMENT:
//                ((DocumentMessageViewRight) holder).initalize(context,message);
//                break;
//            case RECEIVE_DOCUMENT:
//                ((DocumentMessageViewLeft) holder).initalize(context,message,userMap.get(message.getSender()));
//                break;
//            case SENT_AUDIO:
//                ((SentAudio) holder).initalize(context,message);
//                break;
//            case RECEIVE_AUDIO:
//                ((ReceiveAudio) holder).initalize(context,message,userMap.get(message.getSender()));
//                break;
//            case SENT_RECORDING:
//                ((SentRecording) holder).initalize(context,message,userMap.get(message.getSender()));
//                break;
//            case RECEIVE_RECORDING:
//                ((ReceiveRecording) holder).initalize(context,message,userMap.get(message.getSender()));
//                break;
//            case SENT_GIF:
//                ((SentGif) holder).initalize(context,message);
//                break;
//            case RECEIVE_GIF:
//                ((ReceiveGif) holder).initalize(context,message,userMap.get(message.getSender()));
//                break;
//            case SENT_STICKER:
//                ((SentSticker) holder).initalize(context,message);
//                break;
//            case RECEIVE_STICKER:
//                ((ReceiveSticker) holder).initalize(context,message,userMap.get(message.getSender()));
//                break;
//            case SENT_CONTACT:
//                ((SentContact) holder).initalize(context,message);
//                break;
//            case RECEIVE_CONTACT:
//                ((ReceiveContact) holder).initalize(context,message,userMap.get(message.getSender()));
//                break;
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
       Message message = messages.get(position);
       if(message.getMessageType() == MessageType.DATE || message.getMessageType() == MessageType.INFO) {
           return DATE_AND_INFO_MESSAGE;
       } else if(message.getMessageType() == MessageType.WARNING) {
           return WARNNIG_MESSAGE;
       } else {
           if(message.getSender() != null && message.getSender().length() > 0 && message.getSender().equalsIgnoreCase(myId)) {
               if(message.getMessageType() == MessageType.TEXT) {
                   return SENT_TEXT_MESSAGE;
               } else  if(message.getMessageType() == MessageType.LINK_TEXT_VIEW) {
                   return SENT_LINK_TEXT_MESSAGE;
               }  else if(message.getMessageType() == MessageType.IMAGE) {
                   if(message.getSharedFiles().size() < 4) {
                       return SENT_SINGLE_IMAGE;
                   } else {
                       return SENT_MULTIPLE_IMAGES;
                   }
               } else if(message.getMessageType() == MessageType.VIDEO) {
                   if(message.getSharedFiles().size() < 4) {
                       return SENT_SINGLE_VIDEO;
                   } else {
                       return SENT_MULTIPLE_VIDEOS;
                   }
               }  else if(message.getMessageType() == MessageType.DOCUMENT) {
                   return SENT_DOCUMENT;
               } else if(message.getMessageType() == MessageType.AUDIO) {
                   return SENT_AUDIO;
               } else if(message.getMessageType() == MessageType.RECORDING) {
                   return SENT_RECORDING;
               } else if(message.getMessageType() == MessageType.GIF) {
                   return SENT_GIF;
               } else if(message.getMessageType() == MessageType.STICKER) {
                   return SENT_STICKER;
               } else if(message.getMessageType() == MessageType.CONTACT) {
                   return SENT_CONTACT;
               }
           } else {
               if(message.getMessageType() == MessageType.TEXT) {
                   return RECEIVE_TEXT_MESSAGE;
               } else if(message.getMessageType() == MessageType.LINK_TEXT_VIEW) {
                   return RECEIVE_LINK_TEXT_MESSAGE;
               }  else if(message.getMessageType() == MessageType.IMAGE) {
                   if(message.getSharedFiles().size() < 4) {
                       return RECEIVE_SINGLE_IMAGE;
                   } else {
                       return RECEIVE_MULTIPLE_IMAGES;
                   }
               } else if(message.getMessageType() == MessageType.VIDEO) {
                   if(message.getSharedFiles().size() < 4) {
                       return RECEIVE_SINGLE_VIDEO;
                   } else {
                       return RECEIVE_MULTIPLE_VIDEOS;
                   }
               } else if(message.getMessageType() == MessageType.DOCUMENT) {
                   return RECEIVE_DOCUMENT;
               }  else if(message.getMessageType() == MessageType.AUDIO) {
                   return RECEIVE_AUDIO;
               }  else if(message.getMessageType() == MessageType.RECORDING) {
                   return RECEIVE_RECORDING;
               } else if(message.getMessageType() == MessageType.GIF) {
                   return RECEIVE_GIF;
               } else if(message.getMessageType() == MessageType.STICKER) {
                   return RECEIVE_STICKER;
               } else if(message.getMessageType() == MessageType.CONTACT) {
                   return RECEIVE_CONTACT;
               }
           }
       }
       return -1;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

}
