package com.newmview.wifi.helper;



import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.dashboard.roomdb.gagdagent.GagdAgent;
import com.functionapps.mview_sdk2.main.Mview;
import com.mview.airtel.R;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public  class DialogClass {
    private static Context context;
    private static TraceRouteTask traceroute=null;
    private static String date_time;
    private final String feature;
    ProgressBar progressBar;
    //  private  String date_time=null;
    private  TextView rtt_avg_val_Tv,rtt_dev_val_Tv,rtt_min_val_Tv,host_Tv_val,time_val_Tv,rtt_max_val_Tv,packetLoss_Tv_val;
    private TextView host_Tv_Val;
    private LinearLayout pingLL;
//    private static final int TIMEOUT = 10000;

    private Handler handler = new Handler();

//    private Runnable timeoutRunnable = new Runnable() {
//        @Override
//        public void run() {
//            if (progressBar != null) {
//                progressBar.setVisibility(View.INVISIBLE);
//                Toast.makeText(MviewApplication.ctx,"Cannot connect to Server. Please try again.", Toast.LENGTH_SHORT).show();            }
//        }
//    };

    public DialogClass(String feature)
    {
        this.feature=feature;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void pingreport(String a, String b, String c, String data1, Context context) {
        // TODO Auto-generated method stub

        try {
            pingreport2(context);
        } catch (Exception e) {
            System.out.println("error is" + e.getMessage());
            e.printStackTrace();

        }


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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void pingreport2(Context context) {
        ArrayList<HashMap<String,String>> detailsList = getUrlFromTable(context);
        if (detailsList != null) {
            try {
                for (int j = 0; j < detailsList.size(); j++) {
                    HashMap<String, String> hashMap = detailsList.get(j);
                    String url_name = hashMap.get("url");
                    System.out.println(" url_name is"+url_name);
                    pingreport1("","","","",context,url_name);
                }

            } catch (Exception e) {
                System.out.println("exception in dynamic method" + e.getMessage());
                e.printStackTrace();
            }


        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public  void showPingAlert(Context context) {


        pingreport1("","","","",context,"198.12.250.223");



    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public  void pingreport1(String a, String b, String c, String data, Context context,String ipNew) {
        String ip = ipNew;
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
        View view = inflater.inflate(R.layout.ping_layout, null);
        TableLayout pingTL=view.findViewById(R.id.pingTL);
        rtt_min_val_Tv=view.findViewById(R.id.rtt_min_val_Tv);
        rtt_max_val_Tv=view.findViewById(R.id.rtt_max_val_Tv);
        rtt_avg_val_Tv=view.findViewById(R.id.rtt_avg_val_Tv);
        rtt_dev_val_Tv=view.findViewById(R.id.rtt_dev_val_Tv);
        time_val_Tv=view.findViewById(R.id.time_val_Tv);
        host_Tv_Val=view.findViewById(R.id.host_val_Tv);
        packetLoss_Tv_val=view.findViewById(R.id.packetLoss_val_Tv);
        pingLL=view.findViewById(R.id.pingLL);
        AutoCompleteTextView testEt=view.findViewById(R.id.testEt);
        testEt.setThreshold(1);
        DB_handler db_handler=new DB_handler(MviewApplication.ctx);
        db_handler.open();

        //System.out.println(helper.Config.getDateTime()+" : via readtable  Mutex  status is "+uploadinit);
        ArrayList<HashMap<String, String>> ipList= (ArrayList<HashMap<String, String>>) db_handler.selectIPPingdetails();
        db_handler.close();
        ArrayList<String> list_count = new ArrayList<>();
        if(ipList!=null)
        {
            System.out.println(" iplist is"+ipList);
            for(int i=0;i<ipList.size();i++)
            {
                list_count.add(ipList.get(i).get("ip"));
            }
            System.out.println(" list_count for is is"+ list_count);
            testEt.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,list_count));
        }
        Button startBtn=view.findViewById(R.id.startBtn);
        progressBar=view.findViewById(R.id.progressBar);
        // progressBar.getProgressDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

        if(Utils.checkifavailable(feature))
        {
            if(feature.equalsIgnoreCase("ping"))
            {
                pingLL.setVisibility(View.VISIBLE);
            }
            else
            {
                pingLL.setVisibility(View.GONE);
            }
        }
        String strength = "";
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                handler.postDelayed(timeoutRunnable, TIMEOUT); // Start timeout

//                new PingTask(context," ").execute();
                String ip=testEt.getText().toString();
                db_handler.open();
                db_handler.insertIPPingDetails(ip);
                db_handler.close();
                testEt.setThreshold(1);
                DB_handler db_handler= new DB_handler(MviewApplication.ctx);
                db_handler.open();
                ArrayList<HashMap<String, String>> ipList= (ArrayList<HashMap<String, String>>) db_handler.selectIPPingdetails();
                db_handler.close();
                ArrayList<String> list_count = new ArrayList<>();
                if(ipList!=null)
                {
                    //Log.i(TAG,"iplist is"+ipList);

                    for(int i=0;i<ipList.size();i++)
                    {
                        list_count.add(ipList.get(i).get("ip"));
                    }
                  //  Log.i(TAG,"list_count for is is"+ list_count);
                    List<String> UniqueNumbers = list_count.stream().distinct().collect(Collectors.toList());
                   // Log.i(TAG," UniqueNumbers is"+UniqueNumbers);
                    testEt.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,UniqueNumbers));
                }
                if(Utils.checkifavailable(ip))
                {
                    if(!TextUtils.isEmpty(ip))
                    {
                        Utils.appendLog("ELOG_RUN_PING_UI: Going to run ping test from UI: "+ip);
                        new PingTask(context, ip,rtt_avg_val_Tv,rtt_dev_val_Tv,rtt_max_val_Tv,
                                rtt_min_val_Tv,host_Tv_Val,time_val_Tv,packetLoss_Tv_val,progressBar).execute();

                    }
                    else
                    {
                        Utils.showToast(context,Constants.NO_URL);
                    }
                }
                else
                {
                    Utils.showToast(context,Constants.NO_URL);
                }
            }
        });

        final Dialog dialog = new Dialog(context, R.style.AlertDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();


    }


    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
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

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        // Grab the results
        StringBuilder log = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            log.append(line + "\n");

        }
        System.out.println("Trace route result getting returned as "+log);
        return log.toString();
    }

    public static String parseHopIp(String pingOutput){
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

    public static String parseHopPingTimes(String pingOutput){
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



    public  class PingTask extends AsyncTask<Void, String, BufferedReader> {
        private String ip;

        Context context;
        ProgressBar progressBar;



        public PingTask(Context context, String ip, TextView rtt_avg_val_tv, TextView rtt_dev_val_tv, TextView rtt_max_val_tv, TextView rtt_min_val_tv, TextView host_tv_val, TextView time_val_tv, TextView packetLoss_tv_val, ProgressBar progressBar) {

            this.context=context;
            this.ip=ip;
            this.progressBar=progressBar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected BufferedReader doInBackground(Void... voids) {
            BufferedReader stdInput1 = null;
            try {


                InetAddress[] inetAddress;
                if (isValidIPv4(ip)) {
                    // If `ip` is already an IP address
                    Log.d("TAG", "doInBackground: ipv4");
                    inetAddress = InetAddress.getAllByName(ip);
                } else {
                    Log.d("TAG", "doInBackground: domain name");

                    // If `ip` is a domain name, resolve its IP address
                    inetAddress = InetAddress.getAllByName(new URL("http://" + ip).getHost());
                }
                List<InetAddress> ipv4Addresses = new ArrayList<>();
                for (InetAddress address : inetAddress) {
                    if (address instanceof Inet4Address) {
                        ipv4Addresses.add(address);
                       Utils.appendLog( "ELOG_LIST_IP: ipv4 addresses "+address);
                    }
                }



//                if (inetAddress != null && inetAddress.length > 0) {
//                    InetAddress firstAddress = inetAddress[0];
//                    String firstIpAddress = firstAddress.getHostAddress();
//                    Log.d("TAG", "First IP address: " + firstIpAddress);
//
//                    // You can use firstIpAddress as needed
//                }
                Utils.appendLog("ELOG_RUN_PING_UI: doInBackground of ping test from UI: "+ ipv4Addresses.get(0).getHostAddress());

//                Utils.appendLog("ELOG_RESOLVED_IPV4: "+ Utils.getResolverIpv4());

                Utils.appendLog("ELOG_PRIVATE_IP_UI: is  "+Utils.getPIP());
                Utils.appendLog("ELOG_RESOLVED_IP_UI: is  "+ inetAddress[0].getHostAddress());
                Utils.appendLog("ELOG_PUBLIC_IP_UI: is  "+Utils.publicIp());

                Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "ping -c 10 -s 1000 " + ipv4Addresses.get(0).getHostAddress()});

//                Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "ping -c 10 -t 64 " + inetAddress[0].getHostAddress()});
                    stdInput1 = new BufferedReader(new InputStreamReader(p.getInputStream()));
//                    handler.removeCallbacks(timeoutRunnable); // Remove timeout


                return stdInput1;
                }
            catch (Exception var19) {
                System.out.println(" exception is" + var19);
            }

            return null;
        }

        @Override
        protected void onPostExecute(BufferedReader stdInput1) {
            super.onPostExecute(stdInput1);
            progressBar.setVisibility(View.GONE);
            Utils.appendLog("ELOG_RUN_PING_UI: onPostExecute of ping test from UI: ");

            System.out.println("std  input is  " + stdInput1);
            try {
                JSONObject jsonObject=new JSONObject();


                String rtt_min = null, rtt_avg = null, rtt_max = null, rtt_mdev = null, rtt_dev = null, time_unit_val = null, packet_loss = null;
                String sudoScript;
                while ((sudoScript = stdInput1.readLine()) != null) {
                    Utils.appendLog("ELOG_PING_COMMAND_RESULT_UI: "+sudoScript);
                    if (sudoScript.contains("rtt min/avg/max/mdev")) {
                        String values = sudoScript.substring(sudoScript.indexOf("=") + 1, sudoScript.length());
                        System.out.println("values for ping" + values);
                        String[] splitvalues = values.split("/");
                        rtt_min = splitvalues[0];
                        System.out.println("rtt is " + rtt_min);
                        jsonObject.put("rtt_min", rtt_min);
                        rtt_avg = splitvalues[1];
                        System.out.println("rtt_avg " + rtt_avg);
                        jsonObject.put("rtt_avg", rtt_avg);
                        rtt_max = splitvalues[2];
                        System.out.println("rtt max is " + rtt_max);
                        jsonObject.put("rtt_max", rtt_max);
                        rtt_mdev = splitvalues[3];
                        System.out.println("rtt_ven " + rtt_mdev);
                        String[] rtt_dev_ = rtt_mdev.split(" ");
                        rtt_dev = rtt_dev_[0];
                        time_unit_val = rtt_dev_[1];
                        jsonObject.put("rtt_dev", rtt_dev_[0]);
                        jsonObject.put("time_unit", rtt_dev_[1]);
                    }

                    if (sudoScript.contains("packets transmitted")) {
                        Pattern packetPattern = Pattern.compile("(\\d+) packets transmitted, (\\d+) received, (\\d+)% packet loss");
                        Matcher packetMatcher = packetPattern.matcher(sudoScript);
                        if (packetMatcher.find()) {
                            String packets_transmitted = null, packets_received = null;

                            packets_transmitted = packetMatcher.group(1);
                            packets_received = packetMatcher.group(2);
                            packet_loss = packetMatcher.group(3);
                            System.out.println("packets_transmitted: " + packets_transmitted);
                            System.out.println("packets_received: " + packets_received);
                            jsonObject.put("packets_transmitted", packets_transmitted);
                            jsonObject.put("packets_received", packets_received);
                        }
                    }

                    if (sudoScript.contains("(")) {

                        if(Utils.checkifavailable(feature))
                        {
                            if(feature.equalsIgnoreCase("traceroute"))
                            {
                                String urlForTraceroute = sudoScript.substring(sudoScript.indexOf("(") + 1, sudoScript.indexOf(")") - 0);
                                System.out.println("values for url in first check is " + urlForTraceroute+""+ip);
                                traceroute = new TraceRouteTask(urlForTraceroute,
                                        15,1,true,3,context,ip,progressBar);
                                traceroute.execute();
                            }
                        }


                    } else if (sudoScript.contains("packet loss")) {
//                        String packetloss = sudoScript.substring(sudoScript.indexOf("packet loss") - 4, sudoScript.indexOf("packet loss") - 1);
//                        System.out.println("values for packet loss in second check" + packetloss);
//                        String[] splitvalues = packetloss.split("/");
//                        packet_loss = splitvalues[0];
//                        jsonObject.put("packet_loss", packet_loss);
//                        System.out.println("packet loss is " + packet_loss);

                        String packetLossPattern = "(\\d+)% packet loss";
                        Pattern pattern = Pattern.compile(packetLossPattern);
                        Matcher matcher = pattern.matcher(sudoScript);

                        if (matcher.find()) {
                            packet_loss = matcher.group(1);
                            System.out.println("Values for packet loss in second check: " + packet_loss);
                            jsonObject.put("packet_loss", packet_loss+"%");
                        } else {
                            System.out.println("Packet loss value not found in the given script.");
                        }
                    }


                }


                jsonObject.put("host", ip);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                date_time = now.format(dtf);
                jsonObject.put("dateandtime", date_time);



                JSONArray jsonArray=new JSONArray();
                jsonArray.put(jsonObject);

                Utils.appendLog("ELOG_RUN_PING_UI: Ping Json is "+jsonArray);
//                DB_handler db_handler = null;
//                db_handler = new DB_handler(context);
//                db_handler.open();
//                db_handler.insertInLoggingAgentTable("PING", "ping_report", jsonObject.toString(), date_time, "init");
//                db_handler.close();
//                DashboardDatabase db = DashboardDatabase.getDatabase(context);
//                LoggingAgentDao loggingAgentDao = db.loggingAgentDao();
//
////
//                Log.d("TAG", "onPostExecute:detailsJson "+jsonObject);// detailsObject is your details data
//
//// Insert into the database by vikas
//                LoggingAgent loggingAgent = new LoggingAgent("Agent 1", "ping_report", jsonObject.toString(), "2024-06-19 10:02:56", "active");
//                db.databaseWriteExecutor.execute(() -> {
//                    loggingAgentDao.insert(loggingAgent);
//                    Log.d("TAG", "Data is inserted for logging agent ");
//                });
//
//                db.databaseWriteExecutor.execute(() -> {
//                    List<LoggingAgent> loggingAgents = loggingAgentDao.getAllLoggingAgents();
//                    for (LoggingAgent agent : loggingAgents) {
//                        Log.d("LoggingAgent", "Agent: " + agent.getAgentName() + ", Output: " + agent.getAgentOutput());
//                        // Deserialize JSON string back to object
//                        try {
//                            JSONObject detailsObject = new JSONObject(agent.getAgentOutput());
//                            // Convert JSONObject to JSONArray if necessary
//                            JSONArray agentOutput = new JSONArray();
//                            agentOutput.put(detailsObject);
//                            Log.d("TAG", "json array agentout "+agentOutput);
//                            RequestResponse.sendOldEvents(agentOutput,"ping_report");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                });
                System.out.println("Ping json from lib is  " + jsonObject.toString());

                rtt_avg_val_Tv.setText(rtt_avg);
                rtt_dev_val_Tv.setText(rtt_dev);
                rtt_max_val_Tv.setText(rtt_max);
                rtt_min_val_Tv.setText(rtt_min);
                time_val_Tv.setText(time_unit_val);
                host_Tv_Val.setText(ip);
                packetLoss_Tv_val.setText(packet_loss+"%");



            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


        }
    }


    private boolean isValidIPv4(String ip) {
        String ipv4Pattern =
                "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                        "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                        "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                        "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        Pattern pattern = Pattern.compile(ipv4Pattern);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }
    public  class TraceRouteTask extends AsyncTask<Void, String, String>{
        private final Context context;
        private String res;
        String address,ip;
        int max_ttl;
        int first_ttl;
        boolean resolve;
        int probes;
        TextView view;
        String error;
        ProgressBar progressBar;

        public TraceRouteTask(String address, int max_ttl, int first_ttl, boolean resolve, int probes, Context context, String ip, ProgressBar progressBar){
            this.address = address;
            this.max_ttl = max_ttl;
            this.first_ttl = first_ttl;
            this.resolve = resolve;
            this.probes = probes;
            this.context=context;
            this.ip=ip;
            this.progressBar=progressBar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utils.showLongToast(context,"Performing traceroute..");
            progressBar.setVisibility(View.VISIBLE);
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
                                String probePingOutput = ping(hopAddress[i], 1, 30);
                                hopProbeTimes[i][remaining_ping - 1] = parseHopPingTimes(probePingOutput);
                                if (hopProbeTimes[i][remaining_ping - 1].equals("0 ms")) {
                                    line = "       " + hopProbeTimes[i][remaining_ping - 1] + "       ";
                                } else {
                                    line = "  " + hopProbeTimes[i][remaining_ping - 1] + " ms";
                                }
                                log.append(line);
                                remaining_ping--;
                            }
                        } else {
                            //if probe failed display * in output
                            hopProbeTimes[i][0] = "0";
                            hopProbeTimes[i][1] = "0";
                            hopProbeTimes[i][2] = "0";
                            line = "unidentified"+ " " +"(unidentified)" +"      " + hopProbeTimes[i][0] + "  ms    " + hopProbeTimes[i][1] + "  ms    " + hopProbeTimes[i][2] + "   ms   ";
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
            System.out.println("Traceroute Result is" + result);
            String[] s1 = result.trim().split("\\s+");
            ArrayList<String> list_count = new ArrayList<String>();
            ArrayList<String> list_time = new ArrayList<String>();
            ArrayList<String> list_hops = new ArrayList<String>();

            int j;
            for (j = 0; j < s1.length; j += 9) {
                list_count.add(s1[j]); }
            for (j = 1; j < s1.length; j += 9) {
                list_hops.add(s1[j]); }
            for (j = 5; j < s1.length; j += 9) {
                list_time.add(s1[j]);
            }

            System.out.println(" count is new " +list_count);
            System.out.println(" hops is new " + list_hops);
            System.out.println(" time is new " + list_time);

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
                for(int a=1;a<=list_count.size();a++ )
                {
                    JSONObject pDetail1 = new JSONObject();
                    pDetail1.put("Avg",list_time.get(a));
                    pDetail1.put("Wrst", "");
                    pDetail1.put("Jint", "");
                    pDetail1.put("Loss", "");
                    pDetail1.put("count",a);
                    pDetail1.put("host",list_hops.get(a));
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



            JSONArray jsonArray=new JSONArray();
            jsonArray.put(pDetail20);
            System.out.println("Traceroute json from lib is  " + pDetail20.toString());
//            RequestResponse.sendOldEvents(jsonArray,"site_speed_mtr");
            progressBar.setVisibility(View.GONE);
            Utils.showLongToast(context,"Trace route result sent.");
            addoutputindb(pDetail20.toString(), "site_speed_mtr", context);

        }

        @Override
        protected void onCancelled(){
            super.onCancelled();
        }
    }
}

