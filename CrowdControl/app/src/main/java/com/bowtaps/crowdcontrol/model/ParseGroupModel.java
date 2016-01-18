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

import java.util.List;

/**
 * Created by 1959760 on 10/24/2015.
 */
public class ParseGroupModel extends ParseBaseModel implements GroupModel{

    /**
     * The name of the table that this object is designed to interact with.
     */
    private static final String tableName = "Group";

    /**
     * Key corresponding to {@link ParseGroupModel#getGeneralLocation}
     */
    private static final String generalLocationKey = "GeneralLocation";

    /**
     * Key corresponding to {@link ParseGroupModel#getGroupDescription()}
     */
    private static final String groupDescriptionKey = "GroupDescription";

    /**
     * Key corresponding to {@link ParseGroupModel#getGroupMembers()} and
     */
    private static final String groupMembersKey = "GroupMembers";
    //TODO: Must retrieve a LIST of groupmembers, and corresponding groupmembers keys.

    /**
     * The class constructor. Initializes the model from an existing
     * {@link ParseUser}.
     *
     * @param object The object to use as a handle.
     */
    public ParseGroupModel(ParseObject object) {
        super(object);
    }


    /**
     * Gets the general location of the group.
     *
     * @return The location of the group in the form of a ParseGeoPoint object.
     */
    @Override
    public ParseGeoPoint getGeneralLocation() {
        return (ParseGeoPoint) ((ParseObject) parseObject).get(groupDescriptionKey);
    }

    /**
     * Gets the description of the current group.
     *
     * @return The description string attached to the group.
     */
    @Override
    public String getGroupDescription() {
        return (String) ((ParseObject) parseObject).get(groupDescriptionKey);
    }

    /**
     * Gets the list of users associated with the current group.
     *
     * @return The list of users as ParseUserModel objects that belong to the
     * group.
     */
    @Override
    public ParseUser getGroupMembers() {
        return ((ParseObject) parseObject).getParseUser(groupMembersKey);
    }

    /**
     * puts the new group information into the group application variable
     *
     * @param groupName entered group name
     * @param groupDescription entered group description
     */
    public void SetGroupData(String groupName, String groupDescription) {

        // Add Group info into the single global instance of group
        parseObject.put("GroupName", groupName);
        parseObject.put("GroupDescription", groupDescription);

        ParseACL acl = new ParseACL();

        // Give public read access
        acl.setPublicReadAccess(true);
        CrowdControlApplication.aGroup.setACL(acl);

    }

    /**
     * adds a new member to the active group
     *
     * @param userProfile - the new group member
     */
    public void AddNewMember( ParseObject userProfile ) {
        //TODO this isn't catching empty profiles!!!!
        if ( CrowdControlApplication.aProfile == null || CrowdControlApplication.aProfile.getObjectId() == null ) {
            throw(new NullPointerException());
        }
        ParseRelation relation = CrowdControlApplication.aGroup.getRelation("GroupMembers");
        relation.add(userProfile);
    }

    public void LeaveGroup( ParseObject userProfile ) {
        if (CrowdControlApplication.aUser == null ){
            throw(new NullPointerException());
        }

        //ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupMemebers");
        //String profileId = userProfile.getObjectId();
        //query.whereEqualTo("objectId", profileId);

        ParseRelation<ParseObject> relation = parseObject.getRelation("GroupMembers");
        relation.remove(userProfile);

        parseObject.saveInBackground();
        //TODO: Ideally, we should be error checking this instead of just trusting it.

    }
}