package com.bowtaps.crowdcontrol.model;

import java.security.acl.Group;
import java.util.List;

/**
 * Created by 1959760 on 10/24/2015.
 */
public class ParseGroupModel implements GroupModel{

    public ParseGroupModel()
    {
        // Initialize Fields
        ParseUserModel GroupLeader;
        GroupName = null;
        Itinerary = null;
        Status = null;
        Waypoints = null;
        List<ParseUserModel> GroupMembers;

    }
    // Fields
    private String GroupName;
    private Object Itinerary;
    private String Status;
    private Object Waypoints;


    // Get Methods
    @Override
    public String getGroupName(){
        return this.getGroupName();
    }
    @Override
    public Object getItinerary(){
        return this.Itinerary;
    }
    @Override
    public String getStatus(){
        return this.Status;
    }
    @Override
    public Object getWaypoints(){
        return this.Waypoints;
    }

    // Set Methods
    @Override
    public void setGroupName(String groupName){
        this.GroupName = groupName;
    }
    @Override
    public void setItinerary(Object itinerary){
        this.Itinerary = itinerary;
    }
    @Override
    public void setStatus(String status ){
        this.Status = status;
    }
    @Override
    public void setWaypoints(Object waypoints){
        this.Waypoints = waypoints;
    }

}
