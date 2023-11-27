package com.nigeria.locateme.locateme.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.nigeria.locateme.locateme.R;
import com.nigeria.locateme.locateme.entities.NiboUser;
import com.nigeria.locateme.locateme.entities.Preferences;
import com.nigeria.locateme.locateme.utils.ConnectionDetector;
import com.nigeria.locateme.locateme.utils.MessageUtil;
import com.nigeria.locateme.locateme.utils.ProgressBarUtil;
import com.nigeria.locateme.locateme.webservices.WebService;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 * Oseghe Theophilus
 */
public class RegistrationActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, LocationListener {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mFullName, mPhoneNumber;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private LocationManager locationManager;
    private String currentLat, currentLong;
    private boolean isLocationReady = false;
    private Button mCheckIn;
    private Button mEmailSignInButton;
    static final int LOCATION = 0x1;
    static final int GPS_SETTINGS = 0x7;
    static final int SMS = 0x2;
    static final int CONTACTS = 0x3;

    private AVLoadingIndicatorView loadingIndicatorView;

    GoogleApiClient client;
    LocationRequest mLocationRequest;
    PendingResult<LocationSettingsResult> result;

    private Criteria criteria;

    String time_checkin, last_checkin;

    private Preferences pref;

    private ProgressBarUtil progressBarUtil;

    private ConnectionDetector connectionDetector;

    Tracker t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        // Set up the login form.

      /*  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        MyAnalytics application = (MyAnalytics) getApplication();
        t = application.getDefaultTracker();
        t.setScreenName("Registration Activity");

        pref = new Preferences(this);
        connectionDetector = new ConnectionDetector(getApplicationContext());
        progressBarUtil = new ProgressBarUtil();

        setUpViews();
        setUpListeners();

        populateAutoComplete();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        client = new GoogleApiClient.Builder(this)
                .addApi(AppIndex.API)
                .addApi(LocationServices.API)
                .build();
    }

    private void setUpViews(){
        mFullName = (EditText) findViewById(R.id.fullname);
        mPhoneNumber = (EditText) findViewById(R.id.phone_no);
        mPasswordView = (EditText) findViewById(R.id.password);
        mConfirmPasswordView = (EditText) findViewById(R.id.confirmPassword);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mLoginFormView = findViewById(R.id.login_form);
        loadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.progress_bar);
        //mProgressView = findViewById(R.id.login_progress);

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

       // mCheckIn = (Button) findViewById(R.id.checkinbutton);
    }

    private void setUpListeners(){
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectionDetector.isURLReachable(RegistrationActivity.this)) {
                    attemptLogin();
                } else {
                    //progressBarUtil.showProgress(RegistrationActivity.this, false, mProgressView, mLoginFormView);
                    Toast.makeText(RegistrationActivity.this, getResources().getString(R.string.error_no_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });

       /* mCheckIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
            }
        });*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        client.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        client.disconnect();
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(RegistrationActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegistrationActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(RegistrationActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(RegistrationActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            locationManager.removeUpdates(this);
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //CheckEnableGPS();

        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION,LOCATION);
        askForPermission(Manifest.permission.RECEIVE_SMS,SMS);
        askForPermission(Manifest.permission.READ_CONTACTS,CONTACTS);


        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        //criteria.setCostAllowed(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                             int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //checkLocationPermission();
            // return;
        }else{
            locationManager.requestSingleUpdate(criteria, this, null);
        }

        //xlocationManager.requestSingleUpdate(criteria, this, null);


        //locationManager.requestSingleUpdate(criteria, this, null);

    }

    private void getCurrentLocation() {

        if(CheckEnableGPS()){
            //showProgress(true);

            if(isLocationReady){
                //showProgress(false);
                Log.v("Location details", currentLat + " " + currentLong);

                locationGotten();
                //Toast.makeText(this,"Location gotten: " + currentLat + " " + currentLong,Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(this,"Location not yet available, try again after some time",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            //requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                            askForPermission(Manifest.permission.READ_CONTACTS,CONTACTS);
                        }
                    });
        } else {
            //requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
            askForPermission(Manifest.permission.READ_CONTACTS,CONTACTS);
        }
        return false;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        try{
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

            // Reset errors.
            mFullName.setError(null);
            mPhoneNumber.setError(null);
            mEmailView.setError(null);
            mPasswordView.setError(null);
            mConfirmPasswordView.setError(null);

            // Store values at the time of the login attempt.
            String fullname = mFullName.getText().toString();
            String phone_number = mPhoneNumber.getText().toString();
            String email = mEmailView.getText().toString();
            String address = currentLat + "," + currentLong;
            last_checkin = currentLat + "," + currentLong;
            time_checkin = currentDateTimeString;

            String password = mPasswordView.getText().toString();
            String confirmPassword = mConfirmPasswordView.getText().toString();


            boolean cancel = false;
            View focusView = null;

            if (TextUtils.isEmpty(fullname)) {
                mFullName.setError(getString(R.string.error_field_required));
                focusView = mFullName;
                cancel = true;
            }

            if (TextUtils.isEmpty(phone_number)) {
                mPhoneNumber.setError(getString(R.string.error_field_required));
                focusView = mPhoneNumber;
                cancel = true;
            }

            // Check for a valid password, if the user entered one.
            if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = mPasswordView;
                cancel = true;
            }




            // Check for a valid email address.
            if (TextUtils.isEmpty(email)) {
                mEmailView.setError(getString(R.string.error_field_required));
                focusView = mEmailView;
                cancel = true;
            } else if (!isEmailValid(email)) {
                mEmailView.setError(getString(R.string.error_invalid_email));
                focusView = mEmailView;
                cancel = true;
            }

            if (mPasswordView.getText().toString().trim().isEmpty()) {
                MessageUtil.showAlert(this, "Invalid Password!", "Confirm Password cannot be empty");
                focusView = mPasswordView;
                cancel = true;
            }

            else if (mPasswordView.getText().toString().length() < 4) {
                MessageUtil.showAlert(this, "Password Too Short!", "Password must be greater than 4 characters");
                focusView = mPasswordView;
                cancel = true;
            }

            else if (!mPasswordView.getText().toString().trim().equals(mConfirmPasswordView.getText().toString())) {
                MessageUtil.showAlert(this, "Password Mismatch!", "Passwords don't match");
                focusView = mPasswordView;
                cancel = true;
            }

        /*if(isLocationReady){
            focusView = mCheckIn;
            cancel = true;
        }*/

            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                //showProgress(true);
                NiboUser niboUser = new NiboUser();
                niboUser.setFullName(mFullName.getText().toString());
                niboUser.setPhoneNumber(mPhoneNumber.getText().toString());
                niboUser.setEmail(mEmailView.getText().toString());
                niboUser.setAddress("NULL,NULL");
                niboUser.setLastCheckin(last_checkin);
                niboUser.setTimeCheckin(time_checkin);
                niboUser.setPassword(password);


