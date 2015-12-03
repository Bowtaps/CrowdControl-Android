package com.bowtaps.crowdcontrol.model;

import com.parse.ParseObject;

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
     * The class constructor. Initializes the model from an existing
     * {@link ParseObject}.
     *
     * @param object The object to use as a handle.
     */
    ParseUserProfileModel(ParseObject object) {
        super(object);
    }

    /**
     * Gets the user's display name.
     *
     * @return The user's display name.
     */
    @Override
    public String getDisplayName() {
        return (String) parseObject.get(displayNameKey);
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
}