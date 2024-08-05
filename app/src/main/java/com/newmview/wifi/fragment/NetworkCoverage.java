package com.newmview.wifi.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mview.airtel.R;
import com.newmview.wifi.helper.CustomMarkerView;
import com.newmview.wifi.mView_HealthStatus;

import java.util.ArrayList;

/**
 * Created by functionapps on 10/31/2018.
 */

public class NetworkCoverage extends Fragment {
    private View view;
    private PieChart pieChart_nc;
    private TextView valueTxt_MyCoverage[];
    private ArrayList<Entry> entries_nc;
    private PieDataSet dataset_nc;
    private TableLayout childlayout;
    private boolean dontRefreshScreen;
    private TableLayout childlayout_nc;
    private Toolbar toolbar;
    private int color;


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.network_coverage,container,false);
        childlayout = (TableLayout) view.findViewById(R.id.child_layout);
        childlayout_nc = (TableLayout) view.findViewById(R.id.child_layout_nc);
        pieChart_nc = (PieChart) view.findViewById(R.id.chart_nc);
        toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_new);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        entries_nc = new ArrayList<>();
        entries_nc.add(new Entry(0, 0));
        entries_nc.add(new Entry(0, 1));
        entries_nc.add(new Entry(0, 2));
        entries_nc.add(new Entry(0, 3));

        dataset_nc = new PieDataSet(entries_nc, "Network Coverage");
        useHandler();
        ArrayList<String> labels_nc = new ArrayList<String>();
        labels_nc.add("4G");
        labels_nc.add("3G");
        labels_nc.add("2G");
        labels_nc.add("NS");
        int[] colors = new int[labels_nc.size()];
        
        for (int i = 0; i < labels_nc.size(); i++) {
            colors[i]=ColorTemplate.JOYFUL_COLORS[i];

        }
        dataset_nc.setColors(colors);

       /* Random rnd = new Random();
        for (int i = 0; i < labels_nc.size(); i++) {
             color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        }
        dataset_nc.setColor(color);*/
        PieData data_nc = new PieData(labels_nc, dataset_nc);
        pieChart_nc.setData(data_nc);
        pieChart_nc.setDescription("");
        pieChart_nc.highlightValues(null);
        //pieChart_nc.setElevation(2.0f);

        pieChart_nc.setCenterTextSize(30);
        pieChart_nc.getLegend().setEnabled(false);
        setCustomLegendForMyCoverage();

        return view;
    }
