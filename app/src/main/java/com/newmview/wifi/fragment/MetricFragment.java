package com.newmview.wifi.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mview.airtel.R;
import com.dashboard.activity.GraphDetailsActivity;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.other.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class MetricFragment extends Fragment implements Interfaces.ChangeColorListener{
    private View view;
    private ArrayList<HashMap<String, String>> datalist=new ArrayList<>();
    private Bundle bundle;
    private ArrayList<String> colnames;
    private LinearLayout metric_ll;
    private ArrayList<String> namesList;
    private ArrayList<Integer> randomColorsList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.metric_fragment, container, false);
        init();

        getRandomColorsList();
       displayMetricView();
        return view;

    }

    private void getRandomColorsList() {

        randomColorsList=new ArrayList<>();
        if(datalist!=null && datalist.size()>0) {
for(int i=0;i<datalist.size();i++) {
    randomColorsList.add(Utils.getSomeRandomColor());
}
            if(getActivity()!=null)
            {
                if(getActivity() instanceof GraphDetailsActivity)
                {
                    ((GraphDetailsActivity)getActivity()).sendColorsList(randomColorsList);
                }
            }

        }

    }

    private  void init()
    {
        metric_ll=view.findViewById(R.id.metric_ll);
getExtras();
    }

    private void getExtras() {
bundle=getArguments();
        if (bundle != null) {
           datalist = (ArrayList<HashMap<String, String>>) bundle.getSerializable("tablelist");
            colnames = (ArrayList<String>) bundle.getSerializable("colnameslist");
           
           
System.out.println("data list is "+datalist);

        }
    }

    private void displayMetricView()
    {
        metric_ll.removeAllViews();
        namesList=new ArrayList<String>();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
       params.setMargins(5,10,5,10);
        params.gravity= Gravity.CENTER;
        if(datalist!=null && datalist.size()>0) {
            for(int i=0;i<datalist.size();i++) {
                Button myButton = new Button(getActivity());
                String name=datalist.get(i).get("name");
                namesList.add(name);
                myButton.setText( name+"\n"+datalist.get(i).get("value"));
                myButton.setBackgroundResource(R.drawable.button_bckgrnd);
                myButton.setTextColor(randomColorsList.get(i));
                myButton.setId(i);
                myButton.setLayoutParams(params);
                metric_ll.addView(myButton);
            }
        }
    }

    @Override
    public void changeColor(int color, String legend) {
        int index=-1;
        if(randomColorsList!=null && randomColorsList.size()>0)
        {
            for(int i=0;i<namesList.size();i++)
            {


                String val=namesList.get(i);
                if(Utils.checkifavailable(val))
                {
                    if(Utils.checkifavailable(legend))
                    {
                        if(val.equalsIgnoreCase(legend))
                        {
                            index=i;
                            break;
                        }
                    }
                }
            }

            if(index>-1) {
                if(randomColorsList!=null && randomColorsList.size()>0) {
                    randomColorsList.set(index, color);
                    displayMetricView();

                }

            }
        }
    }

    }

