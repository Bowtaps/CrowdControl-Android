package com.bowtaps.crowdcontrol;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by 7143145 on 3/31/2016.
 */
public class InviteConfirmFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private String mText;

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
        args.putString(ARG_PARAM1, text);
        fragment.setArguments(args);
        return fragment;
    }

    public InviteConfirmFragment() {
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
        View v = inflater.inflate(R.layout.fragment_event, container, false);
        ((TextView) v.findViewById(R.id.event_test)).setText(mText);
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
