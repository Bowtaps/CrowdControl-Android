package com.bowtaps.crowdcontrol;

import android.app.Application;
import android.content.Intent;

import com.bowtaps.crowdcontrol.model.ModelManager;
import com.bowtaps.crowdcontrol.model.ParseModelManager;
import com.parse.ParseClassName;

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

    public boolean isMessageServiceStarted;

    private static final String SINCH_APP_KEY = "52ccd19d-4487-4645-8abf-b13edd57bffd";
    private static final String SINCH_APP_SECRET = "Ke+GKezurEqC46z33Da5Ig==";
    private static final String SINCH_ENVIRONMENT = "sandbox.sinch.com";



    /**
     * Performs application setup operations, including initializing class properties and opening
     * connections for storage.
     */
    @Override
    public void onCreate() {
        super.onCreate();

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
    }
}