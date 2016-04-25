package com.bowtaps.crowdcontrol.model;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.location.LocationManager;

import com.bowtaps.crowdcontrol.CrowdControlApplication;
import com.bowtaps.crowdcontrol.location.GoogleLocationListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Parse implementation of the location manager. Fully implements the
 * {@link LocationManager} interface.
 *
 * @uthor Joseph Mowry
 * @since 1/21/2016.
 */
public class ParseLocationManager implements SecureLocationManager {

    /**
     * Cached locations, cached by the profile ID of the user they belong to.
     */
    private Map<String, LocationModel> memberLocations;

    /**
     * Number of milliseconds to wait between automatic transmissions.
     */
    private int transmissionInterval;

    /**
     * Flag indicating that the user's location is being automatically transmitted.
     */
    private boolean transmitting;

    /**
     * The listener object tied to Google's location services. Used for detecting location updates.
     */
    private GoogleLocationListener listener;

    /**
     * The Android location manager that allows this manager to work with the device's location
     * services and functionality.
     */
    private LocationManager locationManager;


    /**
     * Default constructor. Initializes class properties.
     */
    public ParseLocationManager() {
        transmissionInterval = 10;
        memberLocations = new HashMap<>();
    }

    /**
     * @see SecureLocationManager#initializeLocationRequest()
     */
    @Override
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
     * @see SecureLocationManager#getInterval()
     */
    @Override
    public int getInterval() {
        return transmissionInterval;
    }

    /**
     * @see SecureLocationManager#setInterval(int)
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
     * @see SecureLocationManager#startTransmission()
     */
    @Override
    public void startTransmission() {
        // TODO
    }

    /**
     * @see SecureLocationManager#stopTransmission()
     */
    @Override
    public void stopTransmission() {
        // TODO
    }

    /**
     * @see SecureLocationManager#getTransmitting()
     */
    @Override
    public boolean getTransmitting(){
        return transmitting;
    }

    /**
     * @see SecureLocationManager#setTransmitting(boolean)
     */
    @Override
    public void setTransmitting(boolean transmitting){
        transmitting = transmitting;
    }

    /**
     * @see SecureLocationManager#getCurrentLocation()
     */
    @Override
    public LatLng getCurrentLocation() {
        //Get the current location of this device and set the variables

        Double latitude = listener.getLatitude();
        Double longitude = listener.getLongitude();

        return new LatLng(latitude, longitude);
    }

    /**
     * @see SecureLocationManager#updateLocations(Collection)
     */
    @Override
    public void updateLocations(Collection<? extends LocationModel> locations) {
        List<?extends UserProfileModel> groupMembers = CrowdControlApplication.getInstance().getModelManager().getCurrentGroup().getGroupMembers();
        UserProfileModel me = CrowdControlApplication.getInstance().getModelManager().getCurrentUser().getProfile();
        // Add each location in collection to cache
        if (locations != null) {
            for (LocationModel location : locations) {
                if (location.getFrom() != null) {
                    try{
                        location.getFrom().getDisplayName();
                    }catch(Exception e) {
                        Log.e("Exception", e.toString());
                        for (UserProfileModel user : groupMembers) {
                            if (location.getFrom().getId().equals(user.getId())) {
                                location.setFrom(user);
                                Log.d("Updating Location", location.getFrom().getDisplayName());
                                break;
                            }
                        }
                    }
                    location.setTo(me);
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
    @Override
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

    /**
     * @see SecureLocationManager#broadcastLocation()
     */
    @Override
    public void broadcastLocation(){
        //if the transmitting flag is set to true, send the data
        Log.d("LocationManager", "Bcasting location");
        ParseLocationModel.broadcastLocation();
    }
}
