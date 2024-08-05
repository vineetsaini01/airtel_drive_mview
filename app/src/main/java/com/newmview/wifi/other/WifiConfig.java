package com.newmview.wifi.other;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.mview.airtel.R;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.bean.WifiModel;

import java.util.Objects;
import java.util.Random;

public class WifiConfig {
    private static final String TAG ="WifiConfig" ;

    /**
     * 2.4 GHz band first channel number
     * @hide
     */
    public static final int BAND_24_GHZ_FIRST_CH_NUM = 1;
    /**
     * 2.4 GHz band last channel number
     * @hide
     */
    public static final int BAND_24_GHZ_LAST_CH_NUM = 14;
    /**
     * 2.4 GHz band frequency of first channel in MHz
     * @hide
     */
    public static final int BAND_24_GHZ_START_FREQ_MHZ = 2412;
    /**
     * 2.4 GHz band frequency of last channel in MHz
     * @hide
     */
    public static final int BAND_24_GHZ_END_FREQ_MHZ = 2484;

    /**
     * 5 GHz band first channel number
     * @hide
     */
    public static final int BAND_5_GHZ_FIRST_CH_NUM = 32;
    /**
     * 5 GHz band last channel number
     * @hide
     */
    public static final int BAND_5_GHZ_LAST_CH_NUM = 173;
    /**
     * 5 GHz band frequency of first channel in MHz
     * @hide
     */
    public static final int BAND_5_GHZ_START_FREQ_MHZ = 5160;
    /**
     * 5 GHz band frequency of last channel in MHz
     * @hide
     */
    public static final int BAND_5_GHZ_END_FREQ_MHZ = 5865;

    /**
     * 6 GHz band first channel number
     *
     */
    public static final int BAND_6_GHZ_FIRST_CH_NUM = 1;
    /**
     * 6 GHz band last channel number
     *
     */
    public static final int BAND_6_GHZ_LAST_CH_NUM = 233;
    /**
     * 6 GHz band frequency of first channel in MHz
     *
     */
    public static final int BAND_6_GHZ_START_FREQ_MHZ = 5945;
    /**
     * 6 GHz band frequency of last channel in MHz
     *
     */
    public static final int BAND_6_GHZ_END_FREQ_MHZ = 7105;

    public static double getFreqBw(int freq)
    {
      if(is5GHz(freq))
          return 5;
      else if(is6GHz(freq))
          return 6;
      else if (is24GHz(freq))
          return 2.4;
        return 0;
    }

