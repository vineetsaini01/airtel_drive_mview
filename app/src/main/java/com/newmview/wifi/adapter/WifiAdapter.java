package com.newmview.wifi.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import androidx.recyclerview.widget.RecyclerView;

//import com.newmview.wifi.BR;
import com.mview.airtel.R;
import com.newmview.wifi.bean.WifiModel;
import com.mview.airtel.databinding.WifiDetailsBinding;

import java.util.List;



public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.WifiVh> {
    private static final String TAG ="WifiAdapter" ;
    private  Context context;
    List<WifiModel> mScanResults;
    private WifiVh wifiViewHolder;

   /* public WifiAdapter(List<ScanResult> scanResults, Context context) {
        mScanResults=scanResults;
        this.context=context;
    }*/

    public WifiAdapter(List<WifiModel> scanResults) {
        mScanResults=scanResults;
    }

    @NonNull
    @Override
    public WifiAdapter.WifiVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       /* View v= LayoutInflater.from(context).inflate(R.layout.wifi_details,parent,false);
        WifiVh wifiVh= new WifiVh(v, context);
        return wifiVh;*/

        WifiDetailsBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.wifi_details, parent, false);

        return new WifiVh(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WifiVh holder, int position) {
        WifiModel dataModel = mScanResults.get(position);
        Log.i(TAG,"Wifi Names "+dataModel.getSsidName());
        holder.bind(dataModel);
    }


   /* @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            WifiModel dataModel = mScanResults.get(position);
        Log.i(TAG,"Wifi Names "+dataModel.getSsidName());
            ((WifiVh) holder).bind(dataModel);


    }*/



    @Override
    public int getItemCount() {
        return mScanResults.size();
    }

    public void refreshList(List<WifiModel> scanResults) {
        mScanResults=scanResults;
        notifyDataSetChanged();
    }

    public   class WifiVh extends RecyclerView.ViewHolder {
        public  TextView wifiTv;
        public WifiDetailsBinding binding;


        public WifiVh(WifiDetailsBinding binding) {
            super(binding.getRoot());
            this.binding=binding;

        }
        public void bind(Object obj) {
//          binding.setVariable(BR.viewModel,obj);
            //binding.setVariable(BR.viewMod.l, obj);
            binding.executePendingBindings();
        }
    }
}
