package com.bowtaps.crowdcontrol.model;

import android.support.annotation.Nullable;
import android.util.Log;

import com.bowtaps.crowdcontrol.CrowdControlApplication;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Evan Hammer
 * @since 2016-01-19
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
        try {
            return Double.parseDouble(getParseObject().getString(latitudeKey));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /**
     * Gets the longitude variable, converts it from a string to a double
     * and returns it.
     *
     * @return longitude as a double.
     */
    @Override
    public double getLongitude(){
        try {
            return Double.parseDouble(getParseObject().getString(longitudeKey));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /**
     * Gets the To field and returns the UserProfileModel.
     *
     * @return UserProfileModel containing the user who is receiving the
     *          location.
     */
    @Override
    public ParseUserProfileModel getTo() {
        return ParseUserProfileModel.createFromParseObject(getParseObject().getParseObject(toKey));
    }

    /**
     * Gets the From field and returns the UserProfileModel.
     *
     * @return UserProfileModel containing the user who sent the location.
     */
    @Override
    public ParseUserProfileModel getFrom() {
        return ParseUserProfileModel.createFromParseObject(getParseObject().getParseObject(fromKey));
    }

    /**
     * Sets the Latitude variable, converting from a double to a string.
     *
     * @param latitude - value for the latitudinal coordinate.
     */
    @Override
    public void setLatitude(double latitude){
        getParseObject().put(latitudeKey, latitude + "");
    }

    /**
     * Sets the Longitude variable, converting from a double to a string.
     *
     * @param longitude - value for the longitudinal coordinate.
     */
    @Override
    public void setLongitude(double longitude){
        getParseObject().put(longitudeKey, longitude + "");
    }

    /**
     * Sets the To field, the user Receiving the location
     *
     * @param userProfileModel - UserProfileModel of the location recipient
     */
    @Override
    public void setTo(UserProfileModel userProfileModel){

        // Verify parameters
        if (userProfileModel == null) return;
        if (!(userProfileModel instanceof ParseUserProfileModel)) return;

        // set the user profile
        getParseObject().put(toKey, ((ParseUserProfileModel) userProfileModel).getParseObject());
    }

    /**
     * Sets the From field, the user that sent the location.
     *
     * @param userProfileModel - UserProfileModel of the location sender
     */
    @Override
    public void setFrom(UserProfileModel userProfileModel){

        // Verify parameters
        if (userProfileModel == null) return;
        if (!(userProfileModel instanceof ParseUserProfileModel)) return;

        // set the user profile
        getParseObject().put(fromKey, ((ParseUserProfileModel) userProfileModel).getParseObject());
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
        ParseObject toUser = getParseObject().getParseObject(toKey);
        toUser.fetch();

        // Fetch the "from" pointer
        ParseObject fromUser = getParseObject().getParseObject(toKey);
        fromUser.fetch();
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
        sendLocationToList((List<UserProfileModel>)groupMembers);
    }
    public static void sendLocationToList(List<UserProfileModel> members){
        Log.d("Testing SendLocation", members.toString());
        UserProfileModel me = CrowdControlApplication.getInstance().getModelManager().getCurrentUser().getProfile();
        LatLng currentLocation = CrowdControlApplication.getInstance().getLocationManager().getCurrentLocation();

        for(UserProfileModel profile: members){
            if(!profile.getDisplayName().equals(me.getDisplayName())){
                ParseObject obj = ParseObject.create(tableName);
                Double lat = currentLocation.latitude;
                Double lng = currentLocation.longitude;
                Log.d("Location: ", "Lat: " + lat + "\nLong: " + lng);
                obj.put(longitudeKey, lng.toString());
                obj.put(latitudeKey, lat.toString());
                LocationModel loc = new ParseLocationModel(obj);
                loc.setFrom(me);
                loc.setTo(profile);
                try {
                    loc.save();
                } catch (Exception e) {
                    Log.e("Saving Exception: ", "Error: " + e);
                }
            }
        }
    }

    /**
     * Gathers the locations meant for the user passed into the function from Parse and returns a
     * list of ParseLocationModel objects.
     *
     * @throws ParseException Throws If an exception occurs, throws a {@link ParseException}.
     * @param user The {@link UserProfileModel} of a user to find the locations on parse that were
     *             meant for them.
     *
     * @return A {@link List} of {@link ParseLocationModel} objects that are meant for the user that
     * was passed in.
     */
    public static List<ParseLocationModel> fetchLocationsSentToUser(UserProfileModel user) throws ParseException{
        if (user != null && !(user instanceof ParseUserProfileModel)) {
            throw new IllegalArgumentException("user parameter must be an instance of ParseUserProfileModel");
        }
        List<ParseLocationModel> groupMemberLocations = new ArrayList<>();
        ParseQuery query = ParseQuery.getQuery(tableName);
        query.whereEqualTo(toKey, ((ParseUserProfileModel) user).getParseObject());
        List<ParseObject> response = query.find();
        ParseLocationModel locationModel;
        for (ParseObject obj: response) {
            locationModel = new ParseLocationModel(ParseObject.create(tableName));
            String latitude = obj.get(latitudeKey).toString();
            String longitude = obj.get(longitudeKey).toString();
            locationModel.setLatitude(Double.parseDouble(latitude));
            locationModel.setLongitude(Double.parseDouble(longitude));
            locationModel.setTo(user);
            Object from = obj.get(fromKey);
            ParseUserProfileModel fromProfile = new ParseUserProfileModel((ParseObject)from);
            fromProfile.load();
            locationModel.setFrom(fromProfile);
            groupMemberLocations.add(locationModel);
        }
        return groupMemberLocations;
    }

    /**
     * Gathers the locations sent from the user passed into the function from Parse and returns a
     * list of ParseLocationModel objects.
     *
     * @throws ParseException Throws If an exception occurs, throws a {@link ParseException}.
     * @param user The {@link UserProfileModel} of a user to find the locations on parse that were
     *             sent by them.
     *
     * @return A {@link List} of {@link ParseLocationModel} objects that are meant for the user that
     * was passed in.
     */
    public static List<ParseLocationModel> fetchLocationsSentFromUser(UserProfileModel user) throws ParseException{
        if (user != null && !(user instanceof ParseUserProfileModel)) {
            throw new IllegalArgumentException("user parameter must be an instance of ParseUserProfileModel");
        }
        List<ParseLocationModel> previousLocations = new ArrayList<>();
        ParseQuery query = ParseQuery.getQuery(tableName);
        query.whereEqualTo(fromKey, ((ParseUserProfileModel) user).getParseObject());
        List<ParseObject> response = query.find();
        ParseLocationModel locationModel;
        for(ParseObject obj: response){
            locationModel = new ParseLocationModel(ParseObject.create(tableName));
            locationModel.setLatitude(Double.parseDouble(obj.getString(latitudeKey)));
            locationModel.setLongitude(Double.parseDouble(obj.getString(longitudeKey)));
            locationModel.setFrom(user);
            Object to = obj.get(toKey);
            ParseUserProfileModel toProfile = new ParseUserProfileModel((ParseObject) to);
            toProfile.load();
            locationModel.setTo(toProfile);
            previousLocations.add(locationModel);
        }
        return previousLocations;
    }

    /**
     * @param pObject
     * @return
     */
    @Nullable
    public static ParseLocationModel createFromParseObject(ParseObject pObject){
        if(pObject.getClassName().equals(tableName)){
            return new ParseLocationModel(pObject);
        }
        else{
            return null;
        }
    }

    /**
     * Creates an instance of a {@link ParseLocationModel} from a {@link UserProfileModel} to,
     * a {@link UserProfileModel} from, and a {@link LatLng} loc.
     *
     * @param to The {@link UserProfileModel} that represents the recipient of the location.
     * @param from The {@link UserProfileModel} that represents the sender of the location.
     * @param loc The {@link LatLng} object containing the longitude and latitude of the user.
     * @return A {@link ParseLocationModel} object containing all of the information passed in.
     */
    public static ParseLocationModel createLocationModel(UserProfileModel to, UserProfileModel from, LatLng loc){
        if (to != null && !(to instanceof ParseUserProfileModel)) {
            throw new IllegalArgumentException("to parameter must be an instance of ParseUserProfileModel");
        }
        if(from != null &&  !(from instanceof ParseUserProfileModel)){
            throw new IllegalArgumentException("from parameter must be an instance of ParseUserProfileModel");
        }
        ParseObject obj = ParseObject.create(tableName);
        obj.put(toKey,((ParseUserProfileModel) to).getParseObject());
        obj.put(fromKey, ((ParseUserProfileModel) from).getParseObject());
        obj.put(latitudeKey, ((Double) loc.latitude).toString());
        obj.put(longitudeKey, ((Double) loc.longitude).toString());
        return new ParseLocationModel(obj);
    }
}
