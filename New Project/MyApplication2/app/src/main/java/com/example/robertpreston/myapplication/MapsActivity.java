package com.example.robertpreston.myapplication;

import android.graphics.Canvas;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

//adding gson
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

public class MapsActivity extends FragmentActivity{  // implements View.OnClickListener{

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private EditText text;//our textbox for title
    private Marker current;//current marker for textbox
    private boolean drop_pin;
    private boolean setList;
    private List<Mark> markers;
    private Timer timer = new Timer();
    //delay time in ms
    private final long DELAY = 4000;

    private static String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        setList = true;
        setUpMapIfNeeded();
        drop_pin = true;
        markers = new ArrayList<Mark>();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded();
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
        //mMap.setMyLocationEnabled(true);
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
                            Marker fin = mMap.addMarker(temp);
                            markers.add(new Mark(fin.getTitle(),latitude,longitude));
                            name = fin.getTitle();
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    if (text == null) {
                                        text = (EditText) findViewById(R.id.marker_title);
                                    }
                                    text.setVisibility(View.VISIBLE);
                                    text.setText(marker.getTitle());
                                    current = marker;
                                    LatLng temp = marker.getPosition();
                                    double lat = temp.latitude;
                                    double lon = temp.longitude;
                                    int indx = 0;
                                    for(int i=0; i<markers.size();i++)
                                    {
                                        if(markers.get(i).getLat()==lat && markers.get(i).getLong()==lon){
                                            indx = i;
                                            break;
                                        }
                                    }
                                    //I know this is ugly, this is my workaround for final keyword
                                    final int target_indx = indx;
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
                                                                                  markers.get(target_indx).setName(l.toString());
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
                            Marker fin = mMap.addMarker(temp);
                            markers.add(new Mark(fin.getTitle(),latitude,longitude));
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    if (text == null) {
                                        text = (EditText) findViewById(R.id.marker_title);
                                    }
                                    text.setVisibility(View.VISIBLE);
                                    text.setText(marker.getTitle());
                                    current = marker;
                                    LatLng temp = marker.getPosition();
                                    double lat = temp.latitude;
                                    double lon = temp.longitude;
                                    int indx = 0;
                                    for(int i=0; i<markers.size();i++)
                                    {
                                        if(markers.get(i).getLat()==lat && markers.get(i).getLong()==lon){
                                            indx = i;
                                            break;
                                        }
                                    }
                                    //I know this is ugly, this is my workaround for final keyword
                                    final int target_indx = indx;
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
                                                                    markers.get(target_indx).setName(l.toString());
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

    public void SetPins(View view) {
        if (drop_pin == true) {//if here we know they desire to drop pins
//            Toast.makeText(getApplicationContext(), "Two", Toast.LENGTH_SHORT).show();
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(final LatLng latLng) {
                    MarkerOptions temp = new MarkerOptions().
                            position(new LatLng(latLng.latitude, latLng.longitude)).title("Marker");
                    Marker fin = mMap.addMarker(temp);
                    markers.add(new Mark(fin.getTitle(),latLng.latitude,latLng.longitude));
                    name = fin.getTitle();
//                    Toast.makeText(getApplicationContext(), "Three", Toast.LENGTH_SHORT).show();
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            if (text == null) {
                                text = (EditText) findViewById(R.id.marker_title);
                            }
                            text.setVisibility(View.VISIBLE);
                            text.setText(marker.getTitle());
                            current = marker;
                            //need to find the corresponding data in markers list
                            LatLng temp = marker.getPosition();
                            double lat = temp.latitude;
                            double lon = temp.longitude;
                            int indx = 0;
                            for(int i=0; i<markers.size();i++)
                            {
                                if(markers.get(i).getLat()==lat && markers.get(i).getLong()==lon){
                                    indx = i;
                                    break;
                                }
                            }
                            //I know this is ugly, this is my workaround for final keyword
                            final int target_indx = indx;
//                            Toast.makeText(getApplicationContext(), "Four", Toast.LENGTH_SHORT).show();
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
                                                            markers.get(target_indx).setName(l.toString());
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

    //button to send map to server
    public void SaveMap(View v){
        //Type listOfMarkers = new TypeToken<List<Marker>>(){}.getType();
        /*for(int i =0; i<markers.size();i++){
            Toast.makeText(getApplication().getApplicationContext(),markers.a[i].ToString,Toast.LENGTH_SHORT).show();
        }*/
        Gson gson = new Gson();
        String data = gson.toJson(markers);
//        JSONArray data = gson.toJson(markers);

        if(markers.size()==0)
            Toast.makeText(getApplication().getApplicationContext(),"Empty",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplication().getApplicationContext(), data,Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplication().getApplicationContext(),data, Toast.LENGTH_SHORT).show();

    }

    public static String makeRequest(String uri, String json) {
        HttpURLConnection urlConnection;
        String url;
        String data = json;
        String result = null;
        try {
            //Connect
            urlConnection = (HttpURLConnection) ((new URL(uri).openConnection()));
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

            //Write
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(data);
            writer.close();
            outputStream.close();

            //Read
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

            String line = null;
            StringBuilder sb = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            bufferedReader.close();
            result = sb.toString();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    /*
    private void sendPostRequest() {

        //need to get url from chris
        URL url = new URL("http://www.geturl.com");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try{
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            readStream(in);
            finally{
                urlConnection.disconnect();
            }
        }

        /*
        class FetchTask extends AsyncTask<Void, Void, JSONArray> {
            @Override
            protected JSONArray doInBackground(Void... params) {
                try {
                    HttpConnection httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://www.yoursite.com/script.php");

                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("id", "12345"));
                    nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    sb.append(reader.readLine() + "\n");
                    String line = "0";
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    reader.close();
                    String result11 = sb.toString();

                    // parsing data
                    return new JSONArray(result11);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONArray result) {
                if (result != null) {
                    // do something
                } else {
                    // error occured
                }
            }

        }
    }*/

    //class to store info about markes in list sad that I needed a class
    //but json and markers didn't get along well
    public class Mark implements Parcelable {
        private String name;
        private double lat;
        private double lon;
        public Mark(){
            name = "default";
            lat = 0;
            lon = 0;

        }
        public Mark(String nme, double latitude, double longitude){
            name = nme;
            lat = latitude;
            lon = longitude;
        }
        public void setName(String nme){ name = nme;}
        public String getName(){return name;}
        public Double getLat(){return lat;}
        public Double getLong(){return lon;}
        @Override
        public int describeContents(){
            return 0;
        }
        @Override
        public void writeToParcel(Parcel parcel, int flag){
            parcel.writeString(name);
            parcel.writeDouble(lat);
            parcel.writeDouble(lon);

        }
        public void readFromParcel(Parcel in){
            name = in.readString();
            lat = in.readDouble();
            lon = in.readDouble();
        }
    }
}