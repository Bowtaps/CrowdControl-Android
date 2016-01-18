package com.bowtaps.crowdcontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bowtaps.crowdcontrol.model.ParseUserModel;
import com.parse.ParseUser;


public class SettingsActivity extends AppCompatActivity implements OnClickListener{

    Button mLogoutButton;

    /**
     *
     * @param savedInstanceState
     *
     * Sets up the SettingsActivity page and initializes the page elements. Also sets the listener
     * for clicks and currently has a "dummy" fab element that is currently included for example,
     * and to be repurposed later.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Get handles to Buttons
        mLogoutButton = (Button) findViewById(R.id.logout_button);

        // Declare button clicks
        mLogoutButton.setOnClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    /**
     *
     * @param view
     *
     * Handles the click event depending on the element that was clicked on. Currently only the
     * Logout button is clickable.
     */
    @Override
    public void onClick(View view) {
        // Handles clicks on items in view
        // in this case, either the facebook button or the create account button

        switch (view.getId()) {
            case R.id.logout_button:
                onLogoutButtonClick((Button) view);
                break;

            default:
                // Sorry, you're outta luck
                break;
        }
    }

    /**
     *
     * @param view
     *
     * Calls the launchLogoutActivity() method.
     */
    private void onLogoutButtonClick(Button view) {
        launchLogoutActivity();
    }

    /**
     * Retrieves the current user and logs them out. The intent is then set to direct to the SignupActivity
     * when the user is logged out.
     */
    //TODO: Should make sure to leave a group first before logging out.
    private void launchLogoutActivity() {
        ParseUserModel parseUserModel = new ParseUserModel(CrowdControlApplication.aUser);
        parseUserModel.logOutOfParseUser();

        Intent myIntent = new Intent(this, SignupActivity.class);
        this.startActivity(myIntent);
    }


}
