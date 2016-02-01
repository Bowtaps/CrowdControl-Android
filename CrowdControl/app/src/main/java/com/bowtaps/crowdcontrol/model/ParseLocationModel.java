package com.bowtaps.crowdcontrol.model;

import android.util.Log;

import com.bowtaps.crowdcontrol.CrowdControlApplication;
import com.google.android.gms.maps.model.LatLng;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

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
        Log.d("Latitude: ", this.latitude);
    }

    /**
     * Sets the Longitude variable, converting from a double to a string.
     *
     * @param longitude - value for the longitudinal coordinate.
     */
    @Override
    public void setLongitude(double longitude){
        this.longitude = Double.toString(longitude);
        Log.d("Longitude: ", this.longitude);
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

    /**
     * Loads this object from Parse storage synchronously. In addition to the normal functionality
     * inherited from {@link ParseBaseModel}, this method also fetches and caches the users who
     * are members of this group.
     *
     * @throws ParseException Throws If an exception occurs, throws a {@link ParseException}.
     *
     * @see ParseBaseModel#load()
     */
    @Override
    public void load() throws ParseException {
        super.load();

        // Fetch the "to" pointer
        ParseObject toUser = parseObject.getParseObject(toKey);
        if (toUser == null) {

            // No "to" pointer
            toUser = null;
        } else if (toUser == null || !toUser.equals(toUser)) {

            // New "to" pointer
            To = new ParseUserProfileModel(toUser);
        }

        // Fetch the "from" pointer
        ParseObject fromUser = parseObject.getParseObject(toKey);
        if (fromUser == null) {

            // No "from" pointer
            fromUser = null;
        } else if (fromUser == null || !fromUser.equals(fromUser)) {

            // New "from" pointer
            From = new ParseUserProfileModel(fromUser);
        }
    }
    public static List<LocationModel> fetchMemberLocations() throws ParseException{
        List<LocationModel> groupMemberLocations = new ArrayList<LocationModel>();;
        //get the current user profile
        UserProfileModel profile = CrowdControlApplication.getInstance().getModelManager().getCurrentUser().getProfile();
        ParseQuery query = ParseQuery.getQuery(tableName);
        ParseObject to = ParseObject.createWithoutData("CCUser", profile.getId());
        query.whereEqualTo(toKey, to);
        List<ParseObject> response = query.find();
        ParseLocationModel locationModel;
        for (ParseObject obj: response) {
            locationModel = new ParseLocationModel(ParseObject.create(tableName));
            String latitude = obj.get(latitudeKey).toString();
            String longitude = obj.get(longitudeKey).toString();
            locationModel.setLatitude(Double.parseDouble(latitude));
            locationModel.setLongitude(Double.parseDouble(longitude));
            locationModel.setTo(profile);
            ParseQuery profileQuery = ParseQuery.getQuery("CCUser");
            Object from = obj.get(fromKey);
            ParseUserProfileModel fromProfile = new ParseUserProfileModel((ParseObject)from);
            fromProfile.load();
            boolean added = groupMemberLocations.add(locationModel);
            if(!added){
                //Replace with throwing an exception
                Log.e("GroupList", "Error Adding a member to the list");
            }
        }
        return groupMemberLocations;
    }

    public static void broadcastLocation(){
        //Send out the data to all members of the group
        //First get the group members
        UserProfileModel me = CrowdControlApplication.getInstance().getModelManager().getCurrentUser().getProfile();
        GroupModel group = CrowdControlApplication.getInstance().getModelManager().getCurrentGroup();
        List<? extends UserProfileModel> groupMembers = group.getGroupMembers();
        LatLng currentLocation = CrowdControlApplication.getInstance().getLocationManager().getCurrentLocation();
        ParseQuery oldLoc = ParseQuery.getQuery(tableName);
        ParseObject from = ParseObject.createWithoutData("CCUser", me.getId());
        oldLoc.whereEqualTo(fromKey, from);
        try{
            List<ParseObject> results = oldLoc.find();
            for(ParseObject oldResult: results){
                oldResult.deleteInBackground();
            }
        }catch (ParseException e){
            Log.e("Parse Exception", e.toString());
        }
        //for each member of the group create the location model
        //create a location object from me to each group member
        for (UserProfileModel user: groupMembers) {
            if (user.getDisplayName() != me.getDisplayName()) {
                ParseObject obj = ParseObject.create(tableName);
                Double lat = currentLocation.latitude;
                Double lng = currentLocation.longitude;
                Log.d("Location: ", "Lat: " + lat + "\nLong: " + lng);
                obj.put("Longitude", lng.toString());
                obj.put("Latitude", lat.toString());
                LocationModel loc = new ParseLocationModel(obj);
                loc.setFrom(me);
                loc.setTo(user);
                try {
                    loc.save();
                } catch (Exception e) {
                    Log.e("Saving Exception: ", "Error: " + e);
                }
            } else {
                Log.i("ME: ", "It catches me in the group");
            }
        }
    }
}
