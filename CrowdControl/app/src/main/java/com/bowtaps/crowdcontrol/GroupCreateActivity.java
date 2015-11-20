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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class GroupCreateActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private GroupCreateTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mGroupCreateView;
    private AutoCompleteTextView mGroupDescriptionView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        // Set up the login form.
        mGroupCreateView = (AutoCompleteTextView) findViewById(R.id.group_name);
        mGroupDescriptionView = (AutoCompleteTextView) findViewById(R.id.group_description);

        Button mEmailSignInButton = (Button) findViewById(R.id.group_create_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    /**
     * Attempts to register the group specified by the login form.
     * If there are form errors (invalid group, missing fields, etc.), the
     * errors are presented and no actual registration attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mGroupCreateView.setError(null);
        mGroupDescriptionView.setError(null);

        // Store values at the time of the login attempt.
        String groupName = mGroupCreateView.getText().toString();
        String groupDescription = mGroupDescriptionView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid Group Name.
        if (TextUtils.isEmpty(groupName)) {
            mGroupCreateView.setError(getString(R.string.error_field_required));
            focusView = mGroupCreateView;
            cancel = true;
        } else if (!isGroupNameValid(groupName)) {
            mGroupCreateView.setError(getString(R.string.error_invalid_user_name));
            focusView = mGroupCreateView;
            cancel = true;
        }

        // Check for a valid groupDescription.
        if (TextUtils.isEmpty(groupDescription)) {
            mGroupDescriptionView.setError(getString(R.string.error_field_required));
            focusView = mGroupDescriptionView;
            cancel = true;
        } else if (!isDescriptionValid(groupDescription)) {
            mGroupDescriptionView.setError(getString(R.string.error_invalid_email));
            focusView = mGroupDescriptionView;
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
            mAuthTask = new GroupCreateTask(groupName, groupDescription);
            mAuthTask.execute((Void) null);
        }
    }

    private  boolean isGroupNameValid(String groupname) {
        //TODO: What makes a valid groupname??
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
     * Asynchronously registers a new group
     */
    public class GroupCreateTask extends AsyncTask<Void, Void, Boolean> {

        private final String mGroupName;
        private final String mGroupDescription;

        GroupCreateTask(String groupName, String groupDescription) {
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
            ParseUser user = new ParseUser();
            user.getCurrentUser( );
            group.put("GroupName", mGroupName);
            group.put("GroupDescription", mGroupDescription);
            
            group.saveInBackground();
//                public void done(ParseException e) {
//                    if (e == null) {
//                        // Hooray! Let them use the app now.
//                    } else {
//                        // Sign up didn't succeed. Look at the ParseException
//                        // to figure out what went wrong
//                    }
//                }
//            });

            launchTestActivity();

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    /**
     * Launches the {@link groupJoin}.
     *
     * @see groupJoin
     */
    private void launchTestActivity() {
        Intent myIntent = new Intent(this, GroupNavigationActivity.class);
        this.startActivity(myIntent);
    }
}


