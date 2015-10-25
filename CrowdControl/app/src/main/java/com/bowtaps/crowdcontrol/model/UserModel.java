package com.bowtaps.crowdcontrol.model;

import java.text.DateFormat;
/**
 * Created by 1959760 on 10/24/2015.
 */
public class UserModel {

    public UserModel(){
        // (Parse) UserModel Data
        String ObjectID;
        String UserName;
        String Password;
        Object AuthData;
        Boolean EmailVerified;
        String Email;
        DateFormat CreatedAt;
        DateFormat UpdatedAt;

        //CC UserModel Data
        Object Location;
        Object Preferences;
        String Status;
    }

    // Fields
    public String ObjectID;
    public String UserName;
    public String Password;
    public Object AuthData;
    public Boolean EmailVerified;
    public String Email;
    public DateFormat CreatedAt;
    public DateFormat UpdatedAt;

    //CC UserModel Data
    public Object Location;
    public Object Preferences;
    public String Status;

    // Methods
//        GetGroupModel();
//        GetGroupName();
//        GetItinerary();
//        GetStatus();
//        GetWaypoints();

    // How do we implement this in Java/Android? DAN HELP
//    public Object getLocation(){
//        return this.Location;
//    }
//
//    public Object setLocation(){
//        this.Location = Location;
//    }

}
