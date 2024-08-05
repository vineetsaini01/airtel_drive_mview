package com.newmview.wifi.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.mview.airtel.R;
import com.newmview.wifi.TinyDB;
import com.newmview.wifi.mView_HealthStatus;

/**
 * Created by Sharad Gupta on 11/14/2016.
 */

public class MainSettingsFragment extends Fragment {

    Button saveBtn;
    EditText updateintervaltxt, dashboardupdateinterval_txt, youtubevideo_txt, min_gsm_signal_txt;
    ToggleButton calllogstoggle, servicetoggle;
    private String calllogstext="";
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.mainsettings_new,container,false);
        updateintervaltxt = (EditText)view.findViewById(R.id.updateintervaltxt);
        dashboardupdateinterval_txt = (EditText)view.findViewById(R.id.dashboardupdateinterval_txt);
        youtubevideo_txt = (EditText)view.findViewById(R.id.youtubevideo_txt);
        min_gsm_signal_txt = (EditText)view.findViewById(R.id.min_gsm_signal_txt);

        calllogstoggle = (ToggleButton)view.findViewById(R.id.calllogstoggle);
        servicetoggle = (ToggleButton)view.findViewById(R.id.servicetoggle);


        TinyDB db = new TinyDB(getActivity());
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
/////////////////////////////////////////////////////////////

        t1=db.getString("savecalllog");
        //   //Toast.makeText(getActivity(), "calllog "+t1, //Toast.LENGTH_SHORT).show();
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
        //     //Toast.makeText(this, "backgrnd service "+t, //Toast.LENGTH_SHORT).show();
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

        saveBtn = (Button)view.findViewById(R.id.saveBtn);


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TinyDB db = new TinyDB(getActivity());
                db.putString("sendstateinterval",updateintervaltxt.getText().toString() );
                db.putString("dashboardupdate",dashboardupdateinterval_txt.getText().toString() );
                db.putString("youtube",youtubevideo_txt.getText().toString() );
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
                    autorestart();
                } else {
                    //Toast.makeText(MainSettings.this, "toggle button unchecked and value being saved is "+" "+db.getString("startbackgroundservice"), //Toast.LENGTH_SHORT).show();

                    mView_HealthStatus.startbackgroundservice = false;
                }

                mView_HealthStatus.MIN_GSM_SIGNAL_STRENGTH_FOR_CALL_DROP = Integer.parseInt(min_gsm_signal_txt.getText().toString());

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        getActivity(),R.style.AlertDialogTheme);
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
                                getActivity().setResult(Activity.RESULT_OK, returnIntent);
                                getActivity().finish();
                            }
                        });
                alertDialog.show();

            }
        });

        return view;
    }

    private void autorestart() {

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
            getActivity().onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}//end class
