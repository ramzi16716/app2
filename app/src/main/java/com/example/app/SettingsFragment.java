package com.example.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment implements OnMapReadyCallback {

    private SupportMapFragment supportMapFragment;
    private GoogleMap gMap;
    private LocationManager locationManager;
    private Marker nearestPoliceStationMarker;
    private Marker currentLocationMarker;
    private List<LatLng> policeStationsLocations = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.id_map);
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        // Add police stations locations to the list
        addPoliceStationsLocations();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        Dexter.withContext(requireContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        if (isGPSEnabled()) {
                            getCurrentLocation();
                        } else {
                            // If GPS is not enabled, open location settings
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private boolean isGPSEnabled() {
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Task<Location> task = LocationServices.getFusedLocationProviderClient(requireActivity()).getLastLocation();
        task.addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Current Location")
                            .snippet("Latitude: " + latLng.latitude + ", Longitude: " + latLng.longitude);
                    currentLocationMarker = gMap.addMarker(markerOptions);
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                    // Find the nearest police station
                    LatLng nearestPoliceStation = findNearestPoliceStation(location);
                    if (nearestPoliceStation != null) {
                        // Add marker for the nearest police station
                        addNearestPoliceStationMarker(nearestPoliceStation);

                        // Request directions from current location to nearest police station
                        requestDirections(latLng, nearestPoliceStation);
                    }
                }
            }
        });
    }

    private LatLng findNearestPoliceStation(Location currentLocation) {
        LatLng nearestStation = null;
        float smallestDistance = Float.MAX_VALUE;

        for (LatLng stationLocation : policeStationsLocations) {
            Location stationLocationObj = new Location("Station Location");
            stationLocationObj.setLatitude(stationLocation.latitude);
            stationLocationObj.setLongitude(stationLocation.longitude);

            float distance = currentLocation.distanceTo(stationLocationObj);
            if (distance < smallestDistance) {
                smallestDistance = distance;
                nearestStation = stationLocation;
            }
        }

        return nearestStation;
    }

    private void addNearestPoliceStationMarker(LatLng nearestStation) {
        float couleurBleue = BitmapDescriptorFactory.HUE_BLUE;

        for (LatLng stationLocation : policeStationsLocations) {
            // Add a marker for each police station on the map with blue color
            gMap.addMarker(new MarkerOptions()
                    .position(stationLocation)
                    .title("Poste de Police")
                    .icon(BitmapDescriptorFactory.defaultMarker(couleurBleue)));
        }
        if (nearestPoliceStationMarker != null) {
            nearestPoliceStationMarker.remove();
        }

        float couleurVerte = BitmapDescriptorFactory.HUE_GREEN;
        nearestPoliceStationMarker = gMap.addMarker(new MarkerOptions().position(nearestStation).title("Nearest Police Station")
                .icon(BitmapDescriptorFactory.defaultMarker(couleurVerte)));
    }



    private void requestDirections(LatLng origin, LatLng destination) {
        // Construct the request URL for Directions API
        String requestUrl = "https://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + origin.latitude + "," + origin.longitude
                + "&destination=" + destination.latitude + "," + destination.longitude
                + "&key=" + "gAIzaSyAjUR45eAEpLCJb8nGWPCd001BiuJn0wA8";

        // Make a network request to fetch directions
        // You can use Volley, Retrofit, or any other networking library to make the HTTP request
        // After receiving the response, parse the JSON and draw the path on the map
        // Here, we assume you have implemented the networking and parsing logic
        // For brevity, we omit the implementation details here
    }
    private void addPoliceStationsLocations() {
        policeStationsLocations.add(new LatLng(36.840539, 7.735248));
        policeStationsLocations.add(new LatLng(36.849223, 7.750167));
        policeStationsLocations.add(new LatLng(36.862137, 7.780526));
        policeStationsLocations.add(new LatLng(36.923680, 7.757167));
        policeStationsLocations.add(new LatLng(36.917736, 7.766498));
        policeStationsLocations.add(new LatLng(36.907239, 7.737151));
        policeStationsLocations.add(new LatLng(36.908401, 7.753767));
        policeStationsLocations.add(new LatLng(36.906021, 7.757077));
        policeStationsLocations.add(new LatLng(36.901480, 7.744071));
        policeStationsLocations.add(new LatLng(36.899916, 7.753016));
        policeStationsLocations.add(new LatLng(36.894766, 7.751596));
        policeStationsLocations.add(new LatLng(36.898523, 7.752124));
        policeStationsLocations.add(new LatLng(36.891593, 7.726918));
        policeStationsLocations.add(new LatLng(36.882559, 7.726929));
        policeStationsLocations.add(new LatLng(36.873683, 7.716801));
        policeStationsLocations.add(new LatLng(36.807507, 7.736830));
        policeStationsLocations.add(new LatLng(36.256911, 6.716669));
        policeStationsLocations.add(new LatLng(36.259918, 6.702720));
        policeStationsLocations.add(new LatLng(36.252434, 6.581549));
        policeStationsLocations.add(new LatLng(36.249689, 6.755191));
        policeStationsLocations.add(new LatLng(36.253382, 6.571672));
        policeStationsLocations.add(new LatLng(36.267012, 6.500892));
    }
}
