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

    private void onLogoutButtonClick(Button view) {
        launchLogoutActivity();
    }

    //TODO: Should make sure to leave a group first before logging out.
    private void launchLogoutActivity() {
        ParseUserModel parseUserModel = new ParseUserModel(CrowdControlApplication.aUser);
        parseUserModel.logOutOfParseUser();

        Intent myIntent = new Intent(this, SignupActivity.class);
        this.startActivity(myIntent);
    }


}
