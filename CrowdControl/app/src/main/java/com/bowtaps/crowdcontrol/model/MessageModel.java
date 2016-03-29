package com.bowtaps.crowdcontrol.model;

import java.util.Date;

/**
 * Model for representing and storing an instant message in storage. This class is intended to be
 * read-only, which is why it lacks setters for the corresponding getters.
 *
 * @author Daniel Andrus
 * @since 2016-03-23
 */
public interface MessageModel extends BaseModel, Comparable {

    /**
     * Gets the message ID. This ID can be shared between models if they originated from the same
     * message.
     *
     * @return The string message ID.
     */
    String getMessageId();

    /**
     * Gets the message send time.
     *
     * @return Timestamp when the message was sent.
     */
    Date getMessageTime();

    /**
     * Gets the string contents of the message.
     *
     * @return The string contents of the message.
     */
    String getBody();

    /**
     * Gets the user model that sent the message.
     *
     * @return The user model that sent the message.
     */
    UserProfileModel getFrom();

    /**
     * Gets the user model that the message has been sent to.
     *
     * @return The user model to whom the message was sent.
     */
    UserProfileModel getTo();

    /**
     * Gets the conversation object that this message is a part of.
     *
     * @return The conversation object that this message belongs to.
     */
    ConversationModel getConversation();
}
