package com.bowtaps.crowdcontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bowtaps.crowdcontrol.model.BaseModel;
import com.bowtaps.crowdcontrol.model.GroupModel;


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

    private static final String TAG = GroupInfoFragment.class.getSimpleName();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param text Test text 1
     * @return A new instance of fragment GroupInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
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
                launchGroupJoinActivity();
            }
        });
        //stopService(new Intent(this, MessageService.class));
    }

    /**
     * Updates views in fragment to reflect the current state of {@link #mGroup}.
     */
    private void updateViews() {

        // Use data from current group to fill UI elements with data
        if(mGroup.getGroupLeader() != null){
            mGroupLeaderTextView.setText(mGroup.getGroupLeader().getDisplayName());
        }
        mGroupDescriptionTextView.setText(mGroup.getGroupDescription());
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
     * Builds a Dialog box
     *
     * @param activity
     * @param title
     * @param message
     * @param leaveMessage
     */
    public void leaveDialog(Activity activity, String title, CharSequence message, String leaveMessage){
        AlertDialog.Builder buildThis = new AlertDialog.Builder(activity);

        buildThis.setTitle(title);
        buildThis.setMessage(message);
        buildThis.setPositiveButton(leaveMessage, null);
        buildThis.setNegativeButton("No go back", null);
        buildThis.show();
    }
}
