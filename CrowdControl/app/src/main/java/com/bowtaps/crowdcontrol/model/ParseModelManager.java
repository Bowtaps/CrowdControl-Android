package com.bowtaps.crowdcontrol.model;

import android.content.Context;
import android.os.AsyncTask;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

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

    private ParseGroupModel currentGroup;



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
        if (userModel.getProfile().wasModified()) {
            userModel.getProfile().save();
            userModel.save();
        } else {
            userModel.getProfile().load();
        }

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
        userModel.getProfile().save();
        ((ParseUser) userModel.parseObject).signUp();
        userModel.save();

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
            || (currentUser != null && !currentUser.equals(ParseUser.getCurrentUser()))) {
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

    @Override
    public List<ParseGroupModel> fetchAllGroups() throws Exception {
        return ParseGroupModel.getAll();
    }

    @Override
    public void fetchAllGroupsInBackground(final BaseModel.FetchCallback callback) {

        // Define and execute an AsyncTask to perform the background fetch
        new AsyncTask<Object, Void, List<ParseGroupModel>>() {

            private BaseModel.FetchCallback callback;
            private ParseException exception;

            @Override
            public void onPreExecute() {

                // Initialize variables
                exception = null;
            }

            @Override
            public List<ParseGroupModel> doInBackground(final Object ... params) {

                // Extract parameters to convenience variables
                this.callback = (BaseModel.FetchCallback) params[0];

                // Attempt fetch from storage
                List<ParseGroupModel> result = null;
                try {
                    result = ParseGroupModel.getAll();
                } catch (ParseException ex) {
                    exception = ex;
                }

                return result;
            }

            @Override
            public void onPostExecute(List<ParseGroupModel> result) {

                // Execute callback if one is provided
                if (callback != null) {
                    callback.doneFetchingModels(result, exception);
                }
            }
        }.execute(callback);
    }

    @Override
    public GroupModel getCurrentGroup() {
        return currentGroup;
    }

    @Override
    public void setCurrentGroup(GroupModel group) {

        // Verify parameter
        if (group == null) {
            currentGroup = null;
        } else if (group instanceof ParseGroupModel) {
            currentGroup = (ParseGroupModel) group;
        } else {
            throw new IllegalArgumentException("group parameter must be an instance of ParseGroupModel");
        }
    }

    @Override
    public GroupModel fetchCurrentGroup() throws Exception {

        // Verify user is logged in
        ParseUserModel userModel = getCurrentUser();
        if (userModel != null) {
            setCurrentGroup(ParseGroupModel.getGroupContainingUser(userModel.getProfile()));
        } else {
            setCurrentGroup(null);
        }

        return getCurrentGroup();
    }

    @Override
    public void fetchCurrentGroupInBackground(final BaseModel.LoadCallback callback) {

        // Verify user is logged in
        ParseUserModel userModel = getCurrentUser();
        if (userModel == null) {
            setCurrentGroup(null);

            // Return control to calling thread
            if (callback != null) {
                callback.doneLoadingModel(getCurrentGroup(), null);
            }
            return;
        }

        // Define and execute an AsyncTask to perform the background fetch
        new AsyncTask<Object, Void, ParseGroupModel>() {

            private BaseModel.LoadCallback callback;
            private ParseException exception;

            @Override
            public void onPreExecute() {

                // Initialize variables
                exception = null;
            }

            @Override
            public ParseGroupModel doInBackground(final Object ... params) {

                // Extract parameters to convenience variables
                ParseUserProfileModel profileModel = (ParseUserProfileModel) params[0];
                this.callback = (BaseModel.LoadCallback) params[1];

                ParseGroupModel result = null;

                // Attempt fetch from storage
                try {
                    result = ParseGroupModel.getGroupContainingUser(profileModel);
                } catch (ParseException ex) {
                    exception = ex;
                }

                return result;
            }

            @Override
            public void onPostExecute(ParseGroupModel result) {

                // Execute callback if one is provided
                if (callback != null) {
                    callback.doneLoadingModel(result, exception);
                }
            }
        }.execute(userModel.getProfile(), callback);
    }
}
