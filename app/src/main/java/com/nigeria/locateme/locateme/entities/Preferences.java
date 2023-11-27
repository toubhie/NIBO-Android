package com.nigeria.locateme.locateme.entities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.nigeria.locateme.locateme.activities.LoginActivity;
import com.nigeria.locateme.locateme.utils.Constants;

/**
 * Created by Theophilus on 12/8/2016.
 */
public class Preferences {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;


    // Context
    Context _context;

    // Constructor
    public Preferences(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Constants.PRIVATE_MODE);
        editor = pref.edit();
    }

    public void saveUserDetails(NiboUser niboUser){
        // Storing name in pref
        editor.putString(Constants.KEY_FULL_NAME, niboUser.getFullName());


        // Storing phone numbers in pref
        editor.putString(Constants.KEY_PHONE_NUMBER, niboUser.getPhoneNumber());

        // Storing Emails in pref
        editor.putString(Constants.KEY_EMAIL, niboUser.getEmail());

        editor.putString(Constants.KEY_ADDRESS, niboUser.getAddress());

        editor.putString(Constants.KEY_PASSWORD, niboUser.getPassword());


        // commit changes
        editor.commit();
    }

    public NiboUser getUserDetails(){
        NiboUser niboUser = new NiboUser();

        niboUser.setFullName(pref.getString(Constants.KEY_FULL_NAME, null));

        niboUser.setPhoneNumber(pref.getString(Constants.KEY_PHONE_NUMBER, null));

        niboUser.setEmail(pref.getString(Constants.KEY_EMAIL, null));

        niboUser.setAddress(pref.getString(Constants.KEY_ADDRESS, "NULL,NULL"));

        return niboUser;
    }

    public void saveStoragePath(String storagePath){
        editor.putString(Constants.KEY_STORAGE_PATH, storagePath);

        editor.commit();
    }

    public String getStoragePath(){
        return pref.getString(Constants.KEY_STORAGE_PATH, null);
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */





    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    public void saveAppUserMySQLId(int userId){
        editor.putInt(Constants.KEY_USER_ID, userId);

        editor.commit();
    }

    public int getAppUserMySQLId(){
        return pref.getInt(Constants.KEY_USER_ID, 0);
    }

    public void saveUniqueCode(String uniqueCode){
        editor.putString(Constants.UNIQUE_CODE, uniqueCode);

        editor.commit();
    }


    public String getUniqueCode(){

       // Contact contact = new Contact();

//        contact.setUniqueCode(pref.getString(Constants.UNIQUE_CODE, null));



       return pref.getString(Constants.UNIQUE_CODE, " ");
        //return pref.getString(Constants.UNIQUE_CODE, null);
    }


    public void saveHouseUniqueCode(String uniqueCode){
        editor.putString(Constants.KEY_ADDRESS, uniqueCode);

        editor.commit();
    }


    public String getHouseUniqueCode(){

        return pref.getString(Constants.KEY_ADDRESS, " ");
    }










    /**
     * Create login session
     * */
    public void createLoginSession(){
        editor.putBoolean(Constants.IS_LOGIN, true);
        editor.commit();
    }




    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
   /* public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }*/
    /**
     * Clear session details
     * */
    public void logoutUser(){
        editor.putBoolean(Constants.IS_LOGIN, false);
        editor.commit();
    }

    /**
     * Quick check for login,kjk
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(Constants.IS_LOGIN, false);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(Constants.KEY_IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(Constants.KEY_IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeLaunchForNiboCode(boolean isFirstTime) {
        editor.putBoolean(Constants.KEY_IS_FIRST_TIME_LAUNCH_FOR_NIBO_CODE, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunchForNiboCode() {
        return pref.getBoolean(Constants.KEY_IS_FIRST_TIME_LAUNCH_FOR_NIBO_CODE, true);
    }

    public void createRegistrationSession(){
        editor.putBoolean(Constants.IS_REGISTERED, true);
        editor.commit();
    }

    // Get Registration State
    public boolean isRegistered(){
        return pref.getBoolean(Constants.IS_REGISTERED, false);
    }

    public void saveAbsolutePath(String absolutePath){
        editor.putString(Constants.KEY_ABSOLUTE_PATH, absolutePath);
        editor.commit();
    }

    // Get Registration State
    public String getAbsolutePath(){
        return pref.getString(Constants.KEY_ABSOLUTE_PATH, null);
    }

    public void setProfilePercentage(int profilePercentage) {
        editor.putInt(Constants.KEY_PROFILE_PERCENTAGE, profilePercentage);
        editor.commit();
    }

    public int getProfilePercentage() {
        return pref.getInt(Constants.KEY_PROFILE_PERCENTAGE, 0);
    }

    public void saveLastSyncDate(String lastSyncDate) {
        editor.putString(Constants.KEY_LAST_SYNC_DATE, lastSyncDate);
        editor.commit();
    }

    public String getLastSyncDate() {
        return pref.getString(Constants.KEY_LAST_SYNC_DATE, "");
    }

    public void setSyncInterval(long syncFrequency) {
        editor.putLong(Constants.KEY_SYNC_FREQUENCY, syncFrequency);
        editor.commit();
    }

    public long getSyncInterval() {
        return pref.getLong(Constants.KEY_SYNC_FREQUENCY, SyncInterval.defaultSyncInterval);
    }

}
