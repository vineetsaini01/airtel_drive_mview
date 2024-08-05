package com.newmview.wifi.helper;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.dashboard.activity.GraphDetailsActivity;
import com.functionapps.mview_sdk2.main.Mview;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;
import com.services.MyJobService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.newmview.wifi.other.Constants.scan_number;

/**
 * Created by functionapps on 3/5/2019.
 */

public class JSONParser {
    private static final String TAG = "JSONParser";
    public  String json;
    private static Context context;
    private String status;
    private JSONObject jsonObject;
    private String statusInit;
    private long recordsTotal,recordsFiltered;
    private ArrayList<HashMap<String,String>> graphList;
    private ArrayList<HashMap<String,String>> repIdList;
    private ArrayList<HashMap<String,String>> yTitleList;
    private ArrayList<HashMap<String,String>> chartTitleList;
    private ArrayList<HashMap<String,String>> xTitleList;
    private ArrayList<HashMap<String,String>> xaxisTypeList;
    private ArrayList<HashMap<String,String>> xaxisList;
    private ArrayList<HashMap<String,String>> sourceList;
    private ArrayList<HashMap<String,String>> columnWiseList;
    private ArrayList<HashMap<String,String>> linkrepdetailsList;
    private ArrayList<ArrayList<HashMap<String, String>>> graphFinalList;
    private ArrayList<HashMap<String,String>> datalist;
    private ArrayList<String> dbIdList=new ArrayList<>();
    private ArrayList<String> graphIdList=new ArrayList<>();
    private ArrayList<ArrayList<String>> totalTableNewList=new ArrayList<>();

    private ArrayList<String> colNamesList;
    private ArrayList<HashMap<String,String>> tableDataList;
    private ArrayList<HashMap<String,String>> tablePercentDataList;
    private String totalcount;
    private ArrayList<HashMap<String, String>> pieDataNamesList;
    private ArrayList<HashMap<String, String>> pieDataColumns;
    private ArrayList<ArrayList<HashMap<String, String>>> tableAllColumsDataList;
    private String button;
    private    ArrayList<ArrayList<HashMap<String, String>>> filter;
    private ArrayList<HashMap<String,String>> multiYData;
    // by swapnil bansal 10/03/2022 for round robin
    private String manual_scan_flag, schedule_scan_flag, agent_new_details,updateFlag;
    public String device_sno, file_name, file_size, file_cksum, jar_path, class_name, method_name, ssdt, sedt, period, num_of_times, agent_version, type, frequnecy, currentstatusofserver, statusOfDownload, priority, task_type,data;
    public JSONParser(Context context) {
        this.context=context;
        //  db_handler=new DB_handler(this.context);
    }

