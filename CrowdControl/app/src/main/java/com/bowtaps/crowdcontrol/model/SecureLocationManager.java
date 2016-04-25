package com.bowtaps.crowdcontrol.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.Collection;
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
     * Initializes a new location request.
     */
    void initializeLocationRequest();

    /**
     * Gets a complete list of all cached {@link LocationModel}s.
     *
     * @return The {@link List} of cached {@link LocationModel} objects.
     */
    List<? extends LocationModel> getLocations();

    /**
     * Retrieves the interval for sending/receiving location data, set by the user.
     */
    int getInterval();

    /**
     * Sets the interval for sending/receiving location data, set by the user.
     *
     * @param interval The desired interval between sending/receiving location data amongst other
     *                 users.
     */
    void setInterval(int interval);

    /**
     * Initiates the transmission of location data. This includes sending locations to others and
     * receiving locations from others.
     */
    void startTransmission();

    /**
     * Halts the transmission of location data. This includes sending locations to others and
     * receiving locations from others.
     */
    void stopTransmission();

    /**
     * Gets whether or not this manager is currently transmitting the user's location.
     *
     * @return {@code true} if the manager is transmitting, {@code false} if not.
     */
    boolean getTransmitting();

    /**
     * Sets the internal flag for whether the current manager is transmitting or not.
     *
     * @param transmitting The value to set the {@link #getTransmitting()} flag to.
     */
    void setTransmitting(boolean transmitting);

    /**
     * Gets the current location of the current user.
     *
     * @return Returns the {@link LatLng} object representing the user's location.
     */
    LatLng getCurrentLocation();

    /**
     * Updates cached locations using a provided {@link Collection} of {@link LocationModel}s.
     *
     * @param locations The {@link Collection} of {@link LocationModel} objects to update cache for.
     */
    void updateLocations(Collection<? extends LocationModel> locations);

    /**
     * Gets the cached {@link LocationModel} object for the requested {@link UserProfileModel}.
     *
     * @param userProfileModel The {@link UserProfileModel} of the user to get the cached location
     *                         of. If no location for the supplied user is cached, the {@code null}
     *                         will be returned.
     */
    LocationModel getUserLocation(UserProfileModel userProfileModel);

    /**
     * Gets all cached locations for all users in the given {@link Collection}.
     *
     * @param users The {@link Collection} of {@link UserProfileModel}s to retrieve locations for.
     *
     * @return The cached list of {@link LocationModel}s belonging to the requested users.
     */
    List<? extends LocationModel> getUserLocations(Collection<? extends UserProfileModel> users);

    /**
     * Broadcasts the current user's location to all other members in the group and stores the data
     * in storage.
     */
    void broadcastLocation();
}
