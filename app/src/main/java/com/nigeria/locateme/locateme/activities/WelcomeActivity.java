package com.nigeria.locateme.locateme.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nigeria.locateme.locateme.R;

public class WelcomeActivity extends AppCompatActivity {


    private Button searchNiboCode,signIn,scanNiboQr;

    Tracker t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        MyAnalytics application = (MyAnalytics) getApplication();
        t = application.getDefaultTracker();
        t.setScreenName("Welcome Activity");
        t.send(new HitBuilders.AppViewBuilder().build());


        searchNiboCode = (Button)findViewById(R.id.butSearchNiboCodeAct);
        signIn = (Button)findViewById(R.id.butSignIn);
        scanNiboQr = (Button)findViewById(R.id.butScanNiboCodeAct);


        searchNiboCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent (WelcomeActivity.this, FindFriendActivity.class);
                startActivity(intent);

                t.send(new HitBuilders.EventBuilder()
                        .setCategory("Clicks")  // category i.e. Buttons Action
                        .setAction("Search Button")    // action i.e.  Button Name
                        .setLabel("Clicked")    // label i.e.  any meta-data
                        .build());
            }
        });


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent (WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        scanNiboQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent (WelcomeActivity.this, ScanNiboQRActivity.class);
                startActivity(intent);
            }
        });

    }
}
