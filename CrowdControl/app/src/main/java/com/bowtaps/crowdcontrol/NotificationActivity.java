package com.bowtaps.crowdcontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.bowtaps.crowdcontrol.adapters.GroupModelAdapter;
import com.bowtaps.crowdcontrol.adapters.InvitationModelAdapter;
import com.bowtaps.crowdcontrol.adapters.UserModelAdapter;
import com.bowtaps.crowdcontrol.model.BaseModel;
import com.bowtaps.crowdcontrol.model.GroupModel;
import com.bowtaps.crowdcontrol.model.InvitationModel;
import com.bowtaps.crowdcontrol.model.UserProfileModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the in app notifications for the user
 */
public class NotificationActivity extends AppCompatActivity implements ListView.OnItemClickListener, View.OnClickListener {

    //buttons
    Button mFinishButton;
    Button mClearAllButton;

    //List handlers //TODO Change to notification objects!!!!!!!!!
    private InvitationModelAdapter mNotificationModelAdapter;
    private ListView mNotificationListView;
    private ArrayList mNotificationList;

    private static final String TAG = NotificationActivity.class.getSimpleName();


    /**
     * Sets up the activity and loads in the new notifications
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Notifications");

        //set buttons
        mFinishButton = (Button) findViewById(R.id.finished_button);
        mClearAllButton = (Button) findViewById(R.id.clear_all_button);

        //set button handlers
        mFinishButton.setOnClickListener(this);
        mClearAllButton.setOnClickListener(this);

        // Initialize array stuff
        mNotificationList = new ArrayList<>();
        mNotificationModelAdapter = new InvitationModelAdapter(this, mNotificationList);
        mNotificationListView = (ListView) findViewById(R.id.notification_list_view);

        mNotificationListView.setAdapter(mNotificationModelAdapter);
        mNotificationListView.setOnItemClickListener(this);

        CrowdControlApplication.getInstance().getModelManager().fetchNotificationsInBackground(new BaseModel.FetchCallback() {
            @Override
            public void doneFetchingModels(List<? extends BaseModel> results, Exception ex) {

                // Verify operation was successful
                if (results == null || ex != null) {
                    Log.d("Notification Activity", "Failed to load group list");
                    return;
                }

                // Replace existing list with new results
                mNotificationList.clear();
                mNotificationList.addAll((List<InvitationModel>) results);

                // Force update on the list adapter
                mNotificationModelAdapter.notifyDataSetChanged();
            }
        });

//        // Load notifications
//        try {
//            mNotificationList = new ArrayList<>(CrowdControlApplication.getInstance().getModelManager().fetchNotifications());
//            mNotificationModelAdapter.notifyDataSetChanged();
//        } catch (Exception e) {
//            Log.d("NotificationActivity", "Unable to load notifications");
//        }
    }

    /**
     * Handles the clicks on the buttons in the activity
     * @param v - view of the button
     */
    @Override
    public void onClick(View v) {

        // Handles clicks on items in view
        // in this case, either the facebook button or the create account button

        switch (v.getId()) {
            case R.id.finished_button:
                finish();
                break;

            case R.id.clear_all_button:
                mNotificationModelAdapter.clear();
                break;

            default:
                // Sorry, you're outta luck
                break;
        }
    }


    /**
     * Handles clicks onto the individual notifications in the list view
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Get the selected group
        final GroupModel groupModel = mNotificationModelAdapter.getItem(position).getGroup();

        try {
            CrowdControlApplication.getInstance().getModelManager().joinGroup(groupModel);
        } catch(Exception e){
            Log.d(TAG, "Unable to join group");
        }

        CrowdControlApplication.getInstance().getModelManager().setCurrentGroup((GroupModel) groupModel);
        launchGroupNavigationActivity();
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
}