           /* mAuthTask = new RegistrationTask(email, password);
            new RegistrationTask().execute(contactPojo);
            mAuthTask.execute((Void) null);*/

                new RegistrationTask().execute(niboUser);

                t.send(new HitBuilders.EventBuilder()
                        .setCategory("Clicks")  // category i.e. Buttons Action
                        .setAction("Button")    // action i.e.  Button Name
                        .setLabel("Register Button")    // label i.e.  any meta-data
                        .build());
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegistrationActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class RegistrationTask extends AsyncTask<NiboUser, Integer, Integer> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = ProgressDialog.show(RegistrationActivity.this, "Registering.", "Registering. Please wait...");

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
                    result = webService.registerUser(RegistrationActivity.this, niboUser);

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
                MessageUtil.showAlert(RegistrationActivity.this, "An Error Occurred!", "Error Creating User.");
            } else if(res == 1) {

                pref.createRegistrationSession();
                pref.createLoginSession();
                Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(RegistrationActivity.this, MainActivity.class);
                startActivity(in);
                finish();
            } else if(res == 2) {

                MessageUtil.showAlert(RegistrationActivity.this, "Phone Number Registered!", "Please register with another phone number.");
            }
        }

        @Override
        protected void onCancelled() {
            pd.dismiss();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        currentLat = String.valueOf(location.getLatitude());
        currentLong = String.valueOf(location.getLongitude());

        isLocationReady = true;

        //showProgress(false);

        locationGotten();

        Log.v("Location details", currentLat + " " + currentLong);
        //Toast.makeText(this,"Gbam!!!",Toast.LENGTH_SHORT).show();

    }

    private void locationGotten() {

        /*mCheckIn.setText("Location Gotten Succesfully");
        mCheckIn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        mCheckIn.setClickable(false);*/
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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
                                ActivityCompat.requestPermissions(RegistrationActivity.this,
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
            case LOCATION: {
                // If request is cancelled, the result arrays are empty.
               /* if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        locationManager.requestSingleUpdate(criteria, this, null);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;*/

                askForGPS();
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void askForGPS() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        result = LocationServices.SettingsApi.checkLocationSettings(client, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(RegistrationActivity.this, GPS_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    private boolean CheckEnableGPS(){
        boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(gps_enabled){

            return true;
            //GPS Enabled
            //Toast.makeText(this, "GPS is Enabled", Toast.LENGTH_LONG).show();
        }else{
            AlertDialog.Builder alertGpsEnabled = new AlertDialog.Builder(this);
            alertGpsEnabled.setTitle("GPS needed");
            alertGpsEnabled.setMessage("Please, you need to enable your GPS as it is a requirement for this application");
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
}