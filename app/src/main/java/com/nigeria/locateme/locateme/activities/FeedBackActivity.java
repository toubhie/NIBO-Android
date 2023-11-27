package com.nigeria.locateme.locateme.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.nigeria.locateme.locateme.R;
import com.nigeria.locateme.locateme.entities.NiboUser;
import com.nigeria.locateme.locateme.entities.Preferences;
import com.nigeria.locateme.locateme.utils.ConnectionDetector;
import com.nigeria.locateme.locateme.utils.Constants;
import com.nigeria.locateme.locateme.utils.MessageUtil;
import com.nigeria.locateme.locateme.webservices.WebService;

public class FeedBackActivity extends AppCompatActivity {

    private EditText fullNameEditText;
    private EditText feedbackMessageEditText;

    private FloatingActionButton sendFeedbackFAB;

    private ProgressDialog pDialog;

    private WebService webService;

    private String fullName;
    private String feedbackMessage;

    private Intent intent;

    private NiboUser contact,getUserDetails;

    private Preferences pref;

    private ConnectionDetector connectionDetector;

    private String userName,userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }



        connectionDetector = new ConnectionDetector(getApplicationContext());
        pref = new Preferences(this);

        setUpViews();
        setUpListeners();


        intent = getIntent();

        if(intent != null){
            contact = (NiboUser) intent.getSerializableExtra(Constants.TAG_CONTACT_DETAIL);
        }
    }

    public void setUpViews(){
        fullNameEditText = (EditText) findViewById(R.id.feedback_fullName);
        feedbackMessageEditText = (EditText) findViewById(R.id.feedback_message);

        sendFeedbackFAB = (FloatingActionButton) findViewById(R.id.send_feedback_fab);

        getUserDetails = pref.getUserDetails();
        userName = getUserDetails.getFullName();
        userEmail = getUserDetails.getEmail();


        fullNameEditText.setText(userName);
    }

    public void setUpListeners(){
        sendFeedbackFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    try{
                        fullName = fullNameEditText.getText().toString();
                        feedbackMessage = feedbackMessageEditText.getText().toString();

                        if (connectionDetector.isURLReachable(FeedBackActivity.this)) {
                            if (valid()) {
                                showPreSendFeedbackDialog(contact);
                            }
                        } else {
                            Snackbar snackbar = Snackbar.make(v, "No Connection Found.", Snackbar.LENGTH_LONG)
                                    .setAction("RETRY", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            MessageUtil.showAlert(FeedBackActivity.this, "No Connection Found!", "Please check your connection and try again.");
                                        }
                                    })
                                    .setActionTextColor(Color.RED);

                            View sbView = snackbar.getView();
                            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.WHITE);
                            snackbar.show();
                        }

                    } catch(Exception e){
                        e.printStackTrace();
                    }

            }
        });
    }

    public void sendFeedBack(NiboUser contact){
        try{
            new SendFeedbackTask().execute(contact);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public class SendFeedbackTask extends AsyncTask<NiboUser, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(FeedBackActivity.this);
            pDialog.setMessage("Sending. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Integer doInBackground(NiboUser... params) {
            NiboUser contactPojo = params[0];
            int result = 0;

            try {
                webService = new WebService();
                result = webService.sendFeedback(FeedBackActivity.this, feedbackMessage, fullName, userEmail);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            pDialog.dismiss();

            if(result == 1){
                showSuccessAlert("Success!", fullName.toUpperCase() +
                        ", Thank you for your feedback. Please feel free to send us a message at any time. Thank you for using Nibo.");
            }

            else if(result == 0){
                MessageUtil.showAlert(FeedBackActivity.this, "An Error Occurred!", "Error sending feedback. Please Try again.");
            }
        }
    }

    private boolean valid() {
        boolean valid = true;
        if (fullNameEditText.getText().toString().trim().length() < 2) {
            MessageUtil.showAlert(this, "Invalid Name!", "Please provide a valid name");
            valid = false;
        } else if (feedbackMessageEditText.getText().toString().trim().length() < 5) {
            MessageUtil.showAlert(this, "Invalid Message!", "Please provide a valid message. It must be more than 5 characters");
            valid = false;
        }
        return valid;
    }

    public void showSuccessAlert(final String title, final CharSequence text) {
        FeedBackActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(FeedBackActivity.this);
                builder.setTitle(title);
                builder.setMessage(text);
                builder.setIcon(R.drawable.ic_verified_icon_new);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        Intent intent = new Intent(FeedBackActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.show();
            }
        });
    }

    public void showPreSendFeedbackDialog(final NiboUser contact) {
        FeedBackActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(FeedBackActivity.this);
                builder.setTitle("Warning");
                builder.setMessage("Are sure you want to send this message?")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                sendFeedBack(contact);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
}
