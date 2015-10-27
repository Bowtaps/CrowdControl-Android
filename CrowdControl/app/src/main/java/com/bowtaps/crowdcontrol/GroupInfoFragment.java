package com.bowtaps.crowdcontrol;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link GroupInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupInfoFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private String mText;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mText = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_group_info, container, false);
        ((TextView) v.findViewById(R.id.group_test)).setText(mText);
        return v;
    }


}
