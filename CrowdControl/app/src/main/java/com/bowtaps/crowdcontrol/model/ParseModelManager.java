package com.bowtaps.crowdcontrol.model;

import android.os.AsyncTask;

import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * @author Daniel Andrus
 * @since 2016-01-17
 */
public class ParseModelManager implements ModelManager {

    private ParseUserModel currentUser;

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

        // Sign up new user using the Parse API
        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(username);
        parseUser.setPassword(password);
        parseUser.setEmail(email);
        parseUser.signUp();

        // Assuming no errors have occurred, bind new ParseUser to a new ParseUserModel
        ParseUserModel userModel = new ParseUserModel(parseUser);

        // Force a save to database, both for the new user and their profile
        userModel.getProfile().save();
        userModel.save();

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
    public UserModel getCurrentUser() {

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
