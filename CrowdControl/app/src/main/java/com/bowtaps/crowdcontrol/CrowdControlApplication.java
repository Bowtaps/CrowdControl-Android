package com.bowtaps.crowdcontrol;

import android.Manifest;
import android.app.Application;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.bowtaps.crowdcontrol.model.ModelManager;
import com.bowtaps.crowdcontrol.model.ParseLocationManager;
import com.bowtaps.crowdcontrol.model.ParseModelManager;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import com.bowtaps.crowdcontrol.model.SecureLocationManager;
import com.parse.Parse;
import com.parse.ParseClassName;

import com.facebook.FacebookSdk;

import com.parse.ParseFacebookUtils;

/**
 * The official singleton object for the application.
 *
 * @author Daniel Andrus
 * @since 2015-11-27
 */
@ParseClassName("CrowdControlApplication")
public class CrowdControlApplication extends Application {

    /**
     * Static reference to the singleton instance of this object.
     */
    private static CrowdControlApplication instance = null;



    /**
     * The {@link ModelManager} responsible for data storage and retrieval.
     */
    private ModelManager modelManager = null;


    private static final String SINCH_APP_KEY = "52ccd19d-4487-4645-8abf-b13edd57bffd";
    private static final String SINCH_APP_SECRET = "Ke+GKezurEqC46z33Da5Ig==";
    private static final String SINCH_ENVIRONMENT = "sandbox.sinch.com";
    private SecureLocationManager locationManager = null;




    /**
     * Performs application setup operations, including initializing class properties and opening
     * connections for storage.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        // Initialize singleton reference property
        if (instance == null) {
            instance = this;
        }

        // Initialize internal properties
        // Initialize parse connection
        modelManager = new ParseModelManager(this, "xJ5uDHyuSDxuMVBhNennSenRo9IRLnHx2g8bfPEv", "PuShwUtOWCdhCa9EmEDWjSuJ0AhFkMy9kJhELxHi");
    }

    /**
     * Gets the {@link ModelManager} responsible for data storage and retrieval for the application.
     *
     * @return The {@link ModelManager} to be used for all data operations.
     */
    public ModelManager getModelManager() {
        return modelManager;
    }

    public SecureLocationManager getLocationManager(){
        if(locationManager == null)
        {
            locationManager = new ParseLocationManager();
            locationManager.initializeLocationRequest();
            Log.d("Crowd Control App", "LocationManager was null. Should be set");
        }

        return locationManager;
    }

    /**
     * Gets the singleton instance of this class. Acts as a convenience function.
     *
     * @return A reference to the singleton instance of this class or {@code null} in the rarest of
     *         conditions if something expected occurs.
     */
    public static CrowdControlApplication getInstance() {
        return instance;
    }

    public void onDestroy() {
        stopService(new Intent(this,MessageService.class));
        //super.onDestroy();

        Parse.enableLocalDatastore(this);

        Parse.initialize(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(this);
    }
}