package com.nigeria.locateme.locateme.webservices;

import android.content.Context;
import android.util.Log;

import com.nigeria.locateme.locateme.entities.NiboHouse;
import com.nigeria.locateme.locateme.entities.NiboUser;
import com.nigeria.locateme.locateme.entities.UserLocation;
import com.nigeria.locateme.locateme.utils.Constants;
import com.nigeria.locateme.locateme.entities.Contact;
import com.nigeria.locateme.locateme.entities.Preferences;
import com.nigeria.locateme.locateme.utils.SQLController;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Theophilus on 12/8/2016.
 */
public class WebService {

    private JSONParser jsonParser = new JSONParser();

    private Preferences pref;

    public int registerUser(Context context, NiboUser niboUser){
        int result = 0;
        int userId;

        pref = new Preferences(context);
        JSONObject json = null;

        try {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(Constants.TAG_FULL_NAME, niboUser.getFullName() != null ?
                    niboUser.getFullName() : ""));

            params.add(new BasicNameValuePair(Constants.TAG_EMAIL, niboUser.getEmail() != null ?
                    niboUser.getEmail() : ""));

            params.add(new BasicNameValuePair(Constants.TAG_PHONE_NUMBER, niboUser.getPhoneNumber() != null ?
                    niboUser.getPhoneNumber() : ""));

            params.add(new BasicNameValuePair(Constants.TAG_ADDRESS, niboUser.getAddress() != null ?
                    niboUser.getAddress() : ""));

            params.add(new BasicNameValuePair(Constants.TAG_LAST_CHECKIN, niboUser.getLastCheckin() != null ?
                    niboUser.getLastCheckin() : ""));

            params.add(new BasicNameValuePair(Constants.TAG_TIME_CHECKIN, niboUser.getTimeCheckin() != null ?
                    niboUser.getTimeCheckin() : ""));

            params.add(new BasicNameValuePair(Constants.TAG_PASSWORD, niboUser.getPassword() != null ?
                    niboUser.getPassword() : ""));

            Log.d(Constants.TAG_NIBO, "b4 parsing");

            json = jsonParser.makeHttpRequest(Constants.REGISTER_URL, "POST", params);

            //Log.d(Constants.TAG_NIBO, json.toString());


            result = json.getInt(Constants.TAG_CODE);

            userId = json.getInt(Constants.TAG_USER_ID);

            pref.saveUserDetails(niboUser);
            pref.saveAppUserMySQLId(userId);

            return result;

        } catch (JSONException e) {
            e.printStackTrace();

            Log.d("Whats wrong?", json.toString());
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        return result;
    }

    public int checkInUser(Context context, NiboUser niboUser){
        int result = 0;
        int userId;
        String uniqueCode;

        pref = new Preferences(context);
        JSONObject json = null;

        try {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.TAG_PHONE_NUMBER, niboUser.getPhoneNumber() != null ?
                    niboUser.getPhoneNumber() : ""));

            params.add(new BasicNameValuePair(Constants.TAG_LAST_CHECKIN, niboUser.getLastCheckin()));

            params.add(new BasicNameValuePair(Constants.TAG_TIME_CHECKIN, niboUser.getTimeCheckin()));


            json = jsonParser.makeHttpRequest(Constants.EDIT_PROFILE_URL, "POST", params);

            Log.d(Constants.TAG_NIBO, json.toString());


            result = json.getInt(Constants.TAG_CODE);

            // userId = json.getInt(Constants.TAG_USER_ID);

            uniqueCode = json.getString(Constants.UNIQUE_CODE);

            //pref.saveUserDetails(niboUser);
            // pref.saveAppUserMySQLId(userId);
            pref.saveUniqueCode(uniqueCode);

