package com.NobodyKnows.chatlayoutview.Services;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.NobodyKnows.chatlayoutview.Constants.MessageStatus;
import com.NobodyKnows.chatlayoutview.Constants.MessageType;
import com.NobodyKnows.chatlayoutview.Model.Contact;
import com.NobodyKnows.chatlayoutview.Model.ContactParceable;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.SharedFile;
import com.NobodyKnows.chatlayoutview.R;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LayoutService {
    private static SeekBar LseekBar;
    private static ImageView LplayPause;
    private static String LmessageId;
    private static MediaPlayer mediaPlayer;


    public static String getFormatedDate(String pattern, Date date) {
        if(date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String currentTime = sdf.format(date);
        return currentTime.toUpperCase();
    }

    public static void updateMessageStatus(Message message, TextView textView) {
        MessageStatus status = message.getMessageStatus();
        if(status == MessageStatus.SEEN && message.getSeenAt() != null) {
            textView.setText("Seen at "+getFormatedDate("hh:mm a",message.getSeenAt()));
        } else if(status == MessageStatus.RECEIVED && message.getReceivedAt() != null) {
            textView.setText("Received at "+getFormatedDate("hh:mm a",message.getReceivedAt()));
        }  else if(status == MessageStatus.SENT && message.getSentAt() != null) {
            textView.setText("Sent at "+getFormatedDate("hh:mm a",message.getSentAt()));
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
        return "Size : "+hrSize;
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

    public static Boolean canShowDownloadButton(MessageType messageType, ArrayList<SharedFile> sharedFiles) {
        String envPath = Environment.getExternalStorageDirectory().getPath();
        Boolean canShow = false;
//        for(SharedFile sharedFile:sharedFiles) {
//            if(!new File(envPath+downloadPaths.get(messageType)+"/"+sharedFile.getName()+"."+sharedFile.getExtension()).exists()) {
//                canShow = true;
//                break;
//            }
//        }
        return canShow;
    }

    public static String getFullFileUrl(String downloadPath,SharedFile sharedFile) {
        return Environment.getExternalStorageDirectory().getPath()+downloadPath+"/"+sharedFile.getName()+"."+sharedFile.getExtension();
    }

    public static ArrayList<ContactParceable> getParceableList(ArrayList<Contact> contacts) {
        ContactParceable contactParceable;
        ArrayList<ContactParceable> parceableArrayList = new ArrayList<>();
        for(Contact contact:contacts) {
            contactParceable = new ContactParceable(contact.getName(),contact.getContactNumbers());
            parceableArrayList.add(contactParceable);
        }
        return parceableArrayList;
    }

//    public static void downloadFiles(Context context,ArrayList<SharedFile> sharedFiles, MessageType messageType, ProgressBar progressButton,String messageId) {
//        downloadInfos.put(messageId,new ArrayList());
//        if(sharedFiles.size() == 1) {
//            downloadSingle(sharedFiles.get(0),messageType,progressButton,messageId,context);
//        } else {
//            downloadAll(sharedFiles,messageType,progressButton,messageId,context);
//        }
//    }

    private static void downloadAll(ArrayList<SharedFile> urls, MessageType messageType, ProgressBar progressBar, String messageId, Context context) {
//        String dirPath = downloadPaths.get(messageType);
//        PermissionListener permissionlistener = new PermissionListener() {
//            @Override
//            public void onPermissionGranted() {
//                String envPath = Environment.getExternalStorageDirectory().getPath();
//                if (!new File(envPath +dirPath).exists()) {
//                    new File(envPath + dirPath).mkdirs();
//                }
//                int totalDownloads = urls.size();
//                final int[] downloadCompleted = {0};
//                for(int i=0;i<urls.size();i++) {
//                    String partialUrl = envPath+dirPath+"/"+urls.get(i).getName()+"_PARTIALLY."+urls.get(i).getExtension();
//                    String realname = envPath+dirPath+"/"+urls.get(i).getName()+"."+urls.get(i).getExtension();
//                    if (new File(partialUrl).exists()) {
//                        new File(partialUrl).delete();
//                    }
//                    if(!new File(envPath+dirPath+"/"+urls.get(i).getName()+"."+urls.get(i).getExtension()).exists()) {
//                        DownloadInfo downloadInfo  = new DownloadInfo.Builder().setUrl(urls.get(i).getUrl()).setPath(partialUrl).build();
//                        int finalTotalDownloads = totalDownloads;
//                        int finalI = i;
//                        downloadInfo.setDownloadListener(new DownloadListener() {
//                            @Override
//                            public void onStart() {
//                            }
//
//                            @Override
//                            public void onWaited() {
//
//                            }
//
//                            @Override
//                            public void onPaused() {
//
//                            }
//
//                            @Override
//                            public void onDownloading(long progress, long size) {
//                            }
//
//                            @Override
//                            public void onRemoved() {
//
//                            }
//
//                            @Override
//                            public void onDownloadSuccess() {
//                                downloadCompleted[0]++;
//                                double progress = (((double)downloadCompleted[0]/(double)finalTotalDownloads)*100);
//                                if(progress > 0) {
//                                    progressBar.setIndeterminateMode(false);
//                                    progressBar.setProgress((float) progress);
//                                }
//                                if(progress == 100.0) {
//                                    progressBar.setVisibility(View.GONE);
//                                    downloadInfos.remove(messageId);
//                                }
//                                renameFile(partialUrl,realname);
//                            }
//
//                            @Override
//                            public void onDownloadFailed(DownloadException e) {
//                                downloadManager.remove(downloadInfo);
//                                downloadInfos.get(messageId).remove(finalI);
//                                downloadInfos.get(messageId).add(finalI,null);
//                            }
//                        });
//                        downloadInfos.get(messageId).add(i,downloadInfo);
//                        downloadManager.download(downloadInfo);
//                    } else {
//                        downloadCompleted[0]++;
//                        double progress = (((double)downloadCompleted[0]/(double)totalDownloads)*100);
//                        progressBar.setProgress((float) progress);
//                        downloadInfos.get(messageId).add(i,null);
//                        if(progress == 100.0) {
//                            progressBar.setVisibility(View.GONE);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onPermissionDenied(List<String> deniedPermissions) {
//            }
//        };
//        TedPermission.with(context)
//                .setPermissionListener(permissionlistener)
//                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
//                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .check();
    }

    private static void downloadSingle(SharedFile sharedFile, MessageType messageType, ProgressBar progressBar, String messageId, Context context) {
//        String dirPath = downloadPaths.get(messageType);
//        PermissionListener permissionlistener = new PermissionListener() {
//            @Override
//            public void onPermissionGranted() {
//                String envPath = Environment.getExternalStorageDirectory().getPath();
//                String partialUrl = envPath+dirPath+"/"+sharedFile.getName()+"_PARTIALLY."+sharedFile.getExtension();
//                if (new File(partialUrl).exists()) {
//                    new File(partialUrl).delete();
//                }
//                if (!new File(envPath +dirPath).exists()) {
//                    new File(envPath + dirPath).mkdirs();
//                }
//
//                if(!new File(envPath+dirPath+"/"+sharedFile.getName()+"."+sharedFile.getExtension()).exists()) {
//                    DownloadInfo downloadInfo  = new DownloadInfo.Builder().setUrl(sharedFile.getUrl()).setPath(partialUrl).build();
//                    downloadInfo.setDownloadListener(new DownloadListener() {
//                        @Override
//                        public void onStart() {
//                        }
//
//                        @Override
//                        public void onWaited() {
//
//                        }
//
//                        @Override
//                        public void onPaused() {
//
//                        }
//
//                        @Override
//                        public void onDownloading(long progress, long size) {
//                            int progressDone = calculateProgress(progress,size);
//                            if(progressDone > 0) {
//                                progressBar.setIndeterminateMode(false);
//                                progressBar.setProgress(progressDone);
//                            }
//                        }
//
//                        @Override
//                        public void onRemoved() {
//
//                        }
//
//                        @Override
//                        public void onDownloadSuccess() {
//                            renameFile(partialUrl,envPath+dirPath+"/"+sharedFile.getName()+"."+sharedFile.getExtension());
//                            progressBar.setVisibility(View.GONE);
//                            downloadInfos.remove(messageId);
//                        }
//
//                        @Override
//                        public void onDownloadFailed(DownloadException e) {
//                            downloadManager.remove(downloadInfo);
//                            downloadInfos.remove(messageId);
//                            progressBar.resetProgressButton();
//                        }
//                    });
//                    downloadInfos.get(messageId).add(downloadInfo);
//                    downloadManager.download(downloadInfo);
//                } else {
//                    progressBar.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onPermissionDenied(List<String> deniedPermissions) {
//            }
//        };
//        TedPermission.with(context)
//                .setPermissionListener(permissionlistener)
//                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
//                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .check();
    }

    private static void renameFile(String partialUrl, String newUrl) {
        File from = new File(partialUrl);
        File to = new File(newUrl);
        if(from.exists()) {
            from.renameTo(to);
        }
    }

    private static int calculateProgress(long progress, long size) {
        return (int) (((double) progress / (double) size) * 100);
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

}
