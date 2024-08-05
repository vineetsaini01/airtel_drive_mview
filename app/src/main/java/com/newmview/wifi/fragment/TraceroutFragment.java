package com.newmview.wifi.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.dashboard.roomdb.gagdagent.GagdAgent;
import com.evrencoskun.tableview.TableView;
import com.mview.airtel.R;
import com.newmview.wifi.activity.MainActivity;
import com.newmview.wifi.adapter.TraceRouteTableViewAdapter;

import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.bean.Cell;
import com.newmview.wifi.bean.ColumnHeader;

import com.newmview.wifi.bean.RowHeader;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.helper.MyTableViewListener;
import com.newmview.wifi.helper.RequestResponse;
import com.newmview.wifi.other.Config;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TraceroutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TraceroutFragment extends Fragment {
    private static final String TAG = "TraceRouteFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static TraceRouteTask traceroute=null;
    private TableView tableView;
    private View v;

    private ProgressDialog progressDialog;
    ArrayList<String> list_count = new ArrayList<String>();
    ArrayList<String> list_time = new ArrayList<String>();
    ArrayList<String> list_hops = new ArrayList<String>();
    String ipListForAgent;
    private static String date_time;

    // private final String feature;
    private TraceRouteTableViewAdapter adapter;

    private static final int TIMEOUT = 10000;

//    private Handler handler = new Handler();

//    private Runnable timeoutRunnable = new Runnable() {
//        @Override
//        public void run() {
//            if (progressDialog != null && progressDialog.isShowing()) {
//                progressDialog.dismiss();
//                Utils.showAlert(false, "Cannot connect to Server. Please try again.", getContext(), null);
//            }
//        }
//    };

    //    public  TraceroutFragment(String feature) {
//        this.feature=feature;
//
//    }
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public TraceroutFragment() {
        //  this.feature=feature;
        // Required empty public constructor
    }




    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TraceroutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TraceroutFragment newInstance(String param1, String param2) {
        TraceroutFragment fragment = new TraceroutFragment();
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("FragmentLiveDataObserve")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // by swapnil 11/25/2022
        v=inflater.inflate(R.layout.fragment_tracerout, container, false);
        // TableLayout pingTL=v.findViewById(R.id.pingTL);
        AutoCompleteTextView testEtnew=v.findViewById(R.id.testEt1);
        // by swapnil bansal 2/12/2022
        testEtnew.setThreshold(1);
//        viewModel=new ViewModelProvider(this,new MainViewModelFactory()).get(IPVM.class);
//        viewModel.getMapListObservable().observe(this, new Observer<List<IPlistModel>>() {
//            private List<IPlistModel> mapList;
//
//            @Override
//            public void onChanged(List<IPlistModel> mapList) {
//                Log.i(TAG,"Ip list is "+mapList);
//               // setListToTable(mapList);
//
//
//            }
//        });

        Button startBtn=v.findViewById(R.id.startBtn);

        //  ProgressBar progressBar=v.findViewById(R.id.progressBar);
        tableView = v.findViewById(R.id.content_container1);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                handler.postDelayed(timeoutRunnable, TIMEOUT); // Start timeout

                String ip=testEtnew.getText().toString();
                DB_handler a= new DB_handler(MviewApplication.ctx);
                Log.i(TAG,"insertion is called"+ip);
                a.insertIPDetails(ip);
                DB_handler db_handler=new DB_handler(MviewApplication.ctx);
                db_handler.open();
                Log.i(TAG,"getting from database");
                ArrayList<HashMap<String, String>> ipList= (ArrayList<HashMap<String, String>>) db_handler.selectIPdetails();
                ArrayList<String> list_count = new ArrayList<>();
                if(ipList!=null)
                {
                    System.out.println(" iplist is"+ipList);
                    for(int i=0;i<ipList.size();i++)
                    {
                        list_count.add(ipList.get(i).get("ip"));
                    }
                    System.out.println(" list_count for is is"+ list_count);
                    // BY SWAPNIL BANSAL 2/12/2022
                    List<String> UniqueNumbers1 = list_count.stream().distinct().collect(Collectors.toList());
                    System.out.println(" UniqueNumbers is"+UniqueNumbers1);
                    testEtnew.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,UniqueNumbers1));
                }

                if(Utils.checkifavailable(ip))
                {
                    if(!TextUtils.isEmpty(ip))
                    {
                        Log.i(TAG,"Starting traceroute"+ip);

                        Utils.showLongToast(getContext(),"Starting traceroute ");
                        try {
                            Log.i(TAG,"Performing traceroute");

                            showProgressBeforeLoadingData("Performing traceroute" + "\n" + "This may take upto 1 minute");
                        }
                        catch (Exception e)
                        {
                            Log.i(TAG,"exception is i"+e.getMessage());
                            e.printStackTrace();
                        }
                        Log.i(TAG,"Entering ping class");
                        new  PingTask(getContext(), ip).execute();

                    }
                    else
                    {
                        Utils.showToast(getContext(), Constants.NO_URL);
                    }
                }
                else
                {
                    Utils.showToast(getContext(),Constants.NO_URL);
                }
            }
        });
