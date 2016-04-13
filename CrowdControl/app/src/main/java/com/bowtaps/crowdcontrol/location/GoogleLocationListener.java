package com.bowtaps.crowdcontrol.location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Evan Hammer on 1/26/16.
 */
public class GoogleLocationListener implements LocationListener {
    private Double longitude = 0.0;
    private Double latitude = 0.0;
    private int status;
    private boolean providerEnabled;

    public GoogleLocationListener(){
        Log.d("GoogleLocationListener","Constructer");
    }




    @Override
    public void onLocationChanged(Location location) {

        //Checks to see if the device running this app is an emulator
        if(Build.FINGERPRINT.contains("generic")) {
            this.latitude = 44.07082231;      //Joe's house
            this.longitude = -103.25872087;
            Log.d("Emulation", "It's running on an emulator");
        }

        else{
            this.longitude = location.getLongitude();
            this.latitude = location.getLatitude();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        this.status = status;
    }

    @Override
    public void onProviderEnabled(String provider) {
        this.providerEnabled = true;
    }

    @Override
    public void onProviderDisabled(String provider) {
        this.providerEnabled = false;
    }

    public Double getLongitude(){
        return this.longitude;
    }
    public Double getLatitude(){
        return this.latitude;
    }
    public void setLocation(LatLng newLoc){
        longitude = newLoc.longitude;
        latitude = newLoc.latitude;
    }
}
