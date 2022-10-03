package com.example.geofence_cmp309_1901560;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

public class GeofenceHelper extends ContextWrapper {

    // TAG, pending intent object and empty ID string
    private final static String TAG = "GeofenceHelper";
    PendingIntent pendingIntent;
    String geofenceID = "";

    // Class constructor
    public GeofenceHelper(Context base) {
        super(base);
    }

    // Geofencing request method which builds the fences and sets the initial trigger type
    public GeofencingRequest getGeofencingRequest(Geofence geoFence){
        return new GeofencingRequest.Builder()
                .addGeofence(geoFence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();

    }

    /* Method which gets the geofence data, transition types and loitering delay
       (time spent in/within/out to trigger a transition) */
    public Geofence getGeofence (String ID, LatLng latLng, float radius, int transitionTypes) {
        geofenceID = ID;
        return new Geofence.Builder()
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setRequestId(ID)
                .setTransitionTypes(transitionTypes)
                .setLoiteringDelay(5000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();

    }

    // Method grabbing the pending intent and sending it to the broadcast receiver
    public PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        if (pendingIntent != null) {
            return pendingIntent;
        } else {

            pendingIntent = PendingIntent.getBroadcast(this, 2607, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            return pendingIntent;
        }

    }

    // Error strings for different cases
    public String getErrorString (Exception e) {
        if (e instanceof ApiException){
            ApiException apiException = (ApiException) e;
            switch (apiException.getStatusCode()){
                case GeofenceStatusCodes
                        .GEOFENCE_NOT_AVAILABLE:
                    return "GEOFENCE_NOT_AVAILABLE";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_GEOFENCES:
                    return "GEOFENCE_TOO_MANY_GEOFENCES";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    return "GEOFENCE_TOO_MANY_PENDING_INTENTS";
            }
        }
        return e.getLocalizedMessage();
    }

}
