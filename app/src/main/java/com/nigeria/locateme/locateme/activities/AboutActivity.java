package com.nigeria.locateme.locateme.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.nigeria.locateme.locateme.R;
import com.nigeria.locateme.locateme.utils.DisplayUtil;

public class AboutActivity extends AppCompatActivity {

    ImageView facebookbtn,instagrambtn,twitterbtn,googlebtn,websitebtn;
    String Facebook = "com.facebook.katana";
    String Twitter = "com.twitter.android";
    String Instagram = "com.instagram.android";
    String Google = "com.google.android.apps.plus";

    private DisplayUtil displayUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        facebookbtn = (ImageView)findViewById(R.id.facebookbtn);

        instagrambtn = (ImageView)findViewById(R.id.instagrambtn);

        twitterbtn = (ImageView)findViewById(R.id.twitterbtn);

        googlebtn = (ImageView)findViewById(R.id.googlebtn);

        websitebtn = (ImageView)findViewById(R.id.websitebtn);


        facebookbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharingToSocialMedia(Facebook);
            }
        });


        instagrambtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharingToSocialMedia(Instagram);
            }
        });


        twitterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharingToSocialMedia(Twitter);
            }
        });


        googlebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharingToSocialMedia(Google);
            }
        });

        websitebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://niboapp.com/")));
            }
        });


        displayUtil = new DisplayUtil();

        displayUtil.setStatusBarColor(this,R.color.colorPrimaryDark);
    }



    public void SharingToSocialMedia(String application) {

        Intent intent = new Intent();
        //intent.setAction(Intent.ACTION_VIEW);
        //intent.setType("image/*");
        //intent.putExtra(Intent.EXTRA_STREAM, bmpUri);

        boolean installed = checkAppInstall(application);
        if (installed) {
            //intent.setPackage(application);
            //startActivity(intent);

            if(application.equals("com.facebook.katana")){
                String facebookScheme = "fb://page/120407185260764";
                //intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookScheme));
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(facebookScheme)));
            }else if(application.equals("com.twitter.android")){
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("twitter://user?screen_name=" + "niboapp")));
            }else if(application.equals("com.instagram.android")){

                String instagramScheme = "http://instagram.com/_u/niboapp";
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(instagramScheme)));

                /*intent.setComponent(new ComponentName("com.instagram.android", "com.instagram.android.activity.UrlHandlerActivity"));
                        intent.putExtra("customAppUri", "109387839048857856165");
                startActivity(intent);*/
            }else if(application.equals("com.google.android.apps.plus")){
                /*intent.setClassName("com.google.android.apps.plus", "com.google.android.apps.plus.phone.UrlGatewayActivity");
                intent.putExtra("customAppUri", "109387839048857856165");
                startActivity(intent);*/

                intent.setData(Uri.parse("https://plus.google.com/109387839048857856165/"));
                intent.setPackage("com.google.android.apps.plus"); // don't open the browser, make sure it opens in Google+ app
                startActivity(intent);
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "App Not Installed", Toast.LENGTH_LONG).show();


            if(application.equals("com.facebook.katana")){
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.facebook.com/profile.php?id=120407185260764")));
            }else if(application.equals("com.twitter.android")){
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://twitter.com/#!/" + "niboapp")));
            }else if(application.equals("com.instagram.android")){
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://instagram.com/" + "niboapp")));
            }else if(application.equals("com.google.android.apps.plus")){
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/109387839048857856165/posts")));
            }
        }

    }


    private boolean checkAppInstall(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }
}
