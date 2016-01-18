package com.bowtaps.crowdcontrol.model;

import android.content.Context;
import android.os.AsyncTask;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Parse implementation of the {@link ModelManager} interface.
 *
 * @author Daniel Andrus
 * @since 2016-01-17
 */
public class ParseModelManager implements ModelManager {

    /**
     * An internal reference to the current logged in user. May be {@code null} if no user is logged
     * in.
     */
    private ParseUserModel currentUser;



    /**
     * The default constructor for this class. Requires a context to initialize the Parse API under,
     * as well as application identifiers and client keys for authentication and verification.
     *
     * @param context The context under which to initialize the Parse API.
     * @param applicationId The identification code for the application to use when identifying with
     *                      Parse.
     * @param clientKey The client key for authenticating with Parse.
     */
    public ParseModelManager(Context context, String applicationId, String clientKey) {
        currentUser = null;

        Parse.enableLocalDatastore(context);
        Parse.initialize(context, applicationId, clientKey);
    }



    @Override
    public ParseUserModel logInUser(String username, String password) throws ParseException {

        // Log in user using the Parse API
        ParseUser parseUser = ParseUser.logIn(username, password);
        if (parseUser == null) {
            return null;
        }

        // Create new model using the Parse user
        ParseUserModel userModel = new ParseUserModel(parseUser);
        userModel.load();

        return userModel;
    }

    @Override
    public void logInUserInBackground(String username, String password, final BaseModel.LoadCallback callback) {

        // Define and execute an AsyncTask to perform the login in background
        new AsyncTask<Object, Void, ParseUserModel>() {

            private BaseModel.LoadCallback callback;
            private ParseException exception;

            @Override
            public void onPreExecute() {

                // Initialize variables
                exception = null;
            }

            @Override
            public ParseUserModel doInBackground(final Object ... params) {

                // Extract parameters to convenience variables
                String username = (String) params[0];
                String password = (String) params[1];
                this.callback = (BaseModel.LoadCallback) params[2];

                // Attempt login, catching and recording errors if encountered
                ParseUserModel userModel = null;
                try {
                    userModel = logInUser(username, password);
                } catch (ParseException ex) {
                    exception = ex;
                }

                return userModel;
            }

            @Override
            public void onPostExecute(ParseUserModel result) {

                // Execute callback if one is provided
                if (callback != null) {
                    callback.doneLoadingModel(result, exception);
                }
            }
        }.execute(username, password, callback);
    }

    @Override
    public ParseUserModel createUser(String username, String email, String password) throws ParseException {

        // Create new user model and set properties
        ParseUserModel userModel = ParseUserModel.createFromSignUp(username, password);
        userModel.setEmail(email);

        // Try saving everything
        ((ParseUser) userModel.parseObject).signUp();
        userModel.save();
        userModel.getProfile().save();

        // Return user once everything is saved
        return userModel;
    }

    @Override
    public void createUserInBackground(String username, String email, String password, final BaseModel.SaveCallback callback) {

        // Define and execute an AsyncTask to perform the login in background
        new AsyncTask<Object, Void, ParseUserModel>() {

            private BaseModel.SaveCallback callback;
            private ParseException exception;

            @Override
            public void onPreExecute() {

                // Initialize variables
                exception = null;
            }

            @Override
            public ParseUserModel doInBackground(final Object ... params) {

                // Extract parameters to convenience variables
                String username = (String) params[0];
                String email = (String) params[1];
                String password = (String) params[2];
                this.callback = (BaseModel.SaveCallback) params[3];

                // Attempt login, catching and recording errors if encountered
                ParseUserModel userModel = null;
                try {
                    userModel = createUser(username, email, password);
                } catch (ParseException ex) {
                    exception = ex;
                }

                return userModel;
            }

            @Override
            public void onPostExecute(ParseUserModel result) {

                // Execute callback if one is provided
                if (callback != null) {
                    callback.doneSavingModel(result, exception);
                }
            }
        }.execute(username, email, password, callback);
    }

    @Override
    public ParseUserModel getCurrentUser() {

        // Verify property is in sync with Parse
        if ((currentUser == null && ParseUser.getCurrentUser() != null)
            || !currentUser.equals(ParseUser.getCurrentUser())) {
            currentUser = new ParseUserModel(ParseUser.getCurrentUser());
        }

        return currentUser;
    }

    @Override
    public Boolean logOutCurrentUser() throws ParseException {

        // Verify user is already logged in
        if (getCurrentUser() == null) {
            return false;
        }

        // Log the user out
        ParseUser.logOut();
        currentUser = null;

        return true;
    }
}
