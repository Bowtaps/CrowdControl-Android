package com.bowtaps.crowdcontrol;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.bowtaps.crowdcontrol.adapters.SimpleTabsAdapter;

/**
 * This Activity will manage all the tabs related to the current group
 * It uses a tab based system to switch between fragments
 */
public class GroupNavigationActivity extends AppCompatActivity {

    private TabLayout mTabs;
    private ViewPager tabsviewPager;
    private SimpleTabsAdapter mTabsAdapter;
    private GroupService.GroupServiceBinder groupServiceBinder;

    private ProgressDialog progressDialog;
    private BroadcastReceiver receiver = null;
    private ServiceConnection mServiceConnection;

    private GroupInfoFragment mGroupInfoFragment;
    private MapFragment       mMapFragment;
    private MessagingFragment mMessagingFragment;
    private EventFragment     mEventFragment;

    private Intent mServiceIntent;

    /*
     *  Sets up the (@Link SimpleTabsAdapter) and adds in tabs and their fragments.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        setTitle(CrowdControlApplication.getInstance().getModelManager().getCurrentGroup().getGroupName());

        setUpReceiver();

        //TODO move to implementation
        //Set up messaging service
        mServiceIntent = new Intent( getApplicationContext(), MessageService.class);

        startService(mServiceIntent);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabsviewPager = (ViewPager) findViewById(R.id.tabspager);

        mTabsAdapter = new SimpleTabsAdapter(getSupportFragmentManager());

        // Create fragment objects
        mGroupInfoFragment = GroupInfoFragment.newInstance("Group");
        mMapFragment       = MapFragment.newInstance("Map");
        mMessagingFragment = MessagingFragment.newInstance("Messaging");
        mEventFragment     = EventFragment.newInstance("Events");

        // Add fragments to tab manager
        mTabsAdapter.addFragment(mGroupInfoFragment, "Group");
        mTabsAdapter.addFragment(mMapFragment, "Map");
        mTabsAdapter.addFragment(mMessagingFragment, "Messaging");
        mTabsAdapter.addFragment(mEventFragment, "Events");

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
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                GroupNavigationActivity.this.groupServiceBinder = (GroupService.GroupServiceBinder) service;

                GroupNavigationActivity.this.groupServiceBinder.addGroupUpdatesListener(mGroupInfoFragment);
                GroupNavigationActivity.this.groupServiceBinder.addLocationUpdatesListener(mMapFragment);
                GroupNavigationActivity.this.groupServiceBinder.addGroupUpdatesListener(mMessagingFragment);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                GroupNavigationActivity.this.groupServiceBinder = null;
            }
        };

        bindService(new Intent(getApplicationContext(), GroupService.class), mServiceConnection, BIND_IMPORTANT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        groupServiceBinder.removeGroupUpdatesListener(mGroupInfoFragment);
        groupServiceBinder.removeLocationUpdatesListener(mMapFragment);
        groupServiceBinder.removeGroupUpdatesListener(mMessagingFragment);

        unbindService(mServiceConnection);
        stopService(new Intent(getApplicationContext(), GroupService.class));
    }

    private void setUpReceiver() {
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle("Loading");
//        progressDialog.setMessage("Please wait...");
//        progressDialog.show();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean success = intent.getBooleanExtra("success", false);
//                progressDialog.dismiss();
                if (!success) {
                    Toast.makeText(getApplicationContext(), "Messaging service failed to start", Toast.LENGTH_LONG).show();
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("com.bowtaps.crowdcontrol"));

    }
}
