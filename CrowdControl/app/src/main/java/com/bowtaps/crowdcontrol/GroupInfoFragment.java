package com.bowtaps.crowdcontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bowtaps.crowdcontrol.model.ParseGroupModel;
import com.bowtaps.crowdcontrol.model.ParseUserModel;
import com.parse.ParseObject;

import org.w3c.dom.Text;


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

    private ParseObject mGroup;

    Button mLeaveGroupButton;

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

        mGroup = new ParseObject("Group");
        mGroup = CrowdControlApplication.aGroup;
        TextView GroupName;
        TextView GroupDescription;


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_group_info, container, false);
        ((TextView) v.findViewById(R.id.group_test)).setText(mText);
        GroupName = (TextView) v.findViewById(R.id.text_group_name);
        GroupDescription = (TextView) v.findViewById(R.id.text_group_description);
        GroupName.setText(mGroup.get("GroupName").toString());
        GroupDescription.setText(mGroup.get("GroupDescription").toString());

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
        ParseGroupModel parseGroupModel = new ParseGroupModel(CrowdControlApplication.aGroup);
        

        //finish();
    }
}
