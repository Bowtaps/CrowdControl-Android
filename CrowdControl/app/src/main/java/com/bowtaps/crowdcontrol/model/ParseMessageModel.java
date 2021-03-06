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
     * The column name for the message ID.
     */
    private static final String messageIdKey = "MessageId";

    /**
     * Key for the timestamp column.
     */
    private static final String messageTimeKey = "Timestamp";

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


    /**
     * @see MessageModel#getMessageId()
     */
    @Override
    public String getMessageId() {
        return (String) getParseObject().get(messageIdKey);
    }

    /**
     * @see MessageModel#getMessageTime()
     */
    @Override
    public Date getMessageTime() {
        return (Date) getParseObject().get(messageTimeKey);
    }

    /**
     * @see MessageModel#getBody()
     */
    @Override
    public String getBody() {
        return (String) getParseObject().get(bodyKey);
    }

    /**
     * @see MessageModel#getFrom()
     */
    @Override
    public ParseUserProfileModel getFrom() {
        ParseObject parseObject = getParseObject().getParseObject(fromKey);
        return ParseUserProfileModel.createFromParseObject(parseObject);
    }

    /**
     * @see MessageModel#getTo()
     */
    @Override
    public ParseUserProfileModel getTo() {
        ParseObject parseObject = getParseObject().getParseObject(toKey);
        return ParseUserProfileModel.createFromParseObject(parseObject);
    }

    /**
     * @see MessageModel#getConversation()
     */
    @Override
    public ParseConversationModel getConversation() {
        ParseObject parseObject = getParseObject().getParseObject(conversationKey);
        return ParseConversationModel.createFromParseObject(parseObject);
    }

    /**
     * Compares this object's timestamp (which can be retrieved via {@link #getMessageTime()} with
     * the supplied object. Allows instances of this class to be sorted by time.
     *
     * @see Comparable#compareTo(Object)
     */
    @Override
    public int compareTo(Object other) {
        return other instanceof MessageModel ? getMessageTime().compareTo(((MessageModel) other).getMessageTime()) : 0;
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
            parseQueryFrom.whereLessThanOrEqualTo(messageTimeKey, before);
            parseQueryTo.whereLessThanOrEqualTo(messageTimeKey, before);
        }

        parseQuery = ParseQuery.or(Arrays.asList(parseQueryFrom, parseQueryTo));
        parseQuery.addDescendingOrder(messageTimeKey);
        parseQuery.setLimit(limit);
        List<ParseObject> parseResults = parseQuery.find();

        // Wrap results in models
        for (ParseObject parseResult : parseResults) {
            ParseMessageModel modelResult = createFromParseObject(parseResult);
            if (modelResult != null) results.add(modelResult);
        }

        return results;
    }

    /**
     * Creates a new instance of this class based off the supplied parameters.
     *
     * @param messageId The message's unique ID generated and associated on creation time. Allows
     *                  for conversations to avoid loading and displaying duplicate messages.
     * @param timestamp The time at which this message was created.
     * @param conversation The conversation to which this message belongs.
     * @param fromUser The user that sent this message.
     * @param toUser The user to which this message was sent.
     * @param message The string contents of this message.
     *
     * @return The newly created {@link ParseMessageModel}.
     */
    public static ParseMessageModel create(String messageId, Date timestamp, ParseConversationModel conversation, ParseUserProfileModel fromUser, ParseUserProfileModel toUser, String message) {
        ParseMessageModel messageModel = new ParseMessageModel(new ParseObject(tableName));

        messageModel.getParseObject().put(messageIdKey, messageId);
        messageModel.getParseObject().put(messageTimeKey, timestamp);
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
