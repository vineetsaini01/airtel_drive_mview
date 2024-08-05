package com.newmview.wifi.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dashboard.roomdb.gagdagent.GagdAgent;
import com.functionapps.mview_sdk2.helper.GPSTracker;
import com.functionapps.mview_sdk2.main.Mview;
import com.newmview.wifi.Telephony_params;
import com.newmview.wifi.WebViewHelper;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.bean.Pinger;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.helper.DialogClass;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.other.BigFtpConnectionDownload;
import com.newmview.wifi.other.Config;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.FTPConnectionDownload;
import com.newmview.wifi.other.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class CtsTest {
    static JSONObject object_thanks;
    public static String TAG = " LogFapps";
    public static String date_time;
    public static int TIMEOUT = 30000;
    private final   int startOfRange=65;
    private final  int endOfRange=256;
    private int HTTP_STATUS_CODE = 0;
    private String reason = "NA";
    float wifiDataInMbBS = 0.0F;
    float mobileDataInMbBS = 0.0F;
    float wifiDataInMbAS = 0.0F;
    float mobileDataInMbAS = 0.0F;
    float totalDataBS = 0.0F;
    float totalDataAS = 0.0F;

    float mobileDataTxInMbBs = 0.0F;
    float mobileDataRxInMbBs = 0.0F;
    float wifiDataRxInMbBs = 0.0F;
    float wifiDataTxInMbBs = 0.0F;
    float totalDataRxBs =0.0F;
    float totalDataTxBs =0.0F;

    float mobileDataTxInMbAs = 0.0F;
    float mobileDataRxInMbAs = 0.0F;
    float wifiDataRxInMbAs = 0.0F;
    float wifiDataTxInMbAs = 0.0F;
    float totalDataRxAs =0.0F;
    float totalDataTxAs =0.0F;

    String blockedornot = null;

    public static Interfaces.Callback_result f_result;

    public static void startPingTest(String ping) {
        Utils.appendLog("ELOG_RUN_PING_PANEL: Going to run PING test from panel");
        DB_handler db_helper = new DB_handler(MviewApplication.ctx);
        db_helper.open();

        String current_status = db_helper.current_status("PING");
        String start_date = db_helper.start_date("PING");
        String end_date = db_helper.end_date("PING");
        Date currentdate = Utils.getcurrentdate();
        Date start_date_ = Utils.convertStringtoDate(start_date);
        Date end_date_ = Utils.convertStringtoDate(end_date);

        Log.i("TAG", "current_status is of agent is " + current_status + " and start date is " + start_date + " and end date is " + end_date);

        if (current_status == null || current_status.equalsIgnoreCase("completed")) {
            Log.d("TAG", "current status is completed");
            if (start_date_ != null && end_date_ != null &&
                    currentdate.after(start_date_) && currentdate.before(end_date_))
            {
//                    DashboardDatabase db = DashboardDatabase.getDatabase(MviewApplication.ctx);
//                    GagdAgentDao gagdAgentDao = db.gagdAgentDao();
                Log.d(Mview.TAG, "Going to start ping test");

                List<GagdAgent> initAgents = db_helper.getAgentsByNameAndStatus("PING","INIT");
                Log.d("Agent list", "onClick: "+initAgents);
                if (!initAgents.isEmpty()) {
                    for (GagdAgent agent : initAgents) {
                        Log.d("TAG", "ip is "+agent.getUrl());
                        JSONObject sim1servingobj= Telephony_params.getsim1servingcellinfo();

//                        Utils.appendLog("ELOG_SIM1_SERVINGCELL_INFO_PING_PANEL: is "+sim1servingobj);
                        runPing(agent.getUrl(),agent.getId(),agent.getPacketSize(),agent.getTotalPackets(),ping);

                        // Mark as INPROCESS
//                            db.databaseWriteExecutor.execute(() -> gagdAgentDao.updateStatus(agent.getId(), "INPROCESS"));
//
//
//                            // Update status based on event type
                        if (agent.getEventType().equalsIgnoreCase("event")) {
                            db_helper.updateAgentStatus(agent.getId(), "COMPLETED");
                        } else if (agent.getEventType().equalsIgnoreCase("schedule")) {
                            db_helper.updateAgentStatus(agent.getId(), "COMPLETED");
                        }

                        // Insert results into the table (if required)
                        // db.databaseWriteExecutor.execute(() -> insertTestResults(agent.getId(), pingResult, tracerouteResult));
                    }
                }else {
                    Utils.appendLog("ELOG_NO_IP_FOR_PING_PANEL : No Ip data available in DB for ping test");
                }

            } else {
                Utils.appendLog("ELOG_RUN_PING_PANEL: Status of PING TEST agent is not Active");
            }
        }

        // Reset index to start over
        db_helper.close();

    }


    private static void runPing(String ip, String id, String packetSize, String totalPackets,String type){
        BufferedReader stdInput1 = null;
        try {

            InetAddress[] inetAddress;
            if (ip.matches("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b")) {
                // If `ip` is already an IP address
                inetAddress = InetAddress.getAllByName(ip);
            } else {
                // If `ip` is a domain name, resolve its IP address
                inetAddress = InetAddress.getAllByName(new URL("http://" + ip).getHost());
            }

            List<InetAddress> ipv4Addresses = new ArrayList<>();
            for (InetAddress address : inetAddress) {
                if (address instanceof Inet4Address) {
                    ipv4Addresses.add(address);
                    Utils.appendLog( "ELOG_LIST_IP_PANEL: ipv4 addresses "+address);
                }
            }
            Utils.appendLog("ELOG_PING_IP: is "+ip);
            Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "ping -c 10 -s 1000 " + ipv4Addresses.get(0).getHostAddress()});
//            Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "ping -c "+totalPackets +" -s "+packetSize +" "+ip});
//            Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "ping -c 5 -t 64 " + ip});

            stdInput1 = new BufferedReader(new InputStreamReader(p.getInputStream()));

            JSONObject jsonObject = new JSONObject();

            String rtt_min = null, rtt_avg = null, rtt_max = null, rtt_mdev = null, rtt_dev = null, time_unit_val = null, packet_loss = null;
            String sudoScript;
            while ((sudoScript = stdInput1.readLine()) != null) {
                Utils.appendLog("ELOG_PING_COMMAND_RESULT_PANEL: "+sudoScript);
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
//
                            if (type.equalsIgnoreCase("Traceroute")) {

                                String urlForTraceroute = sudoScript.substring(sudoScript.indexOf("PING"));
                                Log.i(TAG,"values for url in first check is " + urlForTraceroute);
                                String[] s1 = urlForTraceroute.trim().split("\\s+");

                                String s2= Arrays.toString(s1);
                                Log.i(TAG,"S1" +s2);
                                Log.i(TAG," ip is "+s1[2]);
                                String s3=(s1[2]);
                                Log.i(TAG," s3 is"+s3);
                                String urlFinal=s3.replaceAll("[\\[\\](){}]","");
                                String urlTraceroute = sudoScript.substring(sudoScript.indexOf("(") + 1, sudoScript.indexOf(")") - 0);

                                Log.d(TAG, "runPing: trace url old"+urlTraceroute+ " new url "+urlFinal);
                                System.out.println("values for url in first check is " + urlForTraceroute + "" + ip);
                                TraceRouteTask2(urlFinal,
                                        10, 1, true, 3, MviewApplication.ctx, ip,id);

                            }

                } else if (sudoScript.contains("packet loss")) {
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

//                    String packetloss = sudoScript.substring(sudoScript.indexOf("packet loss") - 4, sudoScript.indexOf("packet loss") - 1);
//                    System.out.println("values for packet loss in second check" + packetloss);
//                    String[] splitvalues = packetloss.split("/");
//                    packet_loss = splitvalues[0];
//                    jsonObject.put("packet_loss", packet_loss);
//                    System.out.println("packet loss is " + packet_loss);
                }


            }



            Utils.appendLog("ELOG_RESOLVED_IP: is  "+ ipv4Addresses.get(0).getHostAddress());
            jsonObject.put("resolved_ip",ipv4Addresses.get(0).getHostAddress());
            Utils.appendLog("ELOG_PUBLIC_IP: is  "+Utils.publicIp());
            jsonObject.put("public_ip",Utils.publicIp());
            Utils.appendLog("ELOG_PRIVATE_IP: is  "+Utils.getPIP());

            jsonObject.put("private_ip",Utils.getPIP());
            jsonObject.put("host", ip);
            jsonObject.put("id",id);
            DateTimeFormatter dtf = null;
            dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            LocalDateTime now = LocalDateTime.now();
            date_time = now.format(dtf);
            jsonObject.put("dateandtime", date_time);


            if (type.equalsIgnoreCase("Ping")){
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObject);
                Log.d("TAG", "runPing: json array is "+jsonArray.toString());
                Utils.appendLog("ELOG_PING_JSON_INSERT_PANEL: is "+jsonArray);
                DB_handler db_handler = null;
                db_handler = new DB_handler(MviewApplication.ctx);
                db_handler.open();
                db_handler.insertInLoggingAgentTable("PING", "ping_report", jsonArray.toString(), date_time, "init");
                db_handler.close();
            }

        } catch (Exception e)
        {
            Utils.appendLog("ELOG_PING_EXCEPTION_PANEL: is "+e.getMessage());

            e.printStackTrace();
        }
    }




    public static void startTraceroute(String traceroute) {
        Utils.appendLog("ELOG_RUN_TRACEROUTE_PANEL: Called");
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
                JSONObject sim1servingobj= Telephony_params.getsim1servingcellinfo();

//                Utils.appendLog("ELOG_SIM1_SERVINGCELL_INFO_TRACEROUTE_PANEL: is "+sim1servingobj);

                List<GagdAgent> initAgents = db_helper.getAgentsByNameAndStatus("Traceroute","INIT");
                if (!initAgents.isEmpty()) {

                    for (GagdAgent agent : initAgents) {
                        runPing(agent.getUrl(),agent.getId(),agent.getPacketSize(),agent.getTotalPackets(),traceroute);


                        if (agent.getEventType().equalsIgnoreCase("event")) {
                            db_helper.updateAgentStatus(agent.getId(), "COMPLETED");
                        } else if (agent.getEventType().equalsIgnoreCase("schedule")) {
                            db_helper.updateAgentStatus(agent.getId(), "COMPLETED");
                        }
                    }

                }else {
                    Utils.appendLog("ELOG_NO_IP_FOR_TRACEROUTE_PANEL : No Ip data available in DB for Traceroute test");
                }

            }
        } else {
            Utils.appendLog("ELOG_TRACEROUTE_PANEL : Status of agent is not active");

        }


        // Reset index to start over
        db_helper.close();
    }

    public static void TraceRouteTask2(String address, int max_ttl, int first_ttl, boolean resolve, int probes, Context context, String ip, String id){
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
                    Log.d(TAG, "TraceRouteTask2: resolved ip is "+tracerouteToIP);
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
//            error = "ERROR";
//            cancel(true);
        }

        try{
            String[][] hopProbeTimes = new String[max_ttl][probes];
            String[] hopAddress = new String[max_ttl];
            String[] hopHostname = new String[max_ttl];
            for (int i = first_ttl - 1; i < max_ttl; i++) {
//                if(this.isCancelled()) break;
                String format, line;
                int remaining_ping = probes;
                String hopPingOutput = DialogClass.ping(InetAddress.getByName(address).getHostAddress(), 1, i + 1);
                hopAddress[i] = DialogClass.parseHopIp(hopPingOutput);

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
                            String probePingOutput = DialogClass.ping(hopAddress[i], 1, 30);
                            hopProbeTimes[i][remaining_ping - 1] = DialogClass.parseHopPingTimes(probePingOutput);
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
//                    error = "ERROR";
//                    cancel(true);
                }

                line = "\n";
                //  System.out.println(" result is"+log.append(line));
                res = String.valueOf(log.append(line));
                log.append(line);
//                publishProgress(log.toString());
                String lastAddressCheck = InetAddress.getByName(address).getHostAddress();


            }
        } catch (Exception e){
//            error = "ERROR";
//            cancel(true);
        }

