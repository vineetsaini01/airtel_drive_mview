package com.newmview.wifi.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.mview.airtel.R;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.helper.AllInOneAsyncTaskForNetwork;
import com.newmview.wifi.network.NetworkClass;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity3 extends AppCompatActivity {

    private String response;
    Button add;
    View v;
    LinearLayout layout;
    LayoutInflater vi;
    ViewGroup insertPoint;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        add = findViewById(R.id.button);
        layout=findViewById(R.id.container);

        addProgressBarCard("fetching your router details",0);
        addProgressBarCard("settings things up for you",1);
        addProgressBarCard("sit tight...we're almost there",2);

        //request1
        //progressbar 1 start showing
        //process
        //response
        //progress bar succsess(green)//start second req
        //progress bar non -success(red)//dnt start second req
        // do cross for tick

        add.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                // first request
                 View rootView=insertPoint.getChildAt(0);
                 ProgressBar progressBar=rootView.findViewById(R.id.progressBar1);
                 TextView  textView1 = (TextView) rootView.findViewById(R.id.textView11);
                 View dot=rootView.findViewById(R.id.dot);
                 CheckBox checkBox=rootView.findViewById(R.id.checkbox);
                 // RequestResponse.sendInitRequest();
                  API_sendInitRequest();
                 dot.setVisibility(View.INVISIBLE);
                 progressBar.setVisibility(View.VISIBLE);
                 textView1.setTextColor(Color.RED);


                 // second request
               // RequestResponse.sendImupRequest();
                View rootView1=insertPoint.getChildAt(1);
                ProgressBar progressBar1=rootView1.findViewById(R.id.progressBar1);
                View dot1=rootView1.findViewById(R.id.dot);
                TextView  textView2 = (TextView) rootView1.findViewById(R.id.textView11);
                CheckBox checkBox3=rootView1.findViewById(R.id.checkbox);
               // dot1.setVisibility(View.INVISIBLE);
               // progressBar1.setVisibility(View.VISIBLE);
//                if (asyncTaskPurpose== AllInOneAsyncTaskForNetwork.AsyncTaskPurpose.IMUP) {
//
//
//                    }

                // third  request
               // RequestResponse.sendInitRequest();
                View rootView2=insertPoint.getChildAt(2);
                ProgressBar progressBar2=rootView2.findViewById(R.id.progressBar1);
                View dot2=rootView2.findViewById(R.id.dot);
                TextView  textView3 = (TextView) rootView2.findViewById(R.id.textView11);
                CheckBox checkBox4=rootView2.findViewById(R.id.checkbox);
                //dot2.setVisibility(View.INVISIBLE);
                //progressBar2.setVisibility(View.VISIBLE);
//                if (asyncTaskPurpose== AllInOneAsyncTaskForNetwork.AsyncTaskPurpose.INIT) {
//                    progressBar2.setVisibility(View.INVISIBLE);
//                    checkBox4.setVisibility(View.VISIBLE);
//                    textView3.getPaint().setShader(null);
//                    textView3.setTextColor(Color.GREEN);
//
//                }


        }});

    }



    public String API_sendInitRequest() {
        System.out.println("SendInitRequestInfo ");
        String res = "";
        try {
            new Async_SendInitRequestInfo().execute();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("exception in SendInitRequestInfo is" + e.toString());
        }
        return res;
    }
    public String API_sendInitRequest1() {
        System.out.println("SendInitRequestInfo ");
        String res = "";
        try {
            new Async_SendInitRequestInfo1().execute();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("exception in SendInitRequestInfo is" + e.toString());
        }
        return res;
    }
    public class Async_SendInitRequestInfo1 extends AsyncTask<Object, Void, String> {
        private String res;
        // Add onpostexecute method and in that you will receive response, and in response
        //check if status key has value 0/1
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
        @Override
        protected String doInBackground(Object... params) {
            JSONObject a = new JSONObject();
            String res = "";
            try {
                a.put("msg", "init");
                a.put("os_version", "22");
                a.put("device_info", "");
                a.put("interface", "CLI");
                a.put("prod", "mtantu");
                a.put("msisdn", Utils.getMyContactNum(MviewApplication.ctx));
                a.put("ver", Utils.getMyContactNum(MviewApplication.ctx) );
                a.put("imsi", Constants.IMSI);
                a.put("phone_imsi",  Constants.IMSI);
                a.put("latitude", "0");
                a.put("longitude", "0");
                a.put("lat", "0");
                a.put("lon", "0");
                a.put("lacid", "0");
                a.put("pubid", "0");
                a.put("clickid", "0");
                a.put("cellid", "0");
                a.put("apn", "Jio 4G");// Add actual value
                a.put("apn_type", "Mobile Data");
                a.put("operatorname", "");
                a.put("ip", "NA");
                a.put("port", "9999");
                a.put("country_code", "IN");
                a.put("androidsdk", Build.VERSION.SDK_INT);
                String arg = a.toString();
                res = NetworkClass.sendPostRequest1(Constants.URL, arg);
                System.out.println("Init Request  json is " + a.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
            // This will do nothing and will return null, from here pass this jsonobject to alpha3 server path
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            res = result;
            System.out.println("Init Result is new" + result);
            if (res != null) {
                try {
                    View rootView4 = insertPoint.getChildAt(2);
                    System.out.println("ROOT VIEW IS" +rootView4);
                    ProgressBar progressBar = rootView4.findViewById(R.id.progressBar1);
                    CheckBox checkBox = rootView4.findViewById(R.id.checkbox);
                    TextView textView1 = (TextView) rootView4.findViewById(R.id.textView11);
                    progressBar.setVisibility(View.INVISIBLE);
                    checkBox.setVisibility(View.VISIBLE);
                    textView1.getPaint().setShader(null);
                    textView1.setTextColor(Color.GREEN);
                    System.out.println("calling parser....");
                }catch (Exception E)
                {
                    System.out.println("calling exception"+E.getMessage());
                    E.printStackTrace();

                }
            }

        }
    }
    public class Async_SendInitRequestInfo extends AsyncTask<Object, Void, String> {
        private String res;
        // Add onpostexecute method and in that you will receive response, and in response
        //check if status key has value 0/1
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
        @Override
        protected String doInBackground(Object... params) {
            JSONObject a = new JSONObject();
            String res = "";
            try {
                a.put("msg", "init");
                a.put("os_version", "22");
                a.put("device_info", "");
                a.put("interface", "CLI");
                a.put("prod", "mtantu");
                a.put("msisdn", Utils.getMyContactNum(MviewApplication.ctx));
                a.put("ver", Utils.getMyContactNum(MviewApplication.ctx) );
                a.put("imsi", Constants.IMSI);
                a.put("phone_imsi",  Constants.IMSI);
                a.put("latitude", "0");
                a.put("longitude", "0");
                a.put("lat", "0");
                a.put("lon", "0");
                a.put("lacid", "0");
                a.put("pubid", "0");
                a.put("clickid", "0");
                a.put("cellid", "0");
                a.put("apn", "Jio 4G");// Add actual value
                a.put("apn_type", "Mobile Data");
                a.put("operatorname", "");
                a.put("ip", "NA");
                a.put("port", "9999");
                a.put("country_code", "IN");
                a.put("androidsdk", Build.VERSION.SDK_INT);
                String arg = a.toString();
                res = NetworkClass.sendPostRequest1(Constants.URL, arg);
                System.out.println("Init Request  json is " + a.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
            // This will do nothing and will return null, from here pass this jsonobject to alpha3 server path
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            res = result;
            System.out.println("Init Result is new" + result);
            if (res != null) {
                try {

                    View rootView4 = insertPoint.getChildAt(0);
                    System.out.println("ROOT VIEW IS" +rootView4);
                    ProgressBar progressBar = rootView4.findViewById(R.id.progressBar1);
                    CheckBox checkBox = rootView4.findViewById(R.id.checkbox);
                    TextView textView1 = (TextView) rootView4.findViewById(R.id.textView11);
                    progressBar.setVisibility(View.INVISIBLE);
                    checkBox.setVisibility(View.VISIBLE);
                    textView1.getPaint().setShader(null);
                    textView1.setTextColor(Color.GREEN);
                    System.out.println("calling parser....");

                    View rootView1=insertPoint.getChildAt(1);
                    ProgressBar progressBar1=rootView1.findViewById(R.id.progressBar1);
                    View dot1=rootView1.findViewById(R.id.dot);
                    TextView  textView2 = (TextView) rootView1.findViewById(R.id.textView11);
                    CheckBox checkBox3=rootView1.findViewById(R.id.checkbox);
                    dot1.setVisibility(View.INVISIBLE);
                    progressBar1.setVisibility(View.VISIBLE);
                    textView2.setTextColor(Color.RED);
                    API_sendImupRequest();
                }catch (Exception E)
                {
                    System.out.println("calling exception"+E.getMessage());
                    E.printStackTrace();

                }
            }
        }
    }
    public String API_sendImupRequest() {
        System.out.println("SendImupRequestInfo ");
        String res = "";
        try {
            new Async_SendImupRequestInfo().execute();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("exception in SendImupRequestInfo is" + e.toString());
        }
        return res;
    }


    public class Async_SendImupRequestInfo extends AsyncTask<Object, Void, String> {
        private String res;

        // Add onpostexecute method and in that you will receive response, and in response
        //check if status key has value 0/1
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
        @Override
        protected String doInBackground(Object... params) {
            JSONObject a = new JSONObject();
            String res = "";
            try {
                // String lat = "0";
                // String lon = "0";

                a.put("msg", "imup");
                a.put("ver", Utils.getMyContactNum(MviewApplication.ctx));
                a.put("prod", "mtantu");
                a.put("latitude", "0");
                a.put("ip", "NA");
                a.put("apn_type", "wla");
                a.put("clickid", "0");
                a.put("lon", "0");
                a.put("imsi", Constants.IMSI);
                a.put("type", "config_change");
                a.put("interface", "CLI");
                a.put("cellid", "0");
                a.put("phone_imsi",Constants.IMSI);
                a.put("country_code", "IN");
                a.put("androidsdk", Build.VERSION.SDK_INT);
                a.put("port", "9999");
                a.put("lacid", "0");
                a.put("pubid", "0");
                a.put("msisdn",Utils.getMyContactNum(MviewApplication.ctx));
                a.put("apn", "Jio 4G");// Add actual value
                a.put("lat", "0");
                a.put("longitude", "0");
                a.put("operatorname", "");
                a.put("os_version", "22");
                a.put("device_info", "");
                String arg = a.toString();
                res = NetworkClass.sendPostRequest1(Constants.URL, arg);
                System.out.println("Imup  Request  json is " + a.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
            // This will do nothing and will return null, from here pass this jsonobject to alpha3 server path
        }


        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            res = result;

            System.out.println("Imup Result is" + result);
            View rootView1=insertPoint.getChildAt(1);
            ProgressBar progressBar1=rootView1.findViewById(R.id.progressBar1);
            View dot1=rootView1.findViewById(R.id.dot);
            TextView  textView2 = (TextView) rootView1.findViewById(R.id.textView11);
            CheckBox checkBox3=rootView1.findViewById(R.id.checkbox);
            progressBar1.setVisibility(View.INVISIBLE);
            checkBox3.setVisibility(View.VISIBLE);
            textView2.getPaint().setShader(null);
            textView2.setTextColor(Color.GREEN);


            View rootView2=insertPoint.getChildAt(2);
            ProgressBar progressBar2=rootView2.findViewById(R.id.progressBar1);
            View dot2=rootView2.findViewById(R.id.dot);
            TextView  textView3 = (TextView) rootView2.findViewById(R.id.textView11);
            CheckBox checkBox4=rootView2.findViewById(R.id.checkbox);
            dot2.setVisibility(View.INVISIBLE);
            textView3.setTextColor(Color.RED);
            progressBar2.setVisibility(View.VISIBLE);
            API_sendInitRequest1();



        }


    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void addProgressBarCard(String name,int index)
    {
        vi= (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v= vi.inflate(R.layout.progress_text, null);
        TextView textView=v.findViewById(R.id.textView11);
        textView.setText(name);
        //Shader textShader = new LinearGradient(0, 0, textView.getPaint().measureText(textView.getText().toString()), textView.getTextSize(), new int[]{Color.RED, Color.GREEN}, new float[]{0, 1}, Shader.TileMode.CLAMP);
       // textView.getPaint().setShader(textShader);
        textView.setTextColor(Color.BLACK);
        insertPoint = (ViewGroup) findViewById(R.id.container1);
        insertPoint.addView(v,index);



    }


}