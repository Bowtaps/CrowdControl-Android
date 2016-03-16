package com.bowtaps.crowdcontrol.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bowtaps.crowdcontrol.R;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

/**
 *  This Custom Parse UserModelAdapter is used to quarry Parse for Group Data and adapt that
 *  data to be displayed in a list view
 *
 *  @see com.bowtaps.crowdcontrol.GroupJoinActivity
 *  @author Johnny Ackerman
 */
public class GroupParseAdapter extends ParseQueryAdapter<ParseObject> {

    /**
     *  This QueryFactory generates a query used to grab group information
     *
     *  @see com.bowtaps.crowdcontrol.GroupJoinActivity
     */
    public GroupParseAdapter(Context context) {
        // Use the QueryFactory to construct a PQA that will show all groups
        // currently sorted by the date of which it was created
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("Group");
                query.addAscendingOrder("createdAt");
                //query.whereEqualTo("GroupLocation", true);
                return query;
            }
        });
    }

    /**
     * Customize the layout by overriding getItemView. This allows for use of
     * a custom list view, in displaying the list of groups
     *
     *  @see com.bowtaps.crowdcontrol.GroupJoinActivity
     */
    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_item_group, null);
        }

        super.getItemView(object, v, parent);

        // Add and download the image ***** we can use for group images later
//        ParseImageView todoImage = (ParseImageView) v.findViewById(R.id.icon);
//        ParseFile imageFile = object.getParseFile("image");
//        if (imageFile != null) {
//            todoImage.setParseFile(imageFile);
//            todoImage.loadInBackground();
//        }

        // Add the title view
        TextView titleTextView = (TextView) v.findViewById(R.id.list_group_name);
        titleTextView.setText(object.getString("GroupName"));

        // Give group Description
        TextView groupDescriptionView = (TextView) v.findViewById(R.id.list_group_description);
        groupDescriptionView.setText(object.getString("GroupDescription"));
        return v;
    }

}