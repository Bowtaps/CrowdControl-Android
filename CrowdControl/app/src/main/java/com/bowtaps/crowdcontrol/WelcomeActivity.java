package com.bowtaps.crowdcontrol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    Button mButtonCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Get handles to buttons
        mButtonCreateAccount = (Button) findViewById(R.id.buttonToCreate);

        //Declare button clicks
        mButtonCreateAccount.setOnClickListener(this);
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
                onCreateAccountButtonClick((Button) view);
                break;

            default:
                // Sorry, you're outta luck
                break;
        }
    }

    /**
     * Handles clicks on the buttonToCreate button. Simply launches the {@link CreateAccountActivity}.
     *
     * @param button  The button object that was clicked.
     * @see           CreateAccountActivity
     */
    private void onCreateAccountButtonClick(Button button) {
        launchCreateAccoutButtonActivity();
    }

    /**
     * Launches the {@link CreateAccountActivity}.
     *
     * @see CreateAccountActivity
     */
    private void launchCreateAccoutButtonActivity() {
        Intent myIntent = new Intent(this, CreateAccountActivity.class);
        this.startActivity(myIntent);
    }

}