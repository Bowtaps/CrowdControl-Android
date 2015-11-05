package com.bowtaps.crowdcontrol.model;

/**
 * Created by 1959760 on 10/28/2015.
 */
public interface GroupModel {
    // Get Methods
    public String getGroupName();
    public Object getItinerary();
    public String getStatus();
    public Object getWaypoints();

    // Set Methods
    public void setGroupName(String groupName);
    public void setItinerary(Object itinerary);
    public void setStatus(String status);
    public void setWaypoints(Object waypoints);
}
