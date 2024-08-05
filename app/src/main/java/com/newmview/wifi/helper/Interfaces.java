package com.newmview.wifi.helper;

import android.graphics.Point;
import android.view.MenuItem;
import android.view.View;

import com.newmview.wifi.bean.Pinger;
import com.newmview.wifi.customview.ViewRevealManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Interfaces {

    public interface SpeedResult
    {
        public void onSpeedResultObtained(final String[] time);
        public void parseSpeedResult(Pinger response);

    }
    public interface ResultListner
    {
        public void onResultObtained(boolean status);

    }

    public interface SpeedTestResult
    {
        public void onSpeedResultObtained(float download, float upload);
        public void parseSpeedResult(Pinger response);

    }
    public interface  Callback_result
    {
        public void onSuccess(JSONObject jsonObject);
        public void onFail(int err_code, String err_msg);

    }
    public interface DismissDialogInterface
    {
        public void dismiss();
    }
    public interface onButtonClickedListener
    {
        public void buttonClicked(int id);
    }
    public interface ConvexHullAlgorithm
    {
        ArrayList<Point> execute(ArrayList<Point> points);
    }
    public interface PingResult
    {
        public void onPingResultObtained(final String time);
        public void parsePingResult(Pinger response);

    }
    public interface PopupButtonClickListener
    {
        public void onButtonClicked(View v);
    }
    public interface SaveSuccessfullListener
    {
        public void saveSuccessfull(String name,String path);
    }
    public interface onMenuButtonClickListener
    {
        public void  onMenuButtonClicked(MenuItem v);
    }
    public  interface Swipelisteners
    {
        public void swipeleft();
        public void swiperight();
    }
    public interface RevealViewGroup {

        /**
         * @return Bridge between view and circular reveal animation
         */
        ViewRevealManager getViewRevealManager();

        /**
         *
         * @param manager
         */
        void setViewRevealManager(ViewRevealManager manager);
    }
    public interface EventListener {
        void onRemovePlanEvent(String id);
    }

    public interface ChangeColorListener
    {
        public  void changeColor(int color,String legend);
    }
    public interface DialogButtonClickListener
    {
        public  void onPositiveButtonClicked(String text);
        public void onNegativeButtonClicked(String text);
        public void onDialogDismissed(String text);
    }
    public interface RefreshDataListener
    {
        public void refreshOnTouch();
    }
    public interface FragmentPopListener
    {
        public void fragmentPoppedOff(String tag);
    }
    public interface MarkerTouchListener
    {
        public void onMarkerTouched(String id);
    }

    public interface  Network_list
    {
        public void onSuccess(ArrayList<HashMap<String,String>> list);
        void onFailure();


    }
    public interface  NewNetwork_list
    {
        public void onSuccess(ArrayList<HashMap<String,String>> list);
        void onFailure();


    }
    public interface  Neighbour_cell_List
    {


//        public  void onSuccess(ArrayList<HashMap<Integer, String>> list);

//        void onSuccess(ArrayList<HashMap<Integer, Integer>> list, ArrayList<String> params);

        void onSuccess(ArrayList<HashMap<String, String>> neighboringCellList1,String type);
        void onFailure();
    }

}
