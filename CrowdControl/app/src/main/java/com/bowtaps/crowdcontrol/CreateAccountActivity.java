package com.bowtaps.crowdcontrol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {

    Button mButtonCreate;

    /*
     *  Initiats CreateAccountActivity and sets up the OnClickListener and button
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Get handles to buttons
        mButtonCreate = (Button) findViewById(R.id.buttonToGroupJoin);

        // Declare button clicks
        mButtonCreate.setOnClickListener(this);
    }

    /*
     *  Inflates the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_account, menu);
        return true;
    }

    /*
     *  Used to determine what menu Item was selected
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
     *  Uses a switch statement to determin which OnClickListiner button was selected
     *  and then does the action specific to the button.
     *
     *  @param button The button object that was clicked
     */
    @Override
    public void onClick(View view) {
        // Handles clicks onn items in view
        // in this case, either the facebook button or the create account button

        switch (view.getId()) {
            case R.id.buttonToGroupJoin:
                onCreateButtonClick((Button) view);
                break;

            default:
                // Sorry, you're outta luck
                break;
        }
    }

    /**
     * Handles clicks on the Create button. Simply launches the {@link GroupJoinActivity}.
     *
     * @param button  The button object that was clicked.
     * @see           GroupJoinActivity
     */
    private void onCreateButtonClick(Button button) {
        launchGroupJoinActivity();
    }


    /**
     * Launches the {@link GroupJoinActivity}.
     *
     * @see GroupJoinActivity
     */
    private void launchGroupJoinActivity() {
        Intent myIntent = new Intent(this, GroupJoinActivity.class);
        this.startActivity(myIntent);
    }
}
