package com.example.adam.mapquest;

import android.graphics.Canvas;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.GeoPoint;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Timer;
import java.util.TimerTask;

public class HUD extends FragmentActivity {

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private EditText text;//our textbox for title
    private Marker current;//current marker for textbox
    private boolean drop_pin;
    private boolean setList;
    private Timer timer = new Timer();
    //delay time in ms
    private final long DELAY = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setList = true;
        setContentView(R.layout.activity_hud);
        setUpMapIfNeeded();
        drop_pin = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        // Enabling MyLocation in Google Map
        //getMap().setMyLocationEnabled(true);

        // BY THIS YOU CAN CHANGE MAP TYPE
        // mGoogleMap.setMapType(mGoogleMap.MAP_TYPE_SATELLITE);

        try {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                Toast.makeText(getApplicationContext(), "No Network or GPS", Toast.LENGTH_SHORT).show();
            } else {
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    Toast.makeText(getApplicationContext(), "Network Enabled", Toast.LENGTH_SHORT).show();
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        Criteria criteria = new Criteria();
                        Location Currentloc = locationManager.getLastKnownLocation(
                                locationManager.getBestProvider(criteria, true));
                        if (Currentloc != null) {
                            double latitude = Currentloc.getLatitude();
                            double longitude = Currentloc.getLongitude();
                            MarkerOptions temp = new MarkerOptions().
                                    position(new LatLng(latitude, longitude)).title("Marker");
                            mMap.addMarker(temp);
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    if (text == null) {
                                        text = (EditText) findViewById(R.id.marker_title);
                                    }
                                    text.setVisibility(View.VISIBLE);
                                    text.setText(marker.getTitle());
                                    current = marker;
                                    text.addTextChangedListener(new TextWatcher() {
                                        public void afterTextChanged(Editable s) {
                                            if (timer != null) {
                                                timer.cancel();
                                            }
                                            final Editable l = s;
                                            timer = new Timer();
                                            timer.schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (current != null) {
                                                                if (current.getTitle() != l.toString()) {
                                                                    current.setTitle(l.toString());
                                                                    text.setVisibility(View.INVISIBLE);
                                                                    current = null;
                                                                }
                                                            }
                                                        }
                                                    }
                                                    );
                                                }
                                            }, DELAY);
                                        }

                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                        }

                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            if (timer != null) {
                                                timer.cancel();
                                            }
                                        }
                                    });
                                    text.requestFocus();
                                    return true;
                                }
                            });
                        }
                    }
                } else if (isGPSEnabled) {
                    Toast.makeText(getApplicationContext(), "GPS Enabled", Toast.LENGTH_SHORT).show();
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        Location Currentloc = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (Currentloc != null) {
                            double latitude = Currentloc.getLatitude();
                            double longitude = Currentloc.getLongitude();
                            MarkerOptions temp = new MarkerOptions().
                                    position(new LatLng(latitude, longitude)).title("Marker");
                            mMap.addMarker(temp);
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    if (text == null) {
                                        text = (EditText) findViewById(R.id.marker_title);
                                    }
                                    text.setVisibility(View.VISIBLE);
                                    text.setText(marker.getTitle());
                                    current = marker;
                                    text.addTextChangedListener(new TextWatcher() {
                                        public void afterTextChanged(Editable s) {
                                            if (timer != null) {
                                                timer.cancel();
                                            }
                                            final Editable l = s;
                                            timer = new Timer();
                                            timer.schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (current != null) {
                                                                if (current.getTitle() != l.toString()) {
                                                                    current.setTitle(l.toString());
                                                                    text.setVisibility(View.INVISIBLE);
                                                                    current = null;
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                            }, DELAY);
                                        }

                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                        }

                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            if (timer != null) {
                                                timer.cancel();
                                            }
                                        }
                                    });
                                    text.requestFocus();
                                    return true;
                                }
                            });
                        }
                    }
                }
            }
        /*
        MyLocation location = new MyLocation();
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
            @Override
            public void gotLocation(Location location){
                //we got the location
            }
        };
        location.getLocation(this,locationResult);


        mMap.addMarker(new MarkerOptions().postion(new LatLng(lat,lng)).title("Marker"));
        //position(new LatLng(location.getLatitude(), location.getLongitude())).title("Marker"));
        */
        } catch (Exception e) {
            //need to add error handling here
        }
    }

    public void SetPins(View v) {
        if (drop_pin == true) {//if here we know they desire to drop pins
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    MarkerOptions temp = new MarkerOptions().
                            position(new LatLng(latLng.latitude, latLng.longitude)).title("Marker");
                    mMap.addMarker(temp);
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            if (text == null) {
                                text = (EditText) findViewById(R.id.marker_title);
                            }
                            text.setVisibility(View.VISIBLE);
                            text.setText(marker.getTitle());
                            current = marker;
                            text.addTextChangedListener(new TextWatcher() {
                                public void afterTextChanged(Editable s) {
                                    if (timer != null) {
                                        timer.cancel();
                                    }
                                    final Editable l = s;
                                    timer = new Timer();
                                    timer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (current != null) {
                                                        if (current.getTitle() != l.toString()) {
                                                            current.setTitle(l.toString());
                                                            text.setVisibility(View.INVISIBLE);
                                                            current = null;
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }, DELAY);
                                }

                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    if (timer != null) {
                                        timer.cancel();
                                    }
                                }
                            });
                            text.requestFocus();
                            return true;
                        }
                    });
                }
            });
            drop_pin = false;
        } else {//otherwise we remove that functionality
            mMap.setOnMapClickListener(null);
            drop_pin = true;
        }
    }
}

