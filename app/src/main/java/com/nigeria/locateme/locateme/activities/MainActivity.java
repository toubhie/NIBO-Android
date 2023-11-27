package com.nigeria.locateme.locateme.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nigeria.locateme.locateme.R;
import com.nigeria.locateme.locateme.entities.AppRater;
import com.nigeria.locateme.locateme.entities.Contact;
import com.nigeria.locateme.locateme.entities.NiboHouse;
import com.nigeria.locateme.locateme.entities.NiboUser;
import com.nigeria.locateme.locateme.entities.Preferences;
import com.nigeria.locateme.locateme.entities.UserLocation;
import com.nigeria.locateme.locateme.utils.Constants;
import com.nigeria.locateme.locateme.utils.MessageUtil;
import com.nigeria.locateme.locateme.utils.SQLController;
import com.nigeria.locateme.locateme.webservices.WebService;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{


    private GoogleMap mMap;
    public String latitude;         //latitude to be sent
    public String longitude;        //longitude to be sent
    private String actionOptions[];
    MapView mMapView;
    private MarkerOptions mMarker;
    private GoogleApiClient mGoogleApiClient;
    private ClipboardManager myClipboard;
    private ClipData myClip;
    private Switch mySwitch;
    private boolean mapType = true;


    String newNiboCode;


    private SQLController sq = null;

    private Preferences pref;

    private TextView userNameTxt,userEmailTxt,userAddressTxt;
    private Button shareButton,shareButton1,searchButton,buttonSwitch;

    private NiboUser niboUser,getUserDetails;

    LocationManager lm;
    LatLng myLocation,IntLocation,pickedLocation;

    ProgressDialog dialog;

    boolean shareASavedLocation = false;
    boolean intPinActivity,canTouchMap,intFindFirendPin;
    String getIntLat,getIntLong,getIntName;
    String getIntPersonName,getIntPersonPhoneNumber,getIntPersonLastCheckin,getIntPersonTimeCheckin
            ,getIntPersonLat,getIntPersonLong,stringLocation,variousLocations;

    Tracker t;

    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpViews();
        setUpListeners();

        //t = ((MyAnalytics)this.getApplication()).getTracker(MyAnalytics.TrackerName.APP_TRACKER);
        MyAnalytics application = (MyAnalytics) getApplication();
        t = application.getDefaultTracker();
        t.setScreenName("Home Activity");
        t.send(new HitBuilders.AppViewBuilder().build());

        sq = new SQLController(this);

        pref = new Preferences(this);

        myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

        AppRater.app_launched(this);

        // Toast.makeText(this,"Got intent ",Toast.LENGTH_LONG).show();

        //canTouchMap = false;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //getUserData();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void setUpViews(){
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setVoiceSearch(true);
        searchView.setVoiceIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_mic_white, null));
        searchView.setCursorDrawable(R.drawable.color_cursor_white);

        buttonSwitch =  (Button) findViewById(R.id.buttonSwitch);
        searchButton = (Button) findViewById(R.id.searchButton);

        lm = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);

        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Getting your current location. Please wait...");
        dialog.setCancelable(true);

        shareButton = (Button) findViewById(R.id.shareButton);
        shareButton1 = (Button) findViewById(R.id.shareButton1);
    }

    private void setUpListeners(){
        buttonSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //mapType = mySwitch.isChecked();


                if(mapType){
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    mapType = false;
                }else{
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    mapType = true;
                }

            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSearchNiboCodeActivity();
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("Clicks")  // category i.e. Buttons Action
                        .setAction("Button")    // action i.e.  Button Name
                        .setLabel("Search Nibo Code")    // label i.e.  any meta-data
                        .build());
            }
        });

        shareButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(pickedLocation != null){
                    shareSavedLocation();
                }else if(shareASavedLocation){
                    shareSavedLocation();
                }else{
                    MessageUtil.showShortToast(MainActivity.this,"Please select a location on the map");
                }

            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //shareMyLocation();
                if(latitude == null){
                    MessageUtil.showShortToast(MainActivity.this,"Please wait while we get your current location");
                }else{
                    showShareLocationDialog();
                }


                t.send(new HitBuilders.EventBuilder()
                        .setCategory("Clicks")  // category i.e. Buttons Action
                        .setAction("Button")    // action i.e.  Button Name
                        .setLabel("clicked")    // label i.e.  any meta-data
                        .build());
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                query = query.toUpperCase();
                processSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                //processSearch(query);
                return true;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(MainActivity.this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(MainActivity.this).reportActivityStop(this);
    }

    private void processSearch(String query){
        try{
            if (TextUtils.isEmpty(query)) {
                Toast.makeText(MainActivity.this, "Please enter a NIBO Code.", Toast.LENGTH_SHORT).show();
            }

            else if (query.length() == 7){
                new SearchTemporaryNiboCodeTask().execute(query);
            }

            else if (query.length() == 8){
                new SearchPermanentNiboCodeTask().execute(query);
            }

            else {
                //Toast.makeText(MainActivity.this, "Please enter a valid NIBO Code.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        }

    public class SearchTemporaryNiboCodeTask extends AsyncTask<String, Integer, NiboUser>{

        ProgressDialog processDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            processDialog = ProgressDialog.show(MainActivity.this, "Searching.", "Searching. Please wait...");
        }

        @Override
        protected NiboUser doInBackground(String... params) {

            String niboCode = params[0];

            NiboUser result = null;

            if (niboCode != null){
                try {
                    WebService webService = new WebService();
                    result = webService.searchTemporaryNiboCode(niboCode);

                    return result;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(NiboUser niboUser) {

            processDialog.dismiss();

            if (niboUser != null) {
                showOptionsDialog(niboUser);

                Log.i(Constants.TAG_NIBO, "Found Location");
            } else{
                MessageUtil.showAlert(MainActivity.this, "NIBO Code not found", "This Code doesn't exist or has timed off. \n " +
                        "Please request for another one.");
            }

        }
    }

    public class SearchPermanentNiboCodeTask extends AsyncTask<String, Integer, NiboHouse>{

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this, "Searching.", "Searching. Please wait...");
        }

        @Override
        protected NiboHouse doInBackground(String... params) {
            String permanentNiboCode = params[0];

            NiboHouse result;

            if (permanentNiboCode != null){
                try {

                    WebService webService = new WebService();
                    result = webService.searchPermanentNiboCode(permanentNiboCode);

                    return result;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(NiboHouse niboHouse) {

            progressDialog.dismiss();

            if (niboHouse != null) {

                showHouseDetailsPreviewDialog(niboHouse);

                Log.i(Constants.TAG_NIBO, "Found House");

            } else{
                //new SearchSpecialCodeTask().execute(phone_number);
                MessageUtil.showAlert(MainActivity.this, "NIBO Code not found", "This NIBO Code doesn't exist. Please try again.");
            }

        }
    }





    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_saved_locations) {
            // Handle the camera action
            Intent savedActInt = new Intent(MainActivity.this, SavedLocationActivity.class);
            startActivity(savedActInt);
            t.send(new HitBuilders.EventBuilder()
                    .setCategory("Clicks")  // category i.e. Buttons Action
                    .setAction("Button")    // action i.e.  Button Name
                    .setLabel("Nav Saved Locations")    // label i.e.  any meta-data
                    .build());

        } else if (id == R.id.nav_search_nibo_code) {

            goToSearchNiboCodeActivity();

            t.send(new HitBuilders.EventBuilder()
                    .setCategory("Clicks")  // category i.e. Buttons Action
                    .setAction("Button")    // action i.e.  Button Name
                    .setLabel("Nav Search Nibo Code")    // label i.e.  any meta-data
                    .build());

        } else if (id == R.id.nav_get_nibo_code) {

            Intent getNiboCodeInt = new Intent(MainActivity.this, GetNiboCodeActivity.class);
            startActivity(getNiboCodeInt);
            //setUpMeeting();

            t.send(new HitBuilders.EventBuilder()
                    .setCategory("Clicks")  // category i.e. Buttons Action
                    .setAction("Button")    // action i.e.  Button Name
                    .setLabel("Nav Get A House Nibo Code")    // label i.e.  any meta-data
                    .build());

        } else if (id == R.id.nav_share) {

            shareMyLocation();

        } else if (id == R.id.nav_checkin) {

            checkIn();

        } else if (id == R.id.nav_save) {

           saveCurrentLocation();

        } else if (id == R.id.nav_settings) {

            //Toast.makeText(MainActivity.this, variousLocations + "" , Toast.LENGTH_LONG ).show();
            Intent openSettingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(openSettingsActivity);

            t.send(new HitBuilders.EventBuilder()
                    .setCategory("Clicks")  // category i.e. Buttons Action
                    .setAction("Button")    // action i.e.  Button Name
                    .setLabel("Nav Settings")    // label i.e.  any meta-data
                    .build());

        } else if (id == R.id.nav_feedback) {
            Intent openQRActivity = new Intent(MainActivity.this, FeedBackActivity.class);
            startActivity(openQRActivity);

            t.send(new HitBuilders.EventBuilder()
                    .setCategory("Clicks")  // category i.e. Buttons Action
                    .setAction("Button")    // action i.e.  Button Name
                    .setLabel("Nav Feedback")    // label i.e.  any meta-data
                    .build());

        } else if (id == R.id.nav_scan_qr) {

            checkCameraPermission();



            //openQrActivity();
        }
        /*else if (id == R.id.nav_website) {

            openWebsite ( "http://niboapp.com/");

        }*/ else if (id == R.id.nav_rate) {

            rateApp();

        } else if (id == R.id.nav_share_app) {

            shareApp();

        } else if (id == R.id.nav_about) {
            aboutApp();
        }else if (id == R.id.linearLayoutGetNiboCode) {

            Intent getNiboCodeInt = new Intent(MainActivity.this, GetNiboCodeActivity.class);
            startActivity(getNiboCodeInt);

            t.send(new HitBuilders.EventBuilder()
                    .setCategory("Clicks")  // category i.e. Buttons Action
                    .setAction("Button")    // action i.e.  Button Name
                    .setLabel("Nav Get Nibo Code")    // label i.e.  any meta-data
                    .build());

        }/* else if (id == R.id.nav_exit) {

            finish();

        }*/



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }





    public void goToSearchNiboCodeActivity(){

        Intent finFriActInt = new Intent(MainActivity.this, FindFriendActivity.class);
        startActivity(finFriActInt);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = createLocationRequest();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        double tempLatitude = location.getLatitude();
        double tempLongitude = location.getLongitude();
        latitude = Double.toString(tempLatitude);
        longitude = Double.toString(tempLongitude);

        myLocation = new LatLng(tempLatitude, tempLongitude);


        new ReverseGeocodingTask(getBaseContext()).execute(myLocation);

        if(intPinActivity == false){
            updateCamera();
        }


            if(dialog.isShowing()){
                try {
                    dialog.dismiss();
                    //dialog = null;
                } catch (Exception e) {
                    //TODO: Fill in exception
                }
            }


        if(intPinActivity == false){
            if (mMarker != null) {
                updateMarker();
            }else{
                addMarker();
            }
        }




    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    private void putPinOnMap(String lat,String lon, String name, String msg) {
        double tempIntLat = Double.parseDouble(lat);
        double tempIntLong = Double.parseDouble(lon);
        IntLocation = new LatLng(tempIntLat, tempIntLong);

        // Add a marker in requested location and move the camera
        mMap.addMarker(new MarkerOptions().position(IntLocation).title(name).snippet(msg)).showInfoWindow();

        CameraPosition position = new CameraPosition.Builder()
                .target(IntLocation) // Sets the new camera position
                .zoom(18) // Sets the zoom
                .bearing(180) // Rotate the camera
                .tilt(30) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder

        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


            mMap = googleMap;

            if (intPinActivity == true) {

                if (intFindFirendPin) {
                    putPinOnMap(getIntPersonLat, getIntPersonLong, getIntName, "As at " + getIntPersonTimeCheckin);
                } else {
                    putPinOnMap(getIntLat, getIntLong, getIntName, "Share this location");
                }

            }

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //Location Permission already granted
                    buildGoogleApiClient();
                } else {
                    //Request Location Permission
                    checkLocationPermission();
                }
            } else {
                buildGoogleApiClient();
            }


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.setTrafficEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    //Toast.makeText(MainActivity.this, "Current Location", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng point) {

                    pickedLocation = point;

                    if (canTouchMap) {
                        if (mMarker != null) {
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(point).title("Meeting location").snippet("Share this location as meeting point")).showInfoWindow();
                        }

                    }

                }
            });
            float zoom = 16;
            //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,zoom));


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();


        /*dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Getting your current location. Please wait...");
        dialog.setCancelable(true);*/

        getUserData();

        CheckEnableGPS();

        checkIntent();
    }

    private void getUserData() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        getUserDetails = pref.getUserDetails();
        String userName = getUserDetails.getFullName();
        String userEmail = getUserDetails.getEmail();
        final String userAddress = getUserDetails.getAddress().toString().toUpperCase();

        //MessageUtil.showShortToast(MainActivity.this, "Name: " +userName+" EMail"+ userEmail);

        userNameTxt = (TextView) header.findViewById(R.id.username);
        userEmailTxt = (TextView) header.findViewById(R.id.useremail);
        userAddressTxt = (TextView) header.findViewById(R.id.useraddress);

        userAddressTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(userAddress.equals("NULL,NULL")){
                    Intent getNiboCodeInt = new Intent(MainActivity.this, GetNiboCodeActivity.class);
                    startActivity(getNiboCodeInt);
                }else{
                    //share the House Nibo Code
                    shareHouseNiboCode(userAddress);
                }
            }
        });

        userNameTxt.setText(userName);
        userEmailTxt.setText(userEmail);
        if(userAddress.equals("NULL,NULL")){
            userAddressTxt.setText("Get a Nibo Code?");
            openGetNiboHouseCodePrompt(MainActivity.this);
        }else{
            userAddressTxt.setText(userAddress);
        }

    }

    private void updateCamera() {
        // mMapView.setCameraDistance(10);
        CameraPosition position = new CameraPosition.Builder()
                .target(myLocation) // Sets the new camera position
                .zoom(18) // Sets the zoom
                .bearing(180) // Rotate the camera
                .tilt(30) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder

        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position));
    }

    private void updateMarker() {
        if(stringLocation == null){
            stringLocation = "I'm here";
        }
        mMap.clear();
        mMarker = new MarkerOptions()
                .position(myLocation)
                .title("Me")
                .snippet(stringLocation);
        mMap.addMarker(mMarker).showInfoWindow();
    }

    private void addMarker() {
        if(stringLocation == null){
            stringLocation = "I'm here";
        }
        mMarker = new MarkerOptions()
                .position(myLocation)
                .title("Me")
                .snippet(stringLocation);
        mMap.addMarker(mMarker).showInfoWindow();
    }

    public void shareMyLocation(){

        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setTitle("Choose Method:");

        Resources res = getResources();
        actionOptions = res.getStringArray(R.array.action_options_method);
        build.setItems(R.array.action_options_method, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                Intent i = new Intent(android.content.Intent.ACTION_SEND);

                i.setType("text/plain");
                //i.putExtra(Intent.EXTRA_SUBJECT, "Hi, my current Location is: " + "\n");


                if(actionOptions[item].equalsIgnoreCase("Share Google Map Link")){

                    if(latitude == null){
                        MessageUtil.showShortToast(MainActivity.this,"Please wait while we get your current location");
                    }else{
                        String location = myLocation.latitude / 1E6 + "," + myLocation.longitude / 1E6;
                        i.putExtra(Intent.EXTRA_TEXT, "Hi, my current Location is: " + "\n" + "http://maps.google.com/maps?q=loc:" + location + "\n" + "Sent from Nibo");

                        try {
                            startActivity(Intent.createChooser(i, "Share Location"));
                            t.send(new HitBuilders.EventBuilder()
                                    .setCategory("Clicks")  // category i.e. Buttons Action
                                    .setAction("Button")    // action i.e.  Button Name
                                    .setLabel("Share Google Map Link")    // label i.e.  any meta-data
                                    .build());
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(MainActivity.this, "Not found any app", Toast.LENGTH_LONG).show();
                        }
                    }



                }
                else if(actionOptions[item].equalsIgnoreCase("Share Current Location Nibo Code")){

                    String uniqueCode = pref.getUniqueCode();
                    if(uniqueCode.equals(" ")){
                        Toast.makeText(MainActivity.this, "Please Get a Current Location Nibo Code First", Toast.LENGTH_LONG).show();
                    }else{
                        i.putExtra(Intent.EXTRA_TEXT, "Hi, this is my Nibo Code:" + "\n" + uniqueCode + "," + "\n" + "Put this code in your Nibo app to see my current location or simply click on this link. http://www.niboapp.com/niboCode?code="+uniqueCode + "\n" + "Sent from Nibo");

                        try {
                            startActivity(Intent.createChooser(i, "Share Current Location Nibo Code"));
                            t.send(new HitBuilders.EventBuilder()
                                    .setCategory("Clicks")  // category i.e. Buttons Action
                                    .setAction("Button")    // action i.e.  Button Name
                                    .setLabel("Share Current Location Nibo Code")    // label i.e.  any meta-data
                                    .build());
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(MainActivity.this, "Not found any app", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else if(actionOptions[item].equalsIgnoreCase("Share House Nibo Code")){

                    String permanentCode = getUserDetails.getAddress().toString().toUpperCase();
                    if(permanentCode.equals("NULL,NULL")){
                        Toast.makeText(MainActivity.this, "Please Get a Permanent Nibo Code First", Toast.LENGTH_LONG).show();
                        openGetNiboHouseCodePrompt(MainActivity.this);
                    }else{
                        i.putExtra(Intent.EXTRA_TEXT, "Hi, this is my House Nibo Code:" + "\n" + permanentCode + "," + "\n" + "Put this code in your Nibo app to see my House location or simply click on this link. http://www.niboapp.com/niboCode?code="+permanentCode + "\n" + "Sent from Nibo");

                        try {
                            startActivity(Intent.createChooser(i, "Share House Nibo Code"));
                            t.send(new HitBuilders.EventBuilder()
                                    .setCategory("Clicks")  // category i.e. Buttons Action
                                    .setAction("Button")    // action i.e.  Button Name
                                    .setLabel("Share House Nibo Code")    // label i.e.  any meta-data
                                    .build());
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(MainActivity.this, "Not found any app", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

        });
        AlertDialog alert = build.create();
        alert.show();








    }



    public void shareSavedLocation(){
        Intent i = new Intent(android.content.Intent.ACTION_SEND);
        String locationPicked = "";
        String location = "";

        if (canTouchMap){
            locationPicked = pickedLocation.latitude / 1E6 + "," + pickedLocation.longitude / 1E6;
        }else{
            location = IntLocation.latitude / 1E6 + "," + IntLocation.longitude / 1E6;
        }

        i.setType("text/plain");
        //i.putExtra(Intent.EXTRA_SUBJECT, "Hi, my current Location is: " + "\n");





        if(canTouchMap){
            i.putExtra(Intent.EXTRA_TEXT, "Hi, this is the venue for the meeting " + "\n" + "http://maps.google.com/maps?q=loc:" + locationPicked + "\n" + "Sent from Nibo");
        }else{
            String when = " As at " + getIntPersonTimeCheckin;
            if (!intFindFirendPin) {
                when = "";
            }
            i.putExtra(Intent.EXTRA_TEXT, "Hi, this is " + getIntName + when + ":" + "\n" + "http://maps.google.com/maps?q=loc:" + location + "\n" + "Sent from Nibo");
        }


        try {
            startActivity(Intent.createChooser(i, "Share Location"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Not found any app", Toast.LENGTH_LONG).show();
        }

    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int MY_CAMERA_REQUEST_LOCATION = 101;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Camera Permission Needed")
                        .setMessage("This app needs the Camera permission, please accept to use camera functionality")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.CAMERA},
                                        MY_CAMERA_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_CAMERA_REQUEST_LOCATION );
            }
        }else{
            openScanActivity();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case MY_CAMERA_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.



                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                        openScanActivity();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void showShareLocationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Action:");

        Resources res = getResources();
        actionOptions = res.getStringArray(R.array.action_options);
        builder.setItems(R.array.action_options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                if(actionOptions[item].equalsIgnoreCase("Share My Current Location")){

                    shareMyLocation();

                }
                else if(actionOptions[item].equalsIgnoreCase("Save My Current Location")){

                    saveCurrentLocation();

                }
                else if(actionOptions[item].equalsIgnoreCase("Set Meeting Location")){

                    setUpMeeting();

                }
                else if(actionOptions[item].equalsIgnoreCase("Get Current Location Nibo Code")){

                    checkIn();

                }
                else if(actionOptions[item].equalsIgnoreCase("Scan Nibo QR Code")){

                    checkCameraPermission();

                }

            }

        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public class updateCheckInStatus extends AsyncTask<NiboUser, Integer, Integer> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = ProgressDialog.show(MainActivity.this, "Current Location Nibo Code.", "Getting Current Location Nibo Code. Please wait...");
        }

        @Override
        protected Integer doInBackground(NiboUser... params) {
            NiboUser niboUser = params[0];

            int result = 0;

            if (niboUser != null){
                try {

                    //MessageUtil.showShortToast(RegistrationActivity.this,"Webservice");
                    WebService webService = new WebService();
                    result = webService.checkInUser(MainActivity.this, niboUser);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //MessageUtil.showShortToast(RegistrationActivity.this,""+result);
            return result;
        }

        @Override
        protected void onPostExecute(Integer res) {
            pd.dismiss();


            if (res == 0) {
                MessageUtil.showAlert(MainActivity.this, "An Error Occurred!", "Error Checking In.");
            } else if(res == 1) {

                Toast.makeText(getApplicationContext(), "Gotten Current Location Nibo Code Successfully", Toast.LENGTH_SHORT).show();
                String uniqueCode = pref.getUniqueCode();

                showAlertWithShare(MainActivity.this, "Nibo Code", "This is your new Current Location Nibo Code for your current location: "+uniqueCode+" .Share it with people to view your current location TEMPORARILY. It changes everytime you request for a new one. It has been copied to your clipboard");
                // Toast.makeText(getApplicationContext(),uniqueCode,Toast.LENGTH_LONG).show();
                Log.d("Unique_Code",uniqueCode);

                myClip = ClipData.newPlainText("text", uniqueCode);
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(getApplicationContext(), "Nibo Code Copied to Clipboard.",
                        Toast.LENGTH_SHORT).show();

            } else if(res == 2) {

                MessageUtil.showAlert(MainActivity.this, "Phone Number Doesn't Exists!", "Please register with another phone number.");
            }




        }
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void checkIntent() {
        Intent getCord = getIntent();



        if(getCord.hasExtra("lat") && getCord.hasExtra("long") && getCord.hasExtra("name")){

            //Toast.makeText(this,"Check Intent has cord",Toast.LENGTH_SHORT).show();



            getIntLat = getCord.getStringExtra("lat");

            getIntLong = getCord.getStringExtra("long");

            getIntName = getCord.getStringExtra("name");

            //Toast.makeText(this,"Got intent ",Toast.LENGTH_SHORT).show();



            intFindFirendPin = false;
            shareASavedLocation = true;
            disableNormalMapConditions();

            //putPinOnMap();



        }else if(getCord.hasExtra("name") && getCord.hasExtra("phoneNumber") && getCord.hasExtra("lastCheckin")  && getCord.hasExtra("timeCheckin")){

            getIntName = getCord.getStringExtra("name");

            getIntPersonPhoneNumber = getCord.getStringExtra("phoneNumber");

            getIntPersonLastCheckin = getCord.getStringExtra("lastCheckin");

            getIntPersonTimeCheckin = getCord.getStringExtra("timeCheckin");

            String[] split = getIntPersonLastCheckin.split(",");

            getIntPersonLat = split[0];
            getIntPersonLong = split[1];

            intFindFirendPin = true;

            disableNormalMapConditions();

            MessageUtil.showShortToast(this,""+getIntPersonLat+",    "+getIntPersonLong+",    "+getIntName);
            //putPinOnMap(getIntPersonLat,getIntPersonLong,getIntName);


        }else{
            canTouchMap = false;

            intPinActivity = false;

            intFindFirendPin = false;

            shareButton.setVisibility(View.VISIBLE);
            shareButton1.setVisibility(View.INVISIBLE);

           // Toast.makeText(this,"No Cords ",Toast.LENGTH_SHORT).show();
        }
    }

    private void disableNormalMapConditions(){
        intPinActivity = true;
        shareButton.setVisibility(View.INVISIBLE);
        shareButton1.setVisibility(View.VISIBLE);
    }

    public void saveLocationDialog(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.enter_name_layout, null);
        dialogBuilder.setView(dialogView);

        final EditText nameBoxInDialog = (EditText) dialogView.findViewById(R.id.name);

        dialogBuilder.setTitle("Enter a Name to save as");
        dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String locName = nameBoxInDialog.getText().toString();

                if (locName.length() == 0 || locName.contentEquals(" ")) {
                    Toast.makeText(getApplicationContext(), "Please provide a name", Toast.LENGTH_SHORT).show();
                    saveLocationDialog();
                }
                else{
                    //dialog.dismiss();
                    //save to database

                    //String concLatLong = latitude + "," + longitude;

                    sq.insertData(locName,latitude,longitude);

                    // Inserting into locations table SQLite
                    UserLocation userLocation = new UserLocation();

                    userLocation.setLatitdue(latitude);
                    userLocation.setLongitude(longitude);
                    userLocation.setDescription(locName);
                    userLocation.setMysqlId(0);
                    userLocation.setIsDeleted(Constants.NOT_DELETED);
                    userLocation.setIsSyncedToOnlineDBStatus(Constants.FALSE);

                    SQLController sqlController = new SQLController(getApplicationContext());
                    sqlController.addSavedLocationToSQLiteDB(userLocation);

                    Toast.makeText(getApplicationContext(), "Successfully saved", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                }

            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void saveNiboCodeDialog(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.enter_name_layout, null);
        dialogBuilder.setView(dialogView);

        final EditText nameBoxInDialog = (EditText) dialogView.findViewById(R.id.name);

        dialogBuilder.setTitle("Please Enter Your House Nibo Code");
        dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                newNiboCode = nameBoxInDialog.getText().toString();

                if (newNiboCode.length() < 2 || newNiboCode.contentEquals(" ")) {
                    Toast.makeText(getApplicationContext(), "Please provide a Valid Nibo Code", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), newNiboCode.length()+"", Toast.LENGTH_SHORT).show();
                    saveNiboCodeDialog();
                }
                else {
                    //dialog.dismiss();
                    //save to database

                    //Check Internet Connection


                    String number = getUserDetails.getPhoneNumber();

                    niboUser = new NiboUser();
                    niboUser.setAddress(newNiboCode);
                    niboUser.setPhoneNumber(number);


                    new EditNiboHouseCodeTask().execute(niboUser);

                }

            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private boolean CheckEnableGPS(){
        boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(gps_enabled){



            if(latitude != null){

            }else{


                dialog.show();


            }

            return true;
            //GPS Enabled
            //Toast.makeText(this, "GPS is Enabled", Toast.LENGTH_LONG).show();
        }else{
            AlertDialog.Builder alertGpsEnabled = new AlertDialog.Builder(this);
            alertGpsEnabled.setTitle("GPS needed");
            alertGpsEnabled.setMessage("Please, you need to enable your GPS for this app");
            alertGpsEnabled.setCancelable(false);
            alertGpsEnabled.setPositiveButton("Enable GPS in settings", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertGpsEnabled.show();


            return false;
        }



    }

    private void setUpMeeting(){
        disableNormalMapConditions();
        canTouchMap = true;
        Toast.makeText(MainActivity.this, "Please select a place on the map you'd love to set up the meeting", Toast.LENGTH_LONG).show();
        shareButton1.setText("SHARE MEETING POINT");

        t.send(new HitBuilders.EventBuilder()
                .setCategory("Clicks")  // category i.e. Buttons Action
                .setAction("Button")    // action i.e.  Button Name
                .setLabel("Set Up Meeting")    // label i.e.  any meta-data
                .build());
    }

    private void checkIn(){
        Toast.makeText(MainActivity.this, "Checking in...", Toast.LENGTH_SHORT).show();

        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        //LocateMeUser phoneCon = pref.getUserDetails();
        String number = getUserDetails.getPhoneNumber();
        //Toast.makeText(MainActivity.this, number, Toast.LENGTH_SHORT).show();

        NiboUser niboUser = new NiboUser();
        niboUser.setPhoneNumber(number);
        niboUser.setLastCheckin(latitude+","+longitude);
        niboUser.setTimeCheckin(currentDateTimeString);

        new updateCheckInStatus().execute(niboUser);


    }

    private void saveCurrentLocation(){
        if(latitude != null){
            saveLocationDialog();
            t.send(new HitBuilders.EventBuilder()
                    .setCategory("Clicks")  // category i.e. Buttons Action
                    .setAction("Button")    // action i.e.  Button Name
                    .setLabel("Save Current Location")    // label i.e.  any meta-data
                    .build());
        }else{
            Toast.makeText(MainActivity.this, "location not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareApp(){
        Intent i = new Intent(android.content.Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, "I have just found an amazing app 'Nibo', you can get it for FREE from Google Play at " + "\n" + "https://play.google.com/store/apps/details?id=com.nigeria.locateme.locateme&hl=en" );
        try {
            startActivity(Intent.createChooser(i, "Share App"));
            t.send(new HitBuilders.EventBuilder()
                    .setCategory("Clicks")  // category i.e. Buttons Action
                    .setAction("Button")    // action i.e.  Button Name
                    .setLabel("Share App")    // label i.e.  any meta-data
                    .build());
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Not found any app", Toast.LENGTH_LONG).show();
        }
    }

    private void aboutApp(){
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
        t.send(new HitBuilders.EventBuilder()
                .setCategory("Clicks")  // category i.e. Buttons Action
                .setAction("Button")    // action i.e.  Button Name
                .setLabel("About App")    // label i.e.  any meta-data
                .build());
    }

    private void rateApp() {
        //Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Uri uri = Uri.parse("market://details?id=" + "com.nigeria.locateme.locateme");
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
            t.send(new HitBuilders.EventBuilder()
                    .setCategory("Clicks")  // category i.e. Buttons Action
                    .setAction("Button")    // action i.e.  Button Name
                    .setLabel("Rate App")    // label i.e.  any meta-data
                    .build());
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    private void openWebsite(String url){
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    public void openScanActivity(){
        Intent openQRActivity = new Intent(MainActivity.this, ScanNiboQRActivity.class);
        startActivity(openQRActivity);
        t.send(new HitBuilders.EventBuilder()
                .setCategory("Clicks")  // category i.e. Buttons Action
                .setAction("Button")    // action i.e.  Button Name
                .setLabel("Scan Nibo QR")    // label i.e.  any meta-data
                .build());
    }

    public void shareHouseNiboCode(String NiboHouseCode){
        Intent i = new Intent(android.content.Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, "Hi, this is my House Nibo Code:" + "\n" + NiboHouseCode + "," + "\n" + "Put this code in your Nibo app to see my House location or simply click on this link. http://www.niboapp.com/niboCode?code="+NiboHouseCode + "\n" + "Sent from Nibo");
        startActivity(Intent.createChooser(i, "Share House Nibo Code"));

        t.send(new HitBuilders.EventBuilder()
                .setCategory("Clicks")  // category i.e. Buttons Action
                .setAction("Button")    // action i.e.  Button Name
                .setLabel("Share House Nibo Code")    // label i.e.  any meta-data
                .build());
    }

    private class ReverseGeocodingTask extends AsyncTask<LatLng, Void, String>{

        Context mContext;

        public ReverseGeocodingTask(Context context){
            super();
            mContext = context;
        }

        // Finding address using reverse geocoding
        @Override
        protected String doInBackground(LatLng... params) {
            Geocoder geocoder = new Geocoder(mContext);
            double latitude = params[0].latitude;
            double longitude = params[0].longitude;

            List<Address> addresses = null;
            stringLocation="";

            try {
                addresses = geocoder.getFromLocation(latitude, longitude,1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(addresses != null && addresses.size() > 0 ){
                Address address = addresses.get(0);

                stringLocation = String.format("%s, %s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getLocality(),

                        address.getCountryName());

                variousLocations = String.format("%s, %s, %s",
                        address.getFeatureName(), //7
                        //address.getAdminArea(),  //null
                        //address.getSubAdminArea());  //null
                        address.getFeatureName(), //7
                        //address.getSubLocality()); //null
                        address.getPostalCode()); // null
            }else{
                if(!stringLocation.equals("I'm here")){//dont change to im here if we have a previous address
                    stringLocation = "I'm here";
                }

            }

            return stringLocation;
        }

        @Override
        protected void onPostExecute(String addressText) {
            // Setting the title for the marker.
            // This will be displayed on taping the marker



        }
    }

    public void showAlertWithShare(final Context context, final String title, final CharSequence text) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(title);
                builder.setMessage(text);
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton("SHARE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        shareMyLocation();
                    }
                });
                builder.show();
            }
        });
    }



    public void openGetNiboHouseCodePrompt(final Context context) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("GET A HOUSE NIBO CODE");
                builder.setMessage("Have you got a Nibo Code For Your House? Why not do it right away");
                builder.setCancelable(false);
                builder.setNegativeButton("MAYBE LATER", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        t.send(new HitBuilders.EventBuilder()
                                .setCategory("Clicks")  // category i.e. Buttons Action
                                .setAction("Button")    // action i.e.  Button Name
                                .setLabel("Get Nibo Code Maybe Later")    // label i.e.  any meta-data
                                .build());
                    }
                });
                builder.setNeutralButton("YES I DO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveNiboCodeDialog();
                        t.send(new HitBuilders.EventBuilder()
                                .setCategory("Clicks")  // category i.e. Buttons Action
                                .setAction("Button")    // action i.e.  Button Name
                                .setLabel("I Have Nibo Code")    // label i.e.  any meta-data
                                .build());
                    }
                });
                builder.setPositiveButton("GET NIBO CODE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //shareMyLocation();
                        Intent getNiboCodeInt = new Intent(MainActivity.this, GetNiboCodeActivity.class);
                        startActivity(getNiboCodeInt);
                        t.send(new HitBuilders.EventBuilder()
                                .setCategory("Clicks")  // category i.e. Buttons Action
                                .setAction("Button")    // action i.e.  Button Name
                                .setLabel("Get Nibo Code On Dialog")    // label i.e.  any meta-data
                                .build());
                    }
                });
                builder.show();
            }
        });
    }

    private void showOptionsDialog(final NiboUser niboUser){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(niboUser.getFullName() + " Details:");

        Resources res = getResources();
        actionOptions = res.getStringArray(R.array.action_location_options);
        builder.setItems(R.array.action_location_options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                Intent in = new Intent(MainActivity.this, MapsActivity.class);
                // Do something with the selection
                if(actionOptions[item].equalsIgnoreCase("Home Address")){

                    if(niboUser.getAddress().length() != 8){
                        Toast.makeText(MainActivity.this, "House details unavailable", Toast.LENGTH_SHORT).show();
                    }else{

                        //new SearchPermanentCodeTask().execute(niboUser.getAddress());
                    }

                }
                else if(actionOptions[item].equalsIgnoreCase("Last seen Location")){

                    if(niboUser.getLastCheckin().contains("null")){
                        Toast.makeText(MainActivity.this, "Location details unavailable", Toast.LENGTH_SHORT).show();
                    }else{

                        in.putExtra("name", niboUser.getFullName());
                        in.putExtra("phoneNumber", niboUser.getPhoneNumber());
                        in.putExtra("lastCheckin", niboUser.getLastCheckin());
                        in.putExtra("timeCheckin"," As at " + niboUser.getTimeCheckin());
                        startActivity(in);
                    }
                }
            }

        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showHouseDetailsPreviewDialog(final NiboHouse niboHouse){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("House Details:");

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.contact_preview, null);
        builder.setView(dialogView);


        final TextView housePreviewState = (TextView) dialogView.findViewById(R.id.housePreview_state);
        final TextView housePreviewLocalGovernment = (TextView) dialogView.findViewById(R.id.housePreview_localGovernment);
        final TextView housePreviewStreet = (TextView) dialogView.findViewById(R.id.housePreview_street);
        final TextView housePreviewHouseAddress = (TextView) dialogView.findViewById(R.id.housePreview_houseAddress);
        //final TextView contactPreviewJobTitle = (TextView) dialogView.findViewById(R.id.contactPreview_jobTitle);

        final TableRow housePreviewStateTableRow = (TableRow) dialogView.findViewById(R.id.housePreview_stateTableRow);
        final TableRow housePreviewLocalGovernmentTableRow = (TableRow) dialogView.findViewById(R.id.housePreview_LGATableRow);
        final TableRow housePreviewStreetTableRow = (TableRow) dialogView.findViewById(R.id.housePreview_streetTableRow);
        final TableRow housePreviewHouseAddressTableRow = (TableRow) dialogView.findViewById(R.id.housePreview_houseAddressTableRow);
        //final TableRow contactPreviewJobTitleTableRow = (TableRow) dialogView.findViewById(R.id.contactPreview_jobTitleTableRow);

        if(niboHouse.getState() != null && !niboHouse.getState().equalsIgnoreCase("")){
            housePreviewState.setText(niboHouse.getState() != null ? niboHouse.getState() : "");
        } else{
            housePreviewStateTableRow.setVisibility(View.GONE);
        }

        if(niboHouse.getLocalGovernment() != null && !niboHouse.getLocalGovernment().equalsIgnoreCase("")){
            housePreviewLocalGovernment.setText(niboHouse.getLocalGovernment() != null ? niboHouse.getLocalGovernment() : "");
        } else{
            housePreviewLocalGovernmentTableRow.setVisibility(View.GONE);
        }

        if(niboHouse.getStreet() != null && !niboHouse.getStreet().equalsIgnoreCase("")){
            housePreviewStreet.setText(niboHouse.getStreet() != null ? niboHouse.getStreet() : "");
        } else{
            housePreviewStreetTableRow.setVisibility(View.GONE);
        }

        if(niboHouse.getHouseAddress() != null && !niboHouse.getHouseAddress().equalsIgnoreCase("")){
            housePreviewHouseAddress.setText(niboHouse.getHouseAddress() != null ? niboHouse.getHouseAddress() : "");
        } else{
            housePreviewHouseAddressTableRow.setVisibility(View.GONE);
        }

        builder.setPositiveButton("Open", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {


                Intent in = new Intent(MainActivity.this, MapsActivity.class);
                // Do something with the selection


                if(niboHouse.getLatLong().contains("null")){
                    Toast.makeText(MainActivity.this, "House details unavailable", Toast.LENGTH_SHORT).show();
                }else{

                    //I ALREADY HAVE A FUNCTION RECEIVING SIMILAR INTENT SO ID JUST WORK WITH THE TAGS THERE

                    in.putExtra("name", "The House");
                    in.putExtra("phoneNumber", niboHouse.getLocalGovernment());
                    in.putExtra("lastCheckin", niboHouse.getLatLong());
                    in.putExtra("timeCheckin", niboHouse.getHouseAddress() );
                    startActivity(in);
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog b = builder.create();
        b.show();




        /*Resources res = getResources();
        actionOptions = res.getStringArray(R.array.action_location_options);
        builder.setItems(R.array.action_location_options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                Intent in = new Intent(FindFriendActivity.this, MapsActivity.class);
                // Do something with the selection
                if(actionOptions[item].equalsIgnoreCase("Home Address")){


                    if(sendLatLong.contains("null")){
                        Toast.makeText(FindFriendActivity.this, "House details unavailable", Toast.LENGTH_SHORT).show();
                    }else{

                        in.putExtra("name",sendFullName);
                        in.putExtra("phoneNumber",sendPhoneNumber);
                        in.putExtra("lastCheckin",sendAddress);
                        in.putExtra("timeCheckin","Home Address");
                        startActivity(in);
                    }



                }
                else if(actionOptions[item].equalsIgnoreCase("Last seen Location")){

                    if(sendLastCheckin.contains("null")){
                        Toast.makeText(FindFriendActivity.this, "Location details unavailable", Toast.LENGTH_SHORT).show();
                    }else{

                        in.putExtra("name",sendFullName);
                        in.putExtra("phoneNumber",sendPhoneNumber);
                        in.putExtra("lastCheckin",sendLastCheckin);
                        in.putExtra("timeCheckin"," As at "+sendTimeCheckin);
                        startActivity(in);
                    }



                }
            }

        });
        AlertDialog alert = builder.create();
        alert.show();*/
    }

    public class EditNiboHouseCodeTask extends AsyncTask<NiboUser, Integer, Integer> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = ProgressDialog.show(MainActivity.this, "Saving House Nibo Code.", "Saving. Please wait...");

        }

        @Override
        protected Integer doInBackground(NiboUser... params) {
            // TODO: attempt authentication against a network service.
            NiboUser niboUser = params[0];

            Log.i("locateMeUser: ", ""+ niboUser);
            int result = 0;

            if (niboUser != null){
                try {

                    //MessageUtil.showShortToast(RegistrationActivity.this,"Webservice");
                    WebService webService = new WebService();
                    result = webService.editNiboHouseCode(MainActivity.this, niboUser);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //MessageUtil.showShortToast(RegistrationActivity.this,""+result);
            return result;
        }

        @Override
        protected void onPostExecute(Integer res) {
            pd.dismiss();


            if (res == 0) {
                MessageUtil.showAlert(MainActivity.this, "An Error Occurred!", "Error Updating Profile.");
            } else if(res == 1) {

                //pref.createRegistrationSession();
                //pref.saveUserDetails(locateMeUser);
                pref.saveHouseUniqueCode(newNiboCode);
                Toast.makeText(getApplicationContext(), "Nibo Code Saved Successful", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(MainActivity.this, MainActivity.class);
                startActivity(in);
                finish();
            } else if(res == 2) {

                //MessageUtil.showAlert(SettingsActivity.this, "Phone Number Exists!", "Please register with another phone number.");//this will not come up ever
            }
        }

        @Override
        protected void onCancelled() {
            pd.dismiss();
        }
    }

}
