package com.newmview.wifi.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.dashboard.roomdb.DashboardDatabase;
import com.dashboard.roomdb.gagdagent.GagdAgent;
import com.dashboard.roomdb.gagdagent.GagdAgentDao;
import com.mview.airtel.R;
import com.newmview.wifi.adapter.TraceRouteTableViewAdapter;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.bean.Cell;
import com.newmview.wifi.bean.ColumnHeader;
import com.newmview.wifi.bean.RowHeader;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.helper.MyTableViewListener;
import com.newmview.wifi.helper.RequestResponse;
import com.newmview.wifi.other.Config;
import com.newmview.wifi.other.Utils;
import com.evrencoskun.tableview.TableView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static android.view.View.VISIBLE;



public class TracerouteNewFragment extends Fragment {
    private View rootView;
    private TableView tableView;
    private String address;
    private int max_ttl;
    private int first_ttl;
    private boolean resolve = true;
    private int probes = 3;
    boolean isRunning = false;
    TraceRouteTask traceroute = null;
    private String args;
    ArrayList<String> list_count = new ArrayList<String>();
    ArrayList<String> list_time = new ArrayList<String>();
    ArrayList<String> list_hops = new ArrayList<String>();
    private TraceRouteTableViewAdapter adapter;
    String urlFinal;
    private static final String TAG = "TraceRouteNewFragment";
    LinearLayout a;
    String ip;
    AutoCompleteTextView testEtnew;
    List<String> UniqueNumbers1;
    private ProgressDialog progressDialog;
    ProgressBar someProgressBar;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tracerout2, container, false);
        TextView tracerouteResultText = rootView.findViewById(R.id.tracerouteResultText);
        tracerouteResultText.setMovementMethod(new ScrollingMovementMethod());
        testEtnew=rootView.findViewById(R.id.testEt1);
        Button runPingButton = (Button) rootView.findViewById(R.id.runTracerouteButton);
        runPingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                runTracerouteOnClick();

            }
        });

        return rootView;
    }

    public void onResume() {
        super.onResume();
        testEtnew.setThreshold(1);// Always call the superclass method first
        DB_handler db_handler=new DB_handler(MviewApplication.ctx);
        db_handler.open();
        //  ArrayList<HashMap<String, String>> ipList= (ArrayList<HashMap<String, String>>) db_handler.selectIPdetails();
        ArrayList<String> ipList=db_handler.selectIPdetails1();
        Log.i(TAG," iplist is is is "+ipList+"time is"+ Config.getDateTime());
        db_handler.close();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            UniqueNumbers1 = ipList.stream().distinct().collect(Collectors.toList());
        }
        Log.i(TAG," UniqueNumbers is"+UniqueNumbers1+"tims is"+Config.getDateTime());
        testEtnew.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,UniqueNumbers1));
    }

    public void runTracerouteOnClick(){
        //int addressTracerouteTextId = getResources().getIdentifier("addressTracerouteText", "id", getActivity().getPackageName());
        int tracerouteResultTextId = getResources().getIdentifier("tracerouteResultText", "id", getActivity().getPackageName());
        //EditText addressTracerouteText = (EditText) rootView.findViewById(addressTracerouteTextId);
        TextView tracerouteResultText = (TextView) rootView.findViewById(tracerouteResultTextId);
        someProgressBar = (ProgressBar) rootView.findViewById(R.id.someProgressBar);
       someProgressBar.setVisibility(VISIBLE);
      //  address = addressTracerouteText.getText().toString();
        tableView = rootView.findViewById(R.id.content_containerNew);
         a=rootView.findViewById(R.id.pingLL);
        ip=testEtnew.getText().toString();Log.i(TAG," start of the test is "+ Config.getDateTime());
        DB_handler db_handler=new DB_handler(MviewApplication.ctx);
        db_handler.open();
        Log.i(TAG,"insertion is called"+ip);
        db_handler.insertIPDetails(ip);
        Log.i(TAG,"getting from database");
        db_handler.close();
        testEtnew.setThreshold(1);// Always call the superclass method first
      // DB_handler db_handler=new DB_handler(MviewApplication.ctx);
        db_handler.open();
        //  ArrayList<HashMap<String, String>> ipList= (ArrayList<HashMap<String, String>>) db_handler.selectIPdetails();
        ArrayList<String> ipList=db_handler.selectIPdetails1();
        Log.i(TAG," iplist is is is "+ipList+"time is"+Config.getDateTime());
        db_handler.close();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            UniqueNumbers1 = ipList.stream().distinct().collect(Collectors.toList());
        }
        Log.i(TAG," UniqueNumbers is"+UniqueNumbers1+"tims is"+Config.getDateTime());
        testEtnew.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,UniqueNumbers1));
        if(isRunning) {
            traceroute.cancel(true);
            isRunning = false;
            while (true) {
                if (traceroute.isCancelled()) {
                    break;
                }
            }
        }
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "ping -c 5 -t 64 " + ip});
            BufferedReader stdInput1 = new BufferedReader(new InputStreamReader(p.getInputStream()));
            System.out.println("std  input is  " + stdInput1);
            JSONObject jsonObject=new JSONObject();
            String rtt_min;
            String sudoScript;
            while ((sudoScript = stdInput1.readLine()) != null) {
               // Log.i(TAG,"sudoscript is "+sudoScript);
                if (sudoScript.contains("PING")) {
                    {
                       // Log.i(TAG,"entering trace route box "+Config.getDateTime());
                        String urlForTraceroute = sudoScript.substring(sudoScript.indexOf("PING"));
                      //  Log.i(TAG,"values for url in first check is " + urlForTraceroute);
                        String[] s1 = urlForTraceroute.trim().split("\\s+");
                        String s2= Arrays.toString(s1);
                       // Log.i(TAG,"S1" +s2);
                       // Log.i(TAG," ip is "+s1[2]);
                        String s3=(s1[2]);
                       // Log.i(TAG," s3 is"+s3);
                        urlFinal=s3.replaceAll("[\\[\\](){}]","");
                        //Log.i(TAG,"accountlist is "+urlFinal+" time is "+Config.getDateTime());
                        //Log.i(TAG," time is  at tart"+ Config.getDateTime());
                        tableView.setVisibility(View.GONE);
                        a.setVisibility(VISIBLE);
                    }


                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        traceroute = new TraceRouteTask(urlFinal,15,1, resolve, probes, tracerouteResultText,getContext());
        traceroute.execute();
        isRunning = true;
    }

    public class TraceRouteTask extends AsyncTask<Void, String, String> {
        String address;
        int max_ttl;
        int first_ttl;
        boolean resolve;
        int probes;
        TextView view;
        String error;

        private final Context context;
        private String res;
        public TraceRouteTask(String address, int max_ttl, int first_ttl, boolean resolve,int probes,TextView view,Context context){
            this.address = address;
            this.max_ttl = max_ttl;
            this.first_ttl = first_ttl;
            this.resolve = resolve;
            this.probes = probes;
            this.view = view;
            this.context=context;
           // view.setText("Traceroute -f " + first_ttl + " -m " + max_ttl + " -q " + probes + " " + address);
        }

        @Override
        protected  String  doInBackground(Void... params){
            StringBuilder log = new StringBuilder();
            String res = " ";
            System.out.println(" entering a ");
           // someProgressBar.setVisibility(View.VISIBLE);
            try {
                String format = "traceroute to %s (%s), %d hops max, 60 byte packets\n";
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
                    line = String.format(format, tracerouteToHostname, tracerouteToIP, max_ttl);
                } else {
                    line = String.format(format, address, address, max_ttl);
                }

               // log.append("Traceroute -f " + first_ttl + " -m " + max_ttl + " -q " + probes + " " + address + "\n");
              //  log.append(line);
            }
            catch(Exception e) {
                error = "ERROR PLEASE ENTER CORRECT URL";
                cancel(true);
            }
            try{
                System.out.println("entering try block ");
                String[][] hopProbeTimes = new String[max_ttl][probes];
                String[] hopAddress = new String[max_ttl];
                String[] hopHostname = new String[max_ttl];
                //someProgressBar.setVisibility(View.VISIBLE);

                for (int i = first_ttl - 1; i < max_ttl; i++) {
                    if(this.isCancelled()) break;
                    String format, line;
                    int remaining_ping = probes;
                    Log.d(TAG, "ip in address "+address);
                    String hopPingOutput = ping(InetAddress.getByName(address).getHostAddress(), 1, i + 1,  null);
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
                                String probePingOutput = ping(hopAddress[i], 1, 30,  null);
                                hopProbeTimes[i][remaining_ping - 1] = parseHopPingTimes(probePingOutput);
                                if (hopProbeTimes[i][remaining_ping - 1].equals("0 ms")) {
                                    line = "       " + hopProbeTimes[i][remaining_ping - 1] + "       ";
                                } else {
                                    line = "  " + hopProbeTimes[i][remaining_ping - 1] + " ms";
                                }
//
                                System.out.println(" result is new "+log.append(line));
                                // log.append(line);
                                remaining_ping--;
                            }
                        } else {
                            //if probe failed display * in output
                            hopProbeTimes[i][0] = "0";
                            hopProbeTimes[i][1] = "0";
                            hopProbeTimes[i][2] = "0";
                            line = "unidentified"+ " " +"(unidentified)" +"      " + hopProbeTimes[i][0] + "  ms    " + hopProbeTimes[i][1] + "  ms    " + hopProbeTimes[i][2] + "   ms   ";
                            //  line = "      " + hopProbeTimes[i][0] + "      " + hopProbeTimes[i][1] + "      "+ hopProbeTimes[i][2] + "      ";
                            log.append(line);
                        }
                    }catch(Exception e){
                        error = "ERROR PLEASE ENTER CORRECT URL";
                        cancel(true);
                    }

                    line = "\n";
                    res = String.valueOf(log.append(line));

                    log.append(line);
                    System.out.println(" result is new 1"+log.append(line));
                    publishProgress(log.toString());
                    String lastAddressCheck = InetAddress.getByName(address).getHostAddress();

                    //checks if target address is reached
                    if (hopAddress[i].equals(lastAddressCheck)) {
                        error = "";
                        return res;
                    }
                }
            }
            catch (Exception e){
                error = "ERROR PLEASE ENTER CORRECT URL";
                cancel(true);
            }
            return res;
        }

        @Override
        protected void onProgressUpdate(String... args){
            if (someProgressBar.getVisibility() == View.GONE) {
                System.out.println(" entering this part");
                someProgressBar.setVisibility(View.VISIBLE);
            } else {
                System.out.println(" entering this part two");
                someProgressBar.setVisibility(View.GONE);
                view.setText(args[0]);
            }
             //   progressBar.setVisibility(View.GONE);
          // else
             //   progressBar.setVisibility(View.VISIBLE);
           // view.setVisibility(View.INVISIBLE);
           //
            //view.setText(args[0]);
          //  showProgressBeforeLoadingData("");
            //if(view.setVisibility(VISIBLE))
            //{
             //  dismissProgress();
            //}
            ArrayList<String> list_count = new ArrayList<String>();
            list_count.add(args[0]);
            System.out.println(" args is is "+list_count.size());
           //traceRouteList(list_count);
        }


        @Override
        protected void onPostExecute(String result) {
            //System.out.println("Traceroute over");
           // view.setText(view.getText() + "\n Traceroute over."
            dismissProgress();
            super.onPostExecute(result);
            res = result;
            // BY SWAPNIL BANSAL 26/09/2022
            try {

               // view.setVisibility(View.GONE);
                Log.i(TAG, "Traceroute Result is" + result + "time is " + Config.getDateTime());
                String[] s1 = result.trim().split("\\s+");
                Log.i(TAG, "s1 is"+ Arrays.toString(s1));
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
                Log.i(TAG, " count is new " + list_count+"time is"+Config.getDateTime());
                Log.i(TAG, " hops is new " + list_hops+"time is"+Config.getDateTime());
                Log.i(TAG, "time is new" + list_time+"time is"+Config.getDateTime());
                //a.setVisibility(View.VISIBLE);
                a.setVisibility(View.GONE);
                traceRouteList(list_count, list_time, list_hops);
                updateApp();
                // dismissProgress();
                JSONArray pdoInformation = new JSONArray();
                JSONArray mtrInformation = new JSONArray();
                JSONObject mtr = new JSONObject();

                try {
                    mtr.put("psize", "");
                    mtr.put("bitpattern", "");
                    mtr.put("tests", "");
                    mtr.put("dst", "");
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
                Log.i(TAG, "Traceroute json from lib is  " + pDetail20.toString()+"time is"+Config.getDateTime());
//                RequestResponse.sendOldEvents(jsonArray, "site_speed_mtr");
                // progressBar.setVisibility(View.GONE);
                Utils.showLongToast(context, "Trace route result sent.");
                //  addoutputindb(pDetail20.toString(), "site_speed_mtr", context);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        @Override
        protected void onCancelled(){
            super.onCancelled();
            if(error != null) {
                if (!error.equals("")) {
                    view.setText(error);
                }
            }
        }
    }


    private void dismissProgress() {
        if(progressDialog!=null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
    }



    public static String ping(String address, int packets, int ttl, TextView view) throws Exception{
           System.out.println(" entering ping class ");

        //execute ping command
        String format = "ping -n -c %d -t %d %s";
        String command = String.format(format, packets, ttl, address);
        System.out.println(" entering ping class with command "+command);
        Process process = Runtime.getRuntime().exec(command);
        System.out.println(" entering ping class with process "+process);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        // Grab the results
        StringBuilder log = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            log.append(line + "\n");
            if(view != null){
                view.setText(log.toString());
            }
        }

        return log.toString();
    }

    public String parseHopIp(String pingOutput){
        String hopIp;
        pingOutput = pingOutput.replaceAll("[\\t\\n\\r]+"," ");
        String[] pingOutputArray = pingOutput.split(" ");
        hopIp = pingOutputArray[8].substring(0, pingOutputArray[8].length() - 1);

        System.out.println(" hop is "+hopIp);
        if (hopIp.equals("byte")) {
            hopIp = pingOutputArray[10].substring(0, pingOutputArray[10].length() - 1);
            System.out.println(" hop is in  "+hopIp);
        }
        if (pingOutputArray[7].equals("---")) {
            hopIp = "";
            System.out.println(" hop is in  oit "+hopIp);
        }
        return hopIp;
    }

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

    public void updateApp() {
        android.app.AlertDialog.Builder updatealert = new android.app.AlertDialog.Builder(getActivity());
            updatealert.setTitle(Html.fromHtml("<font color='#FF0000'>TRACEROUTE RESULT IN TABULAR FORM</font>"));
            updatealert.setMessage("Do you want to see the result of traceroute  in tabular form");
            updatealert.setPositiveButton(Html.fromHtml("<font color='#FF0000'>YES</font>"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tableView.setVisibility(VISIBLE);
                    //finish();
                }
            });
            updatealert.setNegativeButton(Html.fromHtml("<font color='#FF0000'>NO</font>"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    a.setVisibility(VISIBLE);
                    someProgressBar.setVisibility(View.GONE);
                }
            }).show();

        }

    }
