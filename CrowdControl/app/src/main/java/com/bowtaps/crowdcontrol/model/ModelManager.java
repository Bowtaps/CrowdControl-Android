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
    void deleteModel(BaseModel model) throws Exception;

    /**
     * Deletes a model from storage in background. This will spawn a separate thread and delete the
     * model on that thread, avoiding the issue of blocking the UI.
     *
     * @param model The model to delete.
     * @param callback The callback object to pass control to after execution.
     */
    void deleteModelInBackground(BaseModel model, final BaseModel.DeleteCallback callback);

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
    UserModel logInUser(String username, String password) throws Exception;

    /**
     * Logs in a user in a separate thread. Spawns a new thread and attempts to log in the user from
     * that, avoiding the problem of blocking the UI thread.
     *
     * @param username The username to use when logging the user in.
     * @param password The password to use when logging the user in.
     * @param callback The callback object to pass control to when the operation completes.
     */
    void logInUserInBackground(String username, String password, final BaseModel.LoadCallback callback);

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
    UserModel createUser(String username, String email, String password) throws Exception;

    /**
     * Creates a new user in background, spawning a new thread to avoid the problem of blocking the
     * UI thread.
     *
     * @param username The username to assign to the new user.
     * @param email The email address belonging to the newly created user.
     * @param password The password to set for the user.
     * @param callback The callback object to pass control back to after the operation completes.
     */
    void createUserInBackground(String username, String email, String password, final BaseModel.SaveCallback callback);

    /**
     * Gets the currently logged in user, if any. Returns {@code null} if no user is logged in.
     *
     * @return The currently logged in user or {@code null} if no user is logged in.
     */
    UserModel getCurrentUser();

    /**
     * Logs out the current user.
     *
     * This is a blocking function that can take several seconds to complete. Care should be taken
     * to not call this method from the UI thread.
     *
     * @return True if the operation was successful, false if not.
     * @throws Exception Throws an exception if an error occurred during logout.
     */
    Boolean logOutCurrentUser() throws Exception;

    /**
     * Loads all groups from storage.
     *
     * This is a blocking function that can take several seconds to complete. Care should be taken
     * to not call this method from the UI thread.
     *
     * @return A list of all {@link GroupModel} objects in storage.
     * @throws Exception Throws an exception if any error occurs.
     */
    List<? extends GroupModel> fetchAllGroups() throws Exception;

    /**
     * Fetches all group objects in storage on a separate thread. Spawns a new thread and performs
     * the fetch on that thread, avoiding the UI blocking issue.
     *
     * @param callback The callback object to pass control and the results to after completion.
     */
    void fetchAllGroupsInBackground(final BaseModel.FetchCallback callback);

    /**
     * Fetches all Notification objects in storage on a separate thread. Spawns a new thread and performs
     * the fetch on that thread, avoiding the UI blocking issue.
     *
     * @param callback The callback object to pass control and the results to after completion.
     */
    void fetchNotificationsInBackground(final BaseModel.FetchCallback callback);


    /**
     * Fetches all user objects in storage on a separate thread. Spawns a new thread and performs
     * the fetch on that thread, avoiding the UI blocking issue.
     *
     * @param callback The callback object to pass control and the results to after completion.
     */
    void fetchSearchedUsersInBackground(final BaseModel.FetchCallback callback, String searchString);

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
    GroupModel getCurrentGroup();

    /**
     * Sets the current active group. This updates the currently cached value, but does not update
     * the storage values. Only modifies what {@link #getCurrentGroup()} returns.
     *
     * @param group The group to set as the current group.
     */
    void setCurrentGroup(GroupModel group);

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
    GroupModel fetchCurrentGroup() throws Exception;

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
    void fetchCurrentGroupInBackground(final BaseModel.LoadCallback callback);

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
    GroupModel createGroup(UserProfileModel leader, String name, String description) throws Exception;

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
    void createGroupInBackground(UserProfileModel leader, String name, String description, final BaseModel.SaveCallback callback);

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
    List<? extends BaseModel> fetchGroupUpdates(String groupId, String userId, Date since) throws Exception;

    /**
     * Creates a new LocationModel object that can be saved to storage. Initializes the new model
     * with the provided function parameters.
     *
     * @param to The user to whom the location is sent to.
     * @param from The user by whom the location is generated (typically the current user).
     * @param location The location object to use as coordinates.
     * @return The new {@link LocationModel} object if successful or {@code null} if the operation
     * was unsuccessful.
     */
    LocationModel createLocation(UserProfileModel to, UserProfileModel from, LatLng location);

    /**
     * Fetches all known locations sent from the given user. Given a {@link UserProfileModel},
     * fetches all locations from storage that have been sent from that user regardless of whom the
     * locations were addressed to.
     *
     * @param user The user from whom the locations were sent.
     * @return A list of all locations sent from the requested user.
     * @throws ParseException Throws an exception if an error occurred for any reason.
     */
    List<? extends LocationModel> fetchLocationsFromUser(UserProfileModel user) throws ParseException;

    /**
     * Fetches all known locations sent to the given user. Given a {@link UserProfileModel}, fetches
     * all locations from storage that have been sent to that user regardless of whom the locations
     * were sent by.
     *
     * @param user The user to whom the locations were sent.
     * @return A list of all locations sent to the requested user.
     * @throws ParseException Throws an exception if an error occurred for any reason.
     */
    List<? extends LocationModel> fetchLocationsToUser(UserProfileModel user) throws ParseException;

    /**
     * Creates a new conversation belonging to a specific group.
     *
     * @param group The group to which the conversation belongs.
     *
     * @return The newly created conversation model.
     *
     * @throws Exception Throws an exception if creating and saving the conversation fails.
     */
    ConversationModel createConversation(GroupModel group) throws Exception;

    /**
     * Fetches all conversations from storage.
     *
     * @return A list of all conversation models in storage.
     *
     * @throws Exception Throws an exception if the operation fails for any reason.
     */
    List<? extends ConversationModel> fetchConversations() throws Exception;

    /**
     * Fetches all conversations belonging to the given group in which the given user is a
     * participant.
     *
     * @param group The group to fetch conversations for.
     * @param user The user to fetch conversations for.
     *
     * @return A list of conversations belonging to the given group in which the given user is a
     *         participant.
     *
     * @throws Exception Throws an exception if any storage operations fail.
     */
    List<? extends ConversationModel> fetchConversationsForGroupAndUser(GroupModel group, UserProfileModel user) throws Exception;

    /**
     * Creates a new message and connects it to the appropriate entities. The newly created message
     * is assembled from the supplied parameters.
     *
     * @param messageId The identifier to assign to the new message.
     * @param timestamp The timestamp that the message was created.
     * @param conversation The conversation to which the message belongs.
     * @param message The string contents of the message.
     *
     * @return A list of newly created message models created based on the supplied parameters.
     *         Each message model will be addressed to a different conversation participant.
     */
    List<? extends MessageModel> createMessage(String messageId, Date timestamp, ConversationModel conversation, String message);

    /**
     * Loads the next uncached messages from storage. Each consecutive call to this method will load
     * another batch of previously unloaded messages, assuming any remain.
     *
     * @param conversation The conversation for which to load the new messages.
     *
     * @return A list of message models belonging to the conversation that have before now not have
     *         been loaded from storage.
     *
     * @throws Exception Throws an exception if the storage operation fails.
     */
    List<? extends MessageModel> fetchMessages(ConversationModel conversation) throws Exception;

    /**
     * Loads the next set of uncached messages from storage. This method is an alternative to
     * calling {@link #fetchMessages(ConversationModel)} that allows for specific control over the
     * range of messages to fetch.
     *
     * @param conversation The conversation from which to fetch the messages.
     * @param user The user to which the fetched messages are to be addressed.
     * @param before The timestamp before which all fetched messages should have been created.
     * @param limit The maximum number of messages to fetch.
     * @return The list of message models fetched from storage.
     * @throws Exception Throws an exception should any storage operation fails.
     */
    List<? extends MessageModel> fetchMessages(ConversationModel conversation, UserProfileModel user, Date before, Integer limit) throws Exception;


    /**
     * Fetches all notifications from storage sent to the currently logged in user.
     *
     * @return A list of all existing invitations addressed to the currently logged in user.
     *
     * @throws Exception throws an exception should any storage operation fail.
     */
    List<? extends InvitationModel> fetchNotifications() throws Exception;

    /**
     * Fetches all invitations addressed to the supplied user.
     *
     * @param user The user for which to fetch invitations.
     *
     * @return List of invitations addressed to the supplied user.
     *
     * @throws Exception Throws an exception should any storage operation fail.
     */
    List<? extends InvitationModel> fetchInvitationsForUser(UserProfileModel user) throws Exception;

    /**
     * Loads all invitations sent out from the supplied group to any user.
     *
     * @param group the group to which all fetched invitations belong.
     *
     * @return A list of invitations sent from the given group.
     *
     * @throws Exception Throws an exception should any storage operation fail.
     */
    List<? extends InvitationModel> fetchInvitationsForGroup(GroupModel group) throws Exception;

    /**
     * Creates a new invitation based on a recipient and the current user and current group
     *
     * @param recipient - reciever of group invitation
     */
     InvitationModel createNewInvitation(UserProfileModel recipient);


    /**
     * Causes the currently logged in user to join the supplied group.
     *
     * @param group The group to join.
     *
     * @return Returns the group to which the current user belongs after the operation. This value
     *         may be {@code null} if the operation fails.
     *
     * @throws Exception Throws an exception should any storage operation fail.
     */
    GroupModel joinGroup(GroupModel group) throws Exception;

    /**
     * Causes the currently logged in user to leave the supplied group.
     *
     * @param group The group to leave.
     *
     * @return Returns the group that the current user has left.
     *
     * @throws Exception Throws an exception should any storage operation fail.
     */
    GroupModel leaveGroup(GroupModel group) throws Exception;
}
