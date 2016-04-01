package com.bowtaps.crowdcontrol;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bowtaps.crowdcontrol.adapters.UserModelAdapter;
import com.bowtaps.crowdcontrol.model.GroupModel;
import com.bowtaps.crowdcontrol.model.UserProfileModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by 7143145 on 3/31/2016.
 */
public class InviteConfirmFragment extends Fragment
        implements GroupService.GroupUpdatesListener, ListView.OnItemClickListener, View.OnClickListener, Observer {
    private static final String ARG_PARAM1 = "param1";

    //LIST OF FOUND USERS FROM THE SEARCH FRAGMENT
    private List<UserProfileModel> mFoundUserList;

    //things needed to display list
    private UserModelAdapter mUserModelAdapter;
    private ListView mFoundUsersListView;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_invite_confirm, container, false);


        //Sets Found User list
        mFoundUserList = ((InviteNavigationActivity) getActivity()).getmFoundUserList();

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

    @Override
    public void onReceivedGroupUpdate(GroupModel group) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onClick(View v) {
        // Handles clicks on items in view
        switch (v.getId()) {
            case R.id.invite_confirmation_button:
                onInviteConfirmationButtonClicked((Button) v);
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
    }

    @Override
    public void update(Observable observable, Object data) {
        //Sets Found User list
        mFoundUserList = ((InviteNavigationActivity) getActivity()).getmFoundUserList();
        mUserModelAdapter.notifyDataSetChanged();
    }
}
