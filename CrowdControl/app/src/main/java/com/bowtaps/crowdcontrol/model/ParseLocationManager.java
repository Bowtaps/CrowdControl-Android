package com.bowtaps.crowdcontrol.model;

import java.util.List;

/**
 * The Parse implementation of the location manager. Fully implements the
 * @{link LocationManager} interface.
 *
 * Created by Joseph Mowry on 1/21/2016.
 */
public class ParseLocationManager implements LocationManager {

    /**
     * TODO: doc me, Doc!
     */
    private List<ParseLocationModel> memberLocations;

    /**
     * TODO: doc me, Doc!
     */
    private int transmissionInterval;

    /**
     * Retrieves the locations of other members of the current group.
     *
     * @throws Exception Throws the exception
     */
    @Override
    public List<ParseLocationModel> getLocations() throws Exception {
        return null;
    }

    /**
     * Retrieves the interval for sending/receiving location data, set by the user.
     */
    @Override
    public int getInterval() {
        return 0;
    }

    /**
     * Sets the interval for sending/receiving location data, set by the user.
     *
     * @param interval The desired interval between sending/receiving location data amongst other
     *                 users.
     */
    @Override
    public void setInterval(int interval) {

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
}
