package com.bowtaps.crowdcontrol.model;

import java.text.DateFormat;
/**
 * Created by 1959760 on 10/24/2015.
 */
public class ParseUserModel implements UserModel {

    public ParseUserModel(){
        //User Data
        String objectID;
        String userName;
        String password;
        Object authData;
        Boolean emailVerified;
        String email;
        DateFormat createdAt;
        DateFormat updatedAt;

        //CCUser Data
        Object location;
        Object preferences;
        String status;
    }

    // Properties
    private String objectID;
    private String userName;
    private String password;
    private Object authData;
    private Boolean emailVerified;
    private String email;
    private DateFormat createdAt;
    private DateFormat updatedAt;

    //CC ParseUserModel Data
    private Object location;
    private Object preferences;
    private String status;

    // Get Methods
    @Override
    public String getObjectID(){
        return this.objectID;
    }
    @Override
    public String getUserName(){
        return this.userName;
    }
    @Override
    public String getPassword(){
        return this.password;
    }
    @Override
    public Object getAuthData(){
        return this.authData;
    }
    @Override
    public Boolean getEmailVerified(){
        return  this.emailVerified;
    }
    @Override
    public String getEmail(){
        return this.email;
    }
    @Override
    public DateFormat getCreatedAt(){
        return this.createdAt;
    }
    @Override
    public DateFormat getUpdatedAt(){
        return this.updatedAt;
    }
    @Override
    public Object getLocation(){
        return this.location;
    }
    @Override
    public Object getPreferences(){
        return this.preferences;
    }
    @Override
    public String getStatus(){
        return this.status;
    }

    // Set Methods
    @Override
    public void setObjectID(String objectID){
        this.objectID = objectID;
    }
    @Override
    public void setUserName(String userName){
        this.userName= userName;
    }
    @Override
    public void setPassword(String password){
        this.password = password;
    }
    @Override
    public void setAuthData(Object authData){
        this.authData = authData;
    }
    @Override
    public void setEmailVerified(Boolean emailVerified){
        this.emailVerified = emailVerified;
    }
    @Override
    public void setEmail(String email){
        this.email = email;
    }
    @Override
    public void setCreatedAt(DateFormat createdDate){
        this.createdAt = createdDate;
    }
    @Override
    public void setUpdatedAt(DateFormat updatedDate){
        this.updatedAt = updatedDate;
    }
    @Override
    public void setLocation(Object location){
        this.location = location;
    }
    @Override
    public void setPreferences(Object preferences){
        this.preferences = preferences;
    }
    @Override
    public void setStatus(String status){
        this.status = status;
    }
}
