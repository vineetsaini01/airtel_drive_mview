package com.newmview.wifi.BatteryGraphTypes;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.mview.airtel.R;
import com.newmview.wifi.helper.CustomMarkerView;
import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.newmview.wifi.application.MviewApplication.batteryList;
import static com.newmview.wifi.application.MviewApplication.timeStamp;

public class BatteryAreaChart extends Fragment {
    View view;
    private LineChart batteryAreaChart;
    private float batlevl;
    private boolean dontRefreshScreen;
    private ProgressDialog progressDialog;
    private String type;

    private int refreshFrequency;
    private Handler nHandler;


    @Override
    public void onResume() {
        super.onResume();
      //  handlerForGettingData();
        //useHandler();
    }

    @Override
    public void onPause() {
        super.onPause();

      /*  try
        {
            mHandler.removeCallbacks(mRunnable);
            nHandler.removeCallbacks(nRunnable);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.battery_area,container,false);
        init();
      // handlerForGettingData();
        useHandler();
        return view;
    }

    private void init() {
        batteryAreaChart=(LineChart) view.findViewById(R.id.batteryAreachart);
        showProgressDialog();
     Bundle  bundle= getArguments();
     if(bundle!=null)
     type= bundle.getString("type");

    }

    private void showProgressDialog() {

        progressDialog=new ProgressDialog(getActivity(),ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setMessage(Constants.LOADING);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void handlerForGettingData() {
        refreshFrequency=2*60*1000;
        nHandler = new Handler();
        nHandler.postDelayed(nRunnable, refreshFrequency);
    }
    private Runnable nRunnable = new Runnable() {

        @Override
        public void run() {

            try {

                getData();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                nHandler.postDelayed(nRunnable, refreshFrequency);
            }



        }
    };

    private void getData() {


        int currSize = timeStamp.size();
        if(currSize  == mView_HealthStatus.MaxPeriodicDataToSaveInDB && currSize >= 1)
        {
            batteryList.remove(0);

            timeStamp.remove(0);
        }
        batteryList.add(Float.valueOf(Utils.getBattery(getActivity())));
        timeStamp.add(Utils.getCurrentHourMin());

    }

    Handler mHandler;
    private void useHandler() {
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 1000);
    }


    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            //Log.e("Handlers", "Calls");
            if(dontRefreshScreen == false)
            {
                try {
                    if(progressDialog!=null && progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }
                    refreshBatteryUsage();
                    mHandler.postDelayed(mRunnable, mView_HealthStatus.updateDashboardUIIntervalInSeconds*1000);
                }catch(Exception e)
                {
                    Log.e("mView MainAct Frag" , e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    };

    private void refreshBatteryUsage() {

        {
            // creating list of entry
            ArrayList<Entry> entries = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<String>();
System.out.println("batterlist in area "+batteryList);
            if( batteryList!= null && batteryList.size()>0) {

                for (int i = 0; i < batteryList.size(); i++) {
                    float bat =batteryList.get(i);
                    entries.add(new Entry(bat, i));

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

                    labels.add(timeStamp.get(i));
                }
            }else {
                batlevl= Float.parseFloat(Utils.getBattery(getActivity()));
                entries.add(new Entry(batlevl, 0));
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                Date resultdate = new Date(System.currentTimeMillis());
                String displaydate = sdf.format(resultdate);
                labels.add(Utils.getCurrentHourMin());
            }

            LineDataSet dataset = new LineDataSet(entries, "battery level %");
            dataset.setLineWidth(2f);

            if(Utils.checkifavailable(type)) {
                if(type.equalsIgnoreCase("area")) {
                    dataset.setDrawFilled(true);
                    dataset.setFillAlpha(80);
                }
            }
chartCustomization(labels,dataset);

        }
    }

    private void chartCustomization(ArrayList<String> labels, LineDataSet dataset) {

        LineData data = new LineData(labels, dataset);
        batteryAreaChart.setData(data);
        batteryAreaChart.setExtraLeftOffset(18);
        batteryAreaChart.setExtraRightOffset(18);
        dataset.setValueTextSize(12f);
        dataset.setDrawValues(false);
        //  batteryAreaChart.animateXY(2000,2000);
        XAxis xAxis = batteryAreaChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLimitLinesBehindData(false);
        xAxis.setTextSize(14f);
        xAxis.setAvoidFirstLastClipping(true);
        batteryAreaChart.setDescription("");

        CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.custom_marker_view_layout, labels);
        batteryAreaChart.setMarkerView(mv);
        batteryAreaChart.invalidate();
    }


}
