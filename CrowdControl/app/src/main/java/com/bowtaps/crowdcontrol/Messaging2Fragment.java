package com.bowtaps.crowdcontrol;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Messaging2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 * This Fragment will handle all messaging between user and group
 */
public class Messaging2Fragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private String mText;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param text Test text 1
     * @return A new instance of fragment Messaging2Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Messaging2Fragment newInstance(String text) {
        Messaging2Fragment fragment = new Messaging2Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, text);
        fragment.setArguments(args);
        return fragment;
    }

    public Messaging2Fragment() {
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
        View v = inflater.inflate(R.layout.fragment_messaging, container, false);
        ((TextView) v.findViewById(R.id.test_text)).setText(mText);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

}
