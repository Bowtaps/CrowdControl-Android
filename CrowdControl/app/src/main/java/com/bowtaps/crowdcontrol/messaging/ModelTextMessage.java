package com.bowtaps.crowdcontrol.messaging;

import com.bowtaps.crowdcontrol.model.MessageModel;

import java.util.Date;

/**
 * @author Daniel Andrus
 */
public class ModelTextMessage implements TextMessage {

    private final MessageModel message;

    public ModelTextMessage(MessageModel model) {
        this.message = model;
    }

    @Override
    public String getMessageId() {
        return message.getMessageId();
    }

    @Override
    public Date getMessageTimestamp() {
        return message.getMessageTime();
    }

    @Override
    public ModelConversationParticipant getSender() {
        return new ModelConversationParticipant(message.getFrom());
    }

    @Override
    public ModelConversation getConversation() {
        return new ModelConversation(message.getConversation());
    }

    @Override
    public String getContent() {
        return message.getBody();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Message && getMessageId().equals(((Message) other).getMessageId());
    }
}
