package com.nigeria.locateme.locateme.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by Williamz on 14-Jul-16.
 */
public class MessageUtil {

    public static void showAlert(final Context context, final String title, final CharSequence text) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(title);
                builder.setMessage(text);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
    }



    public static void showShortToast(Context context, String message) {
        // TODO Auto-generated method stub
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, String message) {
        // TODO Auto-generated method stub
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}