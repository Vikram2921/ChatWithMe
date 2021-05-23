package com.nobodyknows.chatwithme.Activities.Dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.NobodyKnows.chatlayoutview.Constants.MessageStatus;
import com.NobodyKnows.chatlayoutview.Interfaces.LastMessageUpdateListener;
import com.NobodyKnows.chatlayoutview.Model.Message;
import com.bumptech.glide.Glide;
import com.github.tamir7.contacts.Contacts;
import com.google.android.material.tabs.TabLayout;
import com.nobodyknows.chatwithme.Activities.BookVaccine;
import com.nobodyknows.chatwithme.Activities.SetuLogin;
import com.nobodyknows.chatwithme.Database.DatabaseHelper;
import com.nobodyknows.chatwithme.Fragments.DashboardFragment;
import com.nobodyknows.chatwithme.MainActivity;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.FirebaseService;
import com.nobodyknows.chatwithme.services.MessageMaker;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.BluetoothCallback;
import me.aflak.bluetooth.interfaces.DiscoveryCallback;

public class Dashboard extends AppCompatActivity {


    private CircleImageView profile;
    private TextView name,status;
    private ViewPager viewPager;
    private ImageView addChat,addConnection;
    private TabLayout tabLayout;
    private View actionbarview;
    public static FirebaseService firebaseService;
    public static DatabaseHelper databaseHelper;
    public static com.NobodyKnows.chatlayoutview.DatabaseHelper.DatabaseHelper databaseHelperChat;
    private Bluetooth bluetooth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.dashboard_toolbar_view);
        actionbarview = getSupportActionBar().getCustomView();
        getSupportActionBar().setElevation(0);
        MessageMaker.setMyNumber(MessageMaker.getFromSharedPrefrences(getApplicationContext(),"number"));
        firebaseService = new FirebaseService();
        databaseHelper = new DatabaseHelper(getApplicationContext());
        databaseHelperChat = new com.NobodyKnows.chatlayoutview.DatabaseHelper.DatabaseHelper(getApplicationContext(), new LastMessageUpdateListener() {
            @Override
            public void onLastMessageAdded(Message message, String roomid) {
                databaseHelper.updateUserLastMessage(message);
            }
        });
        databaseHelper.createTable();
        EmojiManager.install(new GoogleEmojiProvider());
      //  setupBlueTooth();
        Contacts.initialize(getApplicationContext());
        init();
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

    @Override
    protected void onStart() {
        super.onStart();
//        bluetooth.onStart();
//        if(bluetooth.isEnabled()){
//            startScanning();
//        } else {
//            bluetooth.enable();
//        }
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
            case R.id.menu_book_vacine:
                Intent intent = new Intent(getApplicationContext(), BookVaccine.class);
                startActivity(intent);
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
        databaseHelper.deleteDatabase();
        databaseHelperChat.deleteDatabase();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void menuAddChatClick() {
        if(tabLayout.getSelectedTabPosition() == 0) {
            Intent intent = new Intent(getApplicationContext(),AddNewChat.class);
            intent.putExtra("title","Add New Chat");
            startActivity(intent);
        }
    }

    private void init() {
        profile = actionbarview.findViewById(R.id.profile);
        name = actionbarview.findViewById(R.id.name);
        status = actionbarview.findViewById(R.id.status);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        DashboardFragment dashboardFragment = new DashboardFragment(getSupportFragmentManager());
        int limit = (dashboardFragment.getCount() > 1 ? dashboardFragment.getCount() - 1 : 1);
        viewPager.setOffscreenPageLimit(limit);
        viewPager.setAdapter(dashboardFragment);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        loadInfo();
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

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}