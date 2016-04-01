package com.bowtaps.crowdcontrol;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.bowtaps.crowdcontrol.adapters.GroupModelAdapter;
import com.bowtaps.crowdcontrol.model.BaseModel;
import com.bowtaps.crowdcontrol.model.GroupModel;
import com.bowtaps.crowdcontrol.model.UserModel;

import java.util.ArrayList;
import java.util.List;

/*
 * This Activity is where a user will either join an existing group or create a
 * new one.
 * This Activity will be the default view of all registered users that are not
 * in a group
 */
public class GroupJoinActivity extends AppCompatActivity implements View.OnClickListener, ListView.OnItemClickListener {


    Button mButtonToTabs;

    //sets class based toolbar
    Toolbar mToolbar;



    // List view pieces
    private GroupModelAdapter mGroupListAdapter;
    private ListView mGroupListView;
    private List<GroupModel> mGroupList;

    private Intent mMessagingServiceIntent;

    private static final String TAG = GroupJoinActivity.class.getSimpleName();

    /**
     * Uses the query adapter  {@link GroupModelAdapter} to show a list of available groups in a
     * {@link ListView}.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_join);


        // Initialize list adapter for mGroupListView
        mGroupList = new ArrayList<GroupModel>();
        mGroupListAdapter = new GroupModelAdapter(this, mGroupList);

        // Initialize ListView and set initial view to mMainAdapter
        mGroupListView = (ListView) findViewById(R.id.group_list);
        mGroupListView.setAdapter(mGroupListAdapter);
        mGroupListView.setOnItemClickListener(this);

        // Get handles to Buttons
        mButtonToTabs = (Button) findViewById(R.id.buttonToTab);

        // Declare button click event handlers
        mButtonToTabs.setOnClickListener(this);

        // Check if in a Group
        try {
            CrowdControlApplication.getInstance().getModelManager().fetchCurrentGroup();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if( CrowdControlApplication.getInstance().getModelManager().getCurrentGroup() != null ) {
            launchGroupNavigationActivity();
        }

        //set up Tool Bar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setTitle("Join A Group");


        // Fetch group list to display
        CrowdControlApplication.getInstance().getModelManager().fetchAllGroupsInBackground(new BaseModel.FetchCallback() {
            @Override
            public void doneFetchingModels(List<? extends BaseModel> results, Exception ex) {

                // Verify operation was successful
                if (results == null || ex != null) {
                    Log.d(TAG, "Failed to load group list");
                    return;
                }

                // Replace existing list with new results
                mGroupList.clear();
                mGroupList.addAll((List<GroupModel>) results);

                // Force update on the list adapter
                mGroupListAdapter.notifyDataSetChanged();
            }
        });
    }

    /*
     *  Creates the option menu for the tool bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_join, menu);
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

        //check if settings
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
     *  Determines which button was pressed and launches the code related to
     *  that button
     */
    @Override
    public void onClick(View view) {
        // Handles clicks on items in view
        // in this case, either the facebook button or the create account button

        switch (view.getId()) {
            case R.id.buttonToTab:
                onCreateButtonClick((Button) view);
                break;

            default:
                // Sorry, you're outta luck
                break;
        }
    }

    /*
     *  Grabs the click in the list view and then Joins the Group that was clicked on
     */
    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id){
        //TODO request to join group instead of just joining it

        // Get the selected group
        final GroupModel groupModel = mGroupListAdapter.getItem(position);

        try {
            CrowdControlApplication.getInstance().getModelManager().joinGroup(groupModel);
        } catch(Exception e){
            Log.d(TAG, "Unable to join group");
        }

        CrowdControlApplication.getInstance().getModelManager().setCurrentGroup((GroupModel) groupModel);
        launchGroupNavigationActivity();

    }

    /**
     * Sends user to the group creationg page {@link GroupCreateActivity}.
     *
     * @param button  The button object that was clicked.
     * @see           GroupCreateActivity
     */
    private void onCreateButtonClick(Button button) {
        launchCreateGroupActivity();
    }

    /*
     *  Code ran if settings button is pushed. Launches the settings
     *  activity
     *
     *  @see SettingsActivity
     */
    private void onSettingsButtonClick(Button button) {
        launchSettingsActivity();
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

    /*
     *  Launches the (@Link SettingsActivity)
     *
     *  @see SettingsActivity
     */
    private void launchSettingsActivity() {
        Intent myIntent = new Intent(this, SettingsActivity.class);
        this.startActivity(myIntent);
    }

    /**
     * Launches the {@link GroupCreateActivity}.
     *
     * @see GroupCreateActivity
     */
    private void launchCreateGroupActivity() {
        Intent myIntent = new Intent(this, GroupCreateActivity.class);
        this.startActivity(myIntent);
    }

    /*
     *  Launches the (@Link GroupNavigationActivity)
     *
     *  @see GroupNavigationActivity
     */
    private void launchGroupNavigationActivity() {
        Intent myIntent = new Intent(this, GroupNavigationActivity.class);
        this.startActivity(myIntent);
        finish();
    }

    public void onDestroy() {
        super.onDestroy();
    }

}
