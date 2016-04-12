package com.bowtaps.crowdcontrol.model;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Parse implementation of the {@link GroupModel} interface. Extends the {@link BaseModel} class.
 *
 * @author Joseph Mowry
 * @since 2015-10-24
 */
public class ParseGroupModel extends ParseBaseModel implements GroupModel {

    /**
     * The name of the table that this object is designed to interact with.
     */
    private static final String tableName = "Group";

    /**
     * Key corresponding to {@link #getGroupLeader()} and {@link #setGroupLeader(UserProfileModel)}.
     */
    private static final String leaderKey = "GroupLeader";

    /**
     * Key corresponding to {@link #getGroupName()} and {@link #setGroupName(String)}.
     */
    private static final String groupNameKey = "GroupName";

    /**
     * Key corresponding to {@link #getGroupDescription()} and {@link #setGroupDescription(String)}.
     */
    private static final String groupDescriptionKey = "GroupDescription";

    /**
     * Key corresponding to {@link #getGroupMembers()} and
     */
    private static final String groupMembersKey = "GroupMembers";


    /**
     * Internal cache of group members. This list is populated when {@link #load()} or
     * {@link #loadInBackground(LoadCallback)} is called.
     */
    private List<ParseUserProfileModel> members;

    /**
     * Cached list of conversations.
     */
    private List<ParseConversationModel> conversations;


    /**
     * Forwarding constructor. Invokes {@link #ParseGroupModel(ParseObject)} with a new
     * {@link ParseObject}, which will cause this model to create a new row in the database if it
     * is saved.
     */
    public ParseGroupModel() {
        this(new ParseObject(tableName));
    }

    /**
     * The class constructor. Initializes the model from an existing {@link ParseObject}.
     *
     * @param object The object to use as a handle. This object cannot be {@code null}.
     */
    public ParseGroupModel(ParseObject object) {
        super(object);

        members = new ArrayList<>();
        conversations = new ArrayList<>();
    }

    /**
     * Gets the {@link UserProfileModel} of the leader of the group or {@code null} if there is no
     * leader.
     *
     * @return The {@link UserProfileModel} of the leader of the group or {@code null} if there is
     *         no leader.
     */
    @Override
    public ParseUserProfileModel getGroupLeader() {

        Object parseLeader = getParseObject().get(leaderKey);
        ParseBaseModel leaderModel = null;

        if (parseLeader != null && parseLeader instanceof ParseObject) {
            leaderModel = ParseModelManager.getInstance().checkCache(((ParseObject) parseLeader).getObjectId());

            if (leaderModel == null) {
                leaderModel = ParseUserProfileModel.createFromParseObject((ParseObject) parseLeader);
                leaderModel = ParseModelManager.getInstance().updateCache((ParseBaseModel) leaderModel);
            }
        }

        if (leaderModel != null && leaderModel instanceof ParseUserProfileModel) {
            return (ParseUserProfileModel) leaderModel;
        } else {
            return null;
        }
    }

    /**
     * Sets the given user as this group's designated leader.
     *
     * @param leader The profile of the user to make the leader. {@code null} is a valid value.
     */
    @Override
    public void setGroupLeader(UserProfileModel leader) {

        // Verify parameters
        if (leader != null && !(leader instanceof ParseUserProfileModel)) {
            throw new IllegalArgumentException("leader parameter must be an instance of ParseUserProfileModel");
        }

        // Handle adding/replacing a leader and removing a leader separately
        if (leader == null) {

            // Leader is being removed
            getParseObject().put(leaderKey, null);
        } else {

            // Leader is being added/replaced
            getParseObject().put(leaderKey, ((ParseUserProfileModel) leader).getParseObject());
        }
    }

    /**
     * Gets the name of the group.
     *
     * @return The name of the group.
     */
    @Override
    public String getGroupName() {
        return getParseObject().getString(groupNameKey);
    }

    /**
     * Sets the name of the group.
     *
     * @param name The new name of the group.
     */
    @Override
    public void setGroupName(String name) {
        getParseObject().put(groupNameKey, name);
    }

    /**
     * Gets the description of the group.
     *
     * @return The description of the group.
     */
    @Override
    public String getGroupDescription() {
        return getParseObject().getString(groupDescriptionKey);
    }

    /**
     * Sets the description of the group.
     *
     * @param description The new description of the group.
     */
    @Override
    public void setGroupDescription(String description) {
        getParseObject().put(groupDescriptionKey, description);
    }

    /**
     * Gets the list of users associated with the current group.
     *
     * @return A {@link List} of {@link ParseUserProfileModel} objects that belong to the group.
     */
    @Override
    public List<ParseUserProfileModel> getGroupMembers() {
        return new ArrayList<>(members);
    }

