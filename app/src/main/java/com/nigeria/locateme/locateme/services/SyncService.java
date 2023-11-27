package com.nigeria.locateme.locateme.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.nigeria.locateme.locateme.R;
import com.nigeria.locateme.locateme.activities.MainActivity;
import com.nigeria.locateme.locateme.utils.Constants;

public class SyncService extends Service {
    public SyncService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
       // Toast.makeText(this, "Service was Created", Toast.LENGTH_LONG).show();


    }

    @Override
    public void onStart(Intent intent, int startId) {
       // Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        try {
            Intent resultIntent = new Intent(this, MainActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                    resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mNotifyBuilder;
            NotificationManager mNotificationManager;
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Sets an ID for the notification, so it can be updated
            int notifyID = 9001;

            mNotifyBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle("Alert")
                    .setContentText("A Saved Location has been updated")
                    .setSmallIcon(R.drawable.logo);

            // Set pending intent
            mNotifyBuilder.setContentIntent(resultPendingIntent);
            // Set Vibrate, Sound and Light

            int defaults = 0;
            defaults = defaults | Notification.DEFAULT_LIGHTS;
            defaults = defaults | Notification.DEFAULT_VIBRATE;
            defaults = defaults | Notification.DEFAULT_SOUND;

            mNotifyBuilder.setDefaults(defaults);
            // Set the content for Notification

            mNotifyBuilder.setContentText(intent.getStringExtra(Constants.TAG_SYNC_INTENT_DATA));
            // Set autocancel

            mNotifyBuilder.setAutoCancel(true);
            // Post a notification

            mNotificationManager.notify(notifyID, mNotifyBuilder.build());
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

    }
}
