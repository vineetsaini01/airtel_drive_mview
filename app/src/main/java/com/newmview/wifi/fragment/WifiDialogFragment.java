package com.newmview.wifi.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.mview.airtel.R;

public class WifiDialogFragment extends DialogFragment {
    private RecyclerView mRecyclerView;
    // this method create view for your Dialog
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate layout with recycler view
        View v = inflater.inflate(R.layout.wifi_list, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.wifiRV);
   //     WifiAdapter wifiAdapter=new WifiAdapter()
        //setadapter
        //get your recycler view and populate it.
        return v;
    }
}
