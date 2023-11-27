package com.nigeria.locateme.locateme.activities;

import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nigeria.locateme.locateme.R;
import com.nigeria.locateme.locateme.dialogs.AlertDialogRadio;
import com.nigeria.locateme.locateme.entities.NiboUser;
import com.nigeria.locateme.locateme.entities.Preferences;
import com.nigeria.locateme.locateme.entities.SyncInterval;
import com.nigeria.locateme.locateme.receivers.SyncBroadcastReceiver;
import com.nigeria.locateme.locateme.utils.Constants;
import com.nigeria.locateme.locateme.utils.DisplayUtil;
import com.nigeria.locateme.locateme.utils.MessageUtil;
import com.nigeria.locateme.locateme.utils.ProgressBarUtil;
import com.nigeria.locateme.locateme.webservices.WebService;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity implements AlertDialogRadio.AlertPositiveListener {

    private TextView editProfile;
    private TextView editHomeNiboCode;
    private TextView syncNow;
    private TextView lastSyncDate;
    private TextView setSyncInterval;
    private TextView signIn;
    private TextView signOut;

    private FrameLayout mProgressView;
    private CoordinatorLayout container;

    private NiboUser niboUser,phoneCon;

    private Preferences pref;

    private ProgressBarUtil progressBarUtil;

    private DisplayUtil displayUtil;
    String code,newNiboCode;

    /** Stores the selected item's position */
    int position = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setUpViews();
        setUpListeners();
        determineSignInOrSignOut();

        lastSyncDate.setText(getLastSyncDate());

        progressBarUtil = new ProgressBarUtil();

        displayUtil = new DisplayUtil();

        displayUtil.setStatusBarColor(this,R.color.colorPrimaryDark);
    }

    private void setUpViews() {
        editProfile = (TextView) findViewById(R.id.setting_edit_profile);
        editHomeNiboCode = (TextView) findViewById(R.id.setting_edit_home);
        signIn = (TextView) findViewById(R.id.setting_signIn);
        signOut = (TextView) findViewById(R.id.setting_signOut);
        syncNow = (TextView) findViewById(R.id.setting_sync_now);
        lastSyncDate = (TextView) findViewById(R.id.setting_last_date_of_sync);
        setSyncInterval = (TextView) findViewById(R.id.setting_set_sync_interval);

        container = (CoordinatorLayout) findViewById(R.id.container);
        mProgressView = (FrameLayout) findViewById(R.id.loading_frame);

        pref = new Preferences(getApplicationContext());

        phoneCon = pref.getUserDetails();

        niboUser = new NiboUser();

        niboUser = pref.getUserDetails();
    }

    private void setUpListeners() {
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, EditProfileActivity.class);
                intent.putExtra(Constants.TAG_USER_DETAIL, niboUser);
                startActivity(intent);
                //overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });

        editHomeNiboCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editHouseCode();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarUtil.showProgress(SettingsActivity.this, true, mProgressView, container);

                try {
                    showSignOutDialog();
                    determineSignInOrSignOut();
                } catch (Exception e){
                    progressBarUtil.showProgress(SettingsActivity.this, false, mProgressView, container);
                    e.printStackTrace();
                }
            }
        });

        syncNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpSync();
            }
        });

        setSyncInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();

                /** Instantiating the DialogFragment class */
                AlertDialogRadio alert = new AlertDialogRadio();

                /** Creating a bundle object to store the selected item's index */
                Bundle b  = new Bundle();

                /** Storing the selected item's index in the bundle object */
                b.putInt(Constants.TAG_SYNC_FREQUENCY_POSITION, position);

                /** Setting the bundle object to the dialog fragment object */
                alert.setArguments(b);

                /** Creating the dialog fragment object, which will in turn open the alert dialog window */
                alert.show(manager, "alert_dialog_radio");
            }
        });
    }

    /** Defining button click listener for the OK button of the alert dialog window */
    @Override
    public void onPositiveClick(int position) {
        this.position = position;

        Log.i(Constants.TAG_GBACARD, "Your Choice : " + SyncInterval.intervals[this.position]);

        Log.i(Constants.TAG_GBACARD, "Your Choice in time : " + SyncInterval.intervalTimeInMilliSecs[this.position]);

        // Saving the selected choice in pref
        setSyncInterval(SyncInterval.intervalTimeInMilliSecs[this.position]);
    }

    public void editHouseCode(){
        //check Internet Connectivity
        code = phoneCon.getAddress();

        saveNiboHouseCodeDialog();

    }


    public void saveNiboHouseCodeDialog(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.enter_nibo_code_layout, null);
        dialogBuilder.setView(dialogView);

        final EditText nameBoxInDialog = (EditText) dialogView.findViewById(R.id.edtnibocode);

        nameBoxInDialog.setText(code);


        dialogBuilder.setTitle("House Nibo Code");
        dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                newNiboCode = nameBoxInDialog.getText().toString().trim();

                if (newNiboCode.length() < 2 || newNiboCode.contentEquals(" ")) {
                    Toast.makeText(getApplicationContext(), "Please provide a Valid Nibo Code", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), newNiboCode.length()+"", Toast.LENGTH_SHORT).show();
                    saveNiboHouseCodeDialog();
                }
                else{
                    //dialog.dismiss();
                    //save to database

                    //Check Internet Connection





                    String number = phoneCon.getPhoneNumber();

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

    public class EditNiboHouseCodeTask extends AsyncTask<NiboUser, Integer, Integer> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = ProgressDialog.show(SettingsActivity.this, "Updating House Nibo Code.", "Updating. Please wait...");

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
                    result = webService.editNiboHouseCode(SettingsActivity.this, niboUser);

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
                MessageUtil.showAlert(SettingsActivity.this, "An Error Occurred!", "Error Updating Profile.");
            } else if(res == 1) {

                //pref.createRegistrationSession();
                //pref.saveUserDetails(locateMeUser);
                pref.saveHouseUniqueCode(newNiboCode);
                Toast.makeText(getApplicationContext(), "Nibo Code Update Successful", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(SettingsActivity.this, SettingsActivity.class);
                startActivity(in);
                finish();
            } else if(res == 2) {

                MessageUtil.showAlert(SettingsActivity.this, "Phone Number Exists!", "Please register with another phone number.");//this will not come up ever
            }
        }

        @Override
        protected void onCancelled() {
            pd.dismiss();
        }
    }

    private void determineSignInOrSignOut() {
        if (pref.isLoggedIn()) {
            signIn.setVisibility(View.GONE);
            signOut.setVisibility(View.VISIBLE);
        } else {
            signIn.setVisibility(View.VISIBLE);
            signOut.setVisibility(View.GONE);
        }
    }

    public void showSignOutDialog() {
        SettingsActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Warning");
                builder.setMessage("Are sure you want to Sign Out?")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                signOutUser();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void signOutUser(){
        new SignOutTask().execute();
    }

    class SignOutTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            try {
                progressBarUtil.showProgress(SettingsActivity.this, false, mProgressView, container);

                pref.logoutUser();

                setSyncInterval(SyncInterval.intervalTimeInMilliSecs[0]);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String args) {
            // dismiss the dialog after getting all products
            progressBarUtil.showProgress(SettingsActivity.this, false, mProgressView, container);

            // exit app
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    public void setSyncInterval(long syncFrequency){
        pref.setSyncInterval(syncFrequency);
    }

    public String getLastSyncDate(){

        String lastSyncDate = "Never";

        try{
            if(pref.getLastSyncDate() != null && !pref.getLastSyncDate().equalsIgnoreCase("")){
                lastSyncDate = pref.getLastSyncDate();

                return lastSyncDate;
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return lastSyncDate;
    }

    private void setUpSync(){
        Log.i(Constants.TAG_GBACARD, "Setting up sync");
        pref.checkLogin();

        if(pref.isLoggedIn()){
            new BroadCastReceiverTask().execute();
        }

    }

    public class BroadCastReceiverTask extends AsyncTask<String, String, String> {

        ProgressDialog pDialog;

        @Override
        public void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(SettingsActivity.this);
            pDialog.setMessage("Syncing. Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        public String doInBackground(String... params) {
            try {
                Log.i(Constants.TAG_GBACARD, "SYNC STARTED!!!!");

                long syncFrequency = pref.getSyncInterval();

                // BroadCase Receiver Intent Object
                Intent alarmIntent = new Intent(SettingsActivity.this, SyncBroadcastReceiver.class);
                // Pending Intent Object
                PendingIntent pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                // Alarm Manager Object
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                Log.i(Constants.TAG_GBACARD, "syncFrequency: " + syncFrequency);

                // Start now and Repeat every 24hrs
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), syncFrequency, pendingIntent);

            } catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);

            pDialog.dismiss();
        }
    }
}
