package com.newmview.wifi.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.network.NetworkClass;


public class AllInOneAsyncTaskForEVT extends AsyncTask<String, String, String> {
    public static AsyncTaskPurpose asyncTaskPurpose;
    private String response;
    public Context context;
    private String evtType;
    private String rowid;
    Interfaces.ResultListner resultListner;

    public AllInOneAsyncTaskForEVT(Context ctx, AsyncTaskPurpose asyncTaskPurpose, String eventType, String rowid, Interfaces.ResultListner resultListner) {
        this.asyncTaskPurpose=asyncTaskPurpose;
        this.context = ctx;
        this.evtType = eventType;
        this.rowid =rowid;
        this.resultListner = resultListner;

    }



    public enum AsyncTaskPurpose {
        SDK
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
        System.out.println("SDK Result is" + s);

//        Utils.appendLog("ELOG_EVT_RESPONSE: is:  "+response );
        // by vikas for web test urls
        if (response !=null && asyncTaskPurpose == AsyncTaskPurpose.SDK){

            System.out.println(" Response for sdk is"+response);
            JSONParser parser=new JSONParser(MviewApplication.ctx);
            parser.parseevt(response,evtType,rowid);


        }else if (resultListner != null){
            resultListner.onResultObtained(false);

        }



    }
}
