package com.bowtaps.crowdcontrol.model;

import java.util.Date;
import java.util.List;

/**
 * The base model interface, providing access to core model functionality,
 * including:
 *
 * - unique object identifier
 * - the initial object creation
 * - the last updated date
 * - model saving and loading
 *
 * @author Daniel Andrus
 * @since 2015-11-27
 */
public interface BaseModel {

    /**
     * Gets the unique object identifier for the model. This value is usually
     * determined by the storage medium, such as the database where the model is
     * stored.
     *
     * @return String representation of the unique identifier that can be used
     *         to reference this object.
     */
    public String getId();

    /**
     * Gets the creation date and time for the model. This value is
     * automatically generated and assigned when the object is first added to
     * storage.
     *
     * @return Date object representing the date and time when the model was
     *         first introduced into storage.
     */
    public Date getCreated();

    /**
     * Gets the date and time that the model was last updated in storage. This
     * timestamp is automatically assigned by storage and represents when the
     * storage-side model was last changed.
     *
     * @return Date object representing the date and time when the model was
     *         last updated in storage.
     */
    public Date getUpdated();

    /**
     * Gets a flag indicating that the model has new changes that haven't been
     * saved in storage. This value will change whenever a property is set in
     * the model but hasn't been saved.
     *
     * @return Boolean flag indicating whether or not the model contains unsaved
     *         changes.
     */
    public Boolean wasModified();

    /**
     * Saves this object to storage. This is a blocking function, so care should
     * be taken to not call this function on the main thread.
     */
    public void save() throws Exception;

    /**
     * Saves this object to storage asynchronously. Spawns a separate thread so
     * that this function can be called from the main thread without blocking
     * the UI. Upon completion, whether successful or unsuccessful, returns
     * control to the main thread by calling the
     * {@link SaveCallback#doneSavingModel(BaseModel, Exception)} method on the provided
     * {@link SaveCallback} object.
     *
     * @param callback The callback object to pass control to once the operation
     *                 is complete. If no object is provided (or null is given),
     *                 then nothing will happen after the object has been saved.
     */
    public void saveInBackground(final SaveCallback callback);

    /**
     * Loads this object from storage. This is a blocking function, so care
     * should be taken to not call this function on the main thread.
     *
     * @throws Exception Throws the exception
     */
    public void load() throws Exception;

    /**
     * Loads this object from storage asynchronously. Spawns a separate thread
     * so that this function can be called from the main thread without blocking
     * the UI. Upon completion, whether successful or unsuccessful, returns
     * control to the main thread by calling the
     * {@link LoadCallback#doneLoadingModel(BaseModel, Exception)} method on the provided
     * {@link LoadCallback} object.
     *
     * @param callback The callback object to pass control to once the operation
     *                 is completed. If no object is provided (or null is given),
     *                 then nothing will happen after the object has been
     *                 loaded.
     */
    public void loadInBackground(final LoadCallback callback);



    /**
     * The callback interface that should be used for asynchronous saving
     * operations.
     */
    public interface SaveCallback {

        /**
         * This method is called after completion of an asynchronous background
         * save on a model. It accepts the saved model and an exception object
         * should something go wrong.
         *
         * @param object The object that attempted to be saved. This parameter
         *               cannot be null and will be valid whether or not the
         *               operation was successful.
         * @param ex The exception that occurred, if any. This value will be
         *           null if the operation was successful and will be a valid
         *           exception object if an error occurred.
         */
        public void doneSavingModel(BaseModel object, Exception ex);
    }

    /**
     * The callback interface that should be used for asynchronous loading
     * operations.
     */
    public interface LoadCallback {

        /**
         * This method is called after completion of an asynchronous background
         * load on a model. It accepts the loaded model and an exception object
         * should something go wrong.
         *
         * @param object The object that attempted to be loaded. This parameter
         *               cannot be null and will be valid whether or not the
         *               operation was successful.
         * @param ex The exception that occurred, if any. This value will be
         *           null if the operation was successful and will be a valid
         *           exception object if an error occurred.
         */
        public void doneLoadingModel(BaseModel object, Exception ex);
    }

    /**
     * The callback interface that should be used for asynchornous loading operations that can
     * return multiple objects as a result.
     */
    public interface FetchCallback {

        /**
         * This method is called after completion of an asynchronous background fetch on a set
         * of models. It accepts the fetched models and an exception object should something go
         * wrong.
         *
         * @param results the results of the asynchronous fetch operation. If the operation was
         *                a success but no objects match the fetch critera, this list will be
         *                empty.
         * @param ex The exception that occurred, if any. This value will be {@code null} if the
         *           operation was successful and will be a valid {@link Exception} if an error
         *           occurred.
         */
        public void doneFetchingModels(List<? extends BaseModel> results, Exception ex);
    }
}
