package com.newmview.wifi.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dashboard.roomdb.Contact;
import com.dashboard.roomdb.ContactDao;
import com.dashboard.roomdb.DashboardDatabase;
import com.functionapps.mview_sdk2.main.Mview;
import com.google.android.exoplayer2.util.Util;
import com.newmview.wifi.application.MviewApplication;
import com.receiver.SMSReceiverBroadcast;
import com.mview.airtel.R;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.helper.RequestResponse;
import com.newmview.wifi.listenService;
import com.newmview.wifi.other.Config;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NumberValidationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 2;
    private static final String NUMBER_VAL_TITLE = "Mobile Number Verification";
    Button validationBtn;
    EditText numberet,clusteret,username;
    RadioGroup radioGroup;
    int selectedId;
    RadioButton selectedRadioButton;
    String selectedValue = null;
    private ProgressDialog progressDialog;
    private BroadcastReceiver smsreceiversent;
    int MY_PERMISSIONS_REQUEST = 1;
    private SMSReceiverBroadcast smsReceiverBroadcast;
    private BroadcastReceiver deliveredsmsreceiver;
    private String mob;
    private int otp;
    private SharedPreferences sp;
    private SmsManager smsManager;
    private PendingIntent sentIntent, deliveredPI;
    private String msg;
    private ContactDao contactDao;
    String formatted_senderMob;
    String name;
    String userName;
    private boolean msgrxdflag = false;
    private Cursor c;
    private long delayInMillis = 15000;
    private boolean TIME_OUT = false;
    private ArrayList<Object> listPermissionsNeeded;
    private int times = 0;
    private boolean showotpalert = true;
    private ArrayList<Integer> simCardList = new ArrayList<>();
    private ArrayList<Object> sentsubid;
    private int smsSubscriptionId;
    private SubscriptionManager subscriptionManager;
    private Button resend;
    private LinearLayout number_ll, otp_ll;
    private EditText otp_et;
    private TextView otp_tv;
    private Button otp_sub;
    private Button retry_button;
    private SmsMessage sms;

    public void onStart() {
        super.onStart();
        smsReceiverBroadcast = new SMSReceiverBroadcast() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                String message = null;
                if (intent.getExtras() != null) {
                    msgrxdflag = true;
                    String senderno = intent.getExtras().getString("phno").trim();
                    message = intent.getExtras().getString("message");
                    String otp = intent.getExtras().getString("otp");
                    System.out.println("sender num " + senderno + "message is " + message);
                    if (Utils.checkifavailable(senderno)) {
                        String finalnum = null;
                        String formattedPhone = null;
                        formattedPhone = Utils.formatNumber(senderno);
                        String numwithoutplus = senderno.replace("+", "");
                        String finalnumwithoutplus = numwithoutplus.replace(" ", "");
                        if (formattedPhone != null) {
                            String parts[] = formattedPhone.split(" ", 2);
                            finalnum = parts[1].replace(" ", "");
                            if (finalnum != null) {
                                System.out.println("formatted sender num " + formatted_senderMob);
                                if (formatted_senderMob != null) {
                                    if (formatted_senderMob.equals(senderno)) {
                                        showotpalert = false;
                                        System.out.println("sender num " + senderno);
                                        //   Toast.makeText(context, "calling from 1", Toast.LENGTH_SHORT).show();
                                        getOtpDialog(formatted_senderMob, message, otp); //+917986795188
                                    } else if (formatted_senderMob.equals(finalnum)) {
                                        showotpalert = false;
                                        System.out.println("final num " + finalnum);
                                        //   Toast.makeText(context, "calling from 2", Toast.LENGTH_SHORT).show();
                                        getOtpDialog(formatted_senderMob, message, otp);//7986795188
                                    } else if (formatted_senderMob.equals(finalnumwithoutplus)) {
                                        showotpalert = false;
                                        System.out.println("final num without plus " + finalnumwithoutplus);
                                        //Toast.makeText(context, "calling from 3", Toast.LENGTH_SHORT).show();
                                        getOtpDialog(formatted_senderMob, message, otp);//917986795188
                                    } else {
                                        showotpalert = true;
                                    }
                                }
                                //unregisterSMSReceiver(smsReceiverBroadcast);
                            }
                        }
                    } else {
                        showotpalert = true;
                    } }
            }
        };
    }

    private void getOtpDialog(final String number, final String message, String otp) {
        //  dismissProgress();
        unregisterSMSReceiver(smsReceiverBroadcast);

        Constants.USER_NUM = number;

        if (Utils.checkifavailable(otp)) {
            otp_et.setText(otp);
        }
       /* final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.otp_dialog);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
        final EditText otpet=(EditText)dialog.findViewById(R.id.otpet);
        TextView otptitle = (TextView) dialog.findViewById(R.id.otptitle);

        otptitle.setText((Html.fromHtml(Constants.otp + number)));
        Button submit=dialog.findViewById(R.id.submit);
        Button resend=dialog.findViewById(R.id.resend);
        String usertext=otpet.getText().toString();
        if(otp!=null) {
            otpet.setText(otp);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    deleteSMS(NumberValidationActivity.this, message, number);
                }
            },7200);

        }
        else
        {
            Utils.showToast(NumberValidationActivity.this,"Please enter the OTP!!");
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(otpet.getText().toString().equals(String.valueOf(NumberValidationActivity.this.otp)))
                {
                    dialog.dismiss();

                    goToMainActivity();
                    sp.edit().putBoolean("validated",true).apply();


                }
                else
                {
                    Utils.showToast(NumberValidationActivity.this,Constants.INCORRECT_OTP);
                }
            }
        });


        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMSToDevice();
            }
        });*/


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getSupportActionBar().hide();
        sentsubid = new ArrayList<>();
        simCardList = new ArrayList<Integer>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED  &&
                        ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)

        {



            requestLocationPermissions(MY_PERMISSIONS_REQUEST);
        }
        else
        {


            if(Utils.isMyServiceRunning(listenService.class))
            {
            stopAndRestartService();
            }
            else {
            startListenService();
            }


        }


        sp = getSharedPreferences(Constants.REGISTERATION, MODE_PRIVATE);
        if (sp.getBoolean("validated", false)) {
                openCustomUSSDDialog();
        } else {

            setContentView(R.layout.enter_number);
            init();
            /*deleteSMS(NumberValidationActivity.this, "", "");*/

        }



    }

