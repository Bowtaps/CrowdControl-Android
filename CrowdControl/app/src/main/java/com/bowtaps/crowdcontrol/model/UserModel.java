package com.bowtaps.crowdcontrol.model;

import java.text.DateFormat;

/**
 * Created by 1959760 on 10/27/2015.
 */
public interface UserModel {

    public String getObjectID();
    public void setObjectID();

    public String getUserName();
    public void setUserName();

    public String getPassword();
    public void setPassword(String password);
    public Object getAuthData();
    public void setAuthData(Object authData);
    public Boolean getEmailVerified();
    public void setEmailVerified(Boolean isVerified);
    public String getEmail();
    public void setEmail(String email);
    public DateFormat getCreatedAt();
    public void setCreatedAt(DateFormat createdDate);
    public DateFormat getUpdatedAt();
    public void setUpdatedAt(DateFormat updatedDate);

    //CC ParseUserModel Data
    public Object getLocation();
    public void setLocation(Object location);
    public Object getPreferences();
    public void setPreferences(Object preferences);
    public String getStatus();
    public void setStatus(String status);
}
