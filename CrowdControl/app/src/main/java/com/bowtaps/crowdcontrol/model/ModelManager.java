package com.bowtaps.crowdcontrol.model;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;

import java.util.Date;
import java.util.List;

/**
 * A class dedicated for providing model functionality that does not belong in any individual
 * model, such as logging users in, signing up new users, or getting the current user.
 *
 * @author Daniel Andrus
 * @since 2016-01-17
 */
public interface ModelManager {

    /**
     * Deletes an existing model from storage. Accepts a BaseModel object and removes its
     * corresponding entry in storage.
     *
     * This is a blocking function that can take several seconds to execute. Care should be taken
     * to not call this method on the UI thread. To delete a model on a separate thread, use
     * {@link #deleteModelInBackground(BaseModel, BaseModel.DeleteCallback)}.
     *
     * @param model The model to delete.
     * @throws Exception Throws an exception if any error occurred while deleting the model.
     */
    public void deleteModel(BaseModel model) throws Exception;

    /**
     * Deletes a model from storage in background. This will spawn a separate thread and delete the
     * model on that thread, avoiding the issue of blocking the UI.
     *
     * @param model The model to delete.
     * @param callback The callback object to pass control to after execution.
     */
    public void deleteModelInBackground(BaseModel model, final BaseModel.DeleteCallback callback);

    /**
     * Logs in a user using the given username and password.
     *
     * This is a blocking function that can take several seconds to complete. Care should be taken
     * to not call this method from the UI thread.
     *
     * @param username The username of the user to log in.
     * @param password The password of the user to log in.
     * @return The {@link UserModel} object representing the newly logged in user or {@code null} if
     * the operation was unsuccessful.
     * @throws Exception Throws an exception if any error occurred during the login process.
     */
    public UserModel logInUser(String username, String password) throws Exception;

    /**
     * Logs in a user in a separate thread. Spawns a new thread and attempts to log in the user from
     * that, avoiding the problem of blocking the UI thread.
     *
     * @param username The username to use when logging the user in.
     * @param password The password to use when logging the user in.
     * @param callback The callback object to pass control to when the operation completes.
     */
    public void logInUserInBackground(String username, String password, final BaseModel.LoadCallback callback);

    /**
     * Creates a new user and puts it in storage using the given username, email, and password.
     *
     * This is a blocking function that can take several seconds to complete. Care should be taken
     * to not call this method from the UI thread.
     *
     * @param username The unique username to assign the new user.
     * @param email The email address belonging to the newly created user.
     * @param password The password to set for the user.
     * @return The newly created {@link UserModel} object if the operation was successful or
     * {@code null} if it was not.
     * @throws Exception Throws an exception if an error occurred.
     */
    public UserModel createUser(String username, String email, String password) throws Exception;

    /**
     * Creates a new user in background, spawning a new thread to avoid the problem of blocking the
     * UI thread.
     *
     * @param username The username to assign to the new user.
     * @param email The email address belonging to the newly created user.
     * @param password The password to set for the user.
     * @param callback The callback object to pass control back to after the operation completes.
     */
    public void createUserInBackground(String username, String email, String password, final BaseModel.SaveCallback callback);

    /**
     * Gets the currently logged in user, if any. Returns {@code null} if no user is logged in.
     *
     * @return The currently logged in user or {@code null} if no user is logged in.
     */
    public UserModel getCurrentUser();

    /**
     * Logs out the current user.
     *
     * This is a blocking function that can take several seconds to complete. Care should be taken
     * to not call this method from the UI thread.
     *
     * @return True if the operation was successful, false if not.
     * @throws Exception Throws an exception if an error occurred during logout.
     */
    public Boolean logOutCurrentUser() throws Exception;

    /**
     * Loads all groups from storage.
     *
     * This is a blocking function that can take several seconds to complete. Care should be taken
     * to not call this method from the UI thread.
     *
     * @return A list of all {@link GroupModel} objects in storage.
     * @throws Exception Throws an exception if any error occurs.
     */
    public List<? extends GroupModel> fetchAllGroups() throws Exception;

    /**
     * Fetches all group objects in storage on a separate thread. Spawns a new thread and performs
     * the fetch on that thread, avoiding the UI blocking issue.
     *
     * @param callback The callback object to pass control and the results to after completion.
     */
    public void fetchAllGroupsInBackground(final BaseModel.FetchCallback callback);