//        forcancelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //dismissProgress();
//                getConfirmationFromUser();
//            }
//        });



//
//        final Dialog dialog = new Dialog(getContext(), R.style.AlertDialogTheme);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(v);
//        dialog.setCanceledOnTouchOutside(true);
//        dialog.show();

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_new);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("videotest fragmnet navigate");
                getActivity().onBackPressed();
            }
        });
        return  v;

    }

    // by swapnil 11/25/2022
    private void getConfirmationFromUser() {
        final AlertDialog.Builder alertdialog=new AlertDialog.Builder(getContext(),R.style.AlertDialogTheme);
        alertdialog.setMessage(Constants.USER_CONFIRMATION).
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().onBackPressed();
//                        toolbar.setTitle(R.string.speedtest);
//                        fragmentManager.popBackStack("speed_test", FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                        for(int entry = 0; entry < fragmentManager.getBackStackEntryCount(); entry++){
//                            Log.i("FRAGMENT", "Found fragment: " + fragmentManager.getBackStackEntryAt(entry).getId() +fragmentManager.getFragments());
//                        }


                    }
                })
                .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showProgressBeforeLoadingData("Performing traceroute" + "\n" + "This may take upto 1 minute");
                    }
                }).show();


    }


    @SuppressLint("Range")
    private ArrayList<HashMap<String, String>> getUrlFromTable(Context context) {
        ArrayList<HashMap<String,String>> idList = null;
        try {
            Class db_c = Class.forName("database.DatabaseClass");
            Class<?>[] type = {Context.class};
            Constructor<?> cons = db_c.getConstructor(type);
            Object[] ctxObj = {context};
            Object o = cons.newInstance(ctxObj);
            Method select_m = db_c.getDeclaredMethod("selectUrlName", null);
            idList = (ArrayList<HashMap<String,String>>) select_m.invoke(o, null);
            select_m.setAccessible(true);
            select_m.invoke(o, null);
        } catch (Exception e) {
            System.out.println("Exception from  getUrlFromTable" + e.toString());
            e.printStackTrace();
        }

        return idList;




    }


    public  void showPingAlert(Context context) {

        System.out.println(" entering ping alert");
        //  pingreport1("","","","",context,"198.12.250.223");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public  void pingreport1(String a, String b, String c, String data, Context context,String ipNew) {
        String ip = ipNew;
        Log.i(TAG,"entering ping alert one"+ip);

        String s = null;
        String time_unit = " ";
        String sudoScript = " ";
       /* if (data != " " || data != "") {
            String[] strArray = data.split(",");
            ip = strArray[0];
        }*/
        if (ip.equals("") || ip.equals(" ") || ip.equals((Object)null)) {
            ip = ipNew;
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_tracerout, null);


    }

    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static void addoutputindb(String outputlist2,String evt,Context context) {

        Class rr_c;
        try {
            /*System.out.println("entering in data base class from eclipse");
            rr_c = Class.forName("database.DatabaseClass");
            Class<?>[] type = {Context.class};
            Constructor<?> cons = rr_c.getConstructor(type);
            Object[] ctxObj = {context};
            Object o = cons.newInstance(ctxObj);
            Method method;
            Object[]obj= {evt,evt,outputlist2,getDateTime(),"init"};
            Class<?>params[]=new Class[obj.length];
            for (int i = 0; i < obj.length; i++) {
                if (obj[i] instanceof Integer) {
                    params[i] = Integer.TYPE;
                } else if (obj[i] instanceof String) {
                    params[i] = String.class;
                }

            }



*/

          /*  System.out.println(obj);
            Method req_m =rr_c.getDeclaredMethod("insertInLoggingAgentTable",  params);
            System.out.println("request m is "+req_m);
            req_m.setAccessible(true);
            req_m.invoke(o, obj);*/

        } catch (Exception e) {
            System.out.println("exception is addoutputindb  "+e.toString());

            e.printStackTrace();
        }
        // TODO Auto-generated catch block





    }

    public static String ping(String address, int packets, int ttl) throws Exception{
        //execute ping command
        String format = "ping -n -c %d -t %d %s";
        String command = String.format(format, packets, ttl, address);
        Process process = Runtime.getRuntime().exec(command);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        // Grab the results
        StringBuilder log = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            log.append(line + "\n");

        }
        Log.i(TAG,"Trace route result getting returned as "+log);
        return log.toString();
    }

    static String parseHopIp(String pingOutput){
        String hopIp;
        pingOutput = pingOutput.replaceAll("[\\t\\n\\r]+"," ");
        String[] pingOutputArray = pingOutput.split(" ");
        hopIp = pingOutputArray[8].substring(0, pingOutputArray[8].length() - 1);
        if (hopIp.equals("byte")) {
            hopIp = pingOutputArray[10].substring(0, pingOutputArray[10].length() - 1);
        }
        if (pingOutputArray[7].equals("---")) {
            hopIp = "";
        }
        return hopIp;
    }

    //    static String parseHopPingTimes(String pingOutput){
