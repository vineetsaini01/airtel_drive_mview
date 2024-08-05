/*
package com.mcpsinc.mview.other;

import android.content.Context;

import com.mcpsinc.mview.activity.GraphActivity;
import com.mcpsinc.mview.helper.AsyncTasks_APIgetData;
import com.mcpsinc.mview.helper.CommonUtil;

import java.util.HashMap;

public class RequestResponse {

    private static void sendDrillRequest(String repId, String repName, String repType, String srt, Context context) {
        HashMap<String, String> obj = new HashMap<>();
        obj.put(CommonUtil.USER_ID_KEY, CommonUtil.USER_ID);
        obj.put(CommonUtil.REQUEST_KEY, CommonUtil.DRILL_REQUEST);
        obj.put("repId", repId);
        obj.put("drillType", repType);
        obj.put("drillFields", "Latitude@Longitude");
        //obj.put("drillData", lat + "@" + lon);
        obj.put("srt", srt);
        obj.put("cli", "0");
       // RepId=repId;
        AsyncTasks_APIgetData apigetData = new AsyncTasks_APIgetData(context, GraphActivity.this);
        CommonUtil.request = 2;
        apigetData.execute(obj);


    }
}
*/
