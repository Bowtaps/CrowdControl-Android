package com.bowtaps.crowdcontrol.model;

import com.parse.ParseObject;

/**
 * Parse implementation of the {@link MessageModel} model. Provides functionality for getting and
 * setting fields in storage.
 *
 * @author Daniel Andrus
 * @since 2016-03-23
 */
public class ParseMessageModel extends ParseBaseModel implements MessageModel {

    /**
     * The name of the table with which this class was designed to work.
     */
    private static final String tableName = "Message";

    /**
     * Field name corresponding to {@link #getBody()}.
     */
    private static final String bodyKey = "Body";

    /**
     * Field name corresponding to {@link #getFrom()}.
     */
    private static final String fromKey = "From";

    /**
     * Field name corresponding to {@link #getTo()}.
     */
    private static final String toKey = "To";

    /**
     * Field name corresponding to {@link #getConversation()}.
     */
    private static final String conversationKey = "Conversation";


    /**
     * Class constructor. Initializes object to use the supplied {@link ParseObject} as the
     * underlying handle into storage.
     *
     * @param parseObject The object to use as a handle into storage.
     */
    private ParseMessageModel(ParseObject parseObject) {
        super(parseObject);
    }


    @Override
    public String getBody() {
        return (String) getParseObject().get(bodyKey);
    }

    @Override
    public String getFrom() {
        return (String) getParseObject().get(fromKey);
    }

    @Override
    public String getTo() {
        return (String) getParseObject().get(toKey);
    }

    @Override
    public String getConversation() {
        return (String) getParseObject().get(conversationKey);
    }
}
