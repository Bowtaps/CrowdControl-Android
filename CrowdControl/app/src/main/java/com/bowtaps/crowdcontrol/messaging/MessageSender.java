package com.bowtaps.crowdcontrol.messaging;

/**
 * A message sender interface containing methods for identifying by whom a message was sent.
 *
 * @author Daniel Andrus
 * @since 2016-01-22
 */
public interface MessageSender {

    /**
     * Gets the unique identifier representing this object.
     *
     * @return The sender's unique identifier.
     */
    public String getId();

    /**
     * Gets the sender's name as displayed to other users.
     *
     * @return The sender's display name.
     */
    public String getDisplayName();
}