//        res = result;
        // BY SWAPNIL BANSAL 26/09/2022
        System.out.println("Traceroute Result is" + res);
        String[] s1 = res.trim().split("\\s+");
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

        // Construct JSON object
        try {
            JSONArray tracerouteArray = new JSONArray();
            for (int i = 0; i < list_count.size(); i++) {
                JSONObject hopObject = new JSONObject();
                hopObject.put("count", list_count.get(i));
                hopObject.put("hop", list_hops.get(i));
                hopObject.put("time", list_time.get(i));
                tracerouteArray.put(hopObject);
            }

            JSONArray filteredTracerouteArray = new JSONArray();
            for (int i = 0; i < tracerouteArray.length(); i++) {
                JSONObject hopObject = tracerouteArray.getJSONObject(i);
                if (!hopObject.getString("hop").equals("unidentified")) {
                    filteredTracerouteArray.put(hopObject);
                }
            }


            JSONObject tracerouteJson = new JSONObject();
            tracerouteJson.put("id",id);
            Log.d(TAG, "onPostExecute: url id is "+id);
            tracerouteJson.put("public_ip",Utils.publicIp());
            Log.d(TAG, "onPostExecute: public_ip is "+Utils.publicIp());
            tracerouteJson.put("private_ip",Utils.getPIP());
            Log.d(TAG, "TraceRouteTask:ip "+ip);
            InetAddress inetAddress = null;
            if (ip.matches("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b")) {
                // If `ip` is already an IP address
                inetAddress = InetAddress.getByName(ip);
            } else {
                // If `ip` is a domain name, resolve its IP address
                inetAddress = InetAddress.getByName(new URL("http://" + ip).getHost());
            }
            Log.d(TAG, "resolved_ip: "+inetAddress.getHostAddress());
            tracerouteJson.put("resolved_ip",inetAddress.getHostAddress());
            tracerouteJson.put("traceroute", filteredTracerouteArray);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            date_time = now.format(dtf);

//                tracerouteJson.put("resolved_ip","");


            // Wrap the JSONObject inside a JSONArray
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(tracerouteJson);
            Utils.appendLog("ELOG_TRACEROUTE_JSON_INSERT_PANEL: is "+jsonArray);
            DB_handler db_handler = null;
            db_handler = new DB_handler(MviewApplication.ctx);
            db_handler.open();
            db_handler.insertInLoggingAgentTable("Traceroute", "traceroute_report", jsonArray.toString(), date_time, "init");
            db_handler.close();

            Log.d(TAG, "onPostExecute: tracerouteJson is "+jsonArray);
        } catch (JSONException e) {
            Utils.appendLog("ELOG_TRACEROUTE_EXCEPTION_PANEL: is "+e.getMessage());
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
    public static void startFTPTest(Context context) {
        Utils.appendLog("ELOG_RUN_FTP_PANEL: Going to run FTP test from panel");
        DB_handler db_helper = new DB_handler(context);
        db_helper.open();

        String current_status = db_helper.current_status("FTP");
        String start_date = db_helper.start_date("FTP");
        String end_date = db_helper.end_date("FTP");
        Date currentdate = Utils.getcurrentdate();
        Date start_date_ = Utils.convertStringtoDate(start_date);
        Date end_date_ = Utils.convertStringtoDate(end_date);

        Log.i("TAG", "current_status is of agent is " + current_status + " and start date is " + start_date + " and end date is " + end_date);

        if (current_status == null || current_status.equalsIgnoreCase("completed")) {
            Log.d("TAG", "current status is completed");
            if (start_date_ != null && end_date_ != null &&
                    currentdate.after(start_date_) && currentdate.before(end_date_))
            {
                JSONObject sim1servingobj= Telephony_params.getsim1servingcellinfo();
//                Utils.appendLog("ELOG_SIM1_SERVINGCELL_INFO_FTP_PANEL: is "+sim1servingobj);

                Log.d(Mview.TAG, "Going to start FTP test");
                ftpDownloadFile();


            } else {
                Utils.appendLog("ELOG_RUN_FTP_PANEL: Status of FTP TEST agent is not Active");
            }
        }

        // Reset index to start over
        db_helper.close();

    }

    public static void startBigFtpTest(Context context) {
        Utils.appendLog("ELOG_RUN_BIG_FTP_PANEL: Going to run BIG FTP test from panel");
        DB_handler db_helper = new DB_handler(context);
        db_helper.open();

        String current_status = db_helper.current_status("BigFtp");
        String start_date = db_helper.start_date("BigFtp");
        String end_date = db_helper.end_date("BigFtp");
        Date currentdate = Utils.getcurrentdate();
        Date start_date_ = Utils.convertStringtoDate(start_date);
        Date end_date_ = Utils.convertStringtoDate(end_date);

        Log.i("TAG", "current_status is of agent is " + current_status + " and start date is " + start_date + " and end date is " + end_date);

        if (current_status == null || current_status.equalsIgnoreCase("completed")) {
            Log.d("TAG", "current status is completed");
            if (start_date_ != null && end_date_ != null &&
                    currentdate.after(start_date_) && currentdate.before(end_date_))
            {
                JSONObject sim1servingobj= Telephony_params.getsim1servingcellinfo();
//                Utils.appendLog("ELOG_SIM1_SERVINGCELL_INFO_BIGFTP_PANEL: is "+sim1servingobj);

                Log.d(Mview.TAG, "Going to start BIG FTP test");
                bigFtpDownloadFile();


            } else {
                Utils.appendLog("ELOG_RUN_BIGFTP_PANEL: Status of BIG FTP TEST agent is not Active");
            }
        }

        // Reset index to start over
        db_helper.close();

    }

    public static void startWebTest(Context context) {
        Utils.appendLog("ELOG_RUN_WEB_PANEL: Going to run WEB test from panel");
        DB_handler db_helper = new DB_handler(context);
        db_helper.open();

        String current_status = db_helper.current_status("Web");
        String start_date = db_helper.start_date("Web");
        String end_date = db_helper.end_date("Web");
        Date currentdate = Utils.getcurrentdate();
        Date start_date_ = Utils.convertStringtoDate(start_date);
        Date end_date_ = Utils.convertStringtoDate(end_date);

        Log.i("TAG", "current_status is of agent is " + current_status + " and start date is " + start_date + " and end date is " + end_date);

        if (current_status == null || current_status.equalsIgnoreCase("completed")) {
            Log.d("TAG", "current status is completed");
            if (start_date_ != null && end_date_ != null &&
                    currentdate.after(start_date_) && currentdate.before(end_date_))
            {

                Log.d(Mview.TAG, "Going to start web test");
                JSONObject sim1servingobj= Telephony_params.getsim1servingcellinfo();

//                Utils.appendLog("ELOG_SIM1_SERVINGCELL_INFO_WEB_PANEL: is "+sim1servingobj);

                List<GagdAgent> initAgents = db_helper.getAgentsByNameAndStatus("Web","INIT");
                Log.d("Agent list", "onClick: "+initAgents);
                if (!initAgents.isEmpty()) {
                    for (GagdAgent agent : initAgents) {
                        Log.d("TAG", "ip is "+agent.getUrl());

                        runWebTest(agent.getUrl(),agent.getId());


                        if (agent.getEventType().equalsIgnoreCase("event")) {
                            db_helper.updateAgentStatus(agent.getId(), "COMPLETED");
                        } else if (agent.getEventType().equalsIgnoreCase("schedule")) {
                            db_helper.updateAgentStatus(agent.getId(), "COMPLETED");
                        }

                        // Insert results into the table (if required)
                        // db.databaseWriteExecutor.execute(() -> insertTestResults(agent.getId(), pingResult, tracerouteResult));
                    }
                }else {
                    Utils.appendLog("ELOG_NO_IP_FOR_WEB_PANEL : No Ip data available in DB for WEB test");
                }

            } else {
                Utils.appendLog("ELOG_RUN_WEB_PANEL: Status of WEB TEST agent is not Active");
            }
        }

        // Reset index to start over
        db_helper.close();

    }


    public static void runWebTest(String url, String id){
        try {

            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            // DNS Resolution Time
            long dnsStart = System.currentTimeMillis();
            JSONObject metrics =new JSONObject();
            InetAddress inetAddress=InetAddress.getByName(new URL(url).getHost());
            long dnsEnd = System.currentTimeMillis();
            metrics.put("dns_time", dnsEnd - dnsStart+"ms");
            metrics.put("resolved_ip",inetAddress.getHostAddress());
            metrics.put("host_name",inetAddress.getHostName());


            // Time taken to make the connection
            long startTime = System.currentTimeMillis();
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            long connectEndTime = System.currentTimeMillis();
            metrics.put("connection_time", connectEndTime - startTime+"");

            int responseCode = urlConnection.getResponseCode();
            Log.d("Network","Response code "+responseCode);
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            }
            long endTime = System.currentTimeMillis();
            long totalTime=endTime - startTime;
            metrics.put("total_time", totalTime+"ms");
            metrics.put("response_code",responseCode+"");
            metrics.put("id",id);
            Log.d(TAG, "onPostExecute: url id is "+id);

            metrics.put("public_ip",Utils.publicIp());
            metrics.put("private_ip",Utils.getPIP());
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            date_time = now.format(dtf);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(metrics);
            Log.d("TAG", "runWeb: json array is "+jsonArray.toString());
            Utils.appendLog("ELOG_WEB_JSON_INSERT_PANEL: is "+jsonArray);
            DB_handler db_handler = new DB_handler(MviewApplication.ctx);
            db_handler.open();
            db_handler.insertInLoggingAgentTable("Web", "web_report", jsonArray.toString(), date_time, "init");
            db_handler.close();
            Log.d("Network","time "+totalTime);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.appendLog("ELOG_WEB_EXCEPTION_PANEL: is "+e.getMessage());

        }
    }

    public void startLongWebTest(Context context) {
        Utils.appendLog("ELOG_RUN_LONG_WEB_PANEL: Going to run LONG WEB test from panel");
        DB_handler db_helper = new DB_handler(context);
        db_helper.open();

        String current_status = db_helper.current_status("LongWeb");
        String start_date = db_helper.start_date("LongWeb");
        String end_date = db_helper.end_date("LongWeb");
        Date currentdate = Utils.getcurrentdate();
        Date start_date_ = Utils.convertStringtoDate(start_date);
        Date end_date_ = Utils.convertStringtoDate(end_date);

        Log.i("TAG", "current_status is of agent is " + current_status + " and start date is " + start_date + " and end date is " + end_date);

        if (current_status == null || current_status.equalsIgnoreCase("completed")) {
            Log.d("TAG", "current status is completed");
            if (start_date_ != null && end_date_ != null &&
                    currentdate.after(start_date_) && currentdate.before(end_date_))
            {
                JSONObject sim1servingobj= Telephony_params.getsim1servingcellinfo();
//                Utils.appendLog("ELOG_SIM1_SERVINGCELL_INFO_LONGWEB_PANEL: is "+sim1servingobj);

                Log.d(Mview.TAG, "Going to start Long web test");

                runLongWebTest("www.facebook.com");


            } else {
                Utils.appendLog("ELOG_RUN_LONG_WEB_PANEL: Status of LONG WEB TEST agent is not Active");
            }
        }

        // Reset index to start over
        db_helper.close();

    }
    public void runLongWebTest(String url) {
        long startTestTime = System.currentTimeMillis();
        long endTestTime = startTestTime + 10 * 60 * 1000;
        Utils.appendLog("ELOG_LONG_WEB_TEST: Start time is "+startTestTime+" End time is "+endTestTime);// 10 minutes in milliseconds

        int loopCount = 0; // Initialize the loop counter
        InetAddress inetAddress = null;
        try {
            fetchDataUsageChartBs();
            while (System.currentTimeMillis() < endTestTime) {

                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "https://" + url;
                }

                // DNS Resolution Time
                long dnsStart = System.currentTimeMillis();
                JSONObject metrics = new JSONObject();
                inetAddress = InetAddress.getByName(new URL(url).getHost());
                long dnsEnd = System.currentTimeMillis();
                metrics.put("dns_time", dnsEnd - dnsStart + "");


                // Time taken to make the connection
                long startTime = System.currentTimeMillis();
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                long connectEndTime = System.currentTimeMillis();
                metrics.put("connection_time", connectEndTime - startTime + "");

                int responseCode = urlConnection.getResponseCode();
                Log.d("Network", "Response code " + responseCode);
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                }
                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                metrics.put("total_time", totalTime + "ms");
                metrics.put("response_code", responseCode + "");
                Log.d(TAG, "loopCount is: "+loopCount);
                // Increment the loop count
                loopCount++;


                long loopEndTime = System.currentTimeMillis();
                if (loopEndTime >= endTestTime) {
                    fetchDataUsageChartAs();
                    break;
                }
            }

            Date startdate = new Date(startTestTime);
            // Format the date using SimpleDateFormat
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String startTime = sdf.format(startdate);

            Date enddate = new Date(System.currentTimeMillis());
            // Format the date using SimpleDateFormat
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String endTime = sdf2.format(enddate);

            // Add final metrics including the final count value
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            date_time = now.format(dtf);
            JSONObject finalMetrics = new JSONObject();
            float txMobileData = (mobileDataTxInMbAs-mobileDataTxInMbBs);
            float rxMobileData = (mobileDataRxInMbAs-mobileDataRxInMbBs);
            float txWifiData = (wifiDataTxInMbAs-wifiDataTxInMbBs);
            float rxWifiData = (wifiDataRxInMbAs-wifiDataRxInMbBs);
            float txTotalData = (totalDataTxAs-totalDataTxBs);
            float rxTotalData = (totalDataRxAs-totalDataRxBs);
