package com.example.geofence_cmp309_1901560;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.Calendar;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    // Date variable used to monitor the device's current time
    Date currentTime = Calendar.getInstance().getTime();
    // Databse object
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Date variables for the enter and leave times
    Date timeEntered;
    Date timeLeft;
    // Geofence list
    List<Geofence> geofenceList;

    private static final String TAG = "GeofenceBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        // Geofence event and notification handler objects
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        NotificationHandler notificationHandler = new NotificationHandler(context);

        // Log error if events are not received
        if (geofencingEvent.hasError()){
            Log.d(TAG, "onReceive Error receiving geofencing event");
            return;
        }

        // Variable monitoring the transition type
        int transitionType = geofencingEvent.getGeofenceTransition();

        // The geofence list obtaining the triggered geofences
        geofenceList = geofencingEvent.getTriggeringGeofences();

        // Switch case monitoring the transition types
        switch (transitionType) {
            // Action based on transition Enter type
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                /* Send notification with the notificationHandler object with the landmark name,
                   date of entry and a timer showing the time spent inside the area. */
                notificationHandler.sendDwellNotification("Landmark nearby!", "You have entered " + geofenceList.get(0).getRequestId() + "'s area!\nDate of entry: " + currentTime, LocationListActivity.class);
                timeEntered = currentTime;
                addLocationEnterToFirebase(geofenceList.get(0).getRequestId(), currentTime);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                // Optional functionalities for staying inside the geofence
                break;
            // Action based on transition Leave type
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                /* Send notification with the notificationHandler object with the landmark name,
                   and date of leave. Pressing it opens the visited locations list.*/
                notificationHandler.sendLeaveNotification("Warning!", "You have left " + geofenceList.get(0).getRequestId() + "'s area! \nDate of departure: " + currentTime, LocationListActivity.class);
                timeLeft = currentTime;
                addLocationLeaveToFirebase(geofenceList.get(0).getRequestId(), currentTime);
                break;
        }

    }
    // Adds enter location to Firestore
    private void addLocationEnterToFirebase(String geofence, Date time) {
        // Construct a map of key-value pairs
        Map<String, Object> location = new HashMap<>();
        location.put(FirestoreLocation.KEY_LOCATION, geofence);
        location.put(FirestoreLocation.KEY_TIME_IN, time);

        // Add a new document with a generated ID (name)
        db.collection(FirestoreLocation.COLLECTION_PATH).add(location)
                .addOnSuccessListener(documentReference -> Log.d(Utils.TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(Utils.TAG, "Error adding document", e));

    }

    // Adds leave location to Firestore
    private void addLocationLeaveToFirebase(String geofence, Date time) {
        // Construct a map of key-value pairs
        Map<String, Object> location = new HashMap<>();
        location.put(FirestoreLocation.KEY_LOCATION, geofence);
        location.put(FirestoreLocation.KEY_TIME_OUT, time);

        // Add a new document with a generated ID (name)
        db.collection(FirestoreLocation.COLLECTION_PATH).add(location)
                .addOnSuccessListener(documentReference -> Log.d(Utils.TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(Utils.TAG, "Error adding document", e));

    }

}