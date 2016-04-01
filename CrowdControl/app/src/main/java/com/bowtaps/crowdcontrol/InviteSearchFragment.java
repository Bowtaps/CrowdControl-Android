package com.bowtaps.crowdcontrol;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bowtaps.crowdcontrol.adapters.UserModelAdapter;
import com.bowtaps.crowdcontrol.model.UserProfileModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 7143145 on 3/31/2016.
 */
public class InviteSearchFragment extends Fragment implements ListView.OnItemClickListener {

    private String mText;

    private UserModelAdapter mUserModelAdapter;
    private ListView mFoundUsersListView;
    private List<UserProfileModel> mFoundUserList;
    private List<UserProfileModel> mSearchedUserList;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_invite_search, container, false);

        // Initialize list adapter
        mFoundUserList= new ArrayList<UserProfileModel>();
        mSearchedUserList = new ArrayList<UserProfileModel>();
        mUserModelAdapter = new UserModelAdapter(getActivity(), mSearchedUserList);

//        if(CrowdControlApplication.getInstance().getModelManager().getCurrentGroup().getGroupMembers() != null &&
//                !CrowdControlApplication.getInstance().getModelManager().getCurrentGroup().getGroupMembers().isEmpty())
//            mSearchedUserList.addAll(CrowdControlApplication.getInstance().getModelManager().getCurrentGroup().getGroupMembers());

        //Initialize ListView
        mFoundUsersListView = (ListView) v.findViewById(R.id.user_search_list_view);
        mFoundUsersListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        mFoundUsersListView.setAdapter(mUserModelAdapter);
        mFoundUsersListView.setOnItemClickListener(this);
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //make sure highlighting happens
        mUserModelAdapter.notifyDataSetChanged();

        if( mFoundUserList.contains(mUserModelAdapter.getItem(position))){
            mFoundUserList.remove(mUserModelAdapter.getItem(position));
            ((InviteNavigationActivity) getActivity()).setmFoundUserList(mFoundUserList);
        }
        else {
            mFoundUserList.add(mUserModelAdapter.getItem(position));
            ((InviteNavigationActivity) getActivity()).setmFoundUserList(mFoundUserList);
        }
    }
}
