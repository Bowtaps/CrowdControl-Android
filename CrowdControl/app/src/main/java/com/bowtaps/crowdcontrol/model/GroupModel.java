package com.bowtaps.crowdcontrol.model;

import java.security.acl.Group;

/**
 * Created by 1959760 on 10/24/2015.
 */
public class GroupModel {

    public GroupModel()
    {
        // Initialize Fields
        // TODO: user GroupLeader;
        GroupName = null;
        Itinerary = null;
        Status = null;
        Waypoints = null;
        // TODO: List of users in the group

    }
    // Fields
    public String GroupName;
    public Object Itinerary;
    public String Status;
    public Object Waypoints;


    // Methods
//        GetGroupModel();
//        GetGroupName();
//        GetItinerary();
//        GetStatus();
//        GetWaypoints();
}
