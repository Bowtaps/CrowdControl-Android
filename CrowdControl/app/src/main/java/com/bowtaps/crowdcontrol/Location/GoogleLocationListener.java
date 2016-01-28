package com.bowtaps.crowdcontrol.Location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by Evan Hammer on 1/26/16.
 */
public class GoogleLocationListener implements LocationListener {
    private Double longitude = 0.0;
    private Double latitude = 0.0;
    private int status;
    private boolean providerEnabled;




    @Override
    public void onLocationChanged(Location location) {
        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();
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
}
