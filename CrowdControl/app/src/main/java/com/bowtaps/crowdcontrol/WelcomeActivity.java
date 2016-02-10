package com.bowtaps.crowdcontrol;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.bowtaps.crowdcontrol.model.ParseUserModel;
import com.bowtaps.crowdcontrol.model.ParseUserProfileModel;
import com.parse.ParseUser;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/*
 *  This Activity is the first thing launched by the app.
 *  It determines if the user is logged in and either launches
 *  the {@Link SignupActivity} or launches the {@Link GroupJoinActivity}
 *
 *  @see SignupActivity
 *  @see GroupJoinActivity
 */
public class WelcomeActivity extends AppCompatActivity
        implements View.OnClickListener {

    Button mButtonCreateAccount;
    int REQUEST_FINE_LOCATION = 0;
    private AutoCompleteTextView mLocationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mayRequestLocation();
        // Get handles to buttons
        mButtonCreateAccount = (Button) findViewById(R.id.buttonToCreate);

        //Declare button clicks
        mButtonCreateAccount.setOnClickListener(this);

        mLocationView = (AutoCompleteTextView) findViewById(R.id.location);
        mayRequestLocation();


        //This Determines if a user is logged in from a previous run
        if (CrowdControlApplication.getInstance().getModelManager().getCurrentUser() != null) {

            // User is already logged in, direct them to application
            launchGroupJoinActivity();
            finish();

        } else {

            // Send user to screen to log in
            launchLoginActivity();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
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
        // Handles clicks onn items in view
        // in this case, either the facebook button or the create account button

        switch (view.getId()) {
            case R.id.buttonToCreate:
                onCreateAccountButtonClick();
                break;

            default:
                // Sorry, you're outta luck
                break;
        }
    }

    /**
     * Handles clicks on the buttonToCreate button. Simply launches the {@link SignupActivity}.
     *
     * @see           SignupActivity
     */
    private void onCreateAccountButtonClick() {
        launchLoginActivity();
    }

    /**
     * Launches the {@link SignupActivity}.
     *
     * @see SignupActivity
     */
    private void launchLoginActivity() {
        Intent myIntent = new Intent(this, SignupActivity.class);
        this.startActivity(myIntent);
    }


    /**
     * Launches the {@link GroupJoinActivity}.
     *
     * @see GroupJoinActivity
     */
    private void launchGroupJoinActivity() {
        Intent myIntent = new Intent(this,GroupJoinActivity.class);
        this.startActivity(myIntent);
    }
    private void mayRequestLocation() {
        Log.d("Location Permissions", "In mayRequestLocation");
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            Log.d("Location Permissions", "First if");
            CrowdControlApplication.getInstance().getLocationManager().initializeLocationRequest();
            return;
        }
        if(checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("Location Permissions", "We have permission");
            CrowdControlApplication.getInstance().getLocationManager().initializeLocationRequest();
            return;
        }
        if(shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)){
            Snackbar.make(mLocationView, "Location is required for use in this app", Snackbar.LENGTH_INDEFINITE)

                    .setAction(android.R.string.ok, new View.OnClickListener() {

                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View v) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_FINE_LOCATION);
                            CrowdControlApplication.getInstance().getLocationManager().initializeLocationRequest();
                        }
                    });
        } else{
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_FINE_LOCATION);
            CrowdControlApplication.getInstance().getLocationManager().initializeLocationRequest();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_FINE_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Location Permissions", "user supplied");
                CrowdControlApplication.getInstance().getLocationManager().initializeLocationRequest();
            }
        }
    }

}
