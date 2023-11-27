package com.nigeria.locateme.locateme.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nigeria.locateme.locateme.R;
import com.nigeria.locateme.locateme.entities.Contact;
import com.nigeria.locateme.locateme.utils.ConnectionDetector;
import com.nigeria.locateme.locateme.utils.Constants;
import com.nigeria.locateme.locateme.webservices.WebService;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

public class FindFriendActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private AutoCompleteTextView mPhoneNumber;
    private Button mFindPerson;
    String phone_number;
    private String actionOptions[];
    private ConnectionDetector connectionDetector;
    String sendHouseAddress,sendLocalGovernment,sendState,sendStreet,sendLatLong;

    String sendFullName,sendAddress,sendLastCheckin,sendTimeCheckin,sendPhoneNumber;

    Tracker t;


    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        MyAnalytics application = (MyAnalytics) getApplication();
        t = application.getDefaultTracker();
        t.setScreenName("Search Nibo Code Activity");

        connectionDetector = new ConnectionDetector(getApplicationContext());

        mPhoneNumber = (AutoCompleteTextView) findViewById(R.id.phone_no_edt);
        mFindPerson = (Button) findViewById(R.id.find_person_button);
        populateAutoComplete();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        if(data != null){

            String niboCode = data.getQueryParameter("code");

            mPhoneNumber.setText(niboCode);

            //Toast.makeText(FindFriendActivity.this, niboCode, Toast.LENGTH_SHORT).show();



            if (connectionDetector.isURLReachable(getApplicationContext())) {
                attemptSearch();
            } else {
                Toast.makeText(FindFriendActivity.this, "Not connected to the internet", Toast.LENGTH_SHORT).show();
            }
        }










        mFindPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectionDetector.isURLReachable(getApplicationContext())) {
                    attemptSearch();
                } else {
                    Toast.makeText(FindFriendActivity.this, "Not connected to the internet", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mPhoneNumber, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }


    @Override
    protected void onPause() {
        super.onPause();
        //checkIntent();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        checkIntent();
    }

    private void checkIntent() {

        Intent getCord = getIntent();



        if(getCord.hasExtra(Constants.UNIQUE_CODE)){


            String niboNumber = getCord.getStringExtra(Constants.UNIQUE_CODE);

            mPhoneNumber.setText(niboNumber);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Phone
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> numbers = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            numbers.add(cursor.getString(ProfileQuery.DISPLAY_NAME));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(numbers);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.IS_PRIMARY,
        };

        int NUMBER = 0;
        int DISPLAY_NAME = 0;
        int IS_PRIMARY = 1;
    }

    private void addEmailsToAutoComplete(List<String> phoneNumberCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(FindFriendActivity.this,
                        android.R.layout.simple_dropdown_item_1line, phoneNumberCollection);

        mPhoneNumber.setAdapter(adapter);
    }

    public void attemptSearch(){


        boolean cancel = false;
        View focusView = null;

        phone_number = mPhoneNumber.getText().toString();

        if (TextUtils.isEmpty(phone_number)) {
            mPhoneNumber.setError(getString(R.string.error_field_required));
            focusView = mPhoneNumber;
            cancel = true;
        }


        if (phone_number.length() == 8 ) {

            Contact contactPojo = new Contact();
            contactPojo.setPhoneNumber(mPhoneNumber.getText().toString());


           /* mAuthTask = new UserLoginTask(email, password);
            new RegistrationTask().execute(contactPojo);
            mAuthTask.execute((Void) null);*/

            new SearchPermanentCodeTask().execute(phone_number);

            t.send(new HitBuilders.EventBuilder()
                    .setCategory("Clicks")  // category i.e. Buttons Action
                    .setAction("Button")    // action i.e.  Button Name
                    .setLabel("Find Nibo Code")    // label i.e.  any meta-data
                    .build());
        }


        /*if (phone_number.length() > 11) {
            mPhoneNumber.setError(getString(R.string.valid_number));
            focusView = mPhoneNumber;
            cancel = true;
        }*/


        if (cancel) {

            focusView.requestFocus();

        } else if (phone_number.length() == 7 ) {

            Contact contactPojo = new Contact();
            contactPojo.setPhoneNumber(mPhoneNumber.getText().toString());


           /* mAuthTask = new UserLoginTask(email, password);
            new RegistrationTask().execute(contactPojo);
            mAuthTask.execute((Void) null);*/

            new SearchTask().execute(phone_number);


        }


        else if (phone_number.length() != 7 && phone_number.length() != 8 && phone_number.length() > 2 ) {

            Contact contactPojo = new Contact();
            contactPojo.setPhoneNumber(mPhoneNumber.getText().toString());


           /* mAuthTask = new UserLoginTask(email, password);
            new RegistrationTask().execute(contactPojo);
            mAuthTask.execute((Void) null);*/

            new SearchSpecialCodeTask().execute(phone_number);


        }else{
            //Toast.makeText(getApplicationContext(), "Please enter a valid Nibo Code", Toast.LENGTH_SHORT).show();
        }

    }

    public class SearchTask extends AsyncTask<String, Integer, Contact>{



        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = ProgressDialog.show(FindFriendActivity.this, "Searching.", "Searching. Please wait...");
        }

        @Override
        protected Contact doInBackground(String... num) {
            String number = num[0];

            Contact result;

            if (num != null){
                try {

                    WebService webService = new WebService();
                    result = webService.getUserContactByPrimaryPhoneNumber(number);

                    return result;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Contact contact) {

            pd.dismiss();




            if (contact != null) {



                 sendFullName = contact.getFullName();
                sendPhoneNumber = contact.getPhoneNumber();
                sendAddress = contact.getAddress();
                sendLastCheckin = contact.getLastCheckin();
                sendTimeCheckin = contact.getTimeCheckin();

                showOptionsDialog();//comment this out and fire the intent straight away.

                /*if(sendLastCheckin.contains("null")){
                    Toast.makeText(FindFriendActivity.this, "Location details unavailable", Toast.LENGTH_SHORT).show();
                }else{

                    Intent in = new Intent(FindFriendActivity.this, MapsActivity.class);
                    in.putExtra("name",sendFullName);
                    in.putExtra("phoneNumber",sendPhoneNumber);
                    in.putExtra("lastCheckin",sendLastCheckin);
                    in.putExtra("timeCheckin"," As at "+sendTimeCheckin);
                    startActivity(in);
                }*/


                Toast.makeText(getApplicationContext(), "Found Friend", Toast.LENGTH_SHORT).show();


            } else{

                if(phone_number.length() == 11){


                    ((Activity) FindFriendActivity.this).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            AlertDialog.Builder sendSmsBuilder = new AlertDialog.Builder(new ContextThemeWrapper(FindFriendActivity.this, R.style.myDialog));
                            sendSmsBuilder.setTitle("Person not registered");
                            sendSmsBuilder.setMessage("Send an sms invite link to person to download locateMe.");
                            sendSmsBuilder.setCancelable(false);
                            sendSmsBuilder.setPositiveButton("Send SMS", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    sendSmsInviteLink();
                                }
                            });
                            sendSmsBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            sendSmsBuilder.show();

                        }
                    });
                }else{

                    new SearchSpecialCodeTask().execute(phone_number);
                    //Toast.makeText(getApplicationContext(), "Unique Code doesn't exist", Toast.LENGTH_SHORT).show();
                }



            }

        }
    }

    public class SearchPermanentCodeTask extends AsyncTask<String, Integer, Contact>{



        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = ProgressDialog.show(FindFriendActivity.this, "Searching.", "Searching. Please wait...");
        }

        @Override
        protected Contact doInBackground(String... num) {
            String number = num[0];

            Contact result;

            if (num != null){
                try {

                    WebService webService = new WebService();
                    result = webService.getUserPermanentHouseCode(number);

                    return result;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Contact contact) {

            pd.dismiss();




            if (contact != null) {



                sendHouseAddress = contact.getHouseAddress();
                sendLocalGovernment = contact.getLocalGovernment();
                sendState = contact.getState();
                sendStreet = contact.getStreet();
                sendLatLong = contact.getLatLong();

                showHouseDetailsPreviewDialog(contact);


                Toast.makeText(getApplicationContext(), "Found House", Toast.LENGTH_SHORT).show();


            } else{

                if(phone_number.length() == 11){


                    ((Activity) FindFriendActivity.this).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            AlertDialog.Builder sendSmsBuilder = new AlertDialog.Builder(new ContextThemeWrapper(FindFriendActivity.this, R.style.myDialog));
                            sendSmsBuilder.setTitle("Person not registered");
                            sendSmsBuilder.setMessage("Send an sms invite link to person to download locateMe.");
                            sendSmsBuilder.setCancelable(false);
                            sendSmsBuilder.setPositiveButton("Send SMS", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    sendSmsInviteLink();
                                }
                            });
                            sendSmsBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            sendSmsBuilder.show();

                        }
                    });
                }else{

                    new SearchSpecialCodeTask().execute(phone_number);
                    //Toast.makeText(getApplicationContext(), "Unique Code doesn't exist", Toast.LENGTH_SHORT).show();
                }



            }

        }
    }

    public class SearchSpecialCodeTask extends AsyncTask<String, Integer, Contact>{



        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = ProgressDialog.show(FindFriendActivity.this, "Searching.", "Searching. Please wait...");
        }

        @Override
        protected Contact doInBackground(String... num) {
            String number = num[0];

            Contact result;

            if (num != null){
                try {

                    WebService webService = new WebService();
                    result = webService.getUserSpecialHouseCode(number);

                    return result;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Contact contact) {

            pd.dismiss();




            if (contact != null) {



                sendHouseAddress = contact.getHouseAddress();
                sendLocalGovernment = contact.getLocalGovernment();
                sendState = contact.getState();
                sendStreet = contact.getStreet();
                sendLatLong = contact.getLatLong();

                showHouseDetailsPreviewDialog(contact);


                Toast.makeText(getApplicationContext(), "Found House", Toast.LENGTH_SHORT).show();


            } else{

                if(phone_number.length() == 11){


                    ((Activity) FindFriendActivity.this).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            AlertDialog.Builder sendSmsBuilder = new AlertDialog.Builder(new ContextThemeWrapper(FindFriendActivity.this, R.style.myDialog));
                            sendSmsBuilder.setTitle("Person not registered");
                            sendSmsBuilder.setMessage("Send an sms invite link to person to download locateMe.");
                            sendSmsBuilder.setCancelable(false);
                            sendSmsBuilder.setPositiveButton("Send SMS", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    sendSmsInviteLink();
                                }
                            });
                            sendSmsBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            sendSmsBuilder.show();

                        }
                    });
                }else{

                    Toast.makeText(getApplicationContext(), "Unique Code doesn't exist", Toast.LENGTH_SHORT).show();
                }



            }

        }
    }

    private void sendSmsInviteLink() {
        //phone_number

        String message = "Hi Friend, download Nibo from google play.. its FREE " + "\n" + "http://www.niboapp.com   ";

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phone_number));
        intent.putExtra("sms_body", message);
        startActivity(intent);

    }

    private void showHouseDetailsPreviewDialog(final Contact contact){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("House Details:");

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.contact_preview, null);
        builder.setView(dialogView);


        final TextView housePreviewState = (TextView) dialogView.findViewById(R.id.housePreview_state);
        final TextView housePreviewLocalGovernment = (TextView) dialogView.findViewById(R.id.housePreview_localGovernment);
        final TextView housePreviewStreet = (TextView) dialogView.findViewById(R.id.housePreview_street);
        final TextView housePreviewHouseAddress = (TextView) dialogView.findViewById(R.id.housePreview_houseAddress);
        //final TextView contactPreviewJobTitle = (TextView) dialogView.findViewById(R.id.contactPreview_jobTitle);

        final TableRow housePreviewStateTableRow = (TableRow) dialogView.findViewById(R.id.housePreview_stateTableRow);
        final TableRow housePreviewLocalGovernmentTableRow = (TableRow) dialogView.findViewById(R.id.housePreview_LGATableRow);
        final TableRow housePreviewStreetTableRow = (TableRow) dialogView.findViewById(R.id.housePreview_streetTableRow);
        final TableRow housePreviewHouseAddressTableRow = (TableRow) dialogView.findViewById(R.id.housePreview_houseAddressTableRow);
        //final TableRow contactPreviewJobTitleTableRow = (TableRow) dialogView.findViewById(R.id.contactPreview_jobTitleTableRow);

        if(contact.getState() != null && !contact.getState().equalsIgnoreCase("")){
            housePreviewState.setText(contact.getState() != null ? contact.getState() : "");
        } else{
            housePreviewStateTableRow.setVisibility(View.GONE);
        }

        if(contact.getLocalGovernment() != null && !contact.getLocalGovernment().equalsIgnoreCase("")){
            housePreviewLocalGovernment.setText(contact.getLocalGovernment() != null ? contact.getLocalGovernment() : "");
        } else{
            housePreviewLocalGovernmentTableRow.setVisibility(View.GONE);
        }

        if(contact.getStreet() != null && !contact.getStreet().equalsIgnoreCase("")){
            housePreviewStreet.setText(contact.getStreet() != null ? contact.getStreet() : "");
        } else{
            housePreviewStreetTableRow.setVisibility(View.GONE);
        }

        if(contact.getHouseAddress() != null && !contact.getHouseAddress().equalsIgnoreCase("")){
            housePreviewHouseAddress.setText(contact.getHouseAddress() != null ? contact.getHouseAddress() : "");
        } else{
            housePreviewHouseAddressTableRow.setVisibility(View.GONE);
        }


        builder.setPositiveButton("Open", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {


                Intent in = new Intent(FindFriendActivity.this, MapsActivity.class);
                // Do something with the selection


                    if(sendLatLong.contains("null")){
                        Toast.makeText(FindFriendActivity.this, "Nibo Code Not Yet Registered", Toast.LENGTH_SHORT).show();
                        in.putExtra("activateCode","ActivateCode");
                        in.putExtra("niboCode",phone_number);
                        startActivity(in);
                    }else{

                        //I ALREADY HAVE A FUNTION RECEIVING SIMILAR INTENT SO ID JUST WORK WITH THE TAGS THERE


                        in.putExtra("name","The House");
                        in.putExtra("phoneNumber",sendLocalGovernment);
                        in.putExtra("lastCheckin",sendLatLong);
                        in.putExtra("timeCheckin",sendHouseAddress);
                        startActivity(in);
                    }






            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog b = builder.create();
        b.show();




        /*Resources res = getResources();
        actionOptions = res.getStringArray(R.array.action_location_options);
        builder.setItems(R.array.action_location_options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                Intent in = new Intent(FindFriendActivity.this, MapsActivity.class);
                // Do something with the selection
                if(actionOptions[item].equalsIgnoreCase("Home Address")){


                    if(sendLatLong.contains("null")){
                        Toast.makeText(FindFriendActivity.this, "House details unavailable", Toast.LENGTH_SHORT).show();
                    }else{

                        in.putExtra("name",sendFullName);
                        in.putExtra("phoneNumber",sendPhoneNumber);
                        in.putExtra("lastCheckin",sendAddress);
                        in.putExtra("timeCheckin","Home Address");
                        startActivity(in);
                    }



                }
                else if(actionOptions[item].equalsIgnoreCase("Last seen Location")){

                    if(sendLastCheckin.contains("null")){
                        Toast.makeText(FindFriendActivity.this, "Location details unavailable", Toast.LENGTH_SHORT).show();
                    }else{

                        in.putExtra("name",sendFullName);
                        in.putExtra("phoneNumber",sendPhoneNumber);
                        in.putExtra("lastCheckin",sendLastCheckin);
                        in.putExtra("timeCheckin"," As at "+sendTimeCheckin);
                        startActivity(in);
                    }



                }
            }

        });
        AlertDialog alert = builder.create();
        alert.show();*/
    }



    private void showOptionsDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(sendFullName+"'s Current Location:");


        Resources res = getResources();
        actionOptions = res.getStringArray(R.array.action_location_options);
        builder.setItems(R.array.action_location_options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                Intent in = new Intent(FindFriendActivity.this, MapsActivity.class);
                // Do something with the selection
                /*if(actionOptions[item].equalsIgnoreCase("Home Address")){



                    if(sendAddress.length() != 8){
                        Toast.makeText(FindFriendActivity.this, "House details unavailable", Toast.LENGTH_SHORT).show();
                    }else{



                        //mPhoneNumber.setText(sendLatLong);
                        new SearchPermanentCodeTask().execute(sendAddress);
                        //Start Find Friend Activity and pass Nibo code


                    }

                    *//*if(sendLatLong.contains("null")){
                        Toast.makeText(FindFriendActivity.this, "House details unavailable", Toast.LENGTH_SHORT).show();
                    }else{

                        in.putExtra("name",sendFullName);
                        in.putExtra("phoneNumber",sendPhoneNumber);
                        in.putExtra("lastCheckin",sendAddress);
                        in.putExtra("timeCheckin","Home Address");
                        startActivity(in);
                    }*//*



                }*/
                if(actionOptions[item].equalsIgnoreCase("Open In Maps")){

                    if(sendLastCheckin.contains("null")){
                        Toast.makeText(FindFriendActivity.this, "Location details unavailable", Toast.LENGTH_SHORT).show();
                    }else{

                        in.putExtra("name",sendFullName);
                        in.putExtra("phoneNumber",sendPhoneNumber);
                        in.putExtra("lastCheckin",sendLastCheckin);
                        in.putExtra("timeCheckin"," As at "+sendTimeCheckin);
                        startActivity(in);
                    }



                }
            }

        });
        AlertDialog alert = builder.create();
        alert.show();
    }


}
