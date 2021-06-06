package com.nobodyknows.chatwithme.Activities.Dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.NobodyKnows.chatlayoutview.Services.LayoutService;
import com.bumptech.glide.Glide;
import com.giphy.sdk.ui.Giphy;
import com.github.tamir7.contacts.Contacts;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.nobodyknows.chatwithme.Activities.SearchFreinds;
import com.nobodyknows.chatwithme.Fragments.CallsFragment;
import com.nobodyknows.chatwithme.Fragments.ChatFragment;
import com.nobodyknows.chatwithme.Fragments.FreindsFragment;
import com.nobodyknows.chatwithme.MainActivity;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.MessageMaker;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.video.VideoScalingType;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.BluetoothCallback;
import me.aflak.bluetooth.interfaces.DiscoveryCallback;

public class Dashboard extends AppCompatActivity {


    private CircleImageView profile;
    private TextView name,status;
    private BottomNavigationView bottomNavigationView;
    private View actionbarview;
    private Bluetooth bluetooth;
    private String sinchApplicationKey = "4f4a2900-a600-45ef-9e35-d2d20b6b2e93";
    private String sinchApplicationSecret = "ML6bBC1ri0GvMuNfI93sWw==";
    public static SinchClient sinchClient;
    public static String GIPHY_KEY = "xEH0o5bSCOBYPjXj7tjqmV2YuTML8FjN";
    public static CallClient callClient;
    private SinchClientListener sinchClientListener;
    private CallClientListener callClientListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.dashboard_toolbar_view);
        actionbarview = getSupportActionBar().getCustomView();
        getSupportActionBar().setElevation(0);
        MessageMaker.setContext(getApplicationContext());
        MessageMaker.initializeDatabase();
        MessageMaker.setMyNumber(MessageMaker.getFromSharedPrefrences(getApplicationContext(),"number"));
        MessageMaker.setMySecurityKey(MessageMaker.getFromSharedPrefrences(getApplicationContext(),"securityKey"));
        updateOnlineStatus("Online",false);
        LayoutService.initializeHelper(getApplicationContext());
        Giphy.INSTANCE.configure(getApplicationContext(),GIPHY_KEY,true,null);
        Contacts.initialize(getApplicationContext());
        EmojiManager.install(new GoogleEmojiProvider());
        init();
        setupSinch();
        //  setupBlueTooth();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(MessageMaker.getContext() == null) {
            MessageMaker.setContext(getApplicationContext());
            EmojiManager.install(new GoogleEmojiProvider());
        }
        if(sinchClient == null) {
            setupSinch();
        }
        loadInfo();
