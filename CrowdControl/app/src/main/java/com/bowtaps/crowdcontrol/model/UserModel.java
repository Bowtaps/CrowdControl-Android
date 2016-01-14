package com.bowtaps.crowdcontrol.model;

import com.parse.LogInCallback;
import com.parse.ParseObject;

/**
 * The interface for login user models, providing access to private user
 * data, such as username, email, and phone number.
 *
 * @author Daniel Andrus
 * @since 2015-11-27
 */
public interface UserModel extends BaseModel {

    /**
     * Gets the username for the user. This value should not be changed, as it
     * is set at creation time and is a unique identifier for this object.
     *
     * @return The user's unique username.
     */
    public String getUsername();


    // Get Methods
    public String getObjectID();
    public String getUserName();
    public Object getAuthData();

    /**
     * Gets whether the user has verified their email address with the service.
     *
     * @return True if the user's email address has been verified, false if not.
     */
    public Boolean getEmailVerified();

    /**
     * Gets the user's email address.
     *
     * @return The user's email address.
     */
    public String getEmail();

    /**
     * Replaces the user's existing email address with a new one.
     *
     * @param email The new email address to assign to the user.
     */
    public void setEmail(String email);

    /**
     * Gets the user's phone number.
     *
     * @return The user's phone number.
     */
    public String getPhone();

    /**
     * Replaces the user's phone number with a new one.
     *
     * @param phone The new phone number to assign to the user.
     */
    public void setPhone(String phone);

    /**
     * Set all of the information for a User
     *
     * @param email The new email address to assign to the user.
     * @param password The new password being assigned to the user.
     * @param phone The new phone number to assign to the user.
     */
    public void setAllUserData ( String email, String password, String phone );

    /**
     * Log a user into parse
     *
     * @param email The new email address to assign to the user.
     * @param password The new password being assigned to the user.
     */
    public void logIntoParseUser ( String email, String password, LogInCallback logInCallback);

    public void setDisplayUser( ParseObject displayUser);


}
