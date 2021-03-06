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
     * Gets the group to which this conversation belongs.
     *
     * @return The the group to which conversation belongs.
     */
    GroupModel getGroup();

    /**
     * Gets a list of all conversation participants.
     *
     * @return A list of all conversation participants.
     */
    List<? extends UserProfileModel> getParticipants();

    /**
     * Adds a participant to the conversation model.
     *
     * @param user The user to add as a participant.
     */
    void addParticipant(UserProfileModel user);

    /**
     * Gets all messages that have been cached from storage.
     *
     * @return List of messages cached in memory
     */
    List<? extends MessageModel> getCachedMessages();
}
