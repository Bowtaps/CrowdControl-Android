package com.bowtaps.crowdcontrol;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by 7143145 on 10/26/2015.
 */
public class TestActivity extends AppCompatActivity {

    TabLayout mTabs;
    private ViewPager tabsviewPager;
    private SimpleTabsAdapter mTabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_test);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabsviewPager = (ViewPager) findViewById(R.id.tabspager);

        mTabsAdapter = new SimpleTabsAdapter(getSupportFragmentManager());

        //creating the tabs and adding them to adapter class
        mTabsAdapter.addFragment(GroupInfoFragment.newInstance("Group Information"), "Group Information");
        mTabsAdapter.addFragment(MapFragment.newInstance("Map Fragment"), "Map");
        mTabsAdapter.addFragment(TestFragment.newInstance("Leaves"), "leaves");
        mTabsAdapter.addFragment(EventFragment.newInstance("Suggestions"), "Events");

        //setup viewpager to give swipe effect
        tabsviewPager.setAdapter(mTabsAdapter);

        mTabs = (TabLayout) findViewById(R.id.tabs);
        mTabs.setupWithViewPager(tabsviewPager);

    }

}