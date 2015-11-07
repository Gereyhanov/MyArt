package com.example.rizvan.myart.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.rizvan.myart.fragment.DefaultFragmentTabOne;
import com.example.rizvan.myart.fragment.DefaultFragmentTabThree;
import com.example.rizvan.myart.fragment.DefaultFragmentTabTwo;

/**
 * Created by Rizvan on 24.10.2015.
 */
public class TabsPagerFragmentAdapter extends FragmentPagerAdapter {

    private String[] tabs;

    public TabsPagerFragmentAdapter(FragmentManager fm) {
        super(fm);

        tabs = new String[]{
                "Tab 1",
                "Tab 2",
                "Tab 3"
        };
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return DefaultFragmentTabOne.getInstance();
            case 1:
                return DefaultFragmentTabTwo.getInstance();
            case 2:
                return DefaultFragmentTabThree.getInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabs.length;
    }
}
