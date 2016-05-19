package com.bowtaps.crowdcontrol;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.bowtaps.crowdcontrol.adapters.UserModelAdapter;
import com.bowtaps.crowdcontrol.model.BaseModel;
import com.bowtaps.crowdcontrol.model.GroupModel;
import com.bowtaps.crowdcontrol.model.UserProfileModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Pulls Users from the database and displays them so that they can be selected as potential invitees
 */
public class InviteSearchFragment extends Fragment implements GroupService.GroupUpdatesListener, ListView.OnItemClickListener, View.OnClickListener {

    private UserModelAdapter mUserModelAdapter;
    private ListView mSearchedUsersListView;
    private List<UserProfileModel> mFoundUserList;
    private List<UserProfileModel> mSearchedUserList;


    private static final String TAG = InviteSearchFragment.class.getSimpleName();

    private EditText mSearchEditText;

    //buttons
    private Button mCancelButton;

    /**
     * Searches for a user in the database, sets to be invited (@see InviteConfirmFragment)
     *
     * @return A new instance of fragment InviteSearchFragment.
     */
    public static InviteSearchFragment newInstance(String text) {
        InviteSearchFragment fragment = new InviteSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public InviteSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    /**
     * Initializes all the pieces the user can see so they can be modified by the fragment. sets
     * them all into the fragment view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_invite_search, container, false);

        // Initialize search text bar
        mSearchEditText = (EditText) v.findViewById(R.id.invite_search_edit_text);

        // Initialize list adapter
        mFoundUserList= new ArrayList<UserProfileModel>();
        mSearchedUserList = new ArrayList<UserProfileModel>();
        mUserModelAdapter = new UserModelAdapter(getActivity(), mSearchedUserList);

        //set handler for button and give to onclick
        mCancelButton = (Button) v.findViewById(R.id.invite_cancel_button);
        mCancelButton.setOnClickListener(this);

        if(CrowdControlApplication.getInstance().getModelManager().getCurrentGroup().getGroupMembers() != null &&
                !CrowdControlApplication.getInstance().getModelManager().getCurrentGroup().getGroupMembers().isEmpty())
            CrowdControlApplication.getInstance().getModelManager().fetchSearchedUsersInBackground(new BaseModel.FetchCallback() {
                @Override
                public void doneFetchingModels(List<? extends BaseModel> results, Exception ex) {

                    // Verify operation was successful
                    if (results == null || ex != null) {
                        Log.d(TAG, "Failed to load Searched list");
                        return;
                    }

                    // Replace existing list with new results
                    mSearchedUserList.clear();
                    mSearchedUserList.addAll((List<UserProfileModel>) results);

                    // Force update on the list adapter
                    mUserModelAdapter.notifyDataSetChanged();
                }
            }, mSearchEditText.getText().toString());

        //Initialize ListView
        mSearchedUsersListView = (ListView) v.findViewById(R.id.user_search_list_view);
        mSearchedUsersListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE); //suppose to allow items to be selectable
        mSearchedUsersListView.setAdapter(mUserModelAdapter);
        mSearchedUsersListView.setOnItemClickListener(this);


        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Handles clicks onto the users in the view - currently keeps track of what users have been
     * 'selected and hands them to the InviteConfirmFragment
     * @see InviteConfirmFragment
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //make sure highlighting happens - not working???
        //mUserModelAdapter.notifyDataSetChanged();

        //if on current set - remove
        if( mFoundUserList.contains(mUserModelAdapter.getItem(position))){
            mFoundUserList.remove(mUserModelAdapter.getItem(position));
            ((InviteNavigationActivity) getActivity()).setmFoundUserList(mFoundUserList);
        }
        else { // add to current list
            mFoundUserList.add(mUserModelAdapter.getItem(position));
            ((InviteNavigationActivity) getActivity()).setmFoundUserList(mFoundUserList);
        }
    }

    // Likely not needed
    @Override
    public void onReceivedGroupUpdate(GroupModel group) {

    }

    /**
     * Handles clicks onto any buttons found in this fragment
     * @param v
     */
    @Override
    public void onClick(View v) {
        // Handles clicks on items in view
        switch (v.getId()) {
            case R.id.invite_cancel_button:
                getActivity().finish();

            default:
                // Sorry, you're outta luck
                break;
        }
    }
}