//        bluetooth.onStart();
//        if(bluetooth.isEnabled()){
//            startScanning();
//        } else {
//            bluetooth.enable();
//        }
    }

    private void updateOnlineStatus(String status,Boolean canFinish) {
        Map<String,Object> update = new HashMap<>();
        update.put("currentStatus",status);
        update.put("lastOnline",new Date());
        MessageMaker.getFirebaseService().readFromFireStore("Users").document(MessageMaker.getMyNumber()).collection("AccountInfo").document("PersonalInfo").update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(canFinish) {
                    finish();
                }
            }
        });
    }

    private void setupBlueTooth() {
        bluetooth = new Bluetooth(this);
        bluetooth.setCallbackOnUI(this);
        bluetooth.setBluetoothCallback(bluetoothCallback);
        bluetooth.setDiscoveryCallback(new DiscoveryCallback() {
            @Override
            public void onDiscoveryStarted() {
                Log.d("TAGCON", "Discovery Started");
            }

            @Override
            public void onDiscoveryFinished() {
                Log.d("TAGCON", "Discovery Finished");
            }

            @Override
            public void onDeviceFound(BluetoothDevice device) {
                Log.d("TAGCON", "onDeviceFound: "+device.getName());
            }

            @Override
            public void onDevicePaired(BluetoothDevice device) {
                Log.d("TAGCON", "Paired");
            }

            @Override
            public void onDeviceUnpaired(BluetoothDevice device) {
                Log.d("TAGCON", "Uparied");
            }

            @Override
            public void onError(int errorCode) {
                Log.d("TAGCON", "error: "+errorCode);
            }
        });
    }

    private void startScanning() {
        bluetooth.startScanning();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //bluetooth.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // bluetooth.onActivityResult(requestCode, resultCode);
    }

    private BluetoothCallback bluetoothCallback = new BluetoothCallback() {
        @Override
        public void onBluetoothTurningOn() {
            startScanning();
        }

        @Override
        public void onBluetoothOn() {
            startScanning();
        }

        @Override
        public void onBluetoothTurningOff() {
            stopScanning();
        }

        @Override
        public void onBluetoothOff() {
            stopScanning();
        }

        @Override
        public void onUserDeniedActivation() {

        }
    };

    private void stopScanning() {
        bluetooth.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_chat:
                menuAddChatClick();
                break;
            case R.id.menu_nearby_chat:
                break;
            case R.id.menu_signout:
                signout();
                break;
            default:
                break;
        }
        return true;
    }

    private void signout() {
        SharedPreferences sharedPreferences = getSharedPreferences("ChatWithMe",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        MessageMaker.getDatabaseHelper().deleteDatabase();
        MessageMaker.getDatabaseHelperChat().deleteDatabase();
        if(sinchClient != null) {
            sinchClient.stop();
            callClient.removeCallClientListener(callClientListener);
            callClientListener = null;
            callClient = null;
            sinchClient = null;
        }
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void menuAddChatClick() {
        if(bottomNavigationView.getSelectedItemId() == R.id.chats) {
            Intent intent = new Intent(getApplicationContext(),AddNewChat.class);
            intent.putExtra("title","Add New Chat");
            startActivity(intent);
        } else if(bottomNavigationView.getSelectedItemId() == R.id.freinds) {
            Intent intent = new Intent(getApplicationContext(), SearchFreinds.class);
            startActivity(intent);
        } else if(bottomNavigationView.getSelectedItemId() == R.id.calls) {
            Intent intent = new Intent(getApplicationContext(),AddNewCall.class);
            startActivity(intent);
        }
    }

    private void init() {
        profile = actionbarview.findViewById(R.id.profile);
        bottomNavigationView = findViewById(R.id.bottomnavigation);
        name = actionbarview.findViewById(R.id.name);
        status = actionbarview.findViewById(R.id.status);
        loadInfo();
        actionbarview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewContact.class);
                intent.putExtra("username",MessageMaker.getMyNumber());
                intent.putExtra("isFromChat",true);
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(Dashboard.this,profile,"profile");
                startActivity(intent,activityOptionsCompat.toBundle());
            }
        });
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.chats:
                        selectedFragment = new ChatFragment();
                        break;
                    case R.id.freinds:
                        selectedFragment = new FreindsFragment();
                        break;
                    case R.id.calls:
                        selectedFragment = new CallsFragment();
                        break;
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.viewpager, selectedFragment)
                        .commit();
                return true;
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.viewpager, new ChatFragment()).commit();
    }

    private void loadInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("ChatWithMe",MODE_PRIVATE);
        String profile = sharedPreferences.getString("profile","NO_PROFILE");
        String name = sharedPreferences.getString("name","");
        String status = sharedPreferences.getString("status","");
        if(profile.equalsIgnoreCase("NO_PROFILE")) {
            Glide.with(getApplicationContext()).load(R.drawable.profile).into(this.profile);
        } else {
            Glide.with(getApplicationContext()).load(profile).into(this.profile);
        }
        this.name.setText(name);
        this.status.setText(status);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(sinchClient != null) {
            sinchClient.stopListeningOnActiveConnection();
            sinchClient.terminate();
        }
        updateOnlineStatus("Offline",true);
    }

    private void setupSinch() {
        sinchClient = Sinch.getSinchClientBuilder().context(getApplicationContext())
                .applicationKey(sinchApplicationKey)
                .applicationSecret(sinchApplicationSecret)
                .environmentHost("clientapi.sinch.com")
                .userId(MessageMaker.getMyNumber())
                .enableVideoCalls(true)
                .callerIdentifier(MessageMaker.getMyNumber())
                .build();
        sinchClient.setSupportCalling(true);
        if(sinchClientListener != null) {
            sinchClient.removeSinchClientListener(sinchClientListener);
        }
        sinchClientListener = new SinchClientListener() {
            @Override
            public void onClientStarted(SinchClient sinchClient) {
                setupApptoAppCall();
            }

            @Override
            public void onClientStopped(SinchClient sinchClient) {
            }

            @Override
            public void onClientFailed(SinchClient sinchClient, SinchError sinchError) {

            }

            @Override
            public void onRegistrationCredentialsRequired(SinchClient sinchClient, ClientRegistration clientRegistration) {
            }

            @Override
            public void onLogMessage(int i, String s, String s1) {

            }
        };
        sinchClient.addSinchClientListener(sinchClientListener);
        sinchClient.getVideoController().setResizeBehaviour(VideoScalingType.ASPECT_BALANCED);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();
    }

    private void setupApptoAppCall() {
        callClient = sinchClient.getCallClient();
        callClientListener = new CallClientListener() {
            @Override
            public void onIncomingCall(CallClient callClient, Call call) {
                MessageMaker.handleIncomingCall(getApplicationContext(),call);
            }
        };
        callClient.addCallClientListener(callClientListener);
    }
}