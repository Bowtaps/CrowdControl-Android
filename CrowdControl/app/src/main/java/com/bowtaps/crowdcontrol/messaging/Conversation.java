package com.bowtaps.crowdcontrol.messaging;

import java.util.List;

/**
 * @author Daniel Andrus
 * @since 2016-01-22
 */
public interface Conversation {
    public List<? extends Message> getMessages();
    public List<? extends ConversationParticipant> getParticipants();

    public TextMessage sendTextMessage(String body);

    public void setOnMessageReceivedListener(MessageReceivedListener listener);
    public void removeOnMessageReceivedListener();

    public void setOnMessageSentListener(MessageSentListener listener);
    public void removeOnMessageSentListener();

    public void setOnMessageSendFailedListener(MessageSendFailedListener listener);
    public void removeOnMessageSendFailedListener();

    public interface MessageReceivedListener {
        public void onMessageReceived(final Message message);
    }

    public interface MessageSentListener {
        public void onMessageSent(final Message message);
    }

    public interface MessageSendFailedListener {
        public void onMessageSendFailed(final Message message);
    }
}
