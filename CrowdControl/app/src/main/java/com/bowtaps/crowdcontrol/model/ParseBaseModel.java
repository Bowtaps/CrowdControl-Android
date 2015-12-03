package com.bowtaps.crowdcontrol.model;

import java.util.Date;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

/**
 * The base Parse implementation of models. Fully implements the
 * @{link BaseModel} interface.
 *
 * @author Daniel Andrus
 * @since 2015-11-27
 */
public class ParseBaseModel implements BaseModel {

    /**
     * The class's handle for interacting with its Parse counterpart.
     */
    protected final ParseObject parseObject;

    /**
     * Key corresponding to {@link ParseBaseModel#getId}
     */
    private static final String idKey = "objectId";

    /**
     * Key corresponding to {@link ParseBaseModel#getCreated}
     */
    private static final String createdKey = "createdAt";

    /**
     * Key corresponding to {@link ParseBaseModel#getUpdated}
     */
    private static final String updatedKey = "updatedAt";


    /**
     * The class constructor. Initializes the model from an existing
     * {@link ParseObject}.
     *
     * @param object The object to use as a handle.
     */
    public ParseBaseModel(ParseObject object) {
        parseObject = object;
    }

    /**
     * Gets this object's unique identifier.
     *
     * @return The object's unique identifier.
     */
    @Override
    public String getId() {
        return parseObject.getObjectId();
    }

    /**
     * Gets the object's creation timestamp.
     *
     * @return The object's creation timestamp.
     */
    @Override
    public Date getCreated() {
        return parseObject.getCreatedAt();
    }

    /**
     * Gets the object's last updated timestamp.
     *
     * @return The object's last update timestamp.
     */
    @Override
    public Date getUpdated() {
        return parseObject.getUpdatedAt();
    }

    /**
     * Gets whether or not the object has unsaved changes.
     *
     * @return Whether or not the object has unsaved changes.
     */
    @Override
    public Boolean wasModified() {
        return parseObject.isDirty();
    }

    /**
     * Attempts to save the model to Parse synchronously. This is a blocking
     * function and thus should never be used on the main thread.
     *
     * @throws Exception If an exception is thrown by Parse, it will be passed
     *                   on to this function's caller.
     */
    @Override
    public void save() throws ParseException {
        parseObject.save();
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
        parseObject.saveInBackground(new com.parse.SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (callback != null) {
                    callback.doneSavingModel(model, e);
                }
            }
        });
    }

    /**
     * Attempts to load the model from parse synchronously. This is a blocking
     * function and thus should never be used on the main thread.
     *
     * @throws Exception If an exception is thrown by Parse, it will be passed
     *                   on to this function's caller.
     */
    @Override
    public void load() throws ParseException {
        parseObject.fetch();
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
        parseObject.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (callback != null) {
                    callback.doneLoadingModel(model, e);
                }
            }
        });
    }

}