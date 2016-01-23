package com.bowtaps.crowdcontrol.messaging;

/**
 * Simple interface representing an immutable message object that can be used for accessing basic
 * information about the message, such as who sent the message and to what conversation the message
 * belongs.
 *
 * @author Daniel Andrus
 * @since 2016-01-22
 */
public interface Message {

    /**
     * Gets the {@link MessageSender} that is responsible for sending the message.
     *
     * @return The {@link MessageSender} that sent the message.
     */
    public MessageSender getSender();

    /**
     * Gets the {@link Conversation} to which this message belongs.
     *
     * @return The {@link Conversation} containing this message.
     */
    public Conversation getConversation();

    /**
     * Gets the main content of the message.
     *
     * @return The message content.
     */
    Object getContent();
}
