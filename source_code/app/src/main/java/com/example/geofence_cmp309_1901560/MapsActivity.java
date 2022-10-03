package com.example.geofence_cmp309_1901560;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.geofence_cmp309_1901560.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    // Map and geofencing objects/tags
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    // Location codes and Fused location object
    final private int FINE_LOCATION_ACCESS_CODE = 1;
    final private int BACKGROUND_LOCATION_REQUEST_CODE = 2;
    FusedLocationProviderClient fusedLocationProviderClient;

    // Geofence data
    final private float GEOFENCE_RADIUS = 70;
    final private String GEOFENCE_MCMANUS = "The McManus Art and Museum Gallery";
    final private String GEOFENCE_VA = "V&A Dundee";
    final private String GEOFENCE_DISCOVERY = "Discovery Point and RSS Discovery";
    final private String GEOFENCE_VERDANT = "Verdant Works";
    final private String GEOFENCE_ARTS = "Dundee Contemporary Arts Museum";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Automatically generated map code
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        // Adds marker onclick listener
        mMap.setOnMarkerClickListener(this);
        // Seeks the user location
        enableUserLocation();

        /* Request background location permission if the SDK is 29 or above as this is required
           for newer SDK versions, then creates the geofences*/
        if (Build.VERSION.SDK_INT >= 29) {
            // Request Background Permission
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                createLandmarkGeofences();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    // Show permission request dialog to user
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_REQUEST_CODE );
                    createLandmarkGeofences();
                } else {
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_REQUEST_CODE );
                    createLandmarkGeofences();
                }
            }
        } else {
            // Add the Geofences
            createLandmarkGeofences();
        }

    }

    // Method combining the geofence creation for cleaner code
    public void createLandmarkGeofences() {
        createGeoFence ((new LatLng(56.4625, -2.9710)), GEOFENCE_MCMANUS);
        createGeoFence ((new LatLng(56.4575, -2.9670)), GEOFENCE_VA);
        createGeoFence ((new LatLng(56.4567, -2.9691)), GEOFENCE_DISCOVERY);
        createGeoFence ((new LatLng(56.4615, -2.9835)), GEOFENCE_VERDANT);
        createGeoFence ((new LatLng(56.4575, -2.9751)), GEOFENCE_ARTS);
    }

    /* Method implementing an onclick listener for the markers
       then sends an intent to the browser activity */
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Intent i = new Intent(MapsActivity.this, BrowserActivity.class);

        // Switch statement with marker titles as cases - opens landmark website in the webview
        switch (marker.getTitle()) {
            case "The McManus Art and Museum Gallery":
                Toast.makeText(this, "Opening website in custom web viewer", Toast.LENGTH_SHORT).show();
                i.setData(Uri.parse("https://www.mcmanus.co.uk/"));
                startActivity(i);
                break;
            case "V&A Dundee":
                Toast.makeText(this, "Opening website in custom web viewer", Toast.LENGTH_SHORT).show();
                i.setData(Uri.parse("https://www.vam.ac.uk/dundee"));
                startActivity(i);
                break;
            case "Discovery Point and RSS Discovery":
                Toast.makeText(this, "Opening website in custom web viewer", Toast.LENGTH_SHORT).show();
                i.setData(Uri.parse("https://www.rrsdiscovery.co.uk/"));
                startActivity(i);
                break;
            case "Verdant Works":
                Toast.makeText(this, "Opening website in custom web viewer", Toast.LENGTH_SHORT).show();
                i.setData(Uri.parse("https://www.verdantworks.co.uk/"));
                startActivity(i);
                break;
            case "Dundee Contemporary Arts Museum":
                Toast.makeText(this, "Opening website in custom web viewer", Toast.LENGTH_SHORT).show();
                i.setData(Uri.parse("https://www.dca.org.uk/"));
                startActivity(i);
                break;
        }
        return false;
    }

    // Checks if permissions are present, sets user location and zooms into it
    @SuppressLint("MissingPermission")
    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            zoomToUser();
        } else {
            // Request permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Permission request window
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_CODE);
                mMap.setMyLocationEnabled(true);
                zoomToUser();
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_CODE);
                mMap.setMyLocationEnabled(true);
                zoomToUser();
            }
        }
    }

    // Method which zooms into the user's location
    @SuppressLint("MissingPermission")
    private void zoomToUser() {
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(location -> {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        });
    }

    // Method used to create the geofence by taking the location and the geofence ID
    private void createGeoFence (LatLng latLng, String ID) {
        //mMap.clear();
        addMarker(latLng, ID);
        addCircle(latLng, GEOFENCE_RADIUS, ID);
        addGeofence(latLng, GEOFENCE_RADIUS, ID);
    }

    /* Method which makes use of a helper to add geofences based on radius, location and ID. It is used in createGeoFence.
       It also monitors the user transition with the helper. Additionally, it sends a pending intent to the BroadcastReceiver
       class which handles the transition types. */
    @SuppressLint("MissingPermission")
    private void addGeofence(LatLng latLng, float radius, String ID){
        Geofence geofence = geofenceHelper.getGeofence(ID , latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(unused -> Log.d(TAG, "Geofence Added." + ID))
                .addOnFailureListener(e -> {
                    String errorMessage = geofenceHelper.getErrorString(e);
                    Log.e(TAG, "onFailure" + errorMessage);
                });

    }
    // Method which adds a marker on the map
    private void addMarker(LatLng latLng, String ID){
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        // Switch statement taking the geofence ID and adding the appropriate title and colour for the landmark marker.
        switch (ID) {
            case GEOFENCE_MCMANUS:
                markerOptions.title("The McManus Art and Museum Gallery");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                break;
            case GEOFENCE_VA:
                markerOptions.title("V&A Dundee");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                break;
            case GEOFENCE_VERDANT:
                markerOptions.title("Verdant Works");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                break;
            case GEOFENCE_DISCOVERY:
                markerOptions.title("Discovery Point and RSS Discovery");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                break;
            case GEOFENCE_ARTS:
                markerOptions.title("Dundee Contemporary Arts Museum");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                break;
        }
        mMap.addMarker(markerOptions);

    }

    // Adds a geofence circle based on ID - specifies radius, center location and ARGB colours
    private void addCircle (LatLng latLng, float radius, String ID){
        CircleOptions circleOptions = new CircleOptions();
        switch (ID) {
            case GEOFENCE_MCMANUS:
                circleOptions.center(latLng);
                circleOptions.radius(radius);
                circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
                circleOptions.fillColor(Color.argb(64,255,0,0));
                circleOptions.strokeWidth(4);
                break;
            case GEOFENCE_VA:
                circleOptions.center(latLng);
                circleOptions.radius(radius);
                circleOptions.strokeColor(Color.argb(255, 255, 162, 0));
                circleOptions.fillColor(Color.argb(64,255,162,0));
                circleOptions.strokeWidth(4);
                break;
            case GEOFENCE_VERDANT:
                circleOptions.center(latLng);
                circleOptions.radius(radius);
                circleOptions.strokeColor(Color.argb(255, 0, 255, 209));
                circleOptions.fillColor(Color.argb(64,0,255,209));
                circleOptions.strokeWidth(4);
                break;
            case GEOFENCE_DISCOVERY:
                circleOptions.center(latLng);
                circleOptions.radius(radius);
                circleOptions.strokeColor(Color.argb(255, 0, 131, 255));
                circleOptions.fillColor(Color.argb(64,0,131,255));
                circleOptions.strokeWidth(4);
                break;
            case GEOFENCE_ARTS:
                circleOptions.center(latLng);
                circleOptions.radius(radius);
                circleOptions.strokeColor(Color.argb(255, 65, 159, 249));
                circleOptions.fillColor(Color.argb(64,65,159,249));
                circleOptions.strokeWidth(4);
                break;
        }
        mMap.addCircle(circleOptions);
    }


}