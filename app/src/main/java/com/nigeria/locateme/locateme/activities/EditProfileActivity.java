package com.nigeria.locateme.locateme.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nigeria.locateme.locateme.R;
import com.nigeria.locateme.locateme.entities.NiboUser;
import com.nigeria.locateme.locateme.entities.Preferences;
import com.nigeria.locateme.locateme.utils.ConnectionDetector;
import com.nigeria.locateme.locateme.utils.MessageUtil;
import com.nigeria.locateme.locateme.utils.ProgressBarUtil;
import com.nigeria.locateme.locateme.webservices.WebService;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.DateFormat;
import java.util.Date;

public class EditProfileActivity extends AppCompatActivity {

    private AutoCompleteTextView mEmailView;
    private EditText mFullName, mPhoneNumber;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mEmailSignInButton;


    private Preferences pref;

    private ProgressBarUtil progressBarUtil;

    private ConnectionDetector connectionDetector;
    private AVLoadingIndicatorView loadingIndicatorView;

    private NiboUser phoneCon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        pref = new Preferences(this);
        connectionDetector = new ConnectionDetector(getApplicationContext());
        progressBarUtil = new ProgressBarUtil();

        phoneCon = pref.getUserDetails();


        setUpViews();
        setUpListeners();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);


    }


    private void setUpViews(){

        String fullName,number,email,password;

        fullName = phoneCon.getFullName();
        number = phoneCon.getPhoneNumber();
        email = phoneCon.getEmail();
        password = phoneCon.getPassword();



        mFullName = (EditText) findViewById(R.id.fullname);
        mFullName.setText(fullName);
        mPhoneNumber = (EditText) findViewById(R.id.phone_no);
        mPhoneNumber.setText(number);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setText(password);
        mConfirmPasswordView = (EditText) findViewById(R.id.confirmPassword);
        mConfirmPasswordView.setText(password);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mEmailView.setText(email);
        mLoginFormView = findViewById(R.id.login_form);
        loadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.progress_bar);
        //mProgressView = findViewById(R.id.login_progress);

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

    }

    private void setUpListeners(){
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectionDetector.isURLReachable(EditProfileActivity.this)) {
                    attemptLogin();
                } else {
                    //progressBarUtil.showProgress(RegistrationActivity.this, false, mProgressView, mLoginFormView);
                    Toast.makeText(EditProfileActivity.this, getResources().getString(R.string.error_no_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

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
                NiboUser phoneCon = pref.getUserDetails();
                String number = phoneCon.getPhoneNumber();

                NiboUser niboUser = new NiboUser();
                niboUser.setOldPhoneNumber(number);
                niboUser.setFullName(mFullName.getText().toString());
                niboUser.setPhoneNumber(mPhoneNumber.getText().toString());
                niboUser.setEmail(mEmailView.getText().toString());
                niboUser.setPassword(password);


           /* mAuthTask = new RegistrationTask(email, password);
            new RegistrationTask().execute(contactPojo);
            mAuthTask.execute((Void) null);*/

                new EditProfileTask().execute(niboUser);
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

    public class EditProfileTask extends AsyncTask<NiboUser, Integer, Integer> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = ProgressDialog.show(EditProfileActivity.this, "Updating Profile.", "Updating Profile. Please wait...");

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
                    result = webService.editUserProfile(EditProfileActivity.this, niboUser);

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
                MessageUtil.showAlert(EditProfileActivity.this, "An Error Occurred!", "Error Updating Profile.");
            } else if(res == 1) {

                //pref.createRegistrationSession();
                //pref.saveUserDetails(locateMeUser);
                Toast.makeText(getApplicationContext(), "Profile Update Successful", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(EditProfileActivity.this, EditProfileActivity.class);
                startActivity(in);
                finish();
            } else if(res == 2) {

                MessageUtil.showAlert(EditProfileActivity.this, "Phone Number Exists!", "Please register with another phone number.");//this will not come up ever
            }
        }

        @Override
        protected void onCancelled() {
            pd.dismiss();
        }
    }

}
