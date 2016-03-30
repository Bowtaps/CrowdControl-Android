package com.bowtaps.crowdcontrol;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bowtaps.crowdcontrol.model.GroupModel;

/*
 *  Controller for the view that handels creating a group!!!
 */
public class GroupCreateActivity extends AppCompatActivity implements View.OnClickListener {

    Button mButtonGroupCreate;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private GroupCreateTask mAuthTask = null;

    // UI references.
    private EditText mGroupNameView;
    private EditText mGroupDesctiptionView;
    private View mProgressView;
    private View mLoginFormView;

    private static final String TAG = GroupCreateTask.class.getSimpleName();


    /*
     *  Sets up the Onclicklistener and handles for buttons in the layout
     *
     *  @param mButtonGroupCreate - button handle to create a group
     *  @param mGroupNameView - user input to name the group
     *  @param mGroupDesctiptionView - user input to describe the group
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);

        mButtonGroupCreate = (Button) findViewById(R.id.group_create_button);

        mButtonGroupCreate.setOnClickListener(this);

        mGroupNameView = (EditText) findViewById(R.id.group_name);
        mGroupDesctiptionView = (EditText) findViewById(R.id.group_description);

        mLoginFormView = findViewById(R.id.group_form);
        mProgressView = findViewById(R.id.group_progress);
    }

    /*
     * This is called when a button is clicked. It then determines which listener
     * was clicked and calls the code related to that button
     */
    @Override
    public void onClick(View view) {
        // Handles clicks on items in view
        // in this case, either the facebook button or the create account button

        switch (view.getId()) {
            case R.id.group_create_button:
                onCreateButtonClick((Button) view);
                break;

            default:
                // Sorry, you're outta luck
                break;
        }
    }

    /*
     *  Called when the Group Create Button is clicked
     *  it them attempts to make a new group
     *
     *  @see GroupNavigationActivity
     */
    private void onCreateButtonClick(Button view) {
        attemptGroupCreate();
    }

    /**
     * Handles the Set Group Location action.
     * @param view
     */
    private void onSetLoactionClick(Button view) {
        attemptSetLocation();
    }

    private void attemptSetLocation() {

    }


    /*
     *  Determines if the Group name is valid
     */
    private  boolean isGroupNameValid(String groupname) {
        //TODO: What makes a valid group name??
        return true;
    }

    /*
     * Determines if te Group Description is valid
     */
    private boolean isDescriptionValid(String group) {
        //TODO: Replace this with your own logic
        return true;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid groupname, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptGroupCreate() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mGroupNameView.setError(null);
        mGroupDesctiptionView.setError(null);

        // Store values at the time of the login attempt.
        String groupname = mGroupNameView.getText().toString();
        String groupDescription = mGroupDesctiptionView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid User Name.
        if (TextUtils.isEmpty(groupname)) {
            mGroupNameView.setError(getString(R.string.error_field_required));
            focusView = mGroupNameView;
            cancel = true;
        } else if (!isGroupNameValid(groupname)) {
            mGroupNameView.setError(getString(R.string.error_invalid_user_name));
            focusView = mGroupNameView;
            cancel = true;
        }

        // Check for a valid groupDescription, if the user entered one.
        if (TextUtils.isEmpty(groupDescription)) {
            mGroupDesctiptionView.setError(getString(R.string.error_field_required));
            focusView = mGroupDesctiptionView;
            cancel = true;
        } else if (!TextUtils.isEmpty(groupDescription) && !isDescriptionValid(groupDescription)) {
            mGroupDesctiptionView.setError(getString(R.string.error_invalid_password));
            focusView = mGroupDesctiptionView;
            cancel = true;
        }

        // Check for a valid groupname address.
        if (TextUtils.isEmpty(groupname)) {
            mGroupNameView.setError(getString(R.string.error_field_required));
            focusView = mGroupNameView;
            cancel = true;
        } else if (!isGroupNameValid(groupname)) {
            mGroupNameView.setError(getString(R.string.error_invalid_email));
            focusView = mGroupNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new GroupCreateTask(groupname, groupDescription);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Asynchronously registers a new group
     */
    public class GroupCreateTask extends AsyncTask<Void, Void, Boolean> {

        private final String mGroupName;
        private final String mGroupDescription;
        private GroupModel mGroupModel;


        public GroupCreateTask(String groupName, String groupDescription) {

            // Can add more attributes later
            mGroupName = groupName;
            mGroupDescription = groupDescription;
        }

        /*
         * uses the Group model to save the group to parse in the background
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            mGroupModel = null;
            try {
                mGroupModel = CrowdControlApplication.getInstance().getModelManager().createGroup(
                        CrowdControlApplication.getInstance().getModelManager().getCurrentUser().getProfile(),
                        mGroupName, mGroupDescription);
                return true;
            }
            catch (Exception ex){
                Log.d(TAG, "Unable to save new Group");
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                CrowdControlApplication.getInstance().getModelManager().setCurrentGroup(mGroupModel);
                launchGroupNavigationActivity();
                finish();
            } else {
                mGroupNameView.requestFocus();
            }
        }

        /*
         *  Lets the activity reset the form
         */
        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    /**
     * Launches the {@link GroupNavigationActivity}.
     *
     * @see GroupNavigationActivity
     */
    private void launchGroupNavigationActivity() {
        Intent myIntent = new Intent(this, GroupNavigationActivity.class);
        this.startActivity(myIntent);
    }

    private void launchGroupLocationActivity() {
        Intent myIntent = new Intent(this, GroupLocationActivity.class);
        this.startActivity(myIntent);
    }
}


