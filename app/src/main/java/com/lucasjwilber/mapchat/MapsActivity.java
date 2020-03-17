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
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, PopupMenu.OnMenuItemClickListener, GoogleMap.OnInfoWindowLongClickListener {

    private GoogleMap mMap;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public double userLat;
    public double userLng;
    public String userCurrentAddress;
    FirebaseFirestore dbInstance;
    LinearLayout addCommentForm;
    LinearLayout addReplyForm;
    TextView userLocationTV;
    BitmapDescriptor commentIcon;
    BitmapDescriptor userIcon;
    Comment currentSelectedComment;
    String currentSelectedCommentId;
    Marker currentSelectedMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        addCommentForm = findViewById(R.id.addCommentForm);
        addReplyForm = findViewById(R.id.addReplyForm);
        userLocationTV = findViewById(R.id.commentLocationTextView);

        commentIcon = BitmapDescriptorFactory.fromBitmap(getBitmap(R.drawable.ic_chat_icon));
        userIcon = BitmapDescriptorFactory.fromBitmap(getBitmap(R.drawable.ic_user_pin));

        dbInstance = FirebaseFirestore.getInstance();
        getCommentsFromDbAndCreateMapMarkers();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

//        deleteDocumentByID("comments", "");

    }

//    pop up method to show hamburger
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_popup);
        popup.show();
    }

    // lots of copy-pasta comments that aren't true anymore... you're not adding to Australia here.
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
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        CommentWindowAdapter windowAdapter = new CommentWindowAdapter(getApplicationContext());
        mMap.setInfoWindowAdapter(windowAdapter);
        mMap.setOnInfoWindowLongClickListener(this);
        mMap.setOnMapClickListener(this::onMapClick);

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

                        AsyncTask.execute(() -> {
                            //call geocode to get formatted address
                            Log.i("ljw", "calling geocode api...");
                            getUsersFormattedAddress();

                            //update map on main thread
                            Handler handler = new Handler(Looper.getMainLooper()) {
                                @Override
                                public void handleMessage(Message input) {
                                    Log.i("ljw", "lat/lng for user is " + userLat + "/" + userLng);

                                    //add a marker to display the user's location:
                                    mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(userLat, userLng))
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

    public void addCommentToDB(View v) {
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

                        //set the comment's id to whatever id it was given by firestore
                        comment.setId(documentReference.getId());

                        //add the new comment to the map now that it's in the db
                        // This code really should have been abstracted into a method, rather than copy pasted three times in this one activity.
                        // You could have even made it an instance method for a comment... but instead you have copy pasta.
                        Marker m = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(userLat, userLng))
                                .anchor(0, 1)
                                .icon(commentIcon)
                                .title(commentTitle)
                                .snippet(commentBody));
                        m.setTag(comment);
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
                            Comment c = Objects.requireNonNull(document.toObject(Comment.class));
                            c.setId(document.getId());

                            Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(c.getLat(), c.getLng()))
                                .anchor(0, 1)
                                .icon(commentIcon)
                                .title(c.getTitle())
                                .snippet(c.getText()));

                            marker.setTag(c);

                            Log.i("ljw", "found comment \"" + c.getTitle() + "/" + c.getText() + "\" with id " + c.getId());
                        }
                    } else {
                        Log.i("ljw", "Error getting documents.", task.getException());
                    }
                }
            });
    }

    public void getUsersFormattedAddress() {
        try {
            // Why are you making web requests for this, rather than using the built in geocoding functionality on Android?
            // See https://developer.android.com/reference/android/location/Geocoder
            URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + userLat + "," + userLng + "&key=AIzaSyCPR3lW_wkbjfNPei-UIbbhWWksjwWpy7c");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            Log.i("ljw", "called api, reading response...");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
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

            String postingFromString = "Posting from " + userCurrentAddress;
            Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message input) {
                    userLocationTV.setText(postingFromString);
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
    }

    public void onMapClick(LatLng arg0) {
        addReplyForm.setVisibility(View.INVISIBLE);
        addCommentForm.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        Log.i("ljw", marker.getId() + " long pressed");
        // so that you can't reply to your user pin:
        if (marker.getId().equals("m0")) return;

        addReplyForm.setVisibility(View.VISIBLE);
        Comment c = (Comment) marker.getTag();
        if (c != null) {
            currentSelectedComment = c;
            if (c.getId() == null) Log.i("ljw", "id is null");
            currentSelectedCommentId = c.getId();
        }
        currentSelectedMarker = marker;
    }

    public void addReplyToComment(View v) {
        Log.i("ljw", "reply button clicked");
        EditText replyEditText = findViewById(R.id.replyEditText);
        Reply reply = new Reply("user", replyEditText.getText().toString(), new Date().getTime());

        if (currentSelectedCommentId == null) {
            Log.i("ljw", "comment has a null id so a DB query won't work");
            addReplyForm.setVisibility(View.INVISIBLE);
            return;
        }

        //get comment by id from firestore
        dbInstance.collection("comments")
            .document(currentSelectedCommentId)
            .get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Log.i("ljw", "query successful");

                    Comment c = Objects.requireNonNull(task.getResult()).toObject(Comment.class);
                    //these are to prevent NPEs on old comments that didn't have IDs or instantiated LLs:
                    if (c == null) return;
                    if (c.replies == null) c.replies = new LinkedList<>();
                    Log.i("ljw", "comment currently has " + c.replies.size() + " replies already:");
                    Log.i("ljw", c.replies.toString());
                    c.replies.add(reply);

                    //update comment in firestore
                    dbInstance.collection("comments")
                        .document(currentSelectedCommentId)
                        .set(c)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i("ljw", "successfully updated comment with new reply");
                                addReplyForm.setVisibility(View.INVISIBLE);
                                replyEditText.setText("");

                                //refresh marker
                                currentSelectedMarker.remove();

                                Marker marker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(c.getLat(), c.getLng()))
                                        .anchor(0, 1)
                                        .icon(commentIcon)
                                        .title(c.getTitle())
                                        .snippet(c.getText()));
                                marker.setTag(c);
                                marker.showInfoWindow();
                                currentSelectedMarker = marker;
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("ljw", "failed updating comment with new reply:\n", e);
                            }
                        });
                }
            });
    }

    // Lots of unused code down here; it would be better to remove this code before submitting.
    public void deleteDocumentByID(String collection, String id) {
        dbInstance.collection(collection).document(id)
            .delete()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i("ljw", "successfully deleted " + id + " from " + collection);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("ljw", "Error deleting document", e);
                }
            });
    }

    public void addTestCommentAtLatLng(Double lat, Double lng) {
        Comment comment = new Comment("test", "bleh", lat, lng, new Date().getTime());
        Log.i("ljw", "new comment created: " + comment.toString());
        dbInstance.collection("comments")
                .add(comment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(userLat, userLng))
                                .anchor(0, 1)
                                .icon(commentIcon)
                                .title(comment.getTitle())
                                .snippet(comment.getText()));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("vik", "Error adding document", e);
                    }
                });
    }
}
