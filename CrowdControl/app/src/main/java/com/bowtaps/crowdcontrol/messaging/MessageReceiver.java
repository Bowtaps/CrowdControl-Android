package com.bowtaps.crowdcontrol.messaging;

/**
 * A potential recipient for a message, containing basic information about the recipient, including
 * their backend identification {@link String} and their frontend display name.
 *
 * @author Daniel Andrus
 * @since 2016-01-22
 */
public interface MessageReceiver {

    /**
     * Gets the unique identifier representing this object.
     *
     * @return The receiver's unique identifier.
     */
    String getId();

    /**
     * Gets the receiver's name as displayed to other users.
     *
     * @return The receiver's display name.
     */
    String getDisplayName();
}
