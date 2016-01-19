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
 * A subclass of an {@link ArrayAdapter} to be used to adapt a {@link List} of {@link GroupModel}
 * objects for display in a {@link ListView}.
 *
 * @author Daniel Andrus
 * @since 2016-01-18
 */
public class GroupModelAdapter extends ArrayAdapter<GroupModel> {

    /**
     * Class constructor. Instantiates a new {@link GroupModelAdapter} with no data.
     *
     * @param context The context for this adapter.
     */
    public GroupModelAdapter(Context context) {
        super(context, 0);
    }

    /**
     * Class constructor. Instantiates a new {@link GroupModelAdapter} and initializes it with an
     * existing {@link List} of {@link GroupModel} objects to use as its data set.
     *
     * @param context The context for this adapter.
     * @param items The data set for this adapter to use.
     */
    public GroupModelAdapter(Context context, List<GroupModel> items) {
        super(context, 0, items);
    }

    /**
     * Builds and fills a view object that can be used to render an individual item in the data set.
     *
     * @param position The numerical index of the item in the data set to build the {@link View}
     *                 for.
     * @param convertView The existing {@link View} object to fill with data from the item at
     *                    position. If this value is null, a new {@link View} will be instantiated
     *                    and inflated.
     * @param parent
     *
     * @return A {@link View} object ready for display in the {@link ListView}.
     */
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
