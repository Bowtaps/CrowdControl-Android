package com.bowtaps.crowdcontrol.model;

import android.os.AsyncTask;

import com.bowtaps.crowdcontrol.CrowdControlApplication;

import com.parse.LogInCallback;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * The Parse implementation for the @{link UserModel} interface. Extends the {@link ParseBaseModel}
 * class and adds additional user-related functionality.
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
     * Key corresponding to {@link #getUsername()}
     */
    private static final String usernameKey = "username";

    /**
     * Key corresponding to {@link #getEmailVerified()}.
     */
    private static final String emailVerifiedKey = "emailVerified";

    /**
     * Key corresponding to {@link #getEmail()} and {@link #setEmail(String)}.
     */
    private static final String emailKey = "email";

    /**
     * Key corresponding to {@link #getPhone()} and {@link #setPhone(String)}.
     */
    private static final String phoneKey = "phone";

    /**
     * Key corresponding to {@link #getProfile()} .
     */
    private static final String profileKey = "CCUser";



    /**
     * Internal reference to the {@link ParseUserProfileModel} linked to this user. This value
     * should never be {@code null}.
     */
    private ParseUserProfileModel userProfileModel;



    /**
     * Forwarding class constructor. Invokes
     * {@link #ParseUserModel(ParseUser, ParseUserProfileModel)} with the {@code profile} parameter
     * set to {@code null}.
     *
     * @param object The {@link ParseUser} object to use as a handle.
     */
    public ParseUserModel(ParseUser object) {
        this(object, null);
    }

    /**
     * The class constructor. Initializes the model from an existing {@link ParseUser}.
     *
     * @param object The {@link ParseUser} object to use as a handle.
     * @param profile The {@link ParseUserProfileModel} object that is associated with this model.
     *                If this parameter is {@code null}, then a new {@link ParseUserProfileModel}
     *                will be instantiated and used in its stead.
     */
    public ParseUserModel(ParseUser object, ParseUserProfileModel profile) {
        super(object);

        // TODO: Create a profile model if none exists
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
        return parseObject.getBoolean(emailVerifiedKey);
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
        return parseObject.getString(phoneKey);
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
     * Gets the {@link ParseUserProfileModel} object linked to this {@link ParseUserModel} object.
     *
     * @return The {@link ParseUserProfileModel} linked to this model.
     */
    @Override
    public ParseUserProfileModel getProfile() {
        return userProfileModel;
    }
}
