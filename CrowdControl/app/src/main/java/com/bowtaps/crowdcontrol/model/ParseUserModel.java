package com.bowtaps.crowdcontrol.model;

import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * The Parse implementation for the {@link UserModel} interface. Extends the {@link ParseBaseModel}
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
     * {@link #ParseUserModel(ParseUser, ParseUserProfileModel)} with a new
     * {@link ParseUserProfileModel} object, which will create a new row in the Parse database if
     * it is saved.
     *
     * @param object The {@link ParseUser} object to use as a handle.
     */
    public ParseUserModel(ParseUser object) {
        this(object, null);
    }

    /**
     * The class constructor. Initializes the model from an existing {@link ParseUser} and
     * {@link ParseObject} profile.
     *
     * @param object The {@link ParseUser} object to use as a handle.
     * @param profile The {@link ParseUserProfileModel} object that is associated with this model.
     *                If this value is {@code null}, then one will be provided if possible.
     */
    public ParseUserModel(ParseUser object, ParseUserProfileModel profile) {
        super(object);

        // Resolve a potentially missing profile
        if (profile == null) {
            ParseObject profileObject = object.getParseObject(profileKey);
            if (profileObject == null) {
                profile = new ParseUserProfileModel(); // TODO THIS NEEDS TO BE REMOVED
                object.put(profileKey, profile.getParseObject());
            } else {
                profile = ParseUserProfileModel.createFromParseObject(profileObject);
            }

        }

        userProfileModel = profile;
    }

    /**
     * @see UserModel#getUsername()
     */
    @Override
    public String getUsername() {
        return ((ParseUser) getParseObject()).getUsername();
    }

    /**
     * @see UserModel#getEmailVerified()
     */
    @Override
    public Boolean getEmailVerified() {
        return getParseObject().getBoolean(emailVerifiedKey);
    }

    /**
     * @see UserModel#getEmail()
     */
    @Override
    public String getEmail() {
        return ((ParseUser) getParseObject()).getEmail();
    }

    /**
     * @see UserModel#setEmail(String)
     */
    @Override
    public void setEmail(String email) {
        ((ParseUser) getParseObject()).setEmail(email);
    }

    /**
     * @see UserModel#getPhone()
     */
    @Override
    public String getPhone() {
        return getParseObject().getString(phoneKey);
    }

    /**
     * @see UserModel#setPhone(String)
     */
    @Override
    public void setPhone(String phone) {
        getParseObject().put(phoneKey, phone);
    }

    /**
     * @see UserModel#getProfile()
     */
    @Override
    public ParseUserProfileModel getProfile() {
        return userProfileModel;
    }

    /**
     * @see UserModel#setProfile(UserProfileModel)
     */
    @Override
    public void setProfile(UserProfileModel profile) {
        if (profile == null || !(profile instanceof ParseUserProfileModel)) {
            throw new IllegalArgumentException("parameter 'profile' must be instance of ParseUserProfileModel");
        }

        userProfileModel = (ParseUserProfileModel) profile;
    }


    /**
     * Creates a new instance of this class using the provided username and password. Does not save
     * the object to storage (see {@link #save()}.
     *
     * @param username The username to assign to the new user.
     * @param password The password to assign to the new user.
     * @return Returns the newly created instance of this class or {@code null} if unable to create
     * the object.
     */
    public static ParseUserModel createFromSignUp(String username, String password) {

        // Create new profile object
        ParseUserProfileModel profileModel = new ParseUserProfileModel();

        // Create new Parse user and set attributes
        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(username);
        parseUser.setPassword(password);
        parseUser.put(profileKey, profileModel.getParseObject());

        // Create new user model
        return new ParseUserModel(parseUser, profileModel);
    }

    /**
     * Creates a new instance of this class using the provided {@link ParseObject} as the underlying
     * handle into the database.
     *
     * @param object The database handle to use when creating this class.
     * @return The newly created instance of this class that uses the provided {@link ParseObject}
     * as the underlying object or {@code null} if unable to create the object.
     */
    public static ParseUserModel createFromParseObject(ParseObject object) {

        // Verify parameters
        if (object == null) {
            return null;
        }
        if (!(object instanceof ParseUser)) {
            return null;
        }

        // Instantiate a new object
        return new ParseUserModel((ParseUser) object);
    }
}
