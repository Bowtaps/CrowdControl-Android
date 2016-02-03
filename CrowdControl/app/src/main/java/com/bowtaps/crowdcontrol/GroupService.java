package com.bowtaps.crowdcontrol;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

import com.bowtaps.crowdcontrol.model.BaseModel;
import com.bowtaps.crowdcontrol.model.GroupModel;
import com.bowtaps.crowdcontrol.model.UserProfileModel;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

/**
 * Service for automatically fetching updates to groups and for notifying registered listeners about
 * any detected changes.
 *
 * @author Daniel Andrus <dan.andrus@bowtaps.com>
 */
public class GroupService extends Service {

    /**
     * The {@link Binder} designed to allow for communication between this {@link Service } and
     * other processes.
     */
    private final GroupServiceBinder binder;

    /**
     * List of registered {@link GroupUpdatesListener} objects, implemented using weak references
     * to avoid this service keeping hold of the registered listeners.
     */
    private List<WeakReference<GroupUpdatesListener>> groupUpdatesListeners;

    private final Timer timer;


    /**
     * Default constructor for this object. Initializes properties.
     */
    public GroupService() {
        binder = new GroupServiceBinder();
        groupUpdatesListeners = new LinkedList<WeakReference<GroupUpdatesListener>>();
        timer = new Timer();
    }

    /**
     * @see Service#onStartCommand(Intent, int, int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // TODO spin up repeating task that watches for group updates

        // Pass command on to superclass
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Called when the {@link FetchGroupUpdatesTask} is completed.
     * @param results The results from the cloud code query.
     */
    public void onGroupUpdatesFetched(List<? extends BaseModel> results) {

        // TODO forward calls to listeners

        // TODO restarts scheduler
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
     * Registers a new {@link GroupUpdatesListener} listener object that will receive callbacks
     * when this service detects that a group has been updated. Registering a listener will not
     * not create a strong reference, meaning that the object can be cleaned up by garbage,
     * collection, even if it is still registered with this service.
     *
     * @param listener The listener object to register.
     */
    public void addGroupUpdatesListener(GroupUpdatesListener listener) {

        // Ensure listener is not null
        if (listener == null) return;

        // Ensure listener is not already registered
        for (WeakReference<GroupUpdatesListener> ref : groupUpdatesListeners) {
            if (ref.get() == listener) return;
        }

        // Add listener to list
        groupUpdatesListeners.add(new WeakReference<GroupUpdatesListener>(listener));
    }

    /**
     * Unregisters a {@link GroupUpdatesListener} that has been previously registered using
     * {@link #addGroupUpdatesListener(GroupUpdatesListener)}.
     *
     * @param listener The listener object to register.
     */
    public void removeGroupUpdatesListener(GroupUpdatesListener listener) {

        // Ensure listener is not null
        if (listener == null) return;

        // Search list for matching listener
        WeakReference<GroupUpdatesListener> reference = null;
        for (WeakReference<GroupUpdatesListener> ref : groupUpdatesListeners) {
            if (ref.get() == listener) {
                reference = ref;
                break;
            }
        }

        // Remove reference from list
        if (reference != null) {
            groupUpdatesListeners.remove(reference);
        }
    }


    /**
     * Internal class that extends {@link AsyncTask} for queries to the database.
     */
    private class FetchGroupUpdatesTask extends AsyncTask<Void, Void, List<? extends BaseModel>> {

        /**
         * The group to fetch updates for.
         */
        private final GroupModel group;

        /**
         * The user to make the call on the behalf of.
         */
        private final UserProfileModel user;

        /**
         * The time to fetch all updates more recent than.
         */
        private final Date since;

        /**
         * Class constructor. Initializes private members.
         *
         * @param group The group to fetch updates for.
         * @param user The user to make the call on the behalf of.
         * @param since The time to fetch all updates more recent than.
         */
        public FetchGroupUpdatesTask(GroupModel group, UserProfileModel user, Date since) {
            this.group = group;
            this.user = user;
            this.since = since;
        }

        /**
         * Requests group updates from the application's model manager.
         *
         * @param params Unused
         * @return The results received from the application's model manager.
         */
        @Override
        protected List<? extends BaseModel> doInBackground(Void...params) {
            try {
                return CrowdControlApplication.getInstance().getModelManager().fetchGroupUpdates(group, user, since);
            } catch (Exception ex) {
                return null;
            }
        }

        /**
         * Passes the results to the parent service. Calls
         * {@link GroupService#onGroupUpdatesFetched(List<? extends BaseModel>)}.
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
    }

    /**
     * Listener interface for receiving group updates.
     */
    public interface GroupUpdatesListener {
        public void onReceivedGroupUpdate(GroupModel group);
    }
}
