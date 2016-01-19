package com.bowtaps.crowdcontrol.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bowtaps.crowdcontrol.R;
import com.bowtaps.crowdcontrol.model.GroupModel;

import java.util.List;

/**
 * @author Daniel Andrus
 * @since 2016-01-18
 */
public class GroupModelAdapter extends ArrayAdapter<GroupModel> {

    public GroupModelAdapter(Context context, int resource) {
        super(context, resource);
    }

    public GroupModelAdapter(Context context, int resource, List<GroupModel> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        // Inflate the view if none exists currently
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item_group, null);
        }

        GroupModel model = getItem(position);

        if (model != null) {

            // Add title to view
            TextView titleTextView = (TextView) v.findViewById(R.id.list_group_name);
            if (titleTextView != null) {
                titleTextView.setText(model.getGroupName());
            }

            // Add description to view
            TextView descriptionTextView = (TextView) v.findViewById(R.id.list_group_description);
            if (descriptionTextView != null) {
                descriptionTextView.setText(model.getGroupDescription());
            }
        }

        return v;
    }

}
