package com.newmview.wifi.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;



import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.net.NetworkCapabilities;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mview.airtel.R;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.bean.Pinger;
import com.mview.airtel.databinding.FragmentSpeeDTestNewBinding;
import com.newmview.wifi.helper.AllInOneAsyncTaskForNetwork;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.helper.RequestResponse;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;
import com.webservice.WebService;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

;import static android.content.Context.MODE_PRIVATE;
import static com.newmview.wifi.other.WifiConfig.getFreqBw;


public class SpeeDTestFragmentNew extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "newspeedtest";
    WifiInfo wifiInfo;
    private Bundle bundle;
    private String orderId;
    private String action_type;
    private String orgName;
    private static final int TIMEOUT = 15000;
    private static final int TIMEOUT2 = 500;
   // private MainSharedViewModel viewModel;
    private String viewKey, intentRequestKey;
    double freqBand;
    private View view;
    ProgressDialog progressDialog1 = null;
    private String mParam1;
    private String mParam2;
    private TextView ping_tv, pcktlossval;
    private FragmentSpeeDTestNewBinding binding;
    private float ulspeedNew, dlspeedNew;
    public  ProgressDialog progressDialog;
//    private Handler handler = new Handler();

//    private Runnable timeoutRunnable = new Runnable() {
//        @Override
//        public void run() {
//            if (progressDialog != null && progressDialog.isShowing()) {
//                progressDialog.dismiss();
//                Utils.showAlert(false, "Cannot connect to Server. Please try again.", getActivity(), null);
//            }
//        }
//    };
    public SpeeDTestFragmentNew() {

    }

    public static SpeeDTestFragmentNew newInstance(String param1, String param2) {
        SpeeDTestFragmentNew fragment = new SpeeDTestFragmentNew();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // to be done later
        //   viewModel=new ViewModelProvider(SpeeDTestFragmentNew.this).get(MainSharedViewModel.class);
        getExtras();



    }


    private void getExtras() {
        bundle = getArguments();
        if (bundle != null) {
            System.out.println(" bundle in speed is " + bundle);
            orderId = bundle.getString("orderId");
            action_type = bundle.getString("action_type");
            orgName = bundle.getString("orgName");
            viewKey = bundle.getString("viewKey");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_spee_d_test_new, container, false);
        binding.startspeedtest.setOnClickListener(this);
        //   viewModel.getData().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(String value) {
//                // Call the method of the Activity
//                ((MainActivity) requireActivity()).sendPerformanceTestResultToAWApp(value);
//            }
        // });
        return binding.getRoot();


    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startspeedtest:
                progressDialog = showCustomProgressDialog(getActivity(), "Please wait for few  seconds for getting  the result", false);
//                handler.postDelayed(timeoutRunnable, TIMEOUT); // Start timeout
                pingBeforeDownloadingOrUploading();
                break;
        }
    }

    public ProgressDialog showCustomProgressDialog(Context context, String message, boolean cancelable) {
        ProgressDialog pDialog = new ProgressDialog(context, R.style.roundedCornersDialog);
        //  pDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.new_icon_progress_dialog, null));
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(cancelable);
        pDialog.setMessage(message);
        pDialog.show();
        return pDialog;
    }
    private void pingBeforeDownloadingOrUploading() {

        Utils.ping1(Constants.newhost,
                new Interfaces.PingResult() {
                    @Override
                    public void onPingResultObtained(String response) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                handler.removeCallbacks(timeoutRunnable); // Remove timeout
                                if (response != null) {
                                    String seg[] = response.split("/");
                                    System.out.println(" seg is " + Arrays.toString(seg));
                                    if (seg[1].length() > 0) {
                                        Log.i(TAG,"Ping latency is" + seg[0]);


                                    }
                                    if (seg[1].length() > 0) {
                                        Log.i(TAG,"Packet loss  is" + seg[1]);


                                    }

                                    newSpeedTest256kbNewsignalStrengthlogicbuffer(seg[0], seg[1], new Interfaces.SpeedResult() {
                                        @Override
                                        public void onSpeedResultObtained(String[] response) {
                                            Utils.appendLog("ELOG_RUN_SPEED_PANEL: Speed test Result obtained: "+response);
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (response!= null) {
                                                        Log.i(TAG,"Values are " + Arrays.toString(response));
                                                        String dlSpeed = response[0];
                                                        String ulSpeed = response[1];
                                                        Log.i(TAG,"Dl speed is" +dlSpeed);
                                                        Log.i(TAG,"Ul speed is" +ulSpeed);

                                                        showCustomDialog(getContext(), dlSpeed, ulSpeed, seg[0], seg[1], "SPEED TEST RESULTS", "NEW SPEED TEST", "OK", false, new Interfaces.DialogButtonClickListener() {
                                                            @Override
                                                            public void onPositiveButtonClicked(String text) {

                                                            }

                                                            @Override
                                                            public void onNegativeButtonClicked(String text) {

                                                            }

                                                            @Override
                                                            public void onDialogDismissed(String text) {

                                                            }
                                                        });


                                                    }
                                                }
                                            });


                                        }


                                        @Override
                                        public void parseSpeedResult(Pinger response) {

                                        }
                                    });


                                }
                            }
                        });


                    }

                    @Override
                    public void parsePingResult(Pinger response) {

                    }
                }, null);
    }

    public String[] newSpeedTest256kbNewsignalStrengthlogicbuffer(String latency, String packetLoss, Interfaces.SpeedResult speedResult) {
        String[] arr = new String[4];
         int CONNECT_TIMEOUT_MS = 5000; // 5 seconds connection timeout
         int READ_TIMEOUT_MS = 5000;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    int serverPort = 4020;
                    InetAddress host = null;
                    float ultime = 0, dltime = 0, ulspeed = 0, dlspeed = 0;
                    long T1 = 0, T2 = 0, T3 = 0, T4 = 0, T3New = 0, T2New = 0;
                    Socket socket = null;
                    PrintWriter toServer = null;
                    BufferedReader fromServer = null;
                    InputStream is = null;
                    try {

                        Utils.appendLog("ELOG_RUN_SPEED_UI: Going to run Speed test from UI");
                        host = InetAddress.getByName("180.179.214.56");
                        if (host.isReachable(5000)) { // Timeout is set to 5000 milliseconds (5 seconds)
                            System.out.println("IP address " + "180.179.214.56" + " is reachable");
                        } else {
                            System.out.println("IP address " + "180.179.214.56" + " is not reachable");
                        }
                        Log.i(TAG, "Connecting to server on port " + serverPort);

                        StringBuilder sb = new StringBuilder();

                        @SuppressLint("WifiManagerLeak")
                        WifiManager wm = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
                        wifiInfo = wm.getConnectionInfo();
                        int freq = wifiInfo.getFrequency();
                        freqBand = getFreqBw(freq);
                        if (freqBand == 5.0) {
                            Log.i(TAG, "freqBand entering this " + freqBand);
                            is = getContext().getAssets().open("1mbNew.txt");
                        } else {
                            Log.i(TAG, "freqBand" + freqBand);
                            is = getContext().getAssets().open("256kb.txt");
                        }
                        // is = getAssets().open("256kb.txt");
                        int length = is.available();
                        Log.i(TAG, "length is" + length);
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));

                        String line1;
                        while ((line1 = br.readLine()) != null) {
                            sb.append(line1);
                        }
                        Log.i(TAG, " sb is" + sb.toString());
