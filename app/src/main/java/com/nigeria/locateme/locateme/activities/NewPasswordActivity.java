package com.nigeria.locateme.locateme.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nigeria.locateme.locateme.R;
import com.nigeria.locateme.locateme.utils.ConnectionDetector;
import com.nigeria.locateme.locateme.webservices.WebService;

public class NewPasswordActivity extends AppCompatActivity {

    private TextView submitButton;

    private EditText mEmailView,mEmailPassword;

    private ConnectionDetector connectionDetector;

    private String email;

    private int result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);


        mEmailView = (EditText) findViewById(R.id.edtnewpassword);

        mEmailPassword = (EditText) findViewById(R.id.edtnewpasswordconfirm);

        submitButton = (TextView) findViewById(R.id.sumitnewpassword);

        connectionDetector = new ConnectionDetector(getApplicationContext());


        Intent getConfirmationString = getIntent();

        email = getConfirmationString.getStringExtra("Email");

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = mEmailView.getText().toString().trim();
                String passwordconfirm = mEmailPassword.getText().toString().trim();

                if(connectionDetector.isURLReachable(NewPasswordActivity.this)){
                    if(password.length() > 4 && !password.matches(" ") && password.matches(passwordconfirm)) {

                        new UpdateNewPassword().execute(password);



                    }else{

                        Toast.makeText(NewPasswordActivity.this,"Password must not be empty, must be more than 4 characters and password must match",Toast.LENGTH_SHORT).show();

                    }
                }else{
                    Toast.makeText(NewPasswordActivity.this,"Please check your Internet Connectivity",Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    public class UpdateNewPassword extends AsyncTask<String, Integer, String> {



        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = ProgressDialog.show(NewPasswordActivity.this, "Updating Password.", "Please wait...");
        }

        @Override
        protected String doInBackground(String... num) {
            String password = num[0];

            String msg = "";

            if (num != null){
                try {

                    WebService webService = new WebService();
                    result = webService.updatePassword(email,password);

                    return msg;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(String confirmationCode) {

            pd.dismiss();




            if (result == 1) {


                Intent in = new Intent(NewPasswordActivity.this, LoginActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);



                //Toast.makeText(getApplicationContext(), "A Confirmation Code has been sent to your email. Please Insert Confirmation Code", Toast.LENGTH_LONG).show();


            } else{



                Toast.makeText(getApplicationContext(), "Error Updating Password", Toast.LENGTH_SHORT).show();

            }

        }
    }


}
