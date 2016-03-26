package com.bowtaps.crowdcontrol.model;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
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
    public ParseLocationManager() {
        transmissionInterval = 10;
        memberLocations = new HashMap<>();
    }

    public void initializeLocationRequest() {
        if (listener == null) {
            listener = new GoogleLocationListener();
        }
        if (locationManager == null) {
            locationManager = (LocationManager) CrowdControlApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);
            try {
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, transmissionInterval * 1000, 1000, this.listener);
                    List<String> providers = locationManager.getProviders(true);
                    Location bestLocation = null;
                    for (String provider : providers) {
                        Location loc = locationManager.getLastKnownLocation(provider);
                        if (loc == null) {
                            continue;
                        }
                        if (bestLocation == null || loc.getAccuracy() < bestLocation.getAccuracy()) {
                            Log.d("last known location", loc.toString());
                            bestLocation = loc;
                        }
                    }
                    if (bestLocation == null) {
                        Log.d("Location Manager", "No location");
                    } else {
                        LatLng firstLoc = new LatLng(bestLocation.getLatitude(), bestLocation.getLongitude());
                        listener.setLocation(firstLoc);
                    }
                }

            } catch (SecurityException e1) {
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
     * Retrieves the interval for sending/receiving location data, set by the user.
     */
    @Override
    public int getInterval() {
        return transmissionInterval;
    }

    /**
     * Sets the interval for sending/receiving location data, set by the user.
     *
     * @param interval The desired interval between sending/receiving location data amongst other
     *                 users.
     */
    @Override
    public void setInterval(int interval) {
        if (interval < 0) {
            transmissionInterval = 0;
        } else {
            transmissionInterval = interval;
        }
        try {
            locationManager.removeUpdates(listener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, transmissionInterval * 1000, 1000, this.listener);
        }catch (SecurityException e){
            Log.e("Location Manager", e.toString());
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
        return transmitting;
    }


    public void setTransmitting(boolean transmitting){
        transmitting = transmitting;
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
                Log.d("updateLocations", location.toString());

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

    public void broadcastLocation(){
        //if the transmitting flag is set to true, send the data
        //if the transmitting flag is set to false, leave function
//        if(this.transmitting){
//            ParseLocationModel.broadcastLocation();
//        }
        //List<UserProfileModel> groupMembers = (List<UserProfileModel>) CrowdControlApplication.getInstance().getModelManager().getCurrentGroup().getGroupMembers();
        ParseLocationModel.broadcastLocation();
    }
}
