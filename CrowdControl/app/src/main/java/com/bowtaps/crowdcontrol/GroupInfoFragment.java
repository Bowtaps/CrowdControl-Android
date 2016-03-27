package com.bowtaps.crowdcontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bowtaps.crowdcontrol.adapters.UserModelAdapter;
import com.bowtaps.crowdcontrol.model.BaseModel;
import com.bowtaps.crowdcontrol.model.GroupModel;
import com.bowtaps.crowdcontrol.model.UserProfileModel;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link GroupInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 * Will display information of the current group and allow the
 * user to leave the group
 */
public class GroupInfoFragment extends Fragment implements View.OnClickListener, GroupService.GroupUpdatesListener {

    GroupModel mGroup;
    Button mLeaveGroupButton;
    TextView mGroupLeaderTextView;
    TextView mGroupDescriptionTextView;

    private UserModelAdapter mUserModelAdapter;
    private ListView mMemberListView;
    private List<UserProfileModel> mMemberList;

    private static final String TAG = GroupInfoFragment.class.getSimpleName();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param text Test text 1
     * @return A new instance of fragment GroupInfoFragment.
     */
    public static GroupInfoFragment newInstance(String text) {
        GroupInfoFragment fragment = new GroupInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    public GroupInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Retrieves the arguments for the saved instance.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Instantiates the group objects and sets other layout objects. The layout elements are then
     * set and the function finishes.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mGroup = CrowdControlApplication.getInstance().getModelManager().getCurrentGroup();

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_group_info, container, false);

        // Get handles to view elements
        mLeaveGroupButton = (Button) v.findViewById(R.id.leave_group_button);
        mGroupLeaderTextView = (TextView) v.findViewById(R.id.group_leader_name);
        mGroupDescriptionTextView = (TextView) v.findViewById(R.id.text_group_description);

        // Declare event handlers
        mLeaveGroupButton.setOnClickListener(this);


        // Initialize list adapter
        mMemberList = new ArrayList<UserProfileModel>();
        mUserModelAdapter = new UserModelAdapter(getActivity(), mMemberList);

        //Initialize ListView
        mMemberListView = (ListView) v.findViewById(R.id.user_profile_list_view);
        mMemberListView.setAdapter(mUserModelAdapter);
        //mMemberListView.setOnItemClickListener(this);

        // Put group info on screen
        updateViews();