Handler mHandler;
    private void useHandler() {
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, mView_HealthStatus.updateDashboardUIIntervalInSeconds*1000);
    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            //Log.e("Handlers", "Calls");
            if(dontRefreshScreen == false)
            {
                try {
                   refreshNetworkCoverage();
                    mHandler.postDelayed(mRunnable, mView_HealthStatus.updateDashboardUIIntervalInSeconds*1000);
                }catch(Exception e)
                {
                    Log.e("mView MainAct Frag" , e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    };


    private void setCustomLegendForMyCoverage() {

        valueTxt_MyCoverage = new TextView[4];
        Legend l = pieChart_nc.getLegend();
        int colorcodes[] = l.getColors();
        int sz = l.getColors().length;
        Context context = getActivity();

        for (int i = 0; i < l.getColors().length -1 ; i++) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            TableRow tr = (TableRow)inflater.inflate(R.layout.table_row_legend, childlayout, false);

            childlayout_nc.addView(tr);

            LinearLayout linearLayoutColorContainer=(LinearLayout) tr.getChildAt(0);
            LinearLayout linearLayoutColor= (LinearLayout)linearLayoutColorContainer.getChildAt(0);
            TextView tvLabel = (TextView) tr.getChildAt(1);
            TextView tvAmt = (TextView) tr.getChildAt(2);
            valueTxt_MyCoverage[i] = tvAmt;
            linearLayoutColor.setBackgroundColor(colorcodes[i]);
            tvLabel.setText(l.getLabel(i));
            int aa = (int)(entries_nc.get(i).getVal());

            tvAmt.setText(aa+"");
        }

        pieChart_nc.setCenterText(mView_HealthStatus.strCurrentNetworkState);

        pieChart_nc.getLegend().setWordWrapEnabled(true);
        pieChart_nc.getLegend().setEnabled(false);


    }

    private void refreshNetworkCoverage() {

        {
            pieChart_nc.setCenterText(mView_HealthStatus.onlyCurrentNetworkState);

            long elapsedTime =  (System.currentTimeMillis() - mView_HealthStatus.starTimeInCurrentState);
            long totalTime = mView_HealthStatus.timein4G + mView_HealthStatus.timein3G + mView_HealthStatus.timein2G + mView_HealthStatus.timeinNS +elapsedTime;
            float totalTimeInMinutes = totalTime / (1000*60.0f);
            if( totalTimeInMinutes == 0.0f)
                totalTimeInMinutes = 0.1f;

            mView_HealthStatus.per4g = (float)(mView_HealthStatus.timein4G*100) / ((totalTimeInMinutes*1.0f)*(1000*60));
            mView_HealthStatus.per3g = (float)(mView_HealthStatus.timein3G*100) / ((totalTimeInMinutes*1.0f)*(1000*60));
            mView_HealthStatus.per2g = (float)(mView_HealthStatus.timein2G*100) / ((totalTimeInMinutes*1.0f)*(1000*60));
            mView_HealthStatus.perNS = (float)(mView_HealthStatus.timeinNS*100) / ((totalTimeInMinutes*1.0f)*(1000*60));

            if( mView_HealthStatus.iCurrentNetworkState == 4)
            {
                mView_HealthStatus.per4g = ( ((mView_HealthStatus.timein4G + elapsedTime)/(1000*60.0f) )*100) / (totalTimeInMinutes*1.0f);
                if( mView_HealthStatus.per4g> 100)
                    mView_HealthStatus.per4g = 100;
            }else if( mView_HealthStatus.iCurrentNetworkState == 3)
            {
                mView_HealthStatus.per3g = ( ((mView_HealthStatus.timein3G + elapsedTime)/(1000*60.0f) )*100) / (totalTimeInMinutes*1.0f);
                if( mView_HealthStatus.per3g> 100)
                    mView_HealthStatus.per3g = 100;
            }else if( mView_HealthStatus.iCurrentNetworkState == 2)
            {
                mView_HealthStatus.per2g = ( ((mView_HealthStatus.timein2G + elapsedTime)/(1000*60.0f) )*100) / (totalTimeInMinutes*1.0f);
                if( mView_HealthStatus.per2g> 100)
                    mView_HealthStatus.per2g = 100;
            }else if( mView_HealthStatus.iCurrentNetworkState == 0)
            {
                mView_HealthStatus.perNS = ( ((mView_HealthStatus.timeinNS + elapsedTime)/(1000*60.0f) )*100) / (totalTimeInMinutes*1.0f);
                if( mView_HealthStatus.perNS> 100)
                    mView_HealthStatus.perNS = 100;
            }

            valueTxt_MyCoverage[0].setText(String.format("%.2f",mView_HealthStatus.per4g)+"%");
            valueTxt_MyCoverage[1].setText(String.format("%.2f",mView_HealthStatus.per3g)+"%");
            valueTxt_MyCoverage[2].setText( String.format("%.2f",mView_HealthStatus.per2g)+"%");
            valueTxt_MyCoverage[3].setText(String.format("%.2f",mView_HealthStatus.perNS)+"%");

            entries_nc = new ArrayList<>();
            entries_nc.add(new Entry(mView_HealthStatus.per4g, 0));
            entries_nc.add(new Entry(mView_HealthStatus.per3g, 1));
            entries_nc.add(new Entry(mView_HealthStatus.per2g, 2));
            entries_nc.add(new Entry(mView_HealthStatus.perNS, 3));

            dataset_nc = new PieDataSet(entries_nc, "# of Calls");

            ArrayList<String> labels1 = new ArrayList<String>();
            labels1.add("4G");
            labels1.add("3G");
            labels1.add("2G");
            labels1.add("NS");

            dataset_nc.setColors(ColorTemplate.COLORFUL_COLORS);
            PieData data_nc = new PieData(labels1, dataset_nc);
            pieChart_nc.setData(data_nc);
            pieChart_nc.invalidate();
            CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.custom_marker_view_layout, null);
           pieChart_nc.setMarkerView(mv);
            pieChart_nc.invalidate();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        dontRefreshScreen = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        dontRefreshScreen = false;
        mHandler.postDelayed(mRunnable, mView_HealthStatus.updateDashboardUIIntervalInSeconds*1000);
    }
}
