package com.bowtaps.crowdcontrol.model;

import com.parse.ParseObject;

/**
 * @author Daniel Andrus
 */
public class ParseInvitationModel extends ParseBaseModel implements InvitationModel {

    private ParseInvitationModel(ParseObject object) {
        super(object);
    }

    @Override
    public UserProfileModel getSender() {
        return null;
    }

    @Override
    public UserProfileModel getRecipient() {
        return null;
    }

    @Override
    public GroupModel getGroup() {
        return null;
    }



    static ParseInvitationModel createFromParseObject(ParseObject object) {

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
}
