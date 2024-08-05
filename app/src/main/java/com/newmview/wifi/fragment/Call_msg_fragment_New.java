package com.newmview.wifi.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telecom.Call;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.util.Util;
import com.newmview.wifi.MyPhoneStateListener;
import com.newmview.wifi.MyReceiver2;
import com.newmview.wifi.NewPhoneStateListner;
import com.mview.airtel.R;
import com.newmview.wifi.activity.MainActivity;
import com.newmview.wifi.helper.CallOutput;
import com.newmview.wifi.helper.Call_State_Helper;
import com.newmview.wifi.listenService;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;

import java.io.File;
import java.net.URISyntaxException;

import static android.content.Context.ALARM_SERVICE;
import static androidx.core.content.ContextCompat.checkSelfPermission;
import static com.newmview.wifi.activity.MainActivity.context;
import static com.newmview.wifi.activity.mView_UploadDownloadTest.getPathOfSelectedFile;
import static com.newmview.wifi.activity.mView_UploadDownloadTest.getPathOfThisUri;

/**
 * Created by Sharad Gupta on 11/2/2016.
 */

public class Call_msg_fragment_New extends Fragment implements View.OnClickListener {

    private static final String START_TEST = "start_test";
    private static final int CALL_PERMISSION = 12;
    private static final int RECORD_PERMISSION = 13;
    private static final int PHONECALL_PERMISSION_CODE= 14;
    ImageView addressbook_img;
    TextView smstext, mobile_txt, selectedfile;
    Button call_btn, sms_btn, mms_btn, select_btn;
    Context mcontext;
    int attachtype = 1;
    private Call call;
    public static final int FILE_SELECT = 780;
    String selectedFilePath = "";
    private View view;
    private BroadcastReceiver smsreceiversent;
    private MyPhoneStateListener myPhoneStateListener;
    private ProgressDialog progressDialog;
    private String type;
    String lat, lon;
    private LinearLayout call_main_ll;
    private RelativeLayout call_start_rl;
    private Button call_start_btn;
    private Button cancel_btn;
    private Button showOutput_btn;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.call_sms_auto_delete, container, false);
        init();
        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        boolean readPhoneStatePermissionDenied = false;
        myPhoneStateListener = new MyPhoneStateListener(getActivity(), telephonyManager, readPhoneStatePermissionDenied);
        System.out.println("in on create view of call");
        initializeClickListeners();
        return view;
    }

    private void initializeClickListeners() {

        mms_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFilePath == null || selectedFilePath.equals("")) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                            getActivity(), R.style.AlertDialogTheme);
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
                Uri uri = Uri.parse("file://" + selectedFilePath);
                File png = null;
                png = new File(selectedFilePath);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(png));

                String mob = mobile_txt.getText().toString();
                intent.putExtra("address", mob);
                //intent.setType("image/png");


                if (attachtype == 1) {
                    intent.setType("image/png");

                } else {
                    intent.setType("video/*");

                }
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });


        addressbook_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekPermissions();


            }
        });


    }

    private void readContactList() {
        if (checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, 4);
        } else {
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS,Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST);
        }

    }



    private void initiateCallProcess() {
        if (Utils.checkContext(getActivity())) {
            if (checkSelfPermission(getActivity(), Manifest.permission.CAPTURE_AUDIO_OUTPUT) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED )
                    {
                requestPermissions(new String[]{
                        Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAPTURE_AUDIO_OUTPUT}, RECORD_PERMISSION);
            } else {

                startCall();
            }
        }

            /*if (checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            }*/


    }
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(getActivity(), new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(getActivity(), "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }
    private void startCall() {
        System.out.println(" entering call test");
        String mob = mobile_txt.getText().toString();
        if (!Utils.checkifavailable(mob)) {
            // by swapnil bansal 01/02/2023
            // showAlertDialog("Message", "Please enter phone number", "");
            showCallAlertDialog("Message", "Please enter phone number", "");

        }
        else {
            System.out.println(" entering call test1" + Utils.getDateTime());

            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED ) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CALL_PHONE},
                        22);
            }
            else {
                //You already have permission
                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + mob));
                    startActivity(callIntent);
                    TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                    if (telephonyManager != null) {
                        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
                    }

//                    Call_State_Helper.call_source = "App";
//                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
//
//                    Call_State_Helper.startCallService(getActivity());
//                   checkPermission(Manifest.permission.ANSWER_PHONE_CALLS, PHONECALL_PERMISSION_CODE);
                 //   final Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @RequiresApi(api = Build.VERSION_CODES.P)
