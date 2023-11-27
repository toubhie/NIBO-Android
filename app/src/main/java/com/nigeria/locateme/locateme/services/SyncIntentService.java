package com.nigeria.locateme.locateme.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.nigeria.locateme.locateme.entities.Contact;
import com.nigeria.locateme.locateme.entities.Preferences;
import com.nigeria.locateme.locateme.entities.UserLocation;
import com.nigeria.locateme.locateme.utils.ConnectionDetector;
import com.nigeria.locateme.locateme.utils.Constants;
import com.nigeria.locateme.locateme.utils.NotificationUtil;
import com.nigeria.locateme.locateme.utils.SQLController;
import com.nigeria.locateme.locateme.webservices.WebService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SyncIntentService extends IntentService {

    private ConnectionDetector connectionDetector;

    private Preferences pref;

    private List<UserLocation> userLocationList = new ArrayList<UserLocation>();

    DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

    int count = 0;


    public SyncIntentService() {
        super("SyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try{
            // Release the wake lock provided by the WakefulBroadcastReceiver.
            WakefulBroadcastReceiver.completeWakefulIntent(intent);

            Log.i(Constants.TAG_NIBO, "In intent service...starting service");

            Log.i(Constants.TAG_NIBO, "context here: " + getApplicationContext());

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            connectionDetector = new ConnectionDetector(getApplicationContext());
            pref = new Preferences(getApplicationContext());

            try{
                if(connectionDetector.isURLReachable(getApplicationContext())) {
                    Log.i(Constants.TAG_NIBO, "BC Service Running");


                    Log.i(Constants.TAG_NIBO, "NO OF SAVED LOCATIONS TO BE SYNCED: " + getUserLocationsCount(getApplicationContext()));

                    // Get list of user received contacts from sqlite
                    SQLController sqlController = new SQLController(getApplicationContext());
                    userLocationList = sqlController.getAllSavedLocationsFromSQLiteDB();

                    if(userLocationList.size() != 0) {
                        Log.i(Constants.TAG_NIBO, "Sync needed");

                        for (UserLocation userLocation : userLocationList) {

                            Log.i(Constants.TAG_NIBO, "SyncedToOnlineDBStatus : " + userLocation.getIsSyncedToOnlineDBStatus());

                            if (userLocation.getIsSyncedToOnlineDBStatus().equalsIgnoreCase("TRUE")) {
                                // Record has already been synced before and already exist online so just update record

                                Log.i(Constants.TAG_NIBO, "UPDATING RECORD");

                                updateSavedLocationRecordUsingMySQLId(userLocation);

                                Log.i(Constants.TAG_NIBO, "DONE UPDATING. FINISHING...");

                                //saveSyncDateAndShowNotification(getApplicationContext());

                            } else if (userLocation.getIsSyncedToOnlineDBStatus().equalsIgnoreCase("FALSE")) {
                                // Record has not been synced before and does not exist online so just insert new record

                                Log.i(Constants.TAG_NIBO, "INSERTING RECORD");

                                insertNewSavedLocationRecordToMySQLDB(userLocation);

                                Log.i(Constants.TAG_NIBO, "DONE INSERTING. FINISHING...");

                            } else {
                                // Record has not been synced before and does not exist online so just insert new record

                                Log.i(Constants.TAG_NIBO, "UPDATING RECORD");

                                updateSavedLocationRecordUsingMySQLId(userLocation);

                                Log.i(Constants.TAG_NIBO, "DONE INSERTING. FINISHING...");
                            }
                        }

                        saveSyncDateAndShowNotification(getApplicationContext());

                    } else {
                        Log.i(Constants.TAG_NIBO, "Saved Location list empty. No sync needed");
                    }

                } else {
                    Log.i(Constants.TAG_NIBO, "No internet connection");
                }
            } catch (Exception e){
                e.printStackTrace();
            }

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void updateSavedLocationRecordUsingMySQLId(final UserLocation userLocation){
        try{
            new UpdateNewContactToServerTask().execute(userLocation);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void insertNewSavedLocationRecordToMySQLDB(final UserLocation userLocation){
        try{
            Log.i(Constants.TAG_NIBO, "inside ist mtd sdl: " + userLocation);

            new InsertNewContactToServerTask().execute(userLocation);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public class InsertNewContactToServerTask extends AsyncTask<UserLocation, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(Constants.TAG_NIBO, "pre execute");
        }

        @Override
        protected Integer doInBackground(UserLocation... params) {
            int result = 0;

            try {
                UserLocation userLocation = params[0];


                Log.i(Constants.TAG_NIBO, "in doInBg: " + userLocation);
                Log.i(Constants.TAG_NIBO, "before web service");

                WebService webService = new WebService();
                result = webService.insertNewSavedLocationToServer(getApplicationContext(), userLocation);

                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            if(result == 0){
                Log.i(Constants.TAG_NIBO, "userLocation not synced (insert)");
            }

            else if(result == 1){
                Log.i(Constants.TAG_NIBO, "userLocation synced successfully (insert)");

                count++;
            }
        }
    }

    public class UpdateNewContactToServerTask extends AsyncTask<UserLocation, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(UserLocation... params) {
            UserLocation userLocation = params[0];
            int result = 0;

            try {

                WebService webService = new WebService();
                result = webService.updateLocationToServer(getApplicationContext(), userLocation);

                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            if(result == 0){
                Log.i(Constants.TAG_NIBO, "userLocation not synced (update)");
            }

            else if(result == 1){
                Log.i(Constants.TAG_NIBO, "userLocation synced successfully (update)");

                count++;
            }
        }
    }

    public void saveSyncDateAndShowNotification(Context context){
        try{
            // Finishing
            Date date = new Date();
            String lastSyncDate = dateFormat.format(date);

            Log.i(Constants.TAG_NIBO, "Date of last sync : " + lastSyncDate);

            pref.saveLastSyncDate(lastSyncDate);

            NotificationUtil notificationUtil = new NotificationUtil(context);
            notificationUtil.showCustomNotification(context, "Sync Complete", "Sync Complete", "Your contact cards have been synced successfully.");

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getUserLocationsCount(Context context){
        SQLController sqlController = new SQLController(context);
        int count = sqlController.getSavedLocationsCountForUser();

        return String.valueOf(count);
    }
}
