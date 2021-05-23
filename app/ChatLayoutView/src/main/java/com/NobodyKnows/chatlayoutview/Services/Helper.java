package com.NobodyKnows.chatlayoutview.Services;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.NobodyKnows.chatlayoutview.Constants.MessageType;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.NobodyKnows.chatlayoutview.R;
import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;

public class Helper {
    private Integer RECYCLERVIEW = 0;
    private Integer LISTVIEW = 1;
    private Context context;
    private LayoutInflater layoutInflater;
    private Map<String, User> userMap;
    private String myId;
    private Integer mode;
    private String DOT_SEPRATOR = " \u25CF ";
    private RecyclerView recyclerView;
    private ListView listView;
    private ArrayList<String> messageIds = new ArrayList<>();

    public Helper(Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        userMap = new HashMap();
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public ListView getListView() {
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    public ArrayList<String> getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(ArrayList<String> messageIds) {
        this.messageIds = messageIds;
    }

    public void addMessageId(String messageId) {
        this.messageIds.add(messageId);
    }

    public void addMessageId(int index,String messageId) {
        this.messageIds.add(index,messageId);
    }

    public boolean messageIdExists(String messageId) {
        return this.messageIds.contains(messageId);
    }

    public int getMessageIdPositon(String messageId) {
        return this.messageIds.indexOf(messageId);
    }


    public void clearMessagedIds() {
        this.messageIds.clear();
    }

    public void setUserMap(Map<String,User> userMap) {
        this.userMap = userMap;
    }

    public Map<String, User> getUserMap() {
        return this.userMap;
    }

    public void addUser(User user) {
        this.userMap.put(user.getContactNumber(),user);
    }

    public User getUser(String userID) {
        return userMap.get(userID);
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    public View getReplyMessageView(Message message) {
        View view = layoutInflater.inflate(R.layout.replyview,null);
        ImageView preview = view.findViewById(R.id.preview);
        TextView senderName = view.findViewById(R.id.sendername);
        TextView messageview = view.findViewById(R.id.message);
        View bar = view.findViewById(R.id.bar);
        if(message.getMessageType() == MessageType.DATE) {
            senderName.setText("Date");
            messageview.setText(message.getMessage());
            senderName.setTextColor(Color.BLACK);
            bar.setBackgroundColor(Color.BLACK);
            preview.setVisibility(GONE);
        } else {
            User user =getUser(message.getSender());
            senderName.setTextColor(user.getColorCode());
            if(message.getSender().equalsIgnoreCase(myId)) {
                senderName.setText("You");
            } else {
                senderName.setText(user.getName());
            }
            String url = "";
            if(message.getSharedFiles().size() > 0) {
                url = message.getSharedFiles().get(0).getPreviewUrl();
                if(url == null || url.length() == 0) {
                    url = message.getSharedFiles().get(0).getUrl();
                }
            }
            if(message.getMessageType() == MessageType.IMAGE) {
                messageview.setText("Photo");
                messageview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_image_24,0,0,0);
                Glide.with(getContext()).load(url).override(100,100).into(preview);
            } else if(message.getMessageType() == MessageType.VIDEO) {
                messageview.setText("Video");
                messageview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_videocam_24,0,0,0);
                Glide.with(getContext()).load(url).override(100,100).into(preview);
            } else if(message.getMessageType() == MessageType.AUDIO) {
                messageview.setText("Audio");
                messageview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_headset_14,0,0,0);
                preview.setVisibility(GONE);
            }  else if(message.getMessageType() == MessageType.GIF) {
                messageview.setText("GIF");
                messageview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_gif_24,0,0,0);
                Glide.with(getContext()).asBitmap().load(url).override(100,100).into(preview);
            }  else if(message.getMessageType() == MessageType.RECORDING) {
                messageview.setText("Recording");
                messageview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_mic_24,0,0,0);
                preview.setVisibility(GONE);
            }  else if(message.getMessageType() == MessageType.DOCUMENT) {
                messageview.setText("Document"+DOT_SEPRATOR+message.getSharedFiles().get(0).getName());
                if(message.getSharedFiles().get(0).getFileInfo() != null && message.getSharedFiles().get(0).getFileInfo().length() > 0) {
                    messageview.setText(messageview.getText()+DOT_SEPRATOR+message.getSharedFiles().get(0).getFileInfo());
                }
                preview.setVisibility(GONE);
                messageview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_insert_drive_file_24,0,0,0);
            }  else if(message.getMessageType() == MessageType.CONTACT_SINGLE) {

            }  else if(message.getMessageType() == MessageType.STICKER) {
                messageview.setText("Sticker");
                messageview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_emoji_emotions_24,0,0,0);
                Glide.with(getContext()).asBitmap().load(url).override(100,100).into(preview);
            }   else {
                messageview.setText(message.getMessage());
                preview.setVisibility(GONE);
                messageview.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            }
            bar.setBackgroundColor(user.getColorCode());
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                if(messageIds.contains(message.getMessageId())) {
                    int index = messageIds.indexOf(message.getMessageId());
                    if(mode == LISTVIEW) {
                        listView.smoothScrollToPosition(index);
                        View item = getViewByPosition(index,listView);
                        if(item != null) {
                            item.setBackgroundColor(Color.parseColor("#4003A9F4"));
                            new Handler().postDelayed(() -> item.setBackgroundColor(Color.TRANSPARENT), 1000);
                        }
                    } else {
                        recyclerView.scrollToPosition(index);
                        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(index);
                        if(holder != null) {
                            View item = holder.itemView;
                            item.setBackgroundColor(Color.parseColor("#4003A9F4"));
                            new Handler().postDelayed(() -> item.setBackgroundColor(Color.TRANSPARENT), 1000);
                        }
                    }
                }
            }
        });
        return view;
    }

    private View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public String getSize(long size) {
        String hrSize = null;
        double b = size;
        double k = size / 1024.0;
        double m = ((size / 1024.0) / 1024.0);
        double g = (((size / 1024.0) / 1024.0) / 1024.0);
        double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);
        DecimalFormat dec = new DecimalFormat("0.00");
        if (t > 1) {
            hrSize = dec.format(t).concat(" TB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat(" GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else if (k > 1) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" B");
        }
        return "Size : "+hrSize;
    }

    public String getDuration(long timeInMillis) {
        int hours = (int) ((timeInMillis / (1000 * 60 * 60)));
        int minutes = (int) ((timeInMillis / (1000 * 60)) % 60);
        int seconds = (int) ((timeInMillis / 1000) % 60);
        String time = "0";
        if (hours == 0) {
            time = minutes + ":" + seconds;
        } else {
            time = hours + ":" + minutes + ":" + seconds;
        }
        return time;
    }



}
