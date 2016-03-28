package com.bowtaps.crowdcontrol.model;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
     * Field name for the timestamp when the object was created.
     */
    private static final String createdKey = "createdAt";


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
    public ParseUserProfileModel getFrom() {
        ParseObject parseObject = getParseObject().getParseObject(fromKey);
        return ParseUserProfileModel.createFromParseObject(parseObject);
    }

    @Override
    public ParseUserProfileModel getTo() {
        ParseObject parseObject = getParseObject().getParseObject(toKey);
        return ParseUserProfileModel.createFromParseObject(parseObject);
    }

    @Override
    public ParseConversationModel getConversation() {
        ParseObject parseObject = getParseObject().getParseObject(conversationKey);
        return ParseConversationModel.createFromParseObject(parseObject);
    }

    @Override
    public int compareTo(Object other) {
        return other instanceof MessageModel ? getCreated().compareTo(((MessageModel) other).getCreated()) : 0;
    }


    /**
     * Fetches message objects from storage belonging to the provided conversation and addressed to
     * the provided user with the results limited by the optionally provided number and to only the
     * most recently received messages sent before the optionally provided timestamp.
     *
     * Results will be sorted in descending order by time sent, with newer messages at the beginning
     * of the list. The number of elements in the returned result set will be no more than the
     * supplied value for the {@code limit} parameter.
     *
     * @param conversation The conversation to fetch messages for.
     * @param user The user to fetch messages for. Only will return messages addressed to this user.
     * @param before Optional. The value of the most recent timestamp any returned message should
     *               be. If this parameter is null, then the most recent messages will be returned.
     *               If a non-null value is supplied, then only messages sent before (older than)
     *               the supplied value will be returned.
     * @param limit Optional. The maximum number of messages to retrieve. Must be a positive
     *              non-zero integer. If no limit is supplied, then a default value will be used.
     * @return List containing the results of the query. This list can contain any number of
     * elements between 0 and limit.
     * @throws ParseException Throws a ParseException if a communication with Parse occurs.
     */
    public static List<ParseMessageModel> fetchMessagesForConversationAndUser(ParseConversationModel conversation, ParseUserProfileModel user, Date before, Integer limit) throws ParseException {

        List<ParseMessageModel> results = new ArrayList<>();

        // Verify parameters
        if (conversation == null) {
            throw new IllegalArgumentException("Parameter 1 cannot be null");
        }
        if (user == null) {
            throw new IllegalArgumentException("Parameter 2 cannot be null");
        }
        if (limit == null) {
            throw new IllegalArgumentException("Parameter 4 cannot be null");
        }
        if (limit <= 0) {
            return results;
        }

        // Construct and execute parse query
        ParseQuery parseQuery;
        ParseQuery<ParseObject> parseQueryFrom = new ParseQuery<>(tableName);
        ParseQuery<ParseObject> parseQueryTo   = new ParseQuery<>(tableName);

        parseQueryFrom.whereEqualTo(conversationKey, conversation.getParseObject());
        parseQueryTo.whereEqualTo(conversationKey, conversation.getParseObject());
        parseQueryFrom.whereEqualTo(fromKey, user.getParseObject());
        parseQueryTo.whereEqualTo(toKey, user.getParseObject());

        if (before != null) {
            parseQueryFrom.whereLessThanOrEqualTo(createdKey, before);
            parseQueryTo.whereLessThanOrEqualTo(createdKey, before);
        }

        parseQuery = ParseQuery.or(Arrays.asList(parseQueryFrom, parseQueryTo));
        parseQuery.addDescendingOrder(createdKey);
        parseQuery.setLimit(limit);
        List<ParseObject> parseResults = parseQuery.find();

        // Wrap results in models
        for (ParseObject parseResult : parseResults) {
            ParseMessageModel modelResult = createFromParseObject(parseResult);
            if (modelResult != null) results.add(modelResult);
        }

        return results;
    }

    public static ParseMessageModel create(ParseConversationModel conversation, ParseUserProfileModel fromUser, ParseUserProfileModel toUser, String message) {
        ParseMessageModel messageModel = new ParseMessageModel(new ParseObject(tableName));

        messageModel.getParseObject().put(conversationKey, conversation.getParseObject());
        messageModel.getParseObject().put(fromKey, fromUser.getParseObject());
        messageModel.getParseObject().put(toKey, toUser.getParseObject());
        messageModel.getParseObject().put(bodyKey, message);

        return messageModel;
    }

    /**
     * Determines whether the provided {@link ParseObject} is compatible with this class and can be
     * successfully "wrapped" by an instance of this class.
     *
     * @param parseObject The {@link ParseObject} to evaluate compatibility for.
     * @return {@code true} if it is possible to initialize a new instance of this class using the
     * provided {@link ParseObject}.
     */
    public static Boolean compatibleWithParseObject(ParseObject parseObject) {
        return (parseObject != null && parseObject.getClassName().equals(tableName));
    }

    /**
     * Creates a new instance of this class using the provided {@link ParseObject} as the underlying
     * handle into the database.
     *
     * @param parseObject The {@link ParseObject} to use as the underlying handle into the database.
     * @return The newly created instance of this class or {@code null} if unable to do so.
     */
    public static ParseMessageModel createFromParseObject(ParseObject parseObject) {

        if (parseObject == null) {
            return null;
        }
        if (!parseObject.getClassName().equals(tableName)) {
            return null;
        }

        // Instantiate a new object
        ParseMessageModel model = new ParseMessageModel(parseObject);
        return model;
    }
}
