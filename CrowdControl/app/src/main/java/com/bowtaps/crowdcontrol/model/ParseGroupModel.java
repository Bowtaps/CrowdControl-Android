package com.bowtaps.crowdcontrol.model;

import android.util.Log;

import com.bowtaps.crowdcontrol.CrowdControlApplication;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
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
     * Key corresponding to {@link #getGroupDescription()} and {@link #setGroupDescription(String)}.
     */
    private static final String groupDescriptionKey = "GroupDescription";

    /**
     * Key corresponding to {@link #getGroupName()} and {@link #setGroupName(String)}.
     */
    private static final String groupNameKey = "GroupName";

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

        members = new ArrayList<ParseUserProfileModel>();
    }

    /**
     * Gets the description of the group.
     *
     * @return The description of the group.
     */
    @Override
    public String getGroupDescription() {
        return parseObject.getString(groupDescriptionKey);
    }

    /**
     * Sets the description of the group.
     *
     * @param description The new description of the group.
     */
    @Override
    public void setGroupDescription(String description) {
        parseObject.put(groupDescriptionKey, description);
    }

    /**
     * Gets the name of the group.
     *
     * @return The name of the group.
     */
    public String getGroupName() {
        return parseObject.getString(groupNameKey);
    }

    /**
     * Sets the name of the group.
     *
     * @param name The new name of the group.
     */
    public void setGroupName(String name) {
        parseObject.put(groupNameKey, name);
    }

    /**
     * Gets the list of users associated with the current group.
     *
     * @return A {@link List} of {@ParseUserProfileModel} objects that belong to the group.
     */
    @Override
    public List<ParseUserProfileModel> getGroupMembers() {
        return new ArrayList<ParseUserProfileModel>(members);
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

        // Verify arguments
        if (profile == null || !(profile instanceof ParseUserProfileModel)) {
            return false;
        }

        // Cast arguments to convenience variables
        ParseUserProfileModel parseProfile = (ParseUserProfileModel) profile;

        // Verify that the user isn't already part of the group
        if (members.contains(profile)) {
            return false;
        }

        // Add the profile to the ParseObject relation
        ParseRelation relation = parseObject.getRelation(groupMembersKey);
        relation.add(parseProfile.parseObject);

        // Add the profile to this model's cache
        members.add(parseProfile);

        return true;
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
        ParseRelation relation = parseObject.getRelation(groupMembersKey);
        relation.remove(parseProfile.parseObject);

        // Remove the profile from this model's cache
        members.remove(parseProfile);

        return true;
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

        // Fetch objects in relation
        ParseRelation relation = parseObject.getRelation(groupMembersKey);
        ParseQuery query = relation.getQuery();
        List<ParseObject> results = query.find();

        for (ParseObject result : results) {
            members.add(new ParseUserProfileModel(result));
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

        // Load group in background
        super.loadInBackground(new LoadCallback() {
            @Override
            public void doneLoadingModel(BaseModel object, Exception ex) {

                // Load group members in background
                if (object != null && ex == null) {
                    ((ParseGroupModel) object).parseObject.getRelation(groupMembersKey).getQuery().findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {

                            // Add found users to members array
                            if (objects != null && e == null) {
                                for (ParseObject object : objects) {
                                    members.add(new ParseUserProfileModel(object));
                                }
                            }

                            // Pass control to caller when complete
                            if (callback != null) {
                                callback.doneLoadingModel(thisGroup, e);
                            }
                        }
                    });
                } else if (callback != null) {

                    // Pass control to caller if an error occurred
                    callback.doneLoadingModel(object, ex);
                }
            }
        });
    }



    public static List<ParseGroupModel> getAll() throws ParseException {
        List<ParseGroupModel> result = new ArrayList<ParseGroupModel>();

        // Construct and execute query
        ParseQuery parseQuery = new ParseQuery(tableName);
        List<ParseObject> queryResult = parseQuery.find();

        // Create model objects for query results
        for (ParseObject parseObject : queryResult) {
            result.add(new ParseGroupModel(parseObject));
        }

        return result;
    }

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
}