package com.bowtaps.crowdcontrol;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.bowtaps.crowdcontrol.adapters.SimpleTabsAdapter;
import com.bowtaps.crowdcontrol.adapters.UserModelAdapter;
import com.bowtaps.crowdcontrol.model.GroupModel;
import com.bowtaps.crowdcontrol.model.UserProfileModel;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

/**
 * This tab-based activity holds the fragments for search-invite and confirm-invite
 * (@see InviteConfirmFragment)(@see InviteSearchFragment)
 */
public class InviteNavigationActivity extends AppCompatActivity {

    Toolbar mToolbar;

    private TabLayout mTabs;
    private ViewPager mTabsviewPager;
    private SimpleTabsAdapter mTabsAdapter;
    private GroupService.GroupServiceBinder groupServiceBinder;

    private ServiceConnection mServiceConnection;

    private GroupModel mGroup;

    private InviteSearchFragment mInviteSearchFragment;
    private InviteConfirmFragment mInviteConfirmFragment;

    private Intent mServiceIntent;


    private UserModelAdapter mUserModelAdapter;
    private ListView mFoundUsersListView;
    private List<UserProfileModel> mFoundUserList;


    //sets up icons for tabs
    private int[] tabIcons = {
            R.drawable.streamline_person,
            R.drawable.streamline_map_pin,
            R.drawable.streamline_bubble,
            R.drawable.streamline_notebook
    };


    /*
     *  Sets up the (@Link SimpleTabsAdapter) and adds in tabs and their fragments.
     *  This function also handles the calls required to set up the option menu.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_navigation);
        setTitle(CrowdControlApplication.getInstance().getModelManager().getCurrentGroup().getGroupName() + " - Inviting Friends");


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setTitle(CrowdControlApplication.getInstance().getModelManager().getCurrentGroup().getGroupName() + " - Inviting Friends");

        mTabsviewPager = (ViewPager) findViewById(R.id.tabspager);

        mTabsAdapter = new SimpleTabsAdapter(getSupportFragmentManager());

        // Create fragment objects
        mInviteSearchFragment = InviteSearchFragment.newInstance("Search");
        mInviteConfirmFragment = InviteConfirmFragment.newInstance("Confirm");

        // Add fragments to tab manager
        mTabsAdapter.addFragment(mInviteSearchFragment, "");
        mTabsAdapter.addFragment(mInviteConfirmFragment, "");

        //setup viewpager to give swipe effect
        mTabsviewPager.setAdapter(mTabsAdapter);

        mTabs = (TabLayout) findViewById(R.id.tabs);
        mTabs.setupWithViewPager(mTabsviewPager);

        mTabs.getTabAt(0).setIcon(tabIcons[3]);
        //mTabs.getTabAt(0).setText("0");
        mTabs.getTabAt(1).setIcon(tabIcons[0]);

        // Bind to group service
        groupServiceBinder = null;
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                InviteNavigationActivity.this.groupServiceBinder = (GroupService.GroupServiceBinder) service;

                InviteNavigationActivity.this.groupServiceBinder.addGroupUpdatesListener(mInviteSearchFragment);
                InviteNavigationActivity.this.groupServiceBinder.addGroupUpdatesListener(mInviteConfirmFragment);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                InviteNavigationActivity.this.groupServiceBinder = null;
            }
        };
        bindService(new Intent(getApplicationContext(), GroupService.class), mServiceConnection, BIND_IMPORTANT);

        // Bind to group service
        groupServiceBinder = null;
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                InviteNavigationActivity.this.groupServiceBinder = (GroupService.GroupServiceBinder) service;

                //InviteNavigationActivity.this.groupServiceBinder.addGroupUpdatesListener(mInviteSearchFragment);
                //InviteNavigationActivity.this.groupServiceBinder.addLocationUpdatesListener(mInviteConfirmFragment);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                InviteNavigationActivity.this.groupServiceBinder = null;
            }
        };
    }

    /*
     *  Creates the option menu for the tool bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_event_navigation, menu);
        return true;
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

        //Launch Settings
        if (id == R.id.action_settings) {
            launchSettingsActivity();
            return true;
        }

        //Notifications
        if (id == R.id.action_notification) {
            launchNotificationActivity();
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
     *  Launches the (@Link NotificationActivity)
     *
     *  @see SettingsActivity
     */
    private void launchNotificationActivity() {
        Intent myIntent = new Intent(this, NotificationActivity.class);
        this.startActivity(myIntent);
    }

    /**
     * Disconnects from the group update listener
     * @see GroupService
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        groupServiceBinder.removeGroupUpdatesListener(mInviteSearchFragment);
        groupServiceBinder.removeGroupUpdatesListener(mInviteConfirmFragment);
    }


    /**
     * Allows a fragment to set the Found User List
     * @param FoundUserList - The list in question - holds UserProfileModels
     */
    public void setmFoundUserList( List<UserProfileModel> FoundUserList){
        mFoundUserList = FoundUserList;
    }

    /**
     * allows a fragment to get the Found User List
     * @return UserProfileModel List
     */
    public List<UserProfileModel> getmFoundUserList (){
        return mFoundUserList;
    }
}