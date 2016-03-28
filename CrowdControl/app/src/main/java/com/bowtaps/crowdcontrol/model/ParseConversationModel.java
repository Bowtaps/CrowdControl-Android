package com.bowtaps.crowdcontrol.model;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

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
     * A list of messages in descending sorted order by timestamp.
     */
    private final LinkedList<ParseMessageModel> messages;


    /**
     * Class constructor. Initializes object with existing {@link ParseObject}.
     *
     * @param parseObject The object to use as an underlying handle into storage.
     */
    private ParseConversationModel(ParseObject parseObject) {
        super(parseObject);
        messages = new LinkedList<>();
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
    public GroupModel getGroup() {
        ParseObject parseObject = getParseObject().getParseObject(groupKey);
        return ParseGroupModel.createFromParseObject(parseObject);
    }

    @Override
    public List<? extends ParseUserProfileModel> getParticipants() {
        List<ParseObject> parseParticipants = (List<ParseObject>) getParseObject().get(participantsKey);
        List<ParseUserProfileModel> participants = new ArrayList<>();
        for (ParseObject parseParticipant : parseParticipants) {
            participants.add(ParseUserProfileModel.createFromParseObject(parseParticipant));
        }
        return participants;
    }

    @Override
    public List<? extends ParseMessageModel> getCachedMessages() {
        return Collections.unmodifiableList(messages);
    }

    /**
     * Inserts a single message into the list of cached messages, ignoring duplicates.
     *
     * @param message The message item to insert.
     */
    protected void addToCachedMessages(ParseMessageModel message) {
        addToCachedMessages(Collections.singletonList(message));
    }

    /**
     * Inserts messages into the list of cached messages, ignoring duplicates.
     *
     * @param messages The list of messages to insert
     */
    protected void addToCachedMessages(List<ParseMessageModel> messages) {

        // Verify parameters
        if (messages == null) {
            return;
        }

        // Reverse sort messages by send time
        Collections.sort(messages, new Comparator<ParseMessageModel>() {
            @Override
            public int compare(ParseMessageModel lhs, ParseMessageModel rhs) {
                return rhs.compareTo(lhs);
            }
        });

        // Track iterator into list
        ListIterator<ParseMessageModel> iterator = this.messages.listIterator();
        ParseMessageModel iteratorModel = null;
        if (iterator.hasNext()) {
            iteratorModel = iterator.next();
        }

        // Insert each message into the appropriate location in our linked list
        for (ParseMessageModel message : messages) {

            // Determine where to insert this message
            if (iteratorModel == null || message.compareTo(this.messages.getLast()) < 0) {

                // Message belongs at end; insert there.
                if (!message.equals(this.messages.getLast())) {
                    this.messages.add(message);
                }
            } else if (message.compareTo(iteratorModel) >= 0) {

                // Message belongs before iterator
                if (!message.equals(iteratorModel)) {
                    iterator.add(message);
                }
            } else {

                // Increment iterator until the appropriate location is found, then insert
                while (iteratorModel != null && message.compareTo(iteratorModel) < 0) {
                    iteratorModel = iterator.next();
                }

                if (!message.equals(iteratorModel)) {
                    iterator.add(message);
                }
            }
        }
    }

    /**
     * Gets the message of the oldest cached message or {@code null} if no messages are cached.
     *
     * @return The timestamp of the oldest cached message or {@code null} if no messages are cached.
     */
    protected Date getOldestMessageTimestamp() {
        if (messages.isEmpty()) {
            return null;
        } else {
            return messages.getLast().getCreated();
        }
    }


    /**
     * Fetches from storage a list of conversations belonging to the supplied group in which the
     * supplied user is a participant.
     *
     * @param group The group for which to fetch conversations.
     * @param user The user for which to return conversations only in which they are participating.
     * @return List of conversations contained in the given group and in which the given user is
     * participating.
     * @throws ParseException This exception will only be thrown if the an error occurs while
     * communicating with Parse.
     */
    public static List<ParseConversationModel> fetchConversationsForGroupAndUser(ParseGroupModel group, ParseUserProfileModel user) throws ParseException {
        List<ParseConversationModel> results = new ArrayList<>();

        // Construct and execute query
        ParseQuery parseQuery = new ParseQuery(tableName);
        parseQuery.whereEqualTo(groupKey, group.getParseObject());
        parseQuery.whereEqualTo(participantsKey, user.getParseObject());
        List<ParseObject> queryResult = parseQuery.find();

        // Create model objects from query results
        for (ParseObject parseResult : queryResult) {
            ParseConversationModel modelResult = createFromParseObject(parseResult);
            if (modelResult != null) results.add(modelResult);
        }

        return results;
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
     * Creates a new instance using the provided {@link ParseObject} as the underlying handle into
     * storage.
     *
     * @param parseObject The underlying object to use as a handle into storage.
     * @return The newly created {@link ConversationModel} or null if an error occurred.
     */
    public static ParseConversationModel createFromParseObject(ParseObject parseObject) {

        // Verify parameters
        if (parseObject == null) {
            return null;
        }
        if (!parseObject.getClassName().equals(tableName)) {
            return null;
        }

        // Instantiate new object
        ParseConversationModel model = new ParseConversationModel(parseObject);

        return model;
    }
}
