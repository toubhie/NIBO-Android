package com.nigeria.locateme.locateme.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nigeria.locateme.locateme.R;
import com.nigeria.locateme.locateme.utils.ConnectionDetector;

public class ConfirmationCodeActivity extends AppCompatActivity {


    private TextView submitButton;

    private EditText mEmailView;

    private ConnectionDetector connectionDetector;

    private String confirmationCode,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_code);


        mEmailView = (EditText) findViewById(R.id.confirmEmail);

        submitButton = (TextView) findViewById(R.id.submitButton);

        connectionDetector = new ConnectionDetector(getApplicationContext());

        Intent getConfirmationString = getIntent();

        confirmationCode = getConfirmationString.getStringExtra("confirmationCode");

        email = getConfirmationString.getStringExtra("Email");


        showSuccesssMessage(this, "Check Your Email", "A confirmation code has been sent to your mail. PLease input it here");

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = mEmailView.getText().toString().trim();

                if(connectionDetector.isURLReachable(ConfirmationCodeActivity.this)){
                    if(!mail.matches(confirmationCode)) {

                        Toast.makeText(ConfirmationCodeActivity.this,"Please input the correct Confirmation Code sent to your mail",Toast.LENGTH_SHORT).show();

                    }else{

                        Intent in = new Intent(ConfirmationCodeActivity.this, NewPasswordActivity.class);
                        in.putExtra("Email",email);
                        startActivity(in);
                    }
                }else{
                    Toast.makeText(ConfirmationCodeActivity.this,"Please check your Internet Connectivity",Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    public static void showSuccesssMessage(final Context context, final String title, final CharSequence text) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(title);
                builder.setMessage(text);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });
                builder.show();
            }
        });
    }
}
