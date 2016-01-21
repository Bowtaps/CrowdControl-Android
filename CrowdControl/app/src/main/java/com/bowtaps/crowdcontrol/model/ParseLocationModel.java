package com.bowtaps.crowdcontrol.model;

import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Evan Hammer on 1/19/16.
 */
public class ParseLocationModel extends ParseBaseModel implements LocationModel{
    /**
     * The name of the table that this object is designed to interact with.
     */
    private static final String tableName = "Location";

    /**
     * Key corresponding to {@link ParseLocationModel#getTo()}
     */
    private static final String toKey = "To";

    /**
     * Key corresponding to {@link ParseLocationModel#getFrom()}
     */
    private static final String fromKey = "From";

    /**
     * Key corresponding to {@link ParseLocationModel#getLatitude()}
     */
    private static final String latitudeKey = "Latitude";

    /**
     * Key corresponding to {@link ParseLocationModel#getLongitude()}
     */
    private static final String longitudeKey = "Longitude";

    private String latitude;
    private String longitude;
    private ParseUserProfileModel To;
    private ParseUserProfileModel From;

    /**
     * The class constructor. Initializes the model from an existing
     * {@link ParseLocationModel}.
     *
     * @param object The object to use as a handle.
     */
    public ParseLocationModel(ParseObject object) {
        super(object);
    }

    /**
     * Gets the latitude variable, converts it from a string to a double and
     *  returns it.
     *
     * @return latitude as a double.
     */
    @Override
    public double getLatitude(){
        return Double.parseDouble(latitude);
    }

    /**
     * Gets the longitude variable, converts it from a string to a double
     * and returns it.
     *
     * @return longitude as a double.
     */
    @Override
    public double getLongitude(){
        return Double.parseDouble(longitude);
    }

    /**
     * Gets the To field and returns the UserProfileModel.
     *
     * @return UserProfileModel containing the user who is receiving the
     *          location.
     */
    @Override
    public UserProfileModel getTo(){
        return this.To;
    }

    /**
     * Gets the From field and returns the UserProfileModel.
     *
     * @return UserProfileModel containing the user who sent the location.
     */
    @Override
    public UserProfileModel getFrom(){
        return From;
    }

    /**
     * Sets the Latitude variable, converting from a double to a string.
     *
     * @param latitude - value for the latitudinal coordinate.
     */
    @Override
    public void setLatitude(double latitude){
        this.latitude = Double.toString(latitude);
    }

    /**
     * Sets the Longitude variable, converting from a double to a string.
     *
     * @param longitude - value for the longitudinal coordinate.
     */
    @Override
    public void setLongitude(double longitude){
        this.longitude = Double.toString(longitude);
    }

    /**
     * Sets the To field, the user Receiving the location
     *
     * @param userProfileModel - UserProfileModel of the location recipient
     */
    @Override
    public void setTo(UserProfileModel userProfileModel){
        //set the user profile
        if(userProfileModel == null){
            this.To = null;
        } else{
            parseObject.put(toKey, ((ParseUserProfileModel) userProfileModel).parseObject);
        }
    }

    /**
     * Sets the From field, the user that sent the location.
     *
     * @param userProfileModel - UserProfileModel of the location sender
     */
    @Override
    public void setFrom(UserProfileModel userProfileModel){
        //set the user profile
        if(userProfileModel == null){
            this.From = null;
        } else{
            parseObject.put(fromKey, ((ParseUserProfileModel) userProfileModel).parseObject);
        }
    }
}
