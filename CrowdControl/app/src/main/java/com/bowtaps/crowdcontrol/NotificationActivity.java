package com.bowtaps.crowdcontrol;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.bowtaps.crowdcontrol.adapters.GroupModelAdapter;
import com.bowtaps.crowdcontrol.adapters.UserModelAdapter;
import com.bowtaps.crowdcontrol.model.GroupModel;
import com.bowtaps.crowdcontrol.model.UserProfileModel;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity implements ListView.OnItemClickListener, View.OnClickListener {

    //buttons
    Button mFinishButton;
    Button mClearAllButton;

    //List handlers //TODO Change to notification objects!!!!!!!!!
    private GroupModelAdapter mNotificationModelAdapter;
    private ListView mNotificationListView;
    private List<GroupModel> mNotificationList;



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

        //Initallize array stuff
        mNotificationList = new ArrayList<>();
        mNotificationModelAdapter = new GroupModelAdapter(this, mNotificationList);
        mNotificationListView = (ListView) findViewById(R.id.notification_list_view);

        mNotificationListView.setAdapter(mNotificationModelAdapter);
        mNotificationListView.setOnItemClickListener(this);

    }

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
