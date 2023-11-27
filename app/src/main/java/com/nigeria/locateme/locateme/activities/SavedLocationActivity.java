package com.nigeria.locateme.locateme.activities;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.nigeria.locateme.locateme.R;
import com.nigeria.locateme.locateme.utils.DBHelper;
import com.nigeria.locateme.locateme.utils.DisplayUtil;
import com.nigeria.locateme.locateme.utils.SQLController;

public class SavedLocationActivity extends AppCompatActivity {

    ListView lv;
    SQLController sq = null;
    TextView getlistid, getlistname, getlistlat,getlistlong;
    private String getlistidstr, getlistnamestr, getlistlatstr, getlistlongstr;
    private String sqlActionOptions[];

    private DisplayUtil displayUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        sq = new SQLController(this);
        getAllSavedLocations();


        displayUtil = new DisplayUtil();

        displayUtil.setStatusBarColor(this,R.color.colorPrimaryDark);
    }

    private void getAllSavedLocations() {

        lv = (ListView)findViewById(R.id.list);
        Cursor cur = sq.readData();
        String[] from = new String[] { DBHelper.USER_ID, DBHelper.USER_NAME, DBHelper.USER_LAT, DBHelper.USER_LONGI };
        int[] to = new int[] { R.id.id, R.id.name, R.id.lattitude, R.id.longitude};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter( SavedLocationActivity.this, R.layout.customlayout, cur, from, to);
        adapter.notifyDataSetChanged();
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                getlistid = (TextView) v.findViewById(R.id.id);
                getlistname = (TextView) v.findViewById(R.id.name);
                getlistlat = (TextView) v.findViewById(R.id.lattitude);
                getlistlong = (TextView) v.findViewById(R.id.longitude);


                getlistidstr = getlistid.getText().toString();
                getlistnamestr = getlistname.getText().toString();
                getlistlatstr = getlistlat.getText().toString();
                getlistlongstr = getlistlong.getText().toString();

                final long sqId = Long.parseLong(getlistidstr);

                showShareLocationDialog(sqId);



            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent,  View v, int position, long id) {


                getlistid = (TextView)v.findViewById(R.id.id);
                getlistidstr = getlistid.getText().toString();
                final long sqId = Long.parseLong(getlistidstr);

                showShareLocationDialog(sqId);



                return false;
            }
        });

    }

    public void editLocationDialog(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.enter_name_layout, null);
        dialogBuilder.setView(dialogView);

        final EditText nameBoxInDialog = (EditText) dialogView.findViewById(R.id.name);

        dialogBuilder.setTitle("Edit name");
        dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String locName = nameBoxInDialog.getText().toString();

                if (locName.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please provide a new name", Toast.LENGTH_SHORT).show();
                }
                else{
                    //dialog.dismiss();
                    //save to database

                    //String concLatLong = latitude + "," + longitude;

                    final long id = Long.parseLong(getlistidstr);

                    sq.update(id,locName);
                    //sq.insertData(locName,latitude,longitude);
                    Toast.makeText(getApplicationContext(), "Successfully saved", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    Intent i = new Intent(
                            SavedLocationActivity.this,
                            SavedLocationActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                }

            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void showShareLocationDialog(final long sqId){



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Action:");

        Resources res = getResources();
        sqlActionOptions = res.getStringArray(R.array.sql_action_options);
        builder.setItems(R.array.sql_action_options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection

                if(sqlActionOptions[item].equalsIgnoreCase("Open In Map")){


                    Intent upd = new Intent(SavedLocationActivity.this, MapsActivity.class);
                    upd.putExtra("name", getlistnamestr);
                    upd.putExtra("lat", getlistlatstr);
                    upd.putExtra("long", getlistlongstr);
                    /*upd.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    upd.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
                    startActivity(upd);

                }
                else if(sqlActionOptions[item].equalsIgnoreCase("Navigate")){

                    navigateToLocation();

                }
                else if(sqlActionOptions[item].equalsIgnoreCase("Edit")){

                    editLocationDialog();

                }
                else if(sqlActionOptions[item].equalsIgnoreCase("Delete")){
                    sq.delete(sqId);
                    Toast.makeText(getApplicationContext(),"Deleted Successfully",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(
                            SavedLocationActivity.this,
                            SavedLocationActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                else if(sqlActionOptions[item].equalsIgnoreCase("Share Location")){

                    Intent i = new Intent(android.content.Intent.ACTION_SEND);
                    i.setType("text/plain");
                    String location = getlistlatstr+","+getlistlongstr;
                    i.putExtra(Intent.EXTRA_TEXT, "Hi, this is " + getlistnamestr + ":" + "\n" + "http://maps.google.com/maps?q=loc:" + location + "\n" + "Sent from Nibo");
                    try {
                        startActivity(Intent.createChooser(i, "Share Location"));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(SavedLocationActivity.this, "Not found any app", Toast.LENGTH_LONG).show();
                    }


                }


            }

        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void navigateToLocation(){
        String uri = "http://maps.google.com/maps?daddr=" + getlistlatstr + "," + getlistlongstr + " (" + getlistnamestr + ")";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        try
        {
            startActivity(intent);
        }
        catch(ActivityNotFoundException ex)
        {
            try
            {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(unrestrictedIntent);
            }
            catch(ActivityNotFoundException innerEx)
            {
                Toast.makeText(this, "Please install a maps application", Toast.LENGTH_LONG).show();
            }
        }
    }
}
