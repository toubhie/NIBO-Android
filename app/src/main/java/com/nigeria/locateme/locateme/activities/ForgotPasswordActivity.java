package com.nigeria.locateme.locateme.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nigeria.locateme.locateme.R;
import com.nigeria.locateme.locateme.entities.Contact;
import com.nigeria.locateme.locateme.utils.ConnectionDetector;
import com.nigeria.locateme.locateme.utils.ProgressBarUtil;
import com.nigeria.locateme.locateme.webservices.WebService;

public class ForgotPasswordActivity extends AppCompatActivity {


    private TextView submitButton;

    private EditText mEmailView;

    private ConnectionDetector connectionDetector;

    private ProgressBarUtil progressBarUtil;

    private View mProgressView;
    private View mLoginFormView;
    private String mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_actvity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mEmailView = (EditText) findViewById(R.id.email);

        submitButton = (TextView) findViewById(R.id.submit_button);

        connectionDetector = new ConnectionDetector(getApplicationContext());

        progressBarUtil = new ProgressBarUtil();

        mLoginFormView = findViewById(R.id.container);
        //loadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.progress_bar);
        mProgressView = findViewById(R.id.loading_frame);



        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mail = mEmailView.getText().toString();

                if(connectionDetector.isURLReachable(ForgotPasswordActivity.this)){
                    if(!mEmailView.getText().toString().trim().matches("(\\+[0-9]+[\\-\\.]*)?(\\([0-9]+\\)[\\- \\.]*)?([0-9][0-9\\- \\.]+[0-9])") &&
                            !mEmailView.getText().toString().trim().matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {

                        Toast.makeText(ForgotPasswordActivity.this,"Please input a valid mail",Toast.LENGTH_SHORT).show();

                    }else{
                        new CheckEmailPasswordRecovery().execute(mail);

                    }
                }else{
                    Toast.makeText(ForgotPasswordActivity.this,"Please check your Internet Connectivity",Toast.LENGTH_SHORT).show();
                }


            }
        });

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public class CheckEmailPasswordRecovery extends AsyncTask<String, Integer, String> {



        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = ProgressDialog.show(ForgotPasswordActivity.this, "Checking.", "Please wait...");
        }

        @Override
        protected String doInBackground(String... num) {
            String mail = num[0];

            String result;

            if (num != null){
                try {

                    WebService webService = new WebService();
                    result = webService.getUserEmailForPasswordRecovery(mail);

                    return result;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(String confirmationCode) {

            pd.dismiss();




            if (confirmationCode != null) {


                Intent in = new Intent(ForgotPasswordActivity.this, ConfirmationCodeActivity.class);
                in.putExtra("confirmationCode",confirmationCode);
                in.putExtra("Email",mail);
                startActivity(in);




                Toast.makeText(getApplicationContext(), "A Confirmation Code has been sent to your email. Please Insert Confirmation Code", Toast.LENGTH_LONG).show();


            } else{



                Toast.makeText(getApplicationContext(), "Email Isn't Registered on Nibo", Toast.LENGTH_SHORT).show();

            }

        }
    }

}
