package com.newmview.wifi.customview;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.mview.airtel.R;


public class MyRowHeaderViewHolder extends AbstractViewHolder {

    @NonNull
    public final TextView row_header_textview;

    @NonNull
    public final LinearLayout row_header_container;
    public MyRowHeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        row_header_textview = itemView.findViewById(R.id.row_header_textview);
        row_header_container=itemView.findViewById(R.id.row_header_container);

    }
}