        return v;
    }

    /**
     * Handles buttons in the UI being clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {

        // Handles clicks on items in view
        switch (view.getId()) {
            case R.id.leave_group_button:
                onLeaveGroupButtonClick((Button) view);
                break;

            default:
                // Sorry, you're outta luck
                break;
        }
    }

    /**
     * Handles group update events, which trigger when the app receives new information regarding
     * the group and the members thereof.
     *
     * @param group The group that has been updated.
     */
    @Override
    public void onReceivedGroupUpdate(GroupModel group) {

        // Update internally cached group
        mGroup = group;

        updateViews();
    }


    /**
     * Causes the currently logged in user to leave the current group.
     */
    private void onLeaveGroupButtonClick(Button view) {

        if( mGroup.getGroupLeader() == CrowdControlApplication.getInstance().getModelManager().getCurrentUser().getProfile()){
            leaveLeaderDialog(getActivity(), "Destroy Group?", "Are you sure you want to delete this group?", "Yes, Remove Group");
        }
        else {
            //mGroup.removeGroupMember(CrowdControlApplication.getInstance().getModelManager().getCurrentUser().getProfile());
            leaveDialog(getActivity(), "Leave Group?", "Are you sure you want to leave this group?", "Yes, Leave Group");
        }
    }

    /**
     * Updates views in fragment to reflect the current state of {@link #mGroup}.
     */
    private void updateViews() {
        if( CrowdControlApplication.getInstance().getModelManager().getCurrentGroup() != null) {
            // Use data from current group to fill UI elements with data
            if (mGroup.getGroupLeader() != null) {
                mGroupLeaderTextView.setText(mGroup.getGroupLeader().getDisplayName());
            }
            mGroupDescriptionTextView.setText(mGroup.getGroupDescription());

            // Replace existing list with new results
            mMemberList.clear();
            if(CrowdControlApplication.getInstance().getModelManager().getCurrentGroup().getGroupMembers() != null)
                mMemberList.addAll(CrowdControlApplication.getInstance().getModelManager().getCurrentGroup().getGroupMembers());
            // Force update on the list adapter
            mUserModelAdapter.notifyDataSetChanged();
        }
        else {
            CrowdControlApplication.getInstance().getModelManager().setCurrentGroup(null);
            launchGroupJoinActivity();
        }
    }

    /**
     * Launches the {@link GroupJoinActivity}.
     *
     * @see GroupJoinActivity
     */
    private void launchGroupJoinActivity() {
        Intent myIntent = new Intent(getActivity(), GroupJoinActivity.class);
        this.startActivity(myIntent);
        getActivity().finish();
    }

    /**
     * Builds a Dialog box to leave a group
     *
     * @param activity
     * @param title
     * @param message
     * @param leaveMessage
     */
    public void leaveDialog(Activity activity, String title, CharSequence message, String leaveMessage){
        AlertDialog.Builder buildDialog = new AlertDialog.Builder(activity);

        buildDialog.setTitle(title);
        buildDialog.setMessage(message);
        buildDialog.setPositiveButton(leaveMessage, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mGroup.removeGroupMember(CrowdControlApplication.getInstance().getModelManager().getCurrentUser().getProfile());

                // Attempt to save change to group in background
                mGroup.saveInBackground(new BaseModel.SaveCallback() {
                    @Override
                    public void doneSavingModel(BaseModel object, Exception ex) {

                        // Verify operation was a success
                        if (ex != null) {
                            Log.d(TAG, "Unable to save group");
                            return;
                        }

                        CrowdControlApplication.getInstance().getModelManager().setCurrentGroup(null);
                    }
                });
            }
        });
        buildDialog.setNegativeButton("No go back", null);
        buildDialog.show();
    }

    /**
     * Builds a Dialog box to leave a group
     *
     * @param activity
     * @param title
     * @param message
     * @param leaveMessage
     */
    public void leaveLeaderDialog(Activity activity, String title, CharSequence message, String leaveMessage){
        AlertDialog.Builder buildThis = new AlertDialog.Builder(activity);

        buildThis.setTitle(title);
        buildThis.setMessage(message);
        buildThis.setPositiveButton(leaveMessage, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mGroup.removeGroupMember(CrowdControlApplication.getInstance().getModelManager().getCurrentUser().getProfile());
                mGroup.deleteInBackground(new BaseModel.DeleteCallback() {
                    @Override
                    public void doneDeletingModel(BaseModel object, Exception ex) {
                        // Verify operation was a success
                        if (ex != null) {
                            Log.d(TAG, "Unable to save group");
                            return;
                        }

                        CrowdControlApplication.getInstance().getModelManager().setCurrentGroup(null);
                    }
                });

//                // Attempt to save change to group in background
//                mGroup.saveInBackground(new BaseModel.SaveCallback() {
//                    @Override
//                    public void doneSavingModel(BaseModel object, Exception ex) {
//
//                        // Verify operation was a success
//                        if (ex != null) {
//                            Log.d(TAG, "Unable to save group");
//                            return;
//                        }
//
//                        CrowdControlApplication.getInstance().getModelManager().setCurrentGroup(null);
//                        launchGroupJoinActivity();
//                    }
//                });
            }
        });
        buildThis.setNegativeButton("No go back", null);
        buildThis.show();
    }

//    /*
//     *  Grabs the click in the list view and then Joins the Group that was clicked on
//     */
//    @Override
//    public void onItemClick(AdapterView parent, View view, int position, long id) {
//        //TODO request to join group instead of just joining it
//
//        // Get the selected group
//        final UserProfileModel userProfileModel = mUserModelAdapter.getItem(position);
//
//        // First load members from current group
//        CrowdControlApplication.getInstance().getModelManager().getCurrentGroup().loadInBackground(new BaseModel.LoadCallback() {
//            @Override
//            public void doneLoadingModel(BaseModel object, Exception ex) {
//
//                // Verify operation was successful
//                if (object == null || ex != null) {
//                    Log.d(TAG, "Unable to load group model");
//                    return;
//                }
//
//            }
//        });
//    }
}