//        String hopPingTime;
//        pingOutput = pingOutput.replaceAll("[\\t\\n\\r]+"," ");
//        String[] probePingOutputArray = pingOutput.split(" ");
//        if (probePingOutputArray[13].equals("packets") || probePingOutputArray[13].equals("exceeded")) {
//            hopPingTime = "0";
//          Log.i(TAG," HOP PING YIME IS"+hopPingTime);
//        } else {
//            hopPingTime = probePingOutputArray[13].substring(5);
//            Log.i(TAG," HOP PING YIME IS is "+hopPingTime);
//        }
//        return hopPingTime;
//    }
    static String parseHopPingTimes(String pingOutput){
        String hopPingTime;
        pingOutput = pingOutput.replaceAll("[\\t\\n\\r]+"," ");
        String[] probePingOutputArray = pingOutput.split(" ");
        if (probePingOutputArray[13].equals("packets") || probePingOutputArray[13].equals("exceeded")) {
            hopPingTime = "0";
        } else {
            hopPingTime = probePingOutputArray[13].substring(5);
        }
        return hopPingTime;
    }
    public static void appendLog(String text) {

        File logFile = new File( "sdcard/jarlogs.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {

                System.out.println(" exception in new file is"+e.getMessage());
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            // BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            System.out.println(" exception is"+e.getMessage());
            e.printStackTrace();
        }
    }

    public void start() {

        DB_handler db_helper = new DB_handler(MviewApplication.ctx);
        db_helper.open();

        String current_status = db_helper.current_status("Traceroute");
        String start_date = db_helper.start_date("Traceroute");
        String end_date = db_helper.end_date("Traceroute");
        Date currentdate = Utils.getcurrentdate();
        Date start_date_ = Utils.convertStringtoDate(start_date);
        Date end_date_ = Utils.convertStringtoDate(end_date);

        Log.i(TAG, "current_status is of agent is " + current_status + " and start date is " + start_date + " and end date is " + end_date);

        if (current_status == null || current_status.equalsIgnoreCase("completed")) {
            Log.d(TAG, "current status is completed");
            if (start_date_ != null && end_date_ != null &&
                    currentdate.after(start_date_) && currentdate.before(end_date_))
            {
                List<GagdAgent> initAgents = db_helper.getAgentsByNameAndStatus("PING","INIT");
                if (!initAgents.isEmpty()) {

                    for (GagdAgent agent : initAgents) {
                        String ip="1.8.8.1";
                        new  PingTask(MviewApplication.ctx, agent.getUrl()).execute();

                    }

                }else {
                    Utils.appendLog("ELOG_NO_IP_FOR_TRACEROUTE_PANEL : No Ip data available in DB for ping test");
                }



            }
        } else {
            Log.d(TAG, "Status of agent is not active");
        }


        // Reset index to start over
        db_helper.close();
    }

    public  class PingTask extends AsyncTask<Void, String, BufferedReader> {
        private final String ip;

        Context context;
        //   ProgressBar progressBar;


        public PingTask(Context context, String ip) {
            this.context=context;
            this.ip=ip;
            //  this.progressBar=progressBar;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected BufferedReader doInBackground(Void... voids) {
            try {
                // JSONObject jsonObject=new JSONObject();

                Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "ping -c 5 -t 64 " + ip});
                BufferedReader stdInput1 = new BufferedReader(new InputStreamReader(p.getInputStream()));
                return stdInput1;
            } catch (Exception var19) {
                Log.i(TAG," exception is" + var19);

            }


            return null;

        }

        @Override
        protected void onPostExecute(BufferedReader stdInput1) {
            super.onPostExecute(stdInput1);
//            handler.removeCallbacks(timeoutRunnable); // Remove timeout

            // progressBar.setVisibility(View.GONE);
            Log.i(TAG,"std  input is "+stdInput1);

            try {
                JSONObject jsonObject=new JSONObject();
                String rtt_min = null, rtt_avg = null, rtt_max = null, rtt_mdev = null, rtt_dev = null, time_unit_val = null, packet_loss = null;
                String sudoScript;
                Log.d(TAG, "onPostExecute: "+stdInput1.readLine());
                while ((sudoScript = stdInput1.readLine()) != null) {
                    Log.i(TAG,"sudoscript is "+sudoScript);
                    if (sudoScript.contains("rtt min/avg/max/mdev")) {
                        //rtt min/avg/max/mdev = 29.981/53.028/102.847/29.209
                        //PING www.google.com (142.250.194.132) 56(84) bytes of data.
                        String values = sudoScript.substring(sudoScript.indexOf("=") + 1, sudoScript.length());
                        Log.i(TAG,"values for ping" + values);
                        String[] splitvalues = values.split("/");
                        rtt_min = splitvalues[0];
                        jsonObject.put("rtt_min", rtt_min);
                        rtt_avg = splitvalues[1];
                        jsonObject.put("rtt_avg", rtt_avg);
                        rtt_max = splitvalues[2];
                        System.out.println("rtt max is " + rtt_max);
                        jsonObject.put("rtt_max", rtt_max);
                        rtt_mdev = splitvalues[3];
                        String[] rtt_dev_ = rtt_mdev.split(" ");
                        rtt_dev = rtt_dev_[0];
                        time_unit_val = rtt_dev_[1];
                        jsonObject.put("rtt_dev", rtt_dev_[0]);
                        jsonObject.put("time_unit", rtt_dev_[1]);
                    }
                    if (sudoScript.contains("(")) {
                        System.out.println(" entering ( feature trace route box ");
                       // if (Utils.checkifavailable(feature)) {
                            System.out.println(" entering feature trace route box ");

                            {
                                System.out.println(" entering trace route box ");
                                String urlForTraceroute = sudoScript.substring(sudoScript.indexOf("(") + 1, sudoScript.indexOf(")") - 0);
                                System.out.println("values for url in first check is " + urlForTraceroute);
                                //traceroute = new TraceRouteTask(urlForTraceroute, 15, 1, true, 3, context, ip, progressBar);
                               // traceroute.execute();
                            }
                        //}
                    }
                    // by swapnil 18/11/2022
                    if (sudoScript.contains("PING")) {

                        // if (Utils.checkifavailable(feature)) {



                            Log.i(TAG,"entering trace route box ");

                            String urlForTraceroute = sudoScript.substring(sudoScript.indexOf("PING"));
                            Log.i(TAG,"values for url in first check is " + urlForTraceroute);
                            String[] s1 = urlForTraceroute.trim().split("\\s+");

                            String s2= Arrays.toString(s1);
                            Log.i(TAG,"S1" +s2);
                            Log.i(TAG," ip is "+s1[2]);
                            String s3=(s1[2]);
                            Log.i(TAG," s3 is"+s3);
                            String urlFinal=s3.replaceAll("[\\[\\](){}]","");
                            Log.i(TAG,"accountlist is "+urlFinal);

                            //String[] s4 = s3.trim().split("");
                            // System.out.println(" s4 is "+Arrays.toString(s4));
                            // [, (, 1, 4, 2, ., 2, 5, 0, ., 1, 8, 2, ., 1, 3, 2, )]
                            //  3,5,7,9,11,13,15,17,19,21,23,25,27,29,31
                            // String a=s4[2]+s4[3]+s4[4]+s4[5]+s4[6]+s4[7]+s4[8]+s4[9]+s4[10]+s4[11]+s4[12]+s4[13]+s4[14]+s4[15]+s4[16];
                            // System.out.println(" final  is"+a);
                            // String s5=Arrays.toString(s4);
                            // System.out.println(" s5 is"+s5);
                            // String a=s5.indexOf(4);


//                    if(s2.contains("("))
//                    {
//                        String values = s2.substring(sudoScript.indexOf("(")+4,sudoScript.indexOf("(")+19);
//                        System.out.println(" values is "+values);
//                    }
                            Log.i(TAG," time is "+ Config.getDateTime());
                            traceroute = new TraceRouteTask(urlFinal, 10, 1, true, 3, context, ip);
                            traceroute.execute();

                        //}
                        //  }

                    }
//                    else if (sudoScript.contains("packet loss")) {
//                        String packetloss = sudoScript.substring(sudoScript.indexOf("packet loss") - 4, sudoScript.indexOf("packet loss") - 1);
//                        Log.i(TAG,"values for packet loss in second check" + packetloss);
//                        String[] splitvalues = packetloss.split("/");
//                        packet_loss = splitvalues[0];
//                        jsonObject.put("packet_loss", packet_loss);
//                        Log.i(TAG,"packet loss is " + packet_loss);
//
//                    }


                }

                jsonObject.put("host", ip);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                date_time = now.format(dtf);
                jsonObject.put("dateandtime", date_time);
                JSONArray jsonArray=new JSONArray();
                jsonArray.put(jsonObject);
                Log.i(TAG,"Ping json from lib is  " + jsonObject.toString());
//                RequestResponse.sendOldEvents(jsonArray,"ping_report");
                // rtt_avg_val_Tv.setText(rtt_avg);
                // rtt_dev_val_Tv.setText(rtt_dev);
                // rtt_max_val_Tv.setText(rtt_max);
                // rtt_min_val_Tv.setText(rtt_min);
                // time_val_Tv.setText(time_unit_val);
                // host_Tv_Val.setText(ip);
                // packetLoss_Tv_val.setText(packet_loss);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


        }
    }

    public  class TraceRouteTask extends AsyncTask<Void, String, String> {
        private final Context context;
        private String res;
        String address,ip;
        int max_ttl;
        int first_ttl;
        boolean resolve;
        int probes;
        TextView view;
        String error;
        //  ProgressBar progressBar;

        public TraceRouteTask(String address, int max_ttl, int first_ttl, boolean resolve, int probes, Context context, String ip){
            this.address = address;
            this.max_ttl = max_ttl;
            this.first_ttl = first_ttl;
            this.resolve = resolve;
            this.probes = probes;
            this.context=context;
            this.ip=ip;
            // this.progressBar=progressBar;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG,"Entering trace route part");
            // dismissProgress();
            // Utils.showLongToast(context,"Performing traceroute ");
            // showProgressBeforeLoadingData("Performing traceroute this may take 30 seconds");
            // progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params){
            StringBuilder log = new StringBuilder();
            String res = " ";
            try {
                // String format = "traceroute to %s (%s), %d hops max, 60 byte packets\n";
                String line;

                //resolves target hostname if necessary
                if (resolve) {
                    String tracerouteToHostname;
                    String tracerouteToIP;
                    try {
                        tracerouteToHostname = InetAddress.getByName(address).getHostName();
                        Log.i(TAG,"Entering trace route part");
                        tracerouteToIP = InetAddress.getByName(address).getHostAddress();
                    } catch (UnknownHostException e) {
                        tracerouteToHostname = address;
                        tracerouteToIP = address;
                    }
                    line = String.format(tracerouteToHostname, tracerouteToIP, max_ttl);
                } else {
                    line = String.format(address, address, max_ttl);
                }

                // log.append("$ traceroute -f " + first_ttl + " -m " + max_ttl + " -q " + probes + " " + address + "\n");
                //log.append(line);
            } catch(Exception e) {
                error = "ERROR";
                cancel(true);
            }

            try{
                String[][] hopProbeTimes = new String[max_ttl][probes];
                String[] hopAddress = new String[max_ttl];
                String[] hopHostname = new String[max_ttl];
                for (int i = first_ttl - 1; i < max_ttl; i++) {
                    if(this.isCancelled()) break;
                    String format, line;
                    int remaining_ping = probes;
                    String hopPingOutput = ping(InetAddress.getByName(address).getHostAddress(), 1, i + 1);
                    hopAddress[i] = parseHopIp(hopPingOutput);

                    if(hopAddress[i].equals("")){
                        format = " %d  ";
                    } else {
                        format = " %d  %s (%s)";
                    }

                    //resolves hop hostname
                    if(hopAddress[i].equals("")) {
                        line = String.format(format, i + 1);
                    } else {
                        if (resolve) {
                            try {
                                hopHostname[i] = InetAddress.getByName(hopAddress[i]).getHostName();
                            } catch (UnknownHostException e) {
                                hopHostname[i] = hopAddress[i];
                            }
                            line = String.format(format, i + 1, hopHostname[i], hopAddress[i]);
                        } else {
                            format = " %d  %s (%<s)";
                            line = String.format(format, i + 1, hopAddress[i]);
                        }
                    }
                    log.append(line);

                    try{
                        //probes each hop a certain number of times
                        if(!hopAddress[i].equals("")) {
                            while (remaining_ping != 0) {
                                String probePingOutput = ping(hopAddress[i], 1, 10);
                                Log.i(TAG," probePingOutput IS"+probePingOutput);
                                hopProbeTimes[i][remaining_ping - 1] = parseHopPingTimes(probePingOutput);
                                Log.i(TAG," probePingOutput IS is is"+ hopProbeTimes[i][remaining_ping - 1]);
                                if (hopProbeTimes[i][remaining_ping - 1].equals("0 ms")) {
                                    line = "       " + hopProbeTimes[i][remaining_ping - 1] + "       ";
                                    Log.i(TAG," LINE 4 IS IS"+line);
                                } else {
                                    line = "  " + hopProbeTimes[i][remaining_ping - 1] + " ms";
                                    Log.i(TAG," LINE 5 IS IS"+line);
                                }
                                Log.i(TAG," RESULT IS IN TRACEROUTE"+log.append(line));
                                //log.append(line);
                                remaining_ping--;
                            }
                        } else {
                            //if probe failed display * in output
                            hopProbeTimes[i][0] = "0";
                            hopProbeTimes[i][1] = "0";
                            hopProbeTimes[i][2] = "0";
                            line = "unidentified"+ " " +"(unidentified)" +"      " + hopProbeTimes[i][0] + "  ms    " + hopProbeTimes[i][1] + "  ms    " + hopProbeTimes[i][2] + "   ms   ";
                            // line = "unidentified"+ " " +"(unidentified)" +"      " + hopProbeTimes[i][0] + "  ms    " + hopProbeTimes[i][1] + "  ms    " + hopProbeTimes[i][2] + "   ms   "+hopProbeTimes[i][3] + "   ms   "+hopProbeTimes[i][4] + "   ms   "+hopProbeTimes[i][5] + "   ms   ";
                            log.append(line);
                        }
                    }catch(Exception e){
                        error = "ERROR";
                        cancel(true);
                    }

                    line = "\n";
                    //  System.out.println(" result is"+log.append(line));
                    res = String.valueOf(log.append(line));
                    log.append(line);
                    publishProgress(log.toString());
                    String lastAddressCheck = InetAddress.getByName(address).getHostAddress();

                    //checks if target address is reached
                    if (hopAddress[i].equals(lastAddressCheck)) {
                        error = "";
                        return res;
                    }
                }
            } catch (Exception e){
                error = "ERROR";
                cancel(true);
            }
            return res;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            res = result;
            // BY SWAPNIL BANSAL 26/09/2022
            try {


                Log.i(TAG, "Traceroute Result is" + result + "time is " + Config.getDateTime());
                String[] s1 = result.trim().split("\\s+");
                Log.i(TAG, "s1 is"+Arrays.toString(s1));
                ArrayList<String> list_count = new ArrayList<String>();
                ArrayList<String> list_time = new ArrayList<String>();
                ArrayList<String> list_hops = new ArrayList<String>();

                int j;
                for (j = 0; j < s1.length; j += 9) {
                    list_count.add(s1[j]);
                }
                for (j = 1; j < s1.length; j += 9) {
                    list_hops.add(s1[j]);
                }
                for (j = 5; j < s1.length; j += 9) {
                    list_time.add(s1[j]);
                }
                Log.i(TAG, " count is new " + list_count);
                Log.i(TAG, " hops is new " + list_hops);
                Log.i(TAG, "time is new" + list_time);
                tableView.setVisibility(View.VISIBLE);
                traceRouteList(list_count, list_time, list_hops);
                dismissProgress();
                JSONArray pdoInformation = new JSONArray();
                JSONArray mtrInformation = new JSONArray();

                JSONObject mtr = new JSONObject();

                try {
                    mtr.put("psize", "");
                    mtr.put("bitpattern", "");
                    mtr.put("tests", "");
                    mtr.put("dst", ip);
                    mtr.put("src", "");
                    mtr.put("tos", "");
                    for (int a = 1; a <= list_count.size(); a++) {
                        JSONObject pDetail1 = new JSONObject();
                        pDetail1.put("Avg", list_time.get(a));
                        pDetail1.put("Wrst", "");
                        pDetail1.put("Jint", "");
                        pDetail1.put("Loss", "");
                        pDetail1.put("count", a);
                        pDetail1.put("host", list_hops.get(a));
                        pDetail1.put("Best", "");
                        pDetail1.put("Javg", "");
                        pDetail1.put("Drop", "");
                        pdoInformation.put(pDetail1);
                    }
                } catch (Exception var33) {
                }

                mtrInformation.put(mtr);

                JSONObject pDetail20 = new JSONObject();
                try {
                    pDetail20.put("mtr", mtrInformation);
                    pDetail20.put("hubs", pdoInformation);
                } catch (JSONException var32) {
                    var32.printStackTrace();
                }


                JSONArray jsonArray = new JSONArray();
                jsonArray.put(pDetail20);
                Log.i(TAG, "Traceroute json from lib is  " + pDetail20.toString());
                //  System.out.println("Traceroute json from lib is  " + pDetail20.toString());
                //commented by vikas
//                RequestResponse.sendOldEvents(jsonArray, "site_speed_mtr");
                // progressBar.setVisibility(View.GONE);
                Utils.showLongToast(context, "Trace route result sent.");
                addoutputindb(pDetail20.toString(), "site_speed_mtr", context);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        @Override
        protected void onCancelled(){
            super.onCancelled();
        }
    }
    private void dismissProgress() {
        if(progressDialog!=null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
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
                        "CLICK TO CANCEL THE TEST",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // progressDialog.dismiss();
                                //getActivity().onBackPressed();
                                // by swapnil 11/25/2022
                                getConfirmationFromUser();
                            }
                        });

                progressDialog.show();
            }
        }
    }



    private void traceRouteList(ArrayList<String> list_count, ArrayList<String> list_time, ArrayList<String> list_hops) {
        this.list_count=list_count;
        this.list_time=list_time;
        this.list_hops=list_hops;
        adapter = new TraceRouteTableViewAdapter(getContext());
        tableView.setAdapter(adapter);
        tableView.setTableViewListener(new MyTableViewListener(getContext()));
        adapter.setAllItems(getColumnHeaderList(), getRowHeaderList(), getCellList());


    }

    private List<List<Cell>> getCellList() {
        List<List<Cell>> mCellList = new ArrayList<>();
        List<Cell> cellList=null;
        for (int i = 0; i < list_count.size(); i++) {
            cellList = new ArrayList<>();
            // cellList.add(new Cell(i +"",list_count.get(i),1));
            cellList.add(new Cell(i +"",list_hops.get(i),0));
            cellList.add(new Cell(i +"",list_time.get(i),0));
            mCellList.add(cellList);

        }

        return mCellList;

    }

    private List<ColumnHeader> getColumnHeaderList() {
        ArrayList<String> columnStringList=Config.getTraceRouteHeaders();
        ArrayList<ColumnHeader> columnHeaderList=new ArrayList<>();
        for(int i=0;i< columnStringList.size();i++) {
            ColumnHeader header = new ColumnHeader(String.valueOf(i), columnStringList.get(i));
            columnHeaderList.add(header);
        }
        return columnHeaderList;
    }

    private List<RowHeader> getRowHeaderList() {
        List<RowHeader> rowHeaderList = new ArrayList<>();
        for (long i = 0; i < list_count.size(); i++) {
            long text = i + 1;
            RowHeader header = new RowHeader(String.valueOf(i), "" + text);
            rowHeaderList.add(header);

        }
        return rowHeaderList;

    }

}