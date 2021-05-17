package com.nobodyknows.chatwithme.Fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class DashboardFragment extends FragmentPagerAdapter {

    public DashboardFragment(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return new ChatFragment();
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return new StoriesFragment();
            case 2: // Fragment # 1 - This will show SecondFragment
                return new SettingFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return "Chat";
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return "Stories";
            case 2: // Fragment # 1 - This will show SecondFragment
                return "Settings";
            default:
                return null;
        }
    }
}
