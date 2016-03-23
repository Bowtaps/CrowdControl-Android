package com.bowtaps.crowdcontrol.model;

/**
 * Model for representing and storing an instant message in storage.
 *
 * @author Daniel Andrus
 * @since 2016-03-23
 */
public interface MessageModel extends BaseModel {

    /**
     * Gets the string contents of the message.
     *
     * @return The string contents of the message.
     */
    String getBody();

    /**
     * Sets the string contents of the message.
     *
     * @param body The strong contents of the message.
     */
    void setBody(String body);

    /**
     * Gets the user model that sent the message.
     *
     * @return The user model that sent the message.
     */
    UserProfileModel getFrom();

    /**
     * Sets the user model that sent the message.
     *
     * @param user The user model that sent the message.
     */
    void setFrom(UserProfileModel user);

    /**
     * Gets the user model that the message has been sent to.
     *
     * @return The user model to whom the message was sent.
     */
    UserProfileModel getTo();

    /**
     * Sets the user model to whom the message was sent.
     *
     * @param user The user model to whom the message was sent.
     */
    void setTo(UserProfileModel user);

    /**
     * Gets the conversation object that this message is a part of.
     *
     * @return The conversation object that this message belongs to.
     */
    ConversationModel getConversation();

    /**
     * Sets the conversation object that this message is a part of.
     *
     * @param conversation The converation object that this mesage belongs to.
     */
    void setConversation(ConversationModel conversation);
}
