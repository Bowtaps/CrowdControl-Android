package com.bowtaps.crowdcontrol.messaging;

import com.bowtaps.crowdcontrol.model.UserProfileModel;

import java.util.Date;

/**
 * @author Daniel Andrus
 */
public class SinchTextMessage implements TextMessage {

    private final com.sinch.android.rtc.messaging.Message message;
    private final UserProfileModel sender;

    public SinchTextMessage(com.sinch.android.rtc.messaging.Message message, UserProfileModel sender) {
        this.message = message;
        this.sender = sender;
    }

    @Override
    public String getMessageId() {
        return message.getMessageId();
    }

    @Override
    public Date getMessageTimestamp() {
        return message.getTimestamp();
    }

    @Override
    public ModelConversationParticipant getSender() {
        return new ModelConversationParticipant(sender);
    }

    @Override
    public Conversation getConversation() {
        // TODO
        return null;
    }

    @Override
    public String getContent() {
        return message.getTextBody();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Message && getMessageId().equals(((Message) other).getMessageId());
    }
}
