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
     * @see UserProfileModel#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        return getParseObject().has(displayNameKey) ? getParseObject().getString(displayNameKey) : null;
    }

    /**
     * @see UserProfileModel#setDisplayName(String)
     */
    @Override
    public void setDisplayName(String displayName) {
        getParseObject().put(displayNameKey, displayName);
    }


    /**
     * Creates a new instance of this class using the provided {@link ParseObject} as the underlying
     * handle into the database.
     *
     * @param object The database handle to use when creating this class.
     * @return The newly created instance of this class that uses the provided {@link ParseObject}
     * as the underlying object or {@code null} if unable to create the object.
     */
    public static ParseUserProfileModel createFromParseObject(ParseObject object) {

        // Verify parameters
        if (object == null) {
            return null;
        }
        if (!object.getClassName().equals(tableName)) {
            return null;
        }

        // Instantiate a new object
        ParseBaseModel model = ParseModelManager.getInstance().checkCache(object.getObjectId());
        if (model == null) {
            model = new ParseUserProfileModel(object);
            model = ParseModelManager.getInstance().updateCache(model);
        }

        return (ParseUserProfileModel) model;
    }
}