//                        @Override
//                        public void run() {
//                            TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
//                            if (tm != null) {
//                                boolean success = tm.endCall();
//                                System.out.println(" entering call test2"+Utils.getDateTime());
//                                showProgressBeforeLoadingData("Computing Call Result" + "\n" + "This may take upto 10 seconds");
//                                // showCallAlertDialogNew("Message", "Click on  OK to see the call result", "");
//                            }
//                        }
//
//                    }, 25000);

                }
                catch (SecurityException e) {
                    e.printStackTrace();
                }


            }


        }
        if (MainActivity.REPORT_FLAG) {
            if (type == null) {
                type = "";
            }
            Utils.showReportIssueAlert(type, getActivity(), lat, lon, Constants.CALL_TEST_DONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    private void showProgressBeforeLoadingData(String message) {
        System.out.println(" entering progress dialog ");

        {
            System.out.println(" entering progress dialog one ");
            progressDialog = new ProgressDialog(getActivity());
            if (!progressDialog.isShowing())
            {
                progressDialog.setMessage(message);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setButton(ProgressDialog.BUTTON_POSITIVE,
                        "Click to view the result",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new CallOutput("Ping").showCallAlert(context);
                            }
                        });

                progressDialog.show();
            }
        }
    }

    public void startDial(int milliseconds, String phoneNumber){

        //performs call
        if (!phoneNumber.equals("")) {
            int pendingFlags;if (Util.SDK_INT >= 23) {
                pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
            } else {
                pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT;
            }
           // PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, pendingFlags);
            // PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0)
            Intent intent = new Intent(context, MyReceiver2.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, pendingFlags);
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + milliseconds, pi);
            Uri number = Uri.parse("tel:" + phoneNumber);
            Intent dial = new Intent(Intent.ACTION_CALL, number);
            startActivity(dial);
        }
    }

    private void initiateSmsProcess() {

        String mob = mobile_txt.getText().toString();
        if(!Utils.checkifavailable(mob))
        {
            // by swapnil bansal 01/02/2023
           // showAlertDialog("Message","Please enter phone number!","");
            showCallAlertDialog("Message","Please enter phone number!","");

        }else {
            String msg = smstext.getText().toString();
            if(!Utils.checkifavailable(msg)) {
                // by swapnil bansal 01/02/2023
               // showAlertDialog("Message","Please enter message to send","");
                showCallAlertDialog("Message","Please enter message to send","");
            }else {
                try {
                    showProgress();
                    sendSms();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    System.out.println("exception in sending sms "+e.toString());
                }

            }
        }



    }

    private void showProgress() {
        progressDialog=new ProgressDialog(getActivity(),ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setMessage("Sending..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void sendSms() {
        String SENT = "SMS_SENT";
           String message = smstext.getText().toString();
           String mob = mobile_txt.getText().toString();


            PendingIntent sentIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(SENT), 0);


            smsreceiversent = new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent arg1) {

                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            dismissProgress();
                            Toast.makeText(getActivity(), "SMS Sent", Toast.LENGTH_LONG).show();
                            break;

                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            dismissProgress();
                            // by swapnil bansal 01/02/2023
                         //   showAlertDialog("Call/Sms/Mms test", Constants.GENERIC_FAILURE,"");
                            showCallAlertDialog("Call/Sms/Mms test", Constants.GENERIC_FAILURE,"");
                            break;

                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            dismissProgress();
                            // by swapnil bansal 01/02/2023
                           // showAlertDialog("Call/Sms/Mms test", Constants.NO_SERVICE,"");
                            showCallAlertDialog("Call/Sms/Mms test", Constants.NO_SERVICE,"");
                            break;


                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            dismissProgress();
                            // by swapnil bansal 01/02/2023
                          //  showAlertDialog("Call/Sms/Mms test", Constants.ENABLE_NTWRK,"");
                            showCallAlertDialog("Call/Sms/Mms test", Constants.ENABLE_NTWRK,"");
                            break;
                }

                }
            };

            getActivity().registerReceiver(smsreceiversent, new IntentFilter(SENT));
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(mob, null, message, sentIntent, null);

    }

    private void dismissProgress() {
        if(progressDialog!=null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
    }

    private void unregisterReceiver() {

            try {
                if(smsreceiversent!=null)
                getActivity().unregisterReceiver(smsreceiversent);
            }
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            }


    }

    // by swapnil bansal 01/02/2023
    private void showAlertDialog(String title,String message,String purpose) {
        AlertDialog.Builder alert=new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setCancelable(true);
        alert.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(Utils.checkifavailable(purpose))
                        {
                            if(purpose.equalsIgnoreCase(START_TEST))

                                //    seekPermissions();
                                openMainCallView();

                        }
                        else {
                            dialog.cancel();
                        }
                    }
                });
        alert.show();
    }

    public void showCallAlertDialog(String title,String message,String purpose) {
        System.out.println(" entering updateApp() ");
        android.app.AlertDialog.Builder updatealert = new android.app.AlertDialog.Builder(context);
            updatealert.setTitle(title);
            updatealert.setMessage(message);
            updatealert.setPositiveButton(Html.fromHtml("<font color='#FF0000'>OK</font>"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    if(Utils.checkifavailable(purpose))
                    {
                        if(purpose.equalsIgnoreCase(START_TEST))

                            //    seekPermissions();
                            openMainCallView();

                    }
                    else {
                        dialog.cancel();
                    }
                }
            });
        updatealert.show();
