package com.newmview.wifi.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mview.airtel.R;
import com.newmview.wifi.TinyDB;

/**
 * Created by Sharad Gupta on 10/11/2016.
 */
public class UploadDownloadSettings extends AppCompatActivity {

    Button saveBtn;
    EditText my_downloadurl,my_ftpserverurl,my_ftpusername,my_ftppwd, my_ftpdirtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.uploaddownloadsettings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        saveBtn = (Button)findViewById(R.id.saveBtn);
        my_downloadurl = (EditText)findViewById(R.id.my_downloadurl);
        my_ftpserverurl = (EditText)findViewById(R.id.my_ftpserverurl);
        my_ftpusername = (EditText)findViewById(R.id.my_ftpusername);
        my_ftppwd = (EditText)findViewById(R.id.my_ftppwd);
        my_ftpdirtxt = (EditText)findViewById(R.id.my_ftpdirtxt);

        TinyDB db = new TinyDB(UploadDownloadSettings.this);
        String t1 = db.getString("downloadurl");
        if( t1 == null || t1.equals(""))
            my_downloadurl.setText("http://speedtest.tele2.net/5MB.zip");

        else
            my_downloadurl.setText(t1);

        t1 = db.getString("ftpserverurl");
        if( t1 == null || t1.equals(""))
            my_ftpserverurl.setText("speedtest.tele2.net"); //hi-dj.com");
        else
            my_ftpserverurl.setText(t1);

        t1 = db.getString("ftpusername");
        if( t1 == null || t1.equals(""))
            my_ftpusername.setText("anonymous"); //hidjftp");
        else
            my_ftpusername.setText(t1);

        t1 = db.getString("ftppwd");
        if( t1 == null || t1.equals(""))
            my_ftppwd.setText("admin@mview.com"); //hidj@2015");
        else
            my_ftppwd.setText(t1);

        t1 = db.getString("ftpuploaddir");
        if( t1 == null || t1.equals(""))
            my_ftpdirtxt.setText("upload"); //hidj@2015");
        else
            my_ftpdirtxt.setText(t1);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TinyDB db = new TinyDB(UploadDownloadSettings.this);
                db.putString("downloadurl",my_downloadurl.getText().toString() );
                db.putString("ftpserverurl",my_ftpserverurl.getText().toString() );
                db.putString("ftpusername",my_ftpusername.getText().toString() );
                db.putString("ftppwd",my_ftppwd.getText().toString() );
                db.putString("ftpuploaddir",my_ftpdirtxt.getText().toString() );

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        UploadDownloadSettings.this,R.style.AlertDialogTheme);
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
