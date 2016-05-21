package com.bowtaps.crowdcontrol.model;

import com.bowtaps.crowdcontrol.CrowdControlApplication;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Andrus
 */
public class ParseInvitationModel extends ParseBaseModel implements InvitationModel {

    /**
     * The name of the Parse table associated with this class.
     */
    private static final String tableName = "Invitation";

    /**
     * The column name associated with the {@link #getSender()} method.
     */
    private static final String senderKey = "Sender";

    /**
     * The column name associated with the {@link #getRecipient()} method.
     */
    private static final String recipientKey = "Recipient";

    /**
     * The column name associated with the {@link #getGroup()} method.
     */
    private static final String groupKey = "Group";

    /**
     * The column name associated with the {@link #initializeNewInvitation(UserProfileModel)} ()} method.
     */
    private static final String sentGroupKey = "sendingGroup";

    private ParseUserProfileModel recipient;


    /**
     * Constructor for this class. Creates a new instance of the model based on the supplied
     * {@link ParseObject}.
     *
     * @param object The {@link ParseObject} to use as the underlying handle into storage.
     */
    public ParseInvitationModel(ParseObject object) {
        super(object);
    }

    /**
     * @see InvitationModel#getSender()
     */
    @Override
    public ParseUserProfileModel getSender() {

        ParseObject parseSender = (ParseObject) getParseObject().get(senderKey);
        ParseBaseModel model = ParseModelManager.getInstance().checkCache(parseSender.getObjectId());

        if (model == null) {
            model = ParseUserProfileModel.createFromParseObject(parseSender);
            model = ParseModelManager.getInstance().updateCache(model);
        }

        return (ParseUserProfileModel) model;
    }

    /**
     * @see InvitationModel#getRecipient()
     */
    @Override
    public ParseUserProfileModel getRecipient() {

        ParseObject parseRecipient = (ParseObject) getParseObject().get(recipientKey);
        ParseBaseModel model = ParseModelManager.getInstance().checkCache(parseRecipient.getObjectId());

        if (model == null) {
            model = ParseUserProfileModel.createFromParseObject(parseRecipient);
            model = ParseModelManager.getInstance().updateCache(model);
        }

        return (ParseUserProfileModel) model;
    }

    /**
     * @see InvitationModel#getGroup()
     */
    @Override
    public ParseGroupModel getGroup() {

        ParseObject parseGroup = (ParseObject) getParseObject().get(groupKey);
        ParseBaseModel model = ParseModelManager.getInstance().checkCache(parseGroup.getObjectId());

        if (model == null) {
            model = ParseGroupModel.createFromParseObject(parseGroup);
            model = ParseModelManager.getInstance().updateCache(model);
        }

        return (ParseGroupModel) model;
    }

    /**
     * Creates a new invitation based on the sender and receiver
     * @param recipient
     */
    @Override
    public void initializeNewInvitation(UserProfileModel recipient) {
        getParseObject().put(senderKey, CrowdControlApplication.getInstance().getModelManager().getCurrentUser().getProfile().getId());
        getParseObject().put(recipientKey, recipient.getId());
        getParseObject().put(sentGroupKey, CrowdControlApplication.getInstance().getModelManager().getCurrentGroup().getId());
    }




    /**
     * Creates a new instance based on the supplied parameters. Uses the provided
     * {@link ParseObject} as the underlying handle into storage. Automatically attempts to cache
     * the object with the model manager.
     *
     * @param object The {@link ParseObject} to use as an underlying handle into storage.
     *
     * @return The newly created model.
     */
    protected static ParseInvitationModel createFromParseObject(ParseObject object) {

        // Verify parameters
        if (object == null) {
            throw new IllegalArgumentException("Parameter 1 cannot be null");
        }

        ParseBaseModel model = ParseModelManager.getInstance().checkCache(object.getObjectId());
        if (model == null) {
            model = new ParseInvitationModel(object);
            model = ParseModelManager.getInstance().updateCache(model);
        }
        return (ParseInvitationModel) model;

    }

    /**
     * Fetches all invitations from storage sent to the given user.
     *
     * @param user The user for which to fetch all invitations.
     *
     * @return A list of invitations sent to the given user.
     *
     * @throws ParseException Throws a {@link ParseException} if any database operation fails.
     */
    protected static List<ParseInvitationModel> fetchAllSentTo(ParseUserProfileModel user) throws ParseException {
        if (user == null) {
            throw new IllegalArgumentException("Parameter 1 cannot be null");
        }

        ParseQuery parseQuery = new ParseQuery(tableName);
        parseQuery.whereEqualTo(recipientKey, user.getParseObject());

        List<ParseObject> parseResults = parseQuery.find();
        List<ParseInvitationModel> results = new ArrayList<>();

        for (ParseObject parseResult : parseResults) {
            results.add(ParseInvitationModel.createFromParseObject(parseResult));
        }

        return results;
    }

    /**
     * Fetches all invitations from storage associated with the given group.
     *
     * @param group The group object for which to fetch invitations.
     *
     * @return A list of all invitations sent from the given group.
     *
     * @throws ParseException Throws an exception if any database operation fails.
     */
    protected static List<ParseInvitationModel> fetchAllForGroup(ParseGroupModel group) throws ParseException {
        if (group == null) {
            throw new IllegalArgumentException("Parameter 1 cannot be null");
        }

        ParseQuery parseQuery = new ParseQuery(tableName);
        parseQuery.whereEqualTo(groupKey, group.getParseObject());

        List<ParseObject> parseResults = parseQuery.find();
        List<ParseInvitationModel> results = new ArrayList<>();

        for (ParseObject parseResult : parseResults) {
            results.add(ParseInvitationModel.createFromParseObject(parseResult));
        }

        return results;
    }


    /**
     * Fetches all Invite Notifications from the database.
     *
     * This is a blocking function that can take several seconds to complete. Care should be taken
     * to not call this method from the UI thread.
     *
     * @return A {@link List} of all {@link ParseGroupModel} objects in the database.
     * @throws ParseException Throws an exception if any error occurs.
     */
    public static List<ParseInvitationModel> getAll() throws ParseException {
        List<ParseInvitationModel> result = new ArrayList<>();

        // Construct and execute query
        ParseQuery parseQuery = new ParseQuery(tableName);
        List<ParseObject> queryResult = parseQuery.find();

        // Create model objects for query results
        for (ParseObject parseObject : queryResult) {
            result.add(new ParseInvitationModel(parseObject));
        }

        return result;
    }


}
