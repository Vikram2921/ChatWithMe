package com.NobodyKnows.chatlayoutview.Services;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.NobodyKnows.chatlayoutview.Constants.MessageStatus;
import com.NobodyKnows.chatlayoutview.Constants.MessageType;
import com.NobodyKnows.chatlayoutview.Constants.UploadStatus;
import com.NobodyKnows.chatlayoutview.Interfaces.ChatLayoutListener;
import com.NobodyKnows.chatlayoutview.Interfaces.ProgressClickListener;
import com.NobodyKnows.chatlayoutview.Model.Contact;
import com.NobodyKnows.chatlayoutview.Model.ContactParceable;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.SharedFile;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.NobodyKnows.chatlayoutview.ProgressButton;
import com.NobodyKnows.chatlayoutview.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.ixuea.android.downloader.DownloadService;
import com.ixuea.android.downloader.callback.DownloadListener;
import com.ixuea.android.downloader.domain.DownloadInfo;
import com.ixuea.android.downloader.exception.DownloadException;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import io.github.ponnamkarthik.richlinkpreview.MetaData;
import io.github.ponnamkarthik.richlinkpreview.ResponseListener;
import io.github.ponnamkarthik.richlinkpreview.RichLinkView;
import io.github.ponnamkarthik.richlinkpreview.RichPreview;

import static com.NobodyKnows.chatlayoutview.ChatLayoutView.databaseHelper;
import static com.NobodyKnows.chatlayoutview.ChatLayoutView.downloadPaths;
import static com.NobodyKnows.chatlayoutview.ChatLayoutView.helper;
import static com.ixuea.android.downloader.DownloadService.downloadManager;

public class LayoutService {
    private static SeekBar LseekBar;
    private static ImageView LplayPause;
    private static String LmessageId;
    private static MediaPlayer mediaPlayer;
    private static Map<String,View> uploadView;
    private static Map<String,ArrayList<DownloadInfo>> downloadInfos;

    public static void initializeHelper(Context context) {
        uploadView = new HashMap<>();
        downloadManager = DownloadService.getDownloadManager(context);
        downloadInfos = new HashMap<>();
    }

    public static void addUploadView(String messageId,String roomid,View view) {
        uploadView.put(messageId+"]-]"+roomid,view);
    }

    public static View getUploadView(String messageId,String roomId) {
        return uploadView.get(messageId+"]-]"+roomId);
    }


