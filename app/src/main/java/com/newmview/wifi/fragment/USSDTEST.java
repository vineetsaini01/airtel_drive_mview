package com.newmview.wifi.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.newmview.wifi.PhoneCallHelper;
import com.mview.airtel.R;
import com.newmview.wifi.application.MviewApplication;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import static android.content.Context.TELEPHONY_SERVICE;


public class USSDTEST extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "aa";
    private Button button;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;

    public USSDTEST() {

    }


    public static USSDTEST newInstance(String param1, String param2) {
        USSDTEST fragment = new USSDTEST();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_u_s_s_d_t_e_s_t, container, false);
        button = (Button) view.findViewById(R.id.fragment1btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CALL_PHONE},
                            22);
                }
                else
                {
                    openCustomUSSDDialog();
                }

            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 22) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCustomUSSDDialog();
            } else {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void openCustomUSSDDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_code, null);
        EditText codeBtn = view.findViewById(R.id.codeBtn);
        Button runBtn = view.findViewById(R.id.runBtn);
        Button cancelBtn = view.findViewById(R.id.cancelBtn);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        alertBuilder.setView(view);
        final AlertDialog dialog = alertBuilder.show();
        cancelBtn.setOnClickListener(view1 -> dialog.dismiss());
        runBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String USSDCode = codeBtn.getText().toString();
                System.out.println(" entering it one "+USSDCode);

                if(!USSDCode.startsWith("*")  || !USSDCode.endsWith("#"))
                {
                    System.out.println(" entering it ");
                    Toast.makeText(MviewApplication.ctx,"Enter valid USSD code",Toast.LENGTH_LONG).show();
                    return;
                }
                runUSSDCode(USSDCode);
                dialog.dismiss();
            }
        });

    }

    @SuppressLint({"NewApi", "MissingPermission"})
    private void runUSSDCode(String USSDCode) {

        try {
            String code=USSDCode.trim();
            USSDCode = USSDCode.substring(0, USSDCode.length() - 1) + Uri.encode("#");
            System.out.println(" ussd code is"+USSDCode);
            Intent i = new Intent(Intent.ACTION_CALL);
            i.setData(Uri.parse("tel:" + USSDCode));
            startActivity(i);

            TelephonyManager manager = (TelephonyManager) getContext().getSystemService(TELEPHONY_SERVICE);
            String networkOperator = manager.getNetworkOperatorName();
            System.out.println("Network Operator is: " + networkOperator);
            if (manager == null) {
                Toast.makeText(getContext(), "Telephony Manager not available", Toast.LENGTH_SHORT).show();
                return;
            }

//            View view = LayoutInflater.from(getContext()).inflate(R.layout.status_dialog,null);
//            CircularProgressIndicator progressIndicator =view.findViewById(R.id.progressIndicator);
//            TextView message = view.findViewById(R.id.message);
//            TextView ok = view.findViewById(R.id.okbutton);
//            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
//            alertBuilder.setView(view);
//            alertBuilder.setCancelable(false);
//            final AlertDialog dialog = alertBuilder.show();
//            ok.setVisibility(View.GONE);
//            ok.setOnClickListener(view1 ->dialog.dismiss());
            final boolean[] hasResponseBeenHandled = {false};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                Handler handler = new Handler(Looper.getMainLooper());

                manager.sendUssdRequest(code, new TelephonyManager.UssdResponseCallback() {

                    @Override
                    public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
                        super.onReceiveUssdResponse(telephonyManager, request, response);
                        System.out.println("Success Response ussd is " + response.toString());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Process the response after a delay
                                Log.d("USSD", "Delayed processing of USSD response");
                                // Add your handling code here
                            }
                        }, 8000);

                    }

                    @Override
                    public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                        super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
                        System.out.println("Response USSD failed with code: " + failureCode);
                        switch (failureCode) {
                            case TelephonyManager.USSD_RETURN_FAILURE:
                                System.out.println("USSD_RETURN_FAILURE");
                                break;
                            case TelephonyManager.USSD_ERROR_SERVICE_UNAVAIL:
                                System.out.println("USSD_ERROR_SERVICE_UNAVAIL");
                                break;
                            default:
                                System.out.println("Unknown error code: " + failureCode);
                        }

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Handle failure after a delay
                                Log.d("USSD", "Delayed handling of USSD failure");
                                // Add your handling code here
                            }
                        }, 10000);

                    }


                }, new Handler());
            }else {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + USSDCode));
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.CALL_PHONE}, 1);
                    return;
                }
                getContext().startActivity(intent);
            }
        }catch (Exception e)
        {
            System.out.println(" Exception in ussd test is"+e.getMessage());
            e.printStackTrace();

        }
    }


}
