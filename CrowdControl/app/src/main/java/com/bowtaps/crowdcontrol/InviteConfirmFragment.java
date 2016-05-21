package com.bowtaps.crowdcontrol;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bowtaps.crowdcontrol.adapters.UserModelAdapter;
import com.bowtaps.crowdcontrol.model.BaseModel;
import com.bowtaps.crowdcontrol.model.GroupModel;
import com.bowtaps.crowdcontrol.model.InvitationModel;
import com.bowtaps.crowdcontrol.model.UserProfileModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Johnny on 3/31/2016.
 * Fragment ment to confirm and send notifications
 */
public class InviteConfirmFragment extends Fragment
        implements GroupService.GroupUpdatesListener, ListView.OnItemClickListener, View.OnClickListener, Observer {
    private static final String ARG_PARAM1 = "param1";

    //LIST OF FOUND USERS FROM THE SEARCH FRAGMENT
    private List<UserProfileModel> mFoundUserList;

    //things needed to display list
    private UserModelAdapter mUserModelAdapter;
    private ListView mFoundUsersListView;

    private Button mCancelButton;
    private Button mConfirmButton;


    //TODO THIS BUTTON NEEDS TO BECOME AN EVENT LISTENER!!!
    private Button mRefreshButton;

    private static final String TAG = InviteConfirmFragment.class.getSimpleName();



    /// I need a listener for a single object in the other fragment. gosh darn it.
    public void observe(Observable foundList){
        foundList.addObserver(this);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param text Test text 1
     * @return A new instance of fragment EventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InviteConfirmFragment newInstance(String text) {
        InviteConfirmFragment fragment = new InviteConfirmFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public InviteConfirmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * initializes the view elements of the fragment so they can be modified by other functions
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view - displays the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_invite_confirm, container, false);


        //Sets Found User list
        mFoundUserList = ((InviteNavigationActivity) getActivity()).getmFoundUserList();

        //set handler for button and give to onclick
        mCancelButton = (Button) v.findViewById(R.id.invite_cancel_button);
        mConfirmButton = (Button) v.findViewById(R.id.invite_confirmation_button);
        mCancelButton.setOnClickListener(this);
        mConfirmButton.setOnClickListener(this);

        //TODO THIS BUTTON NEEDS TO BECOME AN EVENT LISTENER
        mRefreshButton = (Button) v.findViewById(R.id.invite_refresh_button);
        mRefreshButton.setOnClickListener(this);

        //attempt to fill list
        if(mFoundUserList == null){
            mFoundUserList = new ArrayList<UserProfileModel>();
        }
        mUserModelAdapter = new UserModelAdapter(getActivity(), mFoundUserList);

        //Initialize ListView
        mFoundUsersListView = (ListView) v.findViewById(R.id.user_confirmation_list_view);
        mFoundUsersListView.setAdapter(mUserModelAdapter);
        mFoundUsersListView.setOnItemClickListener(this);

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //Likely not needed
    @Override
    public void onReceivedGroupUpdate(GroupModel group) {

    }

    /**
     * handles clicks on items in the Found User List
     * @param parent - holder of list layout
     * @param view - display of pressed item
     * @param position - position in list adapter
     * @param id - honestly, no idea
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (mUserModelAdapter.getItem(position) != null){ //hopefully prevents race conditions
            //remove from list
            mFoundUserList.remove(mUserModelAdapter.getItem(position));
            //set activity list
            ((InviteNavigationActivity) getActivity()).setmFoundUserList(mFoundUserList);
            //make sure highlighting happens
            mUserModelAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Handles all clicks on the buttons found in this fragment
     * @param v - view of button clicked
     */
    @Override
    public void onClick(View v) {
        // Handles clicks on items in view
        switch (v.getId()) {
            case R.id.invite_confirmation_button:
                onInviteConfirmationButtonClicked((Button) v);
                break;

            //TODO THIS NEEDS TO BE AN EVENT LISTENER
            case R.id.invite_refresh_button:
                //Sets Found User list
                mFoundUserList.clear();
                //prevents null if refresh if first thing clicked
                if( ((InviteNavigationActivity) getActivity()).getmFoundUserList() != null ) {
                    mFoundUserList.addAll(((InviteNavigationActivity) getActivity()).getmFoundUserList());
                }
                mUserModelAdapter.notifyDataSetChanged();
                break;

            case R.id.invite_cancel_button:
                getActivity().finish();

            default:
                // Sorry, you're outta luck
                break;
        }
    }

    /**
     * Confirmation has been confirmed on which users to invite.
     * send invites now
     *
     * @param v
     */
    private void onInviteConfirmationButtonClicked(Button v) {
        //TODO actually invite users
        for( UserProfileModel user : mFoundUserList ) {
            CrowdControlApplication.getInstance().getModelManager().createNewInvitation(user).saveInBackground(new BaseModel.SaveCallback() {
                @Override
                public void doneSavingModel(BaseModel object, Exception ex) {
                    if(ex != null) {

                    }
                    else{
                        Log.d(TAG, "Failed to load Searched list");
                    }

                }
            });
        }
        mFoundUserList.clear();
        mUserModelAdapter.notifyDataSetChanged();
    }

    /**
     * Code to be called when the given observable is changed (couldn't get to work)
     * @param observable
     * @param data
     */
    @Override
    public void update(Observable observable, Object data) {
        //Sets Found User list
        mFoundUserList.clear();
        mFoundUserList.addAll(((InviteNavigationActivity) getActivity()).getmFoundUserList());
        mUserModelAdapter.notifyDataSetChanged();
    }

}
