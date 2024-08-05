package com.newmview.wifi.application;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.newmview.wifi.helper.CommonUtil;
import com.newmview.wifi.helper.JSONParser;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;
import com.webservice.PviewWebService;

import java.util.HashMap;

import static com.newmview.wifi.activity.MainActivity.context;
import static com.newmview.wifi.other.Constants.CONNECTION_ERROR;
import static com.newmview.wifi.other.Constants.pviewURL;

public class MviewService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getDashboardsData();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        getSideDrawerData();


    }

    private void getSideDrawerData() {

    }

    private void getDashboardsData() {
        if (Utils.isNetworkAvailable(MviewService.this)) {

            HashMap<String, String> obj = new HashMap<>();
            obj.put(CommonUtil.REQUEST_KEY, CommonUtil.DASHBOARD_REQUEST);
            obj.put(CommonUtil.USER_ID_KEY, CommonUtil.USER_ID_NEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new AllInOneAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,obj);

            } else {
                new AllInOneAsyncTask().execute(obj);
            }


            CommonUtil.request = 3;

        } else {
            /* Utils.showToast(getActivity(), Constants.NO_INTERNET);*/
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class AllInOneAsyncTask extends AsyncTask<HashMap<String,String>, Void, String>{


        private String url;
        private String response;

        @Override
        protected String doInBackground(HashMap<String, String>... params) {
            url= pviewURL;
            response = PviewWebService.sendPostRequest(url, params[0],context);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(response!=null)
            {
                JSONParser jsonParser=new JSONParser(MviewService.this);
                jsonParser.parseData(response);
                System.out.println("calling parser....");
            }
            else
            {
                if(Constants.ERROR==2)
                {
                    Utils.showToast(context,CONNECTION_ERROR);
                   // dismissProgress();
                    Constants.ERROR=0;
                }

            }
        }
    }
}