    public static WifiModel getConnectedWifiDetails()
    {
        Context context= MviewApplication.ctx;
        WifiModel wifiModel=null;
        final ConnectivityManager connMgr = (ConnectivityManager)
                Objects.requireNonNull(context).getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          NetworkCapabilities networkCapabilities= connMgr.getNetworkCapabilities(connMgr.getActiveNetwork());
          long down=networkCapabilities.getLinkDownstreamBandwidthKbps()/1000;
          long up=networkCapabilities.getLinkUpstreamBandwidthKbps()/1000;
          Log.i(TAG,"Downstream "+down +" upstream "+up);
        }*/
        if (wifi.isConnectedOrConnecting()) {
            Log.i(TAG,"Wifi si connectted");
            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wm.getConnectionInfo();
            int linkSpeed = wifiInfo.getLinkSpeed();
            String ss = wifiInfo.getSSID();
            int rs = wifiInfo.getRssi();
           int freq= wifiInfo.getFrequency();
            wifiModel=new WifiModel();
            wifiModel.setLinkSpeed(linkSpeed);
            wifiModel.setSignalStrength(rs);
            wifiModel.setSsidName(ss);
            wifiModel.setBSSID(wifiInfo.getBSSID());
            wifiModel.setWifiOn(true);
            wifiModel.setSupport5GHzBand(wm.is5GHzBandSupported());
Log.i(TAG,"Band sent "+ getFreqBw(freq));
            wifiModel.setFrequencyBandwidth(getFreqBw(freq));
            wifiModel.setChannelNo(convertFrequencyToChannel(freq)+"");
            Log.i(TAG,"Connected wifi name is "+wifiModel.getSsidName() +" with ss of "+rs +" and link speed of "+linkSpeed +" freq is "+freq);


        }
        else
        {

             wifiModel=new WifiModel();
            wifiModel.setLinkSpeed(0);
            wifiModel.setSignalStrength(0);
            wifiModel.setSsidName("NA");
            wifiModel.setBSSID("NA");
            wifiModel.setWifiOn(false);
        }
        return wifiModel;
    }

    public static int getColorForLinkSpeed(int linkSpeed)
    {
        int color=0;
        double freqBand=getConnectedWifiDetails().getFrequencyBandwidth();
        Log.i(TAG,"Freq band on touching "+freqBand);
        if(freqBand==2.4)
        {
            if (linkSpeed>=100) {

                // color= Color.parseColor("#006400");//dark green

                color=MviewApplication.ctx.getResources().getColor(R.color.dark_green);
                System.out.println("color is "+color);

            }
            else if (linkSpeed >= 50 && linkSpeed<100) {
                color= MviewApplication.ctx.getResources().getColor(R.color.light_green);
                //   color= Color.parseColor("#90EE90");//light green

                System.out.println("color is "+color);

            }
            else if(linkSpeed >=30 && linkSpeed<50)
            {
                color=MviewApplication.ctx.getResources().getColor(R.color.yellow);
                //    color= Color.parseColor("#FFA500");//orange

            }
            else if(linkSpeed>=0 && linkSpeed<30)
            {
                color= MviewApplication.ctx.getResources().getColor(R.color.red);
            }
        }
        else if(freqBand==5.0)
        {
            if (linkSpeed>=400) {

                // color= Color.parseColor("#006400");//dark green
                Log.i(TAG,"Link speed "+linkSpeed +" so returning dark green color");
                color=MviewApplication.ctx.getResources().getColor(R.color.dark_green);
                System.out.println("color is "+color);

            }
            else if (linkSpeed >= 300 && linkSpeed<400) {
                Log.i(TAG,"Link speed "+linkSpeed +" so returning light green color");
                color= MviewApplication.ctx.getResources().getColor(R.color.light_green);
                //   color= Color.parseColor("#90EE90");//light green

                System.out.println("color is "+color);

            }
            else if(linkSpeed >=200 && linkSpeed<300)
            {
                Log.i(TAG,"Link speed "+linkSpeed +" so returning yellow color");
                color=MviewApplication.ctx.getResources().getColor(R.color.yellow);
                //    color= Color.parseColor("#FFA500");//orange

            }
            else if(linkSpeed>=0 && linkSpeed<200)
            {
                Log.i(TAG,"Link speed "+linkSpeed +" so returning red color");
                color= MviewApplication.ctx.getResources().getColor(R.color.red);
            }
        }

        return color;
    }
    public  static  int getColorForWifiSignalStrength(int signalStrength)
    {
        Context context;
        int color = 0;
        if (signalStrength<0 && signalStrength>=-30) {

           // color= Color.parseColor("#006400");//dark green
           color=MviewApplication.ctx.getResources().getColor(R.color.dark_green);
            System.out.println("color is "+color);

        }
        else if (signalStrength<-30 && signalStrength>=-40) {
           color= MviewApplication.ctx.getResources().getColor(R.color.light_green);
         //   color= Color.parseColor("#90EE90");//light green

            System.out.println("color is "+color);

        }
        else if(signalStrength<-40 && signalStrength>=-70)
        {
            color=MviewApplication.ctx.getResources().getColor(R.color.yellow);
        //    color= Color.parseColor("#FFA500");//orange

        }
        else if(signalStrength<-70)
        {
            color= MviewApplication.ctx.getResources().getColor(R.color.red);
        }
       /* if (signalStrength<=0 && signalStrength>=-10) {
            color= Color.parseColor("#023020");//dark green

            System.out.println("color is "+color);

        }
        else if(signalStrength<-10 && signalStrength>=-20)
        {
            color= Color.GREEN;

        }
        else if(signalStrength<-20 && signalStrength >=-40)
        {
            color=Color.parseColor("#90EE90");
        }

        else if(signalStrength<-40 && signalStrength>=-50)
        {
            //color=Color.BLUE; System.out.println("color 1 is "+color);
            color=Color.parseColor("#0000FF");
        }
        else if(signalStrength<-50 && signalStrength>=-60)
        {
            color=Color.parseColor("#ADD8E6");//light blue

        }
        else if(signalStrength<-60 && signalStrength>=-70)
        {
            color=Color.YELLOW;
        }
        else if(signalStrength<-70 && signalStrength>=-80)
        {
            color=Color.parseColor("#FFA500");
        }
        else if(signalStrength<-80)
        {
            color=Color.RED;
        }*/

        return color;
    }

    /**
     * Utility function to check if a frequency within 2.4 GHz band
     * @param freqMhz frequency in MHz
     * @return true if within 2.4GHz, false otherwise
     *
     * @hide
     */
    public static boolean is24GHz(int freqMhz) {
        return freqMhz >= BAND_24_GHZ_START_FREQ_MHZ && freqMhz <= BAND_24_GHZ_END_FREQ_MHZ;
    }

    /**
     * Utility function to check if a frequency within 5 GHz band
     * @param freqMhz frequency in MHz
     * @return true if within 5GHz, false otherwise
     *
     * @hide
     */
    public static boolean is5GHz(int freqMhz) {
        return freqMhz >=  BAND_5_GHZ_START_FREQ_MHZ && freqMhz <= BAND_5_GHZ_END_FREQ_MHZ;
    }

    /**
     * Utility function to check if a frequency within 6 GHz band
     * @param freqMhz
     * @return true if within 6GHz, false otherwise
     *
     * @hide
     */
    public static boolean is6GHz(int freqMhz) {
        return freqMhz >= BAND_6_GHZ_START_FREQ_MHZ && freqMhz <= BAND_6_GHZ_END_FREQ_MHZ;
    }
  /*  public static int convertFrequencyToChannel(int freq) {
        if (freq >= 2412 && freq <= 2484) {
            return (freq - 2412) / 5 + 1;
        } else if (freq >= 5170 && freq <= 5825) {
            return (freq - 5170) / 5 + 34;
        } else {
            return -1;
        }
    }*/
    /* Random number generator used for AP channel selection. */
    private static final Random sRandom = new Random();
    /**
     * Convert frequency to channel.
     * @param frequency frequency to convert
     * @return channel number associated with given frequency, -1 if no match
     */
    public static int convertFrequencyToChannel(int frequency) {
        if (frequency >= 2412 && frequency <= 2472) {
            return (frequency - 2412) / 5 + 1;
        } else if (frequency == 2484) {
            return 14;
        } else if (frequency >= 5170  &&  frequency <= 5825) {
            /* DFS is included. */
            return (frequency - 5170) / 5 + 34;
        }
        return -1;
    }
    /**
     * Return a channel number for AP setup based on the frequency band.
     * @param apBand 0 for 2GHz, 1 for 5GHz
     * @param allowed2GChannels list of allowed 2GHz channels
     * @param allowed5GFreqList list of allowed 5GHz frequencies
     * @return a valid channel number on success, -1 on failure.
     */
