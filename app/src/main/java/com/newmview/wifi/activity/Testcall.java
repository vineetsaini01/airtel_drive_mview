package com.newmview.wifi.activity;

import android.os.Build;
import androidx.annotation.RequiresApi;
import android.telecom.Call;

@RequiresApi(api = Build.VERSION_CODES.M)
public class Testcall extends Call.Callback {
    @Override
    public void onStateChanged(Call call, int state) {
        super.onStateChanged(call, state);
    }

    @Override
    public void onDetailsChanged(Call call, Call.Details details) {
        super.onDetailsChanged(call, details);
    }
}
