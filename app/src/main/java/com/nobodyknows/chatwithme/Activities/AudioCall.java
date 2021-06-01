package com.nobodyknows.chatwithme.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.MessageMaker;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;

import java.util.List;

import me.mutasem.slidetoanswer.SwipeToAnswerView;

import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.sinchClient;

public class AudioCall extends AppCompatActivity {

    private String username;
    private Boolean making= false,isVideoCall = false;
    private User user;
    private ImageView profile;
    private SwipeToAnswerView answer,decline;
    private TextView name,number,time,namevideo;
    private Button hangup;
    private ImageView mute,speaker,video,camswitch;
    private RelativeLayout bigView,smallview;
    private ConstraintLayout whileincall,audioLayout,videolayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_call);
        getSupportActionBar().hide();
        username = getIntent().getStringExtra("username");
        making = getIntent().getBooleanExtra("making",false);
        isVideoCall = getIntent().getBooleanExtra("video",false);
        user = MessageMaker.getDatabaseHelper().getUser(username);
        if(isVideoCall) {
            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    init();
                }

                @Override
                public void onPermissionDenied(List<String> deniedPermissions) {
                }
            };
            TedPermission.with(getApplicationContext())
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,Manifest.permission.MODIFY_AUDIO_SETTINGS,Manifest.permission.READ_PHONE_STATE)
                    .check();
        } else {
            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    init();
                }

                @Override
                public void onPermissionDenied(List<String> deniedPermissions) {
                }
            };
            TedPermission.with(getApplicationContext())
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.RECORD_AUDIO,Manifest.permission.MODIFY_AUDIO_SETTINGS,Manifest.permission.READ_PHONE_STATE)
                    .check();
        }
    }




    private void initVideoCall() {
        video.setVisibility(View.GONE);
        videolayout.setVisibility(View.GONE);
        audioLayout.setVisibility(View.VISIBLE);
        if(making) {
            MessageMaker.videoCall(username);
            MessageMaker.playRingSound(getApplicationContext());
        }
        MessageMaker.getCurrentCallRef().addCallListener(new VideoCallListener() {
            @Override
            public void onVideoTrackAdded(Call call) {
                VideoController vc = sinchClient.getVideoController();
                View myPreview = vc.getLocalView();
                View remoteView = vc.getRemoteView();
                MessageMaker.setMyVideoView(myPreview);
                MessageMaker.setRemoteVideoView(remoteView);
                if(myPreview.getParent() != null) {
                    ((ViewGroup)myPreview.getParent()).removeView(myPreview);
                }
                if(remoteView.getParent() != null) {
                    ((ViewGroup)remoteView.getParent()).removeView(remoteView);
                }
                bigView.addView(remoteView);
                smallview.addView(myPreview);
                video.setVisibility(View.VISIBLE);
                audioLayout.setVisibility(View.GONE);
                videolayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onVideoTrackPaused(Call call) {

            }

            @Override
            public void onVideoTrackResumed(Call call) {

            }

            @Override
            public void onCallProgressing(Call call) {
                if(making) {
                    time.setText("Ringing");
                }
            }

            @Override
            public void onCallEstablished(Call call) {
                if(MessageMaker.getIsCallStarted()) {
                    MessageMaker.stopRingtone();
                    MessageMaker.setIsCallStarted(true);
                    changeView();
                }
            }

            @Override
            public void onCallEnded(Call call) {
                MessageMaker.updateCallInfo(call,true);
                MessageMaker.setCurrentCallRef(null);
                finish();
            }

            @Override
            public void onShouldSendPushNotification(Call call, List<PushPair> list) {

            }
        });
        camswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sinchClient.getVideoController().toggleCaptureDevicePosition();
            }
        });
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVideo();
            }
        });
        smallview.setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {

            }

            @Override
            public void onDoubleClick(View view) {
//                smallview.removeAllViews();
//                bigView.removeAllViews();
//                if(MessageMaker.getIsVideoViewSwitched()) {
//                    smallview.addView(MessageMaker.getRemoteVideoView());
//                    bigView.addView(MessageMaker.getMyVideoView());
//                    namevideo.setText(user.getName());
//                    MessageMaker.setIsVideoViewSwitched(false);
//                } else {
//                    smallview.addView(MessageMaker.getMyVideoView());
//                    bigView.addView(MessageMaker.getRemoteVideoView());
//                    MessageMaker.setIsVideoViewSwitched(true);
//                    namevideo.setText("You");
//                }
            }
        }));
    }

    private void initAudioCall() {
        video.setVisibility(View.GONE);
        videolayout.setVisibility(View.GONE);
        audioLayout.setVisibility(View.VISIBLE);
        if(making) {
            MessageMaker.audioCall(username);
            MessageMaker.playRingSound(getApplicationContext());
        }
        MessageMaker.getCurrentCallRef().addCallListener(new CallListener() {
            @Override
            public void onCallProgressing(Call call) {
                if(making) {
                    time.setText("Ringing");
                }
            }


            @Override
            public void onCallEstablished(Call call) {
                if(MessageMaker.getIsCallStarted()) {
                    MessageMaker.stopRingtone();
                    MessageMaker.setIsCallStarted(true);
                    changeView();
                }
                time.setText(MessageMaker.getFullTimeFromSeconds(call.getDetails().getDuration()));
            }

            @Override
            public void onCallEnded(Call call) {
                MessageMaker.updateCallInfo(call,true);
                MessageMaker.setCurrentCallRef(null);
                finish();
            }

            @Override
            public void onShouldSendPushNotification(Call call, List<PushPair> list) {

            }
        });

    }

    private void init() {
        whileincall = findViewById(R.id.whilincall);
        audioLayout = findViewById(R.id.audiocalllayout);
        videolayout = findViewById(R.id.videocalllayout);
        name = findViewById(R.id.name);
        number = findViewById(R.id.number);
        namevideo = findViewById(R.id.username);
        mute = findViewById(R.id.mic);
        speaker = findViewById(R.id.speaker);
        video = findViewById(R.id.video);
        hangup = findViewById(R.id.hangup);
        time = findViewById(R.id.time);
        answer = findViewById(R.id.answer);
        decline = findViewById(R.id.decline);
        camswitch = findViewById(R.id.cameraswitch);
        profile = findViewById(R.id.profile);
        namevideo = findViewById(R.id.username);
        bigView = findViewById(R.id.bigview);
        smallview = findViewById(R.id.smallview);

        name.setText(user.getName());
        namevideo.setText(user.getName());
        number.setText(user.getContactNumber());
        MessageMaker.loadProfile(getApplicationContext(),user.getProfileUrl(),profile);
        if(making) {
            time.setText("Calling");
            whileincall.setVisibility(View.VISIBLE);
            decline.setVisibility(View.GONE);
            answer.setVisibility(View.GONE);
        } else {
            time.setText("00:00");
            whileincall.setVisibility(View.GONE);
            decline.setVisibility(View.VISIBLE);
            answer.setVisibility(View.VISIBLE);
        }


        hangup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageMaker.hangup();
                finish();
            }
        });
        answer.setSlideListner(new SwipeToAnswerView.SlideListner() {
            @Override
            public void onSlideCompleted() {
                MessageMaker.stopRingtone();
                MessageMaker.answer();
                changeView();
            }
        });
        decline.setSlideListner(new SwipeToAnswerView.SlideListner() {
            @Override
            public void onSlideCompleted() {
                MessageMaker.hangup();
                finish();
            }
        });
        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MessageMaker.getIsCallMuted()) {
                    sinchClient.getAudioController().mute();
                    mute.setImageResource(R.drawable.ic_baseline_mic_off_24);
                    MessageMaker.setIsCallMuted(true);
                } else {
                    sinchClient.getAudioController().unmute();
                    mute.setImageResource(R.drawable.ic_baseline_mic_on_24);
                    MessageMaker.setIsCallMuted(false);
                }
            }
        });

        speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MessageMaker.getIsOnSpeaker()) {
                    sinchClient.getAudioController().disableSpeaker();
                    speaker.setImageResource(R.drawable.speaker_off);
                    MessageMaker.setIsOnSpeaker(false);
                } else {
                    sinchClient.getAudioController().enableSpeaker();
                    speaker.setImageResource(R.drawable.speaker_on);
                    MessageMaker.setIsOnSpeaker(true);
                }
            }
        });

        if(isVideoCall) {
            initVideoCall();
        } else {
            initAudioCall();
        }
    }

    private void startVideo() {
        if(MessageMaker.getIsVideoOn()) {
            MessageMaker.getCurrentCallRef().pauseVideo();
            video.setImageResource(R.drawable.video_off);
            MessageMaker.setIsVideoOn(false);
        } else {
            MessageMaker.getCurrentCallRef().resumeVideo();
            video.setImageResource(R.drawable.video);
            MessageMaker.setIsVideoOn(true);
        }
    }

    private void changeView() {
        mute.setEnabled(true);
        speaker.setEnabled(true);
        video.setEnabled(true);
        whileincall.setVisibility(View.VISIBLE);
        answer.setVisibility(View.GONE);
        decline.setVisibility(View.GONE);
    }
}