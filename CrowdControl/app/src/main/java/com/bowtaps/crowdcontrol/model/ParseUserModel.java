package com.bowtaps.crowdcontrol.model;

import com.parse.ParseUser;

/**
 * The Parse implementation for the @{link UserModel} interface.
 *
 * @author Daniel Andrus
 * @since 2015-11-27
 */
public class ParseUserModel extends ParseBaseModel implements UserModel {

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

}
