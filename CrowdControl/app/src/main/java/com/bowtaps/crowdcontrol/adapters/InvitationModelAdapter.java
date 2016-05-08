package com.bowtaps.crowdcontrol.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bowtaps.crowdcontrol.R;
import com.bowtaps.crowdcontrol.model.InvitationModel;

import java.util.List;

/**
 * @author Daniel Andrus
 */
public class InvitationModelAdapter extends ArrayAdapter<InvitationModel> {

    public InvitationModelAdapter(Context context) {
        super(context, 0);
    }

    public InvitationModelAdapter(Context context, List<InvitationModel> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        // Inflate the view if none exists currently
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item_notification, null);
        }

        InvitationModel model = getItem(position);

        if (model != null) {

            // Add title to view
            TextView titleTextView = (TextView) v.findViewById(R.id.list_notification_label);
            if (titleTextView != null) {
                titleTextView.setText(model.getGroup().getGroupName());
            }

            // Add description to view
            TextView descriptionTextView = (TextView) v.findViewById(R.id.list_notification_description);
            if (descriptionTextView != null) {
                descriptionTextView.setText("Invitation sent by " + model.getSender().getDisplayName());
            }
        }

        return v;
    }
}
