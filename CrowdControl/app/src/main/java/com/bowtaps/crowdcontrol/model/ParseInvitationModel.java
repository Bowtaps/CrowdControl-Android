package com.bowtaps.crowdcontrol.model;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Andrus
 */
public class ParseInvitationModel extends ParseBaseModel implements InvitationModel {

    private static final String tableName = "Invitation";
    private static final String senderKey = "Sender";
    private static final String recipientKey = "Recipient";
    private static final String groupKey = "Group";

    private ParseInvitationModel(ParseObject object) {
        super(object);
    }

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
}
