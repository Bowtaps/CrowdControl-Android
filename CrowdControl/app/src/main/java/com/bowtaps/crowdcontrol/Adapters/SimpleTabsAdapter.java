package com.bowtaps.crowdcontrol.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 7143145
 * @since 10/26/2015.
 */
public class SimpleTabsAdapter extends FragmentPagerAdapter {

    private final List<Fragment> FragmentList = new ArrayList();
    private final List<String> FragmentTitles = new ArrayList();

    public SimpleTabsAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title) {
        FragmentList.add(fragment);
        FragmentTitles.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentList.get(position);
    }

    @Override
    public int getCount() {
        return FragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return FragmentTitles.get(position);
    }
}
