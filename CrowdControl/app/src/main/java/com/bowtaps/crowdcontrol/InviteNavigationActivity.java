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
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    /*
     *  Sets up the (@Link SimpleTabsAdapter) and adds in tabs and their fragments.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_navigation);
        setTitle(CrowdControlApplication.getInstance().getModelManager().getCurrentGroup().getGroupName() + " - Inviting Friends");


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
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
        mTabs.getTabAt(0).setText("0");
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
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //groupServiceBinder.removeGroupUpdatesListener(mInviteSearchFragment);
        //groupServiceBinder.removeLocationUpdatesListener(mInviteConfirmFragment);
    }


    public void setmFoundUserList( List<UserProfileModel> FoundUserList){
        mFoundUserList = FoundUserList;
    }

    public List<UserProfileModel> getmFoundUserList (){
        return mFoundUserList;
    }
}