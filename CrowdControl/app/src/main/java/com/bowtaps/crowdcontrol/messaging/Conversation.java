package com.bowtaps.crowdcontrol.messaging;

import java.util.List;

/**
 * An interface for interacting with conversations between users. Contains methods for retrieving
 * message history, setting up event listeners, and sending messages from the current user to the
 * rest of the conversation participants.
 *
 * @author Daniel Andrus
 * @since 2016-01-22
 */
public interface Conversation {

    /**
     * Gets a list of currently loaded past messages. This list is sorted in descending order by
     * received time, with the most recently received message at the beginning. This list may not
     * contain the complete message history and can change over time as new messages are sent,
     * received, and retrieved from storage.
     *
     * @return {@link List} of {@link Message}s that belong to this conversation and are currently
     *         loaded in memory.
     */
    List<? extends Message> getMessages();

    /**
     * Gets a list of {@link ConversationParticipant}s that belong to this conversation. This list
     * will contain the current user if they are a member of this conversation.
     *
     * @return {@link List} of {@link ConversationParticipant}s that belong to this conversation,
     *         including the current user if they are a member of this conversation.
     */
    List<? extends ConversationParticipant> getParticipants();

    /**
     * Sends a new text message from the currently logged in user to the rest of the conversation
     * participants. Returns the generated {@link TextMessage} object that will be sent. This will
     * build and send the message on a background thread, and thus failure/success cannot be
     * determined using this method alone. The results of this operation will be passed to the
     * {@link MessageSentListener} and {@link MessageSendFailedListener} listeners that are
     * registered via the {@link #setOnMessageSentListener(MessageSentListener)} and
     * {@link #setOnMessageSendFailedListener(MessageSendFailedListener)} methods.
     *
     * @param body The {@link String} contents of the message.
     *
     * @return The {@link TextMessage} object to be sent to the other conversation participants.
     */
    TextMessage sendTextMessage(String body, MessageSender sender);


    /**
     * Replaces any existing {@link MessageReceivedListener} with the provided listener as the
     * primary handler of message receive events.
     *
     * @param listener The {@link MessageReceivedListener} to register.
     */
    void setOnMessageReceivedListener(MessageReceivedListener listener);

    /**
     * Removes any previously set listener. Any incoming messages received will not be able to
     * notify any listeners until a new one is set via
     * {@link #setOnMessageReceivedListener(MessageReceivedListener)}.
     */
    void removeOnMessageReceivedListener();

    /**
     * Replaces any existing {@link MessageSentListener} with the provided listener as the primary
     * handler of successful message send events.
     *
     * @param listener The {@link MessageSentListener} to register.
     */
    void setOnMessageSentListener(MessageSentListener listener);

    /**
     * Removes any previously set listener. Any successfully sent messages will not be able to
     * notify any listeners until a new one is set via
     * {@link #setOnMessageSentListener(MessageSentListener)}.
     */
    void removeOnMessageSentListener();

    /**
     * Replaces any existing {@link MessageSendFailedListener} with the provided listener as the
     * primary handler of failed message send events.
     *
     * @param listener The {@link MessageSendFailedListener} to register.
     */
    void setOnMessageSendFailedListener(MessageSendFailedListener listener);

    /**
     * Removes any previously set listener. Any messages that failed to send will not be able to
     * notify any listeners until a new one is set via
     * {@link #setOnMessageSendFailedListener(MessageSendFailedListener)}.
     */
    void removeOnMessageSendFailedListener();

    /**
     * The interface for objects wishing to handle incoming messages.
     */
    interface MessageReceivedListener {

        /**
         * Callback that is triggered when an incoming message is received.
         *
         * @param message The message that was received.
         */
        void onMessageReceived(final Message message);
    }

    /**
     * The interface for objects wishing to handle successful message send events.
     */
    interface MessageSentListener {

        /**
         * Callback that is triggered when an outgoing message is successfully sent.
         *
         * @param message The message that was sent.
         */
        void onMessageSent(final Message message, final MessageReceiver receiver);
    }

    /**
     * The interface for objects wishing to handle failed message send events.
     */
    interface MessageSendFailedListener {

        /**
         * Callback that is triggered when an outgoing message fails to send.
         *
         * @param message The message that failed send.
         */
        void onMessageSendFailed(final Message message, final MessageReceiver receiver);
    }
}
