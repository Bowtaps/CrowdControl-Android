package com.bowtaps.crowdcontrol.model;

import android.content.Context;
import android.util.Log;
import android.location.LocationManager;

import com.bowtaps.crowdcontrol.CrowdControlApplication;
import com.bowtaps.crowdcontrol.location.GoogleLocationListener;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;

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
    public LatLng getCurrentLocation() {
        //Get the current location of this device and set the variables

        Double latitude = listener.getLatitude();
        Double longitude = listener.getLongitude();

        return new LatLng(latitude, longitude);
    }

    private void broadcastLocation(){
        //if the transmitting flag is set to true, send the data
        //if the transmitting flag is set to false, leave function
        if(this.transmitting){
            ParseLocationModel.broadcastLocation();
        }
    }
    private void fetchMembersLocations() throws ParseException {
        memberLocations = ParseLocationModel.fetchMemberLocations();
    }

    /**
     * public void updateLocations(List<LocationModel>){
     *     //Go through the memberLocations and update any locations that are contained in the list
     * }
     * checkout HashMaps
     */
}
