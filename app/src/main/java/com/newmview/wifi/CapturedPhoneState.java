package com.newmview.wifi;

import android.telephony.CellLocation;
import android.telephony.SignalStrength;

/**
 * Created by Sharad Gupta on 9/27/2016.
 */
public class CapturedPhoneState {
//    public long captureTime;
//    public String time; // X Axis
//    public String batterylevel;
//    public String simDataUsed;
//    public String wifiDataUsed;
    public BasicPhoneState basicPhoneState;
    public float per4g, per3g, per2g, perNS;
    public String signalStrength;
    //public String LAC;
    public String cellLocation;
    public CellLocation cellLocationObj;
    public SignalStrength signalStrengthObj;
    public boolean roaming;
    public String networkType;
    public String source;

    public class BasicPhoneState {
        public String captureTime;
        public String time;
        public String batterylevel;
        public String simDataUsed;
        public String wifiDataUsed;
        public long captureTimeN;
        public String hourMin;
    }
}
