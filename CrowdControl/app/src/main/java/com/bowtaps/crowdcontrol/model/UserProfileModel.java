package com.bowtaps.crowdcontrol.model;

import com.parse.ParseUser;

/**
 * The interface for user profile models, providing access to public-facing
 * user profile data, such as display name and profile image.
 *
 * @author Daniel Andrus
 * @since 2015-11-27
 */
public interface UserProfileModel extends BaseModel {

    /**
     * Gets the display name of the current user. This value is not unique,
     * is chosen by the user, should always be used to represent the user
     * when presented to other users, and should never be used as an index
     * key. This value can and should, however, be indexed for use in text
     * searches.
     *
     * Additionally, the user's display name can be changed and thus should
     * calls to this function should be performed relatively frequently.
     *
     * @return String of the user's self-chosen display name.
     */
    public String getDisplayName();

    /**
     * Sets the user's display name that will be seen by other users.
     *
     * @param displayName The new display name for the user.
     */
    public void setDisplayName(String displayName);

    public String getDisplayDatabaseID();

    public void setInheritedUser( ParseUser inheritedUser);

}
