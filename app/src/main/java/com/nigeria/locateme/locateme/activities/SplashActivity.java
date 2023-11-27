package com.nigeria.locateme.locateme.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.nigeria.locateme.locateme.R;
import com.nigeria.locateme.locateme.entities.Preferences;

public class SplashActivity extends AppCompatActivity {

    private Preferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pref = new Preferences(getApplicationContext());

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {





                if (pref.isRegistered() && pref.isLoggedIn()) {

                    Intent intent= new Intent (SplashActivity.this, MainActivity.class);
                    startActivity(intent);

                    // close this activity
                    finish();

                } else if(pref.isLoggedIn()){

                    Intent intent= new Intent (SplashActivity.this, MainActivity.class);
                    startActivity(intent);

                    // close this activity
                    finish();

                }else {
                    Intent i = new Intent(SplashActivity.this, WizardActivity.class);
                    startActivity(i);
                    // close this activity
                    finish();
                }
            }
        }, 1500);

    }
}
