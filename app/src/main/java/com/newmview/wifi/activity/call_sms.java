package com.newmview.wifi.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.mview.airtel.R;

import java.io.File;
import java.net.URISyntaxException;

import static com.newmview.wifi.activity.mView_UploadDownloadTest.getPathOfSelectedFile;
import static com.newmview.wifi.activity.mView_UploadDownloadTest.getPathOfThisUri;

/**
 * Created by Sharad Gupta on 11/2/2016.
 */

public class call_sms extends AppCompatActivity {

    ImageView addressbook_img;
    TextView smstext, mobile_txt, selectedfile;
    Button call_btn, sms_btn, mms_btn, select_btn;
    Context mcontext;
    int attachtype = 1;

    public static final int FILE_SELECT = 780;
    String selectedFilePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.call_sms_new);

        mcontext = this;
        addressbook_img = (ImageView)findViewById(R.id.addressbook_img);



        mobile_txt = (TextView)findViewById(R.id.mobile_txt);
        smstext = (TextView)findViewById(R.id.smstext);
        selectedfile = (TextView)findViewById(R.id.selectedfile);

        call_btn = (Button)findViewById(R.id.call_btn);
        sms_btn = (Button)findViewById(R.id.sms_btn);
        mms_btn = (Button)findViewById(R.id.mms_btn);
        select_btn = (Button)findViewById(R.id.select_btn);

        addressbook_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(call_sms.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED ) {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, 4);
                }else {
                    //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS,Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST);
                }
            }
        });

        select_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath();;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                Uri myUri = Uri.parse(folderPath);
                intent.setDataAndType(myUri, "*/*");
                startActivityForResult(intent, FILE_SELECT);
            }
        });

        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(call_sms.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED ) {
                    String mob = mobile_txt.getText().toString();
                    if( mob == null || mob.equals(""))
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                call_sms.this);
                        alertDialog.setTitle("Message");
                        alertDialog.setMessage("Please enter phone number");
                        alertDialog.setCancelable(true);

                        alertDialog.setNegativeButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });

                        alertDialog.show();
                    }else {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + mob));
                        startActivity(callIntent);
                    }
                }
            }
        });

        sms_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mob = mobile_txt.getText().toString();
                if( mob == null || mob.equals(""))
                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                            call_sms.this);
                    alertDialog.setTitle("Message");
                    alertDialog.setMessage("Please enter phone number");
                    alertDialog.setCancelable(true);

                    alertDialog.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
                    alertDialog.show();
                }else {
                    String msg = smstext.getText().toString();
                    if( msg == null || msg.equals("")) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                call_sms.this);
                        alertDialog.setTitle("Message");
                        alertDialog.setMessage("Please enter message to send");
                        alertDialog.setCancelable(true);

                        alertDialog.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                        alertDialog.show();
                    }else {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(mob, null, msg, null, null);
                        Toast.makeText(mcontext, "SMS Sent", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        mms_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( selectedFilePath == null || selectedFilePath.equals(""))
                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                            call_sms.this);
                    alertDialog.setTitle("Message");
                    alertDialog.setMessage("Please select file to attach");
                    alertDialog.setCancelable(true);

                    alertDialog.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
                    alertDialog.show();
                    return;
                }
                String msg = smstext.getText().toString();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("smsto:"));  // This ensures only SMS apps respond
                intent.putExtra("sms_body", msg);
                Uri uri = Uri.parse("file://"+selectedFilePath);
                File png = null;
                png = new File(selectedFilePath);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(png));

                String mob = mobile_txt.getText().toString();
                intent.putExtra("address",mob);
                //intent.setType("image/png");


                if( attachtype == 1)
                {
                    intent.setType("image/png");
//                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
//                    sendIntent.setClassName("com.android.mms", "com.android.mms.ui.ComposeMessageActivity");
//
//                    sendIntent.putExtra("sms_body",msg);
//                    String p = "file://" + selectedFilePath;
//                    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(p)); //"file:///sdcard/image_4.png"));
//                    sendIntent.setType("image/png");
//                    startActivity(sendIntent);;
                }else {
                    intent.setType("video/*");
//                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
//                    sendIntent.setClassName("com.android.mms", "com.android.mms.ui.ComposeMessageActivity");
//                    sendIntent.putExtra("address", "1213123123");
//                    sendIntent.putExtra("sms_body", "if you are sending text");
//                    final File file1 = new File(mFileName);
//                    if(file1.exists()){
//                        System.out.println("file is exist");
//                    }
//                    Uri uri = Uri.fromFile(file1);
//                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
//                    sendIntent.setType("video/*");
//                    startActivity(sendIntent);
                }
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent); }
            }
        });

    }//end onCreate

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(resultCode, resultCode, data);
        if (resultCode == this.RESULT_OK) {
            if(reqCode==4){
                if(data.getData()!=null){
                    Uri contactData = data.getData();
                    String[] numberArray=parseNumber(contactData,call_sms.this);

                    mobile_txt.setText(numberArray[1]);

                }
            } else  if(reqCode==FILE_SELECT){
                Uri uri = data.getData();

                String path="";
                try {
                    path = getPathOfSelectedFile(uri);
                } catch (URISyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if( path == null) {
                    path = getPathOfThisUri(this, uri);
                }

                if( path != null) {
                    selectedFilePath = path;
                    selectedfile.setText(path);
                    if(selectedFilePath.contains("jpg") || selectedFilePath.contains("png") || selectedFilePath.contains("bmp") )
                        attachtype = 1;
                    else if(selectedFilePath.contains("mp3") || selectedFilePath.contains("mp4") || selectedFilePath.contains("flv"))
                        attachtype = 2;
                }


            }//end else
        }//end RESULT_OK
    }//end function

    public static String[] parseNumber(Uri contactData, Context mycontext){
        String[] myArray= new String[2];
        String name="";
        String phoneNumberValid="";
        Cursor c = mycontext.getContentResolver().query(contactData, null, null, null, null);
        if (c.moveToFirst()) {
            String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
            String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if (hasPhone.equalsIgnoreCase("1")) {
                Cursor phones = mycontext.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                        null, null);
                phones.moveToFirst();


                if(!c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)).equals("")){
                    name=c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                }else{
                    name="";
                }



                phoneNumberValid=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                if(!phoneNumberValid.equals("")){
//                    phoneNumberValid= phoneNumberValid.replaceAll("\\s+","");
//                    if(phoneNumberValid.substring(0,1).equalsIgnoreCase("+")){
//                        if(phoneNumberValid.length()>=10){
//                            phoneNumberValid=phoneNumberValid.substring(phoneNumberValid.length()-10);
//                        }
//                    }
//
//
//                    if(phoneNumberValid.length()!=10){
//                        phoneNumberValid="";
//                        Toast.makeText(mycontext, "Not a valid mobile number", Toast.LENGTH_LONG).show();
//                    }
//                }else{
//                    phoneNumberValid="";
//                    Toast.makeText(mycontext, "Not a valid mobile number", Toast.LENGTH_LONG).show();
//                }

            }
        }

        myArray[0]=name;
        myArray[1]=phoneNumberValid;

        return myArray;
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
    public void onResume() {
        super.onResume();
//        final Button btn = ((AlertDialog)getDialog()).getButton(DialogInterface.BUTTON_NEUTRAL);
//        if (btn != null) {
//            if (Build.VERSION.SDK_INT >= 21) {
//                DisplayMetrics metrics = getResources().getDisplayMetrics();
//                int maxWidth = (metrics.widthPixels / 3);
//                btn.setSingleLine(false);
//                btn.setMaxWidth(maxWidth);
//                btn.setMaxLines(2);
//                btn.setEllipsize(TextUtils.TruncateAt.END);
//                // something in the above code reverts the allCaps
//                btn.setAllCaps(true);
//            }
//        }
    }
}
