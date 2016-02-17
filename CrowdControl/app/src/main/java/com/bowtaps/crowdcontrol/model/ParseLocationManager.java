package com.bowtaps.crowdcontrol.model;

import android.content.Context;
import android.util.Log;
import android.location.LocationManager;

import com.bowtaps.crowdcontrol.CrowdControlApplication;
import com.bowtaps.crowdcontrol.location.GoogleLocationListener;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Parse implementation of the location manager. Fully implements the
 * @{link LocationManager} interface.
 *
 * Created by Joseph Mowry on 1/21/2016.
 */
public class ParseLocationManager implements SecureLocationManager {

    /**
     * Cached locations, cached by the profile ID of the user they belong to.
     */
    private Map<String, LocationModel> memberLocations;

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
        memberLocations = new HashMap<>();
        //listener = new GoogleLocationListener();
        //locationManager = (LocationManager) CrowdControlApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);

    }

    public void initializeLocationRequest()
    {
        if(this.listener == null){
            this.listener = new GoogleLocationListener();
        }
        if(this.locationManager == null) {
            this.locationManager = (LocationManager) CrowdControlApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);
            try{
                if (this.locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 1000, this.listener);
                }

            }catch(SecurityException e1){
                //do something
                Log.e("Security Exception", e1.toString());
            }
        }
    }

    /**
     * @see SecureLocationManager#getLocations()
     */
    @Override
    public List<LocationModel> getLocations() {
        return new ArrayList<>(memberLocations.values());
    }

    /**
     * @see SecureLocationManager#loadAllLocations()
     */
    @Override
    public List<LocationModel> loadAllLocations() throws ParseException {
        this.fetchMembersLocations();
        this.transmitting = true;
        this.broadcastLocation();
        return getLocations();
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
    public LatLng getCurrentLocation() {
        //Get the current location of this device and set the variables

        Double latitude = listener.getLatitude();
        Double longitude = listener.getLongitude();

        return new LatLng(latitude, longitude);
    }

    @Override
    public void updateLocations(Collection<? extends LocationModel> locations) {

        // Add each location in collection to cache
        if (locations != null) {
            for (LocationModel location : locations) {
                if (location.getFrom() != null) {
                    memberLocations.put(location.getFrom().getId(), location);
                }
            }
        }
    }

    /**
     * @see SecureLocationManager#getUserLocation(UserProfileModel)
     */
    @Override
    public LocationModel getUserLocation(UserProfileModel userProfileModel) {

        // Verify parameters
        if (userProfileModel == null) {
            return null;
        }

        // Return cached data, if any
        if (memberLocations.containsKey(userProfileModel.getId())) {
            return memberLocations.get(userProfileModel.getId());
        } else {
            return null;
        }
    }

    /**
     * @see SecureLocationManager#getUserLocations(Collection)
     */
    public List<LocationModel> getUserLocations(Collection<? extends UserProfileModel> users) {
        List<LocationModel> locations = new ArrayList<>();

        if (users != null) {
            for (UserProfileModel user : users) {
                LocationModel location = getUserLocation(user);
                if (location != null) {
                    locations.add(location);
                }
            }
        }

        return locations;
    }

    private void broadcastLocation(){
        //if the transmitting flag is set to true, send the data
        //if the transmitting flag is set to false, leave function
        if(this.transmitting){
            ParseLocationModel.broadcastLocation();
        }
    }
    private void fetchMembersLocations() throws ParseException {
        memberLocations.clear();
        updateLocations(ParseLocationModel.fetchMemberLocations());
    }
}
