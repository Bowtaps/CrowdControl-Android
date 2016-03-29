package com.bowtaps.crowdcontrol.messaging;

import com.bowtaps.crowdcontrol.model.UserProfileModel;

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
}
