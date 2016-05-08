package com.bowtaps.crowdcontrol.messaging;

import com.bowtaps.crowdcontrol.model.UserProfileModel;

/**
 * @author Daniel Andrus
 */
public class ModelConversationParticipant implements ConversationParticipant {

    private final UserProfileModel model;

    public ModelConversationParticipant(UserProfileModel model) {
        this.model = model;
    }

    @Override
    public String getId() {
        return model.getId();
    }

    @Override
    public String getDisplayName() {
        return model.getDisplayName();
    }
}
