package com.bowtaps.crowdcontrol.messaging;

import com.bowtaps.crowdcontrol.model.ConversationModel;
import com.bowtaps.crowdcontrol.model.MessageModel;
import com.bowtaps.crowdcontrol.model.UserProfileModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Andrus
 */
public class ModelConversation implements Conversation {

    private final ConversationModel model;

    public ModelConversation(ConversationModel model) {
        this.model = model;
    }

    @Override
    public List<? extends ModelTextMessage> getMessages() {
        List<ModelTextMessage> messages = new ArrayList<>();

        for (MessageModel message : model.getCachedMessages()) {
            messages.add(new ModelTextMessage(message));
        }

        return messages;
    }

    @Override
    public List<? extends ConversationParticipant> getParticipants() {
        List<ModelConversationParticipant> participants = new ArrayList<>();

        for (UserProfileModel user : model.getParticipants()) {
            participants.add(new ModelConversationParticipant(user));
        }

        return participants;
    }

    @Override
    public TextMessage sendTextMessage(String body, MessageSender sender) {
        // TODO
        return null;
    }

    @Override
    public void setOnMessageReceivedListener(MessageReceivedListener listener) {
        // TODO
    }

    @Override
    public void removeOnMessageReceivedListener() {
        // TODO
    }

    @Override
    public void setOnMessageSentListener(MessageSentListener listener) {
        // TODO
    }

    @Override
    public void removeOnMessageSentListener() {
        // TODO
    }

    @Override
    public void setOnMessageSendFailedListener(MessageSendFailedListener listener) {
        // TODO
    }

    @Override
    public void removeOnMessageSendFailedListener() {
        // TODO
    }
}
