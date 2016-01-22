package com.bowtaps.crowdcontrol.messaging;

/**
 * @author Daniel Andrus
 * @since 2016-01-22
 */
public interface Message {
    public MessageSender getSender();
    public Conversation getConversation();
    Object getContent();
}
