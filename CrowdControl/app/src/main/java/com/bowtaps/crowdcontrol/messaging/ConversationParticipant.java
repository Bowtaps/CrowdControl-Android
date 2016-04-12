package com.bowtaps.crowdcontrol.messaging;

/**
 * Conglomerate interface combining the functionality of {@link MessageSender} and
 * {@link MessageReceiver}. Represents a user that is participating in a messaging conversation.
 *
 * @author Daniel Andrus
 * @since 2016-01-22
 */
public interface ConversationParticipant extends MessageSender, MessageReceiver {
}
