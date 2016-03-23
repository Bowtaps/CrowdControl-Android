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
     * Gets the ID of the group to which this conversation belongs.
     *
     * @return The ID of the group to which conversation belongs.
     */
    String getGroup();

    /**
     * Gets a list of IDs of all conversation participants.
     *
     * @return A list of IDs of all conversation participants.
     */
    List<String> getParticipants();
}
