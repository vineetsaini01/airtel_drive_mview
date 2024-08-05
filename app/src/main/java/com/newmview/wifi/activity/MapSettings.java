package com.newmview.wifi.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.mview.airtel.R;
import com.newmview.wifi.TinyDB;

/**
 * Created by Sharad Gupta on 11/5/2016.
 */

public class MapSettings extends AppCompatActivity {

    ToggleButton tg1;
    Button saveBtn;
    EditText updateintervaltxt,fastestintervaltxt,displacmenttxt,markertxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.mapview_settingsnew);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        saveBtn = (Button) findViewById(R.id.saveBtn);
        tg1=(ToggleButton)findViewById(R.id.markerplottoggle);
        updateintervaltxt = (EditText) findViewById(R.id.updateintervaltxt);
        fastestintervaltxt = (EditText) findViewById(R.id.fastestintervaltxt);
        displacmenttxt = (EditText) findViewById(R.id.displacmenttxt);
        markertxt = (EditText) findViewById(R.id.markertxt);

        TinyDB db = new TinyDB(MapSettings.this);
        String t1 = db.getString("updateinterval");
        if( t1 == null || t1.equals("")) {
            updateintervaltxt.setText("10");
            db.putString("updateinterval", "10");
        }else
            updateintervaltxt.setText(t1);

        t1 = db.getString("fastestinterval");
        if( t1 == null || t1.equals("")) {
            fastestintervaltxt.setText("5");
            db.putString("fastestinterval", "5");
        }else
            fastestintervaltxt.setText(t1);

        t1 = db.getString("displacment");
        if( t1 == null || t1.equals("")) {
            displacmenttxt.setText("10");
            db.putString("displacment", "10");
        }else
            displacmenttxt.setText(t1);

        t1 = db.getString("plotMarkerAftermeters");
        if( t1 == null || t1.equals("")) {
            markertxt.setText("10");
            db.putString("plotMarkerAftermeters", "10");
        } else
            markertxt.setText(t1);

        t1 = db.getString("plotmarkers");
        if( t1 == null || t1.equals("")) {
            //tg1.setText("ON");
            tg1.setChecked(true);
            db.putString("plotmarkers", "ON");

        }else if(t1.equals("ON"))
        {
         tg1.setChecked(true);
        }
        else
        {
           tg1.setChecked(false);
        }
            //tg1.setText(t1);

        //tg1.getText();
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TinyDB db = new TinyDB(MapSettings.this);
                db.putString("updateinterval",updateintervaltxt.getText().toString() );
                db.putString("fastestinterval",fastestintervaltxt.getText().toString() );
                db.putString("displacment",displacmenttxt.getText().toString() );
                db.putString("plotMarkerAftermeters",markertxt.getText().toString() );
                if(tg1.isChecked()) {
                    db.putString("plotmarkers", "ON");
                }
                else
                {
                    db.putString("plotmarkers","OFF");
                }
                //db.putString("plotmarkers",tg1.getText().toString() );

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        MapSettings.this,R.style.AlertDialogTheme);
                alertDialog.setTitle("Settings");
                alertDialog.setMessage("Settings saved succesfully!!");
                alertDialog.setCancelable(true);

                alertDialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                                Intent returnIntent = new Intent();
                                //returnIntent.putExtra("result",result);
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                            }
                        });
                alertDialog.show();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
