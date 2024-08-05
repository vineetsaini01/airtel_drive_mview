package com.newmview.wifi.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.newmview.wifi.mView_HealthStatus;

/**
 * Created by Sharad Gupta on 11/14/2016.
 */

public class MainSettings extends AppCompatActivity {

    Button saveBtn;
    EditText updateintervaltxt, dashboardupdateinterval_txt, youtubevideo_txt, min_gsm_signal_txt;
    ToggleButton calllogstoggle, servicetoggle;
    private String calllogstext="";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainsettings_new);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        updateintervaltxt = (EditText) findViewById(R.id.updateintervaltxt);
        dashboardupdateinterval_txt = (EditText) findViewById(R.id.dashboardupdateinterval_txt);
        youtubevideo_txt = (EditText) findViewById(R.id.youtubevideo_txt);
        min_gsm_signal_txt = (EditText) findViewById(R.id.min_gsm_signal_txt);

        calllogstoggle = (ToggleButton) findViewById(R.id.calllogstoggle);
        servicetoggle = (ToggleButton) findViewById(R.id.servicetoggle);


        TinyDB db = new TinyDB(MainSettings.this);
        String t1 = db.getString("sendstateinterval");
        if (t1 == null || t1.equals("")) {
            updateintervaltxt.setText("300");
            db.putString("sendstateinterval", "300");
        } else
            updateintervaltxt.setText(t1);
//////////////////////////////////////////
        t1 = db.getString("dashboardupdate");
        if (t1 == null || t1.equals("")) {
            dashboardupdateinterval_txt.setText("5");
            db.putString("dashboardupdate", "5");
        } else
            dashboardupdateinterval_txt.setText(t1);
///////////////////////////////////////////////
        t1 = db.getString("youtube");
        if (t1 == null || t1.equals("")) {
           /* youtubevideo_txt.setText("https://www.youtube.com/watch?v=Sg64rEtDd4s");
            db.putString("youtube", "https://www.youtube.com/watch?v=Sg64rEtDd4s");*/
            youtubevideo_txt.setText("www.youtube.com/embed/HngTaeW9KVs");
            db.putString("youtube", "www.youtube.com/embed/HngTaeW9KVs");

        } else
            youtubevideo_txt.setText(t1);

////////////////////////////////////////////////////////////////////
        t1 = db.getString("mingsmsignalforcalldrop");
        if (t1 == null || t1.equals("")) {
            min_gsm_signal_txt.setText("12");

            db.putString("mingsmsignalforcalldrop", "12");
        } else
            min_gsm_signal_txt.setText(t1);
//////////////////////////////////////////////////////////////

        t1=db.getString("savecalllog");
        //Toast.makeText(this, "calllog "+t1, //Toast.LENGTH_SHORT).show();
        if (t1 == null || t1.equals("")) {
            calllogstoggle.setChecked(true);
            db.putString("savecalllog", "ON");
        } else if (t1.equals("ON"))
        {
            calllogstoggle.setChecked(true);
        }
        else
        {
            calllogstoggle.setChecked(false);
        }




      String  t = db.getString("startbackgroundservice");
        //Toast.makeText(this, "backgrnd service "+t, //Toast.LENGTH_SHORT).show();
        System.out.println("t is"+t);
        if (t == null || t.equals("")) {
            //servicetoggle.setText("ON");
            servicetoggle.setChecked(true);
            db.putString("startbackgroundservice", "ON");
        } else if (t.equals("ON"))
        {
                servicetoggle.setChecked(true);
            }
            else

        {
            servicetoggle.setChecked(false);
        }

        saveBtn = (Button) findViewById(R.id.saveBtn);


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TinyDB db = new TinyDB(MainSettings.this);
                db.putString("sendstateinterval",updateintervaltxt.getText().toString() );
                db.putString("dashboardupdate",dashboardupdateinterval_txt.getText().toString() );
                db.putString("youtube",youtubevideo_txt.getText().toString() );

                sharedPreferences = MainActivity.context.getSharedPreferences("Autorestart", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                //db.putString("savecalllog",calllogstoggle.getText().toString() );
                if(calllogstoggle.isChecked()) {
                    db.putString("savecalllog", "ON");
                }
                else
                {
                    db.putString("savecalllog","OFF");
                }



                db.putString("mingsmsignalforcalldrop",min_gsm_signal_txt.getText().toString() );
                if(servicetoggle.isChecked()) {
                    db.putString("startbackgroundservice", "ON");
                }
                else
                {
                    db.putString("startbackgroundservice","OFF");
                }

                mView_HealthStatus.periodicRefreshFrequencyInSeconds = Integer.parseInt(updateintervaltxt.getText().toString());
                mView_HealthStatus.updateDashboardUIIntervalInSeconds = Integer.parseInt(dashboardupdateinterval_txt.getText().toString());
                mView_HealthStatus.youtubeurl = youtubevideo_txt.getText().toString();




               // if (calllogstoggle.getText().toString() .equals("On") || calllogstoggle.getText().toString() .equals("ON")) {
                if(calllogstoggle.isChecked())
                {
                    mView_HealthStatus.writeCallLogs = true;
                } else {
                    mView_HealthStatus.writeCallLogs = false;
                }

                if (servicetoggle.isChecked()) {
                    //Toast.makeText(MainSettings.this, "toggle button checked and value being saved is "+" "+db.getString("startbackgroundservice"), //Toast.LENGTH_SHORT).show();
                    mView_HealthStatus.startbackgroundservice = true;

                } else {
                    //Toast.makeText(MainSettings.this, "toggle button unchecked and value being saved is "+" "+db.getString("startbackgroundservice"), //Toast.LENGTH_SHORT).show();

                    mView_HealthStatus.startbackgroundservice = false;
                   /* Intent bck=new Intent(MainSettings.this,Background_service.class);
                    Toast.makeText(MainSettings.this, "stopping background service=", Toast.LENGTH_SHORT).show();

                    stopService(bck);
               */ }

                mView_HealthStatus.MIN_GSM_SIGNAL_STRENGTH_FOR_CALL_DROP = Integer.parseInt(min_gsm_signal_txt.getText().toString());

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        MainSettings.this,R.style.AlertDialogTheme);
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
    }//end onCreate




    private void setupAlarm() {

        Intent bck=new Intent(MainSettings.this,Background_service.class);
        startService(bck);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}//end class
