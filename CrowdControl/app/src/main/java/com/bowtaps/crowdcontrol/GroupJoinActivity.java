package com.bowtaps.crowdcontrol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.bowtaps.crowdcontrol.adapters.CustomParseAdapter;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

/*
 * This Activity is where a user will either join an existing group or create a
 * new one.
 * This Activity will be the default view of all registered users that are not
 * in a group
 */
public class GroupJoinActivity extends AppCompatActivity implements View.OnClickListener {


    Button mButtonToTabs;

    // List view pieces
    private ParseQueryAdapter<ParseObject> mainAdapter;
    private CustomParseAdapter groupListAdapter;
    private ListView groupListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_join);

        // Initialize main ParseQueryAdapter
        mainAdapter = new ParseQueryAdapter<ParseObject>(this, "Group");
        mainAdapter.setTextKey("GroupName");
        //mainAdapter.setTextKey("GroupDescription");

        //mainAdapter.setImageKey("image");

        // Initialize the subclass of ParseQueryAdapter
        groupListAdapter = new CustomParseAdapter(this);

        // Initialize ListView and set initial view to mainAdapter
        groupListView = (ListView) findViewById(R.id.group_list);
        groupListView.setAdapter(groupListAdapter);
        mainAdapter.loadObjects(); // Querry in CustomParseAdapter



        // Get handles to Buttons
        mButtonToTabs = (Button) findViewById(R.id.buttonToTab);

        // Declare button clicks
        mButtonToTabs.setOnClickListener(this);
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

            default:
                // Sorry, you're outta luck
                break;
        }
    }

    /**
     * Handles clicks on the Facebook login button. Simply launches the {@link GroupNavigationActivity}.
     *
     * @param button  The button object that was clicked.
     * @see           GroupNavigationActivity
     */
    private void onCreateButtonClick(Button button) {
        launchTabActivity();
    }


    /**
     * Launches the {@link GroupJoinActivity}.
     *
     * @see GroupNavigationActivity
     */
    private void launchTabActivity() {
        Intent myIntent = new Intent(this, GroupCreateActivity.class);
        this.startActivity(myIntent);
    }
}
