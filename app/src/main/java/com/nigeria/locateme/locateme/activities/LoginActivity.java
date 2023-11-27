package com.nigeria.locateme.locateme.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nigeria.locateme.locateme.R;
import com.nigeria.locateme.locateme.entities.NiboUser;
import com.nigeria.locateme.locateme.entities.Preferences;
import com.nigeria.locateme.locateme.utils.ConnectionDetector;
import com.nigeria.locateme.locateme.utils.Constants;
import com.nigeria.locateme.locateme.utils.MessageUtil;
import com.nigeria.locateme.locateme.utils.ProgressBarUtil;
import com.nigeria.locateme.locateme.utils.SQLController;
import com.nigeria.locateme.locateme.webservices.WebService;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;

    private View mProgressView;
    private View mLoginFormView;
    private AVLoadingIndicatorView loadingIndicatorView;

    private TextView signInButton;
    private TextView signUpButton;
    private TextView forgotPassword;

    private EditText mEmailView;
    private EditText mPasswordView;

    private CheckBox showPasswordCheckBox;

    private ProgressBarUtil progressBarUtil;

    private Preferences preferences;

    private ConnectionDetector connectionDetector;

    private SQLController sqlController;

    Tracker t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        MyAnalytics application = (MyAnalytics) getApplication();
        t = application.getDefaultTracker();
        t.setScreenName("Login Activity");

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        setUpViews();
        setUpListeners();

        progressBarUtil = new ProgressBarUtil();

        sqlController = new SQLController(this);

        connectionDetector = new ConnectionDetector(getApplicationContext());

        preferences = new Preferences(getApplicationContext());
    }

    public void setUpViews(){
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        signInButton = (TextView) findViewById(R.id.sign_in_button);
        signUpButton = (TextView) findViewById(R.id.register_text);
        forgotPassword = (TextView) findViewById(R.id.forgotPassword);

        showPasswordCheckBox = (CheckBox) findViewById(R.id.showPassword);

        mLoginFormView = findViewById(R.id.container);
        //loadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.progress_bar);
        mProgressView = findViewById(R.id.loading_frame);

        preferences = new Preferences(getApplicationContext());
    }

    public void setUpListeners(){
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBarUtil.showProgress(LoginActivity.this, true, mProgressView, mLoginFormView);
                checkPermissionsBeforeLogin();
            }
        });


        signUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBarUtil.showProgress(LoginActivity.this, true, mProgressView, mLoginFormView);
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);

                progressBarUtil.showProgress(LoginActivity.this, false, mProgressView, mLoginFormView);
                startActivity(intent);

            }
        });

        showPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // checkbox status is changed from uncheck to checked.
                if (!isChecked) {
                    // show password
                    mPasswordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    mPasswordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        forgotPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean isPhoneNumberValid(String phoneNumberOrEmail) {
        //TODO: Replace this with your own logic
        return phoneNumberOrEmail.matches("(\\+[0-9]+[\\-\\.]*)?(\\([0-9]+\\)[\\- \\.]*)?([0-9][0-9\\- \\.]+[0-9])");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() < 4;
    }

    private boolean valid() {
        boolean valid = true;

        if (!mEmailView.getText().toString().trim().matches("(\\+[0-9]+[\\-\\.]*)?(\\([0-9]+\\)[\\- \\.]*)?([0-9][0-9\\- \\.]+[0-9])") &&
                !mEmailView.getText().toString().trim().matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {

            mEmailView.setError(getString(R.string.error_invalid_email));

            valid = false;
        }

        else if (mPasswordView.getText().toString().trim().length() < 4) {
            MessageUtil.showAlert(this, "Password Too Short", "Password must be greater than 4 characters");
            valid = false;
        }

        if(!valid){
            progressBarUtil.showProgress(LoginActivity.this, false, mProgressView, mLoginFormView);
        }

        return valid;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void signInWithEmail() {
        /*if (mAuthTask != null) {
            progressBarUtil.showProgress(LoginActivity.this, false, mProgressView, mLoginFormView);
            return;
        }*/

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid Or Email.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            progressBarUtil.showProgress(LoginActivity.this, false, mProgressView, mLoginFormView);
            focusView.requestFocus();
        } else {
            NiboUser niboUser = new NiboUser();
            niboUser.setEmail(email);
            niboUser.setPassword(password);

            // perform the user login attempt.
            new UserLoginTask().execute(niboUser);

            t.send(new HitBuilders.EventBuilder()
                    .setCategory("Clicks")  // category i.e. Buttons Action
                    .setAction("Button")    // action i.e.  Button Name
                    .setLabel("Login Button")    // label i.e.  any meta-data
                    .build());
        }
    }

    public class UserLoginTask extends AsyncTask<NiboUser, Integer, Integer> {

        @Override
        protected Integer doInBackground(NiboUser... params) {
            NiboUser niboUser = params[0];
            int result = 0;

            WebService webService = new WebService();

            try {
                result = webService.loginUser(LoginActivity.this, niboUser);

                System.out.println("Result from login: " + result);

                if(result == 1){
                    getAllUsersSavedLocations();
                }

                return result;
            }catch(Exception e){
                System.out.println("Exception : " + e.getMessage());
            }

            // TODO: register the new account here.
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            mAuthTask = null;
            progressBarUtil.showProgress(LoginActivity.this, false, mProgressView, mLoginFormView);

            if(result == 0){
                mPasswordView.setText("");
                mPasswordView.requestFocus();

                Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_sign_in), Toast.LENGTH_SHORT).show();
            }

            else if(result == 1){
                preferences.createLoginSession();
            }

            if(result == 2){
                mPasswordView.setText("");
                mPasswordView.requestFocus();

                MessageUtil.showAlert(LoginActivity.this, "Invalid Credentials", "The username or password you entered doesn't match.");
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            progressBarUtil.showProgress(LoginActivity.this, false, mProgressView, mLoginFormView);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void getAllUsersSavedLocations(){
        progressBarUtil.showProgress(LoginActivity.this, true, mProgressView, mLoginFormView);

        if (connectionDetector.isURLReachable(LoginActivity.this)) {

            // Clear Contents in table first
            try{
                sqlController.deleteAllUserRecordsFromTable();
            } catch (Exception e){
                e.printStackTrace();
            }

            new GetAllUsersSavedLocationsTask().execute();

        } else {
            progressBarUtil.showProgress(LoginActivity.this, false, mProgressView, mLoginFormView);
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public class GetAllUsersSavedLocationsTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            int result = 0;

            try {
                WebService webService = new WebService();
                result = webService.getAllUserSavedLocations(LoginActivity.this);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            progressBarUtil.showProgress(LoginActivity.this, false, mProgressView, mLoginFormView);

            if(result == 0){
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_sign_in), Toast.LENGTH_SHORT).show();
            }

            else if(result == 1){
                int count = sqlController.getSavedLocationsCountForUser();

                Log.i(Constants.TAG_NIBO, "Records gotten...count : " + count);


                preferences.createLoginSession();

                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }

            else if(result == 2){
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_user_not_found), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkPermissionsBeforeLogin() {

        Log.i(Constants.TAG_NIBO, "checking Permissions");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            List<String> permissionsNeeded = new ArrayList<String>();

            final List<String> permissionsList = new ArrayList<String>();

            if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
                permissionsNeeded.add("Access your Location");
            if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
                permissionsNeeded.add("Read Contacts");
            /*if (!addPermission(permissionsList, Manifest.permission.RECEIVE_SMS))
                permissionsNeeded.add("SMS");*/

            if (permissionsList.size() > 0) {

                Log.i(Constants.TAG_NIBO, "Permissions Needed");

                if (permissionsNeeded.size() > 0) {

                    // Need Rationale
                    String message = "You need to grant access to " + permissionsNeeded.get(0);
                    for (int i = 1; i < permissionsNeeded.size(); i++)
                        message = message + ", " + permissionsNeeded.get(i);
                    showMessageOKCancel(message,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                                Constants.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                    }
                                }
                            });
                    return true;
                }
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        Constants.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                return true;
            }

            Log.i(Constants.TAG_NIBO, "Performing Login");
            performLogin();
        }

        Log.i(Constants.TAG_NIBO, "Permissions Not Needed");
        Log.i(Constants.TAG_NIBO, "Performing Login");
        performLogin();
        return false;
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (!shouldShowRequestPermissionRationale(permission))
                    return false;
            }
        }

        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LoginActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                /*perms.put(Manifest.permission.RECEIVE_SMS, PackageManager.PERMISSION_GRANTED);*/

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION

                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                        /*&& perms.get(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED*/) {

                    //All Permissions Granted?
                    Log.i(Constants.TAG_NIBO, "Performing Login");
                    performLogin();

                } else {
                    // Permission Denied
                    Toast.makeText(LoginActivity.this, "Some Permission is still Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void performLogin(){
        if (connectionDetector.isURLReachable(LoginActivity.this)) {
            Log.i(Constants.TAG_NIBO, "Login with email");
            if (valid()) {
                signInWithEmail();
            } else{
                progressBarUtil.showProgress(LoginActivity.this, false, mProgressView, mLoginFormView);
            }
        } else {
            progressBarUtil.showProgress(LoginActivity.this, false, mProgressView, mLoginFormView);
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_no_connection), Toast.LENGTH_SHORT).show();
        }

    }
}

