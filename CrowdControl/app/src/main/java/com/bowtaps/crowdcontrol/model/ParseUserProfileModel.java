package com.bowtaps.crowdcontrol.model;

import com.bowtaps.crowdcontrol.CrowdControlApplication;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * The Parse implementation of the @{link ParseBaseModel} interface.
 *
 * @author Daniel Andrus
 * @since 2015-11-27
 */
public class ParseUserProfileModel extends ParseBaseModel implements UserProfileModel {

    /**
     * The name of the table that this object is designed to interact with.
     */
    private static final String tableName = "CCUser";

    /**
     * Key corresponding to {@link ParseUserProfileModel#getDisplayName}
     */
    private static final String displayNameKey = "DisplayName";


    /**
     * Forwarding constructor. Invokes {@link #ParseUserProfileModel(ParseObject)} with a new
     * {@link ParseObject}, which will cause this model to create a new row in the database if it
     * is saved.
     */
    public ParseUserProfileModel() {
        this(new ParseObject(tableName));
    }

    /**
     * The class constructor. Initializes the model from an existing {@link ParseObject}.
     *
     * @param object The object to use as a handle. This value cannot be {@code null}.
     */
    public ParseUserProfileModel(ParseObject object) {
        super(object);
    }

    /**
     * Gets the user's display name.
     *
     * @return The user's display name.
     */
    @Override
    public String getDisplayName() {
        return parseObject.getString(displayNameKey);
    }

    /**
     * Sets the user's display name.
     *
     * @param displayName The new display name for the user.
     */
    @Override
    public void setDisplayName(String displayName) {
        parseObject.put(displayNameKey, displayName);
    }

    public static ParseUserProfileModel createFromParseObject(ParseObject object) {

        // Verify parameters
        if (object == null) {
            return null;
        }
        if (!object.getClassName().equals(tableName)) {
            return null;
        }

        // Instantiate a new object
        ParseUserProfileModel model = new ParseUserProfileModel(object);

        return model;
    }
}
