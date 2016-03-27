package com.bowtaps.crowdcontrol;

import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bowtaps.crowdcontrol.model.BaseModel;
import com.bowtaps.crowdcontrol.model.ParseGroupModel;
import com.bowtaps.crowdcontrol.model.ParseUserModel;
import com.parse.ParseUser;


public class SettingsActivity extends AppCompatActivity implements OnClickListener{

    Button mLogoutButton;
    private static final String TAG = SettingsActivity.class.getSimpleName();

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
                onLogoutButtonClick();
                break;

            default:
                // Sorry, you're outta luck
                break;
        }
    }

    /**
     *
     *
     *
     * Calls the launchLogoutActivity() method.
     */
    private void onLogoutButtonClick() {

        try {
            if(CrowdControlApplication.getInstance().getModelManager().getCurrentGroup() != null)
            {
                CrowdControlApplication.getInstance().getModelManager().getCurrentGroup().removeGroupMember(CrowdControlApplication.getInstance().getModelManager().getCurrentUser().getProfile());

                // Attempt to save change to group in background
                CrowdControlApplication.getInstance().getModelManager().getCurrentGroup().saveInBackground(new BaseModel.SaveCallback() {
                    @Override
                    public void doneSavingModel(BaseModel object, Exception ex) {

                        // Verify operation was a success
                        if (ex != null) {
                            Log.d(TAG, "Unable to save group");
                            return;
                        }

                        CrowdControlApplication.getInstance().getModelManager().setCurrentGroup(null);
                    }
                });
            }
            if (CrowdControlApplication.getInstance().getModelManager().logOutCurrentUser()) {
                launchLogoutActivity();
            } else {

                // Report error to log
                Log.d(TAG, "User logout failed");
            }
        } catch (Exception ex) {
            Log.d(TAG, "Failed to log user out");
        }
    }

    /**
     * Retrieves the current user and logs them out. The intent is then set to direct to the SignupActivity
     * when the user is logged out.
     */
    private void launchLogoutActivity() {

        //Todo move to messaging implentation
        //stop the messaging service
        stopService(new Intent(getApplicationContext(), MessageService.class));

        //Todo Question --- does this log out the ParseUser?!?!?

        Intent myIntent = new Intent(this, SignupActivity.class);
        this.startActivity(myIntent);
    }


}
