package com.bowtaps.crowdcontrol.model;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
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
     * The singleton instance of this class. Can be retrieved by calling {@link #getInstance()}.
     */
    private static ParseModelManager instance;


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

        instance = this;
    }

    /**
     * Gets the singleton instance of this class.
     *
     * @return The singleton instance of this class.
     */
    protected static ParseModelManager getInstance() {
        return instance;
    }


    /**
     * @see ModelManager#deleteModel(BaseModel)
     */
    @Override
    public void deleteModel(BaseModel model) throws ParseException {

        // Verify parameters
        if (model == null) {
            throw new IllegalArgumentException("model cannot be null");
        } else if (!(model instanceof ParseBaseModel)) {
            throw new IllegalArgumentException("model must be instance of type ParseBaseModel");
        }

        ParseBaseModel parseModel = (ParseBaseModel) model;

        // Remove from cache
        if (cachedModels.containsKey(parseModel.getId())) {
            cachedModels.remove(parseModel.getId());
        }

        // Delete from storage
        parseModel.delete();
    }

    /**
     * @see ModelManager#deleteModelInBackground(BaseModel, BaseModel.DeleteCallback)
     */
    @Override
    public void deleteModelInBackground(BaseModel model, final BaseModel.DeleteCallback callback) {

        // Verify parameters
        if (model == null) {
            throw new IllegalArgumentException("model cannot be null");
        } else if (!(model instanceof ParseBaseModel)) {
            throw new IllegalArgumentException("model must be instance of type ParseBaseModel");
        }

        ParseBaseModel parseModel = (ParseBaseModel) model;

        // Remove from cache
        if (cachedModels.containsKey(parseModel.getId())) {
            cachedModels.remove(parseModel.getId());
        }

        // Delete from storage
        parseModel.deleteInBackground(callback);
    }

    /**
     * @see ModelManager#logInUser(String, String)
     */
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

    /**
     * @see ModelManager#logInUserInBackground(String, String, BaseModel.LoadCallback)
     */
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

    /**
     * @see ModelManager#createUser(String, String, String)
     */
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

    /**
     * @see ModelManager#createUserInBackground(String, String, String, BaseModel.SaveCallback)
     */
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

    /**
     * @see ModelManager#getCurrentUser()
     */
    @Override
    public ParseUserModel getCurrentUser() {

        // Verify property is in sync with Parse
        if ((currentUser == null && ParseUser.getCurrentUser() != null)
            || (currentUser != null && !currentUser.equals(ParseUser.getCurrentUser()))) {
            currentUser = new ParseUserModel(ParseUser.getCurrentUser());
            currentUser = (ParseUserModel) updateCache(currentUser);
            currentUser.setProfile((ParseUserProfileModel) updateCache(currentUser.getProfile()));
        }

        return currentUser;
    }

    /**
     * @see ModelManager#logOutCurrentUser()
     */
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

    /**
     * @see ModelManager#fetchAllGroups()
     */
    @Override
    public List<ParseGroupModel> fetchAllGroups() throws Exception {
        List<ParseGroupModel> groups = ParseGroupModel.getAll();
        for (int i = 0; i < groups.size(); i++) {
            groups.set(i, (ParseGroupModel) updateCache(groups.get(i)));
        }
        return groups;
    }

    /**
     * @see ModelManager#fetchAllGroupsInBackground(BaseModel.FetchCallback)
     */
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

    /**
     * @see ModelManager#fetchAllGroupsInBackground(BaseModel.FetchCallback)
     */
    @Override
    public void fetchNotificationsInBackground(final BaseModel.FetchCallback callback) {

        // Define and execute an AsyncTask to perform the background fetch
        new AsyncTask<Object, Void, List<ParseInvitationModel>>() {

            private BaseModel.FetchCallback callback;
            private ParseException exception;

            @Override
            public void onPreExecute() {

                // Initialize variables
                exception = null;
            }

            @Override
            public List<ParseInvitationModel> doInBackground(final Object ... params) {

                // Extract parameters to convenience variables
                this.callback = (BaseModel.FetchCallback) params[0];

                // Attempt fetch from storage
                List<ParseInvitationModel> result = null;
                try {
                    result = ParseInvitationModel.getAll();
                } catch (ParseException ex) {
                    exception = ex;
                }

                return result;
            }

            @Override
            public void onPostExecute(List<ParseInvitationModel> result) {

                // Update cache
                if (result != null) {
                    for (int i = 0; i < result.size(); i++) {
                        result.set(i, (ParseInvitationModel) updateCache(result.get(i)));
                    }
                }

                // Execute callback if one is provided
                if (callback != null) {
                    callback.doneFetchingModels(result, exception);
                }
            }
        }.execute(callback);
    }

    /**
     * @see ModelManager#getCurrentGroup()
     */
    @Override
    public ParseGroupModel getCurrentGroup() {
        return currentGroup;
    }

    /**
     * @see ModelManager#setCurrentGroup(GroupModel)
     */
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

    /**
     * @see ModelManager#fetchCurrentGroup()
     */
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

    /**
     * @see ModelManager#fetchCurrentGroupInBackground(BaseModel.LoadCallback)
     */
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

    /**
     * @see ModelManager#createGroup(UserProfileModel, String, String)
     */
    @Override
    public ParseGroupModel createGroup(UserProfileModel leader, String name, String description) throws Exception {

        // Validate parameters
        if (leader != null && !(leader instanceof ParseUserProfileModel)) {
            throw new IllegalArgumentException("leader parameter must be an instance of ParseUserProfileModel");
        }

        // Create and save group
        ParseGroupModel groupModel = new ParseGroupModel();
        groupModel.setGroupLeader(leader);
        groupModel.addGroupMember(leader);
        groupModel.setGroupName(name);
        groupModel.setGroupDescription(description);
        groupModel.save();
        groupModel = (ParseGroupModel) updateCache(groupModel);

        // Create and save a new conversation model
        ParseConversationModel conversation = createConversation(groupModel);
        conversation.addParticipant(leader);
        conversation.save();
        conversation = (ParseConversationModel) updateCache(conversation);
        groupModel.addCachedConversation(conversation);

        return groupModel;
    }

    /**
     * @see ModelManager#createGroupInBackground(UserProfileModel, String, String, BaseModel.SaveCallback)
     */
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

    /**
     * @see ModelManager#fetchGroupUpdates(String, String, Date)
     */
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
        List<ParseConversationModel> groupConversations = new ArrayList<>();
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
            } else if (model instanceof ParseConversationModel) {
                groupConversations.add((ParseConversationModel) model);
            }
        }

        // Create group object using received member objects
        if (groupModel != null) {
            groupModel.clearGroupMembers();
            groupModel.addGroupMembers(groupMembers);
            for (ParseConversationModel conversationModel : groupConversations) {
                groupModel.addCachedConversation(conversationModel);
            }
            results.add(groupModel);
        }

        return results;
    }

    /**
      * @see ModelManager#createLocation(UserProfileModel, UserProfileModel, LatLng)
     */
    @Override
    public ParseLocationModel createLocation(UserProfileModel to, UserProfileModel from, LatLng location){
        ParseLocationModel loc;
        try {
            loc = ParseLocationModel.createLocationModel(to, from, location);
        }catch (IllegalArgumentException e1){
            Log.e("Create Location Error", e1.toString());
            loc = null;
        }
        if(loc != null) {
            loc = (ParseLocationModel) updateCache(loc);
        }
        return loc;
    }

    /**
     * @see ModelManager#fetchLocationsFromUser(UserProfileModel)
     */
    @Override
    public List<ParseLocationModel> fetchLocationsFromUser(UserProfileModel user) throws ParseException {
        List<ParseLocationModel> locationModels;
        int i = 0;
        //Get the location models from parse
        locationModels = ParseLocationModel.fetchLocationsSentFromUser(user);
        for(i=0;i<locationModels.size();i++){
            locationModels.set(i, (ParseLocationModel) updateCache(locationModels.get(i)));
        }
        return locationModels;
    }

    /**
     * @see ModelManager#fetchLocationsToUser(UserProfileModel)
     */
    @Override
    public List<ParseLocationModel> fetchLocationsToUser(UserProfileModel user) throws ParseException{
        List<ParseLocationModel> locationModels;
        int i = 0;
        locationModels = ParseLocationModel.fetchLocationsSentToUser(user);
        for(i = 0; i < locationModels.size(); i++){
            locationModels.set(i, (ParseLocationModel) updateCache(locationModels.get(i)));
        }
        return locationModels;
    }


    /**
     * @see ModelManager#createConversation(GroupModel)
     */
    @Override
    public ParseConversationModel createConversation(GroupModel group) throws ParseException {

        // Verify parameters
        if (group == null) {
            throw new IllegalArgumentException("Argument 1 cannot be null");
        }
        if (!(group instanceof ParseGroupModel)) {
            throw new IllegalArgumentException("Argument 1 must be an instance of ParseGroupModel");
        }

        // Create and save the conversation
        ParseConversationModel conversation = ParseConversationModel.create((ParseGroupModel) group);
        conversation.saveInBackground(null);
        conversation = (ParseConversationModel) updateCache(conversation);

        return conversation;
    }

    /**
     * @see ModelManager#fetchConversations()
     */
    @Override
    public List<? extends ParseConversationModel> fetchConversations() throws ParseException {

        // Verify that current user and current group are not null
        if (getCurrentGroup() == null || getCurrentUser() == null) {
            return new ArrayList<>();
        }

        return fetchConversationsForGroupAndUser(getCurrentGroup(), getCurrentUser().getProfile());
    }

    /**
     * @see ModelManager#fetchConversationsForGroupAndUser(GroupModel, UserProfileModel)
     */
    @Override
    public List<? extends ParseConversationModel> fetchConversationsForGroupAndUser(GroupModel group, UserProfileModel user) throws ParseException {

        // Verify parameters
        if (group == null) {
            throw new IllegalArgumentException("Parameter 1 cannot be null");
        }
        if (!(group instanceof ParseGroupModel)) {
            throw new IllegalArgumentException("Parameter 1 must be an instance of ParseGroupModel");
        }
        if (user == null) {
            throw new IllegalArgumentException("Parameter 2 cannot be null");
        }
        if (!(user instanceof ParseUserProfileModel)) {
            throw new IllegalArgumentException("Parameter 2 must be an instance of ParseUserProfileModel");
        }

        List<ParseConversationModel> results = ParseConversationModel.fetchConversationsForGroupAndUser((ParseGroupModel) group, (ParseUserProfileModel) user);

        // Update cache
        for (int i = 0; i < results.size(); i++) {
            results.set(i, (ParseConversationModel) updateCache(results.get(i)));
        }

        return results;
    }

    /**
     * @see ModelManager#createMessage(String, Date, ConversationModel, String)
     */
    @Override
    public List<? extends ParseMessageModel> createMessage(String messageId, Date timestamp, ConversationModel conversation, String message) {
        List<ParseMessageModel> messages = new ArrayList<>();
        ParseUserProfileModel user = getCurrentUser().getProfile();

        if (messageId == null) {
            throw new IllegalArgumentException("Parameter 1 cannot be null");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("Parameter 2 cannot be null");
        }
        if (conversation == null) {
            throw new IllegalArgumentException("Parameter 3 cannot be null");
        }
        if (!(conversation instanceof ParseConversationModel)) {
            throw new IllegalArgumentException("Parameter 3 must be of instance ParseConversationModel");
        }
        if (message == null) {
            throw new IllegalArgumentException("Parameter 4 cannot be null");
        }

        for (ParseUserProfileModel toUser : ((ParseConversationModel) conversation).getParticipants()) {
            if (!user.equals(toUser)) {
                ParseMessageModel messageModel = ParseMessageModel.create(messageId, timestamp, (ParseConversationModel) conversation, user, toUser, message);
                messageModel.saveInBackground(null);
                messages.add(messageModel);
            }
        }

        return messages;
    }

    /**
     * @see ModelManager#fetchMessages(ConversationModel)
     */
    @Override
    public List<? extends ParseMessageModel> fetchMessages(ConversationModel conversation) throws ParseException {

        // Verify that current user is not null
        if (getCurrentGroup() == null) {
            return new ArrayList<>();
        }

        // Verify parameters
        if (conversation == null) {
            throw new IllegalArgumentException("Parameter 1 cannot be null");
        }
        if (!(conversation instanceof ParseConversationModel)) {
            throw new IllegalArgumentException("Parameter 1 must be instance of ParseConversationModel");
        }

        Integer limit = 20;
        UserProfileModel user = getCurrentUser().getProfile();
        Date before = ((ParseConversationModel) conversation).getOldestMessageTimestamp();

        // Fetch messages from storage
        return fetchMessages(conversation, user, before, limit);
    }

    /**
     * @see ModelManager#fetchMessages(ConversationModel, UserProfileModel, Date, Integer)
     */
    @Override
    public List<? extends ParseMessageModel> fetchMessages(ConversationModel conversation, UserProfileModel user, Date before, Integer limit) throws ParseException {

        // Verify parameters
        if (conversation == null) {
            throw new IllegalArgumentException("Parameter 1 cannot be null");
        }
        if (!(conversation instanceof ParseConversationModel)) {
            throw new IllegalArgumentException("Parameter 1 must be of instance ParseConversationModel");
        }
        if (user == null) {
            throw new IllegalArgumentException("Parameter 2 cannot be null");
        }
        if (!(user instanceof ParseUserProfileModel)) {
            throw new IllegalArgumentException("Parameter 2 must be of instance ParseUserProfileModel");
        }
        if (limit == null || limit <= 0) {
            limit = 20;
        }

        List<ParseMessageModel> results = ParseMessageModel.fetchMessagesForConversationAndUser((ParseConversationModel) conversation, (ParseUserProfileModel) user, before, limit);

        // Update cache with the results
        for (int i = 0; i < results.size(); i++) {
            results.set(i, (ParseMessageModel) updateCache(results.get(i)));
        }

        // Add fetched messages to conversation's internal list of cached messages
        ((ParseConversationModel) conversation).addToCachedMessages(results);

        return results;
    }


    /**
     * @see ModelManager#fetchNotifications()
     */
    @Override
    public List<? extends ParseInvitationModel> fetchNotifications() throws ParseException {

        // Call cloud code to load notifications
        List<ParseObject> parseResults = ParseCloud.callFunction("fetchNotifications", new HashMap<String, Object>());
        List<ParseInvitationModel> modelResults = new ArrayList<>();

        // Process results
        for (ParseObject parseObject : parseResults) {
            ParseBaseModel model = getModelFromParseObject(parseObject);

            // Add any invitations to return set
            if (model instanceof ParseInvitationModel) {
                modelResults.add((ParseInvitationModel) model);
            }
        }

        return modelResults;
    }

    /**
     * @see ModelManager#fetchInvitationsForUser(UserProfileModel)
     */
    @Override
    public List<? extends ParseInvitationModel> fetchInvitationsForUser(UserProfileModel user) throws ParseException {

        // Verify parameters
        if (user == null) {
            throw new IllegalArgumentException("Parameter 1 cannot be null");
        }
        if (!(user instanceof ParseUserProfileModel)) {
            throw new IllegalArgumentException("Parameter 1 must be of type ParseUserProfileModel");
        }

        List<ParseInvitationModel> invitations = ParseInvitationModel.fetchAllSentTo((ParseUserProfileModel) user);
        return invitations;
    }

    /**
     * @see ModelManager#fetchInvitationsForGroup(GroupModel)
     */
    @Override
    public List<? extends ParseInvitationModel> fetchInvitationsForGroup(GroupModel group) throws ParseException {

        // Verify parameters
        if (group == null) {
            throw new IllegalArgumentException("Parameter 1 cannot be null");
        }
        if (!(group instanceof ParseUserProfileModel)) {
            throw new IllegalArgumentException("Parameter 1 must be of type ParseGroupModel");
        }

        List<ParseInvitationModel> invitations = ParseInvitationModel.fetchAllForGroup((ParseGroupModel) group);
        return invitations;
    }


    /**
     * Returns the model stored in cache matching the given model's object ID. If no such object is
     * stored in cache, then the provided model will be placed in cache and will be returned.
     *
     * @param model The model object to compare against in cache.
     *
     * @return Returns the model stored in cache.
     */
    protected ParseBaseModel updateCache(ParseBaseModel model) {
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
     * Checks internal cache to see if it contains a model with the supplied ID.
     *
     * @param id The string ID of the model to check the cache for.
     *
     * @return The cached model with the supplied ID or {@code null} if no such model is cached.
     */
    protected ParseBaseModel checkCache(String id) {
        if (id == null) {
            return null;
        }

        if (cachedModels.containsKey(id)) {
            return cachedModels.get(id);
        } else {
            return null;
        }
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

            // Try building a messaging model
            if (model == null) {
                model = ParseMessageModel.createFromParseObject(parseObject);
            }

            // Try building a conversation model
            if (model == null) {
                model = ParseConversationModel.createFromParseObject(parseObject);
            }

            // Try building an invitation model
            if (model == null) {
                model = ParseInvitationModel.createFromParseObject(parseObject);
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

    /**
     * @see ModelManager#joinGroup(GroupModel)
     */
    public ParseGroupModel joinGroup(GroupModel group) throws ParseException {

        // Verify parameters
        if (group == null) {
            throw new IllegalArgumentException("parameter 'group' cannot be null");
        }
        if (!(group instanceof ParseGroupModel)) {
            throw new IllegalArgumentException("parameter 'group' must be of type ParseGroupModel");
        }

        // Construct parameter hash map
        HashMap<String, Object> params = new HashMap<>();
        params.put("group", group.getId());

        ParseObject parseObject = ParseCloud.callFunction("joinGroup", params);

        if (parseObject != null) {
            ParseBaseModel model = getModelFromParseObject(parseObject);
            if (model != null && model instanceof ParseGroupModel) {
                this.currentGroup = (ParseGroupModel) model;
                return (ParseGroupModel) model;
            }
        }

        return null;
    }

    /**
     * @see ModelManager#leaveGroup(GroupModel)
     */
    public ParseGroupModel leaveGroup(GroupModel group) throws ParseException {

        if (group == null) {
            throw new IllegalArgumentException("parameter 'group' cannot be null");
        }
        if (!(group instanceof ParseGroupModel)) {
            throw new IllegalArgumentException("parameter 'group' must be of type ParseGroupModel");
        }

        // Construct parameter hash map
        HashMap<String, Object> params = new HashMap<>();
        params.put("group", group.getId());

        Object parseResult = ParseCloud.callFunction("leaveGroup", params);
        if (!(parseResult instanceof ParseObject)) {
            return null;
        }

        ParseObject parseObject = (ParseObject) parseResult;

        if (parseObject != null) {
            ParseBaseModel model = getModelFromParseObject(parseObject);
            if (model != null && model instanceof ParseGroupModel) {
                if (this.currentGroup != null && this.currentGroup.equals(model)) {
                    this.currentGroup = null;
                }
                return (ParseGroupModel) model;
            }
        }

        return null;
    }
}