    /**
     * Gets the current group that the current user is a member of. If no user is logged in or the
     * user is not a member of any groups, then will return {@code null}.
     *
     * This method only makes use of a cached value. This means that a call to this method may not
     * directly affect the current state of the models in storage. In order to query storage to
     * determine the actual current group that the user belongs to, use
     * {@link #fetchCurrentGroup()}.
     *
     * @return The {@link GroupModel} of which the currently logged in user is a member or
     * {@code null} if no such object exists.
     */
    public GroupModel getCurrentGroup();

    /**
     * Sets the current active group. This updates the currently cached value, but does not update
     * the storage values. Only modifies what {@link #getCurrentGroup()} returns.
     *
     * @param group The group to set as the current group.
     */
    public void setCurrentGroup(GroupModel group);

    /**
     * Queries storage to determine which group the current user belongs and caches and returns the
     * result. If the user is not a member of any group, then this method will return {@code null}.
     *
     * This is a blocking function that can take several seconds to complete. Care should be taken
     * to not call this method from the UI thread.
     *
     * @return Returns the {@link GroupModel} object that the user belongs to or {@code null} if
     * the current user is not a member of any group or if there is no current user.
     * @throws Exception Throws an exception if any error occurs.
     */
    public GroupModel fetchCurrentGroup() throws Exception;

    /**
     * Queries storage to determine which group the current user belongs and caches the result. If
     * the user is not a member of any group, then this method will return {@code null}.
     *
     * This method spawns a separate thread and runs its operations on that thread, avoiding the
     * issue of blocking the UI. Because of this, any results this method generates will be returned
     * to the provided {@link BaseModel.LoadCallback} object if one is provided.
     *
     * @param callback The callback object to pass control to after the operation completes.
     */
    public void fetchCurrentGroupInBackground(final BaseModel.LoadCallback callback);

    /**
     * Creates a new group object and saves it to storage using the provided parameters.
     *
     * This is a blocking function that can take several seconds to complete. Care should be taken
     * to not call this method from the UI thread.
     *
     * @param leader The declared leader of the group.
     * @param name The name of the group.
     * @param description The description of the group.
     * @return The newly created group model or {@code null} if no group was created.
     * @throws Exception Throws an exception if any error occurs.
     */
    public GroupModel createGroup(UserProfileModel leader, String name, String description) throws Exception;

    /**
     * Creates a new group object and saves it to storage using the provided parameters.
     *
     * This method spawns a separate thread and runs its operations on that thread, avoiding the
     * issue of blocking the UI. Because of this, any results this method generates will be returned
     * to the provided {@link BaseModel.LoadCallback} object if one is provided.
     *
     * @param leader The declared leader of the group.
     * @param name The name of the group.
     * @param description The description of the group.
     * @param callback The callback object to pass control to after the operation completes.
     */
    public void createGroupInBackground(UserProfileModel leader, String name, String description, final BaseModel.SaveCallback callback);

    /**
     * Fetches any updates for a group that have occurred since the provided timestamp. Returns a
     * list of models for any objects that have been updated since the given timestamp.
     *
     * This is a blocking function that can take several seconds to complete. Care should be taken
     * to not call this method from the UI thread.
     *
     * @param groupId The ID of the group to fetch updates for.
     * @param userId The ID of the user to fetch updates for.
     * @param since The timestamp since last a result was fetched.
     * @return A {@link List} of {@link BaseModel} objects that have been updated since the
     * timestamp.
     * @throws Exception Throws an exception if any error occurs.
     */
    public List<? extends BaseModel> fetchGroupUpdates(String groupId, String userId, Date since) throws Exception;

    public LocationModel createLocation(UserProfileModel to, UserProfileModel from, LatLng location);

    public List<? extends LocationModel> fetchLocationsFromUser(UserProfileModel user) throws Exception;

    public List<? extends LocationModel> fetchLocationsToUser(UserProfileModel user) throws Exception;

    public ConversationModel createConversation(GroupModel group) throws Exception;

    public List<? extends ConversationModel> fetchConversations() throws Exception;

    public List<? extends ConversationModel> fetchConversationsForGroupAndUser(GroupModel group, UserProfileModel user) throws Exception;

    public List<? extends MessageModel> createMessage(String messageId, ConversationModel conversation, String message);

    public List<? extends MessageModel> fetchMessages(ConversationModel conversation) throws Exception;

    public List<? extends MessageModel> fetchMessages(ConversationModel conversation, UserProfileModel user, Date before, Integer limit) throws Exception;
}