//                        is.close();


//                        socket = new Socket();
                        socket = new Socket(host, serverPort);
//                        socket.connect(new InetSocketAddress(host,serverPort),TIMEOUT);

                        toServer = new PrintWriter(socket.getOutputStream(), true);
                        T1 = System.currentTimeMillis();//taking time entry just when we are about to send the data to server
                        Log.i(TAG, "T1 TIME IS :" + T1);
                        toServer.println("Hello from Client"+ T1 + sb.toString() + socket.getLocalSocketAddress());//Sending data to server
                        toServer.flush();
                        socket.setSoTimeout(TIMEOUT);
                        Utils.appendLog("ELOG_RUN_SPEED_UI: Data sent to server");
                        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        String line = null;
                        line = fromServer.readLine();
                        Log.i(TAG, " Receiving from server :" + line);
                        // this will be t4 time just when have read the response from server
                        T4 = System.currentTimeMillis();
                        T3 = Long.parseLong(line.substring(37, 50));
                        Log.i(TAG, "T3 TIME IS :" + T3);

                        T3New = T3;
                        Log.i(TAG, "T3 TIME ADDED IS :" + T3New);
                        T2 = Long.parseLong(line.substring(51, 64));
                        Log.i(TAG, "T2 TIME IS :" + T2);
                        T2New = T2;
                        Log.i(TAG, "T2 ADDED TIME IS :" + T2New);

                        Log.i(TAG, " T4 TIME IS:" + T4);


