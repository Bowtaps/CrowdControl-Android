package com.bowtaps.crowdcontrol.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bowtaps.crowdcontrol.R;
import com.bowtaps.crowdcontrol.model.GroupModel;
import com.bowtaps.crowdcontrol.model.UserProfileModel;

import java.util.List;

/**
 * Created by Johnny on 3/15/2016.
 *
 * Ment to adapt a user list to a list view
 */
public class UserModelAdapter extends ArrayAdapter<UserProfileModel> {

    /**
     * Class constructor. Instantiates a new {@link GroupModelAdapter} with no data.
     *
     * @param context The context for this adapter.
     */
    public UserModelAdapter(Context context) {
        super(context, 0);
    }

    /**
     * Class constructor. Instantiates a new {@link GroupModelAdapter} and initializes it with an
     * existing {@link List} of {@link GroupModel} objects to use as its data set.
     *
     * @param context The context for this adapter.
     * @param items The data set for this adapter to use.
     */
    public UserModelAdapter(Context context, List<UserProfileModel> items) {
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
            v = vi.inflate(R.layout.list_item_member, null);
        }

        UserProfileModel model = getItem(position);

        if (model != null) {

            // Add title to view
            TextView titleTextView = (TextView) v.findViewById(R.id.list_member_user_name);
            if (titleTextView != null) {
                titleTextView.setText(model.getDisplayName());
            }
        }

        return v;
    }
}
