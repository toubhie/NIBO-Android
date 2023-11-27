package com.nigeria.locateme.locateme.receivers;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.nigeria.locateme.locateme.R;
import com.nigeria.locateme.locateme.activities.FindFriendActivity;
import com.nigeria.locateme.locateme.utils.Constants;



public class ListeningToSMS extends BroadcastReceiver {

    private String name;
    private String phone;
    private String email;
    private String address;
    private String sender;


    private int notificationId = 100;

    private NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        try {

            Log.i(Constants.TAG_NIBO, "SMS Listener is Listening");

            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                             int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            if (intent.getAction().equals(Constants.SMS_RECEIVED)) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    // get sms objects
                    Object[] pdus = (Object[]) bundle.get("pdus");

                    /*if (pdus.length == 0) {
                        return;
                    }*/

                    // large message might be broken into many
                    SmsMessage[] fullMessage = new SmsMessage[pdus.length];
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < pdus.length; i++) {
                        fullMessage[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        sb.append(fullMessage[i].getMessageBody().toString());
                    }
                    sender = fullMessage[0].getOriginatingAddress().toString();
                    String message = sb.toString();

                    String splittedMessage[] = message.split(":");


                    // the arrray of the message will be ispon,name of user, latitude, longitude. and other things in case
                    if (splittedMessage[0].equalsIgnoreCase(Constants.TAG_NIBO_PREFIX)) {
                        // prevent any other broadcast receivers from receiving broadcast. Like cases when it is an ispon message, so others would not be able to
                        abortBroadcast();

                        Log.i(Constants.TAG_NIBO, "Nibo message entered");

                        try {
                            String contactMessage[] = splittedMessage[1].split(",");

                            Log.i(Constants.TAG_GBACARD, "contactMessage[0]: " +contactMessage[0]);
                            Log.i(Constants.TAG_GBACARD, "contactMessage[1]: " +contactMessage[1]);


                            String niboNumber = contactMessage[0];









                            displayNotificationInActivity(context, niboNumber);
                        } catch (Exception e) {
                            e.printStackTrace();
                            //Toast.makeText(context, name + phone + email + " Contact not saved!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }}
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void displayNotificationInActivity(Context context, String niboNumber) {

        try {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
            ringtone.play();
        } catch (Exception e) {
            e.printStackTrace();
        }


        //Calling the default notification service
        NotificationCompat.Builder myBuilder = new NotificationCompat.Builder(context);
        myBuilder.setTicker("Nibo Number Gotten"); //this is the first text that is shown to users b4 notification bar is drawn down
        myBuilder.setSmallIcon(R.drawable.logo);
        myBuilder.setContentTitle("You have received a new nibo number");
        myBuilder.setContentText("Click here to view this person's location");
        myBuilder.setAutoCancel(true);
        //myBuilder.addAction(R.drawable.ic_launcher, "", pIntent);//it automatically cancels when a user clicks on it
        //myBuilder.setDefaults(Notification.DEFAULT_SOUND);


        Intent resultIntent = new Intent(context, FindFriendActivity.class);
        resultIntent.putExtra(Constants.UNIQUE_CODE, niboNumber);



        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        myBuilder.setContentIntent(pendingIntent);


        //and finally use the notificationManger to build the whole process
        //notification id allows you update the notification later
        notificationManager.notify(++notificationId, myBuilder.build());

    }
}