    public void parseData(final String result) {
        try {
//
            if(result!=null )
            {
                System.out.println("request is "+CommonUtil.request);


                if(CommonUtil.request==5)


                {
                    parseGetGraphData(result);

                }
                /*else if(CommonUtil.request==6)
                {
                    getCoordinatesJSON(result);


                }*/
                else
                {
                    jsonObject=new JSONObject(result);
                    status= jsonObject.optString("status");




                    if(status.equalsIgnoreCase("400"))
                    {

                        String  message=jsonObject.optString("message");
//                        Utils.showToast(context,message);
                    }
                    else {
                        if (CommonUtil.request == 1) {
                            parseInitData(result);
                        } else if (CommonUtil.request == 2) {
                            parseDataAccordingToDrillDown(result);
                            //   parseGraphData(result);
                        } else if (CommonUtil.request == 3) {
                            parseGetDashboardData(result);
                        } else if (CommonUtil.request == 4) {
                            parseGetGraphDetailsData(result);
                        }
                    }
                }
            }
            else
            {

            }


        }


        catch (JSONException e)
        {
            e.printStackTrace();
            Constants.ERROR=3;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void parseGetGraphData(String result) throws JSONException {




        try {


            if(GraphDetailsActivity.ctype.equalsIgnoreCase("tablewithpie"))
            {
                //clearPieLists();
                JSONArray tjsonArray = new JSONArray(result);
                colNamesList = new ArrayList<>();
                tableDataList = new ArrayList<>();
                tablePercentDataList = new ArrayList<>();
                JSONObject jsonObject = tjsonArray.optJSONObject(1);
                if(jsonObject!=null ) {
                    JSONArray colArray = jsonObject.optJSONArray("columns");
                    if(colArray!=null && colArray.length()>0) {
                        for (int i = 0; i < colArray.length(); i++) {
                            JSONObject colArrayJsonObject = colArray.optJSONObject(i);
                            colNamesList.add(colArrayJsonObject.optString("data"));
                        }
                    }
                }
                System.out.println("table percent collist " + colNamesList);
                JSONObject dataJsonObject = tjsonArray.optJSONObject(2);
                if(dataJsonObject!=null && dataJsonObject.length()>0) {
                    JSONArray jsonDataArray = dataJsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonDataArray.length(); i++) {
                        JSONObject jsonObject1 = jsonDataArray.getJSONObject(i);
                        JSONArray dataArray = jsonObject1.getJSONArray("data");

                        for (int j = 0; j < dataArray.length(); j++) {
                            JSONObject dataObject = dataArray.getJSONObject(j);
                            HashMap<String, String> dataHp = new HashMap<>();
                            if (j == 0) {
                                dataHp.put("name", dataObject.optString("name"));
                                dataHp.put("y", dataObject.optString("y"));
                                tableDataList.add(dataHp);
                            } else if (j == 1) {
                                dataHp.put("name", dataObject.optString("name"));
                                dataHp.put("y", dataObject.optString("y"));
                                dataHp.put("percent", dataObject.optString("percent"));
                                tablePercentDataList.add(dataHp);
                            }

                        }
                    }
                }






                JSONObject jsonObject2=tjsonArray.getJSONObject(3);
                JSONArray piedataarray=jsonObject2.getJSONArray("piedata");
                pieDataNamesList=new ArrayList<>();
                for(int i=0;i<piedataarray.length();i++)
                {
                    JSONObject jsonObject3=piedataarray.getJSONObject(i);
                    HashMap<String,String> datehp=new HashMap<>();
                    datehp.put("name",jsonObject3.optString("name"));
                    pieDataNamesList.add(datehp);
                    JSONArray piearray=jsonObject3.getJSONArray("data");
                    pieDataColumns=new ArrayList<>();
                    for(int j=0;j<piearray.length();j++)
                    {
                        JSONObject obj1 = piearray.getJSONObject(j);
                        HashMap<String, String> datahp = new HashMap<>();
                        datahp.put("name", obj1.optString("name"));
                        datahp.put("y", obj1.optString("y"));
                        datahp.put("count",obj1.optString("count"));
                        pieDataColumns.add(datahp);

                    }
                }



                JSONObject totalObj = tjsonArray.getJSONObject(4);
                JSONArray totalArray = totalObj.getJSONArray("data");
                JSONObject totalCountObj=    totalArray.getJSONObject(0);
                totalcount=totalCountObj.optString("totalCount");



                System.out.println("table percent data " + tablePercentDataList);
                System.out.println("pie date range "+CommonUtil.pieDateRange);
                System.out.println("pie colums data "+CommonUtil.pieColumns);
                if(context instanceof GraphDetailsActivity)
                {
                    System.out.println("column wise list "+columnWiseList);
                    ((GraphDetailsActivity)context).sendResponseOfTableWithPie(colNamesList, tableDataList, tablePercentDataList,totalcount,pieDataNamesList,pieDataColumns);
                }

            }
            else if(GraphDetailsActivity.ctype.equalsIgnoreCase("table"))
            {
//                JSONArray jsonArray=new JSONArray(result);
//                colNamesList=new ArrayList<>();
//                for(int i=1;i<jsonArray.length();i++)
//                {
//                    datalist=new ArrayList<>();
//                    JSONObject jsonObject=jsonArray.getJSONObject(i);
//                    String colname=jsonObject.optString("name");
//                    colNamesList.add(colname);
//                    JSONArray data_array=  jsonObject.optJSONArray("data");
//                    for(int j=0;j<data_array.length();j++)
//                    {
//
//                        Object element = data_array.get(j);
//                        if (element instanceof JSONObject) {
//                            HashMap<String, String> datamap = new HashMap<>();
//                            JSONObject data_obj = (JSONObject) element;
//                            String name = data_obj.optString("name");
//                            String value = data_obj.optString("value");
//                            datamap.put("name", name);
//                            datamap.put("value", value);
//                            datalist.add(datamap);
//                        }else if (element instanceof JSONArray) {
//                            // Handle the case where the element is a JSONArray
//                            // For example, you can log this or process it as per your needs
//                            Log.w("BarGraph", "Element at index " + j + " is a JSONArray. Skipping this element.");
//                        } else {
//                            // Handle the case where the element is neither JSONObject nor JSONArray
//                            Log.w("BarGraph", "Element at index " + j + " is neither a JSONObject nor a JSONArray. Skipping this element.");
//                        }
//
//
//                    }
//
//
//                }
//
//                if(context instanceof GraphDetailsActivity)
//                {
//                    System.out.println("column wise list "+columnWiseList);
//                    ((GraphDetailsActivity)context).sendResponseOfGetGraphDetails(datalist,colNamesList);
//                }

                if(Utils.checkifavailable(result))
                {
                    result=result.replace("/div>","");
                }

                parseTableData(result);

            }
            else if(GraphDetailsActivity.ctype.equalsIgnoreCase("Multi_Y_Axis_Bar"))
            {
                getMultiYAxisResponse(result);
            }
            else if(GraphDetailsActivity.ctype.equalsIgnoreCase("metric")) {
                getMetricGraphResponse(result);

            }
            else if(GraphDetailsActivity.ctype.equalsIgnoreCase("tablewithpercent")) {

                getTableWithPercentResponse(result);

            }
            else if(GraphDetailsActivity.ctype.equalsIgnoreCase("donut"))
            {
                getDonutResponse(result);

            }
            else if(GraphDetailsActivity.ctype.equalsIgnoreCase("pie") )
            {
                getPieGraphResponse(result);

            }
/*
            else if(GraphDetailsActivity.ctype.equalsIgnoreCase("gmap"))
            {
                JSONArray jsonArray = new JSONArray(result);
                JSONObject jsonObject = jsonArray.getJSONObject(1);
                JSONArray graphDataArray = jsonObject.getJSONArray("commonData");
                graphList=new ArrayList<>();
                graphFinalList=new ArrayList<>();
                for(int i=0;i<graphDataArray.length();i++)
                {
                    HashMap<String, String> graphdatahashMap = new HashMap<>();
                    JSONObject obj = graphDataArray.getJSONObject(i);
                    graphdatahashMap.put("name", String.valueOf(obj.optString("name")));
                    graphList.add(graphdatahashMap);

                    JSONArray dataArray = obj.getJSONArray("data");
                    ArrayList<HashMap<String, String>> local = new ArrayList<>();
                    for(int j=0;j<dataArray.length();j++)
                    {
                        HashMap<String, String> datahashMap = new HashMap<>();
                        JSONObject obj1 = dataArray.getJSONObject(j);
                        datahashMap.put("name", obj1.optString("name"));
                        datahashMap.put("y", String.valueOf(obj1.optInt("y")));
                        datahashMap.put("lat",String.valueOf(obj1.optDouble("Latitude")));
                        datahashMap.put("long",String.valueOf(obj1.optDouble("Longitude")));
                        local.add(datahashMap);

                    }
                    if (local != null && local.size() > 0) {
                        graphFinalList.add(local);
                    }
                }

            }
*/

            else {

                {
                    init();
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject jsonObject = jsonArray.getJSONObject(1);
                    JSONArray graphDataArray = jsonObject.getJSONArray("commonData");
                    for (int graphNum = 0; graphNum < graphDataArray.length(); graphNum++) {
                        HashMap<String, String> graphdatahashMap = new HashMap<>();
                        JSONObject obj = graphDataArray.getJSONObject(graphNum);
                        graphdatahashMap.put("name", String.valueOf(obj.optString("name")));
                        JSONArray dataArray = obj.getJSONArray("data");
                        graphList.add(graphdatahashMap);
                        ArrayList<HashMap<String, String>> local = new ArrayList<>();
                        for (int datanum = 0; datanum < dataArray.length(); datanum++) {
                            HashMap<String, String> datahashMap = new HashMap<>();
                            JSONObject obj1 = dataArray.getJSONObject(datanum);
                            datahashMap.put("name", obj1.optString("name"));
                            datahashMap.put("y", String.valueOf(obj1.optInt("y")));
                            if(GraphDetailsActivity.ctype.equalsIgnoreCase("gmap"))
                            {
                                System.out.println("lat long "+obj1.optDouble("lat "));
                                datahashMap.put("lat",String.valueOf(obj1.optDouble("Latitude")));
                                datahashMap.put("long",String.valueOf(obj1.optDouble("Longitude")));

                            }
                            local.add(datahashMap);

                        }
                        if (local != null && local.size() > 0) {
                            graphFinalList.add(local);
                        }

                    }
                    System.out.println("new graphlist is " + graphList);
                    System.out.println("new graphlist final is " + graphFinalList);

                    JSONObject jsonObject1 = jsonArray.optJSONObject(2);
                    JSONArray reportArray = jsonObject1.optJSONArray("repId");
                    JSONArray yTitleArray = jsonObject1.optJSONArray("ytitle");
                    JSONArray chartTitleArray = jsonObject1.optJSONArray("chartTitle");
                    JSONArray xTitleArray = jsonObject1.optJSONArray("xtitle");
                    JSONArray xaxistypeArray = jsonObject1.optJSONArray("xaxistype");
                    JSONArray xaxisArray = jsonObject1.optJSONArray("xaxis");
                    JSONArray sourceArray = jsonObject1.optJSONArray("source");
                    JSONArray columnwiseArray = jsonObject1.optJSONArray("columnwisecount");
                    JSONArray linkRepArray = jsonObject1.optJSONArray("linkrepdetails");
                    JSONArray btnArray=jsonObject1.optJSONArray("button");


                    for (int rep = 0; rep < reportArray.length(); rep++) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("name", String.valueOf(reportArray.get(rep)));
                        repIdList.add(hashMap);
                        System.out.println("rep id is " + repIdList);


                    }
                    for (int rep = 0; rep < yTitleArray.length(); rep++) {
                        HashMap<String, String> hashMap = new HashMap<>();

                        hashMap.put("name", String.valueOf(yTitleArray.get(0)));
                        yTitleList.add(hashMap);

                    }
                    for (int rep = 0; rep < chartTitleArray.length(); rep++) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("name", String.valueOf(chartTitleArray.get(0)));
                        chartTitleList.add(hashMap);

                    }
                    for (int rep = 0; rep < xTitleArray.length(); rep++) {
                        HashMap<String, String> hashMap = new HashMap<>();

                        hashMap.put("name", String.valueOf(xTitleArray.get(0)));
                        xTitleList.add(hashMap);

                    }
                    if (xaxistypeArray != null && xaxistypeArray.length() > 0) {

                        for (int rep = 0; rep < xaxistypeArray.length(); rep++) {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("name", String.valueOf(xaxistypeArray.get(0)));
                            xaxisTypeList.add(hashMap);

                        }
                    }
                    if (xaxisArray != null && xaxisArray.length() > 0) {
                        for (int rep = 0; rep < xaxisArray.length(); rep++) {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("name", String.valueOf(xaxisArray.get(0)));
                            xaxisList.add(hashMap);

                        }
                    }
                    if (sourceArray != null && sourceArray.length() > 0) {

                        for (int rep = 0; rep < sourceArray.length(); rep++) {
                            HashMap<String, String> hashMap = new HashMap<>();

                            hashMap.put("name", String.valueOf(sourceArray.get(0)));
                            sourceList.add(hashMap);

                        }
                    }
                    if (columnwiseArray != null && columnwiseArray.length() > 0) {
                        for (int rep = 0; rep < columnwiseArray.length(); rep++) {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("name", String.valueOf(columnwiseArray.get(0)));
                            columnWiseList.add(hashMap);

                        }
                    }
                    if (linkRepArray != null && linkRepArray.length() > 0) {

                        for (int rep = 0; rep < linkRepArray.length(); rep++) {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("name", String.valueOf(linkRepArray.get(0)));
                            linkrepdetailsList.add(hashMap);

                        }
                    }
                    if(btnArray!=null && btnArray.length()>0)

                    {
                        button= String.valueOf(btnArray.get(0));
                    }

                }

                if (context instanceof GraphDetailsActivity) {
                    System.out.println("column wise list " + columnWiseList);
                    System.out.println("graph final list "+graphFinalList);
                    ((GraphDetailsActivity) context).sendResponseOfGetGraphDetails(graphFinalList, graphList, repIdList,
                            yTitleList, chartTitleList, xTitleList, xaxisTypeList, xaxisList, sourceList, columnWiseList, linkrepdetailsList,button);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
           /* JSONObject resObject = new JSONObject(result);
            JSONObject res = resObject.getJSONObject("response");
            status = Integer.parseInt(res.optString("status"));


            if (status == 400) {
                Constants.ERROR = 3;
            }*/

           Utils.showAlert(false, Constants.SERVER_ERROR,context, null);
          //  Utils.showAlert(Constants.SERVER_ERROR,context);
           // Constants.ERROR=3;
        }
        catch (Exception e)
        {
            Utils.showAlert(false, Constants.SERVER_ERROR,context, null);
          //  Utils.showAlert(Constants.SERVER_ERROR,context);
        }

    }
    private void parseTableData(String result) {
        TableAysnc asynk = new TableAysnc(result);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            asynk.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            asynk.execute();
        }
    }