    public static String getFormatedDate(String pattern, Date date) {
        if(date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String currentTime = sdf.format(date);
        return currentTime.toUpperCase();
    }

    public static String getMessageText(Message message) {
        if(message.getMessageType() == MessageType.CONTACT_SINGLE ) {
            return message.getContacts().size()+" Contact.";
        } else  if(message.getMessageType() == MessageType.CONTACT_MULTIPLE) {
            return message.getContacts().size()+" Contacts.";
        } else if(message.getMessageType() == MessageType.GIF) {
            return "GIF";
        } else if(message.getMessageType() == MessageType.STICKER) {
            return "Sticker";
        } else {
            return message.getMessage();
        }
    }

    public static void updateMessageStatus(Message message, TextView textView) {
        MessageStatus status = message.getMessageStatus();
        if(status == MessageStatus.SEEN && message.getSeenAt() != null) {
            textView.setText("Seen at "+getFormatedDate("hh:mm a",message.getSeenAt()));
        } else if(status == MessageStatus.RECEIVED && message.getReceivedAt() != null) {
            textView.setText("Received at "+getFormatedDate("hh:mm a",message.getReceivedAt()));
        }  else if(status == MessageStatus.SENT && message.getSentAt() != null) {
            textView.setText("Sent at "+getFormatedDate("hh:mm a",message.getSentAt()));
        } else if(status == MessageStatus.SENDING) {
            textView.setText("Sending");
        }
    }

    public static void loadGifAndSticker(Context context,String url,ImageView imageView) {
        Glide.with(context).load(url)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC)).into(imageView);
    }

    public static void updateReplyView(Message message,View view) {
        TextView sendernameReply = view.findViewById(R.id.sendernamereply);
        TextView messageTimeReply = view.findViewById(R.id.messagetimereply);
        TextView messageReply = view.findViewById(R.id.messageReply);
        if(message != null) {
            sendernameReply.setTextColor(helper.getUser(message.getSender()).getColorCode());
            sendernameReply.setText(helper.getUser(message.getSender()).getName());
            String day = "";
            if(LayoutService.getFormatedDate("dd-MM-yyyy",message.getSentAt()).equals(LayoutService.getFormatedDate("dd-MM-yyyy",new Date()))) {
                day = "Today";
            } else  {
                day = LayoutService.getFormatedDate("dd MMMM yyyy",message.getSentAt());
            }
            messageTimeReply.setText(LayoutService.getFormatedDate("hh:mm a",message.getSentAt())+", "+day);
            messageReply.setText(getMessageText(message));
        } else {
            sendernameReply.setVisibility(View.GONE);
            messageTimeReply.setVisibility(View.GONE);
            messageReply.setText("This message was deleted from this chat.");
        }
    }

    public static boolean containsURL(String content){
        String REGEX = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern p = Pattern.compile(REGEX,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(content);
        if(m.find()) {
            return true;
        }

        return false;
    }

    public static String getSize(double size) {
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
        return hrSize;
    }

    public static String getDuration(Double timeInMillis) {
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

    private static void renameFile(String partialUrl, String newUrl) {
        File from = new File(partialUrl);
        File to = new File(newUrl);
        if(from.exists()) {
            from.renameTo(to);
        }
    }


    public static int generateUserColorCode() {
        final Random mRandom = new Random(System.currentTimeMillis());
        final int baseColor = Color.WHITE;
        final int baseRed = Color.red(baseColor);
        final int baseGreen = Color.green(baseColor);
        final int baseBlue = Color.blue(baseColor);
        final int red = (baseRed + mRandom.nextInt(256)) / 2;
        final int green = (baseGreen + mRandom.nextInt(256)) / 2;
        final int blue = (baseBlue + mRandom.nextInt(256)) / 2;
        return Color.rgb(red, green, blue);
    }

    public static String getConvertedDate(Date date) {
        if(date != null) {
            return date.toString();
        }
        return "";
    }

    public static Date getConvertedDate(String date) {
        if(date.length() > 0) {
            Date newdate = new Date(date);
            return newdate;
        }
        return null;
    }

    public static boolean convertBoolean(String value) {
        if(value.equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public static String convertBoolean(Boolean value) {
        if(value) {
            return "true";
        }
        return "false";
    }

    public static void setUpSenderName(View view, User user,Message message, ChatLayoutListener chatLayoutListener) {
        TextView textView = view.findViewById(R.id.sendername);
        if(message.getRoomId().startsWith("G")) {
            textView.setText(user.getName());
            textView.setTextColor(user.getColorCode());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chatLayoutListener.onSenderNameClicked(user, message);
                }
            });
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    public static void updateLinkView(String message, View view) {
        RichLinkView richLinkView = view.findViewById(R.id.richlinkview);
        List<String> urls = extractUrls(message);
        String link = "";
        if(urls != null && urls.size() > 0) {
            link = urls.get(0);
        }
        if(link != null && link.length() > 0) {
            MetaData metaData = databaseHelper.getLink(link);
            richLinkView.setDefaultClickListener(false);
            if(metaData == null) {
                String finalLink = link;
                RichPreview richPreview = new RichPreview(new ResponseListener() {
                    @Override
                    public void onData(MetaData metaData) {
                        metaData.setUrl(finalLink);
                        databaseHelper.insertInLinks(metaData);
                        richLinkView.setLinkFromMeta(metaData);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
                richPreview.getPreview(link);
            } else {
                richLinkView.setLinkFromMeta(metaData);
            }
        }
    }

    /**
     * Returns a list with all links contained in the input
     */
    private static List<String> extractUrls(String text) {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find())
        {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }

    private static String getLocalPathForSharedFile(SharedFile sharedFile,String sender,String myid,MessageType messageType) {
        if(sender.equals(myid)) {
            return sharedFile.getLocalPath();
        } else {
            String envPath = Environment.getExternalStorageDirectory().getPath();
            String dirPath = downloadPaths.get(messageType);
            return envPath+dirPath+"/"+sharedFile.getFileId()+"."+sharedFile.getExtension();
        }
    }

    private static String getLocalPathForSharedFileForDownload(SharedFile sharedFile,String divider) {
        return sharedFile.getFileId()+divider+"."+sharedFile.getExtension();
    }

    public static void loadMediaViewSingle(Context context,Message message,ChatLayoutListener chatLayoutListener,View view,String mynumber) {
        RoundedImageView roundedImageView = view.findViewById(R.id.image);
        ProgressButton progressButton = view.findViewById(R.id.progressbutton);
        TextView info = view.findViewById(R.id.info);
        info.setText(getSize(message.getSharedFiles().get(0).getSize()));
        if(message.getSender().equals(mynumber) && message.getMessageStatus() == MessageStatus.SENDING) {
            progressButton.initalize();
            progressButton.setUploadType();
            Glide.with(context).load(message.getSharedFiles().get(0).getLocalPath()).into(roundedImageView);
            if(message.getUploadStatus() == UploadStatus.NOT_STARTED) {
                chatLayoutListener.onUpload(message,progressButton);
                addUploadView(message.getMessageId(),message.getRoomId(),view);
                progressButton.setProgress(0);
            } else {
                if(message.getUploadStatus() == UploadStatus.FAILED) {
                    progressButton.setLabel("Retry");
                }
            }
            progressButton.setProgressClickListener(new ProgressClickListener() {
                @Override
                public void onStart() {
                    chatLayoutListener.onUpload(message,progressButton);
                    addUploadView(message.getMessageId(),message.getRoomId(),view);
                }

                @Override
                public void onCancel() {
                    databaseHelper.updateMessageUploadStatus(message.getRoomId(),message.getMessageId(),UploadStatus.CANCELED);
                    chatLayoutListener.onUploadCanceled(message,progressButton);
                }
            });
        } else {
            String path = getLocalPathForSharedFile(message.getSharedFiles().get(0),message.getSender(),mynumber,message.getMessageType());
            File file = new File(path);
            if(file.exists()) {
                if(message.getMessageType() == MessageType.VIDEO) {
                    view.findViewById(R.id.playicon).setVisibility(View.VISIBLE);
                }
                progressButton.setVisibility(View.GONE);
                loadPreview(context,roundedImageView,message.getSharedFiles().get(0),true);
            } else {
                if(message.getSender().equals(mynumber)) {
                    progressButton.setVisibility(View.GONE);
                } else {
                    progressButton.initalize();
                    progressButton.setDownloadType();
                    loadPreview(context,roundedImageView,message.getSharedFiles().get(0),false);
                    progressButton.setProgressClickListener(new ProgressClickListener() {
                        @Override
                        public void onStart() {
                            downloadFile(context,progressButton,message,mynumber);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                }
            }
        }
    }

    public static void loadMediaViewMultiple(Context context,Message message,ChatLayoutListener chatLayoutListener,View view,String mynumber) {
        RoundedImageView image1 = view.findViewById(R.id.image1);
        RoundedImageView image2 = view.findViewById(R.id.image2);
        RoundedImageView image3 = view.findViewById(R.id.image3);
        RoundedImageView image4 = view.findViewById(R.id.image4);
        TextView moretext = view.findViewById(R.id.moretext);
        TextView info1 = view.findViewById(R.id.info1);
        TextView info2 = view.findViewById(R.id.info2);
        TextView info3 = view.findViewById(R.id.info3);
        TextView info4 = view.findViewById(R.id.info4);
        info1.setText(getSize(message.getSharedFiles().get(0).getSize()));
        info2.setText(getSize(message.getSharedFiles().get(1).getSize()));
        info3.setText(getSize(message.getSharedFiles().get(2).getSize()));
        info4.setText(getSize(message.getSharedFiles().get(3).getSize()));
        ProgressButton progressButton = view.findViewById(R.id.progressbutton);
        if(message.getSharedFiles().size() > 4) {
            moretext.setVisibility(View.VISIBLE);
            moretext.setText("+ "+(message.getSharedFiles().size() - 3));
        }
        if(message.getSender().equals(mynumber) && message.getMessageStatus() == MessageStatus.SENDING) {
            progressButton.initalize();
            progressButton.setUploadType();
            Glide.with(context).load(message.getSharedFiles().get(0).getLocalPath()).into(image1);
            Glide.with(context).load(message.getSharedFiles().get(1).getLocalPath()).into(image2);
            Glide.with(context).load(message.getSharedFiles().get(2).getLocalPath()).into(image3);
            Glide.with(context).load(message.getSharedFiles().get(3).getLocalPath()).into(image4);
            if(message.getUploadStatus() == UploadStatus.NOT_STARTED) {
                chatLayoutListener.onUpload(message,progressButton);
                addUploadView(message.getMessageId(),message.getRoomId(),view);
                progressButton.setProgress(0);
            } else {
                if(message.getUploadStatus() == UploadStatus.FAILED) {
                    progressButton.setLabel("Retry");
                }
            }
            progressButton.setProgressClickListener(new ProgressClickListener() {
                @Override
                public void onStart() {
                    chatLayoutListener.onUpload(message,progressButton);
                    addUploadView(message.getMessageId(),message.getRoomId(),view);
                }

                @Override
                public void onCancel() {
                    databaseHelper.updateMessageUploadStatus(message.getRoomId(),message.getMessageId(),UploadStatus.CANCELED);
                    chatLayoutListener.onUploadCanceled(message,progressButton);
                }
            });
        } else {
            File file = null;
            int i=0;
            progressButton.initalize();
            progressButton.setProgressClickListener(new ProgressClickListener() {
                @Override
                public void onStart() {
                    downloadFile(context,progressButton,message,mynumber);
                }

                @Override
                public void onCancel() {

                }
            });
            for(SharedFile sharedFile:message.getSharedFiles()) {
                file = new File(getLocalPathForSharedFile(sharedFile,message.getSender(),mynumber,message.getMessageType()));
                if(file.exists()) {
                    progressButton.setVisibility(View.GONE);
                    if(i==0) {
                        loadPreview(context,image1,sharedFile,true);
                    } else if(i==1) {
                        loadPreview(context,image2,sharedFile,true);
                    } else if(i==2) {
                        loadPreview(context,image3,sharedFile,true);
                    } else if(i==3) {
                        loadPreview(context,image4,sharedFile,true);
                    }
                } else {
                    if(message.getSender().equals(mynumber)) {
                        progressButton.setVisibility(View.GONE);
                    } else {
                        progressButton.setDownloadType();
                        if(i==0) {
                            loadPreview(context,image1,sharedFile,false);
                        } else if(i==1) {
                            loadPreview(context,image2,sharedFile,false);
                        } else if(i==2) {
                            loadPreview(context,image3,sharedFile,false);
                        } else if(i==3) {
                            loadPreview(context,image4,sharedFile,false);
                        }
                        progressButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                downloadFile(context,progressButton,message,mynumber);
                            }
                        });
                    }
                }
                i++;
            }

        }
    }

    private static void loadPreview(Context context,RoundedImageView roundedImageView,SharedFile sharedFile,Boolean isExist) {
        if(isExist) {
            Glide.with(context).load(sharedFile.getLocalPath()).into(roundedImageView);
        } else {
            Glide.with(context).load(sharedFile.getPreviewUrl()).into(roundedImageView);
        }
    }

    public static void downloadFile(Context context,ProgressButton progressBar,Message message,String mynumber) {
        downloadInfos.put(message.getMessageId(),new ArrayList<>());
        if(message.getSharedFiles().size() == 1) {
            downloadSingle(context, progressBar, message, mynumber);
        } else {
            downloadFileMultiple(context, progressBar, message, mynumber);
        }
    }

    private static void downloadSingle(Context context,ProgressButton progressBar,Message message,String mynumber) {
        String dirPath = downloadPaths.get(message.getMessageType());
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                SharedFile sharedFile = message.getSharedFiles().get(0);
                String envPath = Environment.getExternalStorageDirectory().getPath();
                String partialUrl = envPath+dirPath+"/"+getLocalPathForSharedFileForDownload(sharedFile,"_PARTIALLY");
                if (new File(partialUrl).exists()) {
                    new File(partialUrl).delete();
                }
                if (!new File(envPath +dirPath).exists()) {
                    new File(envPath + dirPath).mkdirs();
                }

                if(!new File(envPath+dirPath+"/"+getLocalPathForSharedFileForDownload(sharedFile,"")).exists()) {
                    DownloadInfo downloadInfo  = new DownloadInfo.Builder().setUrl(sharedFile.getUrl()).setPath(partialUrl).build();
                    downloadInfo.setDownloadListener(new DownloadListener() {
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onWaited() {

                        }

                        @Override
                        public void onPaused() {

                        }

                        @Override
                        public void onDownloading(long progress, long size) {
                            int progressDone = calculateProgress(progress,size);
                            if(progressDone > 0) {
                                progressBar.setIndeterminateMode(false);
                                progressBar.setProgress(progressDone);
                            }
                        }

                        @Override
                        public void onRemoved() {

                        }

                        @Override
                        public void onDownloadSuccess() {
                            renameFile(partialUrl,envPath+dirPath+"/"+getLocalPathForSharedFileForDownload(sharedFile,""));
                            progressBar.setVisibility(View.GONE);
                            downloadInfos.remove(message.getMessageId());
                        }

                        @Override
                        public void onDownloadFailed(DownloadException e) {
                            downloadManager.remove(downloadInfo);
                            downloadInfos.remove(message.getMessageId());
                            progressBar.resetProgressButton();
                        }
                    });
                    downloadInfos.get(message.getMessageId()).add(downloadInfo);
                    downloadManager.download(downloadInfo);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
            }
        };
        TedPermission.with(context)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private static void downloadFileMultiple(Context context,ProgressButton progressBar,Message message,String mynumber) {
        String dirPath = downloadPaths.get(message.getMessageType());
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                downloadInfos.put(message.getMessageId(),new ArrayList<>());
                String envPath = Environment.getExternalStorageDirectory().getPath();
                if (!new File(envPath +dirPath).exists()) {
                    new File(envPath + dirPath).mkdirs();
                }
                int totalDownloads = message.getSharedFiles().size();
                List<SharedFile> urls = message.getSharedFiles();
                final int[] downloadCompleted = {0};
                for(int i=0;i<urls.size();i++) {
                    String partialUrl = envPath+dirPath+"/"+getLocalPathForSharedFileForDownload(urls.get(i),"_PARTIALLY");
                    String realname = envPath+dirPath+"/"+getLocalPathForSharedFileForDownload(urls.get(i),"");
                    if (new File(partialUrl).exists()) {
                        new File(partialUrl).delete();
                    }
                    if(!new File(envPath+dirPath+"/"+urls.get(i).getName()+"."+urls.get(i).getExtension()).exists()) {
                        DownloadInfo downloadInfo  = new DownloadInfo.Builder().setUrl(urls.get(i).getUrl()).setPath(partialUrl).build();
                        int finalTotalDownloads = totalDownloads;
                        int finalI = i;
                        downloadInfo.setDownloadListener(new DownloadListener() {
                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onWaited() {

                            }

                            @Override
                            public void onPaused() {

                            }

                            @Override
                            public void onDownloading(long progress, long size) {
                            }

                            @Override
                            public void onRemoved() {

                            }

                            @Override
                            public void onDownloadSuccess() {
                                downloadCompleted[0]++;
                                double progress = (((double)downloadCompleted[0]/(double)finalTotalDownloads)*100);
                                if(progress > 0) {
                                    progressBar.setIndeterminateMode(false);
                                    progressBar.setProgress((float) progress);
                                }
                                if(progress == 100.0) {
                                    progressBar.setVisibility(View.GONE);
                                    downloadInfos.remove(message.getMessageId());
                                }
                                renameFile(partialUrl,realname);
                            }


                            @Override
                            public void onDownloadFailed(DownloadException e) {
                                downloadManager.remove(downloadInfo);
                                downloadInfos.get(message.getMessageId()).remove(finalI);
                                downloadInfos.get(message.getMessageId()).add(finalI,null);
                            }
                        });
                        downloadInfos.get(message.getMessageId()).add(i,downloadInfo);
                        downloadManager.download(downloadInfo);
                    } else {
                        downloadCompleted[0]++;
                        double progress = (((double)downloadCompleted[0]/(double)totalDownloads)*100);
                        progressBar.setProgress((float) progress);
                        downloadInfos.get(message.getMessageId()).add(i,null);
                        if(progress == 100.0) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
            }
        };
        TedPermission.with(context)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    public static void loadDocumentViewSingle(Context context,Message message,ChatLayoutListener chatLayoutListener,View view,String mynumber) {
        TextView filename = view.findViewById(R.id.filename);
        TextView size = view.findViewById(R.id.size);
        ProgressButton progressButton = view.findViewById(R.id.progressbutton);
        SharedFile sharedFile = message.getSharedFiles().get(0);
        if(sharedFile != null) {
            filename.setText(sharedFile.getName());
            size.setText(LayoutService.getSize(sharedFile.getSize()));
            if(message.getSender().equals(mynumber) && message.getMessageStatus() == MessageStatus.SENDING) {
                progressButton.initalize();
                progressButton.setUploadType();
                if(message.getUploadStatus() == UploadStatus.NOT_STARTED) {
                    chatLayoutListener.onUpload(message,progressButton);
                    addUploadView(message.getMessageId(),message.getRoomId(),view);
                    progressButton.setProgress(0);
                } else {
                    if(message.getUploadStatus() == UploadStatus.FAILED) {
                        progressButton.setLabel("Retry");
                    }
                }
                progressButton.setProgressClickListener(new ProgressClickListener() {
                    @Override
                    public void onStart() {
                        chatLayoutListener.onUpload(message,progressButton);
                        addUploadView(message.getMessageId(),message.getRoomId(),view);
                    }

                    @Override
                    public void onCancel() {
                        databaseHelper.updateMessageUploadStatus(message.getRoomId(),message.getMessageId(),UploadStatus.CANCELED);
                        chatLayoutListener.onUploadCanceled(message,progressButton);
                    }
                });
            } else {
                String path = getLocalPathForSharedFile(message.getSharedFiles().get(0),message.getSender(),mynumber,message.getMessageType());
                File file = new File(path);
                if(file.exists()) {
                    progressButton.setVisibility(View.GONE);
                } else {
                    if(message.getSender().equals(mynumber)) {
                        progressButton.setVisibility(View.GONE);
                    } else {
                        progressButton.initalize();
                        progressButton.setDownloadType();
                        progressButton.setProgressClickListener(new ProgressClickListener() {
                            @Override
                            public void onStart() {
                                downloadFile(context,progressButton,message,mynumber);
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                    }
                }
            }
        }
    }

    public static void loadAudioViewSingle(Context context, Message message, ChatLayoutListener chatLayoutListener, View view, String mynumber) {
        TextView filename = view.findViewById(R.id.filename);
        TextView size = view.findViewById(R.id.size);
        ProgressButton progressButton = view.findViewById(R.id.progressbutton);
        ImageView playAudio  = view.findViewById(R.id.playicon);
        SharedFile sharedFile = message.getSharedFiles().get(0);
        if(sharedFile != null) {
            filename.setText(sharedFile.getName());
            size.setText(LayoutService.getSize(sharedFile.getSize()));
            if(message.getSender().equals(mynumber) && message.getMessageStatus() == MessageStatus.SENDING) {
                progressButton.initalize();
                progressButton.setUploadType();
                playAudio.setVisibility(View.VISIBLE);
                playAudio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chatLayoutListener.onPlayAudio(sharedFile);
                    }
                });
                if(message.getUploadStatus() == UploadStatus.NOT_STARTED) {
                    chatLayoutListener.onUpload(message,progressButton);
                    addUploadView(message.getMessageId(),message.getRoomId(),view);
                    progressButton.setProgress(0);
                } else {
                    if(message.getUploadStatus() == UploadStatus.FAILED) {
                        progressButton.setLabel("Retry");
                    }
                }
                progressButton.setProgressClickListener(new ProgressClickListener() {
                    @Override
                    public void onStart() {
                        chatLayoutListener.onUpload(message,progressButton);
                        addUploadView(message.getMessageId(),message.getRoomId(),view);
                    }

                    @Override
                    public void onCancel() {
                        databaseHelper.updateMessageUploadStatus(message.getRoomId(),message.getMessageId(),UploadStatus.CANCELED);
                        chatLayoutListener.onUploadCanceled(message,progressButton);
                    }
                });
            } else {
                String path = getLocalPathForSharedFile(message.getSharedFiles().get(0),message.getSender(),mynumber,message.getMessageType());
                File file = new File(path);
                if(file.exists()) {
                    progressButton.setVisibility(View.GONE);
                    playAudio.setVisibility(View.VISIBLE);
                    playAudio.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chatLayoutListener.onPlayAudio(sharedFile);
                        }
                    });
                } else {
                    if(message.getSender().equals(mynumber)) {
                        progressButton.setVisibility(View.GONE);
                        playAudio.setVisibility(View.VISIBLE);
                        playAudio.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                chatLayoutListener.onPlayAudio(sharedFile);
                            }
                        });
                    } else {
                        progressButton.initalize();
                        progressButton.setDownloadType();
                        playAudio.setVisibility(View.GONE);
                        progressButton.setProgressClickListener(new ProgressClickListener() {
                            @Override
                            public void onStart() {
                                downloadFile(context,progressButton,message,mynumber);
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                    }
                }
            }
        }
    }

    private static int calculateProgress(long progress, long size) {
        return (int) (((double) progress / (double) size) * 100);
    }
}
