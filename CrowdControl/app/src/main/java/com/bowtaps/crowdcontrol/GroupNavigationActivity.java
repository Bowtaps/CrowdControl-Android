package com.bowtaps.crowdcontrol;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.bowtaps.crowdcontrol.adapters.SimpleTabsAdapter;

/**
 * This Activity will manage all the tabs related to the current group
 * It uses a tab based system to switch between fragments
 */
public class GroupNavigationActivity extends AppCompatActivity {

    Toolbar mToolbar;

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
        setContentView(R.layout.activity_tab);
        setTitle(CrowdControlApplication.getInstance().getModelManager().getCurrentGroup().getGroupName());

        setUpReceiver();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setTitle(CrowdControlApplication.getInstance().getModelManager().getCurrentGroup().getGroupName());

        tabsviewPager = (ViewPager) findViewById(R.id.tabspager);

        mTabsAdapter = new SimpleTabsAdapter(getSupportFragmentManager());

        // Start messaging service
        startService(new Intent(getApplicationContext(), MessageService.class));

        // Create fragment objects
        mGroupInfoFragment = GroupInfoFragment.newInstance("Group");
        mMapFragment       = MapFragment.newInstance("Map");
        mMessagingFragment = MessagingFragment.newInstance("Messaging");
        //mEventFragment     = EventFragment.newInstance("Events");

        // Add fragments to tab manager
        mTabsAdapter.addFragment(mGroupInfoFragment, "");
        mTabsAdapter.addFragment(mMapFragment, "");
        mTabsAdapter.addFragment(mMessagingFragment, "");
        //mTabsAdapter.addFragment(mEventFragment, "Events");

        //setup viewpager to give swipe effect
        tabsviewPager.setAdapter(mTabsAdapter);

        mTabs = (TabLayout) findViewById(R.id.tabs);
        mTabs.setupWithViewPager(tabsviewPager);

        mTabs.getTabAt(0).setIcon(tabIcons[0]);
        mTabs.getTabAt(1).setIcon(tabIcons[1]);
        mTabs.getTabAt(2).setIcon(tabIcons[2]);

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
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
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

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("com.bowtaps.crowdcontrol.GroupNavigationActivity"));

    }

    /*
     *  This handles the clicks on the drop down menu in the tool bar
     *
     *  @see GroupModelAdapter
     *  @see GroupNavigationActivity
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection Launch Settings
        if (id == R.id.action_settings) {
            launchSettingsActivity();
            return true;
        }

        //noinspection Launch Invite
        if (id == R.id.action_invite) {
            launchInviteActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     *  Launches the (@Link SettingsActivity)
     *
     *  @see SettingsActivity
     */
    private void launchSettingsActivity() {
        Intent myIntent = new Intent(this, SettingsActivity.class);
        this.startActivity(myIntent);
    }

    /*
     *  Launches the (@Link InviteNavigationActivity)
     *
     *  @see InviteNavigationActivity
     */
    private void launchInviteActivity() {
        Intent myIntent = new Intent(this, InviteNavigationActivity.class);
        this.startActivity(myIntent);
    }

    /*
     *  Creates the option menu for the tool bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        //If leader display leader specific menu
        if( CrowdControlApplication.getInstance().getModelManager().getCurrentGroup().getGroupLeader() == null) {
            getMenuInflater().inflate(R.menu.menu_main_event_navigation_leader, menu);
            return true;
        }
        else if ( CrowdControlApplication.getInstance().getModelManager().getCurrentGroup().getGroupLeader().
                equals(CrowdControlApplication.getInstance().getModelManager().getCurrentUser().getProfile())) {
            getMenuInflater().inflate(R.menu.menu_main_event_navigation_leader, menu);
            return true;
        }
        else {
            getMenuInflater().inflate(R.menu.menu_main_event_navigation, menu);
            return true;
        }
    }
}
