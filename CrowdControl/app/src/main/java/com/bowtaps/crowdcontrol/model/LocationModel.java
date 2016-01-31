package com.bowtaps.crowdcontrol.model;

import com.parse.Parse;

import java.util.List;

/**
 * Created by Evan Hammer on 1/19/16.
 */
public interface LocationModel extends BaseModel {

    /**
     * Gets the latitude variable, converts it from a string to a double and
     *  returns it.
     *
     * @return latitude as a double.
     */
    public double getLatitude();

    /**
     * Gets the longitude variable, converts it from a string to a double
     * and returns it.
     *
     * @return longitude as a double.
     */
    public double getLongitude();

    /**
     * Gets the To field and returns the UserProfileModel.
     *
     * @return UserProfileModel containing the user who is receiving the
     *          location.
     */
    public UserProfileModel getTo();

    /**
     * Gets the From field and returns the UserProfileModel.
     *
     * @return UserProfileModel containing the user who sent the location.
     */
    public UserProfileModel getFrom();

    /**
     * Sets the Latitude variable, converting from a double to a string.
     *
     * @param latitude - value for the latitudinal coordinate.
     */
    public void setLatitude(double latitude);

    /**
     * Sets the Longitude variable, converting from a double to a string.
     *
     * @param longitude - value for the longitudinal coordinate.
     */
    public void setLongitude(double longitude);

    /**
     * Sets the To field, the user Receiving the location
     *
     * @param userProfileModel - UserProfileModel of the location recipient
     */
    public void setTo(UserProfileModel userProfileModel);

    /**
     * Sets the From field, the user that sent the location.
     *
     * @param userProfileModel - UserProfileModel of the location sender
     */
    public void setFrom(UserProfileModel userProfileModel);
}
