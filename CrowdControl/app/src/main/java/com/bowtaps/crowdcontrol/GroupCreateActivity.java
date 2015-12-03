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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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

    private void onCreateButtonClick(Button view) {
        attemptGroupCreate();
    }


    private  boolean isGroupNameValid(String groupname) {
        //TODO: What makes a valid group name??
        return true;
    }

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

        GroupCreateTask(String groupName, String groupDescription) {
            //Can add more attributes later
            mGroupName = groupName;
            mGroupDescription = groupDescription;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            // TODO: register the new group here.
            ParseObject group = new ParseObject("Group");
            ParseUser user = CrowdControlApplication.aUser;
            ParseObject member = new ParseObject("CCUser");

            member.put("DisplayName", user.get("CCUser") );
            group.put("GroupName", mGroupName);
            group.put("GroupDescription", mGroupDescription);

            ParseRelation relation = group.getRelation("GroupMembers");
            relation.add( member );


            ParseACL acl = new ParseACL();

            // Give public read access
            acl.setPublicReadAccess(true);
            group.setACL(acl);

            CrowdControlApplication.aGroup = group;
            group.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    finish();
                }
            });


            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
                launchTestActivity();
            } else {
                mGroupNameView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    /**
     * Launches the {@link GroupJoinActivity}.
     *
     * @see GroupJoinActivity
     */
    private void launchTestActivity() {
        Intent myIntent = new Intent(this, GroupNavigationActivity.class);
        this.startActivity(myIntent);
    }
}


