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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

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
import com.nigeria.locateme.locateme.R;
import com.nigeria.locateme.locateme.entities.NiboUser;
import com.nigeria.locateme.locateme.entities.Preferences;
import com.nigeria.locateme.locateme.entities.UserLocation;
import com.nigeria.locateme.locateme.utils.Constants;
import com.nigeria.locateme.locateme.utils.MessageUtil;
import com.nigeria.locateme.locateme.utils.SQLController;
import com.nigeria.locateme.locateme.webservices.WebService;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements NavigationView.OnNavigationItemSelectedListener , OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    public String latitude;         //latitude to be sent
    public String longitude;        //longitude to be sent
    private String actionOptions[];
    MapView mMapView;
    private MarkerOptions mMarker;
    private GoogleApiClient mGoogleApiClient;
    private Switch mySwitch;
    private boolean mapType = true;
    private boolean mapTouched = false;

    private ClipboardManager myClipboard;
    private ClipData myClip;

    private SQLController sq = null;

    private Preferences pref;

    public double navLat,navLong;


    public String msgBody, msgTitle;


    private Button shareButton,shareButton1,navButton,buttonSwitch;

    LocationManager lm;
    LatLng myLocation,IntLocation,pickedLocation;

    ProgressDialog dialog;

    boolean intPinActivity,canTouchMap,canTouchHouseGate,intFindFirendPin;
    String getIntLat,getIntLong,getIntName;
    String getIntPersonName,getIntPersonPhoneNumber,getIntPersonLastCheckin,getIntPersonTimeCheckin
            ,getIntPersonLat,getIntPersonLong,stringLocation;

    String getState,getLocalGov,getStreet,getHouseAddress,getBuildingType,getEmail,getPhoneNumber,getNiboCode,getFullName;

    Tracker t;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        sq = new SQLController(this);

        pref = new Preferences(this);

        myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

       // Toast.makeText(this,"Got intent ",Toast.LENGTH_LONG).show();

        MyAnalytics application = (MyAnalytics) getApplication();
        t = application.getDefaultTracker();
        t.setScreenName("Display Map Activity");

        canTouchMap = false;
        canTouchHouseGate = false;
        //intPinActivity = false;


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Location myLocation = getLastKnownLocation();
        //onLocationChanged(myLocation);

        lm = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);

        dialog = new ProgressDialog(MapsActivity.this);
        dialog.setMessage("Getting your current location. Please wait...");
        dialog.setCancelable(true);



        shareButton = (Button) findViewById(R.id.shareButton);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //shareMyLocation();
                showShareLocationDialog();
            }
        });

        shareButton1 = (Button) findViewById(R.id.shareButton1);

        shareButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //shareSavedLocation();
                showShareLocationDialog();
            }
        });

        navButton = (Button) findViewById(R.id.navButton);

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToLocation();
            }
        });


        //mySwitch = (Switch) findViewById(R.id.mySwitch);

        //mySwitch.setChecked(false);
        //attach a listener to check for changes in state
        //mySwitch.setOnCheckedChangeListener(this);


        buttonSwitch =  (Button) findViewById(R.id.buttonSwitch);


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
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(intPinActivity == true){

            if(intFindFirendPin ){
                putPinOnMap(getIntPersonLat,getIntPersonLong,getIntName,getIntPersonTimeCheckin);
            }else{
                putPinOnMap(getIntLat,getIntLong,getIntName,"Share this location");
            }

        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MapsActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
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
                //Toast.makeText(MapsActivity.this,"Current Location", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {

                pickedLocation = point;

                if(canTouchMap){

                    mapTouched = true;
                    if (mMarker != null) {
                        mMap.clear();

                        if(allowedDistance()){
                            mMap.addMarker(new MarkerOptions().position(point).title(msgTitle).snippet(msgBody)).showInfoWindow();
                        }else{
                            Toast.makeText(MapsActivity.this, "Please tap a place close to your building's gate", Toast.LENGTH_SHORT).show();
                        }

                    }

                }

            }
        });
        float zoom = 16;
      //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,zoom));
    }




    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckEnableGPS();

        checkIntent();
    }



    private void checkIntent() {
        Intent getCord = getIntent();



        if(getCord.hasExtra("lat") && getCord.hasExtra("long") && getCord.hasExtra("name")){

            //Toast.makeText(this,"Check Intent has cord",Toast.LENGTH_SHORT).show();



            getIntLat = getCord.getStringExtra("lat");

            getIntLong = getCord.getStringExtra("long");

            getIntName = getCord.getStringExtra("name");

           // Toast.makeText(this,"Got intent ",Toast.LENGTH_SHORT).show();



            intFindFirendPin = false;
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

            msgTitle = "Meeting location";
            msgBody = "Share this location as where the meeting will hold";



            //latitude = Double.toString(getIntPersonLat);
            double latitude = Double.parseDouble(getIntPersonLat);
            double longitude = Double.parseDouble(getIntPersonLong);

            navLat = Double.parseDouble(getIntPersonLat);
            navLong = Double.parseDouble(getIntPersonLong);


            myLocation = new LatLng(latitude, longitude);

            //new ReverseGeocodingTask(getBaseContext()).execute(myLocation);

            //MessageUtil.showShortToast(this,""+getIntPersonLat+",    "+getIntPersonLong+",    "+getIntName);
            //putPinOnMap(getIntPersonLat,getIntPersonLong,getIntName);


        } else if(getCord.hasExtra("state") && getCord.hasExtra("localGov") && getCord.hasExtra("street")  && getCord.hasExtra("houseAddress")){

            getState = getCord.getStringExtra("state");

            getLocalGov = getCord.getStringExtra("localGov");

            getStreet = getCord.getStringExtra("street");

            getHouseAddress = getCord.getStringExtra("houseAddress");

            getBuildingType = getCord.getStringExtra("buildingType");

            getEmail = getCord.getStringExtra("email");

            getPhoneNumber = getCord.getStringExtra("phoneNumber");

            getFullName = getCord.getStringExtra("fullName");





            intFindFirendPin = false;


            navButton.setVisibility(View.INVISIBLE);
            shareButton1.setText("REQUEST HOUSE NIBO CODE");
            //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            touchHouseGate();

            //disableNormalMapConditions();






        } else if(getCord.hasExtra("activateCode")){

            getNiboCode = getCord.getStringExtra("niboCode");

            intFindFirendPin = false;


            navButton.setVisibility(View.INVISIBLE);
            shareButton1.setText("ACTIVATE HOUSE NIBO CODE");
            //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            touchHouseGate();

            MessageUtil.showAlert(this,"Activate Nibo Code", "This Nibo Code Has Not Been Activated, Please Stand at Your Gate and Activate it or Just Touch Your Gate on The Map");



        }else{
            intPinActivity = false;

            intFindFirendPin = false;

            shareButton.setVisibility(View.VISIBLE);
            shareButton1.setVisibility(View.INVISIBLE);

            //Toast.makeText(this,"No Cords ",Toast.LENGTH_SHORT).show();
        }
    }

    private void disableNormalMapConditions(){
        intPinActivity = true;
        shareButton.setVisibility(View.INVISIBLE);
        shareButton1.setVisibility(View.VISIBLE);
    }


    private void touchHouseGate(){
        disableNormalMapConditions();
        canTouchMap = true;
        canTouchHouseGate = true;
        intPinActivity = false;
        //Toast.makeText(MapsActivity.this, "Please touch the gate of your house on this Map", Toast.LENGTH_LONG).show();
        MessageUtil.showAlert(this,"Stand By Gate", "Please Stand by your building's gate OR simply touch the gate of your house on this Map and click REQUEST.");
        msgTitle = "House Gate";
        msgBody = "This is the gate of my house";
    }



    @Override
    protected void onPause() {
        super.onPause();
        //mMapView.onPause();
       /* if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }*/
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }


    @Override
    public void onLocationChanged(Location location) {
        double tempLatitude = location.getLatitude();
        double tempLongitude = location.getLongitude();
        latitude = Double.toString(tempLatitude);
        longitude = Double.toString(tempLongitude);

        myLocation = new LatLng(tempLatitude, tempLongitude);

        if(intPinActivity == false){
            updateCamera();
        }


        if(dialog.isShowing()){
            dialog.dismiss();
        }

        if(intPinActivity == false){
            if (mMarker != null) {
                updateMarker();
            }else{
                addMarker();
            }
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
        mMap.clear();
        mMarker = new MarkerOptions()
                .position(myLocation)
                .title("Me")
                .snippet("I'm here");
        mMap.addMarker(mMarker).showInfoWindow();
    }

    private void addMarker() {
        mMarker = new MarkerOptions()
                .position(myLocation)
                .title("Me")
                .snippet("I'm here");
        mMap.addMarker(mMarker).showInfoWindow();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saved_locations:

                //shareMyLocation();
                //showShareLocationDialog();
                /*Intent i = new Intent(this,SavedLocationActivity.class);
                startActivity(i);*/

                Intent into = new Intent(this,MainActivity.class);
                startActivity(into);


                return true;
            case R.id.find_person:

                Intent intf = new Intent(this,FindFriendActivity.class);
                startActivity(intf);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void showShareLocationDialog(){

        if(canTouchHouseGate){

            if(myLocation != null ){
                if(shareButton1.getText().toString().equals("ACTIVATE HOUSE NIBO CODE")){
                    activateNiboCode();
                }else{
                    saveRequestLocation();
                }

            }else{
                //MessageUtil.showShortToast(MapsActivity.this,"Please select the gate of your building on the map");
                MessageUtil.showLongToast(MapsActivity.this,"Please wait a little while we get your current location or touch the gate of your building on the map");
            }



        }else {


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose Action:");

            Resources res = getResources();
            actionOptions = res.getStringArray(R.array.action_options_nibo_code);
            builder.setItems(R.array.action_options_nibo_code, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    if (actionOptions[item].equalsIgnoreCase("Share This Location")) {

                        shareThisLocation();

                    } else if (actionOptions[item].equalsIgnoreCase("Save This Location")) {

                        saveThisLocationDialog();

                    } else if (actionOptions[item].equalsIgnoreCase("Navigate")) {

                        navigateToLocation();

                    }


                }

            });
            AlertDialog alert = builder.create();
            alert.show();


        }
    }


    public void shareThisLocation(){
        Intent i = new Intent(android.content.Intent.ACTION_SEND);
        //String location = myLocation.latitude / 1E6 + "," + myLocation.longitude / 1E6;
        i.setType("text/plain");
        //i.putExtra(Intent.EXTRA_SUBJECT, "Hi, my current Location is: " + "\n");
        //i.putExtra(Intent.EXTRA_TEXT, "Hi, my current Location is: " + "\n" + "http://maps.google.com/maps?q=loc:" + location + "\n" + "Sent from Nibo");

        String thisLocation = navLat+","+navLat;
        i.putExtra(Intent.EXTRA_TEXT, "Hi, this is " + getIntName + ":" + "\n" + "http://maps.google.com/maps?q=loc:" + thisLocation + "\n" + "Sent from Nibo");

        try {
            startActivity(Intent.createChooser(i, "Share Location"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Not found any app", Toast.LENGTH_LONG).show();
        }

    }

    public void activateNiboCode(){

        String locationPicked = "";
        String location = "";
        String choosenLocation;



        if (mapTouched){
            locationPicked = pickedLocation.latitude + "," + pickedLocation.longitude;
            choosenLocation = locationPicked;
        }else{
            //location = myLocation.latitude / 1E6 + "," + myLocation.longitude / 1E6;
            location = myLocation.latitude + "," + myLocation.longitude;
            choosenLocation = location;
        }

        NiboUser niboUser = new NiboUser();
        niboUser.setAddress(getNiboCode);
        niboUser.setLatLong(choosenLocation);

        new activateHouseCode().execute(niboUser);

        t.send(new HitBuilders.EventBuilder()
                .setCategory("Clicks")  // category i.e. Buttons Action
                .setAction("Button")    // action i.e.  Button Name
                .setLabel("Activate Nibo Code")    // label i.e.  any meta-data
                .build());

    }

    public void saveRequestLocation(){
        //Intent i = new Intent(android.content.Intent.ACTION_SEND);
        String locationPicked = "";
        String location = "";
        String choosenLocation;



        if (mapTouched){
            locationPicked = pickedLocation.latitude + "," + pickedLocation.longitude;
            choosenLocation = locationPicked;
        }else{
            //location = myLocation.latitude / 1E6 + "," + myLocation.longitude / 1E6;
            location = myLocation.latitude + "," + myLocation.longitude;
            choosenLocation = location;
        }





        NiboUser niboUser = new NiboUser();



        niboUser.setFullName(getFullName);
        niboUser.setHouseAddress(getHouseAddress);
        niboUser.setBuildingType(getBuildingType);
        niboUser.setState(getState);
        niboUser.setStreet(getStreet);
        niboUser.setLocalGovernment(getLocalGov);
        niboUser.setEmail(getEmail);
        niboUser.setPhoneNumber(getPhoneNumber);
        niboUser.setLatLong(choosenLocation);


        new requestHouseCode().execute(niboUser);

        t.send(new HitBuilders.EventBuilder()
                .setCategory("Clicks")  // category i.e. Buttons Action
                .setAction("Button")    // action i.e.  Button Name
                .setLabel("Request Nibo Code")    // label i.e.  any meta-data
                .build());


    }

    public void navigateToLocation(){
        String uri = "http://maps.google.com/maps?daddr=" + navLat + "," + navLong + " (" + getIntName + ")";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        try
        {
            startActivity(intent);
        }
        catch(ActivityNotFoundException ex)
        {
            try
            {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(unrestrictedIntent);
            }
            catch(ActivityNotFoundException innerEx)
            {
                Toast.makeText(this, "Please install a maps application", Toast.LENGTH_LONG).show();
            }
        }
    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
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
                                ActivityCompat.requestPermissions(MapsActivity.this,
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

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }





    public class requestHouseCode extends AsyncTask<NiboUser, Integer, Integer>{

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = ProgressDialog.show(MapsActivity.this, "Requesting.", "Requesting House Nibo Code. Please wait...");
        }

        @Override
        protected Integer doInBackground(NiboUser... params) {
            NiboUser niboUser = params[0];

            int result = 0;

            if (niboUser != null){
                try {

                    //MessageUtil.showShortToast(RegistrationActivity.this,"Webservice");
                    WebService webService = new WebService();
                    result = webService.requestHouseCode(MapsActivity.this, niboUser);

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
                MessageUtil.showAlert(MapsActivity.this, "An Error Occurred!", "Error Checking In.");
            } else if(res == 1) {

                Toast.makeText(getApplicationContext(), "Request Successful", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Nibo Code Copied To Clipboard", Toast.LENGTH_LONG).show();

                //String uniqueCode = pref.getUniqueCode();

                String uniqueNiboCode = pref.getHouseUniqueCode();
                String msg = "Your request was successful and this is your House Nibo Code: "+ uniqueNiboCode.toUpperCase() + " which has been sent to Your Mail. You can begin to share your NIBO Code with Emergency Services, Delivery Services like Konga, Jumia and also with Family and Friends. Thank you for using Nibo";
                showSuccesssMessage("Request Successful"  , msg);

                myClip = ClipData.newPlainText("text", uniqueNiboCode);
                myClipboard.setPrimaryClip(myClip);
                 //Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                //Log.d("Unique_Code",uniqueCode);
                //finish();

            } else if(res == 2) {

                MessageUtil.showAlert(MapsActivity.this, "Phone Number Doesn't Exists!", "Please register with another phone number.");
            }




        }
    }


    public class activateHouseCode extends AsyncTask<NiboUser, Integer, Integer>{

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = ProgressDialog.show(MapsActivity.this, "Activating.", "Activating House Nibo Code. Please wait...");
        }

        @Override
        protected Integer doInBackground(NiboUser... params) {
            NiboUser niboUser = params[0];

            int result = 0;

            if (niboUser != null){
                try {

                    //MessageUtil.showShortToast(RegistrationActivity.this,"Webservice");
                    WebService webService = new WebService();
                    result = webService.activateHouseCode(MapsActivity.this, niboUser);

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
                MessageUtil.showAlert(MapsActivity.this, "An Error Occurred!", "Error Checking In.");
            } else if(res == 1) {

                Toast.makeText(getApplicationContext(), "Activation Successful", Toast.LENGTH_SHORT).show();
                //String uniqueCode = pref.getUniqueCode();


                String msg = "Your Nibo Code has now been activated and can be used. Thank you for using Nibo.";
                showSuccesssMessage("Activation Successful"  , msg);
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();

                //Log.d("Unique_Code",uniqueCode);
                //finish();

            } else if(res == 2) {

                MessageUtil.showAlert(MapsActivity.this, "Phone Number Doesn't Exists!", "Please register with another phone number.");
            }




        }
    }



    public void showSuccesssMessage( final String title, final CharSequence text) {
        ((Activity) MapsActivity.this).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle(title);
                builder.setMessage(text);
                builder.setCancelable(false);
                builder.setPositiveButton("TELL YOUR FRIENDS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        ((Activity) MapsActivity.this).finish();


                        socialMediaShare();

                    }
                });
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        ((Activity) MapsActivity.this).finish();


                        //socialMediaShare();

                    }
                });
                builder.show();
            }
        });
    }

    public void socialMediaShare(){
        Intent i = new Intent(android.content.Intent.ACTION_SEND);

        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, "Hi, i just got a Nibo Code for my House. Download the Nibo App to Get Yours Now. #Nibo #NiboCode #WhatsYourNiboCode #SmartAddresses.");

        try {
            startActivity(Intent.createChooser(i, "Tell Your Friends About Nibo"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "Not found any app", Toast.LENGTH_LONG).show();
        }

    }

    public void saveThisLocationDialog(){
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
                    saveThisLocationDialog();
                }
                else{
                    //dialog.dismiss();
                    //save to database

                    //String concLatLong = latitude + "," + longitude;

                    String navLatStr = Double.toString(navLat);
                    String navLongStr = Double.toString(navLong);

                    sq.insertData(locName,navLatStr,navLongStr);



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
            //stringLocation="";

            try {
                addresses = geocoder.getFromLocation(latitude, longitude,1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(addresses != null && addresses.size() > 0 ){
                Address address = addresses.get(0);

                stringLocation = String.format("%s, %s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getSubThoroughfare(),
                        address.getCountryName());
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

            //Snackbar.make(mMapView, stringLocation, Snackbar.LENGTH_LONG).setAction("Action",null).show();

           //Toast.makeText(MapsActivity.this, stringLocation, Toast.LENGTH_LONG).show();


        }
    }


    public boolean allowedDistance(){
        boolean allow = true;


        Location locationA = new Location("point A");

        locationA.setLatitude(pickedLocation.latitude);
        locationA.setLongitude(pickedLocation.longitude);

        Location locationB = new Location("point B");

        locationB.setLatitude(myLocation.latitude);
        locationB.setLongitude(myLocation.longitude);

        float distance = locationA.distanceTo(locationB);

        //Toast.makeText(MapsActivity.this, "Distance is: "+ distance, Toast.LENGTH_LONG).show();

        /*if(distance>25){
            allow = false;
            pickedLocation = null;
        }*/

        return allow;

    }

}
