package com.bowtaps.crowdcontrol.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bowtaps.crowdcontrol.R;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class CustomParseAdapter extends ParseQueryAdapter<ParseObject> {

    public CustomParseAdapter(Context context) {
        // Use the QueryFactory to construct a PQA that will only show
        // Todos marked as high-pri
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("Group");
                query.addAscendingOrder("createdAt");
                //query.whereEqualTo("GroupLocation", true);
                return query;
            }
        });
    }

    // Customize the layout by overriding getItemView
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

        // Add a reminder of how long this item has been outstanding
        TextView timestampView = (TextView) v.findViewById(R.id.list_group_description);
        timestampView.setText(object.getString("GroupDescription"));
        return v;
    }

}