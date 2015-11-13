package com.example.robertpreston.myapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Intent;


import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private final static String SERVER_URL = "http://52.89.13.174:8080/mapquest-server/json";

    private static String maps;

    private static AsyncTask<String,String,String> recieve;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        if (position != 6) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                    .commit();
        }
        //start map stuff when its selected have to use
        if (position == 6) {
            Intent intent = new Intent(this, MapsActivity.class);
            //intent.setComponent(new ComponentName("com.example.robertpreston.myapplication", "com.example.robertpreston.myapplication.MapsActivity"));
            startActivity(intent);
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
            case 6:
                mTitle = getString(R.string.title_section6);
                break;
            case 7:
                mTitle = getString(R.string.title_section7);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "This will open a settings window", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static int fragNumber;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            fragNumber = sectionNumber;
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            switch(fragNumber)
            {
                case 1: //Map
                    rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    break;
                case 2: //Profile
                    rootView = inflater.inflate(R.layout.profile_activity, container, false);
                    break;
                case 3: //Friends
                    rootView = inflater.inflate(R.layout.friendslist_activity, container, false);
                    break;
                case 4: //MeetUps this will become the get maps fragment
                    rootView = inflater.inflate(R.layout.fragment_getmap, container, false);
                    populateListView(rootView.getContext(), (ListView) rootView.findViewById(R.id.mapListView));
                    break;
                case 5: //Invites
                    rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    break;
                case 6: //Create Event
                    rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    break;
                case 7: //App Info
                    rootView = inflater.inflate(R.layout.map_activity, container, false);
                    break;
            }
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    public static void populateListView(final Context context, ListView item){
        //get server data here
        String prev;
        getServerdata(SERVER_URL);
        if(maps == null)
        {
            prev = null;
        }
        else
        {
            prev = maps;
        }
        try {
            //busy wait for async task should research suggested method for this
            while( maps == prev)
            {

            }
            JSONObject map = new JSONObject(maps);
            final ArrayList<String> mapnames = new ArrayList<String>();
            mapnames.add((String) map.get("MapName"));
            final ArrayList<Mark> markers = new ArrayList<Mark>();
            JSONArray jsonarr = (JSONArray) map.get("arrayname");
            for(int i =0; i<jsonarr.length();i++){
                JSONObject tempmark = (JSONObject) jsonarr.get(i);
                Mark temp = new Mark(tempmark.getString("name"),tempmark.getDouble("lat"),tempmark.getDouble("lon"));
                markers.add(temp);
            }
            ArrayAdapter<String> mapAdapter = new ArrayAdapter<String>(context,
                                                    android.R.layout.simple_list_item_1, mapnames);
            item.setAdapter(mapAdapter);
            item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {

                                                String selectedmap = (String) adapter.getItemAtPosition(position);
                                                //need to change this to get marker array based on position
                                                Intent intent = new Intent(context, MapsActivity.class);
                                                intent.putExtra("Markers",markers);
                                                intent.putExtra("MapName",selectedmap);
                                                context.startActivity(intent);
                                                Toast.makeText(context, selectedmap, Toast.LENGTH_LONG).show();
                                            }
                                        }
                );
        } catch(JSONException json){
            Log.d("Json", "invalid jsonobject constructor ");
        }
    }
    public static void getServerdata(String uri) {
            final String address = uri;
            //String maps;
            recieve = new AsyncTask<String, String, String>() {
                @Override
                protected void onPreExecute()
                {
                    Log.d("ANDRO_ASYNC", "onPreExecute()");
                }

                @Override
                protected String doInBackground(String... params) {
                    HttpURLConnection urlConnection;
                    String result = null;
                    try {
                        URL url = new URL(address);

                        //Connect
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("Content-Type", "application/json");
                        urlConnection.setRequestProperty("Accept", "application/json");
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();
                        InputStreamReader isr = new InputStreamReader(urlConnection.getInputStream());
                        BufferedReader reader = new BufferedReader(isr);
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null)
                        {
                            sb.append(line + "\n");
                        }
                        result = sb.toString();
                        // You can perform UI operations here
                        //Toast.makeText(this,response, 0).show();
                        isr.close();
                        reader.close();

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    maps = result;
                    return result;
                }
            }.execute();
    }

    /* adapter for list view with custom class for adapter helpful example written by
    // yours truly
    private class MyListAdapter extends ArrayAdapter<String> {
        public MyListAdapter(String names) {
            super(MainActivity.this, R.layout.fragment_getmap, names);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //make sure we have a view to work with
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.da_nums, parent, false);
            }
            // Find the state to work with confused on finals use may only allow one state value
            final String nme = numbers.get(position);
            //Fill the view
            TextView name = (TextView) itemView.findViewById(R.id.item_number);
            name.setText(nme);
            Button edit = (Button) itemView.findViewById(R.id.item_Delete_num);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    numbers.remove(position);
                    if(indx==0)
                        curstate.setEmergency(numbers);
                    else if(indx==1)
                        curstate.setBlock(numbers);
                    else
                        curstate.setAllow(numbers);
                    populateListView();
                }
            });
            return itemView;
        }
    }*/
}
