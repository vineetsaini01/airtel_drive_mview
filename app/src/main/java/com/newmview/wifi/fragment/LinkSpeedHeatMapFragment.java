package com.newmview.wifi.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mview.airtel.R;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.bean.WifiHeatMapPoints;
import com.newmview.wifi.database.DB_handler;
import com.mview.airtel.databinding.FragmentLinkSpeedHeatMapBinding;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.newmview.wifi.other.WifiConfig.getConnectedWifiDetails;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LinkSpeedHeatMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LinkSpeedHeatMapFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "LinkSpeedHeatMap";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentLinkSpeedHeatMapBinding linkSpeedHeatMapBinding;
    private ArrayList<WifiHeatMapPoints> wifiHeatMapPoints;
    private String path;
    private List<WifiHeatMapPoints> excellentPoints, fairPoints, goodPoints, poorPoints;
    private DB_handler db_handler;
    private Bitmap bitmap;
    private double y_percentage,lg_percentage,d_percentage,r_percentage;
    private ArrayList<Double> percentList;
    private String mapId;

    public LinkSpeedHeatMapFragment() {
        // Required empty public constructor
    }

    public static LinkSpeedHeatMapFragment newInstance(Bundle bundle) {
        LinkSpeedHeatMapFragment fragment = new LinkSpeedHeatMapFragment();
        fragment.setArguments(bundle);
       /* Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        Log.i(TAG, "Setting fragment");
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        Log.i(TAG, "Bundle is " + bundle);
        if (bundle != null) {
            wifiHeatMapPoints = bundle.getParcelableArrayList("pointsList");
            path = bundle.getString("path");
            mapId=bundle.getString("mapId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        //StrictMode.setVmPolicy(builder.build());

        linkSpeedHeatMapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_link_speed_heat_map, container, false);
        init();
        setClickListeners();
        setColorsToHeatMap();
        getStats();
        return linkSpeedHeatMapBinding.getRoot();

    }

    private void init() {

        db_handler = new DB_handler(MviewApplication.ctx);
        bitmap = setMapToImage();


        if (bitmap != null) {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bitmap, new Matrix(), null);
            linkSpeedHeatMapBinding.linkSpeedHMV.setImageBitmap(bitmap);
        }
        setClickListeners();
    }

    private Bitmap setMapToImage() {
        File imgFile = new File(path);

        if (imgFile.exists()) {
            java.io.FileInputStream in = null;
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

                return mutableBitmap;

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        return null;
    }

    private void setClickListeners() {

    }

    private void setColorsToHeatMap() {
       /* for(int i=0;i<wifiHeatMapPoints.size();i++)
        {
            Log.i(TAG,"Quad id for "+i+" is "+wifiHeatMapPoints.get(i).getQuadId() +" point x "+wifiHeatMapPoints.get(i).getPoints().get(0).x +" y "+wifiHeatMapPoints.get(i).getPoints().get(0).y);
        }*/
        setPointsToRespectiveLists(getPointsAccToGridId("quad1"), "quad1");
        setPointsToRespectiveLists(getPointsAccToGridId("quad2"), "quad2");
        setPointsToRespectiveLists(getPointsAccToGridId("quad3"), "quad3");
        setPointsToRespectiveLists(getPointsAccToGridId("quad4"), "quad4");
        setPointsToRespectiveLists(getPointsAccToGridId("quad"), "quad");


    }

    public List<WifiHeatMapPoints> getPointsAccToGridId(String gridId) {

        Log.i(TAG, "QUAD id is " + gridId);
        List<WifiHeatMapPoints> wifiPoints;

        for (int i = 0; i < wifiHeatMapPoints.size(); i++) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (gridId != null) {

                    wifiPoints =
                            wifiHeatMapPoints.stream()
                                    .filter(str -> str.getQuadId().equalsIgnoreCase(gridId))
                                    .collect(Collectors.toList());
                    return wifiPoints;
                }


            }

        }
        return null;
    }
    private void getStats()
    {
        int yellowColorPoints=linkSpeedHeatMapBinding.linkSpeedHMV.getYellowPoints();
        int lightGreenPoints=linkSpeedHeatMapBinding.linkSpeedHMV.getBluePoints();
        int darkGreenPoints=linkSpeedHeatMapBinding.linkSpeedHMV.getGreenPoints();
        int redPoints=linkSpeedHeatMapBinding.linkSpeedHMV.getRedPointsSize();
        DecimalFormat decimalFormat=new DecimalFormat("0.0");
        int totalPoints=yellowColorPoints + lightGreenPoints + darkGreenPoints + redPoints;
        y_percentage=((double)(yellowColorPoints *100 )/totalPoints);
        lg_percentage= ((double)(lightGreenPoints* 100)/totalPoints);
        d_percentage=  ((double)(darkGreenPoints * 100)/totalPoints);
        r_percentage=  ((double)(redPoints *100) /totalPoints);
        String y_text="Fair";
        String lg_text="Good";
        String dg_tet="Excellent";
        String red_text="Poor";
        //Utils.appendCrashLog("");
        Log.i(TAG,"Count for dg "+darkGreenPoints +" count for lg "+lightGreenPoints +" yellow "+yellowColorPoints +" red "+redPoints +" total "+totalPoints);
        Log.i(TAG,"Percentt for dg "+d_percentage +"  for lg "+lg_percentage +" yellow "+y_percentage +" red "+r_percentage +" total "+totalPoints);
    db_handler.open();
    db_handler.updateLsFinalMapPercentDetails(mapId,d_percentage,lg_percentage,y_percentage,r_percentage);
    db_handler.close();
      /*  linkSpeedHeatMapBinding.darkGreenTv.setText(decimalFormat.format(d_percentage)+"% ("+dg_tet+")");
        linkSpeedHeatMapBinding.lightGreenTv.setText(decimalFormat.format(lg_percentage)+"% ("+lg_text+")");
        linkSpeedHeatMapBinding.yellowTv.setText(decimalFormat.format(y_percentage)+"% ("+y_text+")");
        linkSpeedHeatMapBinding.redTv.setText(decimalFormat.format(r_percentage)+"% ("+red_text+")");
*/

    }

    private void setPointsToRespectiveLists(List<WifiHeatMapPoints> heatMapPoints, String quadId) {
        poorPoints = new ArrayList<>();
        fairPoints = new ArrayList<>();
        excellentPoints = new ArrayList<>();
        goodPoints = new ArrayList<>();
        if (heatMapPoints != null) {
            for (int i = 0; i < heatMapPoints.size(); i++) {
                WifiHeatMapPoints point = heatMapPoints.get(i);
                int linkSpeed = point.getLinkSpeed();
                int color = point.getLinkSpeedColor();

                Log.i(TAG, "Points link speed " + linkSpeed);
                double freqBand = getConnectedWifiDetails().getFrequencyBandwidth();
                Log.i(TAG, "Freq band on touching " + freqBand);
                if (freqBand == 2.4) {
                    if (linkSpeed >=100 ) {

                        // color= Color.parseColor("#006400");//dark green

                       // excellentPoints.add(point.getPoints().get(0));
                        excellentPoints.add(point);

                    } else if (linkSpeed >= 50 && linkSpeed<100) {
                        goodPoints.add(point);

                    } else if (linkSpeed >=30 && linkSpeed<50) {
                        fairPoints.add(point);

                    } else if (linkSpeed >=0 && linkSpeed<30) {
                        poorPoints.add(point);
                    }
                } else if (freqBand == 5.0) {
                    if (linkSpeed >= 400) {

                        excellentPoints.add(point);

                    } else if (linkSpeed >= 300 && linkSpeed<400) {
                        goodPoints.add(point);

                    } else if (linkSpeed >= 200 && linkSpeed<300) {
                        fairPoints.add(point);
                    } else if (linkSpeed >=0 && linkSpeed<200) {
                        poorPoints.add(point);
                    }

                }

            }

//vif(poorPoints.size()>)
            Log.i(TAG, "Red points getting added for " + quadId);
            Log.i(TAG, "Light Green points getting added for " + quadId);
            Log.i(TAG, "Yellow points getting added for " + quadId);
            linkSpeedHeatMapBinding.linkSpeedHMV.setPoorPoints(poorPoints);
            linkSpeedHeatMapBinding.linkSpeedHMV.setFairPoints(fairPoints);
            linkSpeedHeatMapBinding.linkSpeedHMV.setGoodPoints(goodPoints);
            linkSpeedHeatMapBinding.linkSpeedHMV.setExcellentPoints(excellentPoints);
        }
        

    }
}