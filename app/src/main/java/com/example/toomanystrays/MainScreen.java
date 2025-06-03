package com.example.toomanystrays;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.toomanystrays.activities.CreatePinActivity;
import com.example.toomanystrays.activities.PinDetailsActivity;
import com.example.toomanystrays.activities.SignIn_Activity;
import com.example.toomanystrays.activities.MyPinsListActivity;
import com.example.toomanystrays.models.Pin;
import com.example.toomanystrays.repository.CategoryRepository;
import com.example.toomanystrays.repository.CommentRepository;
import com.example.toomanystrays.repository.PinRepository;
import com.example.toomanystrays.repository.StrayRepository;
import com.example.toomanystrays.repository.UserRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


/** @noinspection ALL*/
public class MainScreen extends AppCompatActivity implements OnMapReadyCallback, LocationListener
        , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    //Google API
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private GoogleSignInAccount acct;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Marker mCurrLocationMarker, lastSelectedMarker;
    private HashMap<String, Pin> markersAndPins;

    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient client;
    private TextView textUsername;
    private LinearLayout layoutProfile;
    private double gotoLatitude, gotoLongitude, last_latitude, last_longitude;
    private int mapLayerOpt = 0;
    private boolean initial, isGotoLocation, flag = false, addingMarker = true;
    private ArrayList<Pin> allPinsObj;
    private Pin pinAt;
    public FloatingActionButton buttonSignout, buttonCreate_Pin, buttonCancel_Pin
            , buttonListPin, buttonChangeLayer, buttonViewPinDetails;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("debug", "On Create");
        setContentView(R.layout.activity_main_screen);

        //Get Intent
        Intent getIntent = getIntent();
        gotoLatitude = getIntent().getDoubleExtra("pin_latitude", 0);
        gotoLongitude = getIntent().getDoubleExtra("pin_longitude", 0);
        isGotoLocation = getIntent().getBooleanExtra("Go_to_Location_Pin", false);

        //Initial TextView
        textUsername = findViewById(R.id.textUsername);

        //Initial LayoutProfile
        layoutProfile = findViewById(R.id.layoutProfile);

        //Initial FloatingActionButton
        buttonSignout = findViewById(R.id.buttonSignOut);
        buttonChangeLayer = findViewById(R.id.buttonChangeLayer);
        buttonCreate_Pin = findViewById(R.id.buttonCreate_Pin);
        buttonCancel_Pin = findViewById(R.id.buttonCancel_Pin);
        buttonListPin = findViewById(R.id.buttonListPin);
        buttonViewPinDetails = findViewById(R.id.buttonViewPinDetails);

        //Initial Google Get User
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);
        acct = GoogleSignIn.getLastSignedInAccount(this);

        //Initial DATA
        initial = true;
        allPinsObj = new ArrayList<>();
        //set Image Profile
        setImageProfile();
        //set Text Username
        textUsername.setText(UserRepository.getUser().getUsername());
        //set Pins
        allPinsObj = PinRepository.getPinAll();

        //Markers on Map
        markersAndPins = new HashMap<String, Pin>();

        //Initial Google Map
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_View);
        mapFragment.getMapAsync(this);

        //#### SetOnClickListener ####
        //Go to ProfileActivity
        layoutProfile.setOnClickListener(v -> {
            Log.d("debug", "Profile Cilcked");
        });

        buttonSignout.setOnClickListener(v -> signOut());

        //Create a new Pin
        buttonCreate_Pin.setOnClickListener(v -> {
          last_latitude = lastSelectedMarker.getPosition().latitude;
          last_longitude = lastSelectedMarker.getPosition().longitude;
          FetchData(1);
        });

        buttonCancel_Pin.setOnClickListener(v -> {
            addingMarker = true;
            lastSelectedMarker = null;

            //display All Pins
            displayAllPins(markersAndPins);

            //hide & show button
            buttonCreate_Pin.hide();
            buttonCancel_Pin.hide();
            buttonListPin.show();
            buttonSignout.show();
        });

        //Change Layer Map
        buttonChangeLayer.setOnClickListener(v -> {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    switch (mapLayerOpt) {
                        case 0 :
                            mapLayerOpt = 1;
                            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            break;
                        case 1 :
                            mapLayerOpt = 2;
                            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                            break;
                        case 2 :
                            mapLayerOpt = 0;
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            break;
                    }
                }
            });
        });

        //Go to ListPinActivity
        buttonListPin.setOnClickListener(v -> {
            FetchData(2);
        });

        //View Selected Pin Details
        buttonViewPinDetails.setOnClickListener(v -> {
            FetchData(4);
        });

        buttonCreate_Pin.show();
        buttonCancel_Pin.show();
        buttonListPin.show();
        buttonSignout.show();
        buttonViewPinDetails.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FetchData(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("debug", "On Start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debug", "On Resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("debug", "On Pause");
        if (!initial) {
            FetchData(5);
        } else initial = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("debug", "On Stop");
    }

    private void FetchData(int c) {
        Log.d("debug", "MainScreen : Fetch Date Case : "+c+" is Started");
        Thread waitingThread = new Thread(() -> {
            waitForInitialize(c);
            switch (c) {
                case 0:
                    finish();
                    break;

                case 1:
                    Intent intentCase1 = new Intent(MainScreen.this, CreatePinActivity.class);
                    intentCase1.putExtra("last_latitude", last_latitude);
                    intentCase1.putExtra("last_longitude", last_longitude);
                    lastSelectedMarker = null;
                    finish();
                    startActivity(intentCase1);
                    break;

                case 2:
                    Intent intentCase2 = new Intent(MainScreen.this, MyPinsListActivity.class);
                    finish();
                    startActivity(intentCase2);
                    break;

                case 4:
                    Intent intentCase4 = new Intent(MainScreen.this, PinDetailsActivity.class);
                    Log.d("debug", "putExtra pin_id : "+ pinAt.getId());
                    intentCase4.putExtra("pinID", pinAt.getId());
                    intentCase4.putExtra("access_from", "MainScreen");
                    finish();
                    startActivity(intentCase4);
                    break;

                default:
                    break;
            }
        });

        Thread notifyingThread = new Thread(() -> {
            // Perform some tasks before notifying
            InitializeDATA(c);
        });

        waitingThread.start();
        notifyingThread.start();
    }

    public synchronized void waitForInitialize(int c) {
        while (!flag) {
            try {
                wait(); // Thread waits until notified
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Method execution continues after the flag is set
        //set Image Profile
        setImageProfile();
        //set Text Username
        textUsername.setText(UserRepository.getUser().getUsername());
        //set Pins
        allPinsObj = PinRepository.getPinAll();
        flag = false;
        Log.d("debug", "MainScreen : Done Wait Thread");
        Log.d("debug", "MainScreen : Fetch Date Case : "+c+" is Finished");
    }

    public synchronized void InitializeDATA(int c) {
        // Perform some tasks before notifying
        //Initialize & Fetch Data
        switch (c) {
            case 5:
            case 0:
            case 1:
                try {
                    UserRepository.FETCH_DATA_USER(acct);
                    CategoryRepository.GET_CATEGORY();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            case 2:
                try {
                    PinRepository.GET_USER_PINS(UserRepository.getUser().getId());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            case 3:
                try {
                    PinRepository.GET_ALL_PINS();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //Clear Marker
                if (!markersAndPins.isEmpty()) {
                    markersAndPins.clear();
                }
                if (!allPinsObj.isEmpty()) {
                    allPinsObj.clear();
                }
                break;

            case 4:
                try {
                    CategoryRepository.GET_CATEGORY();
                    PinRepository.GET_ALL_PINS();
                    StrayRepository.GET_STRAY_BY_PIN(pinAt.getId());
                    CommentRepository.GET_COMMENT_BY_PIN(pinAt.getId());
                    Thread.sleep(1500);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
        try {
            Log.d("debug", "MainScreen : Sleep Thread 1 sec");
            Thread.sleep(1000);
            Log.d("debug", "MainScreen : Thread wake up");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        flag = true;
        notify(); // Notifies waiting thread(s)
        Log.d("debug", "MainScreen : Done Notify Thread");
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        //Hide button
        buttonCreate_Pin.hide();
        buttonCancel_Pin.hide();
        buttonViewPinDetails.hide();

        //show All Pins
        displayAllPins(markersAndPins);

        //Clicking on Marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                if (lastSelectedMarker == null) {
                    pinAt = markersAndPins.get(marker.getId());
                    Log.d("debug marker", "Marker at : "+pinAt.getPin_name()+" | ID : "+pinAt.getId());
                    //show button to see Pin Details
                    buttonViewPinDetails.show();
                } else {
                    buttonCreate_Pin.show();
                    buttonCancel_Pin.show();
                    buttonViewPinDetails.hide();
                    Log.d("debug marker", "Marker at : "+marker.getTitle());
                }
                return false;
            }
        });

        //Adding maker on click
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                //check if adding Marker
                //check visibily button
                if (addingMarker) {
                    addingMarker = false;
                    mMap.clear();
                    lastSelectedMarker = mMap.addMarker(new MarkerOptions().position(point).title("Selected Point"));

                    //Fetch All Pins
                    FetchData(3);

                    //hide & show button
                    buttonCreate_Pin.show();
                    buttonCancel_Pin.show();
                    buttonListPin.hide();
                    buttonSignout.hide();
                    buttonViewPinDetails.hide();
                } else {
                    addingMarker = true;
                    lastSelectedMarker = null;

                    displayAllPins(markersAndPins);

                    //hide & show button
                    buttonCreate_Pin.hide();
                    buttonCancel_Pin.hide();
                    buttonViewPinDetails.hide();
                    buttonListPin.show();
                    buttonSignout.show();
                }
            }
        });
        initialPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
                startActivity(getIntent());
            }
        }
    }

    private void initialPermission() {
        //init location
        if (ActivityCompat.checkSelfPermission(MainScreen.this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        } else {
            //if permission denied
            ActivityCompat.requestPermissions(MainScreen.this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    private void setImageProfile() {
        ShapeableImageView profile = findViewById(R.id.imageProfile);
        String imageUrl = String.valueOf(acct.getPhotoUrl());
        Thread setImage = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    try {
                        URL url = new URL(imageUrl);
                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        profile.setImageBitmap(bmp);
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        setImage.start();
    }

    private void signOut() {
        gsc.signOut().addOnCompleteListener(task -> {
            finish();
            Intent intent = new Intent(MainScreen.this, SignIn_Activity.class);
            startActivity(intent);
        });
    }

    private void displayAllPins(HashMap markersAndPins) {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap.clear();
                if (!allPinsObj.isEmpty() && markersAndPins.isEmpty()) {
                    for (int i = 0; i < allPinsObj.size(); i++) {
                        //Get Pin Data
                        String pin_name = allPinsObj.get(i).getPin_name();
                        double latitude = allPinsObj.get(i).getLatitude();
                        double longitude = allPinsObj.get(i).getLongitude();
                        LatLng latLng = new LatLng(latitude, longitude);
                        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(pin_name)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                        markersAndPins.put(marker.getId(), allPinsObj.get(i));
                    }
                }
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        mLastLocation = location;
        //move map camera
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        if (isGotoLocation) {
            //Place Pin location marker
            latLng = new LatLng(gotoLatitude, gotoLongitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
