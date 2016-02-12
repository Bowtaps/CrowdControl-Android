package com.bowtaps.crowdcontrol.model;

import android.util.Pair;

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
     * TODO: Doc me, Doc!
     */
    public void initializeLocationRequest();

    /**
     * Gets a complete list of all known {@link LocationModel}s. To load all known locations from
     * storage, see {@link #loadAllLocations()}.
     *
     * @return The {@link List} of cached {@link LocationModel} objects.
     */
    public List<? extends LocationModel> getLocations();

    /**
     * Fetches all known locations from storage. This is a blocking function.
     *
     * @return The {@link List} of {@link LocationModel} objects loaded from storage.
     */
    public List<? extends LocationModel> loadAllLocations() throws Exception;

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

    public LatLng getCurrentLocation();

    /**
     * Updates cached locations using a provided {@link Collection} of {@link LocationModel}s.
     *
     * @param locations The {@link Collection} of {@link LocationModel} objects to update cache for.
     */
    public void updateLocations(Collection<? extends LocationModel> locations);

    /**
     * Gets the cached {@link LocationModel} object for the requested {@link UserProfileModel}.
     *
     * @param userProfileModel The {@link UserProfileModel} of the user to get the cached location
     *                         of. If no location for the supplied user is cached, the {@code null}
     *                         will be returned.
     */
    public LocationModel getUserLocation(UserProfileModel userProfileModel);

    /**
     * Gets all cached locations for all users in the given {@link Collection}.
     *
     * @param users The {@link Collection} of {@link UserProfileModel}s to retrieve locations for.
     *
     * @return The cached list of {@link LocationModel}s belonging to the requested users.
     */
    public List<? extends LocationModel> getUserLocations(Collection<? extends UserProfileModel> users);
}