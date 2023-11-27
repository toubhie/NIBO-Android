package com.nigeria.locateme.locateme.entities;

/**
 * Created by Williamz on 5/11/2017.
 */

public class SyncInterval {
    public static String[] intervals = new String[]{
            //"Never",
            "Every 5 Minutes",
            "Every 10 Minutes",
            "Every 15 Minutes",
            "Every 30 Minutes",
            "Every 1 Hour",
            "Every 2 Hours",
            "Every 4 Hours",
            "Every 12 Hours",
            "Every 24 Hours"
    };

    public static long[] intervalTimeInMilliSecs = new long[]{
            // 0, // never
            1000 * 60 * 5, // 5 mins
            1000 * 60 * 10, // 10 mins
            1000 * 60 * 15, // 15 mins
            1000 * 60 * 30, // 30 mins
            1000 * 60 * 60, // 1 hr
            1000 * 60 * 60 * 2, // 2 hrs
            1000 * 60 * 60 * 4, // 4 hrs
            1000 * 60 * 60 * 12, // 12 hrs
            1000 * 60 * 60 * 24, // 24 hrs
    };

    public static long defaultSyncInterval = 1000 * 60 * 60 * 12; // 12 hrs
}
