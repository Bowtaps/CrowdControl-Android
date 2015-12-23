package com.bowtaps.crowdcontrol.model;

import com.bowtaps.crowdcontrol.CrowdControlApplication;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * The Parse implementation for the @{link UserModel} interface.
 *
 * @author Daniel Andrus
 * @since 2015-11-27
 */
public class ParseUserModel extends ParseBaseModel implements UserModel {

    // Properties
    private String objectID;
    private String userName;
//    private String password;
    private Object authData;
//    private Boolean emailVerified;
//    private String email;

//    //CC ParseUserModel Data
//    private Object location;
//    private Object preferences;
//    private String status;

    // Get Methods
    @Override
    public String getObjectID(){
        return this.objectID;
    }
    @Override
    public String getUserName(){
        return this.userName;
    }
    @Override
    public Object getAuthData(){
        return this.authData;
    }

    /**
     * The name of the table that this object is designed to interact with.
     */
    private static final String tableName = "_User";

    /**
     * Key corresponding to {@link ParseUserModel#getUsername}
     */
    private static final String usernameKey = "username";

    /**
     * Key corresponding to {@link ParseUserModel#getEmailVerified}
     */
    private static final String emailVerifiedKey = "emailVerified";

    /**
     * Key corresponding to {@link ParseUserModel#getEmail} and
     * {@link ParseUserModel#setEmail}
     */
    private static final String emailKey = "email";

    /**
     * Key correponding to {@link ParseUserModel#getPhone} and
     * {@link ParseUserModel#setPhone}
     */
    private static final String phoneKey = "phone";

    /**
     * Key correponding to {@link ParseUserModel#getUserDatabaseID} and
     * {@link ParseUserModel#setPhone}
     */
    private static final String userDatabaseIDKey = "ObjectID";

    /**
     * Key correponding to {@link ParseUserModel#getPhone}
     */
    private static final String displayUserIDKey = "CCUser";


    /**
     * The class constructor. Initializes the model from an existing
     * {@link ParseUser}.
     *
     * @param object The object to use as a handle.
     */
    public ParseUserModel(ParseUser object) {
        super(object);
    }

    /**
     * Gets the user's username.
     *
     * @return The user's username.
     */
    @Override
    public String getUsername() {
        return ((ParseUser) parseObject).getUsername();
    }

    /**
     * Gets whether or not the user has verified their email.
     *
     * @return Whether or not the user has verified their email.
     */
    @Override
    public Boolean getEmailVerified() {
        return (Boolean) ((ParseUser) parseObject).get(emailVerifiedKey);
    }

    /**
     * Gets the user's email.
     *
     * @return The user's email.
     */
    @Override
    public String getEmail() {
        return ((ParseUser) parseObject).getEmail();
    }

    /**
     * Sets the user's email.
     *
     * @param email The new email address to assign to the user.
     */
    @Override
    public void setEmail(String email) {
        ((ParseUser) parseObject).setEmail(email);
    }

    /**
     * Gets the user's phone number.
     *
     * @return The user's phone number.
     */
    @Override
    public String getPhone() {
        return (String) parseObject.get(phoneKey);
    }

    /**
     * Sets the user's phone number.
     *
     * @param phone The new phone number to assign to the user.
     */
    @Override
    public void setPhone(String phone) {
        parseObject.put(phoneKey, phone);
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
        ((ParseUser) parseObject).signUp();
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
//        ((ParseUser) parseObject).signupInBackground(new com.parse.SaveCallback(e) {
//            @Override
//            public void done(ParseException e) {
//                if (callback != null) {
//                    callback.doneSavingModel(model, e);
//                }
//            }
//        });
        ((ParseUser) parseObject).signUpInBackground();
    }

    /**
     * Set all of the information for a User
     *
     * @param email The new email address to assign to the user.
     * @param password The new password being assigned to the user.
     * @param phone The new phone number to assign to the user.
     */
    public void setAllUserData ( String email, String password, String phone )
    {
        setEmail(email);
        ((ParseUser) parseObject).setUsername(email);
        ((ParseUser) parseObject).setPassword(password);
        setPhone(phone);
    }

    public void logIntoParseUser ( String email, String password )
    {
        ((ParseUser) parseObject).logInInBackground(email, password);
    }

    public String getUserDatabaseID() { return (String) parseObject.get(userDatabaseIDKey);}

    public void setDisplayUser( ParseObject displayUser)
    {
        parseObject.put(displayUserIDKey, CrowdControlApplication.aProfile.getObjectId());
    }

}
