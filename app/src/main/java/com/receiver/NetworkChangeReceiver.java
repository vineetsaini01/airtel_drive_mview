package com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.newmview.wifi.CommonFunctions;
import com.newmview.wifi.other.Utils;

public class NetworkChangeReceiver extends BroadcastReceiver {
    public int TYPE_WIFI = 1;
    public int TYPE_MOBILE = 2;
    public int TYPE_NOT_CONNECTED = 0;
    @Override
    public void onReceive(final Context context, final Intent intent) {

       /* final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);*/

        String currentConnectivity= CommonFunctions.chkDataConnectivity(context);

            Utils.showToast(context,currentConnectivity);

       /* String statusString = getConnectivityStatusString(context);
        Toast.makeText(context, statusString, Toast.LENGTH_SHORT).show();*/

       /* if (wifi.isAvailable() || mobile.isAvailable()) {
            // Do something
            Utils.showToast(context,"wifi available "+wifi.isAvailable() +"mobile dtaa "+mobile.isAvailable());
            Log.d("Network Available ", "Flag No 1" +"wifiavailable "+wifi.isAvailable()  +"mobile data available "+mobile.isAvailable());
        }*/
    }


    public int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public String getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);
        String status = null;
        if (conn == TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }

}
