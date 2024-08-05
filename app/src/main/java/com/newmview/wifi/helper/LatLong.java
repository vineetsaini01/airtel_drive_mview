package com.newmview.wifi.helper;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

import static com.newmview.wifi.helper.CommonUtil.stateslist;

public class LatLong {



    public static void addStates() {
        HashMap<String, LatLng> hp=new HashMap<>();
        stateslist=new ArrayList<>();
       hp.put("Andaman and Nicobar",	new LatLng(11.66702557,92.73598262));
       hp.put("Andhra Pradesh",new LatLng(14.7504291,78.57002559));
       hp.put("Arunachal Pradesh",new LatLng(27.10039878,93.61660071));
       hp.put("Assam",new LatLng(26.7499809,94.21666744));
       hp.put("Bihar",new LatLng(25.78541445,87.4799727));
       hp.put("Chattisgarh",new LatLng(21.27,81.86));
        hp.put("Delhi",new LatLng(28.6273928, 77.1716954));
       hp.put("Goa",new LatLng(15.29,74.12));
        hp.put("Gujarat",new LatLng(22.309425	,72.136230));
        hp.put("Haryana",new LatLng(29.238478	,76.431885));
        hp.put("Himachal Pradesh",new LatLng(32.084206,	77.571167));
        hp.put("Jammu and Kashmir",new LatLng(33.77,	76.57));
        hp.put("Jharkhand",new LatLng(23.61,85.27));
        hp.put("Karnataka",new LatLng(15.317277	,75.713890));
        hp.put("Kerala",new LatLng(10.850516,	76.271080));
        hp.put("Madhya Pradesh",new LatLng(23.473324,	77.947998));
        hp.put("Maharashtra",new LatLng(19.601194,	75.552979));
        hp.put("Manipur",new LatLng(24.66,93.90));
        hp.put("Meghalaya",new LatLng(25.46,91.35));
        hp.put("Mizoram",new LatLng(23.16,	92.93));
        hp.put("Nagaland",new LatLng(26.15,	94.56));
        hp.put("Odisha",new LatLng(20.5431241,84.6897321));
        hp.put("Punjab",new LatLng(31.14,75.34));
        hp.put("Rajasthan",new LatLng(	27.391277,	73.432617));
        hp.put("Sikkim",new LatLng(	27.53,	88.51));
        hp.put("Tamil Nadu",new LatLng(11.059821,	78.387451));
        hp.put("Tripura",new LatLng(23.94,91.98));
        hp.put("Uttarakhand",new LatLng(30.06,79.01));
        hp.put("Uttar Pradesh",new LatLng(28.207609,	79.826660));
        hp.put("West Bengal",new LatLng(22.978624	,87.747803));
        hp.put("Telangana",new LatLng(17.123184	,79.208824));
        hp.put("Lakshadweep",new LatLng(10.00,73.00));
        hp.put("Puducherry",new LatLng(11.56, 79.53));
        hp.put("Dadra and Nagar Haveli",new LatLng(30.42, 76.54));
        hp.put("Daman and Diu",new LatLng(20.25 ,72.53));
        hp.put("Chandigarh",new LatLng(30.44, 76.54));













    //   hp.put("Jammu and Kashmir")
       stateslist.add(hp);


    }

}
