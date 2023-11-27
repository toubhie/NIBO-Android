package com.nigeria.locateme.locateme.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.nigeria.locateme.locateme.entities.Preferences;
import com.nigeria.locateme.locateme.services.SyncIntentService;
import com.nigeria.locateme.locateme.utils.Constants;

public class SyncBroadcastReceiver extends WakefulBroadcastReceiver {

    private Preferences pref;

    public SyncBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        pref = new Preferences(context);

        if(pref.isLoggedIn()) {
            Log.i(Constants.TAG_NIBO, "In wakeful broadcast receiver...setting up service");

            //Start Sync Service
            Intent startServiceIntent = new Intent(context, SyncIntentService.class);
            startWakefulService(context, startServiceIntent);

        } else{
            Log.i(Constants.TAG_NIBO, "In wakeful broadcast receiver...stopping service...not logged in");

            context.stopService(intent);
        }

    }
}
