package com.newmview.wifi.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.network.NetworkClass;
import com.newmview.wifi.other.Utils;
import com.services.MyJobService;

import org.json.JSONException;

public class AllInOneAsyncTaskForNetwork extends AsyncTask<String, String, String> {
    public AsyncTaskPurpose asyncTaskPurpose;
    private String response;
    public Context context;
    private String evtType;

    public AllInOneAsyncTaskForNetwork(Context ctx, AsyncTaskPurpose asyncTaskPurpose) {
        this.asyncTaskPurpose=asyncTaskPurpose;
        this.context = ctx;

    }

    public enum AsyncTaskPurpose {
        IMUP,
        IMUPNEW,
        INIT,SDK,
        APP_OPEN,
        CALL_EVT, WEB_TEST_EVENT, VIDEO_TEST_EVT, DOWNLOAD_EVT, UPLOAD_EVT, REPORT_ISSUE_EVT, NETWORK_MONITOR_INFO_EVT, NEIGHBORING_CELLS_INFO, NETWORK_MONITOR_INFO_EVT_SECOND_SIM,
        // BY SWAPNIL BANSAL 10/06/2022
        DYNAMIC_EVT_PUSH,GSS,GEUL,JAR_DOWNLOAD_COMPLETE,UD,GAGD,
        //
        ToggleOffComplete,ToggleOnComplete,
        // // FOR SCREEN SHOT UPLOAD 10/14/2022
        GCN, UPLOAD,SPU,SENDSURVEY,
        Send_Logging_Event,
        // BY SWAPNIL BANSAL  30/03/2023
        NEW_VIDEO_TEST_EVT,NEW_WEB_TEST_EVT,
        NEW_SPEED_CLIENT_SOCKET_TEST



    }


    @Override
    protected String doInBackground(String... data) {
       String response= NetworkClass.sendPostRequest(data[0]);
        return response;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onPostExecute(String s)
    {
        super.onPostExecute(s);
        response = s;
        Utils.appendLog("ELOG_SERVER_RESPONSE: is "+response);
        System.out.println("Response from server is" + s);

        if (response != null && asyncTaskPurpose==AsyncTaskPurpose.INIT) {
            System.out.println(" Response  for init  is"+response);
            JSONParser jsonParser = new JSONParser(MviewApplication.ctx);
            jsonParser.parseInitNewData(response);
            RequestResponse.sendImupRequest();
            MyJobService.scheduleWork();
            //mView_HealthStatus.initResonse=response;
            System.out.println("calling parser....");
        }


        // by swapnil bansal for round robin
        if (response != null && asyncTaskPurpose==AsyncTaskPurpose.IMUP)
        {
            System.out.println(" Response for imup is"+response);
            JSONParser parser=new JSONParser(MviewApplication.ctx);
            parser.parseImupData(response);
        }
        if (response != null && asyncTaskPurpose==AsyncTaskPurpose.IMUPNEW)
        {
            System.out.println(" Response for new imup is"+response);
            JSONParser parser=new JSONParser(MviewApplication.ctx);
            parser.parseNewImupData(response);
        }

        // by vikas for web test urls
        if (response != null && asyncTaskPurpose==AsyncTaskPurpose.GAGD)
        {
            System.out.println(" Response  for gagd  is"+response);
            Utils.appendLog("ELOG_GAGD_FLAG: GAGD response is: "+response);
            JSONParser parser=new JSONParser(MviewApplication.ctx);
            parser.parseGagdData(response);
        }
//        if (response !=null && asyncTaskPurpose == AsyncTaskPurpose.SDK){
//
//            System.out.println(" Response for sdk is"+response);
////            JSONParser parser=new JSONParser(MviewApplication.ctx);
////            parser.parseevt(response,evtType);
//
//
//        }

        // DONE BY SWAPNIL FOR ROUND ROBIN PART ON 10/06/2022
        if (response != null && asyncTaskPurpose==AsyncTaskPurpose.GEUL)
        {
            System.out.println(" Response  for new video test  is"+response);
            JSONParser parser=new JSONParser(MviewApplication.ctx);
            try {
                parser.updateAndParseManualScan(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (response != null && asyncTaskPurpose==AsyncTaskPurpose.GSS)
        {
            Utils.appendLog(" Response  for new video test  is"+response);
            JSONParser parser=new JSONParser(MviewApplication.ctx);
            try {
                parser.updateAndParseGssScan(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (response != null && asyncTaskPurpose==AsyncTaskPurpose.UD)
        {
            Utils.appendLog(" Response  for UD request  is"+response);
            JSONParser parser=new JSONParser(MviewApplication.ctx);
            try {
                parser.parseAgentsUpdatedData(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (response!=null&& asyncTaskPurpose==AsyncTaskPurpose.SPU)
        {
            System.out.println(" Response  for new video test  is"+response);
            JSONParser parser=new JSONParser(MviewApplication.ctx);
            try {
                parser.parseSPU(response);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        // DONE BY SWAPNIL FOR ROUND ROBIN PART ON 10/06/2022
        if (response != null && asyncTaskPurpose==AsyncTaskPurpose.GCN)
        { System.out.println(" Response  for new video test  is"+response);

            JSONParser parser=new JSONParser(MviewApplication.ctx);
            try {
                parser.parseGCNData(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (response != null && asyncTaskPurpose==AsyncTaskPurpose.UPLOAD)
        {
            System.out.println(" Response  for new video test  is"+response);
            JSONParser parser=new JSONParser(MviewApplication.ctx);
            try {
                parser.getUploadData(response);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (response != null && asyncTaskPurpose==AsyncTaskPurpose.NEW_WEB_TEST_EVT)
        {
            System.out.println(" Response  for new video test  is"+response);
        }
        if (response != null && asyncTaskPurpose==AsyncTaskPurpose.NEW_VIDEO_TEST_EVT)
        {
            System.out.println(" Response  for new video test  is"+response);
        }
        if (response != null && asyncTaskPurpose==AsyncTaskPurpose.VIDEO_TEST_EVT)
        {
            System.out.println(" Response  for new video test  is"+response);
        }

        if (response != null && asyncTaskPurpose==AsyncTaskPurpose.NEW_SPEED_CLIENT_SOCKET_TEST)
        {
            System.out.println(" Response  for new video test  is"+response);
        }
        if (response != null && asyncTaskPurpose == AsyncTaskPurpose.CALL_EVT){

            System.out.println(" Response  for new call test  is"+response);

        }

        switch (asyncTaskPurpose)
        {

        }


    }
}
