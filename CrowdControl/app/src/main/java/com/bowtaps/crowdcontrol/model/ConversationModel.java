package com.bowtaps.crowdcontrol.model;

import java.util.List;

/**
 * Model for representing a instant messaging conversation in storage.
 *
 * @author Daniel Andrus
 * @since 2016-03-23
 */
public interface ConversationModel extends BaseModel {

    /**
     * Gets the title of the conversation.
     *
     * @return The conversation title.
     */
    String getTitle();

    /**
     * Sets the title of the conversation.
     *
     * @param title The conversation title.
     */
    void setTitle(String title);

    /**
     * Gets the group that this conversation belongs to.
     *
     * @return The group that this conversation belongs to.
     */
    GroupModel getGroup();

    /**
     * Gets a list of conversation participants.
     *
     * @return A not-null list of conversation participants.
     */
    List<UserProfileModel> getParticipants();
}
