package com.bowtaps.crowdcontrol.model;

/**
 * Model for representing and storing an instant message in storage. This class is intended to be
 * read-only, which is why it lacks setters for the corresponding getters.
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
     * Gets the ID of the user model that sent the message.
     *
     * @return The ID user model that sent the message.
     */
    String getFrom();

    /**
     * Gets the ID of the user model that the message has been sent to.
     *
     * @return The ID user model to whom the message was sent.
     */
    String getTo();

    /**
     * Gets the ID of the conversation object that this message is a part of.
     *
     * @return The ID conversation object that this message belongs to.
     */
    String getConversation();
}