//    private static void askpermissionsBackground(Activity activity){
//        int permissiongps = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
//                permissiongps != PackageManager.PERMISSION_GRANTED) {
//            String[] permissions = new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION};
//            ActivityCompat.requestPermissions(activity,
//                    permissions, 10);
//
//        }
//    }
    @SuppressLint("MissingInflatedId")
    private void openCustomUSSDDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.number_validation, null);
        EditText number = view.findViewById(R.id.number_et);
        Button nextBtn = view.findViewById(R.id.nextBtn);
        android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(this);
        alertBuilder.setView(view);
        final android.app.AlertDialog dialog = alertBuilder.show();
        dialog.setCancelable(false);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newNumber = number.getText().toString();
                if (!newNumber.isEmpty()){
                    if (newNumber == Utils.getMyContactNum(MviewApplication.ctx)){
                        goToMainActivity();
                    }else {
                        DB_handler db_handler = new DB_handler(MviewApplication.ctx);
                        db_handler.open();
                        db_handler.updateUserNumber(newNumber);
                        db_handler.close();
                        goToMainActivity();

                    }
                }else {
                    Utils.showToast(NumberValidationActivity.this, "Please enter your number!");

                }


                dialog.dismiss();

            }
        });

    }
    private void requestLocationPermissions(int issue) {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE,Manifest.permission.SEND_SMS

        },issue);
    }

    private void startListenService() {

        Intent serviceIntent = new Intent(this, listenService.class);
        this.startService(serviceIntent);
       /* batteryList.add(Float.valueOf(Utils.getBattery(context)));
        timeStamp.add(Utils.getCurrentHourMin());*/
    }

    private void stopAndRestartService() {
        Intent serviceIntent = new Intent(this, listenService.class);
        this.stopService(serviceIntent);
        Intent serviceIntent1 = new Intent(this, listenService.class);
        this.startService(serviceIntent1);
    }

    private void init() {
        validationBtn = findViewById(R.id.validation_btn);
        numberet = findViewById(R.id.mobile_txt);
        username = findViewById(R.id.user_name);
        clusteret = findViewById(R.id.circle_et);
        validationBtn.setOnClickListener(this);
        resend = findViewById(R.id.resend_btn);
        resend.setOnClickListener(this);
        number_ll = findViewById(R.id.number_val_layout);
        otp_ll = findViewById(R.id.otp_layout);
        otp_tv = findViewById(R.id.otp_tv);
        otp_et = findViewById(R.id.otp_et);
        otp_sub = findViewById(R.id.send_btn);
        retry_button = findViewById(R.id.retry_btn);
        retry_button.setOnClickListener(this);
        otp_sub.setOnClickListener(this);
//        DashboardDatabase db = DashboardDatabase.getDatabase(this);
//        contactDao = db.contactDao();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.validation_btn) {
            radioGroup = findViewById(R.id.rg_usertype);
            selectedId = radioGroup.getCheckedRadioButtonId();

            selectedRadioButton = findViewById(selectedId);

            // Initialize selectedValue to store the value of the selected RadioButton


            // Check if any RadioButton is selected
            if (selectedRadioButton != null) {
                selectedValue = selectedRadioButton.getText().toString();
                Log.d("TAG", "onClick: radio value is  "+selectedValue);
            }
            //  if (Utils.isNetworkAvailable(this)) {
            userName = username.getText().toString().trim();

            String num = numberet.getText().toString();
            name = clusteret.getText().toString().trim();

            if (!name.isEmpty() && !num.isEmpty() && !userName.isEmpty()) {
                if (num.length() >= 10) {
                    msgrxdflag = false;
//                    Contact contact = new Contact(name, num);
//                    insertContact(contact);
                    showValidationAlertBeforeSendingOtp();
                } else {
                    Utils.showToast(NumberValidationActivity.this, "Please enter correct number!");
                }
            } else {
                if (name.isEmpty() && num.isEmpty() && userName.isEmpty()){
                    Utils.showToast(NumberValidationActivity.this, "Please enter your cluster name, number and username!");

                }
                else if (name.isEmpty()) {
                    Utils.showToast(NumberValidationActivity.this, "Please enter your cluster name!");
                } else if (num.isEmpty()) {
                    Utils.showToast(NumberValidationActivity.this, "Please enter your number!");
                }else if (userName.isEmpty()) {
                    Utils.showToast(NumberValidationActivity.this, "Please enter your username!");
                }
                else if (name.isEmpty() && num.isEmpty()){
                    Utils.showToast(NumberValidationActivity.this, "Please enter your cluster name and number!");

                }
            }


            //} /*else {
            //Utils.showToast(this, Constants.NO_INTERNET);
            // }
        }
        else if (v.getId() == R.id.resend_btn) {
            String num = String.valueOf(numberet.getText());
            if (Utils.checkifavailable(num)) {
                if (otp != 0) {
                    sendSms(otp, num);
                }
            } else {
                showNumberAlert();
            }
        }
        else if (v.getId() == R.id.send_btn) {
            //if (otp_et.getText().toString().equals(String.valueOf(NumberValidationActivity.this.otp)))
            if (true) {
                DB_handler db = new DB_handler(NumberValidationActivity.this);
                db.open();
                Log.d("TAG", "onClick: inserting number and circle name into table "+name+formatted_senderMob);
                db.insertUserMsisdn(formatted_senderMob,name, userName,selectedValue);
                db.close();
//                if(Config.isNetworkAvailable(this)) {
//                    RequestResponse.updateuserprofile(this, formatted_senderMob);
//                }
                goToMainActivity();
                sp.edit().putBoolean("validated", true).apply();



            } else {
                Utils.showToast(NumberValidationActivity.this, Constants.INCORRECT_OTP);
            }
        }
        else if (v.getId() == R.id.retry_btn) {
            sendSMSToDevice();
        }

    }

