package com.newmview.wifi.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.newmview.wifi.activity.MainActivity;
import com.newmview.wifi.application.MviewService;
import com.newmview.wifi.interfaces.AsynctaskListener;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;
import com.webservice.PviewWebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.newmview.wifi.other.Constants.pviewURL;



/**
 * Created by functionapps on 3/5/2019.
 */

public class AsyncTasks_APIgetData extends AsyncTask<HashMap<String,String>, Void, String>  {

    private ProgressDialog progressDialog;
    private Context context;
    private AsynctaskListener callback;
    private String response;
    private String json;


    public AsyncTasks_APIgetData(Context context) {
        this.context = context;
    }

    public AsyncTasks_APIgetData(Context context, AsynctaskListener listener) {
        this.context = context;
        callback=listener;
       System.out.println("res request is "+CommonUtil.request);
    }

    public AsyncTasks_APIgetData(MviewService context) {
        this.context=context;
    }


    @Override
    protected void onPreExecute() {

        super.onPreExecute();

            showProgressBeforeLoadingData();

System.out.println("req in on pre......"+Utils.getDateTime());

    }

    private void showProgressBeforeLoadingData() {

        if (progressDialog == null ) {
            progressDialog = new ProgressDialog(context);
            if (!progressDialog.isShowing())
            {
                progressDialog.setMessage(Constants.LOADING);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
        }
    }

    @Override
    protected String doInBackground(HashMap<String, String>... params) {
        //while (!isCancelled()) {
        String url=null;
        System.out.println("req in doinnn...... "+Utils.getDateTime());
/*if(CommonUtil.request==6)
{
     url=Constants.coordinatesURL;
    response = PviewWebService.sendPostRequest(url, null,context);
    getCoordinatesJSON(response);

}
else*/
{
   url= pviewURL;
    response = PviewWebService.sendPostRequest(url, params[0],context);
    
}





            System.out.println("res in doinback "+response +" from request is "+CommonUtil.request);

       //}
        return response;
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result!=null) {
            JSONParser jsonParser = new JSONParser(context);

            try {
                dismissProgress();
                if(CommonUtil.request==3)
                {
                    ((MainActivity)context).getDashboards();
                }
/*
            if(CommonUtil.request==6)
            {
                if(context instanceof GraphDetailsActivity)
                {

                    ((GraphDetailsActivity)context).sendLatLongResponse(json);

                }
            }
            else {
*/


                      if(result.equalsIgnoreCase("timeout"))
                        {
    Utils.showToast(context,Constants.CONNECTION_TIMEOUT_STRING);

              }
                      else if(result.equalsIgnoreCase(Constants.SERVER_ERROR))
                      {
                          Utils.showAlert(false, Constants.SERVER_ERROR,context, null);
                          //Utils.showToast(context,Constants.SERVER_ERROR);
                      }
                      else
                      {
                          jsonParser.parseData(result);
                      }

             /* if(Constants.ERROR==2)
                {
                    Utils.showToast(context,CONNECTION_ERROR);
                    dismissProgress();
                    Constants.ERROR=0;
                }
                else if(Constants.ERROR==3)
                {
                    Utils.showToast(context,Constants.SERVER_ERROR);
                    dismissProgress();
                    Constants.ERROR=0;
                }
                else
              {
                  dismissProgress();
              }*/
                if(callback!=null) {
                    callback.onTaskCompleted();
                }
                // }
            } catch (Exception e) {
                dismissProgress();
                Utils.showToast(context,Constants.SERVER_ERROR);
                System.out.println("Exception is parsing is " + e.toString());
            }
        }
        else
        {
//            Utils.showToast(context,Constants.SERVER_ERROR);
//            dismissProgress();
        }
           /* if(Constants.ERROR==2)
            {
                Utils.showToast(context,CONNECTION_ERROR);
                dismissProgress();
                Constants.ERROR=0;
            }
            else
            {
                Utils.showToast(context,Constants.SERVER_ERROR);
                dismissProgress();
            }*/



    }

    private void dismissProgress() {
        if(progressDialog!=null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
    }

    private void getCoordinatesJSON(final String result) {

       /* AsyncTask.execute(new Runnable() {
            @Override
            public void run() {*/
        //TODO your background code
        if(result!=null) {
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(result);

                JSONArray finalArray = new JSONArray();
                ArrayList<String> states = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    JSONObject jsonObject2 = new JSONObject();
                    JSONArray coordarray= new JSONArray(jsonArray.getJSONObject(i).optString("coordinates"));
                    jsonObject2.put("coordinates", coordarray);
                    jsonObject2.put("type", jsonArray.getJSONObject(i).optString("type"));
                    finalArray.put(jsonObject2);
                    String state = jsonObject.optString("state");
                    states.add(state);


                }

                System.out.println("result is " + finalArray);

                json = String.valueOf(finalArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        //  }
        // });




    }

}
