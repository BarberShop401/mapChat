package com.lucasjwilber.mapchat;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, PopupMenu.OnMenuItemClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public double userLat;
    public double userLng;
    public String userCurrentAddress;
    FirebaseFirestore dbInstance;
    LinearLayout addCommentForm;
    BitmapDescriptor commentIcon;
    BitmapDescriptor userIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        addCommentForm = findViewById(R.id.addCommentForm);

        commentIcon = BitmapDescriptorFactory.fromBitmap(getBitmap(R.drawable.ic_chat_icon));
        userIcon = BitmapDescriptorFactory.fromBitmap(getBitmap(R.drawable.ic_user_pin));

        dbInstance = FirebaseFirestore.getInstance();
        getCommentsFromDbAndCreateMapMarkers();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

//    pop up method to show hamburger
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_popup);
        popup.show();
    }

//    private void writeNewCommentToDB(String title, String body, double userLat, double userLng, long timestamp) {
//        Comment newComment = new Comment(title, body, userLat, userLng, timestamp);
//        // Create a new user with a first and last name
//
//        Map<String, Object> user = new HashMap<>();
//        user.put("first", "Ada");
//        user.put("last", "Lovelace");
//        user.put("born", 1815);
//
//// Add a new document with a generated ID
//        dbInstance.collection("users")
//                .add(user)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.i("vik", "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.i("vik", "Error adding document", e);
//                    }
//                });
//        dbInstance.collection("users")
//            .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//        @Override
//        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//            if (task.isSuccessful()) {
//                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
//                    Log.i("vik", document.getId() + " => " + document.getData());
//                }
//            } else {
//                Log.i("vik", "Error getting documents.", task.getException());
//            }
//        }
//    });
//    }


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
        //remove the directions/gps buttons
        mMap.getUiSettings().setMapToolbarEnabled(false);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        Log.i("ljw", "successfully got location");
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            userLat = location.getLatitude();
                            userLng = location.getLongitude();
                            Log.i("ljw", "lat: " + userLat + "\nlong: " + userLng);

                            //call geocode to get formatted address
                            Log.i("ljw", "calling geocode api...");
                            AsyncTask.execute(() -> {
                                try {
                                    URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + userLat + "," + userLng + "&key=AIzaSyAy2p9oGlSYUvXCQE0K8az2NcFKPK_YVEY");

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

                                } catch (MalformedURLException e) {
                                    Log.i("ljw", "malformedURLexception:\n" + e.toString());
                                } catch (ProtocolException e) {
                                    Log.i("ljw", "protocol exception:\n" + e.toString());
                                } catch (IOException e) {
                                    Log.i("ljw", "IO exception:\n" + e.toString());
                                }

                                    //update map on main thread
                                    Handler handler = new Handler(Looper.getMainLooper()) {
                                        @Override
                                        public void handleMessage(Message input) {
                                            Log.i("ljw", "lat/lng for user is " + userLat + "/" + userLng);

                                            //add a marker to display the user's location:
                                            mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(userLat, userLng))
                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                                    .title("My Location")
                                                    .icon(userIcon)
                                                    .snippet(userCurrentAddress));

                                            //center the map on the user
                                            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(userLat, userLng)));

                                            //this zooms in on the user's location by restricting how far you can zoom out:
                                            //TODO: set the default zoom but somehow still allow users to zoom out farther than that
                                            mMap.setMinZoomPreference((float) 15.0);

                                            //using map type 2 to remove clutter, so only our markers are displayed:
                                            //https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap#setMapType(int)
                                            mMap.setMapType(2);
                                        }
                                    };
                                    handler.obtainMessage().sendToTarget();

                            });
                        }
                    })
                    .addOnFailureListener(this, error -> {
                        Log.i("ljw", "error getting location:\n" + error.toString());
                    });
        }
    }

    public void toggleFormVisibility(View v) {
        //toggle visibility
        addCommentForm.setVisibility(addCommentForm.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
    }

    public void submitCommentButtonClicked(View v) {
        //gather form data
        EditText commentTitleView = findViewById(R.id.commentTitleEditText);
        String commentTitle = commentTitleView.getText().toString();
        EditText commentBodyView = findViewById(R.id.commentBodyEditText);
        String commentBody = commentBodyView.getText().toString();

        //create a Comment object
        Comment comment = new Comment(commentTitle, commentBody, userLat, userLng, new Date().getTime());
        Log.i("ljw", "new comment created: " + comment.toString());

        //push it to DB
        dbInstance.collection("comments")
                .add(comment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i("vik", "DocumentSnapshot added with ID: " + documentReference.getId());
                        Log.i("ljw", "successfully added new comment to DB");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("vik", "Error adding document", e);
                    }
                });

        //hide form
        addCommentForm.setVisibility(View.INVISIBLE);
    }

    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    public void getCommentsFromDbAndCreateMapMarkers() {
        dbInstance.collection("comments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Log.i("ljw", document.getId() + " => " + document.getData());
                                Map<String, Object> data = document.getData();
                                String title = (String) data.get("title");
                                String text = (String) data.get("text");
                                double lat = (Double) data.get("lat");
                                double lng = (Double) data.get("lng");
                                long timestamp = new BigDecimal((Double) data.get("timestamp")).longValue();

                                Comment c = new Comment(
                                        (String) data.get("title"),
                                        (String) data.get("text"),
                                        (Double) data.get("lat"),
                                        (Double) data.get("lng"),
                                        timestamp);

                                mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(c.getLat(), c.getLng()))
                                    .icon(commentIcon)
                                    .title(c.getTitle())
                                    .snippet(c.getText()));

                                Log.i("ljw", "data: " + title + "," + text + ", " + lat + ", " + lng + ", ");
                                Log.i("ljw", c.toString());
                            }
                        } else {
                            Log.i("ljw", "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }
}
