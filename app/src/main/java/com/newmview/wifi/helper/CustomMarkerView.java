package com.newmview.wifi.helper;

import android.content.Context;
import android.widget.TextView;


import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.mview.airtel.R;
import com.newmview.wifi.other.Utils;

import java.util.ArrayList;


/**
 * Created by functionapps on 11/1/2018.
 */


public class CustomMarkerView extends MarkerView {
    private final ArrayList<String> labels;
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */

    private TextView tvContent;

    public CustomMarkerView(Context context, int layoutResource, ArrayList<String> labels) {
        super(context, layoutResource);
        this.labels=labels;
        // this markerview only displays a textview
        tvContent=findViewById(R.id.tvContent);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        String date=null;
        if(labels!=null && labels.size()>0) {
            date = labels.get(e.getXIndex());
        }
        String markertext="";
        if(Utils.checkifavailable(date))
        {
            markertext=String.format("%s\n%s", e.getVal(), labels.get(e.getXIndex()));
        }
        else
        {
            markertext=String.format("%s",e.getVal());
        }
        tvContent.setText(markertext); // set the entry-value as the display text

    }


    @Override
    public int getXOffset(float xpos) {
        // this will center the marker-view horizontally
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float ypos) {
        // this will cause the marker-view to be above the selected value
        return -getHeight();
    }
}

