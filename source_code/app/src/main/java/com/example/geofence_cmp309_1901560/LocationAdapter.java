package com.example.geofence_cmp309_1901560;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/* This is the same adapter as in Lab6 but using FirestoreContact class instead of String[] */
public class LocationAdapter extends ArrayAdapter<FirestoreLocation> {

    // Adapter constructor
    public LocationAdapter(Context context, ArrayList<FirestoreLocation> loc){
        super(context, 0, loc);
    }

    // Gets listview within activity_location_list and sets data
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the locations data for this position
        FirestoreLocation loc = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.location_layout, parent, false);
        }
        // Lookup views

        TextView display_location = (TextView) convertView.findViewById(R.id.display_location);
        TextView display_enter = (TextView) convertView.findViewById(R.id.display_entry);
        TextView display_leave = (TextView) convertView.findViewById(R.id.display_leave);

        // Add the data to the template view
        display_location.setText("Landmark: " + loc.getLocationValue());
        if (loc.getEnterValue() != null ) {
            display_enter.setText("Enter Date: " + loc.getEnterValue().toString());
            display_leave.setVisibility(View.GONE);
        } else if (loc.getLeaveValue() != null ){
            display_enter.setVisibility(View.GONE);
            display_leave.setText("Leave Date: " + loc.getLeaveValue().toString());
        }

        // Return the completed view to render on screen
        return convertView;
    }
}