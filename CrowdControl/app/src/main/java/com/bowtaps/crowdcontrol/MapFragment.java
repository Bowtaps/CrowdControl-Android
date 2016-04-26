package com.bowtaps.crowdcontrol;


import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bowtaps.crowdcontrol.model.GroupModel;
import com.bowtaps.crowdcontrol.model.LocationModel;
import com.bowtaps.crowdcontrol.model.ModelManager;
import com.bowtaps.crowdcontrol.model.ParseLocationManager;
import com.bowtaps.crowdcontrol.model.ParseLocationModel;
import com.bowtaps.crowdcontrol.model.SecureLocationManager;
import com.bowtaps.crowdcontrol.model.UserModel;
import com.bowtaps.crowdcontrol.model.UserProfileModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Will Display a Google Map and place group members on it
 */
public class MapFragment extends Fragment implements View.OnClickListener, GroupService.LocationUpdatesListener, OnMapReadyCallback {
    private static final String ARG_PARAM1 = "param1";
    private static ArrayList<Float> mMarkerColors;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private String mText;
    FloatingActionButton mLocationButton;
    FloatingActionButton mSyncButton;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param text Test text 1
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String text) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, text);
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * Requires empty public constructor
     */
    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * resumes any saved instant
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * starts the View
     *
     * @param inflater  Grabs the Layout
     * @param container Container for the view
     * @param savedInstanceState    resumed instant state
     * @return  returns a view to the parent Activity
     *
     * @see GroupNavigationActivity
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment f = SupportMapFragment.newInstance();
        FragmentTransaction t = getChildFragmentManager().beginTransaction();
        t.add(R.id.map_frame, f);
        t.commit();
        MapsInitializer.initialize(CrowdControlApplication.getInstance());

        mMarkerColors = new ArrayList<Float>();
        mMarkerColors.add(BitmapDescriptorFactory.HUE_AZURE);
        mMarkerColors.add(BitmapDescriptorFactory.HUE_BLUE);
        mMarkerColors.add(BitmapDescriptorFactory.HUE_CYAN);
        mMarkerColors.add(BitmapDescriptorFactory.HUE_GREEN);
        mMarkerColors.add(BitmapDescriptorFactory.HUE_MAGENTA);
        mMarkerColors.add(BitmapDescriptorFactory.HUE_ORANGE);
        mMarkerColors.add(BitmapDescriptorFactory.HUE_VIOLET);
        mMarkerColors.add(BitmapDescriptorFactory.HUE_YELLOW);

        // Get handle to button
        mLocationButton = (FloatingActionButton)v.findViewById(R.id.locationButton);
        mSyncButton = (FloatingActionButton)v.findViewById(R.id.syncButton);

        // Declare button clicks
        mLocationButton.setOnClickListener(this);
        mSyncButton.setOnClickListener(this);
        f.getMapAsync(this);
        return v;
    }

    @Override
    public void onMapReady(GoogleMap map){
        Log.d("Map", "on Map ready function");
        centerOnUser();
    }

    @Override
    public void onClick(View view) {
        // Handles clicks on items in view
        // in this case, either the facebook button or the create account button

        switch (view.getId()) {
            case R.id.locationButton:
                myMethodCall1((FloatingActionButton) view);
                break;

            case R.id.syncButton:
                myMethodCall2((FloatingActionButton) view);
                break;

            default:
                // Sorry, you're outta luck
                break;
        }
    }

    private void myMethodCall1(FloatingActionButton view) {
        centerOnUser();
    }

    private void myMethodCall2(FloatingActionButton view) {
        if(mMap == null){
            setUpMapIfNeeded();
        }
        refreshMarkers();

    }

    /**
     * Centers the map on the user's current location recieved from the location manager.
     */
    private void centerOnUser(){
        SecureLocationManager secureLocationManager = CrowdControlApplication.getInstance().getLocationManager();
        LatLng myLoc = secureLocationManager.getCurrentLocation();
        if(mMap == null){
            setUpMapIfNeeded();
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 15));
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_frame))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This funciton adds a marker to the current user's location
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        LatLng myLoc = CrowdControlApplication.getInstance().getLocationManager().getCurrentLocation();
        UserModel me = CrowdControlApplication.getInstance().getModelManager().getCurrentUser();
        mMap.addMarker(new MarkerOptions().position(myLoc).title(me.getProfile().getDisplayName()));
    }

    /**
     * Clears and re-adds the markers for the users when their locations have been updated
     */
    private void refreshMarkers(){
        String memberDisplayName;
        // Get locations for current group members
        ModelManager modelManager = CrowdControlApplication.getInstance().getModelManager();
        GroupModel group = modelManager.getCurrentGroup();
        UserProfileModel me = modelManager.getCurrentUser().getProfile();
        LatLng myLoc = CrowdControlApplication.getInstance().getLocationManager().getCurrentLocation();
        try {
            List<? extends LocationModel> locations = CrowdControlApplication.getInstance().getLocationManager().getLocations();
            Integer i = 0;
            // Remove pins from map
            if(mMap != null){
                mMap.clear();
            }
            else{
                mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_frame)).getMap();
                mMap.clear();
            }
            mMap.addMarker(new MarkerOptions().position(myLoc).title(me.getDisplayName()));
            // Put markers on map
            for (LocationModel location : locations){

                // Build location marker
                Double longitude = location.getLongitude();
                Double latitude = location.getLatitude();

                // Add marker to map
//                if(location.getFrom().getDisplayName() == null){
//                    memberDisplayName = "member".concat(i.toString());
//                }else{
//                    memberDisplayName = location.getFrom().getDisplayName();
//                }
                if(location.getFrom().getDisplayName() != null){
                    memberDisplayName = location.getFrom().getDisplayName();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(memberDisplayName).icon(BitmapDescriptorFactory.defaultMarker(mMarkerColors.get(i))));
                    i = (i+1)% 8;
                }else{
                    CrowdControlApplication.getInstance().getLocationManager().removeLocation(location.getFrom());
                }

            }
        }catch (Exception e) {
            Log.d("Exception testing", e.toString());
        }
    }

    /**
     * When new locations are recived from the service update the cache and update the map.
     * @param locations {@link List} of {@link LocationModel}
     */
    @Override
    public void onReceivedLocationUpdate(List<LocationModel> locations) {
        CrowdControlApplication.getInstance().getLocationManager().updateLocations(locations);
        refreshMarkers();
    }
}
