package com.bowtaps.crowdcontrol;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * The official singleton object for the application.
 *
 * @author Daniel Andrus
 * @since 2015-11-27
 */
@ParseClassName("CrowdControlApplication")
public class CrowdControlApplication extends Application {

    // Global Parse Objects
    // cannot be declared as final because parse.initiallized has not been completed yet
    public static ParseUser aUser;
    public static ParseObject aGroup = new ParseObject("Group");
    public static ParseObject aProfile = new ParseObject("CCUser");


    /*
     *  Sets up the data store and gives initial values to the Application Variables
     *
     * @param aUser - Global instance of the Parse User, the current user of the app
     * @param aProfile - the public information for aUser
     * @param aGroup - the public instance of the group that the user is in
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize parse

        //ParseObject.registerSubclass(CrowdControlApplication.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "xJ5uDHyuSDxuMVBhNennSenRo9IRLnHx2g8bfPEv", "PuShwUtOWCdhCa9EmEDWjSuJ0AhFkMy9kJhELxHi");
        aUser = new ParseUser();
        aGroup = new ParseObject("Group");

    }
}