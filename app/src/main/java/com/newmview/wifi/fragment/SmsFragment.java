package com.newmview.wifi.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.mview.airtel.R;
import com.newmview.wifi.activity.call_sms;
import com.newmview.wifi.other.Utils;

public class SmsFragment extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 1;

    private Button sms_btn;
    private TextView smstext, mobile_txt;
    ImageView addressbook_img;

    private View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.blank_fragment, container, false);
        sms_btn = v.findViewById(R.id.sms_btn);
        smstext = v.findViewById(R.id.smstext);
        mobile_txt = v.findViewById(R.id.mobile_txt);
        addressbook_img = (ImageView)v.findViewById(R.id.addressbook_img);


        addressbook_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED ) {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, 4);
                }else {
                    //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS,Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST);
                }
            }
        });

        sms_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    requestSmsPermission();
                } else {
                    initiateSmsProcess();
                }
            }
        });

        return v;
    }

    private void requestSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.SEND_SMS)) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Permission Needed")
                    .setMessage("This permission is needed to send SMS")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initiateSmsProcess();
            } else {
                Toast.makeText(getContext(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initiateSmsProcess() {
        String mob = mobile_txt.getText().toString();
        if (mob == null || mob.isEmpty()) {
            showAlertDialog("Message", "Please enter phone number");
        } else {
            String msg = smstext.getText().toString();
            if (msg == null || msg.isEmpty()) {
                showAlertDialog("Message", "Please enter message to send");
            } else {
                sendSms(mob, msg);
            }
        }
    }

    private void sendSms(String mob, String msg) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        Utils.appendLog("ELOG_SEND_SMS: mobile no is: "+mob+" msg is: "+msg);

        PendingIntent sentPI = PendingIntent.getBroadcast(getContext(), 0, new Intent(SENT), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(getContext(), 0, new Intent(DELIVERED), PendingIntent.FLAG_UPDATE_CURRENT);

        // Register for SMS sent event
        getContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Utils.appendLog( "ELOG_SEND_SMS: SMS Sent");
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Utils.appendLog( "ELOG_SEND_SMS: Generic failure");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Utils.appendLog( "ELOG_SEND_SMS: No service");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Utils.appendLog( "ELOG_SEND_SMS: Null PDU");
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Utils.appendLog( "ELOG_SEND_SMS: Radio off");
                        break;
                }
            }
        }, new IntentFilter(SENT));

        // Register for SMS delivered event
        getContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Utils.appendLog( "ELOG_SEND_SMS: SMS delivered");
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Utils.appendLog( "ELOG_SEND_SMS: Generic failure");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Utils.appendLog( "ELOG_SEND_SMS: No service");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Utils.appendLog( "ELOG_SEND_SMS: Null PDU");
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Utils.appendLog( "ELOG_SEND_SMS: Radio off");
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(mob, null, msg, sentPI, deliveredPI);
    }

    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }
}
