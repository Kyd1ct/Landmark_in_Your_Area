package com.example.geofence_cmp309_1901560;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends AppCompatActivity {

    // An array of all permissions we want ot check when the app launches
    String[] permissions = {
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    // Connection and location objects/variables
    ConnectivityManager connManager;
    boolean isConnected = false;
    private static final int LOCATION_REQUEST = 1;
    public LocationRequest mLocationRequest;
    public LocationCallback mLocationCallback;
    public FusedLocationProviderClient mLocationProvider;


    @Override
    @SuppressLint("MissingPermission")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check all permissions using the custom utility class and request them
        if (!Utils.checkAllPermissions(this, permissions)) {
            requestPermissions(permissions, 0);
        }
        // Initialise the connectivity manager object, do this before starting any network operations
        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();
        // Register callback to monitor for changes
        connManager.registerNetworkCallback(networkRequest, netCallback);

        // Get active networks info and check if it's connected
        NetworkInfo netInfo = connManager.getActiveNetworkInfo();
        isConnected = (netInfo != null && netInfo.isConnected());
        if (isConnected) {
            /* Get network capabilities */
            Network net = connManager.getActiveNetwork();
            NetworkCapabilities netCaps = connManager.getNetworkCapabilities(net);
        } else {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }

        // Location permission check
        int locationPermissionCheck = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        if (locationPermissionCheck != PackageManager.PERMISSION_DENIED) {
            initialiseLocationUpdates();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
        }

        // Button variables
        Button btn = (Button) findViewById(R.id.bSwitch);
        Button btn_loc = (Button) findViewById(R.id.bLocations);

        // Lambda onclick listeners transferring the user to other activities
        btn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MapsActivity.class)));

        btn_loc.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LocationListActivity.class)));

    }

    // Method initialising location updates
    @SuppressLint("MissingPermission")
    private void initialiseLocationUpdates() {
        mLocationProvider = LocationServices.getFusedLocationProviderClient(this);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null)
                    return;
            }
        };


        mLocationProvider.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper());
    }



    /* NOTE! This is just a normal variable declaration for Network Callback,
     * it is outside functions but inside the class.
     * This was done so to keep the top of the document tidy */
    ConnectivityManager.NetworkCallback netCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onLost(Network network) {
            /* TODO: report the loss to the user and disable the functionality that depends on it */
        }

        @Override
        public void onLosing(Network network, int maxMsToLive) {
            /* TODO: report the potential loss to the user */
        }

        @Override
        public void onUnavailable() {
            /* TODO: report the loss to the user and disable the functionality that depends on it */
        }

        @Override
        public void onAvailable(Network network) {
            /* TODO: report the new connection and resume enable the network functionality */
        }

        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            /* TODO: report the change in capabilities and adjust the app behaviour accordingly */
        }
    };

    /* Don't forget to unregister the callback when no longer needed */
    @Override
    protected void onDestroy() {
        connManager.unregisterNetworkCallback(netCallback);
        super.onDestroy();
    }
}


