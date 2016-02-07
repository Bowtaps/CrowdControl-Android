package com.bowtaps.crowdcontrol;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.bowtaps.crowdcontrol.adapters.SimpleTabsAdapter;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.HashMap;
import java.util.List;

/**
 * This Activity will manage all the tabs related to the current group
 * It uses a tab based system to switch between fragments
 */
public class GroupNavigationActivity extends AppCompatActivity {

    TabLayout mTabs;
    private ViewPager tabsviewPager;
    private SimpleTabsAdapter mTabsAdapter;
    private GroupService.GroupServiceBinder groupServiceBinder;

    /*
     *  sets up the (@Link SimpleTabsAdapter) and adds in the possible Fragments
     *
     *  @see SimpleTabsAdapter
     *  @see GroupInfoFragment
     *  @see MapFragment
     *  @see MessageingFragment
     *  @see EventFragment
     */
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
        mTabsAdapter.addFragment(MessagingFragment.newInstance("Messaging"), "Messaging");
        //mTabsAdapter.addFragment(MessagingFragment.instantiate(this.getBaseContext(), "Messaging"), "Messaging");
        mTabsAdapter.addFragment(EventFragment.newInstance("Suggestions"), "Events");

        //setup viewpager to give swipe effect
        tabsviewPager.setAdapter(mTabsAdapter);

        mTabs = (TabLayout) findViewById(R.id.tabs);
        mTabs.setupWithViewPager(tabsviewPager);


        // Start service if it's not working
        if (!GroupService.isRunning()) {
            Intent serviceIntent = new Intent(getApplicationContext(), GroupService.class);
            serviceIntent.putExtra(GroupService.INTENT_GROUP_ID_KEY, CrowdControlApplication.getInstance().getModelManager().getCurrentGroup().getId());
            serviceIntent.putExtra(GroupService.INTENT_USER_ID_KEY, CrowdControlApplication.getInstance().getModelManager().getCurrentUser().getProfile().getId());
            startService(serviceIntent);
        }

        groupServiceBinder = null;
        bindService(new Intent(getApplicationContext(), GroupService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                GroupNavigationActivity.this.groupServiceBinder = (GroupService.GroupServiceBinder) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                GroupNavigationActivity.this.groupServiceBinder = null;
            }
        }, BIND_IMPORTANT);
    }
}