//    private void insertContact(Contact contact) {
//        // Execute database operation in a background thread
//        DashboardDatabase.databaseWriteExecutor.execute(() -> {
//            contactDao.insert(contact);
//            Log.d(Mview.TAG, "insertContact: data inserted");
//        });
//    }

    private void showValidationAlertBeforeSendingOtp() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        alert.setTitle(NUMBER_VAL_TITLE);
        alert.setMessage(Constants.VALIDATION_ALERT);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendSMSToDevice();

            }
        });
        alert.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {



                    if(Utils.isMyServiceRunning(listenService.class))
                    {
                       stopAndRestartService();
                    }
                    else {
                        startListenService();
                    }


                    Constants.IMSI=Utils.getImsi(this);
                    System.out.println("imsi from main class 1"+Constants.IMSI);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;

           /* case MY_PERMISSIONS_REQUEST_SEND_SMS:
                System.out.println("calling from 2");
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendMsgViaSMSMANAGER();
                }
                else if(!shouldShowRequestPermissionRationale(permissions[0]))
                {
                    dismissProgress();
                   showAlert("This app requires the permissions to move further.Press Ok button to turn on the permissions from settings.");


                } else
                {
                    dismissProgress();
                }
                break;*/



            case Constants.PERMISSIONS_REQUEST:

                if (grantResults.length > 0) {

                    if (grantResults.length == 3) {
                        System.out.println("grant_results are " + Arrays.toString(grantResults) + "and size is " + grantResults.length);

                        if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                            System.out.println("requests approved " + requestCode);
                            sendMsgViaSMSMANAGER();
                        } else if (!shouldShowRequestPermissionRationale(permissions[0]) || !shouldShowRequestPermissionRationale(permissions[1])|| !shouldShowRequestPermissionRationale(permissions[2])) {
                            dismissProgress();
                            //   getRunTimePermissions();
                          //  showAlert("This app requires permissions to move further.Press Ok button to turn on the permissions from settings,so please enable all the permissions!!");

                        } else {
                            dismissProgress();
                            getRunTimePermissions();
                        }


                    } else if (grantResults.length == 1) {
                        System.out.println("grant_results are " + Arrays.toString(grantResults) + "and size is " + grantResults.length);
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            System.out.println("request approved " + requestCode);
                            sendMsgViaSMSMANAGER();
                        } else if (!shouldShowRequestPermissionRationale(permissions[0])) {
                            dismissProgress();
                           // showAlert("This app requires the permissions to move further.Press Ok button to turn on the permissions from settings.");

                        } else {
                            dismissProgress();
                            getRunTimePermissions();
                        }

                    }
                }
                break;


        }


    }


    private void showAlert(String msg) {
        times++;
        System.out.println("called " + times++);
        final Dialog dialog = new Dialog(NumberValidationActivity.this, R.style.AlertDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.alertdialog, null);
        TextView textView = view.findViewById(R.id.msg);
        textView.setText(msg);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        Button okbutton = view.findViewById(R.id.ok);
        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                takeUserToSettings();

            }
        });


    }

    private void takeUserToSettings() {
        Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(i);


    }

    private void getRunTimePermissions() {
        System.out.println("getting permissions");
        int permissionReadPHoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int permissionSendSms = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int permissionSecureSettings = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SECURE_SETTINGS);
        listPermissionsNeeded = new ArrayList<>();
        if (permissionReadPHoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (permissionSendSms != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (permissionSecureSettings != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_SECURE_SETTINGS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            System.out.println("getting permissions non empty");
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), Constants.PERMISSIONS_REQUEST);
            //  Utils.showToast(NumberValidationActivity.this,"not empty  ");
        } else {
            System.out.println("getting permissions empty");
            //    Utils.showToast(NumberValidationActivity.this,"empty  ");


        }


    }

    private void sendSms(int otp, String num) {
        //     Utils.appendLog(Utils.getDateTime() +" : Reached sms function...");
        showProgress();
        showOTPMainScreen();


        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        // String message = smstext.getText().toString();
        final String mob = numberet.getText().toString();
        msg = otp + " is your OTP for access to your Airtel drive application. Do not share the OTP with anyone.";
        Intent dl = new Intent(DELIVERED);
        dl.putExtra("num", num);
        dl.putExtra("msg", msg);
        otp_tv.setText((Html.fromHtml(Constants.otp + num)));
        int pendingFlags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        sentIntent = PendingIntent.getBroadcast(this, 0, new Intent(SENT),pendingFlags);
        deliveredPI = PendingIntent.getBroadcast(this, 0, dl, pendingFlags);
       // sentIntent = PendingIntent.getBroadcast(this, 0, new Intent(SENT), );
       // deliveredPI = PendingIntent.getBroadcast(this, 0, dl, 0);

       /* Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                progressDialog.dismiss();
                unregisterSMSSentReceiver();
                TIME_OUT=true;
             //   Utils.showToast(NumberValidationActivity.this,"Unable to send sms.So kindly try again!!");
            }
        }, delayInMillis);*/


        smsreceiversent = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {

                System.out.println("result code is " + getResultCode());
                int resultCode = getResultCode();
                //  Utils.appendLog(Utils.getDateTime() +": Result code for sending is...  "+resultCode);
                switch (resultCode) {

                    case Activity.RESULT_OK:
                        dismissProgress();
                        unregisterSMSSentReceiver();

                        //Utils.showToast(NumberValidationActivity.this, "SMS sent");
                        break;

                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Utils.showToast(NumberValidationActivity.this, "Please check message for otp!!");
                        dismissProgress();
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                if (simCardList != null && simCardList.size() > 0) {
                                    if (simCardList.size() != sentsubid.size()) {
                                        simCardList.remove(smsSubscriptionId);
                                        int size = simCardList.size();
                                        size--;
                                        smsSubscriptionId = simCardList.get(size);
                                        SmsManager sms = null;
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                                            sms = SmsManager.getSmsManagerForSubscriptionId(smsSubscriptionId);
                                        } else {
                                            sms = SmsManager.getDefault();
                                        }

                                        sms.sendTextMessage(mob, null, msg, sentIntent, deliveredPI);
                                    }
                                } else {
                                    dismissProgress();

                                    showAlertDialog("Otp", Constants.GENERIC_FAILURE);
                                    unregisterSMSSentReceiver();
                                }
                            }
                        }


                        break;

                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        dismissProgress();
                        showAlertDialog("Otp", Constants.NO_SERVICE);
                        unregisterSMSSentReceiver();
                        break;


                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        dismissProgress();
                        showAlertDialog("Otp", Constants.ENABLE_NTWRK);
                        unregisterSMSSentReceiver();
                        break;

                    default:
                        dismissProgress();
                        showAlertDialog("Otp", "Error occured is... " + resultCode);
                        unregisterSMSSentReceiver();
                        break;


                }

            }
        };

        deliveredsmsreceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                int resultCode = getResultCode();
                //getDeliveryStatus(arg1);
                System.out.println("result code for delivering   " + resultCode);
                //Utils.appendLog(Utils.getDateTime() +": Result code for delivering is  "+resultCode);
                switch (resultCode) {
                    case Activity.RESULT_OK:

                        if (progressDialog != null) {
                            /*
                            Check for 5 seconds whether we have read the OTP or not
                             */
                            if (progressDialog.isShowing()) {
                                for (int i = 0; i < 6; i++) {

                                    if (!showotpalert) {
                                        progressDialog.dismiss();
                                        break;
                                    } else {
                                        progressDialog.dismiss();
                                        //   Toast.makeText(NumberValidationActivity.this, "calling from 4", Toast.LENGTH_SHORT).show();
                                        getOtpDialog(formatted_senderMob, msg, null);
                                        break;
                                    }
                                }
                            }
                        }

                        break;
//

                    case Activity.RESULT_CANCELED:
                        unregisterSMSReceiver(deliveredsmsreceiver);

                        break;


                   /* default:
                        unregisterSMSReceiver(deliveredsmsreceiver);
                        break;*/

                }
            }
        };


        registerReceiver(smsreceiversent, new IntentFilter(SENT));
        registerReceiver(deliveredsmsreceiver, new IntentFilter(DELIVERED));
        sendMsgViaSMSMANAGER();
        registerSMSReceiver();





       /* if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_SMS,Manifest.permission.READ_PHONE_STATE},MY_PERMISSIONS_REQUEST_SEND_SMS);

        }
        else
        {
            System.out.println("calling from 1");
            sendMsgViaSMSMANAGER();

        }
*/


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getDeliveryStatus(Intent intent) {
        byte[] pdu = intent.getByteArrayExtra("pdu");
        String format = intent.getStringExtra("format");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && format != null) {
            sms = SmsMessage.createFromPdu(pdu, format);
        } else {
            sms = SmsMessage.createFromPdu(pdu);
        }

        int statusCode = sms.getStatus();
        String status = translateDeliveryStatus(statusCode);
        Utils.showToast(this, "status is " + status);

    }

    String translateDeliveryStatus(int statusCode) {
        switch (statusCode) {
            case Telephony.Sms.STATUS_COMPLETE:
                return "Sms.STATUS_COMPLETE";
            case Telephony.Sms.STATUS_FAILED:
                return "Sms.STATUS_FAILED";
            case Telephony.Sms.STATUS_PENDING:
                return "Sms.STATUS_PENDING";
            case Telephony.Sms.STATUS_NONE:
                return "Sms.STATUS_NONE";
            default:
                return "Unknown status code";


        }
    }

    private void showOTPMainScreen() {
        //  Utils.appendLog(Utils.getDateTime() +" : Reached otp main screen function...");
        number_ll.setVisibility(View.GONE);
        otp_ll.setVisibility(View.VISIBLE);
    }


    private void registerSMSReceiver() {

        IntentFilter intentFilter = new IntentFilter("SMS_INTENT");

        intentFilter.setPriority(1000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(smsReceiverBroadcast, intentFilter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(smsReceiverBroadcast, intentFilter);
        }
    }

    private void unregisterSMSReceiver(BroadcastReceiver receiver) {
        if (receiver != null)
            try {
                unregisterReceiver(receiver);
            } catch (Exception e) {
                System.out.println("exception is " + e.toString());
            }
    }


    private void sendMsgViaSMSMANAGER() {
        if (sentsubid == null) {
            sentsubid = new ArrayList<>();
        }
        if (simCardList == null) {
            simCardList = new ArrayList<Integer>();
        }

        //       Utils.appendLog(Utils.getDateTime() +" : Sending sms via sms manager...");

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            System.out.println("getting permissions in sms block");
            subscriptionManager = SubscriptionManager.from(NumberValidationActivity.this);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            final List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
            if (subscriptionInfoList != null && subscriptionInfoList.size() > 0) {
                for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
                    System.out.println("getting permissions in sms block for loop");


                    int subscriptionId = subscriptionInfo.getSubscriptionId();

                    simCardList.add(subscriptionId);
                }

                smsSubscriptionId = subscriptionManager.getDefaultSmsSubscriptionId();
                sentsubid.add(smsSubscriptionId);
                smsManager = SmsManager.getSmsManagerForSubscriptionId(smsSubscriptionId);

            }

        } else {
            smsManager = SmsManager.getDefault();
        }
        System.out.println("getting sms sending" + smsManager);

        if (smsManager != null) {

            smsManager.sendTextMessage(mob, null, msg, sentIntent, deliveredPI);
        }

    }

    private void unregisterSMSSentReceiver() {
        try {
            if (smsreceiversent != null)
                unregisterReceiver(smsreceiversent);
        } catch (Exception e) {

        }
    }

    private void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    private void sendSMSToDevice() {
        otp = Utils.generateRandomNumber();


        mob = numberet.getText().toString();
        formatted_senderMob = mob;
       /*String formatted_senderMobilenum=Utils.formatNumber(mob);
       String parts[]=formatted_senderMobilenum.split(" ",2);
      formatted_senderMob=parts[1].replace(" ","");*/
        System.out.println("formatted_sender " + formatted_senderMob);

        if (mob == null || mob.equals("")) {
            showNumberAlert();

        } else {

            try {
                getRunTimePermissions();


                sendSms(otp, String.valueOf(mob));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("exception in sending sms " + e.toString());
            }


        }


    }

    private void showNumberAlert() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(
                this);
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
    }

    private void showProgress() {
//        Utils.appendLog(Utils.getDateTime() +" : Reached show progress function...");
        progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setMessage("Sending..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void showAlertDialog(String title, String message) {
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this, R.style.AlertDialogTheme);
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setCancelable(true);
        alert.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        dialog.cancel();
                    }
                });
        alert.show();
    }

    private void goToMainActivity() {

        Intent intent = new Intent(NumberValidationActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void deleteSMS(Context context, String message, String number) {
        try {

            System.out.println("Deleting SMS from inbox");
            Uri uriSms = Uri.parse("content://sms/inbox");
            c = context.getContentResolver().query(uriSms,
                    new String[]{"_id", "thread_id", "address",
                            "person", "date", "body"}, null, null, null);

            if (c != null && c.moveToFirst()) {
                do {
                    long id = c.getLong(0);
                    long threadId = c.getLong(1);
                    String address = c.getString(2);
                    String body = c.getString(5);
                    String date = c.getString(4);

                   /* Log.e("log>>>",
                            "0>" + c.getString(0) + "1>" + c.getString(1)
                                    + "2>" + c.getString(2) + "<-1>"
                                    + c.getString(3) + "4>" + c.getString(4)
                                    + "5>" + c.getString(5));
                    Log.e("log>>>", "date" + c.getString(0));*/


                    if (message.equals(body) && address.equals(number)) {
                        System.out.println("body is " + body + "from num is " + address);
                        int deletion = context.getContentResolver().delete(Uri.parse("content://sms/" + id), "date=?",
                                new String[]{c.getString(4)});
                        System.out.println("deleltion  " + deletion);
                        break;

                        //context.getContentResolver().delete(Uri.parse("content://sms/" + id), null, null);
                    } else {

                    }
                }
                while (c.moveToNext());

            }
        } catch (Exception e) {
            System.out.println("exception in deleting sms " + e.toString());
        } finally {
            //c.close();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterSMSReceiver(smsReceiverBroadcast);

    }

    @Override
    protected void onResume() {
        super.onResume();

        registerSMSReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterSMSReceiver(smsReceiverBroadcast);
    }
}
