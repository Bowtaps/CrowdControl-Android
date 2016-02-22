package com.bowtaps.crowdcontrol.model;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bowtaps.crowdcontrol.CrowdControlApplication;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
     * An internal reference to the group to which the currently logged-in user belongs. May be
     * {@code null} if no user is logged in or if they do not belong to any group.
     */
    private ParseGroupModel currentGroup;

    /**
     * Internal cache for created models.
     */
    private Map<String, ParseBaseModel> cachedModels;



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
        currentGroup = null;
        cachedModels = new HashMap<>();

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
        ParseUserProfileModel profileModel = userModel.getProfile();

        profileModel.load();

        userModel = (ParseUserModel) updateCache(userModel);
        profileModel = (ParseUserProfileModel) updateCache(profileModel);
        userModel.setProfile(profileModel);

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

                // Update cache
                if (result != null) {
                    result = (ParseUserModel) updateCache(result);
                }

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
        ((ParseUser) userModel.getParseObject()).signUp();
        userModel.save();
        userModel = (ParseUserModel) updateCache(userModel);
        userModel.setProfile((ParseUserProfileModel) updateCache(userModel.getProfile()));

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

                // Update cache
                if (result != null) {
                    result = (ParseUserModel) updateCache(result);
                }

                // Execute callback if one is provided
                if (callback != null) {
                    callback.doneSavingModel((ParseUserModel) updateCache(result), exception);
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
        List<ParseGroupModel> groups = ParseGroupModel.getAll();
        for (int i = 0; i < groups.size(); i++) {
            groups.set(i, (ParseGroupModel) updateCache(groups.get(i)));
        }
        return groups;
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

                // Update cache
                if (result != null) {
                    for (int i = 0; i < result.size(); i++) {
                        result.set(i, (ParseGroupModel) updateCache(result.get(i)));
                    }
                }

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
            setCurrentGroup((ParseGroupModel) updateCache(ParseGroupModel.getGroupContainingUser(userModel.getProfile())));
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

                // Check against cache
                if (result != null) {
                    result = (ParseGroupModel) updateCache(result);
                }

                setCurrentGroup(result);

                // Execute callback if one is provided
                if (callback != null) {
                    callback.doneLoadingModel(result, exception);
                }
            }
        }.execute(userModel.getProfile(), callback);
    }

    @Override
    public ParseGroupModel createGroup(UserProfileModel leader, String name, String description) throws Exception {
        // Validate parameters
        if (leader != null && !(leader instanceof ParseUserProfileModel)) {
            throw new IllegalArgumentException("leader parameter must be an instance of ParseUserProfileModel");
        }

        ParseGroupModel groupModel = new ParseGroupModel();
        groupModel.setGroupLeader(leader);
        groupModel.addGroupMember(leader);
        groupModel.setGroupName(name);
        groupModel.setGroupDescription(description);
        groupModel.save();
        groupModel = (ParseGroupModel) updateCache(groupModel);
        return groupModel;
    }

    @Override
    public void createGroupInBackground(UserProfileModel leader, String name, String description, final BaseModel.SaveCallback callback) {
        // Validate parameters
        if (leader != null && !(leader instanceof ParseUserProfileModel)) {
            throw new IllegalArgumentException("leader parameter must be an instance of ParseUserProfileModel");
        }

        // Define and execute an AsyncTask to perform the background create
        new AsyncTask<Object, Void, ParseGroupModel>() {

            private BaseModel.SaveCallback callback;
            private Exception exception;

            @Override
            public void onPreExecute() {

                // Initialize variables
                exception = null;
            }

            @Override
            public ParseGroupModel doInBackground(final Object ... params) {

                // Extract parameters to convenience variables
                ParseUserProfileModel profileModel = (ParseUserProfileModel) params[0];
                String name = (String) params[1];
                String description = (String) params[2];
                this.callback = (BaseModel.SaveCallback) params[3];

                ParseGroupModel result = null;

                // Attempt fetch from storage
                try {
                    result = createGroup(profileModel, name, description);
                } catch (Exception ex) {
                    exception = ex;
                }

                return result;
            }

            @Override
            public void onPostExecute(ParseGroupModel result) {

                // Update cache
                if (result != null) {
                    result = (ParseGroupModel) updateCache(result);
                }

                // Execute callback if one is provided
                if (callback != null) {
                    callback.doneSavingModel(updateCache(result), exception);
                }
            }
        }.execute(leader, name, description, callback);
    }

    @Override
    public List<ParseBaseModel> fetchGroupUpdates(String groupId, String userPId, Date since) throws ParseException {

        // Verify parameters
        if (groupId == null) {
            throw new IllegalArgumentException("parameter 'groupId' cannot be null");
        }
        if (userPId == null) {
            throw new IllegalArgumentException("parameter 'userPId' cannot be null");
        }
        if (since == null) {
            throw new IllegalArgumentException("parameter 'since' cannot be null");
        }

        // Our list that will contain our results
        List<ParseBaseModel> results = new LinkedList<>();

        // Construct parameter hash map
        HashMap<String, Object> params = new HashMap<>();
        params.put("group", groupId);
        params.put("userProfile", userPId);
        params.put("timestamp", since);

        // Call cloud code
        List<Object> parseResults = ParseCloud.callFunction("fetchGroupUpdates", params);

        List<ParseUserProfileModel> groupMembers = new ArrayList<>();
        ParseGroupModel groupModel = null;

        // Process results
        for (Object result : parseResults) {

            // Ignore non-ParseObject objects
            if (!(result instanceof ParseObject)) continue;

            // Cast to more convenient type
            ParseObject parseResult = (ParseObject) result;

            // Build ParseModel using the given ParseObject of unknown origin
            ParseBaseModel model = getModelFromParseObject(parseResult);
            if (model == null) {
                // never mind
            } else if (model instanceof ParseLocationModel) {
                results.add(model);
            } else if (model instanceof ParseGroupModel) {
                groupModel = (ParseGroupModel) model;
            } else if (model instanceof ParseUserProfileModel) {
                groupMembers.add((ParseUserProfileModel) model);
            }
        }

        // Create group object using received member objects
        if (groupModel != null) {
            groupModel.clearGroupMembers();
            groupModel.addGroupMembers(groupMembers);
            results.add(groupModel);
        }

        return results;
    }

    /**
     * Returns the model stored in cache matching the given model's object ID. If no such object is
     * stored in cache, then the provided model will be placed in cache and will be returned.
     *
     * @param model The model object to compare against in cache.
     *
     * @return Returns the model stored in cache.
     */
    private ParseBaseModel updateCache(ParseBaseModel model) {
        if (model == null) return null;

        ParseBaseModel cachedModel;

        if (cachedModels.containsKey(model.getId())) {
            cachedModel = cachedModels.get(model.getId());
            if (cachedModel != model) {
                cachedModel.setUnderlyingParseObject(model.getParseObject());
            }
        } else {
            cachedModels.put(model.getId(), model);
            cachedModel = model;
        }

        return cachedModel;
    }

    /**
     * Gets an object from cache or creates a corresponding model.
     *
     * @param parseObject The ParseObject to check against cache.
     *
     * @return The model that has been created or found in cache.
     */
    private ParseBaseModel getModelFromParseObject(ParseObject parseObject) {
        if (parseObject == null) return null;

        ParseBaseModel model;

        // Check cache
        if (!cachedModels.containsKey(parseObject.getObjectId())) {

            // Try building a group model
            model = ParseGroupModel.createFromParseObject(parseObject);

            // Try building a location model
            if (model == null) {
                model = ParseLocationModel.createFromParseObject(parseObject);
            }

            // Try building a user model
            if (model == null) {
                model = ParseUserModel.createFromParseObject(parseObject);
            }

            // Try building a user profile model
            if (model == null) {
                model = ParseUserProfileModel.createFromParseObject(parseObject);
            }

            // Put object in cache if a matching model was found
            if (model != null) {
                cachedModels.put(parseObject.getObjectId(), model);
            }
        } else {

            // Just fetch model from cache
            model = cachedModels.get(parseObject.getObjectId());

            // Update underlying ParseObject if necessary
            if (model.getParseObject() != parseObject && !model.getParseObject().equals(parseObject)) {
                model.setUnderlyingParseObject(parseObject);
            }
        }

        return model;
    }
}
