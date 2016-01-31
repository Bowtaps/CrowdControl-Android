package com.bowtaps.crowdcontrol.model;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.location.LocationManager;
import android.location.Location;

import com.bowtaps.crowdcontrol.CrowdControlApplication;
import com.bowtaps.crowdcontrol.Location.GoogleLocationListener;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

/**
 * The Parse implementation of the location manager. Fully implements the
 * @{link LocationManager} interface.
 *
 * Created by Joseph Mowry on 1/21/2016.
 */
public class ParseLocationManager implements SecureLocationManager {

    /**
     * TODO: doc me, Doc!
     */
    private List<LocationModel> memberLocations;

    /**
     * TODO: doc me, Doc!
     */
    private int transmissionInterval;

    /**
     * TODO: doc me, Doc!
     */
    private boolean transmitting;

    private GoogleLocationListener listener;
    private LocationManager locationManager;

    /**
     * TODO: doc me, Doc!
     */
    public ParseLocationManager(){
        transmissionInterval = 10;
        memberLocations = null;
        listener = new GoogleLocationListener();
        locationManager = (LocationManager) CrowdControlApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);
        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 1000, listener);
        }catch(SecurityException e1){
            //do something
            Log.e("Security Exception", e1.toString());
        }
    }

    /**
     * Retrieves the locations of other members of the current group.
     *
     * @throws Exception Throws the exception
     */
    @Override
    public List<LocationModel> getLocations() throws Exception {
        //if null throw exception
        this.fetchMembersLocations();
        this.transmitting = true;
        this.broadcastLocation();
        return this.memberLocations;
    }

    /**
     * Retrieves the interval for sending/receiving location data, set by the user.
     */
    @Override
    public int getInterval() {
        return this.transmissionInterval;
    }

    /**
     * Sets the interval for sending/receiving location data, set by the user.
     *
     * @param interval The desired interval between sending/receiving location data amongst other
     *                 users.
     */
    @Override
    public void setInterval(int interval) {
        if(interval < 0){
            this.transmissionInterval = 0;
        }else {
            this.transmissionInterval = interval;
        }
    }

    /**
     * Initiates the transmission of location data. This includes sending locations to others and
     * receiving locations from others.
     */
    @Override
    public void startTransmission() {

    }

    /**
     * Halts the transmission of location data. This includes sending locations to others and
     * receiving locations from others.
     */
    @Override
    public void stopTransmission() {

    }

    public boolean getTransmitting(){
        return this.transmitting;
    }


    public void setTransmitting(boolean transmitting){
        this.transmitting = transmitting;
    }

    @Override
    public Pair<Double, Double> getCurrentLocation() {
        //Get the current location of this device and set the variables

        Double latitude = listener.getLatitude();
        Double longitude = listener.getLongitude();

        return Pair.create(latitude, longitude);
    }

    private void broadcastLocation(){
        //if the transmitting flag is set to true, send the data
        //if the transmitting flag is set to false, leave function
        if(this.transmitting){
            //Send out the data to all members of the group
            //First get the group members
            UserProfileModel me = CrowdControlApplication.getInstance().getModelManager().getCurrentUser().getProfile();
            GroupModel group = CrowdControlApplication.getInstance().getModelManager().getCurrentGroup();
            List<? extends UserProfileModel> groupMembers = group.getGroupMembers();
            ParseQuery oldLoc = ParseQuery.getQuery("Location");
            ParseObject from = ParseObject.createWithoutData("CCUser", me.getId());
            oldLoc.whereEqualTo("From", from);
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
                    ParseObject obj = ParseObject.create("Location");
                    Double lat = this.listener.getLatitude();
                    Double lng = this.listener.getLongitude();
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
    private void fetchMembersLocations() throws ParseException {
        //get the current user profile
        UserProfileModel profile = CrowdControlApplication.getInstance().getModelManager().getCurrentUser().getProfile();
        ParseQuery query = ParseQuery.getQuery("Location");
        ParseObject to = ParseObject.createWithoutData("CCUser", profile.getId());
        query.whereEqualTo("To", to);
        List<ParseObject> response = query.find();
        ParseLocationModel locationModel;
        this.memberLocations = null;
        this.memberLocations = new ArrayList<LocationModel>();
        for (ParseObject obj: response) {
            locationModel = new ParseLocationModel(ParseObject.create("Location"));
            String latitude = obj.get("Latitude").toString();
            String longitude = obj.get("Longitude").toString();
            locationModel.setLatitude(Double.parseDouble(latitude));
            locationModel.setLongitude(Double.parseDouble(longitude));
            locationModel.setTo(profile);
            ParseQuery profileQuery = ParseQuery.getQuery("CCUser");
            Object from = obj.get("From");
            ParseUserProfileModel fromProfile = new ParseUserProfileModel((ParseObject)from);
            fromProfile.load();
            boolean added = this.memberLocations.add(locationModel);
            if(!added){
                //Replace with throwing an exception
                Log.e("GroupList", "Error Adding a member to the list");
            }
        }
    }
}
