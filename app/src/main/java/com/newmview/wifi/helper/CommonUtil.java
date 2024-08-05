package com.newmview.wifi.helper;

import com.google.android.gms.maps.model.LatLng;
import com.newmview.wifi.bean.TableDataBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by functionapps on 3/5/2019.
 */

public class CommonUtil {


    //public static final String TURN_ON_WIFI = ;
    public static ArrayList<HashMap<String,String>> graphdata=new ArrayList<>();
    public static ArrayList<HashMap<String,String>> repList=new ArrayList<>();
    public static ArrayList<HashMap<String,String>> dashboardList=new ArrayList<>();
    public static ArrayList<HashMap<String,String>> newDashboardList=new ArrayList<>();
    public static ArrayList<HashMap<String,String>> newGraphdataList=new ArrayList<>();
    public static ArrayList<String> columnIdList=new ArrayList<String>();


    public static String INIT_REQUEST="initreq";
    public static String REQUEST_KEY="code_request";
    public static String USER_ID_KEY="userId";
    public static String USER_ID="admin";
    public static  String PASSWORD_KEY = "userPasswd";
    public static String PASSWORD="admin@321";
    public static String DASHBOARD_REQ="appdcd";
    public static String USER_ID_NEW="admin";
    public static final String TABLE_ERROR ="No data is available.Kindly try with some another date range!" ;

    // public static String USER_ID_NEW="mview";


    public static int request=0;
    public static String DBID_KEY ="dbId";
    public static String DBNAME_KEY ="dbName";
    public static String DASHBOARD_REQUEST="getDashboards";
    public static String GRAPHDETAILS_REQUEST="getGraphDetails";
    public static String GRAPHDATA_REQUEST="getGraphData";
    public static String stateName=null;
    public static String osmUrl="https://nominatim.openstreetmap.org/search.php?q="+stateName+"&polygon_geojson=1&format=json";

    public static ArrayList <HashMap<String, String>> listDbChart=new ArrayList<>();
    public static ArrayList<HashMap<String,String>> list_dashboardName=new ArrayList<>();
    public static String DRILL_REQUEST="ddr";

    public static ArrayList<HashMap<String, String>> drillrepdata=new ArrayList<>();
      public static ArrayList<ArrayList<HashMap<String, String>>> mappgraphlist=new ArrayList<>();
      public static ArrayList<HashMap<String, String>> piecolumns=new ArrayList<>();

    public static ArrayList<HashMap<String, String>> tableFieldsList=new ArrayList<>();
    public static ArrayList<HashMap<String, String>> tableValuesList=new ArrayList<>();
    public static ArrayList<HashMap<String, String>> pieDateRange=new ArrayList<>();
    public static ArrayList<HashMap<String, String>> pieColumns=new ArrayList<>();
    public static ArrayList<HashMap<String, String>> pietablewithcolumns=new ArrayList<>();
    public static ArrayList<String> states=new ArrayList<>();
    public static ArrayList<TableDataBean> tableValues=new ArrayList<>();
    public static ArrayList<HashMap<String, LatLng>> stateslist;

    public static ArrayList<String> trend=new ArrayList<>();


    public static ArrayList<String> headersList;
}
