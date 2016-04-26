package com.bowtaps.crowdcontrol;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.bowtaps.crowdcontrol.model.BaseModel;
import com.bowtaps.crowdcontrol.model.GroupModel;
import com.bowtaps.crowdcontrol.model.LocationModel;
import com.bowtaps.crowdcontrol.model.SecureLocationManager;
import com.bowtaps.crowdcontrol.model.UserProfileModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Service for automatically fetching updates to groups and for notifying registered listeners about
 * any detected changes.
 *
 * @author Daniel Andrus <dan.andrus@bowtaps.com>
 */
public class GroupService extends Service {

    /**
     * Tag to use when logging.
     */
    private static final String TAG = "GroupService";

    /**
     * Flag indicating that this {@link GroupService} is running.
     */
    private static Boolean running = false;

    /**
     * Key to use when passing a {@link GroupModel} ID over an {@link Intent}.
     */
    public static final String INTENT_GROUP_ID_KEY = "group";

    /**
     * Key to use when passing a {@link UserProfileModel} ID over an {@link Intent}.
     */
    public static final String INTENT_USER_ID_KEY = "user";

    /**
     * The {@link Binder} designed to allow for communication between this {@link Service } and
     * other processes.
     */
    private final GroupServiceBinder binder;

    /**
     * List of registered {@link GroupUpdatesListener} objects.
     */
    private List<GroupUpdatesListener> groupUpdatesListeners;

    /**
     * List of registered {@link LocationUpdatesListener} objects.
     */
    private List<LocationUpdatesListener> locationUpdatesListeners;

    /**
     * Timer object used for periodically fetching updates from the server.
     */
    private Timer timer;

    /**
     * The task to execute on repeat.
     */
    private TimerTask timerTask;

    /**
     * The last time an object was updated.
     */
    private Date since;

    /**
     * The ID {@link String} of the group to fetch updates for.
     */
    private String groupId;

    /**
     * The ID {@link String} of the user to fetch updates for.
     */
    private String userPId;

    private LatLng currentLocation;

    private Integer locationCounter;


    /**
     * Default constructor for this object. Initializes properties.
     */
    public GroupService() {
        binder = new GroupServiceBinder();
        groupUpdatesListeners = new LinkedList<>();
        locationUpdatesListeners = new LinkedList<>();
        timer = null;
    }