            return result;

        } catch (JSONException e) {
            e.printStackTrace();

            Log.d("Whats wrong?", json.toString());
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        return result;
    }

    public int editUserProfile(Context context, NiboUser niboUser){
        int result = 0;
        int userId;
        String uniqueCode;

        pref = new Preferences(context);
        JSONObject json = null;

        try {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();


            params.add(new BasicNameValuePair(Constants.TAG_OLD_PHONE_NUMBER, niboUser.getOldPhoneNumber() != null ?
                    niboUser.getOldPhoneNumber() : ""));

            params.add(new BasicNameValuePair(Constants.TAG_PHONE_NUMBER, niboUser.getPhoneNumber() != null ?
                    niboUser.getPhoneNumber() : ""));

            params.add(new BasicNameValuePair(Constants.TAG_FULL_NAME, niboUser.getFullName()));

            params.add(new BasicNameValuePair(Constants.TAG_EMAIL, niboUser.getEmail()));

            params.add(new BasicNameValuePair(Constants.TAG_PASSWORD, niboUser.getPassword()));


            json = jsonParser.makeHttpRequest(Constants.EDIT_PROFILE_USER_URL, "POST", params);

            Log.d(Constants.TAG_NIBO, json.toString());


            result = json.getInt(Constants.TAG_CODE);

            // userId = json.getInt(Constants.TAG_USER_ID);

            //uniqueCode = json.getString(Constants.UNIQUE_CODE);

            if(result == 1){
                pref.saveUserDetails(niboUser);
            }

            // pref.saveAppUserMySQLId(userId);
            //pref.saveUniqueCode(uniqueCode);

            return result;

        } catch (JSONException e) {
            e.printStackTrace();

            Log.d("Whats wrong?", json.toString());
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        return result;
    }

    public int editNiboHouseCode(Context context, NiboUser niboUser){
        int result = 0;
        int userId;
        String uniqueCode;

        pref = new Preferences(context);
        JSONObject json = null;

        try {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();


            params.add(new BasicNameValuePair(Constants.TAG_PHONE_NUMBER, niboUser.getPhoneNumber() != null ?
                    niboUser.getPhoneNumber() : ""));

            params.add(new BasicNameValuePair(Constants.TAG_ADDRESS, niboUser.getAddress() != null ?
                    niboUser.getAddress() : ""));


            json = jsonParser.makeHttpRequest(Constants.EDIT_NIBO_HOUSE_CODE, "POST", params);

            Log.d(Constants.TAG_NIBO, json.toString());


            result = json.getInt(Constants.TAG_CODE);

            // userId = json.getInt(Constants.TAG_USER_ID);

            //uniqueCode = json.getString(Constants.UNIQUE_CODE);

            if(result == 1){
                //pref.saveUserDetails(locateMeUser);
            }

            // pref.saveAppUserMySQLId(userId);
            //pref.saveUniqueCode(uniqueCode);

            return result;

        } catch (JSONException e) {
            e.printStackTrace();

            Log.d("Whats wrong?", json.toString());
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        return result;
    }


    public int requestHouseCode(Context context, NiboUser niboUser){
        int result = 0;
        int userId;
        String uniqueCode;

        pref = new Preferences(context);
        JSONObject json = null;

        try {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.TAG_BUILDING_TYPE, niboUser.getBuildingType() != null ?
                    niboUser.getBuildingType() : ""));

            params.add(new BasicNameValuePair(Constants.TAG_HOUSE_ADDRESS, niboUser.getHouseAddress() != null ?
                    niboUser.getHouseAddress() : ""));

            params.add(new BasicNameValuePair(Constants.TAG_STATE, niboUser.getState() != null ?
                    niboUser.getState() : ""));

            params.add(new BasicNameValuePair(Constants.TAG_LOCAL_GOVERNMENT, niboUser.getLocalGovernment() != null ?
                    niboUser.getLocalGovernment() : ""));

            params.add(new BasicNameValuePair(Constants.TAG_STREET, niboUser.getStreet() != null ?
                    niboUser.getStreet() : ""));

            params.add(new BasicNameValuePair(Constants.TAG_LATLONG, niboUser.getLatLong() != null ?
                    niboUser.getLatLong() : ""));

            params.add(new BasicNameValuePair(Constants.TAG_EMAIL, niboUser.getEmail() != null ?
                    niboUser.getEmail() : ""));

            params.add(new BasicNameValuePair(Constants.TAG_PHONE_NUMBER, niboUser.getPhoneNumber() != null ?
                    niboUser.getPhoneNumber() : ""));

            params.add(new BasicNameValuePair(Constants.TAG_FULL_NAME, niboUser.getFullName() != null ?
                    niboUser.getFullName() : ""));


            json = jsonParser.makeHttpRequest(Constants.REQUEST_HOUSE_CODE_URL, "POST", params);

            Log.d(Constants.TAG_NIBO, json.toString());


            result = json.getInt(Constants.TAG_CODE);

            String NiboHouseCode = json.getString(Constants.UNIQUE_CODE);

            if (result == 1) {
                try {
                    pref.saveHouseUniqueCode(NiboHouseCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            return result;

        } catch (JSONException e) {
            e.printStackTrace();

            Log.d("Whats wrong?", json.toString());
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        return result;
    }

    public int activateHouseCode(Context context, NiboUser niboUser){
        int result = 0;
        int userId;
        String uniqueCode;

        pref = new Preferences(context);
        JSONObject json = null;

        try {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.TAG_PERMANENT_NIBO_CODE, niboUser.getAddress() != null ?
                    niboUser.getAddress() : ""));



            params.add(new BasicNameValuePair(Constants.TAG_LATLONG, niboUser.getLatLong() != null ?
                    niboUser.getLatLong() : ""));




            json = jsonParser.makeHttpRequest(Constants.ACTIVATE_HOUSE_CODE_URL, "POST", params);

            Log.d(Constants.TAG_NIBO, json.toString());


            result = json.getInt(Constants.TAG_CODE);


            return result;

        } catch (JSONException e) {
            e.printStackTrace();

            Log.d("Whats wrong?", json.toString());
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        return result;
    }

    public Contact getUserContactByPrimaryPhoneNumber(String phoneNumber){
        try{
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.TAG_PHONE_NUMBER, phoneNumber.trim()));

            JSONObject json = jsonParser.makeHttpRequest(Constants.GET_CONTACT_BY_PHONE_NUMBER_URL, "GET", params);


            int result = json.getInt(Constants.TAG_CODE);

            if (result == 1){
                JSONArray messageObj = json.getJSONArray(Constants.TAG_USER); // JSON Array

                // get first site object from JSON Array
                JSONObject jsonObj = messageObj.getJSONObject(0);

                Contact contact = new Contact();

                contact.setPhoneNumber(jsonObj.getString(Constants.TAG_PHONE_NUMBER).trim());

                contact.setFullName(jsonObj.getString(Constants.TAG_FULL_NAME));

                contact.setAddress(jsonObj.getString(Constants.TAG_ADDRESS));

                contact.setLastCheckin(jsonObj.getString(Constants.TAG_LAST_CHECKIN));

                contact.setTimeCheckin(jsonObj.getString(Constants.TAG_TIME_CHECKIN));

                return contact;
            }

            return null;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String getUserEmailForPasswordRecovery(String mail){
        try{
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.KEY_EMAIL, mail.trim()));

            JSONObject json = jsonParser.makeHttpRequest(Constants.GET_USER_BY_EMAIL, "GET", params);


            int result = json.getInt(Constants.TAG_CODE);
            String confirmationCode = json.getString(Constants.TAG_CONFIRMATION_CODE);

            if (result == 1){


                return confirmationCode;
            }

            return null;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public int updatePassword(String mail, String password){
        try{
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.KEY_EMAIL, mail.trim()));

            params.add(new BasicNameValuePair(Constants.KEY_PASSWORD, password.trim()));

            JSONObject json = jsonParser.makeHttpRequest(Constants.UPDATE_PASSWORD, "GET", params);


            int result = json.getInt(Constants.TAG_CODE);
            //String confirmationCode = json.getString(Constants.TAG_CONFIRMATION_CODE);

            if (result == 1){


                return result;
            }

            return 0;
        } catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public Contact getUserPermanentHouseCode(String permanentHouseCode){
        try{
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.TAG_HOUSE_ADDRESS, permanentHouseCode.trim()));

            JSONObject json = jsonParser.makeHttpRequest(Constants.GET_HOUSE_BY_PERMANENT_CODE_URL, "GET", params);


            int result = json.getInt(Constants.TAG_CODE);

            if (result == 1){
                JSONArray messageObj = json.getJSONArray(Constants.TAG_USER); // JSON Array

                // get first site object from JSON Array
                JSONObject jsonObj = messageObj.getJSONObject(0);

                Contact contact = new Contact();

                contact.setHouseAddress(jsonObj.getString(Constants.TAG_HOUSE_ADDRESS).trim());

                contact.setLocalGovernment(jsonObj.getString(Constants.TAG_LOCAL_GOVERNMENT));

                contact.setState(jsonObj.getString(Constants.TAG_STATE));

                contact.setStreet(jsonObj.getString(Constants.TAG_STREET));

                contact.setLatLong(jsonObj.getString(Constants.TAG_LATLONG));

                return contact;
            }

            return null;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Contact getUserSpecialHouseCode(String specialHouseCode){
        try{
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.TAG_HOUSE_ADDRESS, specialHouseCode.trim()));

            JSONObject json = jsonParser.makeHttpRequest(Constants.GET_HOUSE_BY_SPECIAL_CODE_URL, "GET", params);


            int result = json.getInt(Constants.TAG_CODE);

            if (result == 1){
                JSONArray messageObj = json.getJSONArray(Constants.TAG_USER); // JSON Array

                // get first site object from JSON Array
                JSONObject jsonObj = messageObj.getJSONObject(0);

                Contact contact = new Contact();

                contact.setHouseAddress(jsonObj.getString(Constants.TAG_HOUSE_ADDRESS).trim());

                contact.setLocalGovernment(jsonObj.getString(Constants.TAG_LOCAL_GOVERNMENT));

                contact.setState(jsonObj.getString(Constants.TAG_STATE));

                contact.setStreet(jsonObj.getString(Constants.TAG_STREET));

                contact.setLatLong(jsonObj.getString(Constants.TAG_LATLONG));

                return contact;
            }

            return null;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public int loginUser(Context context, NiboUser niboUser){
        int result = 0;

        pref = new Preferences(context);

        try{
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.TAG_USERNAME, niboUser.getEmail()));

            params.add(new BasicNameValuePair(Constants.TAG_PASSWORD, niboUser.getPassword()));

            JSONObject json = jsonParser.makeHttpRequest(Constants.LOGIN_URL, "GET", params);

            Log.d(Constants.TAG_NIBO, json.toString());

            result = json.getInt(Constants.TAG_CODE);

            if (result == 1){
                try {
                    JSONArray messageObj = json.getJSONArray(Constants.TAG_USER); // JSON Array

                    // get first site object from JSON Array
                    JSONObject jsonObj = messageObj.getJSONObject(0);

                    NiboUser niboUser1 = new NiboUser();

                    niboUser1.setFullName(jsonObj.getString(Constants.TAG_FULL_NAME).trim());
                    niboUser1.setEmail(jsonObj.getString(Constants.TAG_EMAIL).trim());
                    niboUser1.setAddress(jsonObj.getString(Constants.TAG_ADDRESS).trim());
                    niboUser1.setPhoneNumber(jsonObj.getString(Constants.TAG_PHONE_NUMBER).trim());
                    niboUser1.setTimeCheckin(jsonObj.getString(Constants.TAG_TIME_CHECKIN).trim());
                    niboUser1.setLastCheckin(jsonObj.getString(Constants.TAG_LAST_CHECKIN).trim());

                    int userId = Integer.parseInt(jsonObj.getString(Constants.TAG_ID).trim());

                    Log.i(Constants.TAG_NIBO, "usedId in login: " + userId);


                    ///
                    Log.i(Constants.TAG_NIBO, "locateMeUser1: " + niboUser1);

                    pref.saveUserDetails(niboUser1);
                    pref.saveAppUserMySQLId(userId);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            return result;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public int getAllUserSavedLocations(Context context){

        int result = 0;

        pref = new Preferences(context);

        try{
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            Log.i(Constants.TAG_NIBO, "userId{}}{ in web service" + String.valueOf(pref.getAppUserMySQLId()));

            params.add(new BasicNameValuePair(Constants.TAG_USER_ID, String.valueOf(pref.getAppUserMySQLId())));

            JSONObject json = jsonParser.makeHttpRequest(Constants.GET_ALL_USER_SAVED_LOCATIONS_URL, "GET", params);

            Log.d(Constants.TAG_NIBO, json.toString());

            result = json.getInt(Constants.TAG_CODE);

            if (result == 1){
                try{
                    JSONArray messageObj = json.getJSONArray(Constants.TAG_SAVED_LOCATIONS); // JSON Array

                    //JSONArray messageObj = new JSONArray(jsonResult); // JSON Array

                    Log.i(Constants.TAG_NIBO, "messageObj: " + messageObj);

//                    messageObj = messageObj.substring(jsonResult.indexOf("["));

                    for(int i = 0; i < messageObj.length(); i++){
                        // get first site object from JSON Array
                        JSONObject jsonObj = messageObj.getJSONObject(i);

                        Log.i(Constants.TAG_NIBO, "each messageObj: " + jsonObj.toString());

                        int locationId = Integer.parseInt(jsonObj.getString(Constants.TAG_LOCATION_ID_UPPER).trim());
                        Long userId = Long.parseLong(jsonObj.getString(Constants.TAG_USER_ID_UPPER).trim());
                        int sqliteId = Integer.parseInt(jsonObj.getString(Constants.TAG_SQLITE_ID_UPPER).trim());
                        String longitude = jsonObj.getString(Constants.TAG_LONGITUDE_UPPER).trim();
                        String latitude = jsonObj.getString(Constants.TAG_LATITUDE_UPPER).trim();
                        String description = jsonObj.getString(Constants.TAG_DESCRIPTION_UPPER).trim();
                        int isDeleted = Integer.parseInt(jsonObj.getString(Constants.TAG_IS_DELETED_UPPER).trim());


                        UserLocation userLocation = new UserLocation();

                        // userLocation.setLocationId(locationId);
                        userLocation.setUserId(userId);
                        userLocation.setSqliteId(sqliteId);
                        userLocation.setMysqlId(locationId);
                        userLocation.setLongitude(longitude);
                        userLocation.setLatitdue(latitude);
                        userLocation.setDescription(description);
                        userLocation.setIsDeleted(isDeleted);
                        userLocation.setIsSyncedToOnlineDBStatus(Constants.FALSE);

                        // Adding record to sqlite
                        SQLController sqlController = new SQLController(context);
                        sqlController.insertData(description, latitude, longitude);

                        sqlController.addSavedLocationToSQLiteDB(userLocation);

                    }

                    Log.i(Constants.TAG_NIBO, "Records added in web service");

                } catch (JSONException e){
                    e.printStackTrace();
                }

            }

            return result;
        } catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    public int insertNewSavedLocationToServer(Context context, UserLocation userLocation){
        int result = 0;

        pref = new Preferences(context);

        try {

            Log.i(Constants.TAG_NIBO, "inside insrt webservice: " + userLocation);

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.TAG_LONGITUDE, userLocation.getLongitude() != null ?
                    userLocation.getLongitude() : ""));
            params.add(new BasicNameValuePair(Constants.TAG_LATITUDE, userLocation.getLatitude() != null ?
                    userLocation.getLatitude() : ""));
            params.add(new BasicNameValuePair(Constants.TAG_DESCRIPTION, userLocation.getDescription() != null ?
                    userLocation.getDescription() : ""));


            params.add(new BasicNameValuePair(Constants.TAG_USER_ID, String.valueOf(pref.getAppUserMySQLId())));

            params.add(new BasicNameValuePair(Constants.TAG_SQLITE_ID, String.valueOf(userLocation.getSqliteId())));

            params.add(new BasicNameValuePair(Constants.TAG_IS_DELETED, String.valueOf(userLocation.getIsDeleted())));

            JSONObject json = jsonParser.makeHttpRequest(Constants.INSERT_NEW_SAVED_LOCATION_TO_SYNC, "GET", params);

            Log.d(Constants.TAG_NIBO, json.toString());

            result = json.getInt(Constants.TAG_CODE);

            if(result == 1){
                int contactMysqlId = json.getInt(Constants.TAG_SAVED_LOCATION_MYSQL_ID);

                userLocation.setMysqlId(contactMysqlId);
                userLocation.setIsSyncedToOnlineDBStatus(Constants.TRUE);

                SQLController sqlController = new SQLController(context);
                sqlController.updateContactFromSQLiteDBById(userLocation);
            }

            return result;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public int updateLocationToServer(Context context, UserLocation userLocation){
        int result = 0;

        pref = new Preferences(context);

        try {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.TAG_LONGITUDE, userLocation.getLongitude() != null ?
                    userLocation.getLongitude() : ""));
            params.add(new BasicNameValuePair(Constants.TAG_LATITUDE, userLocation.getLatitude() != null ?
                    userLocation.getLatitude() : ""));
            params.add(new BasicNameValuePair(Constants.TAG_DESCRIPTION, userLocation.getDescription() != null ?
                    userLocation.getDescription() : ""));


            params.add(new BasicNameValuePair(Constants.TAG_USER_ID, String.valueOf(pref.getAppUserMySQLId())));

            params.add(new BasicNameValuePair(Constants.TAG_SQLITE_ID, String.valueOf(userLocation.getSqliteId())));

            params.add(new BasicNameValuePair(Constants.TAG_IS_DELETED, String.valueOf(userLocation.getIsDeleted())));

            params.add(new BasicNameValuePair(Constants.TAG_SAVED_LOCATION_MYSQL_ID, String.valueOf(userLocation.getMysqlId())));

            Log.i(Constants.TAG_NIBO, "Saved Location Mysqlid: " + String.valueOf(userLocation.getMysqlId()));

            Log.i(Constants.TAG_NIBO, "isDeleted: " + userLocation.getIsDeleted());

            JSONObject json = jsonParser.makeHttpRequest(Constants.UPDATE_SAVED_LOCATION_TO_SYNC, "GET", params);

            Log.d(Constants.TAG_NIBO, json.toString());

            result = json.getInt(Constants.TAG_CODE);

            return result;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public int sendFeedback(Context context, String feedbackMessage,String fullName, String email){
        int result = 0;

        pref = new Preferences(context);

        try {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.TAG_FULL_NAME, fullName.trim()));
            params.add(new BasicNameValuePair(Constants.TAG_FEEDBACK_MESSAGE, feedbackMessage.trim()));
            params.add(new BasicNameValuePair(Constants.TAG_EMAIL, email.trim()));

            JSONObject json = jsonParser.makeHttpRequest(Constants.SEND_FEEDBACK_URL, "GET", params);

            Log.d(Constants.TAG_NIBO, json.toString());

            result = json.getInt(Constants.TAG_CODE);

            return result;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }


   /* public int checkIfPrimaryPhoneNumberExistOnServer(String primaryPhoneNumber){
        int result = 0;

        try{
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.TAG_PRIMARY_PHONE_NUMBER, primaryPhoneNumber));

            JSONObject json = jsonParser.makeHttpRequest(Constants.CHECK_PRIMARY_PHONE_NUMBER_URL, "GET", params);

            Log.d(Constants.TAG_NIBO, json.toString());

            result = json.getInt(Constants.TAG_CODE);

            return result;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public int updateUserProfile(Context context, Contact contact){
        int result = 0;

        pref = new Preferences(context);

        try {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.TAG_FIRSTNAME, contact.getFirstName()));
            params.add(new BasicNameValuePair(Constants.TAG_LASTNAME, contact.getLastName()));

            params.add(new BasicNameValuePair(Constants.TAG_PRIMARY_EMAIL, contact.getPrimaryEmail() != null ?
                    contact.getPrimaryEmail() : ""));
            params.add(new BasicNameValuePair(Constants.TAG_SECONDARY_EMAIL, contact.getSecondaryEmail() != null ?
                    contact.getSecondaryEmail() : ""));
            params.add(new BasicNameValuePair(Constants.TAG_CONCATENATED_EMAILS, contact.getConcatenatedEmails() != null ?
                    contact.getConcatenatedEmails() : ""));

            params.add(new BasicNameValuePair(Constants.TAG_PRIMARY_PHONE_NUMBER, contact.getPrimaryPhoneNumber() != null ?
                    contact.getPrimaryPhoneNumber() : ""));
            params.add(new BasicNameValuePair(Constants.TAG_SECONDARY_PHONE_NUMBER, contact.getSecondaryPhoneNumber() != null ?
                    contact.getSecondaryPhoneNumber() : ""));
            params.add(new BasicNameValuePair(Constants.TAG_CONCATENATED_PHONE_NUMBERS, contact.getConcatenatedPhoneNumbers() != null ?
                    contact.getConcatenatedPhoneNumbers() : ""));

            params.add(new BasicNameValuePair(Constants.TAG_ADDRESS, contact.getAddress()));
            params.add(new BasicNameValuePair(Constants.TAG_USER_ID, String.valueOf(pref.getAppUserMySQLId())));

            params.add(new BasicNameValuePair(Constants.TAG_COMPANY_NAME, contact.getCompanyName()));
            params.add(new BasicNameValuePair(Constants.TAG_JOB_TITLE, contact.getJobTitle()));
            params.add(new BasicNameValuePair(Constants.TAG_COMPANY_WEBSITE, contact.getCompanyWebsite()));

            JSONObject json = jsonParser.makeHttpRequest(Constants.EDIT_PROFILE_URL, "POST", params);

            Log.d(Constants.TAG_NIBO, json.toString());

            result = json.getInt(Constants.TAG_CODE);

            pref.saveUserDetails(contact);

            return result;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public int updateUserProfileAfterDelete(Context context, Contact contact){
        int result = 0;

        pref = new Preferences(context);

        try {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.TAG_FIRSTNAME, contact.getFirstName()));
            params.add(new BasicNameValuePair(Constants.TAG_LASTNAME, contact.getLastName()));

            params.add(new BasicNameValuePair(Constants.TAG_PRIMARY_EMAIL, contact.getPrimaryEmail() != null ?
                    contact.getPrimaryEmail() : ""));
            params.add(new BasicNameValuePair(Constants.TAG_SECONDARY_EMAIL, contact.getSecondaryEmail() != null ?
                    contact.getSecondaryEmail() : ""));
            params.add(new BasicNameValuePair(Constants.TAG_CONCATENATED_EMAILS, contact.getConcatenatedEmails() != null ?
                    contact.getConcatenatedEmails() : ""));

            params.add(new BasicNameValuePair(Constants.TAG_PRIMARY_PHONE_NUMBER, contact.getPrimaryPhoneNumber() != null ?
                    contact.getPrimaryPhoneNumber() : ""));
            params.add(new BasicNameValuePair(Constants.TAG_SECONDARY_PHONE_NUMBER, contact.getSecondaryPhoneNumber() != null ?
                    contact.getSecondaryPhoneNumber() : ""));
            params.add(new BasicNameValuePair(Constants.TAG_CONCATENATED_PHONE_NUMBERS, contact.getConcatenatedPhoneNumbers() != null ?
                    contact.getConcatenatedPhoneNumbers() : ""));

            params.add(new BasicNameValuePair(Constants.TAG_ADDRESS, contact.getAddress()));
            params.add(new BasicNameValuePair(Constants.TAG_USER_ID, String.valueOf(pref.getAppUserMySQLId())));

            params.add(new BasicNameValuePair(Constants.TAG_COMPANY_NAME, contact.getCompanyName()));
            params.add(new BasicNameValuePair(Constants.TAG_JOB_TITLE, contact.getJobTitle()));
            params.add(new BasicNameValuePair(Constants.TAG_COMPANY_WEBSITE, contact.getCompanyWebsite()));

            JSONObject json = jsonParser.makeHttpRequest(Constants.EDIT_PROFILE_URL_AFTER_DELETE, "POST", params);

            Log.d(Constants.TAG_NIBO, json.toString());

            result = json.getInt(Constants.TAG_CODE);

            pref.saveUserDetails(contact);

            return result;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public int sendFeedback(Context context, String feedbackMessage){
        int result = 0;

        pref = new Preferences(context);

        try {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.TAG_USER_ID, String.valueOf(pref.getAppUserMySQLId())));
            params.add(new BasicNameValuePair(Constants.TAG_FEEDBACK_MESSAGE, feedbackMessage.trim()));

            JSONObject json = jsonParser.makeHttpRequest(Constants.SEND_FEEDBACK_URL, "GET", params);

            Log.d(Constants.TAG_NIBO, json.toString());

            result = json.getInt(Constants.TAG_CODE);

            return result;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public Contact getUserContactByPrimaryPhoneNumber(String primaryPhoneNumber){
        try{
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.TAG_PRIMARY_PHONE_NUMBER, primaryPhoneNumber.trim()));

            JSONObject json = jsonParser.makeHttpRequest(Constants.GET_CONTACT_BY_PHONE_NUMBER_URL, "GET", params);

            Log.d(Constants.TAG_NIBO, json.toString());

            int result = json.getInt(Constants.TAG_CODE);

            if (result == 1){
                JSONArray messageObj = json.getJSONArray(Constants.TAG_USER); // JSON Array

                // get first site object from JSON Array
                JSONObject jsonObj = messageObj.getJSONObject(0);

                Contact contact = new Contact();

                contact.setId(Integer.parseInt(jsonObj.getString(Constants.TAG_ID).trim()));
                contact.setFullName(jsonObj.getString(Constants.TAG_FULL_NAME).trim());
                contact.setPrimaryPhoneNumber(jsonObj.getString(Constants.TAG_PRIMARY_PHONE_NUMBER).trim());
                contact.setSecondaryPhoneNumber(jsonObj.getString(Constants.TAG_SECONDARY_PHONE_NUMBER).trim());
                contact.setConcatenatedPhoneNumbers(jsonObj.getString(Constants.TAG_CONCATENATED_PHONE_NUMBERS).trim());
                contact.setPrimaryEmail(jsonObj.getString(Constants.TAG_PRIMARY_EMAIL).trim());
                contact.setSecondaryEmail(jsonObj.getString(Constants.TAG_SECONDARY_EMAIL).trim());
                contact.setConcatenatedEmails(jsonObj.getString(Constants.TAG_CONCATENATED_EMAILS).trim());
                contact.setAddress(jsonObj.getString(Constants.TAG_ADDRESS).trim());
                contact.setCompanyName(jsonObj.getString(Constants.TAG_COMPANY_NAME).trim());
                contact.setJobTitle(jsonObj.getString(Constants.TAG_JOB_TITLE).trim());
                contact.setCompanyWebsite(jsonObj.getString(Constants.TAG_COMPANY_WEBSITE).trim());
                contact.setDateCreated(jsonObj.getString(Constants.TAG_DATE_CREATED).trim());

                return contact;
            }

            return null;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public int addContactToServer(Context context, Contact contact){
        int result = 0;

        pref = new Preferences(context);

        try {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.TAG_FULL_NAME, contact.getFullName()));

            params.add(new BasicNameValuePair(Constants.TAG_PRIMARY_EMAIL, contact.getPrimaryEmail() != null ?
                    contact.getPrimaryEmail() : ""));
            params.add(new BasicNameValuePair(Constants.TAG_SECONDARY_EMAIL, contact.getSecondaryEmail() != null ?
                    contact.getSecondaryEmail() : ""));
            params.add(new BasicNameValuePair(Constants.TAG_CONCATENATED_EMAILS, contact.getConcatenatedEmails() != null ?
                    contact.getConcatenatedEmails() : ""));

            params.add(new BasicNameValuePair(Constants.TAG_PRIMARY_PHONE_NUMBER, contact.getPrimaryPhoneNumber() != null ?
                    contact.getPrimaryPhoneNumber() : ""));
            params.add(new BasicNameValuePair(Constants.TAG_SECONDARY_PHONE_NUMBER, contact.getSecondaryPhoneNumber() != null ?
                    contact.getSecondaryPhoneNumber() : ""));
            params.add(new BasicNameValuePair(Constants.TAG_CONCATENATED_PHONE_NUMBERS, contact.getConcatenatedPhoneNumbers() != null ?
                    contact.getConcatenatedPhoneNumbers() : ""));

            params.add(new BasicNameValuePair(Constants.TAG_ADDRESS, contact.getAddress()));

            params.add(new BasicNameValuePair(Constants.TAG_COMPANY_NAME, contact.getCompanyName()));
            params.add(new BasicNameValuePair(Constants.TAG_JOB_TITLE, contact.getJobTitle()));
            params.add(new BasicNameValuePair(Constants.TAG_COMPANY_WEBSITE, contact.getCompanyWebsite()));

            params.add(new BasicNameValuePair(Constants.TAG_USER_ID, String.valueOf(pref.getAppUserMySQLId())));

            params.add(new BasicNameValuePair(Constants.TAG_SQLITE_ID, String.valueOf(contact.getSqliteId())));

            JSONObject json = jsonParser.makeHttpRequest(Constants.ADD_CONTACT_TO_SERVER_URL, "GET", params);

            Log.d(Constants.TAG_NIBO, json.toString());

            result = json.getInt(Constants.TAG_CODE);

            return result;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public int login(String phoneNumber, String password){
        int result = 0;

        try{
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.TAG_PHONE_NUMBER, phoneNumber));

            params.add(new BasicNameValuePair(Constants.TAG_PASSWORD, password));

            JSONObject json = jsonParser.makeHttpRequest(Constants.LOGIN_URL, "GET", params);

            Log.d(Constants.TAG_NIBO, json.toString());

            result = json.getInt(Constants.TAG_CODE);

            return result;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }*/


   /* public int deleteContactById(Context context, Contact contact){
        int result = 0;

        pref = new Preferences(context);

        try {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.TAG_SQLITE_ID, String.valueOf(contact.getSqliteId())));

            params.add(new BasicNameValuePair(Constants.TAG_USER_ID, String.valueOf(pref.getAppUserMySQLId())));

            JSONObject json = jsonParser.makeHttpRequest(Constants.DELETE_CONTACT_URL, "GET", params);

            Log.d(Constants.TAG_NIBO, json.toString());

            result = json.getInt(Constants.TAG_CODE);

            return result;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public int sendConfirmationCodeByEmail(Context context, String email){
        int result = 0;

        pref = new Preferences(context);

        try{
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.TAG_USER_ID, String.valueOf(pref.getAppUserMySQLId())));
            params.add(new BasicNameValuePair(Constants.TAG_EMAIL, email));

            JSONObject json = jsonParser.makeHttpRequest(Constants.RETRIEVE_PASSWORD_URL, "GET", params);

            Log.d(Constants.TAG_NIBO, json.toString());

            result = json.getInt(Constants.TAG_CODE);

            return result;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public int confirmConfirmationCode(Context context, String confirmationCode){
        int result = 0;

        pref = new Preferences(context);

        try{
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.TAG_CONFIRMATION_CODE, confirmationCode));
            params.add(new BasicNameValuePair(Constants.TAG_USER_ID, String.valueOf(pref.getAppUserMySQLId())));

            JSONObject json = jsonParser.makeHttpRequest(Constants.CONFIRM_CONFIRMATION_CODE_URL, "GET", params);

            Log.d(Constants.TAG_NIBO, json.toString());

            result = json.getInt(Constants.TAG_CODE);

            return result;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public int changePassword(Context context, String password){
        int result = 0;

        pref = new Preferences(context);

        try{
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.TAG_PASSWORD, password));
            params.add(new BasicNameValuePair(Constants.TAG_USER_ID, String.valueOf(pref.getAppUserMySQLId())));

            JSONObject json = jsonParser.makeHttpRequest(Constants.CHANGE_PASSWORD_URL, "GET", params);

            Log.d(Constants.TAG_NIBO, json.toString());

            result = json.getInt(Constants.TAG_CODE);

            return result;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
*/

    public NiboUser searchTemporaryNiboCode(String temporaryNiboCode){
        try{
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.TAG_TEMPORARY_NIBO_CODE, temporaryNiboCode.trim()));

            JSONObject json = jsonParser.makeHttpRequest(Constants.SEARCH_TEMPORARY_NIBO_CODE_URL, "GET", params);

            int result = json.getInt(Constants.TAG_CODE);

            if (result == 1){
                JSONArray messageObj = json.getJSONArray(Constants.TAG_USER); // JSON Array

                // get first site object from JSON Array
                JSONObject jsonObj = messageObj.getJSONObject(0);

                NiboUser niboUser = new NiboUser();

                niboUser.setPhoneNumber(jsonObj.getString(Constants.TAG_PHONE_NUMBER).trim());

                niboUser.setFullName(jsonObj.getString(Constants.TAG_FULL_NAME));

                niboUser.setAddress(jsonObj.getString(Constants.TAG_ADDRESS));

                niboUser.setLastCheckin(jsonObj.getString(Constants.TAG_LAST_CHECKIN));

                niboUser.setTimeCheckin(jsonObj.getString(Constants.TAG_TIME_CHECKIN));

                return niboUser;
            }

            return null;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public NiboHouse searchPermanentNiboCode(String permanentNiboCode){
        try{
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(Constants.TAG_PERMANENT_NIBO_CODE, permanentNiboCode.trim()));

            JSONObject json = jsonParser.makeHttpRequest(Constants.SEARCH_PERMANENT_NIBO_CODE_URL, "GET", params);

            int result = json.getInt(Constants.TAG_CODE);

            if (result == 1){
                JSONArray messageObj = json.getJSONArray(Constants.TAG_HOUSE); // JSON Array

                // get first site object from JSON Array
                JSONObject jsonObj = messageObj.getJSONObject(0);

                NiboHouse niboHouse = new NiboHouse();

                niboHouse.setHouseAddress(jsonObj.getString(Constants.TAG_HOUSE_ADDRESS).trim());

                niboHouse.setLatLong(jsonObj.getString(Constants.TAG_LATLONG));

                niboHouse.setLocalGovernment(jsonObj.getString(Constants.TAG_LOCAL_GOVERNMENT));

                niboHouse.setNearestBusstop(jsonObj.getString(Constants.TAG_NEAREST_BUSSTOP));

                niboHouse.setNiboCode(jsonObj.getString(Constants.TAG_PERMANENT_NIBO_CODE));

                niboHouse.setState(jsonObj.getString(Constants.TAG_STATE));

                return niboHouse;
            }

            return null;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