//                        fromServer.close();
//                        toServer.close();
//                        socket.close();

                    } catch (Exception e) {
                        progressDialog.dismiss();
//                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        Utils.appendLog("Exception in reading data from server" + e.getMessage());
                        e.printStackTrace();
                        return;
                    }finally {
                        if (fromServer != null){
                            fromServer.close();
                        }
                        if (toServer != null){
                            toServer.close();

                        }
                        if (socket != null){
                            socket.close();

                        }
                        if (is != null){
                            is.close();
                        }
                    }


                    long diffmsT2minusT1 = T2New - T1;
                    long diffmsT4minusT3 = T4 - T3New;
                    Log.i(TAG, "T2 - T1 IS " + diffmsT2minusT1);
                    Log.i(TAG, "T4 - T3 IS " + diffmsT4minusT3);
                    float t4t1seconds = (T4 - T1);

                    @SuppressLint("WifiManagerLeak") WifiManager wm = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wm.getConnectionInfo();
                    int freq = wifiInfo.getFrequency();
                    double freqBand = getFreqBw(freq);
                    Log.i(TAG, " Freq is" + freqBand);

                    int signalStrength = wifiInfo.getRssi();
                    Log.i(TAG, " SignalStrength is" + signalStrength);


                    // for wifi 2.4 hertz
                    if (freqBand == 2.4 && signalStrength < -60) {
                        ultime = (float) (t4t1seconds * 0.55);
                        dltime = (float) (t4t1seconds * 0.45);
                    } else if (freqBand == 2.4 && signalStrength > -60) {
                        ultime = (float) (t4t1seconds * 0.52);
                        dltime = (float) (t4t1seconds * 0.48);
                    } else if (freqBand == 5.0) {
                        Log.i(TAG, "entering 5g band :");
                        ultime = (float) (t4t1seconds * 0.47);
                        dltime = (float) (t4t1seconds * 0.53);

                    }

                    // for mobile data only
                    else {
                        ultime = (float) (t4t1seconds * 0.65);
                        dltime = (float) (t4t1seconds * 0.35);

                    }
                    // for wifi 5  hertz
                    if (freqBand == 5.0) {
                        Log.i(TAG, "Entering 5g speed :");
                        ulspeed = (float) ((1 * 8 * 1000) / ultime);
                        dlspeed = (float) ((1 * 8 * 1000) / dltime);
                    } else {
                        ulspeed = (float) ((0.256 * 8 * 1000) / ultime);
                        dlspeed = (float) ((0.256 * 8 * 1000) / dltime);
                    }

                    ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = cm.getActiveNetworkInfo();
                    NetworkCapabilities nc = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
                    }

                    float downSpeed = nc.getLinkDownstreamBandwidthKbps() / 1000;
                    Log.i(TAG, " down speed is " + downSpeed + "mbps");
                    float upSpeed = nc.getLinkUpstreamBandwidthKbps() / 1000;
                    Log.i(TAG, "up speed is " + upSpeed + "mbps");
                    arr[0] = String.valueOf(dlspeed);
                    arr[1] = String.valueOf(ulspeed);
                    if (speedResult != null) {
                        speedResult.onSpeedResultObtained(arr);

                    }
                } catch (Exception e) {
//                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    Utils.appendLog("Error in last block"+e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        executorService.shutdown();
        return arr;


    }

    public void showCustomDialog(Context context, String downlaod, String uplaod, String latency, String packetLoss, String description, String title, String buttonText, boolean cancelable, Interfaces.DialogButtonClickListener dialogButtonClickListener) {
        System.out.println(" downlaod is" + downlaod);

        progressDialog.dismiss();
        if (!((Activity) context).isFinishing()) {
            final Dialog dialog = new Dialog(context);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(cancelable);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.custom_dialog_new_2);
            TextView text = (TextView) dialog.findViewById(R.id.title);
            text.setText(title);
            TextView text1 = (TextView) dialog.findViewById(R.id.description);
            text1.setText(description);
            TextView dtextView = (TextView) dialog.findViewById(R.id.dtxtview);
            //  dtextView.setText(downlaod);
            TextView utextView = (TextView) dialog.findViewById(R.id.utxtview);
            TextView latencytxtview = (TextView) dialog.findViewById(R.id.latencttxtviewNew);
            TextView packetlosstxtview = (TextView) dialog.findViewById(R.id.packetlosstxtviewNew);

            if (Utils.checkifavailable(downlaod)) {
                dtextView.setText(downlaod);
            } else {
                dtextView.setText("Not available");
            }
            if (Utils.checkifavailable(uplaod)) {
                utextView.setText(uplaod);
            } else {
                utextView.setText("Not available");
            }

            if (Utils.checkifavailable(latency)) {
                latencytxtview.setText(latency);
            } else {
                latencytxtview.setText("Not available");
            }

            if (Utils.checkifavailable(packetLoss)) {
                packetlosstxtview.setText(packetLoss);
            } else {
                packetlosstxtview.setText("Not available");
            }

            // utextView.setText(uplaod);
            Button dialogButton = (Button) dialog.findViewById(R.id.button);
            dialogButton.setText(buttonText);

            ;
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogButtonClickListener != null)
                        dialogButtonClickListener.onPositiveButtonClicked("");

                    dialog.dismiss();

                        System.out.println(" entering the app");
//                        sendRequest(downlaod, latency, packetLoss, uplaod);
                        //sendFinalResultToCallingApkNew(downlaod, latency, packetLoss, uplaod);

                }
            });

            dialog.show();
        }
    }
    private void sendRequest(String dmsg, String latency, String packetLoss, String umsg) {
        System.out.println(" entering the speed result");
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    //Your code goes here
                    if (WebService.isInternetAvailable()) {
                        JSONObject videoObj = new JSONObject();
                        videoObj.put("downlaod_speed", dmsg);
                        videoObj.put("uplaod_speed", umsg);
                        videoObj.put("latency", latency);
                        videoObj.put("packet_loss", packetLoss);

                        JSONArray speedArray=new JSONArray();
                        speedArray.put(videoObj);
                        RequestResponse.sendClientSocketSpeedEvent(speedArray, AllInOneAsyncTaskForNetwork.AsyncTaskPurpose.NEW_SPEED_CLIENT_SOCKET_TEST,"speed_test");
//                        WebService.API_sendVideoTest();
//                        new WebService.Async_SendNeighboringCellsInfo().execute();


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }



}