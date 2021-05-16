package com.nobodyknows.chatwithme.Activities.Dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.NobodyKnows.chatlayoutview.Model.Contact;
import com.bumptech.glide.Glide;
import com.github.tamir7.contacts.Contacts;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().hide();
        Contacts.initialize(getApplicationContext());
        init();
    }

    private void init() {
        profile = findViewById(R.id.profile);
        name = findViewById(R.id.name);
        status = findViewById(R.id.status);
        addChat = findViewById(R.id.addchat);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        DashboardFragment dashboardFragment = new DashboardFragment(getSupportFragmentManager());
        int limit = (dashboardFragment.getCount() > 1 ? dashboardFragment.getCount() - 1 : 1);
        viewPager.setOffscreenPageLimit(limit);
        viewPager.setAdapter(dashboardFragment);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        loadInfo();
        addChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tabLayout.getSelectedTabPosition() == 0) {
                    Intent intent = new Intent(getApplicationContext(),AddNewChat.class);
                    startActivity(intent);
                }
            }
        });
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