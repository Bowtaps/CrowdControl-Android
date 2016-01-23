package com.bowtaps.crowdcontrol.messaging;

/**
 * Simple interface representing a text-only message.
 *
 * @author Daniel Andrus
 * @since 2016-01-22
 */
public interface TextMessage extends Message {

    /**
     * Gets the main content of the message represented as a {@link String} object.
     *
     * @return The message content.
     */
    @Override
    public String getContent();
}
