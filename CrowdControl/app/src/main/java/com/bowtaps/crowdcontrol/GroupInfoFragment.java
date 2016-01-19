package com.bowtaps.crowdcontrol;

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
import com.bowtaps.crowdcontrol.model.ParseGroupModel;
import com.parse.ParseObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link GroupInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 * Will display information of the current group and allow the
 * user to leave the group
 */
public class GroupInfoFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";

    private String mText;

    private GroupModel mGroup;

    Button mLeaveGroupButton;

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
        args.putString(ARG_PARAM1, text);
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
        if (getArguments() != null) {
            mText = getArguments().getString(ARG_PARAM1);
        }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mGroup = CrowdControlApplication.getInstance().getModelManager().getCurrentGroup();
        TextView GroupName;
        TextView GroupDescription;


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_group_info, container, false);
        ((TextView) v.findViewById(R.id.group_test)).setText(mText);
        GroupName = (TextView) v.findViewById(R.id.text_group_name);
        GroupDescription = (TextView) v.findViewById(R.id.text_group_description);
        GroupName.setText(mGroup.getGroupName());
        GroupDescription.setText(mGroup.getGroupDescription());

        // Get handles to Buttons
        mLeaveGroupButton = (Button) v.findViewById(R.id.leave_group_button);

        // Declare button clicks
        mLeaveGroupButton.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        // Handles clicks on items in view
        // in this case, either the facebook button or the create account button

        switch (view.getId()) {
            case R.id.leave_group_button:
                onLeaveGroupButtonClick((Button) view);
                break;

            default:
                // Sorry, you're outta luck
                break;
        }
    }

    private void onLeaveGroupButtonClick(Button view) {
        launchLeaveGroupButtonClick();
    }

    /**
     * Retrieves the current user and logs them out. The intent is then set to direct to the SignupActivity
     * when the user is logged out.
     */
    //TODO: Should make sure to leave a group first before logging out.
    private void launchLeaveGroupButtonClick() {
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
                getActivity().finish();
            }
        });
    }
}
