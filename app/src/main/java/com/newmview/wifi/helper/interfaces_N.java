package com.newmview.wifi.helper;

public class interfaces_N {
    public interface PingResult
    {
        public void onPingResultObtained(final String time);

    }
    public interface ChangeColorListener
    {
        public  void changeColor(int color,String legend);
    }
}
