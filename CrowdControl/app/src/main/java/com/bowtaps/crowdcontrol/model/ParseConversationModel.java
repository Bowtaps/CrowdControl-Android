package com.bowtaps.crowdcontrol.model;

import com.parse.ParseObject;

import java.util.Arrays;
import java.util.List;

/**
 * Parse implementation of the {@link ConversationModel} model. Provides functionality for
 * getting and setting fields in storage.
 *
 * @author Daniel Andrus
 * @since 2016-03-23
 */
public class ParseConversationModel extends ParseBaseModel implements ConversationModel {

    /**
     * The name of the table that this class is designed to work with.
     */
    private static final String tableName = "Conversation";

    /**
     * Key corresponding to {@link #setTitle(String)} and {@link #getTitle()}}.
     */
    private static final String titleKey = "Title";

    /**
     * Key corresponding to {@link #getGroup()}.
     */
    private static final String groupKey = "Group";

    /**
     * Key corresponding to {@link #getParticipants()}.
     */
    private static final String participantsKey = "Participants";


    /**
     * Class constructor. Initializes object with existing {@link ParseObject}.
     *
     * @param parseObject The object to use as an underlying handle into storage.
     */
    private ParseConversationModel(ParseObject parseObject) {
        super(parseObject);
    }


    @Override
    public String getTitle() {
        return (String) getParseObject().get(titleKey);
    }

    @Override
    public void setTitle(String title) {
        getParseObject().put(titleKey, title);
    }

    @Override
    public String getGroup() {
        return (String) getParseObject().get(groupKey);
    }

    @Override
    public List<String> getParticipants() {
        return Arrays.asList((String[]) getParseObject().get(participantsKey));
    }
}