    /**
     * Gets the list of users associated with the current group.
     *
     * @return An {@link ArrayList} of {@link UserProfileModel} objects that belong to the group.
     */
    public UserProfileModel getGroupMember(String id) {
        ParseUserProfileModel thisMember = null;

        for (ParseUserProfileModel member:members) {
            if(member.getId().equals(id)) {
                thisMember = member;
            }
        }

        return thisMember;
    }

    /**
     * Clears all members from this group. Does not modify the group leader, but will remove the
     * leader from the group if they are a member.
     *
     * @return {@code true} if the operation was successful, {@code false} if not.
     */
    @Override
    public Boolean clearGroupMembers() {

        ParseRelation relation = getParseObject().getRelation(groupMembersKey);

        for (ParseUserProfileModel profile : members) {
            relation.remove(profile.getParseObject());
        }
        members.clear();

        return true;
    }

    /**
     * Adds multiple new members to the group.
     *
     * @param profiles The {@link Collection} of users to add as members to this group.
     *
     * @return {@code true} if the users were successfully added to the group, {@code false} if not
     *         or if any member is already a member of the group.
     */
    @Override
    public Boolean addGroupMembers(Collection<? extends UserProfileModel> profiles) {

        // Verify arguments
        if (profiles == null) {
            return false;
        }
        for (UserProfileModel profile : profiles) {
            if (!(profile instanceof ParseUserProfileModel)) {
                return false;
            }

            // Verify that users aren't already parts of the group
            if (members.contains(profile)) {
                return false;
            }
        }

        ParseRelation relation = getParseObject().getRelation(groupMembersKey);

        // Add the users to the relation and to the model's cache
        for (UserProfileModel profile : profiles) {
            relation.add(((ParseUserProfileModel) profile).getParseObject());
            members.add((ParseUserProfileModel) profile);
        }

        return true;
    }

    /**
     * Adds a new member to the group.
     *
     * @param profile The {@link UserProfileModel} of the user to be added.
     *
     * @return True if the user was successfully added to the group, false if not or if the user is
     *         already a member of the group.
     */
    @Override
    public Boolean addGroupMember(UserProfileModel profile) {
        return addGroupMembers(Collections.singleton(profile));
    }

    /**
     * Removes a user from the group.
     *
     * @param profile The {@link UserProfileModel} of the user to be removed.
     *
     * @return True if the user was successfully removed from the group, false if not or if the user
     *         was already not a member of the group.
     */
    @Override
    public Boolean removeGroupMember(UserProfileModel profile) {

        // Verify arguments
        if (profile == null || !(profile instanceof ParseUserProfileModel)) {
            return false;
        }

        // Cast arguments to convenience variables
        ParseUserProfileModel parseProfile = (ParseUserProfileModel) profile;

        // Verify that the user is a member of the group
        if (!members.contains(profile)) {
            return false;
        }

        // Remove the profile from the ParseObject relation
        ParseRelation relation = getParseObject().getRelation(groupMembersKey);
        relation.remove(parseProfile.getParseObject());

        // Remove the profile from this model's cache
        members.remove(parseProfile);

        return true;
    }

    protected void addCachedConversation(ParseConversationModel conversation) {
        if (!conversations.contains(conversation)) {
            conversations.add(conversation);
        }
    }

    @Override
    public List<? extends ParseConversationModel> getCachedConversations() {
        return Collections.unmodifiableList(conversations);
    }

    /**
     * Loads this object from Parse storage synchronously. In addition to the normal functionality
     * inherited from {@link ParseBaseModel}, this method also fetches and caches the users who
     * are members of this group.
     *
     * @throws ParseException Throws If an exception occurs, throws a {@link ParseException}.
     *
     * @see ParseBaseModel#load()
     */
    @Override
    public void load() throws ParseException {
        super.load();
        members.clear();

        // Fetch the leader
        if (getParseObject().get(leaderKey) != null) {
            ParseObject parseLeader = (ParseObject) getParseObject().get(leaderKey);

            if (ParseModelManager.getInstance().checkCache(parseLeader.getObjectId()) == null) {
                ParseUserProfileModel.createFromParseObject(parseLeader);
            }
        }

        // Fetch objects in relation
        ParseRelation relation = getParseObject().getRelation(groupMembersKey);
        ParseQuery query = relation.getQuery();
        List<ParseObject> results = query.find();

        for (ParseObject result : results) {
            members.add(ParseUserProfileModel.createFromParseObject(result));
        }
    }

