package com.newmview.wifi.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;

import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.newmview.wifi.BatteryGraphTypes.BatteryAreaChart;
import com.newmview.wifi.BatteryGraphTypes.BatteryBarChart;
import com.mview.airtel.R;
import com.newmview.wifi.helper.CustomMarkerView;
import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by functionapps on 10/31/2018.
 */

public class BatteryUsageFragment extends Fragment implements View.OnClickListener {
    private View view;
    private LineChart batteryLinechart;
    float batlevl=0;
    private Toolbar toolbar;
    private boolean dontRefreshScreen;
    private ProgressDialog progressDialog;
    private ImageView selectChart;
    private ArrayList<String> chartTypes;
    private FragmentManager fragmentManager;
    private int refreshFrequency;
    private Handler nHandler;


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.btry_usage,container,false);
        init();
        return view;
    }

    private void init() {
        if(Utils.checkContext(getActivity())) {
            fragmentManager = getActivity().getSupportFragmentManager();
        }
        batteryLinechart=(LineChart)view.findViewById(R.id.batteryLinechart);
        addChrtTypesInList();
        selectChart=view.findViewById(R.id.Selectchart);
        selectChart.setOnClickListener(this);
        toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_new);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        loadFragment(new BatteryAreaChart(),null,"line");
      //  Utils.getCapturedData(getActivity());
        //useHandler();
       // showProgress();


    }

    private void showProgress() {
        progressDialog=new ProgressDialog(getActivity(),ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setMessage(Constants.LOADING);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void addChrtTypesInList() {
        chartTypes= new ArrayList<String>();
        chartTypes.add(0,"Bar");
        chartTypes.add(1,"Line");
        chartTypes.add(2,"Area");

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
                   //refreshBatteryUsage();
                   // mHandler.postDelayed(mRunnable, mView_HealthStatus.updateDashboardUIIntervalInSeconds*1000);
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

            if( mView_HealthStatus.timeSeriesCapturedData != null && mView_HealthStatus.timeSeriesCapturedData.size()>0) {

                for (int i = 0; i < mView_HealthStatus.timeSeriesCapturedData.size(); i++) {
                    float bat = Float.parseFloat(mView_HealthStatus.timeSeriesCapturedData.get(i).batterylevel);
                    entries.add(new Entry(bat, i));

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                   /* Date resultdate = new Date(mView_HealthStatus.timeSeriesCapturedData.get(i).captureTime);
                    String displaydate = sdf.format(resultdate);*/


                 /*  String newdate=mView_HealthStatus.timeSeriesCapturedData.get(i).captureTime.
                           substring(0,mView_HealthStatus.timeSeriesCapturedData.get(i).captureTime.length()-3);*/
                    labels.add(mView_HealthStatus.timeSeriesCapturedData.get(i).hourMin);
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

            LineData data = new LineData(labels, dataset);
            batteryLinechart.setData(data);
            batteryLinechart.setExtraLeftOffset(18);
           batteryLinechart.setExtraRightOffset(18);
            dataset.setValueTextSize(12f);
        //  batteryLinechart.animateXY(2000,2000);
            XAxis xAxis = batteryLinechart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawAxisLine(false);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawLimitLinesBehindData(false);
            xAxis.setTextSize(14f);
            xAxis.setAvoidFirstLastClipping(true);
            batteryLinechart.setDescription("");
            //dataset.setDrawFilled(true);
           /* Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            int color2= Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
           dataset.setColor(color);
           dataset.setFillColor(color2);*/
            CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.custom_marker_view_layout, labels);
            batteryLinechart.setMarkerView(mv);
            batteryLinechart.invalidate();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.Selectchart:
                if(Utils.checkContext(getActivity())) {
                    Dialog dialog = new Dialog(getActivity(), R.style.AlertDialogTheme);
                    openDialog(dialog);

                }
        }
    }
    private void initializeRadioButtonDialog(Dialog dialog, RadioGroup radioGroup,String title) {


        if(chartTypes!=null && chartTypes.size()>0) {

            for (int i = 0; i < chartTypes.size();i++)
            {
                RadioButton radioButton = new RadioButton(getActivity());
                RadioGroup.LayoutParams rprms = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                radioButton.setText(chartTypes.get(i));
                radioGroup.addView(radioButton,rprms);




            }
        }
      TextView textView=dialog.findViewById(R.id.tvTitle);
        textView.setText(title);

    }

    private void openDialog(Dialog dialog) {

        //System.out.println("title is"+title);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.TOP;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        dialog.setContentView(R.layout.dialoglayout);
      RadioGroup  radiogroup = (RadioGroup) dialog.findViewById(R.id.radioBtn);
        radiogroup.removeAllViews();
        initializeRadioButtonDialog(dialog,radiogroup,"Graph Types");
        addListenersToRadioButtons(radiogroup,dialog);

        dialog.show();
    }

    private void addListenersToRadioButtons(RadioGroup radiogroup,Dialog dialog) {
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //for (int i = 0; i < group.getChildCount(); i++) {
                    //RadioButton radioButton = (RadioButton) group.getChildAt(i);
                    View radioButton = radiogroup.findViewById(checkedId);

                    int index = radiogroup.indexOfChild(radioButton);
                   // if (radioButton.getId() == checkedId) {
                switch (index)
                {
                    case 0:

                        loadFragment(new BatteryBarChart(),dialog,"Bar");
                        break;
                    case 1:
                        loadFragment(new BatteryAreaChart(),dialog,"Line");
                        break;
                    case 2:
                        loadFragment(new BatteryAreaChart(),dialog,"Area");
                        break;

                }

            }
        });
    }


    private void loadFragment(Fragment fragment,Dialog dialog,String type) {
        //Utils.showToast(getActivity(),"in load");
        Bundle bundle=new Bundle();
        bundle.putString("type",type);

if(fragmentManager==null)
    fragmentManager=getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);

        fragmentManager.beginTransaction().replace(R.id.chart_frame, fragment).commit();
        fragment.setArguments(bundle);
        if(dialog!=null)
        dialog.dismiss();

    }
}
