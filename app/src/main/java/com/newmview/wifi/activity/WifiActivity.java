package com.newmview.wifi.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mview.airtel.R;
import com.newmview.wifi.adapter.WifiAdapter;
import com.newmview.wifi.bean.WifiModel;
import com.newmview.wifi.broadcastreceiver.WiFiDirectBroadcastReceiver;
import com.mview.airtel.databinding.ActivityWifiBinding;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;
import com.newmview.wifi.viewmodel.MainViewModelFactory;
import com.newmview.wifi.viewmodel.WifiViewModel;
import com.receiver.ConnectedWifiReceiver;
import com.receiver.WifiScanReceiver;

import java.util.ArrayList;
import java.util.List;

import static com.newmview.wifi.other.Utils.checkForGps;
import static com.newmview.wifi.other.WifiConfig.getConnectedWifiDetails;

public class WifiActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "WifiActivity";
    private WifiViewModel viewModel;
    private ActivityWifiBinding wifiBinding;
    private WifiManager mWifiManager;
    private ConnectedWifiReceiver mReceiver;
    private WifiScanReceiver mWifiScanReceiver;
    private Handler handler = new Handler();
    private long delay = 5;//seconds
    private Runnable runnable;
    private Toolbar toolbar;
    private List<WifiModel> mScanResults = new ArrayList<>();
    private WifiAdapter wifiAdapter;
    private boolean anySSIDConnected;
    private boolean alertVisible;
    private String flowType;
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    BroadcastReceiver receiver;
    private IntentFilter intentFilter;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changeSSIDBtn:
                takeUserToWifiSettingsPage();
                break;
            /*case R.id.viewMapsBtn:
                Intent i=new Intent(WifiActivity.this,HeatMapListActivity.class);
                startActivity(i);
                break;*/
        }
    }

    /*    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu items for use in the action bar
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.refresh_menu, menu);

            return super.onCreateOptionsMenu(menu);
        }
        public interface CustomClickListener {
            void buttonClicked();
        }*/
    public interface CustomClickListener {
        void buttonClicked();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //     ActivityWifiBinding wifiBinding=
      /*  requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.PROGRESS_VISIBILITY_ON);
        setProgressBarIndeterminateVisibility(true);*/
        getExtras();
        init();
       // wifiPeerManager();

    }

    private void wifiPeerManager() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        else {
            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(int reasonCode) {

                }
            });
        }
    }

    private void getExtras() {
    Bundle bundle=getIntent().getExtras();
    if(bundle!=null)
    {
        flowType=bundle.getString("flowType");
    }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // add data source


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        checkForGps(WifiActivity.this);
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 90);
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 87);
        }
        else
        {
            refreshData();
        }
       // registerReceiver(receiver, intentFilter);

        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    //    unregisterReceiver(receiver);
    }

    private void refreshData() {
        Log.i(TAG,"Running first at "+ Utils.getDateTime());
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                Log.i(TAG,"Running at "+ Utils.getDateTime());
                mWifiManager.startScan();
                viewModel.refresh(mWifiManager);
               // WifiListRepository.getInstance().addDataSource(mWifiManager);
                handler.postDelayed(runnable, delay*1000);

            }
        }, delay*1000);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                /*  NavUtils.navigateUpFromSameTask(this);*/
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }
    private void setValuesToWifiGauge(WifiModel scanResult) {
        if(scanResult!=null) {
            boolean isConnected = scanResult.isConnected();
            if (isConnected) {
                String ssidName = scanResult.getSsidName();
                int linkSpeed = scanResult.getLinkSpeed();
                int signalStrength = scanResult.getSignalStrength();
                if (!Utils.checkifavailable(ssidName)) {
                    ssidName = "Unknown";
                }
             double band=scanResult.getFrequencyBandwidth();
                String channelNo=scanResult.getChannelNo();
                Log.i(TAG,"Band obtained "+band);
                wifiBinding.connectedWifiTv.setText("Connected WiFi : " + ssidName +" ("+band+"GHz)" +" Ch-"+channelNo);
                wifiBinding.ssGauge.setValue(signalStrength);
                wifiBinding.linkSpeed.setValue(linkSpeed);
                boolean supports=scanResult.isSupporting5GHzBand();

                if(supports)
                {
                    wifiBinding.supports5gTv.setVisibility(View.GONE);
                }
                else
                {
                    wifiBinding.supports5gTv.setVisibility(View.VISIBLE);
                    wifiBinding.supports5gTv.setText("Your device does not support 5GHz band");
                }


            } else {

                wifiBinding.connectedWifiTv.setText(Constants.WIFI_NOT_CONNECTED);
                wifiBinding.ssGauge.setValue(0);
                wifiBinding.linkSpeed.setValue(0);
            }
        }
        else
        {
            wifiBinding.connectedWifiTv.setText(Constants.WIFI_NOT_CONNECTED);
            wifiBinding.ssGauge.setValue(0);
            wifiBinding.linkSpeed.setValue(0);
}
     
    }
    private void init()
    {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        wifiBinding= DataBindingUtil.setContentView(this, R.layout.activity_wifi);

        setToolbar();
if(Utils.checkifavailable(flowType))
{
    if(flowType.equalsIgnoreCase("rapid"))
    {
        wifiBinding.createButton.setVisibility(View.GONE);
    }
}
        populateWifiList();
       // setProgressBarIndeterminateVisibility(true); // turn progress on

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        viewModel=new ViewModelProvider(this,
                new MainViewModelFactory(WifiActivity.this, mWifiManager)).get(WifiViewModel.class);
        wifiBinding.setViewModel(new WifiModel());
        //Set values for link speed of connected wifi
        seBasicValuesToGauges();
        setClickListeners();
        wifiBinding.supports5gTv.setText("");
       

        viewModel.getWifiDetailsObservable().observe(WifiActivity.this, new Observer<List<WifiModel>>() {
    @Override
    public void onChanged(List<WifiModel> scanResults) {

        if(scanResults!=null)
        {
            mScanResults=new ArrayList<>();
if(scanResults.size()>0) {
    //wifiBinding.toolbarProgressBar.setVisibility(View.GONE); // turn progress on

  for(int i=0;i<scanResults.size();i++)
  {
      Log.i(TAG,"Scan Results in WifiActivity "+scanResults.get(i).getSsidName() +" is connected "+scanResults.get(i).isConnected());
     /* Log.i(TAG, "Scan Results changed..." + scanResults.get(i).getSsidName() + " Link speed "+ scanResults.get(i).getLinkSpeed() +
              "  ss " + scanResults.get(i).getSignalStrength());*/

      boolean isConnected=scanResults.get(i).isConnected();
      if(isConnected)
      {
          anySSIDConnected=true;
          alertVisible=false;
          setValuesToWifiGauge(scanResults.get(i));
      }
      else {

          mScanResults.add(scanResults.get(i));
      }

  }

  if(!anySSIDConnected)
  {
      checkIfWifiIsOnOrNot();
      setValuesToWifiGauge(null);

  }
}
else
{
    checkIfWifiIsOnOrNot();
}

        }
        else
        {
            checkIfWifiIsOnOrNot();

        }
        wifiAdapter.refreshList(mScanResults);
        anySSIDConnected=false;
       // alertVisible=false;
    }
});
}

    private void seBasicValuesToGauges() {
        wifiBinding.ssGauge.setMaxValue(-10);
        wifiBinding.ssGauge.setMinValue(-90);
        wifiBinding.ssGauge.setMaxValueTextColor(Color.GREEN);
        wifiBinding.ssGauge.setMinValueTextColor(Color.RED);
        wifiBinding.linkSpeed.setMaxValue(1000);
        wifiBinding.linkSpeed.setMinValue(0);
        wifiBinding.linkSpeed.setMaxValueTextColor(Color.GREEN);
        wifiBinding.linkSpeed.setMinValueTextColor(Color.RED);
    }

    private void checkIfWifiIsOnOrNot() {
        WifiModel wifiModel= getConnectedWifiDetails();
        Log.i(TAG,"Wifi is ON or Not "+wifiModel.isWifiOn());
        if(wifiModel!=null)
        {

            if(!wifiModel.isWifiOn())
            {

                    promptUserToTurnWifiOn();

            }

        }
        else
        {
promptUserToTurnWifiOn();
        }
    }

    private void promptUserToTurnWifiOn() {
        if(!alertVisible) {
            alertVisible = true;
            Utils.showAlert(false, Constants.TURN_ON_WIFI, WifiActivity.this, new Interfaces.DialogButtonClickListener() {
                @Override
                public void onPositiveButtonClicked(String text) {
                    takeUserToWifiSettingsPage();
alertVisible=false;
                }

                @Override
                public void onNegativeButtonClicked(String text) {

                }

                @Override
                public void onDialogDismissed(String text) {

                }
            });
        }

    }

    private void takeUserToWifiSettingsPage() {
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    private void setClickListeners() {
        wifiBinding.setClickListener(new CustomClickListener() {
            @Override
            public void buttonClicked() {
            }
        });
        wifiBinding.changeSSIDBtn.setOnClickListener(this);
        wifiBinding.viewMapsBtn.setOnClickListener(this);
    }

    private void setToolbar() {
      //  setSupportActionBar(wifiBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable
                = new ColorDrawable(getResources().getColor(R.color.white));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_app_icon);
    }

    private void populateWifiList() {
        wifiBinding.wifiRv.setLayoutManager(new LinearLayoutManager(WifiActivity.this));
       wifiAdapter=new WifiAdapter(mScanResults);
        wifiBinding.wifiRv.setAdapter(wifiAdapter);
    }

}
