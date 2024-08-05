package com.newmview.wifi.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.pengrad.mapscaleview.MapScaleView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.google.maps.android.ui.IconGenerator;
import com.mview.airtel.R;
import com.dashboard.activity.GraphDetailsActivity;
import com.newmview.wifi.helper.CommonUtil;

import com.newmview.wifi.interfaces.AsynctaskListener;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;
import com.webservice.PviewWebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class MapsFragment extends Fragment implements OnMapReadyCallback, AsynctaskListener,
        GoogleMap.OnCameraChangeListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener ,
        SearchView.OnQueryTextListener, SearchView.OnCloseListener, GoogleMap.OnMapClickListener{

    View view;
    private GoogleMap mMap;
    private GeoJsonLayer layer;
    private Bundle bundle;

    private ArrayList<HashMap<String, String>> columnWiseList;
    private ArrayList<ArrayList<HashMap<String, String>>> graphFinalList;
    private ArrayList<HashMap<String,String>> commondatalist;
    private String jsonResponse;
    private ArrayList<String> statesNames=new ArrayList<>();
    private JSONArray finalArray=new JSONArray();
    StringBuilder stringBuilder=new StringBuilder();
    private int j=0;
    private String finalurl;
    private boolean lastflag=false;
    public Handler handler;
    private ProgressBar pd;
    public  ArrayList<ArrayList<LatLng>> mainlist;
    public   ArrayList<ArrayList<ArrayList<LatLng>>> mainstates;
    public   ArrayList<ArrayList<ArrayList<LatLng>>> message;
    int p=0;
    private ArrayList<String> states=new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<LatLng>>> polylist;
    private IconGenerator iconFactory;
    private ArrayList<ArrayList<ArrayList<ArrayList<LatLng>>>> mainfinallist=new ArrayList<>();
    private ArrayList<HashMap<String,String >> columnList;
    private ArrayList<HashMap<String,String>> graphList;
    private ArrayList<Marker> markers=new ArrayList<>();
    private Message childThreadMessage;
    private Marker marker;
    private MapScaleView scaleView;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    private ArrayList<LatLng> centerlatlong=new ArrayList<>();
    private  int color=0;
    private SearchView searchView;
    private AsyncTask_getLatLong asyncTasks;
    private String response;
    private ArrayList<ArrayList<ArrayList<LatLng>>> list;
    private HashMap<String, String> selectedVal;
    private int selectedindex=-1;
    private boolean latlongFlag=false;
    private ArrayList<WeightedLatLng> pointsList;

    private String clickedVal;
    private FragmentManager childFragmentManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        childFragmentManager = getChildFragmentManager();
        handler=new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                message= (ArrayList<ArrayList<ArrayList<LatLng>>>) msg.obj;
                setPolygon(message);
            }
        };
    }




    private void setPolygon(ArrayList<ArrayList<ArrayList<LatLng>>> message) {



        /*  try {*/
        System.out.println(" message size is "+message.size() +message);


        for (int i = 0; i < message.size(); i++) {


            p++;

            color++;
            for (int j = 0; j < message.get(i).size(); j++) {

System.out.println("color is "+color);
int color1=Utils.enlight(color,20);
                    PolygonOptions polygonOptions = new PolygonOptions();

                    polygonOptions.addAll(message.get(i).get(j))
                            .strokeWidth(2).zIndex(p);
                    System.out.println("selected val index is "+selectedindex +" i "+i +" j "+j);
                    if(i==selectedindex)
                    {
                       /* LatLng latLng = Utils.getLatLongOfState(selectedVal.get("name"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 4F));*/
                        int redcolor= Color.rgb(139,0,0);
                        polygonOptions.fillColor(redcolor);
                        selectedindex=-1;
                    }
                    else {
                        polygonOptions.fillColor(Utils.gradientColor(color));
                    }
                        Polygon polygon = mMap.addPolygon(polygonOptions);
                        polygon.setClickable(true);



                        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
                            @Override
                            public void onPolygonClick(Polygon polygon) {

                                int stateindex= (int) (polygon.getZIndex()-1);
                                String statename=states.get(stateindex);
                                StringBuilder data=new StringBuilder();
                                LatLng latLng = Utils.getLatLongOfState(statename);
                                int index=Utils.getIndexN(commondatalist,"name",statename);
                                for(int i=0;i<graphFinalList.size();i++)
                                {
                                    data.append("\n").append( graphList.get(i).get("name")).append(" ").append(graphFinalList.get(i).get(index).get("y"));
                                }


System.out.println("index in map is "+index +"latlng "+latLng);


                                if(index!=-1) {
                                    if (latLng != null) {
                                        addText(getActivity(), mMap, latLng, statename + "\n"+
                                                data, 3, 15);
                                    }
                                }
                            }

                        });



            }
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.mapfragment,container,false);
        init();
        if(graphFinalList!=null && graphFinalList.size()>0)
        {

            System.out.println("graphFinal List "+graphFinalList);
            if (GraphDetailsActivity.group.equalsIgnoreCase("Geography") &&
                    (GraphDetailsActivity.subgrp.equalsIgnoreCase("state"))) {
                {
                    getLatLongCoordinatesData();

                }
            } else if(Utils.checkifavailable(graphFinalList.get(0).get(0).get("lat")) )
                {
                    if(!graphFinalList.get(0).get(0).get("lat").equalsIgnoreCase("NaN")) {
                        latlongFlag = true;
                        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                                .findFragmentById(R.id.map);
                        assert mapFragment != null;
                        mapFragment.getMapAsync(this);
                    }
                    else {
                        getLatLongCoordinatesData();
                    }

        }

        }



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.



        return view;
    }

    private void setPointValues() {

        {



            //mMap = googleMap;


            pointsList=new ArrayList<WeightedLatLng>();

            if (graphFinalList != null && graphFinalList.size() > 0) {
                for (int i = 0; i < graphFinalList.size(); i++) {
                    for (int j = 0; j < graphFinalList.get(i).size(); j++) {
                        double lat = Double.parseDouble(graphFinalList.get(i).get(j).get("lat"));
                        double lng=Double.parseDouble(graphFinalList.get(i).get(j).get("long"));
                        int y= Integer.parseInt(graphFinalList.get(i).get(j).get("y"));
                        clickedVal= graphFinalList.get(i).get(j).get("name")+"  "+"Value - "+y;
                        addMarker(BitmapDescriptorFactory.HUE_BLUE,lat,lng,clickedVal);



                           /* if(GraphDetailsActivity.column.contains("site"))
                            {
                                if(y<=5 && y>=1)
                                {
                                    addMarker(BitmapDescriptorFactory.HUE_GREEN,lat,lng,clickedVal);


                                }
                                else if(y>=6 && y<=10)
                                {

                                    addMarker(BitmapDescriptorFactory.HUE_YELLOW,lat,lng,clickedVal);


                                }
                                else if(y>=11 && y<=15)
                                {
                                    addMarker(BitmapDescriptorFactory.HUE_MAGENTA,lat,lng,clickedVal);
                                }
                                else
                                {
                                    addMarker(BitmapDescriptorFactory.HUE_RED,lat,lng,clickedVal);

                                }


                            }
                            else
                            {
                                addMarker(BitmapDescriptorFactory.HUE_BLUE,lat,lng,clickedVal);
                            }




*/
                    }
                }
            }

            pd.setVisibility(View.GONE);




        }


    }


    @Override
    public void onCameraMove() {
        CameraPosition cameraPosition = mMap.getCameraPosition();
        scaleView.update(cameraPosition.zoom, cameraPosition.target.latitude);
    }

    @Override
    public void onCameraIdle() {
        CameraPosition cameraPosition = mMap.getCameraPosition();
        scaleView.update(cameraPosition.zoom, cameraPosition.target.latitude);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        scaleView.update(cameraPosition.zoom, cameraPosition.target.latitude);
    }
    private void addMarker(float color, double lat, double lng, String clickedVal) {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(GraphDetailsActivity.column)
                .snippet(clickedVal).icon(BitmapDescriptorFactory.defaultMarker(color))
        );

    }

    private void getLatLongCoordinatesData() {

        //  if(GraphDetailsActivity.group.equalsIgnoreCase("Geography") && GraphDetailsActivity.subgrp.equalsIgnoreCase("state")) {
        CommonUtil.states.clear();

        if (graphFinalList != null && graphFinalList.size() > 0) {
            //  for (int a = 0; a < graphFinalList.size(); a++) {
            commondatalist = new ArrayList<>();
            commondatalist.addAll(graphFinalList.get(0));


            if (commondatalist != null) {
                if (commondatalist.size() > 0) {

                    for (int i = 0; i < commondatalist.size(); i++) {


                        CommonUtil.states.add(commondatalist.get(i).get("name"));



                        System.out.println("final state is "+commondatalist.get(i).get("name") +"  "+i );

                    }



                }
            }
            // }
        }




        String url=getFinalUrl();


       asyncTasks = new AsyncTask_getLatLong(getActivity(), this,url);
        asyncTasks.execute();




        // }

    }

    private String  getFinalUrl() {
        stringBuilder.setLength(0);
        statesNames.clear();





        System.out.println("string builder "+stringBuilder);
        int i;
        finalurl=null;

        for (i = j; i < j + 6; i++) {
            if(i<CommonUtil.states.size()) {
                System.out.println("i is " + i + "j is " + j);

                statesNames.add(CommonUtil.states.get(i));

            }

        }
        j = i;
        System.out.println("");

        for(int k=0;k<statesNames.size();k++)
        {
            if(statesNames.size()>0) {
                stringBuilder.append(statesNames.get(k).replace("&", "and"));
                if (k < statesNames.size() - 1) {
                    stringBuilder.append("|");
                }

            }
            System.out.println("i states name "+stringBuilder);
        }

        try {
            if(statesNames.size()>0) {
                String latLongUrl="http://198.12.250.223/pview_idea_new/stcord/India/";
                finalurl = latLongUrl + URLEncoder.encode(String.valueOf(stringBuilder), "UTF-8").replace("+", "%20");
                // finalurl=latLongUrl+stringBuilder;
                latLongUrl = finalurl;
                System.out.println("final url is " + latLongUrl);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalurl;
    }

    private void init() {
        pd=view.findViewById(R.id.progress);
        iconFactory=new IconGenerator(getActivity());
        scaleView = (MapScaleView) view.findViewById(R.id.scaleView);
       /* searchView=getActivity().findViewById(R.id.search);

        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        bundle=getArguments();
        if(getArguments()!=null)
        {
            columnWiseList= (ArrayList<HashMap<String, String>>) bundle.getSerializable("columnWiseList");
            graphFinalList= (ArrayList<ArrayList<HashMap<String, String>>>) bundle.getSerializable("graphFinalList");
            columnList= (ArrayList<HashMap<String, String>>) bundle.getSerializable("columnsList");
            graphList= (ArrayList<HashMap<String, String>>) bundle.getSerializable("graphList");
            selectedVal=(HashMap<String,String>)bundle.getSerializable("selectedVal");
            System.out.println("selected val in getting  "+selectedVal);


        }
      /*  SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
*/


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng Indialatlng = new LatLng(22.3511148, 78.6677428);
 if (Indialatlng != null) {
     mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Indialatlng, 4F));

        if (latlongFlag) {
            mMap.setOnMapClickListener(this);
         setPointValues();
        } else
        {

            // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Indialatlng, 4F));
            mMap.getUiSettings().setZoomGesturesEnabled(true);

            mMap = googleMap;

            mMap.setOnCameraMoveListener(this);
            mMap.setOnCameraIdleListener(this);
            mMap.setOnCameraChangeListener(this);
            if (polylist != null) {
                setPolygon(polylist);


                AsyncTask.execute(new Runnable() {

                    private String response;
                    private String json;
                    private JSONArray jsonArray = null;
                    private JSONObject jsonObject = null;


                    @Override
                    public void run() {
                        //TODO your background code


                        while (statesNames.size() > 0) {
                            sendRequestForCoordAndGetResponse();


                        }

                        if (statesNames.size() < 1) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }


                    }
                });
            }

        }
    }
    }

    private void sendRequestForCoordAndGetResponse() {

        response = PviewWebService.sendPostRequest(finalurl, null, getActivity());

        list = getCoordinatesJSON(response);
        getFinalUrl();

        // Create a message in child thread.
        childThreadMessage = new Message();
        childThreadMessage.obj = list;


        // Put the message in main thread message queue.
        handler.sendMessage(childThreadMessage);


    }

    public Marker addText(final Context context, final GoogleMap map,
                          final LatLng location, final String text, final int padding,
                          final int fontSize) {
       // Utils.showToast(getActivity(),"clicked!!");
        if(marker!=null)
            marker.remove();


        if (context == null || map == null || location == null || text == null
                || fontSize <= 0) {
            return marker;
        }

        final TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(fontSize);

        final Paint paintText = textView.getPaint();

        final Rect boundsText = new Rect();
        paintText.getTextBounds(text, 0, textView.length(), boundsText);
        paintText.setTextAlign(Paint.Align.CENTER);

        final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        final Bitmap bmpText = Bitmap.createBitmap(boundsText.width() + 2
                * padding, boundsText.height() + 2 * padding, conf);

        final Canvas canvasText = new Canvas(bmpText);
        paintText.setColor(Color.BLACK);

        canvasText.drawText(text, canvasText.getWidth() / 2,
                canvasText.getHeight() - padding - boundsText.bottom, paintText);

        final MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text)))
                .anchor(0.5f, 1);


        marker = map.addMarker(markerOptions);




        return marker;
    }
    @Override
    public void onTaskCompleted() {

    }

    public void LatLongJSONResponseFunction(ArrayList<ArrayList<ArrayList<LatLng>>> list) {
        pd.setVisibility(View.VISIBLE);

        this.polylist=list;
System.out.println("list size "+list.size());

    SupportMapFragment mapFragment = (SupportMapFragment) childFragmentManager
            .findFragmentById(R.id.map);
    if(mapFragment!=null) {
        mapFragment.getMapAsync(this);
    }



    }

    private LatLng getPolygonCenterPoint(ArrayList<LatLng> polygonPointsList) {

        LatLng centerLatLng = null;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i = 0 ; i < polygonPointsList.size() ; i++)
        {
            builder.include(polygonPointsList.get(i));
        }
        LatLngBounds bounds = builder.build();
        centerLatLng =  bounds.getCenter();

        return centerLatLng;
    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        /*try {
            String latLongUrl="http://203.122.58.233/pview_idea_new/stcord/India/";
            finalurl = latLongUrl + URLEncoder.encode(String.valueOf(stringBuilder), "UTF-8").replace("+", "%20");
            asyncTasks = new AsyncTask_getLatLong(getActivity(), this,finalurl);
            asyncTasks.execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
*/
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }



    private class AsyncTask_getLatLong extends AsyncTask<HashMap<String, String>, Void, ArrayList<ArrayList<ArrayList<LatLng>>>> {
        private final AsynctaskListener callback;
        private final String url;
        private Context context;
        ProgressDialog progressDialog;

        private String response;
        private String json;
        public AsyncTask_getLatLong(Context context, AsynctaskListener listener, String url) {
            this.context = context;
            this.url=url;
            callback=listener;
            CommonUtil.request = 6;
            System.out.println("request is "+CommonUtil.request);
        }



        @Override
        protected ArrayList<ArrayList<ArrayList<LatLng>>> doInBackground(HashMap<String, String>... hashMaps) {
            JSONArray jsonArray=null;
            JSONObject jsonObject=null;
            ArrayList<ArrayList<ArrayList<LatLng>>> list;
            response = PviewWebService.sendPostRequest(finalurl, null, context);
            list=getCoordinatesJSON(response);
            getFinalUrl();

            json = String.valueOf(jsonObject);

            return list;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            showProgressBeforeLoadingData();


        }

        @Override
        protected void onPostExecute(ArrayList<ArrayList<ArrayList<LatLng>>> response) {


            super.onPostExecute(response);
            dismissProgress();
            if(response!=null)
                LatLongJSONResponseFunction(response);



        }
        private void dismissProgress() {
            if(progressDialog!=null && progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
        }

        private void showProgressBeforeLoadingData() {


            if (progressDialog == null) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(Constants.LOADING);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            }
        }
        public LatLng getPolygonCenterPoint(ArrayList<LatLng> polygonPointsList){
            LatLng centerLatLng = null;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for(int i = 0 ; i < polygonPointsList.size() ; i++)
            {
                builder.include(polygonPointsList.get(i));
            }
            LatLngBounds bounds = builder.build();
            centerLatLng =  bounds.getCenter();

            return centerLatLng;
        }



    }

    private ArrayList<ArrayList<ArrayList<LatLng>>> getCoordinatesJSON(final String result) {
        System.out.println("result is "+result);


        //TODO your background code
        if(result!=null) {
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(result);
                mainstates=new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if((selectedVal!=null)&& (selectedVal.size()>0) && (selectedVal.get("name").equalsIgnoreCase(jsonArray.getJSONObject(i).optString("state"))))
                    {

                        selectedindex=i;
                    }

                  /*  if(selectedVal!=null) {
                        if ((selectedVal.get("name")).equalsIgnoreCase(jsonArray.getJSONObject(i).optString("state"))) {

                            selectedindex = i;
                            System.out.println("selected val index" + selectedindex + "selected name "+selectedVal.get("name"));
                        }
                    }*/
                    if(jsonArray.getJSONObject(i).optString("type").equalsIgnoreCase("polygon")) {


                        JSONArray coordarray = new JSONArray(jsonArray.getJSONObject(i).optString("coordinates"));

                        mainlist = new ArrayList<>();

                        for (int j = 0; j < coordarray.length(); j++) {
                            JSONArray latarr = coordarray.getJSONArray(j);
                            ArrayList<LatLng> li = new ArrayList<>();
                            for (int k = 0; k < latarr.length(); k++) {

                                li.add(new LatLng(latarr.getJSONArray(k).optDouble(1), latarr.getJSONArray(k).optDouble(0)));

                            }
                            mainlist.add(li);

                        }
                        String state = jsonObject.optString("state");

                        states.add(state);
                        mainstates.add(mainlist);
                        System.out.println("states "+state);


                    }
                    else if(jsonArray.getJSONObject(i).optString("type").equalsIgnoreCase("Multipolygon"))
                    {
                        JSONArray coordarray = new JSONArray(jsonArray.getJSONObject(i).optString("coordinates"));



                        mainlist = new ArrayList<>();
                        for (int j = 0; j < coordarray.length(); j++) {
                            JSONArray latoutarr = coordarray.getJSONArray(j);

                            JSONArray latarr=latoutarr.getJSONArray(0);
                            ArrayList<LatLng> li1 = new ArrayList<>();
                            for(int l=0;l<latarr.length();l++) {

                                li1.add(new LatLng(latarr.getJSONArray(l).optDouble(1), latarr.getJSONArray(l).optDouble(0)));

                            }
                            mainlist.add(li1);
                        }
                        String state = jsonObject.optString("state");

                        states.add(state);
                        mainstates.add(mainlist);

                        System.out.println("main states size is "+mainfinallist.size());

                    }



                }



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }



        return  mainstates;

    }


    @Override
    public void onMapClick(LatLng latLng) {
        if(latlongFlag) {
            //Utils.showToast(getActivity(), "lat long " + latLng);
            if (getIndex(graphFinalList, latLng.latitude, latLng.longitude) != -1) {
                addText(getActivity(), mMap, latLng, clickedVal, 3, 15);
            }
        }

    }

    private int getIndex(ArrayList<ArrayList<HashMap<String, String>>> list, double latitude, double longitude) {


        int i;
        for (i = 0; i < list.size(); i++) {

            for(int j=0;j<list.get(i).size();j++)
            {
                Double lati=(Double.parseDouble(list.get(i).get(j).get("lat")));
                Double longi=  (Double.parseDouble(list.get(i).get(j).get("long")));


                System.out.println("lat and long from list "+ lati + " ," +longi +"clciked latlong "+latitude +" ,"+longitude);
                if((lati==latitude) && (longi==longitude))
                {

                    clickedVal= GraphDetailsActivity.column+"\n"+graphFinalList.get(i).get(j).get("name")+"\n"+"Value - "+graphFinalList.get(i).get(j).get("y");
                    System.out.println("index is "+j);
                    return j;
                }
            }
        }
        return -1;



    }

    @Override
    public void onStop() {
        super.onStop();

        if(getActivity()!=null) {

            if(handler!=null)
                handler.removeCallbacksAndMessages(childThreadMessage);
            if (pd != null && pd.isShown()) {
                pd.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if(getActivity()!=null) {
            if(handler!=null)
                handler.removeCallbacksAndMessages(childThreadMessage);
            if (pd != null && pd.isShown()) {
                pd.setVisibility(View.GONE);
            }
        }


    }
}
