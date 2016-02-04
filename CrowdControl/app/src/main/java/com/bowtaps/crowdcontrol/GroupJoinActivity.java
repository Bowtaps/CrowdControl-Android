package com.bowtaps.crowdcontrol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    Button mButtonSettings;

    // List view pieces
    private GroupModelAdapter mGroupListAdapter;
    private ListView mGroupListView;
    private List<GroupModel> mGroupList;

    private Intent mServiceIntent;

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

        //TODO move to implementation
        //Set up messaging service
        mServiceIntent = new Intent( getApplicationContext(), MessageService.class);

        startService(mServiceIntent);

        // Initialize list adapter for mGroupListView
        mGroupList = new ArrayList<GroupModel>();
        mGroupListAdapter = new GroupModelAdapter(this, mGroupList);

        // Initialize ListView and set initial view to mMainAdapter
        mGroupListView = (ListView) findViewById(R.id.group_list);
        mGroupListView.setAdapter(mGroupListAdapter);
        mGroupListView.setOnItemClickListener(this);

        // Get handles to Buttons
        mButtonToTabs = (Button) findViewById(R.id.buttonToTab);
        mButtonSettings = (Button) findViewById(R.id.buttonSettings);

        // Declare button click event handlers
        mButtonToTabs.setOnClickListener(this);
        mButtonSettings.setOnClickListener(this);

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
     *  Creates the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_join, menu);
        return true;
    }

    /*
     *  Determines which item in the list view is selected
     *
     *  @see CustomParseAdapter
     *  @see GroupNavigationActivity
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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

            case R.id.buttonSettings:
                onSettingsButtonClick((Button) view);
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
        final UserModel userModel = CrowdControlApplication.getInstance().getModelManager().getCurrentUser();

        // First load members from current group
        groupModel.loadInBackground(new BaseModel.LoadCallback() {
            @Override
            public void doneLoadingModel(BaseModel object, Exception ex) {

                // Verify operation was successful
                if (object == null || ex != null) {
                    Log.d(TAG, "Unable to load group model");
                    return;
                }

                if (!groupModel.addGroupMember(userModel.getProfile())) {
                    Log.d(TAG, "Unable to add user to group. User may already be member of group.");
                }

                // After changing the model, attempt to save
                groupModel.saveInBackground(new BaseModel.SaveCallback() {
                    @Override
                    public void doneSavingModel(BaseModel object, Exception ex) {

                        // Verify operation was successful
                        if (ex != null) {
                            Log.d(TAG, "Unable to save group model");
                            return;
                        }

                        CrowdControlApplication.getInstance().getModelManager().setCurrentGroup((GroupModel) object);
                        launchGroupNavigationActivity();
                    }
                });
            }
        });
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
    }

    public void onDestroy() {
        stopService(new Intent(this,MessageService.class));
        super.onDestroy();
    }

}
