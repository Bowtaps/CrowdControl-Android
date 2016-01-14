package com.bowtaps.crowdcontrol;

import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.bowtaps.crowdcontrol.adapters.CustomParseAdapter;
import com.bowtaps.crowdcontrol.model.BaseModel;
import com.bowtaps.crowdcontrol.model.ParseGroupModel;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

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
    private ParseQueryAdapter<ParseObject> mMainAdapter;
    private CustomParseAdapter mGroupListAdapter;
    private ListView mGroupListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_join);

        // Initialize main ParseQueryAdapter
        mMainAdapter = new ParseQueryAdapter<ParseObject>(this, "Group");
        mMainAdapter.setTextKey("GroupName");

        //mMainAdapter.setImageKey("image");

        // Initialize the subclass of ParseQueryAdapter
        mGroupListAdapter = new CustomParseAdapter(this);

        // Initialize ListView and set initial view to mMainAdapter
        mGroupListView = (ListView) findViewById(R.id.group_list);
        mGroupListView.setAdapter(mGroupListAdapter);

        mGroupListView.setOnItemClickListener(this);

        mMainAdapter.loadObjects(); // Querry in CustomParseAdapter

        // Get handles to Buttons
        mButtonToTabs = (Button) findViewById(R.id.buttonToTab);
        mButtonSettings = (Button) findViewById(R.id.buttonSettings);
        // Declare button clicks
        mButtonToTabs.setOnClickListener(this);
        mButtonSettings.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_join, menu);
        return true;
    }

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

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id){
        CrowdControlApplication.aGroup = mGroupListAdapter.getItem(position);
        ParseGroupModel parseGroupModel = new ParseGroupModel(mGroupListAdapter.getItem(position));
        parseGroupModel.AddNewMember(CrowdControlApplication.aProfile);

        parseGroupModel.saveInBackground(new BaseModel.SaveCallback() {
            @Override
            public void doneSavingModel(BaseModel object, Exception ex) {
                //TODO catch ex for error checking
                finish();
            }
        });

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

    private void onSettingsButtonClick(Button button) {
        launchSettingsActivity();
    }

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

    private void launchGroupNavigationActivity() {
        Intent myIntent = new Intent(this, GroupNavigationActivity.class);
        this.startActivity(myIntent);
    }
}
