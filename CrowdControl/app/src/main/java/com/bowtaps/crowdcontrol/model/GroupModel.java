package com.bowtaps.crowdcontrol.model;

import com.google.android.gms.vision.barcode.Barcode;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.List;

/**
 * The interface for group models.
 *
 * @author Joseph Mowry
 * @since 2015-11-28
 */
public interface GroupModel extends BaseModel {

    /**
     * Gets the general location of the group.
     *
     * @return The location of the group in the form of a ParseGeoPoint object.
     */
    public ParseGeoPoint getGeneralLocation();

    /**
     * Gets the description of the current group.
     *
     * @return The description string attached to the group.
     */
    public String getGroupDescription();

    /**
     * Gets the list of users associated with the current group.
     *
     * @return The list of users as ParseUserModel objects that belong to the
     *         group.
     */
    public ParseUser getGroupMembers();

    /**
     * Sets the properties for the current group.
     *
     * @return The list of users as ParseUserModel objects that belong to the
     *         group.
     */
    public void SetGroupData(String groupDescription, String groupName);

}