package com.bowtaps.crowdcontrol.model;

import java.util.Date;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

/**
 * The base Parse implementation of models. Fully implements the
 * {@link BaseModel} interface.
 *
 * @author Daniel Andrus
 * @since 2015-11-27
 */
public class ParseBaseModel implements BaseModel {

    /**
     * The class's handle for interacting with its Parse counterpart.
     */
    private ParseObject parseObject;



    /**
     * The class constructor. Initializes the model from an existing {@link ParseObject}.
     *
     * @param object The object to use as a handle.
     */
    public ParseBaseModel(ParseObject object) {

        // Verify arguments
        if (object == null) {
            throw new IllegalArgumentException("object cannot be null");
        }

        parseObject = object;
    }

    /**
     * Gets the underlying {@link ParseObject} for this model.
     *
     * @return The underlying {@link ParseObject} handle.
     */
    protected ParseObject getParseObject() {
        return parseObject;
    }

    /**
     * Replaces the underlying {@link ParseObject} for this object.
     *
     * @param parseObject The new {@link ParseObject} to attach to this model.
     */
    protected void setUnderlyingParseObject(ParseObject parseObject) {

        // Verify parameters
        if (parseObject == null) {
            throw new IllegalArgumentException("parseObject cannot be null");
        }

        this.parseObject = parseObject;
    }

    /**
     * @see UserProfileModel#getId()
     */
    @Override
    public String getId() {
        return getParseObject().getObjectId();
    }

    /**
     * @see UserProfileModel#getCreated()
     */
    @Override
    public Date getCreated() {
        return getParseObject().getCreatedAt();
    }

    /**
     * @see UserProfileModel#getUpdated()
     */
    @Override
    public Date getUpdated() {
        return getParseObject().getUpdatedAt();
    }

    /**
     * @see UserProfileModel#wasModified()
     */
    @Override
    public Boolean wasModified() {
        return getParseObject().isDirty();
    }

    /**
     * Attempts to save the model to Parse synchronously. This is a blocking
     * function and thus should never be used on the main thread.
     *
     * @throws ParseException If an exception is thrown by Parse, it will be passed
     *                        on to this function's caller.
     */
    @Override
    public void save() throws ParseException {
        getParseObject().save();
    }

    /**
     * Attempts to save the model to Parse asynchronously and passes control
     * back to the main thread by using the object passed as a parameter to this
     * function.
     *
     * @param callback The callback object to pass control to once the operation
     *                 is complete. If no object is provided (or null is given),
     *                 no callback will be executed.
     */
    @Override
    public void saveInBackground(final SaveCallback callback) {
        final BaseModel model = this;
        getParseObject().saveInBackground(new com.parse.SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (callback != null) {
                    callback.doneSavingModel(model, e);
                }
            }
        });
    }

    /**
     * Attempts to load the model from Parse synchronously. This is a blocking
     * function and thus should never be used on the main thread.
     *
     * @throws ParseException If an exception is thrown by Parse, it will be passed
     *                        on to this function's caller.
     */
    @Override
    public void load() throws ParseException {
        getParseObject().fetch();
    }

    /**
     * Attempts to save the model to Parse asynchronously and passes control
     * back to the main thread by using the object passed as a parameter to this
     * function.
     *
     * @param callback The callback object to pass control to once the operation
     *                 is completed. If no object is provided (or null is given),
     *                 then no callback will be executed.
     */
    @Override
    public void loadInBackground(final LoadCallback callback) {
        final BaseModel model = this;
        getParseObject().fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (callback != null) {
                    callback.doneLoadingModel(model, e);
                }
            }
        });
    }

    /**
     * @see UserProfileModel#delete()
     */
    @Override
    public void delete() throws ParseException {
        getParseObject().delete();
    }

    /**
     * @see UserProfileModel#deleteInBackground(DeleteCallback)
     */
    public void deleteInBackground(final DeleteCallback callback) {
        final BaseModel model = this;
        getParseObject().deleteInBackground(new com.parse.DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (callback != null) {
                    callback.doneDeletingModel(model, e);
                }
            }
        });
    }

    /**
     * Overrides the {@link Object#equals(Object)} operator, allowing this object to be used in
     * standard operations, such as list searching and object comparison.
     *
     * @param other The other object to compare this object to.
     * @return {@code true} if the other object is linked to the same {@link ParseObject},
     *         {@code false} if not.
     */
    @Override
    public boolean equals(final Object other) {
        if (other instanceof ParseObject) {
            return (getParseObject() != null && getParseObject().getObjectId().equals(((ParseObject) other).getObjectId()));
        } else if (other instanceof ParseBaseModel) {
            return equals(((ParseBaseModel) other).getParseObject());
        } else {
            return super.equals(other);
        }
    }

}
