package com.bowtaps.crowdcontrol.model;

import android.util.Pair;

import java.util.List;

/**
 * The location manager interface, providing access to core location management functionality,
 * including:
 *
 * - retrieve group member locations
 * - get/set interval values for sending/receiving locations
 * - methods to start/stop the transmission of location data
 *
 * Created by Joseph Mowry on 1/21/2016.
 */
public interface SecureLocationManager {

    /**
     * Retrieves the locations of other members of the current group.
     *
     * @throws Exception Throws the exception
     */
    public List<? extends LocationModel> getLocations() throws Exception;

    /**
     * Retrieves the interval for sending/receiving location data, set by the user.
     */
    public int getInterval();

    /**
     * Sets the interval for sending/receiving location data, set by the user.
     *
     * @param interval The desired interval between sending/receiving location data amongst other
     *                 users.
     */
    public void setInterval(int interval);

    /**
     * Initiates the transmission of location data. This includes sending locations to others and
     * receiving locations from others.
     */
    public void startTransmission();

    /**
     * Halts the transmission of location data. This includes sending locations to others and
     * receiving locations from others.
     */
    public void stopTransmission();

    public boolean getTransmitting();

    public void setTransmitting(boolean transmitting);

    public Pair<Double, Double> getCurrentLocation();
}