    private class TableAysnc extends AsyncTask<String ,String,String>
    {
        String res;
        public TableAysnc(String result) {
            res=result;
            totalTableNewList=new ArrayList<>();
        }



        @Override
        protected String doInBackground(String... strings) {
            try {
                //	oldParsing(res);


                JSONArray mainJsonArray=new JSONArray(res);
                JSONObject mainJsonObject=mainJsonArray.getJSONObject(1);
                recordsTotal=mainJsonObject.optLong("recordsTotal");
                recordsFiltered=mainJsonObject.optLong("recordsFiltered");
                JSONArray jsonArray=mainJsonObject.getJSONArray("data");
                System.out.println("total table new json array "+jsonArray.length());
                for(int i=0;i<jsonArray.length();i++)
                {
                    ArrayList<String> localTableList=new ArrayList<>();
                    //JSONObject dataObj=jsonArray.getJSONObject(i);
                    JSONArray dataArray=jsonArray.getJSONArray(i);
                    for(int j=0;j<dataArray.length();j++)
                    {

                        localTableList.add(String.valueOf(dataArray.get(j)));
                    }
                    System.out.println("total table new local size "+localTableList.size());
                    totalTableNewList.add(localTableList);

                }
                System.out.println("total table new table size "+totalTableNewList.size());
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(res.contains("\\"))
                        {
                            if(context instanceof GraphDetailsActivity)
                            {
                                System.out.println("column wise list "+columnWiseList);
//                                ((GraphDetailsActivity)context).sendSelectedParams(hp);
                                //((GraphDetailsActivity)context).sendResponseOfGetGraphDetails(totalTableList);
                                ((GraphDetailsActivity)context).sendResponseOfTable(totalTableNewList,recordsFiltered,recordsTotal);
                            }
                        }
                        else
                        {
                            //Util.showAlert(CommonUtil.GRAPH_ERROR,context);
                        }

                        //Util.showAlert(CommonUtil.GRAPH_ERROR,context);
                    }
                });

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("datalist in table is "+datalist);
            System.out.println("colnames list in table is "+colNamesList);
            if(context instanceof GraphDetailsActivity)
            {
                System.out.println("column wise list "+columnWiseList);
//                ((GraphDetailsActivity)context).sendSelectedParams(hp);
                ((GraphDetailsActivity)context).sendResponseOfTable(totalTableNewList,recordsFiltered,recordsTotal);
                //	((GraphDetailsActivity)context).sendResponseOfGetGraphDetails(totalTableList);
            }
        }
    }

    private void getMetricGraphResponse(String result) {
        try
        {
            JSONArray jsonArray = new JSONArray(result);
            colNamesList = new ArrayList<>();
            for (int i = 1; i < jsonArray.length(); i++) {
                datalist = new ArrayList<>();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String colname = jsonObject.optString("name");
                colNamesList.add(colname);
                JSONArray data_array = jsonObject.optJSONArray("data");
                for (int j = 0; j < data_array.length(); j++) {
                    HashMap<String, String> datamap = new HashMap<>();
                    JSONObject data_obj = data_array.getJSONObject(j);
                    String name = data_obj.optString("name");
                    String value = data_obj.optString("value");
                    datamap.put("name", name);
                    datamap.put("value", value);
                    datalist.add(datamap);


                }

            }
            if(context instanceof GraphDetailsActivity)
            {
                System.out.println("column wise list "+columnWiseList);
                ((GraphDetailsActivity)context).sendResponseOfMetricGraph(datalist,colNamesList);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
             Utils.showAlert(false, Constants.SERVER_ERROR,context, null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Utils.showAlert(false, Constants.SERVER_ERROR,context, null);
        }
    }

    private void getMultiYAxisResponse(String result) {
try {
    JSONArray tjsonArray = new JSONArray(result);
    JSONObject jsonObject2 = tjsonArray.getJSONObject(1);
    JSONArray commonDataArray = jsonObject2.getJSONArray("commonData");
    JSONObject dataObj = commonDataArray.getJSONObject(0);
    JSONArray dataarray = dataObj.getJSONArray("data");
    pieDataNamesList = new ArrayList<>();


 multiYData = new ArrayList<>();
    for (int i = 0; i < dataarray.length(); i++) {
        JSONArray array = dataarray.getJSONArray(i);
for(int j=0;i<array.length();j++)
{
    HashMap<String, String> datahp = new HashMap<>();
    JSONObject obj0=array.getJSONObject(j);
    String name=obj0.optString("name");
    String y=obj0.optString("y");
    datahp.put("name", name);
    datahp.put("y", y);
    multiYData.add(datahp);
}


        // JSONObject obj1 = piearray.getJSONObject(j);


    }
    if (context instanceof GraphDetailsActivity) {
        System.out.println("donut list " + multiYData);
    //    ((GraphDetailsActivity) context).sendPieGraphResponse(pieDataColumns, pieDataNamesList);
    }
}
catch (JSONException e)
{
    e.printStackTrace();
}
    }

    private void getTableWithPercentResponse(String result) {
        try {
            JSONArray tjsonArray = new JSONArray(result);
            colNamesList = new ArrayList<>();
            tableDataList = new ArrayList<>();

            tableAllColumsDataList = new ArrayList<>();
            JSONObject jsonObject = tjsonArray.getJSONObject(1);
            JSONArray colArray = jsonObject.getJSONArray("columns");
            for (int i = 0; i < colArray.length(); i++) {
                JSONObject colArrayJsonObject = colArray.getJSONObject(i);
                colNamesList.add(colArrayJsonObject.optString("data"));
            }
            System.out.println("table percent collist " + colNamesList);
            JSONObject dataJsonObject = tjsonArray.getJSONObject(2);
            JSONArray jsonDataArray = dataJsonObject.getJSONArray("data");
            for (int i = 0; i < jsonDataArray.length(); i++) {
                JSONObject jsonObject1 = jsonDataArray.getJSONObject(i);
                JSONArray dataArray = jsonObject1.getJSONArray("data");
                tablePercentDataList = new ArrayList<>();

                for (int j = 0; j < dataArray.length(); j++) {
                    JSONObject dataObject = dataArray.getJSONObject(j);
                    HashMap<String, String> dataHp = new HashMap<>();
                       /* if (j == 0) {
                            tablePercentDataList=new ArrayList<>();
                            dataHp.put("name", dataObject.optString("name"));
                            dataHp.put("y", dataObject.optString("y"));
                            tableDataList.add(dataHp);
                        } else  {*/
                    dataHp.put("name", dataObject.optString("name"));
                    dataHp.put("y", dataObject.optString("y"));
                    dataHp.put("percent", dataObject.optString("percent"));
                    tablePercentDataList.add(dataHp);
                    // }


                }
                tableAllColumsDataList.add(tablePercentDataList);
            }


            JSONObject totalObj = tjsonArray.getJSONObject(3);
            JSONArray totalArray = totalObj.getJSONArray("data");
            JSONObject totalCountObj = totalArray.getJSONObject(0);
            totalcount = totalCountObj.optString("totalCount");


            System.out.println("table percent data " + tablePercentDataList);
            System.out.println("table alll colummns " + tableAllColumsDataList);


        if (context instanceof GraphDetailsActivity) {
            System.out.println("column wise list " + columnWiseList);
            ((GraphDetailsActivity) context).sendResponseOfTableWithPercent(colNamesList, tableDataList, tablePercentDataList, totalcount, tableAllColumsDataList);
        }
    }

catch (JSONException e)
    {
        e.printStackTrace();
         Utils.showAlert(false, Constants.SERVER_ERROR,context, null);
    }
catch (Exception e)
    {
        e.printStackTrace();
        Utils.showAlert(false, Constants.SERVER_ERROR,context, null);
    }
}

    private void getPieGraphResponse(String result) {
try {


    JSONArray tjsonArray = new JSONArray(result);
    JSONObject jsonObject2 = tjsonArray.getJSONObject(1);
    JSONArray commonDataArray = jsonObject2.getJSONArray("commonData");
    JSONObject dataObj = commonDataArray.getJSONObject(0);

    JSONArray piedataarray = dataObj.getJSONArray("data");
    pieDataNamesList = new ArrayList<>();
    for (int i = 0; i < piedataarray.length(); i++) {
        JSONObject jsonObject3 = piedataarray.getJSONObject(i);
        HashMap<String, String> datehp = new HashMap<>();
        datehp.put("name", jsonObject3.optString("name"));
        pieDataNamesList.add(datehp);
        JSONArray piearray = jsonObject3.getJSONArray("data");
        pieDataColumns = new ArrayList<>();
        for (int j = 0; j < piearray.length(); j++) {
            JSONObject obj1 = piearray.getJSONObject(j);
            HashMap<String, String> datahp = new HashMap<>();
            datahp.put("name", obj1.optString("name"));
            datahp.put("y", obj1.optString("y"));
            datahp.put("count", obj1.optString("count"));
            pieDataColumns.add(datahp);

        }
    }
    if (context instanceof GraphDetailsActivity) {
        ((GraphDetailsActivity) context).sendPieGraphResponse(pieDataColumns, pieDataNamesList);
    }
}

catch (JSONException e)
{
    e.printStackTrace();
     Utils.showAlert(false, Constants.SERVER_ERROR,context, null);
}
catch (Exception e)
{
    e.printStackTrace();
    Utils.showAlert(false, Constants.SERVER_ERROR,context, null);
}
    }

    private void getDonutResponse(String result) {
        try {
            JSONArray tjsonArray = new JSONArray(result);
            JSONObject jsonObject2 = tjsonArray.getJSONObject(1);
            JSONArray commonDataArray = jsonObject2.getJSONArray("commonData");
            JSONObject dataObj = commonDataArray.getJSONObject(0);
            JSONArray donutdataarray = dataObj.getJSONArray("data");
            pieDataNamesList = new ArrayList<>();


            pieDataColumns = new ArrayList<>();
            for (int i = 0; i < donutdataarray.length(); i++) {
                JSONArray array = donutdataarray.getJSONArray(i);


                HashMap<String, String> datahp = new HashMap<>();
                datahp.put("name", array.optString(0));
                datahp.put("y", array.optString(1));
                pieDataColumns.add(datahp);
                System.out.println("donut print " + pieDataColumns);

                // JSONObject obj1 = piearray.getJSONObject(j);


            }
            if (context instanceof GraphDetailsActivity) {
                System.out.println("donut list " + pieDataColumns);
                ((GraphDetailsActivity) context).sendPieGraphResponse(pieDataColumns, pieDataNamesList);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
             Utils.showAlert(false, Constants.SERVER_ERROR,context, null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Utils.showAlert(false, Constants.SERVER_ERROR,context, null);
        }
    }

    private void clearPieLists() {

        CommonUtil.tableFieldsList.clear();
        CommonUtil.tableValuesList.clear();
        CommonUtil.pieDateRange.clear();
        CommonUtil.pieColumns.clear();
        CommonUtil.pietablewithcolumns.clear();
    }

    private void init() {


        graphFinalList=new ArrayList<>();
        graphList=new ArrayList<>();
        repIdList=new ArrayList<>();
        yTitleList=new ArrayList<>();
        chartTitleList=new ArrayList<>();
        xTitleList=new ArrayList<>();
        xaxisTypeList=new ArrayList<>();
        xaxisList=new ArrayList<>();
        sourceList=new ArrayList<>();
        columnWiseList=new ArrayList<>();
        linkrepdetailsList=new ArrayList<>();


    }

    private void parseGetGraphDetailsData(String result) {
        CommonUtil.trend.clear();
        ArrayList<HashMap<String, String>> localgraphTypes = new ArrayList<>();
        ArrayList<HashMap<String, String>> columnList = new ArrayList<>();
        ArrayList<HashMap<String, String>> relativeList = new ArrayList<>();
        ArrayList<String> groupingList = new ArrayList<>();
        ArrayList<ArrayList<HashMap<String, String>>> subgroupingList = new ArrayList<>();
        ArrayList<HashMap<String, String>> trendList = new ArrayList<>();
        ArrayList<HashMap<String, String>> matrixList = new ArrayList<>();
        ArrayList<HashMap<String,Object>> filterList = new ArrayList<>();
        ArrayList<HashMap<String, String>> filterData = new ArrayList<>();
        ArrayList<HashMap<String, String>> trend = new ArrayList<>();
        ArrayList<HashMap<String,ArrayList<HashMap<String,String>>>> groupWithSubgrpList=new ArrayList<>();
        String groupName=null;

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray graphDetailsArray = jsonObject.getJSONArray("graphDetails");

            HashMap<String, String> graphHashmap = new HashMap<>();
            int i = 0;
            JSONObject obj = graphDetailsArray.getJSONObject(0);
            /*--------------------------------------columns---------------------------------------------*/

            JSONArray colArray = obj.optJSONArray("columns");
            if (colArray != null) {
                for (int col = 0; col < colArray.length(); col++) {
                    HashMap<String, String> columnHashmap = new HashMap<>();
                    JSONObject colobj = colArray.getJSONObject(col);
                    columnHashmap.put("id", String.valueOf(colobj.optString("id")));
                    columnHashmap.put("name", String.valueOf(colobj.optString("name")));
                    columnHashmap.put("columnType", String.valueOf(colobj.optString("columnType")));
                    columnList.add(columnHashmap);

                }


                System.out.println("new column list is" + columnList);
            }
            /*--------------------------------------graphTypes---------------------------------------------*/
            JSONObject obj1 = graphDetailsArray.getJSONObject(1);

            JSONArray graphTypeArray = obj1.optJSONArray("graphType");
            if (graphTypeArray != null) {
                for (int graphNum = 0; graphNum < graphTypeArray.length(); graphNum++) {
                    HashMap<String, String> graphNameHashmap = new HashMap<>();
                    JSONObject graphTypeObj = graphTypeArray.getJSONObject(graphNum);
                    graphNameHashmap.put("name", String.valueOf(graphTypeObj.optString("name")));
                    localgraphTypes.add(graphNameHashmap);
                }
                System.out.println("new graphtype list is" + localgraphTypes);
            }



            /*--------------------------------------relative---------------------------------------------*/
            JSONObject obj2 = graphDetailsArray.getJSONObject(2);
            JSONArray relativeArray = obj2.getJSONArray("relative");
            if (relativeArray != null) {
                for (int relNum = 0; relNum < relativeArray.length(); relNum++) {
                    HashMap<String, String> relativeHashmap = new HashMap<>();
                    JSONObject graphobj = relativeArray.getJSONObject(relNum);
                    relativeHashmap.put("name", String.valueOf(graphobj.optString("name")));
                    relativeList.add(relativeHashmap);

                }
                System.out.println("new relative list is" + relativeList);
            }

            /*--------------------------------------grouping-type---------------------------------------------*/

            JSONObject obj3 = graphDetailsArray.getJSONObject(3);
            JSONArray groupingTypeArray = obj3.optJSONArray("groupingType");
            if (graphTypeArray != null) {
                System.out.println("grouping type array length " + graphTypeArray.length());
                for (int groupNum = 0; groupNum < groupingTypeArray.length(); groupNum++) {


                    HashMap<String, String> groupingHashmap = new HashMap<>();
                    JSONObject grpobj = groupingTypeArray.getJSONObject(groupNum);
                    groupName = String.valueOf(grpobj.optString("name"));
                    groupingHashmap.put("name", String.valueOf(grpobj.optString("name")));
                    groupingList.add(String.valueOf(grpobj.optString("name")));
                    System.out.println("new grp list is  in loop" + groupNum + groupingList);



                    /*--------------------------------------sub-grouping type---------------------------------------------*/
                    JSONArray groupingSubTypeArray = grpobj.optJSONArray("groupingSubType");
                    HashMap<String, ArrayList<HashMap<String, String>>> grphp = new HashMap<>();
                    if (groupingSubTypeArray != null) {
                        ArrayList<HashMap<String, String>> locallist = new ArrayList<>();
                        ArrayList<HashMap<String, String>> subgroupNames = new ArrayList<>();

                        for (int groupSubNum = 0; groupSubNum < groupingSubTypeArray.length(); groupSubNum++) {
                            HashMap<String, String> groupingSubHashmap = new HashMap<>();


                            JSONObject grpsubobj = groupingSubTypeArray.optJSONObject(groupSubNum);
                            //JSONObject optionObj=groupingSubTypeArray.optJSONObject(1);

                            groupingSubHashmap.put("xaxis", String.valueOf(grpsubobj.optString("xaxis")));
                            groupingSubHashmap.put("option", String.valueOf(grpsubobj.optString("option")));
                            subgroupNames.add(groupingSubHashmap);


                            locallist.add(groupingSubHashmap);


                            System.out.println("new sub grp list in loop " + subgroupNames);

                        }
                        grphp.put(groupName, subgroupNames);
                        groupWithSubgrpList.add(grphp);
                        System.out.println("new group with subgrp list " + groupWithSubgrpList);
                        if (locallist != null && locallist.size() > 0) {
                            subgroupingList.add(locallist);
                        }
                    }
                }
                System.out.println("new grp list is" + groupingList);
                System.out.println("new sub grp list is" + subgroupingList);
            }



            /*--------------------------------------trending---------------------------------------------*/
            JSONObject obj4 = graphDetailsArray.getJSONObject(4);
            JSONArray trendingArray = obj4.optJSONArray("trending");
            if (trendingArray != null) {
                for (int trendNum = 0; trendNum < trendingArray.length(); trendNum++) {
                    HashMap<String, String> trendingHashmap = new HashMap<>();
                    JSONObject trendobj = trendingArray.getJSONObject(trendNum);
                    trendingHashmap.put("name", String.valueOf(trendobj.optString("name")));
                    CommonUtil.trend.add(String.valueOf(trendobj.optString("name")));
                    trendList.add(trendingHashmap);

                }
                System.out.println("new trend list is" + trendList);
            }


            /*--------------------------------------matrix---------------------------------------------*/
            JSONObject obj5 = graphDetailsArray.optJSONObject(5);
            if (obj5 != null) {
                JSONArray matrixArray = obj5.optJSONArray("matrix");
                if (matrixArray != null) {
                    for (int Num = 0; Num < matrixArray.length(); Num++) {
                        HashMap<String, String> matrixHashmap = new HashMap<>();
                        JSONObject trendobj = matrixArray.getJSONObject(i);
                        matrixHashmap.put("name", String.valueOf(trendobj.optString("name")));

                        matrixList.add(matrixHashmap);
                    }
                    System.out.println("new matrix list is" + matrixList);
                }
            }



            /*--------------------------------------filter---------------------------------------------*/
            JSONObject obj6 = graphDetailsArray.optJSONObject(6);
            if (obj6 != null)
            {
                JSONArray filterArray = obj6.optJSONArray("filterData");
          filter = new ArrayList<>();

            if (filterArray != null) {
                JSONObject filterobj = null;
                for (int filterNum = 0; filterNum < filterArray.length(); filterNum++) {
                    HashMap<String, Object> filterHashmap = new HashMap<>();
                    filterData = new ArrayList<>();
                    filterobj = filterArray.getJSONObject(filterNum);
                    filterHashmap.put("label", String.valueOf(filterobj.optString("label")));
                    filterHashmap.put("filterType", String.valueOf(filterobj.optString("filterType")));


                    /*--------------------------------------filter-data---------------------------------------------*/

                    JSONArray dataArray = filterobj.optJSONArray("data");
                    if (dataArray != null) {
                        // filterHashmap.put("data", dataArray);

                        System.out.println("new filter length " + dataArray.length());
                        for (int k = 0; k < dataArray.length(); k++) {
                            JSONObject dataobj = dataArray.getJSONObject(k);
                            HashMap<String, String> datahashmap = new HashMap<>();
                            datahashmap.put("id", String.valueOf(dataobj.optString("id")));
                            datahashmap.put("name", String.valueOf(dataobj.optString("name")));

                            filterData.add(datahashmap);
                        }
                        filter.add(filterData);
                        filterHashmap.put("data", filterData);
                    }

                    filterList.add(filterHashmap);
                }
                System.out.println("new filter list is" + filterList);
                System.out.println("new filter data list is" + filterData);
            }
        }

            if(context instanceof GraphDetailsActivity)
            {
                ((GraphDetailsActivity)context).getResponseOfGraphDetailsRequest(localgraphTypes, columnList, relativeList, groupingList,
                        subgroupingList, trendList, matrixList, filterList, filter,groupWithSubgrpList);
            }

        } catch (JSONException e) {

            e.printStackTrace();
            Utils.showAlert(false, Constants.SERVER_ERROR,context, null);
           // Constants.ERROR=3;
        }
    }

    private void parseGetDashboardData(String result) {

        if(CommonUtil.newDashboardList!=null && CommonUtil.newDashboardList.size()>0)
        {
            CommonUtil.newDashboardList.clear();
        }
        if(CommonUtil.newGraphdataList!=null && CommonUtil.newGraphdataList.size()>0)
        {
            CommonUtil.newGraphdataList.clear();
        }
        CommonUtil.mappgraphlist.clear();





        try {
            JSONObject jsonObject=new JSONObject(result);
            JSONArray dashboardArray=jsonObject.getJSONArray("dashboardData");
            for(int i=0;i<dashboardArray.length();i++)
            {
                HashMap<String,String> hashMap=new HashMap<>();
                JSONObject obj = dashboardArray.getJSONObject(i);
                String dbid=String.valueOf(obj.optInt("dbId"));
                String dbName=String.valueOf(obj.optString("dbName"));
                hashMap.put("dbId", dbid);
                hashMap.put("dbName",dbName);
                CommonUtil.newDashboardList.add(hashMap);
                DB_handler db_handler=new DB_handler(context);
                db_handler.open();
                Cursor cursor = db_handler.selectDashboardsData();
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        dbIdList.add(cursor.getString(cursor.getColumnIndex("dbId")));
                    }
                }
                cursor.close();
                if(dbIdList.contains(String.valueOf(obj.optInt("dbId"))))
                {
                    db_handler.upDateDashboardData(dbid,dbName);
                    System.out.println("updating called");
                }
                else
                {
                    db_handler.insertDashboardsNames(dbid,dbName);
                    System.out.println("insertion called");
                }


                JSONArray graphDataArray=obj.getJSONArray("graphData");
                ArrayList<HashMap<String,String>> local=new ArrayList<>();
                for(int j=0;j<graphDataArray.length();j++)
                {

                    HashMap<String,String> graphHashMap=new HashMap<>();
                    JSONObject graphobj=graphDataArray.getJSONObject(j);
                    String graphId=String.valueOf(graphobj.optInt("graphId"));
                    String graphName=String.valueOf(graphobj.optString("graphName"));
                    graphHashMap.put("graphId",String.valueOf(graphobj.optInt("graphId")));
                    graphHashMap.put("graphName",String.valueOf(graphobj.optString("graphName")));
                    CommonUtil.newGraphdataList.add(graphHashMap);

                   /* Cursor graphCursor=db_handler.getGraphSList();
                    if(graphCursor.getCount()>0)
                    {
                        while (graphCursor.getCount()>0)
                        {
                            graphIdList.add(cursor.getString(cursor.getColumnIndex("graphId")));
                        }
                    }
                    graphCursor.close();

                    if(graphIdList.contains(String.valueOf(obj.optInt("graphId"))))
                    {
                        db_handler.updateGraphData(graphId,graphName);
                    }
                    else
                    {
                        db_handler.insertGraphsNames(graphId,graphName);
                    }
                    db_handler.close();

*/

                    local.add(graphHashMap);
                    System.out.println("new local"+local);




                }
                if(local!=null && local.size()>0) {
                    CommonUtil.mappgraphlist.add(local);

                    System.out.println("  new final mapgraphlist " + CommonUtil.mappgraphlist.get(i));
                }






            }

            System.out.println("new dashboard "+CommonUtil.newDashboardList);
            System.out.println("new grahph "+CommonUtil.newGraphdataList);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseDataAccordingToDrillDown(String result) {
        if (CommonUtil.drillrepdata != null) {
            CommonUtil.drillrepdata.clear();
        }

        try {
            JSONObject object= new JSONObject(result);

            if (!object.isNull("Report Data")) {
                JSONArray cellDataArray = object.getJSONArray("Report Data");
                for (int i = 0; i < cellDataArray.length(); i++) {
                    HashMap<String, String> hmap = new HashMap<String, String>();
                    JSONObject obj = cellDataArray.getJSONObject(i);

                    hmap.put("cellid", String.valueOf(obj.getInt("cellId")));
                    hmap.put("rowcol", obj.getString("RowCol_Names"));
                    hmap.put("repid", String.valueOf(obj.getInt("repId")));
                    hmap.put("rowNo", String.valueOf(obj.getInt("rowNo")));
                    hmap.put("colNo", String.valueOf(obj.getInt("colNo")));
                    hmap.put("celldata", obj.getString("CellData"));
                    hmap.put("srt", obj.getString("srt"));
                    hmap.put("atype", obj.getString("atype"));
                    hmap.put("ctype", obj.getString("ctype"));
                    hmap.put("aim", obj.getString("aim"));
                    hmap.put("trendupdatestatus", obj.getString("trendUpdateStatus"));
                    CommonUtil.drillrepdata.add(hmap);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseGraphData(String result) {
        CommonUtil.list_dashboardName.clear();
        CommonUtil.listDbChart.clear();
        CommonUtil.graphdata.clear();

        try
        {

             /* [{"DbChart":[{"name":"Mview",
                    "Mview":[{"userDbId":"236","repId":"2638",
                    "dbId":"31","srt":"Daily","chartName":"Network Signalstrength",
                    "dbType":"Bar"}]}],"GraphData":[{"name":"Date","value":"2019-03-08"},{"name":"Signalstrength","value":"-99"},
                    {"name":"Date","value":"2019-03-08"},{"name":"Signalstrength","value":"-82"},
                    {"name":"Date","value":"2019-03-07"},{"name":"Signalstrength","value":"-103"},
                    {"name":"Date","value":"2019-03-07"},{"name":"Signalstrength","value":"-101"},
                    {"name":"Date","value":"2019-03-07"},{"name":"Signalstrength","value":"-100"},
                    {"repId":"2638","srt":"Daily","range":"default","chartTitle":"Mview > Network Signalstrength","xtitle":"date","ytitle": "Unit" }]}]
*/
            JSONObject jsonObject=new JSONObject(result);
            JSONArray dbChartArray = jsonObject.getJSONArray("DbChart");
            JSONArray graphDataArray = jsonObject.getJSONArray("GraphData");
            for (int iDbChart = 0; iDbChart < dbChartArray.length(); iDbChart++) {
                HashMap<String,String> nameHashMap=new HashMap<>();

                JSONObject dbChartObj = dbChartArray.getJSONObject(0);
                nameHashMap.put("name",dbChartObj.optString("name"));
                CommonUtil.listDbChart.add(nameHashMap);

                JSONArray nameArray=dbChartObj.getJSONArray(CommonUtil.listDbChart.get(iDbChart).get("name"));
                for (int iName = 0; iName < nameArray.length(); iName++) {
                    HashMap<String,String> hash=new HashMap<>();
                    JSONObject dashboardNameObj = nameArray.getJSONObject(iName);
                    hash.put("userDbId", String.valueOf(dashboardNameObj.optInt("userDbId")));
                    hash.put("repId",String.valueOf(dashboardNameObj.optInt("repId")));
                    hash.put("dbId",String.valueOf(dashboardNameObj.optInt("dbId")));
                    hash.put("srt",dashboardNameObj.optString("srt"));
                    hash.put("chartName",dashboardNameObj.optString("chartName"));
                    hash.put("dbType",dashboardNameObj.optString("dbType"));
                    CommonUtil.list_dashboardName.add(hash);
                    System.out.println("response hash "+CommonUtil.list_dashboardName.get(iName));

                }
                for (int iGraphData = 0; iGraphData < graphDataArray.length() - 1; iGraphData++) {
                    HashMap<String,String> graphHash=new HashMap<>();
                    JSONObject graphDataObj = graphDataArray.getJSONObject(iGraphData);
                    graphHash.put("name",graphDataObj.optString("name"));
                    graphHash.put("value",graphDataObj.optString("value"));

                    CommonUtil.graphdata.add(graphHash);


                }

            }
            System.out.println("response graph data "+CommonUtil.graphdata);
            System.out.println("respone dashboard name "+CommonUtil.list_dashboardName);
            System.out.println("response dbname "+CommonUtil.listDbChart);



        }
        catch (JSONException e)
        {
            Constants.ERROR=3;
            e.printStackTrace();
            System.out.println("Result of jsonexception in dashboard "+e.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Result of exception in dashboard "+e.toString());
        }
    }

    private void parseInitData(String result) {
        try {
            JSONObject object = new JSONObject(result);

            /************ Nework, Project and Report Array **************/
            //  JSONArray networkArray = object.getJSONArray("Network");
            //JSONArray projectArray = object.getJSONArray("Project");
            JSONArray reportArray = object.getJSONArray("Report");

            /************ Nework, Project and Report Array **************/
            //  JSONArray netProjArray = object.getJSONArray("Network_Project_Map");
            //JSONArray projRepArray = object.getJSONArray("Project_Report_Map");
            JSONArray dashboardArray = object.getJSONArray("Dashboard");
            JSONArray dbChartArray = object.getJSONArray("DbChart");
            //     JSONArray graphdataArray = object.getJSONArray("GraphData");


            if(CommonUtil.repList!=null)
            {
                CommonUtil.repList.clear();
            }
            for(int i = 0; i < reportArray.length(); i++)
            {
                JSONObject obj = reportArray.getJSONObject(i);
                HashMap<String,String> hashMap=new HashMap<>();

                String repId=  obj.optString("repId");
                String repType=  obj.optString("repType");
                String repName =obj.optString("repName");
                String minAcnt= String.valueOf(obj.optInt("min_acnt"));
                String max_acnt= String.valueOf(obj.optInt("max_acnt"));
                String trendUpdateStatus= obj.getString("trendUpdateStatus");



                hashMap.put("repId",repId);
                hashMap.put("repType",repType);
                hashMap.put("repName",repName);
                hashMap.put("min_acnt",minAcnt);
                hashMap.put("max_acnt",max_acnt);
                hashMap.put("trendUpdateStatus",trendUpdateStatus);
                CommonUtil.repList.add(hashMap);

            }




            ///for dashboards
            if(CommonUtil.dashboardList!=null)
            {
                CommonUtil.dashboardList.clear();
            }

            for(int i=0;i<dashboardArray.length();i++)
            {
                JSONObject obj=dashboardArray.getJSONObject(i);
                HashMap<String,String> hashMap=new HashMap<>();
                hashMap.put("dbId",obj.optString("dbId"));
                hashMap.put("dbName",obj.optString("dbName"));
                CommonUtil.dashboardList.add(hashMap);


            }
            System.out.println("response of dashboards" + CommonUtil.dashboardList);




            /////
            for (int iDbChart = 0; iDbChart < dbChartArray.length(); iDbChart++) {
                HashMap<String,String> nameHashMap=new HashMap<>();

                JSONObject dbChartObj = dbChartArray.getJSONObject(0);
                nameHashMap.put("name",dbChartObj.optString("name"));
                CommonUtil.listDbChart.add(nameHashMap);

                JSONArray nameArray=dbChartObj.getJSONArray(CommonUtil.listDbChart.get(iDbChart).get("name"));
                for (int iName = 0; iName < nameArray.length(); iName++) {
                    HashMap<String,String> hash=new HashMap<>();
                    JSONObject dashboardNameObj = nameArray.getJSONObject(iName);
                    hash.put("userDbId", String.valueOf(dashboardNameObj.optInt("userDbId")));
                    hash.put("repId",String.valueOf(dashboardNameObj.optInt("repId")));
                    hash.put("dbId",String.valueOf(dashboardNameObj.optInt("dbId")));
                    hash.put("srt",dashboardNameObj.optString("srt"));
                    hash.put("chartName",dashboardNameObj.optString("chartName"));
                    hash.put("dbType",dashboardNameObj.optString("dbType"));
                    CommonUtil.list_dashboardName.add(hash);
                    System.out.println("response hash "+CommonUtil.list_dashboardName.get(iName));

                }


            }

        }
        catch (JSONException e)
        {
            Constants.ERROR=3;
            System.out.println("response json exception in parse init "+e.toString());
        }
        catch (Exception e)
        {
            System.out.println("response exception in parse init "+e.toString());
        }
    }

    // by swapnil 10/03/2022 for round robin

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void parseImupData(final String result) {
        try {
            if (result != null) {
                parseGetImupData(result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Constants.ERROR = 3;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void parseGetImupData(String result) throws JSONException {
        try {
            JSONObject object = new JSONObject(result);
            status = object.optString("status");
            manual_scan_flag = object.optString("manual_scan_flag");
            System.out.println("manual_scan_flag is "+manual_scan_flag);
            schedule_scan_flag = object.optString("schedule_scan_flag");
            agent_new_details = object.optString("agent_modification_flag");
            updateFlag=object.optString("update_flag");
            System.out.println(" update flag is"+updateFlag);

            if (status.equalsIgnoreCase("0")) {
                if(updateFlag!=null)
                {
                    if(updateFlag.equals("1"))
                    {
                        RequestResponse.sendUpdateDataRequest(context);
                    }
                }
                if (agent_new_details != null){
                    if(agent_new_details.equals("1"))
                    {
                        Utils.appendLog("ELOG_GAGD_FLAG: Sending GAGD request");
                        RequestResponse.sendGagdRequest();
                    }
                }
                JSONArray array = object.optJSONArray("agent_change_details");

//                if (!Utils.checkifnull(manual_scan_flag) && manual_scan_flag.equalsIgnoreCase("1")) {
//                    System.out.println(" entering manual_scan_flag ");
//                    RequestResponse.get_gss_request(context);
//
//                }
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.optJSONObject(i);
                        device_sno = jsonObject.optString("device_sno");
                        file_name = jsonObject.optString("jar_file_name");
                        System.out.println(" the current status of file_name is"+file_name);
                        //Utils.appendLog("the current status of file_name is"+file_name);
                        file_size = jsonObject.optString("jar_file_size");
                        file_cksum = jsonObject.optString("jar_file_checksum");
                        jar_path = jsonObject.optString("jar_path");
                        class_name = jsonObject.optString("class_name");
                        method_name = jsonObject.optString("method_name");
                        ssdt = jsonObject.optString("schedule_start_time");
                        sedt = jsonObject.optString("schedule_end_time");
                        period = jsonObject.optString("period");
                        System.out.println(" the period is"+period);
                        data=jsonObject.optString("data");
                        System.out.println(" the data"+data);
                        num_of_times = jsonObject.optString("number_of_iteration");
                        agent_version = jsonObject.optString("agent_version");
                        type = jsonObject.optString("type");
                        frequnecy = jsonObject.optString("frequency");
                        System.out.println(" the frequency is"+frequnecy);
                        currentstatusofserver = jsonObject.optString("status_config");
                        System.out.println(" the current status of server is"+currentstatusofserver);
                        statusOfDownload = jsonObject.optString("status");
                        System.out.println(" status of downlaod is "+status);
                        priority = jsonObject.optString("priority");
                        task_type = jsonObject.optString("task_type");
                        System.out.println(" : via parseandsaveimupresponseindb status of Download is " + statusOfDownload);
                        System.out.println(" : entering in table ");
                        DB_handler db_handler = null;
                        db_handler = new DB_handler(context);
                        db_handler.open();
                        boolean exists = db_handler.checkIfRecordExists(file_name);
                        Log.d(TAG, "return exists is : "+exists);

                        // Utils.appendLog("status of download is"+statusOfDownload);
                        if(!exists) {
                                Utils.appendLog("ELOG_INSERT_AGENT: insertion called");
                                db_handler.open();
                                db_handler.insertInSchedulerAgentTable(device_sno, file_name, file_size, file_size, jar_path, class_name, method_name, ssdt, sedt, period, num_of_times, "", "", "", "", "completed", "", "type", "", frequnecy, "", "", data, "ready", priority, task_type, Utils.getDateTime());
                                db_handler.close();
                               // Utils.appendLog("long result insertInSchedulerAgentTable one is" + result1);

                        }else{

                            db_handler.updateAgentDetails(device_sno, file_name, file_size, file_cksum, jar_path,
                                    class_name, method_name, ssdt,
                                    sedt, period, num_of_times,"",
                                    "", " ", "",
                                    agent_version,type,
                                    frequnecy,priority,task_type);

                            Log.i("LogFapps", "status_config is " + currentstatusofserver);
                            if (currentstatusofserver.equalsIgnoreCase("active")) {
                                db_handler.updateStatusOfJarToDownloadStart(file_name, "completed");

                            } else {
                                db_handler.updateStatusOfJarToDownloadStart(file_name, "inactive");
                            }
                        }

                        db_handler.close();

                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.i("LogFapps", "schedule test after 15 minutes ");
                    MyJobService.scheduleTest();

                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void parseNewImupData(final String result) {
        try {
            if (result != null) {
                parseGetNewImupData(result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Constants.ERROR = 3;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void parseGetNewImupData(String result) throws JSONException {
        try {
            JSONObject object = new JSONObject(result);
            status = object.optString("status");
            manual_scan_flag = object.optString("manual_scan_flag");
            System.out.println("manual_scan_flag is "+manual_scan_flag);
            schedule_scan_flag = object.optString("schedule_scan_flag");
            agent_new_details = object.optString("agent_modification_flag");
            updateFlag=object.optString("update_flag");
            System.out.println(" update flag is"+updateFlag);

            if (status.equalsIgnoreCase("0")) {
                if(updateFlag!=null)
                {
                    if(updateFlag.equals("1"))
                    {
                        RequestResponse.sendUpdateDataRequest(context);
                    }
                }
                if (agent_new_details != null){
                    if(agent_new_details.equals("1"))
                    {
                        Utils.appendLog("ELOG_GAGD_FLAG: Sending GAGD request");
                        RequestResponse.sendGagdRequest();
                    }
                }
                JSONArray array = object.optJSONArray("agent_change_details");

//                if (!Utils.checkifnull(manual_scan_flag) && manual_scan_flag.equalsIgnoreCase("1")) {
//                    System.out.println(" entering manual_scan_flag ");
//                    RequestResponse.get_gss_request(context);
//
//                }
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.optJSONObject(i);
                        device_sno = jsonObject.optString("device_sno");
                        file_name = jsonObject.optString("jar_file_name");
                        System.out.println(" the current status of file_name is"+file_name);
                        //Utils.appendLog("the current status of file_name is"+file_name);
                        file_size = jsonObject.optString("jar_file_size");
                        file_cksum = jsonObject.optString("jar_file_checksum");
                        jar_path = jsonObject.optString("jar_path");
                        class_name = jsonObject.optString("class_name");
                        method_name = jsonObject.optString("method_name");
                        ssdt = jsonObject.optString("schedule_start_time");
                        sedt = jsonObject.optString("schedule_end_time");
                        period = jsonObject.optString("period");
                        System.out.println(" the period is"+period);
                        data=jsonObject.optString("data");
                        System.out.println(" the data"+data);
                        num_of_times = jsonObject.optString("number_of_iteration");
                        agent_version = jsonObject.optString("agent_version");
                        type = jsonObject.optString("type");
                        frequnecy = jsonObject.optString("frequency");
                        System.out.println(" the frequency is"+frequnecy);
                        currentstatusofserver = jsonObject.optString("status_config");
                        System.out.println(" the current status of server is"+currentstatusofserver);
                        statusOfDownload = jsonObject.optString("status");
                        System.out.println(" status of downlaod is "+status);
                        priority = jsonObject.optString("priority");
                        task_type = jsonObject.optString("task_type");
                        System.out.println(" : via parseandsaveimupresponseindb status of Download is " + statusOfDownload);
                        System.out.println(" : entering in table ");
                        DB_handler db_handler = null;
                        db_handler = new DB_handler(context);
                        db_handler.open();
                        boolean exists = db_handler.checkIfRecordExists(file_name);
                        Log.d(TAG, "return exists is : "+exists);

                        // Utils.appendLog("status of download is"+statusOfDownload);
                        if(!exists) {

                            System.out.println("insertion called");
                            db_handler.open();
                            db_handler.insertInSchedulerAgentTable(device_sno, file_name, file_size, file_size, jar_path, class_name, method_name, ssdt, sedt, period, num_of_times, "", "", "", "", "completed", "", "type", "", frequnecy, "", "", data, "ready", priority, task_type, Utils.getDateTime());
                            db_handler.close();
                            // Utils.appendLog("long result insertInSchedulerAgentTable one is" + result1);

                        }else{
                            Log.i("LogFapps", "status_config is " + currentstatusofserver);
                            if (currentstatusofserver.equalsIgnoreCase("active")) {
                                db_handler.updateStatusOfJarToDownloadStart(file_name, "completed");

                            } else {
                                db_handler.updateStatusOfJarToDownloadStart(file_name, "inactive");
                            }
                        }



                        db_handler.close();

                    }
                }
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    Log.i("LogFapps", "schedule test after 15 minutes ");
//                    MyJobService.scheduleTest();
//
//                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public void parseevt(String response, String evtType, String rowid) {
        try {

            JSONObject jsonObject = new JSONObject(response);
            String status= jsonObject.optString("status");

                    if (status.equals("0")) {
                        DB_handler db_helper = null;
                        db_helper = new DB_handler(MviewApplication.ctx);

                        db_helper.open();

                        Log.d(Mview.TAG, "parseevt: updateStatusOfAgentData: "+evtType);
                        db_helper.updateStatusOfAgentsEvents("completed",evtType,rowid);

                        db_helper.close();


                    }
                    else if (status.equalsIgnoreCase("404"))
                    {
                        DB_handler db_helper = null;
                        db_helper = new DB_handler(MviewApplication.ctx);
                        Utils.appendLog("ELOG_PARSE_EVT_404: evt_type is "+evtType);

                        db_helper.open();
                        db_helper.updateStatusOfAgentsEvents("init",evtType,rowid);

                        db_helper.close();
                    }



        } catch (Exception e)
        {
            DB_handler db_helper = null;
            db_helper = new DB_handler(MviewApplication.ctx);

            db_helper.open();

           Utils.appendLog("ELOG_PARSE_EVT_EXCEPTION: evt_type is "+evtType+"  Exception is"+e.getMessage());
            db_helper.updateStatusOfAgentsEvents("init",evtType,rowid);

            db_helper.close();
            e.printStackTrace();

        }


    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void parseGagdData(final String result) {
        try {
            if (result != null) {
                parseAndInsertGagdData(result, MviewApplication.ctx);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void parseAndInsertGagdData(String result, Context context) {
        try {
            JSONObject object = new JSONObject(result);
            String status = object.optString("status");

            if (status.equalsIgnoreCase("0")) {
                JSONArray array = object.optJSONArray("agent_details");



                if (array != null && array.length() > 0) {


                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.optJSONObject(i);


                        String id = jsonObject.optString("id");
                        String url = jsonObject.optString("url");
                        String senderNum = jsonObject.optString("sender_num");
                        String agent_name = jsonObject.optString("agent_name");
                        String event_type = jsonObject.optString("event_type");
                        String user_type = jsonObject.optString("user_type");
                        String packet_size = jsonObject.optString("packet_size");
                        String no_of_packets = jsonObject.optString("no_of_packets");
                        String rule = jsonObject.optString("rule");
                        String mobile = jsonObject.optString("mobile");
                        String third_party_mobile = jsonObject.optString("third_party_mobile");

                        DB_handler db_handler = new DB_handler(MviewApplication.ctx);
                        db_handler.open();
//                        db_handler.updateAllAgentStatusToDeleted("DELETED",agent_name);
                        Log.d(TAG, "Updated status of gagd agent to deleted: ");

                        Log.d(TAG, "parseAndInsertGagdData: "+id+" "+url+" "+agent_name+" "+event_type);


                        db_handler.insertGagdAgent(id,url,agent_name,event_type,"INIT",packet_size,no_of_packets,user_type,rule,mobile,third_party_mobile);
                        Log.d(TAG, "Data inserted for gagd table: ");

                        db_handler.close();
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public  void parseAgentsUpdatedData(String response)
    {
        Utils.appendLog("ELOG_PARSE_UD: parseAgentsUpdatedData response of update data req is "+response);
        //Utils.appendLog(" : via parseAgentsUpdatedData response of update data req is "+response);
        if(response!=null)
        {
            String typeToUpdate=null;
            try
            {
                JSONObject responseObj=new JSONObject(response);
                String status=responseObj.optString("status");
                Utils.appendLog("ELOG_PARSE_UD: status of parseAgentsUpdatedData"+status);
                if(status.equalsIgnoreCase("0"))
                {
                    JSONArray dataArray = responseObj.optJSONArray("agent_details");
                    if (dataArray != null && dataArray.length() > 0) {
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject jsonObject = dataArray.optJSONObject(i);
                            String device_sno = jsonObject.optString("device_sno");
                            String file_name = jsonObject.optString("jar_file_name");
                            System.out.println("in parseAgentsUpdatedData file_name "+file_name);
                            String file_size = jsonObject.optString("jar_file_size");
                            String file_cksum = jsonObject.optString("jar_file_checksum");
                            String jar_path = jsonObject.optString("jar_path");
                            String class_name = jsonObject.optString("class_name");
                            String method_name = jsonObject.optString("method_name");
                            String ssdt = jsonObject.optString("schedule_start_time");
                            String sedt = jsonObject.optString("schedule_end_time");
                            String period = jsonObject.optString("period");
                            System.out.println(" period after changing is "+period);
                            String num_of_times = jsonObject.optString("number_of_iteration");
                            String agent_version = jsonObject.optString("agent_version");
                            String type = jsonObject.optString("type");
                            String frequency = jsonObject.optString("frequency");
                            String priority=jsonObject.optString("priority");
                            String task_type=jsonObject.optString("task_type");
                            String network_type=jsonObject.optString("network");
                            String urlsCount=jsonObject.optString("url_count");
                            DB_handler dbhandler= null;
                            try {
                                dbhandler = new DB_handler(context);
                                dbhandler.open();

                                dbhandler.updateAgentDetails(device_sno, file_name, file_size, file_cksum, jar_path,
                                        class_name, method_name, ssdt,
                                        sedt, period, num_of_times,"",
                                        "", " ", "",
                                        agent_version,type,
                                        frequency,priority,task_type);

                                dbhandler.close();

                            } catch (Exception e) {
                                Utils.appendLog("ELOG_PARSE_UD: Exception is parseAgentsUpdatedData "+e.getMessage());
                                e.printStackTrace();
                            }


                        }
                    }
                }
            }
            catch(Exception e)
            {
                System.out.println("Exception in  parseAgentsUpdatedData"+e.getMessage());
                e.printStackTrace();
            }

        }
    }

    public static void  updateAndParseGssScan(String response) throws JSONException {

        JSONObject jsonObject = new JSONObject(response);
        // String scan_number = null;
        String scanid = jsonObject.optString("scanid");
        System.out.println("scanid is  "+scanid);
        if (scanid.contains("[")) {
            scan_number=scanid.substring(scanid.indexOf("[")+2,scanid.indexOf("")+5);
            System.out.println("a is"+scan_number);



        }
//        if(scanid != null)
//        {
//
//            get_event_url_list(context,scan_number);
//        }


    }

    public static void updateAndParseManualScan(String response) throws JSONException {

        JSONObject jsonObject = new JSONObject(response);
        String status = jsonObject.optString("status");
        System.out.println(" status is "+status);
        if (!Utils.checkifnull(status) && status.equalsIgnoreCase("0")) {
            // String scan_id=jsonObject.optString("id");
            //System.out.println(" scan_id is  "+scan_id);
            String more=jsonObject.optString("more");
//            if(more!=null)
//            {
//                if(more.equalsIgnoreCase("y"))
//                {
//                    get_event_url_list(context,scan_number);
//                }
//            }

            JSONArray array = jsonObject.optJSONArray("itemsdetails");
            System.out.println(" array is"+array);
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    HashMap<String,String> details=new HashMap<>();
                    JSONObject object = array.getJSONObject(i);
                    String url = object.optString("blocked_url");
                    System.out.println(" url is  "+url);
                    String id = object.optString("id");
                    System.out.println(" id is  "+id);
                    String network = object.optString("network");
                    System.out.println(" network  is  "+network);
                    String browser = object.optString("browser");
                    System.out.println(" browser   is  "+browser);
                    String dns_ip = object.optString("dns_ip");
                    System.out.println("   dns_ip   is  "+dns_ip);
                    DB_handler db_handler = null;
                    try {
                        System.out.println("entering updateAndParseManualScan  is ");
                        db_handler = new DB_handler(context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    db_handler.open();
                    db_handler.insertUrlDetailsInDb(id,url,network,browser,dns_ip,i);
                    db_handler.close();
                }


            }

        }

    }


    // for app update
    public void parseInitNewData(final String result) {
        try {
            if (result != null) {
                parseGetInitNewData(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Constants.ERROR = 3;
        }


    }

    private void  parseGetInitNewData(String result) throws JSONException {
        try {
            JSONObject object = new JSONObject(result);
            statusInit = object.optString("status");
            DB_handler dbhandler = null;
            try {
                dbhandler = new DB_handler(context);
                dbhandler.open();
                dbhandler.insertInitStatusData(statusInit);
                dbhandler.close();
                System.out.println(" the status of init inserted  is" + statusInit);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String  parseGCNData(String response)
    {
        System.out.println(" : via parseAgentsUpdatedData response of update data req is "+response);
        //Utils.appendLog(" : via parseAgentsUpdatedData response of update data req is "+response);
        if(response!=null)
        {
            Constants.responseOne=response;

        }
        return response;
    }
    public void getUploadData(String result) throws JSONException {
        String id = null,file_name=null,file_size=null;
        try {
            JSONObject jsonobj = new JSONObject(result);
            String status = jsonobj.optString("status");
            String uploadid = null;
            if (Utils.checkifavailable(status) && status.equals("0")) {
                uploadid = jsonobj.optString("id");
                Log.i(TAG,"getUploadData  is "+ uploadid);
                String category_id = jsonobj.optString("category_id");
                Log.i(TAG,"category_id  is "+ uploadid);
                file_name = jsonobj.optString("file_name");
                Log.i(TAG,"file_name in getUploadData  is "+file_name);
                DB_handler adp=new DB_handler(context);
                adp.updateStatusForImageStartNew(file_name,"started",category_id,uploadid);
               /* Upload_service a=new Upload_service();
                a.uploadinit = true;*/
               // Log.i(TAG," upload service is  getUploadData"+a.uploadinit);


            }
            else {
              /*  Upload_service a=new Upload_service();
                a.uploadinit = true;*/
                return;
            }
        }
        catch (JSONException e) {
           /* Upload_service a=new Upload_service();
            a.uploadinit = true;*/
            e.printStackTrace();
        }

    }

    public void parseSPU(String response) {


    }
}
