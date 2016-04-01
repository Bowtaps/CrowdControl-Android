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
import android.widget.ListView;

import com.bowtaps.crowdcontrol.adapters.SimpleTabsAdapter;
import com.bowtaps.crowdcontrol.adapters.UserModelAdapter;
import com.bowtaps.crowdcontrol.model.UserProfileModel;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

public class InviteNavigationActivity extends AppCompatActivity {

    private TabLayout mTabs;
    private ViewPager mTabsviewPager;
    private SimpleTabsAdapter mTabsAdapter;
    private GroupService.GroupServiceBinder groupServiceBinder;

    private ServiceConnection mServiceConnection;

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
        setTitle("Invite");

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

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "InviteNavigation Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.bowtaps.crowdcontrol/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "InviteNavigation Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.bowtaps.crowdcontrol/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    public void setmFoundUserList( List<UserProfileModel> FoundUserList){
        mFoundUserList = FoundUserList;
    }

    public List<UserProfileModel> getmFoundUserList (){
        return mFoundUserList;
    }
}