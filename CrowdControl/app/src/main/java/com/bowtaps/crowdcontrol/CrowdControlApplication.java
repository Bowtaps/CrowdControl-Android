package com.bowtaps.crowdcontrol;

import android.app.Application;
import com.parse.Parse;

/**
 * The official singleton object for the application.
 *
 * @author Daniel Andrus
 * @since 2015-11-27
 */
public class CrowdControlApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize parse
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "xJ5uDHyuSDxuMVBhNennSenRo9IRLnHx2g8bfPEv", "PuShwUtOWCdhCa9EmEDWjSuJ0AhFkMy9kJhELxHi");
    }

}
