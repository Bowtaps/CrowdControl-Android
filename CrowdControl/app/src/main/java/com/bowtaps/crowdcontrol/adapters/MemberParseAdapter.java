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
 * Created by 7143145 on 3/15/2016.
 */
public class MemberParseAdapter extends ParseQueryAdapter<ParseObject> {
    /**
     *  This QueryFactory generates a query used to grab group information
     *
     *  @see com.bowtaps.crowdcontrol.GroupJoinActivity
     */
    public MemberParseAdapter(Context context) {
        // Use the QueryFactory to construct a PQA that will show all groups
        // currently sorted by the date of which it was created
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("CCUser");
                query.addAscendingOrder("DisplayName");
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
            v = View.inflate(getContext(), R.layout.list_item_member, null);
        }

        super.getItemView(object, v, parent);

        // Add the DisplayName
        TextView titleTextView = (TextView) v.findViewById(R.id.list_member_user_name);
        titleTextView.setText(object.getString("DisplayName"));

        return v;
    }
}
