package com.example.geofence_cmp309_1901560;


import com.google.firebase.firestore.PropertyName;

import java.util.Date;

public class FirestoreLocation {
    // Collection path and value names
    public static final String COLLECTION_PATH = "Locations";
    public static final String KEY_LOCATION = "locationValue";
    public static final String KEY_TIME_IN = "enterValue";
    public static final String KEY_TIME_OUT = "leaveValue";

    // Strings storing the values from firebase
    private String locationValue;
    private Date enterValue;
    private Date leaveValue;
    private String id;

    // Firestore builder
    public FirestoreLocation(){}

    // Getters and setters for values
    public String getLocationValue(){return locationValue;}
    public Date getEnterValue(){return enterValue;}
    public Date getLeaveValue(){return leaveValue;}

    public String getID(){return id;}
    public void setID(String id){this.id = id;}

    // Method returning the obtained data as a string with specific format
    @Override
    public String toString(){
        return String.format("id: %s; location_value: %s; enter_value: %s; leave_value: %s", id, locationValue, enterValue, leaveValue);
    }
}