    /**
     * @see Service#onStartCommand(Intent, int, int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Set the running flag
        running = true;

        if (intent == null) {
            stopSelf(startId);
            return START_STICKY;
        }

        // Extract intent arguments
        final String groupId = intent.getStringExtra(INTENT_GROUP_ID_KEY);
        final String userPId = intent.getStringExtra(INTENT_USER_ID_KEY);
        currentLocation = CrowdControlApplication.getInstance().getLocationManager().getCurrentLocation();
        locationCounter = 0;
        // Verify parameters
        if (groupId == null || userPId == null) {
            stopSelf(startId);
            return START_STICKY;
        }

        this.groupId = groupId;
        this.userPId = userPId;

        // Stop any currently running timers
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();

        // Define task to run
        final Handler handler = new Handler();
        this.since = new Date(0);
        timerTask = new TimerTask() {

            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        new FetchGroupUpdatesTask(groupId, userPId, since).execute();
                    }
                });
            }
        };

        // Begin repeating ask
        timer.schedule(timerTask, 5000);

        // Pass command on to superclass
        return START_STICKY;
    }

    /**
     * @see Service#onBind(Intent)
     *
     * @return Instance of {@link GroupService.GroupServiceBinder} that the bound entity can
     *         use to communicate with this service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * Called when this {@link Service} is destroyed. Attempts to stop and
     * cancel all timers and de-registers all registered listeners.
     *
     * @see Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        running = false;

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
        }
        groupUpdatesListeners.clear();

        super.onDestroy();
    }



    /**
     * Called when the {@link FetchGroupUpdatesTask} is completed.
     * @param results The results from the cloud code query.
     */
    public void onGroupUpdatesFetched(List<? extends BaseModel> results) {

        // Verify and repair parameters
        if (results == null)
        {
            results = new ArrayList<>();
        };

        GroupModel group = null;
        List<UserProfileModel> users = new LinkedList<>();
        List<LocationModel> locations = new LinkedList<>();
        SecureLocationManager locationManager = CrowdControlApplication.getInstance().getLocationManager();
        LatLng previousLocation = currentLocation;

        // Separate results into buckets based on type and keep track of most recent update time
        for (BaseModel model : results) {
            if (model instanceof GroupModel) {
                group = (GroupModel) model;
                Log.d("Service", "Added a new GroupModel");
            } else if (model instanceof UserProfileModel) {
                users.add((UserProfileModel) model);
                Log.d("Service", "Added a new UserProfileModel");
            } else if (model instanceof LocationModel) {
                locations.add((LocationModel) model);
            }

            if (model.getUpdated().after(since)) {
                since = model.getUpdated();
            }
        }
        currentLocation = locationManager.getCurrentLocation();
        if(!currentLocation.equals(previousLocation)){
            locationManager.broadcastLocation();
        }

        // Forward calls to listeners
        if (group != null) {
            for (GroupUpdatesListener ref : groupUpdatesListeners) {

                // Invoke callback method call
                try {
                    ref.onReceivedGroupUpdate(group);
                    locationManager.broadcastLocation();
                } catch (Exception ex) {
                    Log.d(TAG, "Exception thrown while handling onReceivedGroupUpdate event");
                }
            }
        }

        // Trigger events of location listeners
        if (!locations.isEmpty()) {
            // First, send updated locations to location manager
            CrowdControlApplication.getInstance().getLocationManager().updateLocations(locations);

            // Notify listeners of changes
            for (LocationUpdatesListener listener : locationUpdatesListeners) {
                // Invoke callback method call
                try {
                    listener.onReceivedLocationUpdate(new ArrayList<>(locations));
                } catch (Exception ex) {
                    Log.d(TAG, "Exception thrown while handling onReceivedLocationUpdate event");
                }
            }
        }

        // Restart the timer only after everything else is done
        timer = new Timer();
        final Handler handler = new Handler();
        timerTask = new TimerTask() {

            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        new FetchGroupUpdatesTask(groupId, userPId, since).execute();
                    }
                });
            }
        };
        timer.schedule(timerTask, 5000);
    }

    /**
     * Registers a new {@link GroupUpdatesListener} listener object that will receive callbacks
     * when this service detects that a group has been updated. This maintains a strong reference
     * to the listener object, which means it must be unregistered with a call to
     * {@link #removeGroupUpdatesListener(GroupUpdatesListener)} before it can be reclaimed by
     * garbage collection.
     *
     * @param listener The listener object to register.
     */
    public void addGroupUpdatesListener(GroupUpdatesListener listener) {

        // Ensure listener is not null
        if (listener == null) return;

        // Ensure listener is not already registered
        if (groupUpdatesListeners.contains(listener)) return;

        // Add listener to list
        groupUpdatesListeners.add(listener);
    }

    /**
     * Unregisters a {@link GroupUpdatesListener} that has been previously registered using
     * {@link #addGroupUpdatesListener(GroupUpdatesListener)}.
     *
     * @param listener The listener object to unregister.
     */
    public void removeGroupUpdatesListener(GroupUpdatesListener listener) {

        // Ensure listener is not null
        if (listener == null) return;

        // Remove reference from list
        groupUpdatesListeners.remove(listener);
    }

    /**
     * Registers a new {@link LocationUpdatesListener} listener object that will receive callbacks
     * when this service detects that a location has been updated. This maintains a strong reference
     * to the listener object, which means that it must be unregistered with a call to
     * {@link #removeLocationUpdatesListener(LocationUpdatesListener)} before it can be reclaimed by
     * garbage collection.
     *
     * @param listener The listener object to register.
     */
    public void addLocationUpdatesListener(LocationUpdatesListener listener) {

        // Ensure listener is not null
        if (listener == null) return;

        // Ensure listener is not already registered
        if (locationUpdatesListeners.contains(listener)) return;

        // Add listener to list
        locationUpdatesListeners.add(listener);
    }

    /**
     * Unregisters a {@link LocationUpdatesListener} that has been previously registered using
     * {@link #addLocationUpdatesListener(LocationUpdatesListener)}.
     *
     * @param listener The listener object to unregister.
     */
    public void removeLocationUpdatesListener(LocationUpdatesListener listener) {

        // Ensure listener is not null
        if (listener == null) return;

        // Remove reference from list
        locationUpdatesListeners.remove(listener);
    }


    /**
     * Internal class that extends {@link AsyncTask} for queries to the database.
     */
    private class FetchGroupUpdatesTask extends AsyncTask<Void, Void, List<? extends BaseModel>> {

        /**
         * The groupId to fetch updates for.
         */
        private final String groupId;

        /**
         * The userId to make the call on the behalf of.
         */
        private final String userId;

        /**
         * The time to fetch all updates more recent than.
         */
        private final Date since;

        /**
         * Class constructor. Initializes private members.
         *
         * @param groupId The groupId to fetch updates for.
         * @param userId The userId to make the call on the behalf of.
         * @param since The time to fetch all updates more recent than.
         */
        public FetchGroupUpdatesTask(String groupId, String userId, Date since) {
            this.groupId = groupId;
            this.userId = userId;
            this.since = since;
        }

        /**
         * Requests groupId updates from the application's model manager.
         *
         * @param params Unused
         * @return The results received from the application's model manager.
         */
        @Override
        protected List<? extends BaseModel> doInBackground(Void...params) {
            try {
                return CrowdControlApplication.getInstance().getModelManager().fetchGroupUpdates(groupId, userId, since);
            } catch (Exception ex) {
                return null;
            }
        }

        /**
         * Passes the results to the parent service. Calls
         * {@link GroupService#onGroupUpdatesFetched(List)}.
         *
         * @param results The results of the asynchronous query.
         */
        @Override
        protected void onPostExecute(List<? extends BaseModel> results) {
            GroupService.this.onGroupUpdatesFetched(results);
        }
    }

    /**
     * Binder for communicating with this service using method calls.
     */
    public class GroupServiceBinder extends Binder {

        /**
         * @see GroupService#addGroupUpdatesListener(GroupUpdatesListener)
         */
        public void addGroupUpdatesListener(GroupUpdatesListener listener) {
            GroupService.this.addGroupUpdatesListener(listener);
        }

        /**
         * @see GroupService#removeGroupUpdatesListener(GroupUpdatesListener)
         */
        public void removeGroupUpdatesListener(GroupUpdatesListener listener) {
            GroupService.this.removeGroupUpdatesListener(listener);
        }

        /**
         * @see GroupService#addLocationUpdatesListener(LocationUpdatesListener)
         */
        public void addLocationUpdatesListener(LocationUpdatesListener listener) {
            GroupService.this.addLocationUpdatesListener(listener);
        }

        /**
         * @see GroupService#removeLocationUpdatesListener(LocationUpdatesListener)
         */
        public void removeLocationUpdatesListener(LocationUpdatesListener listener) {
            GroupService.this.removeLocationUpdatesListener(listener);
        }
    }

    /**
     * Listener interface for receiving group updates.
     */
    public interface GroupUpdatesListener {
        void onReceivedGroupUpdate(GroupModel group);
    }

    /**
     * Listener interface for receiving location updates.
     */
    public interface LocationUpdatesListener {
        void onReceivedLocationUpdate(List<LocationModel> locations);
    }


    /**
     * Checks if the service is currently running.
     *
     * @return {@code true} if this service is running, {@code false} if not.
     */
    public static Boolean isRunning() {
        return running;
    }
}
