package com.nobodyknows.chatwithme.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.NobodyKnows.chatlayoutview.Model.Message;
import com.NobodyKnows.chatlayoutview.Model.User;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.MessageMaker;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import java.util.List;

import me.mutasem.slidetoanswer.SwipeToAnswerView;

import static com.nobodyknows.chatwithme.Activities.Dashboard.Dashboard.databaseHelper;

public class AudioCall extends AppCompatActivity {

    private String username;
    private Boolean making= false;
    private User user;
    private ImageView profile;
    private Call call;
    private SwipeToAnswerView answer,decline;
    private TextView title,name,number,time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_call);
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        username = getIntent().getStringExtra("username");
        making = getIntent().getBooleanExtra("making",false);
        user = databaseHelper.getUser(username);
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

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void updateTime(int seconds) {
        int p1 = seconds % 60;
        int p2 = seconds / 60;
        int p3 = p2 % 60;
        p2 = p2 / 60;
        time.setText(p2 + ":" + p3 + ":" + p1);
    }

    private void init() {
        call = MessageMaker.getCurrentCallRef();
        title = findViewById(R.id.title);
        name = findViewById(R.id.name);
        number = findViewById(R.id.number);
        time = findViewById(R.id.time);
        if(making) {
            title.setText("Outgoing Call");
        } else {
            title.setText("Incoming Call");
        }
        name.setText(user.getName());
        number.setText(user.getContactNumber());
        answer = findViewById(R.id.answer);
        decline = findViewById(R.id.decline);
        profile = findViewById(R.id.profile);
        MessageMaker.loadProfile(getApplicationContext(),user.getProfileUrl(),profile);
        if(making) {
            MessageMaker.audioCall(username);
        }
        MessageMaker.getCurrentCallRef().addCallListener(new CallListener() {
            @Override
            public void onCallProgressing(Call call) {
                updateTime(call.getDetails().getDuration());
            }

            @Override
            public void onCallEstablished(Call call) {
                changeView();
            }

            @Override
            public void onCallEnded(Call call) {
                MessageMaker.setCurrentCallRef(null);
            }

            @Override
            public void onShouldSendPushNotification(Call call, List<PushPair> list) {

            }
        });

        answer.setSlideListner(new SwipeToAnswerView.SlideListner() {
            @Override
            public void onSlideCompleted() {
                call.answer();
            }
        });

        decline.setSlideListner(new SwipeToAnswerView.SlideListner() {
            @Override
            public void onSlideCompleted() {
                call.hangup();
                MessageMaker.setCurrentCallRef(null);
                finish();
            }
        });
    }

    private void changeView() {

    }
}