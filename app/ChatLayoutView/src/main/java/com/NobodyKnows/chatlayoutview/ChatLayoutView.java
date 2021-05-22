package com.NobodyKnows.chatlayoutview;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.NobodyKnows.chatlayoutview.Adapters.RecyclerViewAdapter;
import com.NobodyKnows.chatlayoutview.Constants.MessagePosition;
import com.NobodyKnows.chatlayoutview.Constants.MessageStatus;
import com.NobodyKnows.chatlayoutview.Constants.MessageType;
import com.NobodyKnows.chatlayoutview.DatabaseHelper.DatabaseHelper;
import com.NobodyKnows.chatlayoutview.Interfaces.ChatLayoutListener;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.MessageConfiguration;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.NobodyKnows.chatlayoutview.Services.Helper;
import com.capybaralabs.swipetoreply.ISwipeControllerActions;
import com.capybaralabs.swipetoreply.SwipeController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatLayoutView extends RelativeLayout {

    private LayoutInflater layoutInflater;
    private RelativeLayout root;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<Message> messages = new ArrayList<>();
    private String myUsername = "",roomId= "";
    private Helper helper;
    private Context context;
    private ArrayList<String> dates =new ArrayList<>();
    private Map<MessageType,String> downloadPaths = new HashMap<>();
    private ChatLayoutListener chatLayoutListener;
    private Boolean playSentAndReceivedSoundEffect = false,saveToDatabase = false;
    private MessageConfiguration leftMessageConfiguration;
    private MessageConfiguration rightMessageConfiguration;
    private int sentSoundEffect = R.raw.message_added;
    private int receivedSoundEffect = R.raw.message_received;
    private int blurRadius = 70;
    private DatabaseHelper databaseHelper;

    public ChatLayoutView(Context context) {
        super(context);
        init(null,0,context);
    }

    public ChatLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs,0,context);
    }

    public ChatLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs,defStyleAttr,context);
    }

    private void init(AttributeSet attrs, int defStyleAttr,Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = (RelativeLayout) layoutInflater.inflate(R.layout.chatlayout,this,true);
        recyclerView = root.findViewById(R.id.recyclerview);
        leftMessageConfiguration = new MessageConfiguration();
        rightMessageConfiguration = new MessageConfiguration();
        leftMessageConfiguration.setMessagePosition(MessagePosition.LEFT);
        leftMessageConfiguration.setBackgroundResource(R.drawable.left_message_background);
        rightMessageConfiguration.setMessagePosition(MessagePosition.RIGHT);
        rightMessageConfiguration.setBackgroundResource(R.drawable.right_message_background);
    }

    public void setup(String myUsername,String roomId,Boolean saveToDatabase,DatabaseHelper databaseHelper,ChatLayoutListener chatLayoutListener) {
        this.myUsername = myUsername;
        this.roomId = roomId;
        this.saveToDatabase = saveToDatabase;
        this.chatLayoutListener = chatLayoutListener;
        helper = new Helper(context);
        setupRecyclerView();
        if(saveToDatabase) {
            this.databaseHelper = databaseHelper;
            databaseHelper.createTable(roomId);
            loadPreviousChatMessages();
        }
    }

    private void loadPreviousChatMessages() {
        messages.clear();
        ArrayList<Message> list = databaseHelper.getAllMessages(roomId);
        for(Message message:list) {
            if(!helper.messageIdExists(message.getMessageId())) {
                checkForDate(message);
                notifyAdapter(true);
                checkForSeen(message,null);
            }
        }
        notifyAdapter(true);
    }

    public void addUser(User user) {
        this.helper.addUser(user);
    }

    private void setupRecyclerView() {
        recyclerView.setVisibility(VISIBLE);
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(),messages,helper.getUserMap(),downloadPaths,myUsername);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(false);
        layoutManager.setItemPrefetchEnabled(true);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setInitialPrefetchItemCount(20);
        layoutManager.setRecycleChildrenOnDetach(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerViewAdapter);;
        addSwipeRecyclerView();
    }

    private void addSwipeRecyclerView() {
        SwipeController controller = new SwipeController(getContext(), new ISwipeControllerActions() {
            @Override
            public void onSwipePerformed(int position) {
                chatLayoutListener.onSwipeToReply(messages.get(position),helper.getReplyMessageView(messages.get(position)));
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(controller);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void notifyAdapter(Boolean scrollToBottom) {
        recyclerViewAdapter.notifyItemInserted(recyclerViewAdapter.getItemCount() -1);
        if(recyclerViewAdapter.getItemCount() > 0 && scrollToBottom) {
            recyclerView.scrollToPosition(recyclerViewAdapter.getItemCount() - 1);
        }
    }

    private Message correctMessage(Message message) {
        if(message.getMessageType() == MessageType.GIF || message.getMessageType() == MessageType.STICKER) {
            message.setMessage("");
        }
        message.setRoomId(roomId);
        if(message.getMessageConfiguration() == null) {
            message.setMessageConfiguration(getMessageConfig(message));
        } else {
            if(myUsername.equalsIgnoreCase(message.getSender())) {
                message.getMessageConfiguration().setMessagePosition(MessagePosition.RIGHT);
            } else {
                message.getMessageConfiguration().setMessagePosition(MessagePosition.LEFT);
                if(playSentAndReceivedSoundEffect) {
                    MediaPlayer.create(getContext(),receivedSoundEffect).start();
                }
            }
        }
        return message;
    }

    public void addMessage(Message message) {
        if(!helper.messageIdExists(message.getMessageId())) {
            message = correctMessage(message);
            checkForDate(message);
            notifyAdapter(true);
            if(saveToDatabase) {
                saveToDatabaseTable(message);
            }
            checkForSeen(message,null);
        }
    }

    private void checkForSeen(Message message,Message oldMessage) {
        if(message.getSender().equals(myUsername)) {
            if(oldMessage != null) {
                if(message.getMessageStatus() == MessageStatus.SEEN && oldMessage.getMessageStatus() != MessageStatus.SEEN) {
                    chatLayoutListener.onMessageSeenConfirmed(message);
                }
            }
        } else {
            if(message.getMessageStatus() != MessageStatus.SEEN) {
                chatLayoutListener.onMessageSeen(message);
            }
        }
    }
    
    public void deleteMessage(Message message) {
        if(helper.messageIdExists(message.getMessageId())) {
            int index = helper.getMessageIdPositon(message.getMessageId());
            messages.remove(index);
            recyclerViewAdapter.notifyItemRemoved(index);
            if(saveToDatabase) {
                removeFromDatabase(message);
            }
        }
    }

    private void removeFromDatabase(Message message) {
        databaseHelper.deleteMessage(message.getMessageId(),roomId);
    }

    public void updateMessage(Message message) {
        if(helper.messageIdExists(message.getMessageId())) {
            int index = helper.getMessageIdPositon(message.getMessageId());
            Message messageOld = messages.get(index);
            if(messageOld.getMessageStatus() != message.getMessageStatus()) {
                messages.remove(index);
                messages.add(index,message);
                recyclerViewAdapter.notifyItemChanged(index);
                checkForSeen(message,messageOld);
                if(playSentAndReceivedSoundEffect && message.getMessageStatus() == MessageStatus.SENT && message.getSender().equalsIgnoreCase(myUsername)) {
                    MediaPlayer.create(getContext(),sentSoundEffect).start();
                }
                if(saveToDatabase) {
                    updateMessageToDatabase(message);   
                }
            }
        }
    }

    private void updateMessageToDatabase(Message message) {
        databaseHelper.updateMessage(message,roomId);
    }


    private void saveToDatabaseTable(Message message) {
        databaseHelper.insertInMessage(message,roomId);
    }


    public void setDownloadPath(MessageType messageType,String downloadFolder) {
        this.downloadPaths.put(messageType,downloadFolder);
    }

    private MessageConfiguration getMessageConfig(Message message) {
        if(message.getSender().equals(myUsername)) {
            return rightMessageConfiguration;
        } else {
            if(playSentAndReceivedSoundEffect && message.getMessageStatus() == MessageStatus.SENT) {
                MediaPlayer.create(getContext(),receivedSoundEffect).start();
            }
            return leftMessageConfiguration;
        }
    }

    private String getFormattedDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        return simpleDateFormat.format(date);
    }

    private void checkForDate(Message message) {
        String formattedText = getFormattedDate(message.getCreatedTimestamp());
        if(!dates.contains(formattedText)) {
            dates.add(formattedText);
            Message dateMessage = new Message();
            dateMessage.setMessageType(MessageType.DATE);
            if(message.getCreatedTimestamp() == new Date()) {
                formattedText = "Today";
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE,-1);
                if(message.getCreatedTimestamp() == calendar.getTime()) {
                    formattedText = "Yesterday";
                }
            }
            dateMessage.setMessageId("DATE_"+message.getCreatedTimestamp());
            dateMessage.setMessage(formattedText+"");
            messages.add(dateMessage);
            helper.addMessageId(dateMessage.getMessageId());
        }
        if(message.getIsRepliedMessage()) {
            if(message.getReplyMessageView() == null) {
                Message replyMessage = messages.get(helper.getMessageIdPositon(message.getRepliedMessageId()));
                message.setReplyMessageView(helper.getReplyMessageView(replyMessage));
            }
        }
        messages.add(message);
        helper.addMessageId(message.getMessageId());
    }

    public void reload() {
        messages.clear();
        recyclerViewAdapter.notifyDataSetChanged();
        helper.clearMessagedIds();
        if(saveToDatabase) {
            databaseHelper.createTable(roomId);
            loadPreviousChatMessages();
        }
    }
}
