package com.bowtaps.crowdcontrol.model;

/**
 * @author Evan Hammer
 * @since 2016-01-19
 */
public interface LocationModel extends BaseModel {

    /**
     * Gets the latitude variable, converts it from a string to a double and
     *  returns it.
     *
     * @return latitude as a double.
     */
    double getLatitude();

    /**
     * Gets the longitude variable, converts it from a string to a double
     * and returns it.
     *
     * @return longitude as a double.
     */
    double getLongitude();

    /**
     * Gets the To field and returns the UserProfileModel.
     *
     * @return UserProfileModel containing the user who is receiving the
     *          location.
     */
    UserProfileModel getTo();

    /**
     * Gets the From field and returns the UserProfileModel.
     *
     * @return UserProfileModel containing the user who sent the location.
     */
    UserProfileModel getFrom();

    /**
     * Sets the Latitude variable, converting from a double to a string.
     *
     * @param latitude - value for the latitudinal coordinate.
     */
    void setLatitude(double latitude);

    /**
     * Sets the Longitude variable, converting from a double to a string.
     *
     * @param longitude - value for the longitudinal coordinate.
     */
    void setLongitude(double longitude);

    /**
     * Sets the To field, the user Receiving the location
     *
     * @param userProfileModel - UserProfileModel of the location recipient
     */
    void setTo(UserProfileModel userProfileModel);

    /**
     * Sets the From field, the user that sent the location.
     *
     * @param userProfileModel - UserProfileModel of the location sender
     */
    void setFrom(UserProfileModel userProfileModel);
}
