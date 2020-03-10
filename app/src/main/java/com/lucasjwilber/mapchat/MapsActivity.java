package com.lucasjwilber.mapchat;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public double userLat;
    public double userLong;
    public String userCurrentAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        Log.i("ljw", "successfully got location");
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            userLat = location.getLatitude();
                            userLong = location.getLongitude();
                            Log.i("ljw", "lat: " + userLat + "\nlong: " + userLong);

                            //call geocode to get address with latLong
                            Log.i("ljw", "calling api...");
                            AsyncTask.execute(() -> {
                                //TODO: update api key below to support geocode. replace with taskmaster api key temporarily if necessary.
                                try {
                                    URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + userLat + "," + userLong + "&key=AIzaSyBcsEWrD6BkmgWGYZkxVsywLXIaqxsvl-Q");

                                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                                    con.setRequestMethod("GET");
                                    Log.i("ljw", "called api, reading response...");
                                    BufferedReader in = new BufferedReader(
                                            new InputStreamReader(con.getInputStream()));
                                    String line;
                                    StringBuilder content = new StringBuilder();
                                    while ((line = in.readLine()) != null) {
                                        content.append(line);
                                        if (line.contains("formatted_address")) {
                                            userCurrentAddress = line.split("\" : \"")[1];
                                            userCurrentAddress = userCurrentAddress.substring(0, userCurrentAddress.length() - 2);
                                            Log.i("ljw", "found formatted addresss: " + userCurrentAddress);
                                            break;
                                        }
                                    }
                                    in.close();
                                    con.disconnect();

                                    LatLng userCurrentLatLng = new LatLng(userLat, userLong);

                                    //dummy data for comments (markers) near the user:
                                    List<MarkerOptions> allMarkers = new LinkedList<>();
                                    allMarkers.add(new MarkerOptions()
                                            .position(new LatLng(userLat + 0.01, userLong + 0.01))
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                            .title("comment number 1"));
                                    allMarkers.add(new MarkerOptions()
                                            .position(new LatLng(userLat + 0.02, userLong - 0.01))
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                            .title("comment number 2"));
                                    allMarkers.add(new MarkerOptions()
                                            .position(new LatLng(userLat - 0.03, userLong - 0.02))
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                            .title("comment number 3"));

                                    //update map on main thread
                                    Handler handler = new Handler(Looper.getMainLooper()) {
                                        @Override
                                        public void handleMessage(Message input) {
                                            Log.i("ljw", "lat/lng for marker is " + userLat + "/" + userLong);

                                            mMap.addMarker(new MarkerOptions()
                                                    .position(userCurrentLatLng)
                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                                    .title("My Location"));

                                            //add dummy data markers:
                                            for (MarkerOptions marker : allMarkers) {
                                                mMap.addMarker(marker);
                                            }

                                            mMap.moveCamera(CameraUpdateFactory.newLatLng(userCurrentLatLng));
                                            //this zooms in on the user's location by restricting how far you can zoom out:
                                            //TODO: set the default zoom but somehow still allow users to zoom out farther than that
                                            mMap.setMinZoomPreference((float) 14.0);
                                            //using map type 2 to remove clutter, so only our markers are displayed:
                                            //https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap#setMapType(int)
                                            mMap.setMapType(2);
                                        }
                                    };
                                    handler.obtainMessage().sendToTarget();

                                } catch (MalformedURLException e) {
                                    Log.i("ljw", "malformedURLexception:\n" + e.toString());
                                } catch (ProtocolException e) {
                                    Log.i("ljw", "protocol exception:\n" + e.toString());
                                } catch (IOException e) {
                                    Log.i("ljw", "IO exception:\n" + e.toString());
                                }

                            });
                        }
                    })
                    .addOnFailureListener(this, error -> {
                        Log.i("ljw", "error getting location:\n" + error.toString());
                    });
        }
    }
}