//            finalMetrics.put("mobiledata_usage", ( mobileDataInMbAS - mobileDataInMbBS)+"MB");
//            finalMetrics.put("wifidata_usage", ( wifiDataInMbAS - wifiDataInMbBS)+"MB");
//            finalMetrics.put("totaldata_usage", ( totalDataAS - totalDataBS)+"MB");
            finalMetrics.put("transmitted_mobiledata", txMobileData+"MB");
            finalMetrics.put("received_mobiledata", rxMobileData+"MB");

            finalMetrics.put("transmitted_wifidata", txWifiData+"MB");
            finalMetrics.put("received_wifidata", rxWifiData+"MB");

            finalMetrics.put("transmitted_totaldata", txTotalData+"MB");
            finalMetrics.put("received_totaldata", rxTotalData+"MB");
            finalMetrics.put("dns_time", (endTestTime-startTestTime));

            finalMetrics.put("resolved_ip", inetAddress.getHostAddress());
            finalMetrics.put("host_name", inetAddress.getHostName());
            finalMetrics.put("public_ip", Utils.publicIp());
            finalMetrics.put("private_ip", Utils.getPIP());
            finalMetrics.put("total_iterations", loopCount);
            finalMetrics.put("start_time", startTime);
            finalMetrics.put("end_time",endTime );
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(finalMetrics);
            Utils.appendLog("ELOG_LONG_WEB_TEST: final json is "+jsonArray);

            // Store all the metrics after the loop ends
            DB_handler db_handler = new DB_handler(MviewApplication.ctx);
            db_handler.open();
            String start_date = db_handler.start_date("LongWeb");
            db_handler.updateEndDate("LongWeb",start_date);
            db_handler.insertInLoggingAgentTable("LongWeb", "longweb_report", jsonArray.toString(), date_time, "init");
            db_handler.close();

            Log.d("Network", "Web test completed. Total iterations: " + loopCount);

        } catch (Exception e) {
            e.printStackTrace();
            Utils.appendLog("ELOG_WEB_EXCEPTION_PANEL: is " + e.getMessage());
        }
    }
    public void fetchDataUsageChartBs() {
        long currentMobileTxBytes = TrafficStats.getMobileTxBytes();
        long currentMobileRxBytes = TrafficStats.getMobileRxBytes();
        long totalTxBytes = TrafficStats.getTotalTxBytes();
        long totalRxBytes = TrafficStats.getTotalRxBytes();

        // Calculate mobile data in MB
        mobileDataTxInMbBs = currentMobileTxBytes / (1024F * 1024F);
        mobileDataRxInMbBs = currentMobileRxBytes / (1024F * 1024F);

        // Calculate Wi-Fi data in MB (assuming total minus mobile is Wi-Fi)
        wifiDataTxInMbBs = (totalTxBytes - currentMobileTxBytes) / (1024F * 1024F);
        wifiDataRxInMbBs = (totalRxBytes - currentMobileRxBytes) / (1024F * 1024F);

        // Ensure Wi-Fi data is not negative
        wifiDataTxInMbBs = Math.max(wifiDataTxInMbBs, 0F);
        wifiDataRxInMbBs = Math.max(wifiDataRxInMbBs, 0F);

        // Calculate total data
        totalDataTxBs = wifiDataTxInMbBs + mobileDataTxInMbBs;
        totalDataRxBs = wifiDataRxInMbBs + mobileDataRxInMbBs;
        Log.d("total data in mb ", String.valueOf(totalDataTxBs + totalDataRxBs));
    }

    // For fetchDataUsageChartAs
    public void fetchDataUsageChartAs() {
        long currentMobileTxBytes = TrafficStats.getMobileTxBytes();
        long currentMobileRxBytes = TrafficStats.getMobileRxBytes();
        long totalTxBytes = TrafficStats.getTotalTxBytes();
        long totalRxBytes = TrafficStats.getTotalRxBytes();

        // Calculate mobile data in MB
        mobileDataTxInMbAs = currentMobileTxBytes / (1024F * 1024F);
        mobileDataRxInMbAs = currentMobileRxBytes / (1024F * 1024F);

        // Calculate Wi-Fi data in MB (assuming total minus mobile is Wi-Fi)
        wifiDataTxInMbAs = (totalTxBytes - currentMobileTxBytes) / (1024F * 1024F);
        wifiDataRxInMbAs = (totalRxBytes - currentMobileRxBytes) / (1024F * 1024F);

        // Ensure Wi-Fi data is not negative
        wifiDataTxInMbAs = Math.max(wifiDataTxInMbAs, 0F);
        wifiDataRxInMbAs = Math.max(wifiDataRxInMbAs, 0F);

        // Calculate total data
        totalDataTxAs = wifiDataTxInMbAs + mobileDataTxInMbAs;
        totalDataRxAs = wifiDataRxInMbAs + mobileDataRxInMbAs;
        Log.d("total data in mb ", String.valueOf(totalDataTxAs + totalDataRxAs));
    }




    public static void startVideoTest() {
        Utils.appendLog("ELOG_RUN_VIDEO_PANEL: Called");
        DB_handler db_helper = new DB_handler(MviewApplication.ctx);
        db_helper.open();

        String current_status = db_helper.current_status("Video");
        String start_date = db_helper.start_date("Video");
        String end_date = db_helper.end_date("Video");
        Date currentdate = Utils.getcurrentdate();
        Date start_date_ = Utils.convertStringtoDate(start_date);
        Date end_date_ = Utils.convertStringtoDate(end_date);

        Log.i("TAG", "current_status is of agent is " + current_status + " and start date is " + start_date + " and end date is " + end_date);

        if (current_status == null || current_status.equalsIgnoreCase("completed")) {
            Log.d("TAG", "current status is completed");
            if (start_date_ != null && end_date_ != null &&
                    currentdate.after(start_date_) && currentdate.before(end_date_))
            {
                JSONObject sim1servingobj= Telephony_params.getsim1servingcellinfo();
                Utils.appendLog("ELOG_SIM1_SERVINGCELL_INFO_VIDEO_PANEL: is "+sim1servingobj);

                Log.d(Mview.TAG, "Going to start Video test");
                getVideoKPI();

            }
            else {
                Utils.appendLog("ELOG_RUN_VIDEO_PANEL: Status of VIDEO TEST agent is not Active");
            }
        }

        // Reset index to start over
        db_helper.close();

    }


    private static void getVideoKPI() {
        object_thanks = new JSONObject();
        Utils.appendLog("ELOG_RUN_VIDEO_PANEL: Going to Start Video test");

        try {

        // Using Handler to post the WebView operations to the main thread
        new Handler(Looper.getMainLooper()).post(() -> {
            new WebViewHelper(MviewApplication.ctx, getLatency("www.youtube.com") + " ms", getpacketLoss("www.youtube.com"))
                    .loadUrl("file:///android_asset/youtube.html", new WebViewHelper.JsonResultInterface() {
                        @Override
                        public void sendJsonResult(JSONObject jsonObject) {
                            Log.d("TAG", "video KPI LIST is " + jsonObject);
                            Utils.appendLog("ELOG_RUN_VIDEO_PANEL: Send Json Result is: " + jsonObject);

                            try {
                                object_thanks.put("video_test", jsonObject);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (f_result != null) {
                                f_result.onSuccess(object_thanks);
                            }

                            JSONArray videoArray = new JSONArray();
                            videoArray.put(object_thanks);

                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            LocalDateTime now = LocalDateTime.now();
                            date_time = now.format(dtf);

                            // Ensure database operations are also performed on a suitable thread
                            new Thread(() -> {
                                DB_handler db_helper = new DB_handler(MviewApplication.ctx);
                                db_helper.open();
                                Utils.appendLog("ELOG_VIDEO_JSON_INSERT_PANEL: is " + videoArray);
                                db_helper.insertInLoggingAgentTable("Video", "video_report", videoArray.toString(), date_time, "init");
                                db_helper.close();
                                Log.d("TAG", "Data inserted for CTS AGENT");
                            }).start();
                        }
                    });
        });

        } catch (Exception e){
            Utils.appendLog("ELOG_VIDEO_EXCEPTION_PANEL: is "+e.getMessage());

        }
    }


    public void startUrlBlock() {
        Utils.appendLog("ELOG_RUN_URLBLOCK_PANEL: Called");
        DB_handler db_helper = new DB_handler(MviewApplication.ctx);
        db_helper.open();

        String current_status = db_helper.current_status("UrlBlock");
        String start_date = db_helper.start_date("UrlBlock");
        String end_date = db_helper.end_date("UrlBlock");
        Date currentdate = Utils.getcurrentdate();
        Date start_date_ = Utils.convertStringtoDate(start_date);
        Date end_date_ = Utils.convertStringtoDate(end_date);

        Log.i("TAG", "current_status is of agent is " + current_status + " and start date is " + start_date + " and end date is " + end_date);

        if (current_status == null || current_status.equalsIgnoreCase("completed")) {
            Log.d("TAG", "current status is completed");
            if (start_date_ != null && end_date_ != null &&
                    currentdate.after(start_date_) && currentdate.before(end_date_))
            {
                JSONObject sim1servingobj= Telephony_params.getsim1servingcellinfo();

                Utils.appendLog("ELOG_SIM1_SERVINGCELL_INFO_URLBLOCK_PANEL: is "+sim1servingobj);

                List<GagdAgent> initAgents = db_helper.getAgentsByNameAndStatus("UrlBlock","INIT");
                Log.d("Agent list", "onClick: "+initAgents);
                if (!initAgents.isEmpty()) {
                    for (GagdAgent agent : initAgents) {
                        Log.d("TAG", "ip is "+agent.getUrl());

                        Log.d(Mview.TAG, "Going to start Url block test");
                        startUrlBlockAgent(agent.getUrl(),agent.getId());

                        if (agent.getEventType().equalsIgnoreCase("event")) {
                            db_helper.updateAgentStatus(agent.getId(), "COMPLETED");
                        } else if (agent.getEventType().equalsIgnoreCase("schedule")) {
                            db_helper.updateAgentStatus(agent.getId(), "COMPLETED");
                        }

                        // Insert results into the table (if required)
                        // db.databaseWriteExecutor.execute(() -> insertTestResults(agent.getId(), pingResult, tracerouteResult));
                    }
                }else {
                    Utils.appendLog("ELOG_NO_IP_FOR_URLBLOCK_PANEL : No Ip data available in DB for URLBLOCK test");
                }



            }
            else {
                Utils.appendLog("ELOG_RUN_URLBLOCK_PANEL: Status of URL BLOCK TEST agent is not Active");
            }
        }

        // Reset index to start over
        db_helper.close();

    }


    public void startUrlBlockAgent(String url, String id){
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        Handler mainHandler = new Handler(Looper.getMainLooper());
        String pageTitle = null;
        String pageSource = null;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();
        try {

            Response response = client.newCall(request).execute();
            int responseCode = response.code();

            HTTP_STATUS_CODE = responseCode;

            String responseBody = response.body() != null ? response.body().string() : null;
            Document document = Jsoup.parse(responseBody);
            pageTitle = document.title();
            pageSource = document.html();

            Utils.appendLog("ELOG_URL_BLOCK: Page title: " + pageTitle);
            Utils.appendLog( "ELOG_URL_BLOCK: Page source: " + pageSource);
            Utils.appendLog( "ELOG_URL_BLOCK: Response code: " + responseCode);

            if (pageTitle != null && pageSource != null) {
                assignBlockedStatusAsPerResult(pageTitle, pageSource, responseCode,url,id);
            }
            Log.d("blockedornot", "blockedornot var is : " + blockedornot);

            String finalUrl = url;
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    WebView webView = new WebView(MviewApplication.ctx);
                    webView.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            // captureScreenshot();
                        }
                    });
                    webView.loadUrl(finalUrl);
                }
            });


        } catch (Exception e) {
            Utils.appendLog("ELOG_URL_BLOCK_EXCEPTION: is "+e.getStackTrace());
            addSomeStatementsWhenExceptionIsCaught(e,pageTitle,pageSource,url,id);
            e.printStackTrace();

            Log.e("HTTP_ERROR", e.getMessage());
        }
    }
    private void addSomeStatementsWhenExceptionIsCaught(Exception e, String pageTitle, String pageSource, String url, String id) {
        // TODO Auto-generated method stub
//        exceptionOccured=true;
        //System.out.println( getDateTime()+" : setting exception occured from 1 "+exceptionOccured);
//        logger.error("Exception",e);
        //logger.debug(" : Exception occured in  html driver is  "+e.getMessage());

        //insertEventsInTable("Scan_Url","Html_Driver",e.toString()+" for id "+idofurl,"init");
        JSONObject urlBlockedJson = new JSONObject();
        try {


            if (e != null) {

                if (e.toString().toLowerCase().contains("connect timed out") || e.toString().toLowerCase().contains("failed to connect")) {


                    blockedornot = "1";
                    urlBlockedJson.put("blocked", "success");
                    Utils.appendLog("ELOG_RUN_URLBLOCK_PANEL: Url is blocked because of connection timeout "+e.getMessage());

                    //logger.debug(" : yes url is blocked... from catch block when connection is timed out");

//                timedOut=true;
                } else {
                    if (HTTP_STATUS_CODE != 200) {
                        blockedornot = "1";
                        urlBlockedJson.put("blocked", "success");
                        Utils.appendLog("ELOG_RUN_URLBLOCK_PANEL: Url is blocked because status code is not = 200 and exception is " + e.getMessage());

                        //	logger.debug(" : yes url is blocked... from catch block else condition");
                        //statusCode = "200";
                    }else {
                        Utils.appendLog("ELOG_URL_BLOCK: in else block HTTP_CODE is 200");
                    }

                }
                InetAddress inetAddress=InetAddress.getByName(new URL(url).getHost());

                urlBlockedJson.put("url",url);
                urlBlockedJson.put("id",id);
                urlBlockedJson.put("responsecode",HTTP_STATUS_CODE);
                urlBlockedJson.put("resolved_ip",inetAddress.getHostAddress());
                urlBlockedJson.put("private_ip",Utils.getPIP());
                urlBlockedJson.put("public_ip",Utils.publicIp());

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(urlBlockedJson);
                Utils.appendLog("ELOG_URL_BLOCK_JSON_INSERT_PANEL: is "+jsonArray);
                DB_handler db_handler = new DB_handler(MviewApplication.ctx);
                db_handler.open();
                db_handler.insertInLoggingAgentTable("UrlBlock", "urlblock_report", jsonArray.toString(), date_time, "init");
                db_handler.close();
            }else {
                Utils.appendLog("ELOG_URL_BLOCK: e value is null ");
            }
        }catch (Exception c){
            Utils.appendLog("ELOG_URL_BLOCK_EXCEPTION: is "+e.getStackTrace());

            c.printStackTrace();
        }


    }

    private void assignBlockedStatusAsPerResult(String pageTitle, String pageTextMessage, int STATUS_CODE, String url, String id) {
        try {

            JSONObject urlBlockedJson = new JSONObject();
            if (pageTextMessage.toLowerCase().contains("blocked")
                    || pageTitle.toLowerCase().startsWith("403 Forbidden".toLowerCase())
                    || pageTitle.toLowerCase().contains("403 Forbidden".toLowerCase())) {
                Utils.appendLog("ELOG_RUN_URLBLOCK_PANEL: found blocked in pageTextMessage: "+ pageTextMessage);
//blocked-blockedornot
                if (pageTextMessage.length() < endOfRange) {
                    blockedornot = "1";

                    urlBlockedJson.put("reason",reason);
                    urlBlockedJson.put("blocked", "success");
                    Utils.appendLog("ELOG_RUN_URLBLOCK_PANEL: Url is blocked " + pageTextMessage.length());
                } else {
                    Utils.appendLog("ELOG_RUN_URLBLOCK: in else block of length check :length value is: "+pageTextMessage.length());
                    urlBlockedJson.put("blocked", "failed");
                    // logger.debug(" : Length of blocked String does not lie between the defined range of "+startOfRange +"-"+endOfRange);
                }

            } else if (STATUS_CODE != 200) {
                blockedornot = "1";
                urlBlockedJson.put("blocked", "success");
                Utils.appendLog("ELOG_RUN_URLBLOCK_PANEL: Url is blocked because status code is not = 200");

                // logger.debug(" : yes url is blocked... from status code check ");

            } else {
                Utils.appendLog("ELOG_RUN_URLBLOCK_PANEL: in else block STATUS_CODE = 200");

                urlBlockedJson.put("blocked", "failed");

                // logger.debug(" : Doesnot match any check...");
            }
            InetAddress inetAddress=InetAddress.getByName(new URL(url).getHost());

            //url,responsecode,resolvedip,privateip,publicip,
            urlBlockedJson.put("url",url);
            urlBlockedJson.put("id",id);
            urlBlockedJson.put("responsecode",STATUS_CODE);
            urlBlockedJson.put("resolved_ip",inetAddress.getHostAddress());
            urlBlockedJson.put("private_ip",Utils.getPIP());
            urlBlockedJson.put("public_ip",Utils.publicIp());
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(urlBlockedJson);
            Utils.appendLog("ELOG_URL_BLOCK_JSON_INSERT_PANEL: is "+jsonArray);
            DB_handler db_handler = new DB_handler(MviewApplication.ctx);
            db_handler.open();
            db_handler.insertInLoggingAgentTable("UrlBlock", "urlblock_report", jsonArray.toString(), date_time, "init");
            db_handler.close();

        }catch (Exception e){
            Utils.appendLog("ELOG_RUN_URLBLOCK_PANEL_EXCEPTION: is "+e.getStackTrace());
            e.printStackTrace();
        }
    }

    public static void startSpeedTest(){
        Utils.appendLog("ELOG_RUN_SPEED_PANEL : Going to run speed test from Panel ");

        DB_handler db_helper = new DB_handler(MviewApplication.ctx);
        db_helper.open();

        String current_status = db_helper.current_status("Speed");
        String start_date = db_helper.start_date("Speed");
        String end_date = db_helper.end_date("Speed");
        Date currentdate = Utils.getcurrentdate();
        Date start_date_ = Utils.convertStringtoDate(start_date);
        Date end_date_ = Utils.convertStringtoDate(end_date);

        Log.i("TAG", "current_status is of agent is " + current_status + " and start date is " + start_date + " and end date is " + end_date);

        if (current_status == null || current_status.equalsIgnoreCase("completed")) {
            Log.d("TAG", "current status is completed");
            if (start_date_ != null && end_date_ != null &&
                    currentdate.after(start_date_) && currentdate.before(end_date_))
            {
                try {
                    JSONObject sim1servingobj= Telephony_params.getsim1servingcellinfo();

                    Utils.appendLog("ELOG_SIM1_SERVINGCELL_INFO_SPEED_PANEL: is "+sim1servingobj);


                    JSONObject speedTest = new JSONObject();
                    Log.d(Mview.TAG, "Going to start Speed test");
                    JSONObject downobj = getFinaldownload();
                    Log.d(TAG, "Download  speed  obj is: " + downobj);

//                    JSONObject upobj = getFinalupload();
//                    Log.d(TAG, "Upload  speed  obj is: " + upobj);

                    speedTest.put("speed_result", downobj);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    date_time = now.format(dtf);
//                    speedTest.put("upload_speed", upobj);
                    JSONArray speedArray = new JSONArray();
                    speedArray.put(speedTest);
                    Utils.appendLog("ELOG_SPEED_JSON_INSERT_PANEL : "+speedArray);

                    db_helper.insertInLoggingAgentTable("Speed","speed_report",speedArray.toString(),date_time,"init");


                }catch (Exception e) {
                    Utils.appendLog("ELOG_SPEED_EXCEPTION_PANEL: is "+e.getMessage());

                    e.printStackTrace();
                }


            }else {
                Utils.appendLog("ELOG_RUN_SPEED_PANEL: Status of SPEED TEST agent is not Active");
            }
        }

        // Reset index to start over
        db_helper.close();


    }


    public static JSONObject getFinaldownload() {
        JSONObject jsonObject = new JSONObject();
        CountDownLatch latch = new CountDownLatch(1);

        try {
            float maxvalue = 0F;
            float sizeoffile = 0F;
            final float[] downloadSpeed = {0F};
            final float[] uploadSpeed = {0F};

            Log.d(TAG, "getFinaldownload: called");

            newSpeedTest256kbNewsignalStrengthlogicbuffer(new Interfaces.SpeedTestResult(){

                @Override
                public void onSpeedResultObtained(float download, float upload) {
                    System.out.println("FappsSpeedTest Final upload value is " + upload);
                    System.out.println("FappsSpeedTest Final download value is " + download);
                    downloadSpeed[0] = download;
                    uploadSpeed[0] = upload;
                    latch.countDown();
                    Log.d(TAG, "onSpeedResultObtained: "+downloadSpeed[0]);

                }

                @Override
                public void parseSpeedResult(Pinger response) {

                }

            });
            latch.await();

            jsonObject.put("dlDataSize", (float) Math.round(sizeoffile) + " Mb");
            jsonObject.put("url", "https://125.20.114.234/mtantu_server/uploads/Delhi.json");
            jsonObject.put("dlMaxThroughput", maxvalue + " Mbps");
            jsonObject.put("latency", getLatency("180.179.214.56") + " ms");
            jsonObject.put("dlThroughput", downloadSpeed[0]+ " Mbps");
            jsonObject.put("ulThroughput", uploadSpeed[0] + " Mbps");

            jsonObject.put("resolved_ip","180.179.214.56");
            jsonObject.put("private_ip",Utils.getPIP());
            jsonObject.put("public_ip",Utils.publicIp());
//                jsonObject.put("latency", getLatency("125.20.114.234") + " ms");


        } catch(Exception e) {
            Utils.appendLog(TAG+" Exception occured while conducting download speed test "+e.getMessage());
            e.printStackTrace();
            e.printStackTrace();
        }


        return jsonObject;
    }

    public static Void  newSpeedTest256kbNewsignalStrengthlogicbuffer(Interfaces.SpeedTestResult speedResult) {
        HashMap<String, Float> hmap = new HashMap<>();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    // change port no 4020 to 30030
                    int serverPort = 4020;
                    InetAddress host = null;
                    float ultime = 0, dltime = 0, ulspeed = 0, dlspeed = 0;
                    long T1 = 0, T2 = 0, T3 = 0, T4 = 0, T3New = 0, T2New = 0;
                    Socket socket = null;
                    PrintWriter toServer = null;
                    BufferedReader fromServer = null;
                    InputStream is = null;
                    try {

                        host = InetAddress.getByName("180.179.214.56");
                        Log.i(TAG, "Connecting to server on port " + serverPort);

                        StringBuilder sb = new StringBuilder();

                        @SuppressLint("WifiManagerLeak")
                        WifiManager wm = (WifiManager) MviewApplication.ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wifiInfo = wm.getConnectionInfo();

                        double freqBand = wifiInfo.getFrequency();
                        if (freqBand == 5.0) {
                            Log.i(TAG, "freqBand entering this " + freqBand);
                            is = MviewApplication.ctx.getAssets().open("1mbNew.txt");
                        } else {
                            Log.i(TAG, "freqBand" + freqBand);
                            is = MviewApplication.ctx.getAssets().open("256kb.txt");
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
                        Utils.appendLog("ELOG_RUN_SPEED_PANEL: Going to make connection with Server");

                        socket = new Socket(host, serverPort);
//                        socket.connect(new InetSocketAddress(host,serverPort),TIMEOUT);

                        toServer = new PrintWriter(socket.getOutputStream(), true);
                        Log.i(TAG, "T1 TIME IS :" + T1);
                        T1 = System.currentTimeMillis();//taking time entry just when we are about to send the data to server
                        toServer.println("Hello from Client " + T1 + sb.toString() + socket.getLocalSocketAddress());//Sending data to server
                        toServer.flush();
                        socket.setSoTimeout(TIMEOUT);
                        Utils.appendLog("ELOG_RUN_SPEED_PANEL: Data sent to server");

                        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        String line = null;
                        line = fromServer.readLine();
                        T4 = System.currentTimeMillis();
                        Log.i(TAG, " Receiving from server :" + line);
                        // this will be t4 time just when have read the response from server

                        T3 = Long.parseLong(line.substring(37, 50));
                        Log.i(TAG, "T3 TIME IS :" + T3);

                        T3New = T3;
                        Log.i(TAG, "T3 TIME ADDED IS :" + T3New);
                        T2 = Long.parseLong(line.substring(51, 64));
                        Log.i(TAG, "T2 TIME IS :" + T2);
                        T2New = T2;
                        Log.i(TAG, "T2 ADDED TIME IS :" + T2New);

                        Log.i(TAG, " T4 TIME IS:" + T4);

//                        toServer.close();
//                        fromServer.close();
//                        socket.close();

                    } catch (Exception e) {
                        Utils.appendLog("Exception in reading data from server" + e.getMessage());

                        e.printStackTrace();
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

                    @SuppressLint("WifiManagerLeak") WifiManager wm = (WifiManager) MviewApplication.ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wm.getConnectionInfo();
                    int freq = wifiInfo.getFrequency();
                    double freqBand = 0;
                    if (freq >= 2400 && freq <= 2495) {
                        freqBand = 2.4;
                    } else if ((freq >= 5150 && freq <= 5850) || (freq > 5850 && freq <= 7100)) {
                        freqBand = 5;
                    }

                    Log.d(TAG, "freqband value: "+freqBand);

                    int signalStrength = wifiInfo.getRssi();
                    Log.i(TAG, " SignalStrength is" + signalStrength);


                    // for wifi 2.4 hertz
                    if (freqBand == 2.4 && signalStrength < -60) {
                        Log.d(TAG, "t4t1 time < 60 "+t4t1seconds);
                        ultime = (float) (t4t1seconds * 0.55);
                        dltime = (float) (t4t1seconds * 0.45);
                    } else if (freqBand == 2.4 && signalStrength > -60) {
                        Log.d(TAG, "t4t1 time > -60 "+t4t1seconds);
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

                    Log.d(TAG, "Download speed: " + dlspeed + " Mbps");
                    Log.d(TAG, "Upload speed: " + ulspeed + " Mbps");
                    hmap.put("ulspeed",ulspeed);
                    hmap.put("dlspeed",dlspeed);
                    hmap.put("band", (float) freqBand);
                    if (speedResult != null) {

                        speedResult.onSpeedResultObtained(dlspeed,ulspeed);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        executorService.shutdown();


        return null;
    }



    private static String getLatency(String ip) {
        String latency = "NA";

        try {
            // JSONObject jsonObject=new JSONObject();
            String linenew;
            Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", "ping -c 1 -w 2  -q  " + ip});
            if (process != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((linenew = bufferedReader.readLine()) != null) {
                    Log.d("TAG", "Latency ping output is " + linenew);
                    if (linenew.contains("min/avg")) {
                        String sublin = linenew.substring(linenew.indexOf("="));
                        sublin = sublin.trim();
                        String[] split = sublin.split("/");
                        if (split[1].length() > 0) {
                            latency = split[1];
                            Log.d("TAG", "Latency is " + latency);
                        }
                    }

                }
            }
        } catch (Exception var19) {
            Log.d("TAG", " exception is" + var19);

        }
        return latency;
    }

    private static String getpacketLoss(String ip) {
        String packetlosss = "NA";
        try {
            // JSONObject jsonObject=new JSONObject();
            String linenew;
            Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", "ping -c 5  -w 2  -t 64 " + ip});
            StringBuffer output = new StringBuffer();
            if (process != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((linenew = bufferedReader.readLine()) != null) {

                    Log.d("TAG", "Ping line is " + linenew);
                    output.append(linenew);

                }
                bufferedReader.close();
            }


            Log.d("TAG", "Ping final output is " + output);
            if (output != null) {
                String finaloutput = output.toString();
                finaloutput = finaloutput.substring(finaloutput.indexOf("ping statistics ---") + 19, finaloutput.length());
                Log.d("TAG", "ping remaining " + finaloutput);
                String[] split = finaloutput.split(",");
                Log.d("TAG", "Remaining for ping for packet loss " + finaloutput);
                if (split[2].length() > 0) {
                    split[2] = split[2].trim();
                    packetlosss = split[2].substring(0, split[2].indexOf(" "));
                    Log.d("TAG", "packet loss is " + packetlosss);
                }

            }

        } catch (Exception var19) {
            Log.d("TAG", " exception is" + var19);

        }
        return packetlosss;
    }



    public static void ftpDownloadFile() {
        try {
            // For download
            FTPConnectionDownload sftpConnectionDownload = new FTPConnectionDownload();

            // Get the path to the downloads directory
            String path = MviewApplication.ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

            // Create the directory if it doesn't exist
            File file = new File(path, "mview");
            if (!file.exists()) {
                file.mkdirs();
            }

            // Define the file name and final path
            String fname = "Delhi.json";
            String finalPath = path + "/mview/" + fname;

            // Create the FileOutputStream
            try (FileOutputStream fos = new FileOutputStream(new File(finalPath))) {
                sftpConnectionDownload.downloadTask(MviewApplication.ctx, fos, finalPath);
            }

        } catch (Exception e) {
            Utils.appendLog("ELOG_FTP_EXCEPTION_PANEL: is " + e.getMessage());
        }
    }

    public static void bigFtpDownloadFile() {
        try {
            // For download
            BigFtpConnectionDownload sftpConnectionDownload = new BigFtpConnectionDownload();

            // Get the path to the downloads directory
            String path = MviewApplication.ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

            // Create the directory if it doesn't exist
            File file = new File(path, "mview");
            if (!file.exists()) {
                file.mkdirs();
            }

            // Define the file name and final path
            String fname = "Delhi.json";
            String finalPath = path + "/mview/" + fname;

            // Create the FileOutputStream
            try (FileOutputStream fos = new FileOutputStream(new File(finalPath))) {
                sftpConnectionDownload.downloadTask(MviewApplication.ctx, fos, finalPath);
            }

        } catch (Exception e) {
            Utils.appendLog("ELOG_BIG_FTP_EXCEPTION_PANEL: is " + e.getMessage());
        }
    }


}