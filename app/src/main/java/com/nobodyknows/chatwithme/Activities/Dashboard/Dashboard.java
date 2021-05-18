package com.nobodyknows.chatwithme.Activities.Dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.tamir7.contacts.Contacts;
import com.google.android.material.tabs.TabLayout;
import com.nobodyknows.chatwithme.Database.DatabaseHelper;
import com.nobodyknows.chatwithme.Fragments.DashboardFragment;
import com.nobodyknows.chatwithme.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Dashboard extends AppCompatActivity {


    private CircleImageView profile;
    private TextView name,status;
    private ViewPager viewPager;
    private ImageView addChat,addConnection;
    private TabLayout tabLayout;
    private View actionbarview;
    public static DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.dashboard_toolbar_view);
        actionbarview = getSupportActionBar().getCustomView();
        getSupportActionBar().setElevation(0);
        Contacts.initialize(getApplicationContext());
        databaseHelper = new DatabaseHelper(getApplicationContext());
        databaseHelper.createTable();
        init();
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
            default:
                break;
        }
        return true;
    }

    private void menuAddChatClick() {
        if(tabLayout.getSelectedTabPosition() == 0) {
            Intent intent = new Intent(getApplicationContext(),AddNewChat.class);
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