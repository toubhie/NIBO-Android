package com.nigeria.locateme.locateme.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.nigeria.locateme.locateme.R;
import com.nigeria.locateme.locateme.entities.Preferences;

public class GetNiboCodeActivity extends AppCompatActivity {

    private Preferences prefManager;
    private Button gotIt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_nibo_code);


        prefManager = new Preferences(this);
        if (!prefManager.isFirstTimeLaunchForNiboCode()) {
            launchNiboCodeGetterScreen();
            finish();
        }


        gotIt = (Button) findViewById(R.id.btn_ok);

        gotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GetNiboCodeActivity.this, NiboCodeGetterActivity.class));
                finish();
            }
        });


    }

    private void launchNiboCodeGetterScreen() {
        prefManager.setFirstTimeLaunchForNiboCode(false);
        startActivity(new Intent(GetNiboCodeActivity.this, NiboCodeGetterActivity.class));
        finish();
    }
}