    /**
     * Loads this object from Parse storage asynchronously. In addition to the normal functionality
     * inherited from {@link ParseBaseModel}, this method also fetches and caches the users who
     * are members of this group.
     *
     * @param callback The callback object to pass control to once the operation
     *                 is completed. If no object is provided (or null is given),
     *                 then nothing will happen after the object has been
     *
     * @see ParseBaseModel#loadInBackground(LoadCallback)
     */
    @Override
    public void loadInBackground(final LoadCallback callback) {
        final ParseGroupModel thisGroup = this;

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
                ParseGroupModel model = (ParseGroupModel) params[0];
                this.callback = (BaseModel.LoadCallback) params[1];

                // Attempt fetch from storage
                try {
                    load();
                } catch (ParseException ex) {
                    exception = ex;
                }

                return model;
            }

            @Override
            public void onPostExecute(ParseGroupModel result) {

                // Execute callback if one is provided
                if (callback != null) {
                    callback.doneLoadingModel(result, exception);
                }
            }
        }.execute(this, callback);
    }


    /**
     * Fetches all groups from the database.
     *
     * This is a blocking function that can take several seconds to complete. Care should be taken
     * to not call this method from the UI thread.
     *
     * @return A {@link List} of all {@link ParseGroupModel} objects in the database.
     * @throws ParseException Throws an exception if any error occurs.
     */
    public static List<ParseGroupModel> getAll() throws ParseException {
        List<ParseGroupModel> result = new ArrayList<>();

        // Construct and execute query
        ParseQuery parseQuery = new ParseQuery(tableName);
        List<ParseObject> queryResult = parseQuery.find();

        // Create model objects for query results
        for (ParseObject parseObject : queryResult) {
            result.add(new ParseGroupModel(parseObject));
        }

        return result;
    }

    /**
     * Gets the group containing the provided user as a member. If no groups in the database contain
     * the provided user as a member, then {@code null} will be returned.
     *
     * @param profile The user profile to search for.
     * @return The group model containing the provided user as a member or {@code null} if the user
     * is not a member of any groups.
     * @throws ParseException Throws an exception if any error occurs.
     */
    public static ParseGroupModel getGroupContainingUser(UserProfileModel profile) throws ParseException {
        ParseGroupModel result = null;

        // Verify parameters
        if (profile == null || !(profile instanceof ParseUserProfileModel)) {
            throw new IllegalArgumentException("profile must be instance of ParseUserProfileModel");
        }

        // Construct and execute query
        ParseQuery groupQuery = new ParseQuery(tableName);
        List<ParseObject> groupResult = groupQuery.find();

        for (ParseObject parseGroup : groupResult) {

            // Construct and execute query for group members
            ParseQuery memberQuery = parseGroup.getRelation(groupMembersKey).getQuery();
            memberQuery.whereEqualTo("objectId", ((ParseUserProfileModel) profile).getId());
            memberQuery.setLimit(1);
            List<ParseObject> memberResult = memberQuery.find();

            // Search members column for matches with given user profile
            if (memberResult.size() > 0) {
                result = new ParseGroupModel(parseGroup);
                break;
            }
        }

        return result;
    }

    /**
     * Determines whether the provided {@link ParseObject} is compatible with this class and can be
     * successfully "wrapped" by an instance of this class.
     *
     * @param object The {@link ParseObject} to evaluate compatibility for.
     * @return {@code true} if it is possible to initialize a new instance of this class using the
     * provided {@link ParseObject}.
     */
    public static Boolean compatibleWithParseObject(ParseObject object) {
        return (object != null &&  object.getClassName().equals(tableName));
    }

    /**
     * Creates a new instance of this class using the provided {@link ParseObject} as the underlying
     * handle into the database.
     *
     * @param groupObject The {@link ParseObject} to use as the underlying handle into the database.
     * @return The newly created instance of this class or {@code null} if unable to do so.
     */
    public static ParseGroupModel createFromParseObject(ParseObject groupObject) {
        return createFromParseObject(groupObject, null);
    }

    /**
     * Creates a new instance of this class using the provided {@link ParseObject} as the underlying
     * handle into the database.
     *
     * @param groupObject The {@link ParseObject} to use as the underlying handle into the database.
     * @param memberObjects A list of members to use as the cached member list.
     * @return The newly created instance of this class or {@code null} if unable to do so.
     */
    public static ParseGroupModel createFromParseObject(ParseObject groupObject, List<ParseObject> memberObjects) {

        // Verify parameters
        if (groupObject == null) {
            return null;
        }
        if (!groupObject.getClassName().equals(tableName)) {
            return null;
        }

        // Instantiate a new object
        ParseGroupModel model = new ParseGroupModel(groupObject);

        // Hard set members list
        if (memberObjects != null) {
            model.members.clear();

            // Turn ParseObjects into models and add to internal list
            for (ParseObject memberObject : memberObjects) {
                ParseUserProfileModel memberModel = ParseUserProfileModel.createFromParseObject(memberObject);
                if (memberModel != null) {
                    model.members.add(memberModel);
                }
            }
        }

        return model;
    }


}