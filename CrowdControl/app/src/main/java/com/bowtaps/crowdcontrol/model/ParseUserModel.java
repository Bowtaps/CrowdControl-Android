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

    // Methods
    public String getObjectID(){
        return this.objectID;
    }
    public void setObjectID();

    public String getUserName(){
        return this.userName;
    }
    public void setUserName();

    public String getPassword(){
        return this.password;
    }
    public void setPassword();

    public Object getAuthData(){
        return this.authData;
    }
    public void setAuthData();
    public Boolean getEmailVerified(){
        return  this.emailVerified;
    }
    public void setEmailVerified();
    public String getEmail(){
        return this.email;
    }
    public void setEmail();
    public DateFormat getCreatedAt(){
        return this.createdAt;
    }
    public void setCreatedAt(){
        
    }
    public DateFormat getUpdatedAt(){
        return this.updatedAt;
    }
    public void setUpdatedAt(DateFormat updatedDate){
        this.updatedAt = updatedDate;
    }

    //CC ParseUserModel Data
    public Object getLocation(){
        return this.location;
    }
    public void setLocation(Object location){
        this.location = location;
    }
    public Object getPreferences(){
        return this.preferences;
    }
    public void setPreferences(Object preferences){
        this.preferences = preferences;
    }
    public String getStatus(){
        return this.status;
    }
    public void setStatus(String status){
        this.status = status;
    }

    // How do we implement this in Java/Android? DAN HELP
//    public Object getlocation(){
//        return this.location;
//    }

//    public Object setlocation(){
//        this.location = location;
//    }

}