/*
    public static int chooseApChannel(int apBand,
                                      ArrayList<Integer> allowed2GChannels,
                                      int[] allowed5GFreqList) {
        if (apBand != WifiConfiguration.AP_BAND_2GHZ
                && apBand != WifiConfiguration.AP_BAND_5GHZ) {
            Log.e(TAG, "Invalid band: " + apBand);
            return -1;
        }
        if (apBand == WifiConfiguration.AP_BAND_2GHZ)  {
            */
/* Select a channel from 2GHz band. *//*

            if (allowed2GChannels == null || allowed2GChannels.size() == 0) {
                Log.d(TAG, "2GHz allowed channel list not specified");
                */
/* Use default channel. *//*

                return DEFAULT_AP_CHANNEL;
            }
            */
/* Pick a random channel. *//*

            int index = sRandom.nextInt(allowed2GChannels.size());
            return allowed2GChannels.get(index).intValue();
        }
        */
/* 5G without DFS. *//*

        if (allowed5GFreqList != null && allowed5GFreqList.length > 0) {
            */
/* Pick a random channel from the list of supported channels. *//*

            return convertFrequencyToChannel(
                    allowed5GFreqList[sRandom.nextInt(allowed5GFreqList.length)]);
        }
        Log.e(TAG, "No available channels on 5GHz band");
        return -1;
    }
*/

   /* public static boolean ifDualBandIsSupported(WifiManager wifiManager)
    {
        if(wifiManager.is5GHzBandSupported())
    }*/

}
