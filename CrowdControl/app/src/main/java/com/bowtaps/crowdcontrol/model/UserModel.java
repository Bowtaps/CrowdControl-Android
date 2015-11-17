package com.bowtaps.crowdcontrol.model;

import java.text.DateFormat;

/**
 * Created by 1959760 on 10/27/2015.
 */
public interface UserModel {

    // Get Methods
    public String getObjectID();
    public String getUserName();
    public Object getAuthData();
    public Boolean getEmailVerified();
    public String getEmail();
    public DateFormat getCreatedAt();
    public DateFormat getUpdatedAt();
    public Object getLocation();
    public Object getPreferences();
    public String getStatus();

    //Set Methods
    public void setObjectID(String objectID);
    public void setUserName(String userName);
    public void setPassword(String password);
    public void setAuthData(Object authData);
    public void setEmailVerified(Boolean isVerified);
    public void setEmail(String email);
    public void setCreatedAt(DateFormat createdDate);
    public void setUpdatedAt(DateFormat updatedDate);
    public void setLocation(Object location);
    public void setPreferences(Object preferences);
    public void setStatus(String status);
}