//            updatealert.setNegativeButton(Html.fromHtml("<font color='#FF0000'>LATER</font>"), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
          //  }).show();



    }

    public void showCallAlertDialogNew(String title,String message,String purpose) {
        System.out.println(" entering updateApp() ");
        android.app.AlertDialog.Builder updatealert = new android.app.AlertDialog.Builder(context);
        updatealert.setTitle(title);
        updatealert.setMessage(message);
        updatealert.setPositiveButton(Html.fromHtml("<font color='#FF0000'>OK</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                new CallOutput("Ping").showCallAlert(context);
            }
        });
        updatealert.show();
//            updatealert.setNegativeButton(Html.fromHtml("<font color='#FF0000'>LATER</font>"), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
        //  }).show();



    }

    private void openMainCallView() {
        call_start_rl.setVisibility(View.GONE);
        call_main_ll.setVisibility(View.VISIBLE);


//Utils.showToast(getActivity(),"Kindly enter the number and then press the call button.");
    }

    private void seekPermissions() {
        if (getActivity() != null) {
            if (checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, CALL_PERMISSION);
            }
            else
            {

                    readContactList();

            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == CALL_PERMISSION) {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    readContactList();
                }
            }

            else if(requestCode == RECORD_PERMISSION) {
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startCall();
                    }

                }
            else if(requestCode == PHONECALL_PERMISSION_CODE) {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Storage Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(),"Storage Permission Denied", Toast.LENGTH_SHORT).show();
                }
                }




        }

    private void init() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_new);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        addressbook_img = (ImageView)view.findViewById(R.id.addressbook_img);
        mobile_txt = (TextView)view.findViewById(R.id.mobile_txt);
        smstext = (TextView)view.findViewById(R.id.smstext);
        selectedfile = (TextView)view.findViewById(R.id.selectedfile);

        call_btn = (Button)view.findViewById(R.id.call_btn);
        sms_btn = (Button)view.findViewById(R.id.sms_btn);
        mms_btn = (Button)view.findViewById(R.id.mms_btn);
        select_btn = (Button)view.findViewById(R.id.select_btn);
        if(listenService.gps!=null) {
            lat = listenService.gps.getLatitude() + "";
            lon = listenService.gps.getLongitude() + "";
        }
        else {
            lat="0.0";
            lon="0.0";
        }
        call_start_rl=view.findViewById(R.id.call_start_rl);
        call_main_ll=view.findViewById(R.id.call_main_ll);
        call_start_btn=(Button)view.findViewById(R.id.start);
        cancel_btn=(Button)view.findViewById(R.id.cancel_btn);
       // showOutput_btn=view.findViewById(R.id.showOutput_btn);
        //showOutput_btn.setOnClickListener(this);
        call_start_btn.setOnClickListener(this);
        call_btn.setOnClickListener(this);
        select_btn.setOnClickListener(this);
        sms_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
       super.onActivityResult(resultCode, resultCode, data);
      //  Toast.makeText(getActivity(), "onactivity result called", Toast.LENGTH_SHORT).show();
        System.out.println("onactivity result called");
       if (resultCode == getActivity().RESULT_OK) {
            if(reqCode==4){
                if(data.getData()!=null){
                    Uri contactData = data.getData();
                    String[] numberArray=parseNumber(contactData,getActivity());

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
                    path = getPathOfThisUri(getActivity(), uri);
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
            getActivity().onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.start:
                // by swapnil bansal 01/02/2023
               // showAlertDialog("Call Test",Constants.start_call_test,START_TEST);
                showCallAlertDialog("Call Test",Constants.start_call_test,START_TEST);
                break;
            case R.id.call_btn:
                initiateCallProcess();
                break;
            case R.id.select_btn:
                getData();
                break;
            case R.id.sms_btn:
                initiateSmsProcess();
                break;
            case R.id.cancel_btn:
                takeToMainPage();
                break;
                // by swapnil bansal 01/02/2023
          //  case R.id.showOutput_btn:
               // toggleAirplaneMode(1,false);
               // call.disconnect();


//                System.out.println(" entering call test");
//                String mob = mobile_txt.getText().toString();
//                if (!Utils.checkifavailable(mob)) {
//                    showCallAlertDialog("Message", "Please enter phone number and then make a call ", "");
//                } else {
//                    new CallOutput("Ping").showCallAlert(context);
//                }
//                break;
        }
    }


    public void toggleAirplaneMode(int value, boolean state) {
        Settings.Global.putInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, value);
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", !state);
        context.sendBroadcast(intent);
    }

    private void takeToMainPage() {
        if(Utils.checkContext(getActivity()))
        getActivity().onBackPressed();
    }

    private void getData() {
        String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath();;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Uri myUri = Uri.parse(folderPath);
        intent.setDataAndType(myUri, "*/*");
        startActivityForResult(intent, FILE_SELECT);
    }
}
