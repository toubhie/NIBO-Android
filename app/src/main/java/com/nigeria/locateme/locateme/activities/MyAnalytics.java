package com.nigeria.locateme.locateme.activities;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nigeria.locateme.locateme.R;

import java.util.HashMap;

/**
 * Created by Theophilus on 4/3/2017.
 */
public class MyAnalytics extends Application {

    // The following line should be changed to include the correct property id.
    private static final String PROPERTY_ID = "UA-96639655-1";

    private Tracker mTracker;

    public static int GENERAL_TRACKER = 0;
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    public HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public MyAnalytics() {
        super();
    }

    /*public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t =
                    (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID):
                            (trackerId == TrackerName.GLOBAL_TRACKER) ?
                                    analytics.newTracker(R.xml.global_tracker):
                                    analytics.newTracker(R.xml.ecommerce_tracker);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }*/



    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }



}
