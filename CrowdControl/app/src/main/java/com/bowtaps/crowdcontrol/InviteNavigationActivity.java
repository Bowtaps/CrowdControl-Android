package com.bowtaps.crowdcontrol;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bowtaps.crowdcontrol.adapters.SimpleTabsAdapter;

public class InviteNavigationActivity extends AppCompatActivity {

    private TabLayout mTabs;
    private ViewPager tabsviewPager;
    private SimpleTabsAdapter mTabsAdapter;
    private GroupService.GroupServiceBinder groupServiceBinder;

    private ServiceConnection mServiceConnection;

    private GroupInfoFragment mGroupInfoFragment;
    private MapFragment       mMapFragment;

    private Intent mServiceIntent;

    //sets up icons for tabs
    private int[] tabIcons = {
            R.drawable.streamline_person,
            R.drawable.streamline_map_pin,
            R.drawable.streamline_bubble,
            R.drawable.streamline_notebook
    };


    /*
     *  Sets up the (@Link SimpleTabsAdapter) and adds in tabs and their fragments.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_navigation);
        setTitle(CrowdControlApplication.getInstance().getModelManager().getCurrentGroup().getGroupName());

        tabsviewPager = (ViewPager) findViewById(R.id.tabspager);

        mTabsAdapter = new SimpleTabsAdapter(getSupportFragmentManager());

        // Start messaging service
        startService(new Intent(getApplicationContext(), MessageService.class));

        // Create fragment objects
        mGroupInfoFragment = GroupInfoFragment.newInstance("Group");
        mMapFragment       = MapFragment.newInstance("Map");

        // Add fragments to tab manager
        mTabsAdapter.addFragment(mGroupInfoFragment, "");
        mTabsAdapter.addFragment(mMapFragment, "");

        //setup viewpager to give swipe effect
        tabsviewPager.setAdapter(mTabsAdapter);

        mTabs = (TabLayout) findViewById(R.id.tabs);
        mTabs.setupWithViewPager(tabsviewPager);

        mTabs.getTabAt(0).setIcon(tabIcons[0]);
        mTabs.getTabAt(1).setIcon(tabIcons[1]);

        // Start group service if it's not running
        if (!GroupService.isRunning()) {
            Intent serviceIntent = new Intent(getApplicationContext(), GroupService.class);
            serviceIntent.putExtra(GroupService.INTENT_GROUP_ID_KEY, CrowdControlApplication.getInstance().getModelManager().getCurrentGroup().getId());
            serviceIntent.putExtra(GroupService.INTENT_USER_ID_KEY, CrowdControlApplication.getInstance().getModelManager().getCurrentUser().getProfile().getId());
            startService(serviceIntent);
        }

        // Bind to group service
        groupServiceBinder = null;
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                InviteNavigationActivity.this.groupServiceBinder = (GroupService.GroupServiceBinder) service;

                InviteNavigationActivity.this.groupServiceBinder.addGroupUpdatesListener(mGroupInfoFragment);
                InviteNavigationActivity.this.groupServiceBinder.addLocationUpdatesListener(mMapFragment);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                InviteNavigationActivity.this.groupServiceBinder = null;
            }
        };
        bindService(new Intent(getApplicationContext(), GroupService.class), mServiceConnection, BIND_IMPORTANT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        groupServiceBinder.removeGroupUpdatesListener(mGroupInfoFragment);
        groupServiceBinder.removeLocationUpdatesListener(mMapFragment);

        unbindService(mServiceConnection);
        stopService(new Intent(getApplicationContext(), GroupService.class));
    }

}
