package com.bowtaps.crowdcontrol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class LoginChoiceActivity extends AppCompatActivity implements View.OnClickListener {

    Button mButtonFacebook;
    Button mButtonCrowdControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_choice);

        // Get handles to buttons
        mButtonFacebook = (Button) findViewById(R.id.login_choice_facebook_button);
        mButtonCrowdControl = (Button) findViewById(R.id.login_choice_crowd_control_button);

        // Declare this activity to handle button clicks
        mButtonFacebook.setOnClickListener(this);
        mButtonCrowdControl.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_in, menu);
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
            case R.id.login_choice_facebook_button:
                onFacebookButtonClick((Button) view);
                break;

            case R.id.login_choice_crowd_control_button:
                onCreateAccountButtonClick((Button) view);
                break;

            default:
                // Sorry, you're outta luck
                break;
        }
    }

    /**
     * Handles clicks on the Facebook login button. Simply launches the {@link LoginActivity}.
     *
     * @param button  The button object that was clicked.
     * @see           LoginActivity
     */
    private void onFacebookButtonClick(Button button) {
        launchLoginActivity();
    }

    /**
     * Handles clicks on the Facebook login button. Simply launches the {@link LoginActivity}.
     *
     * @param button  The button object that was clicked.
     * @see           LoginActivity
     */
    private void onCreateAccountButtonClick(Button button) {
        launchLoginActivity();
    }

    /**
     * Launches the {@link LoginActivity}.
     *
     * @see LoginActivity
     */
    private void launchLoginActivity() {
        Intent myIntent = new Intent(this, LoginActivity.class);
        this.startActivity(myIntent);
    }

}
