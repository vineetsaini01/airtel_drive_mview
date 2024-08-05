package com.newmview.wifi.customview;
/*
 * Copyright (c) 2018. Evren Coşkun
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */



import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.mview.airtel.R;
import com.newmview.wifi.activity.HeatMapListActivity;
import com.newmview.wifi.bean.Cell;


/**
 * Created by evrencoskun on 23/10/2017.
 */

public class MyCellViewHolder extends AbstractViewHolder implements View.OnClickListener {
    @NonNull
    public final TextView cell_textview;
    @NonNull
    public final LinearLayout cell_container;
    public String value;
    private Context mContext;

    public MyCellViewHolder(Context mContext, @NonNull View itemView, String filterdValue) {
        super(itemView);
        cell_textview = itemView.findViewById(R.id.cell_data);
        cell_container = itemView.findViewById(R.id.cell_container);
        value=filterdValue;
        this.mContext=mContext;
        System.out.println("value is "+value);
        cell_container.setOnClickListener(this);
    }

    public void setCell(@Nullable Cell cell) {
        cell_textview.setText(String.valueOf(cell.getData()));

        // If your TableView should have auto resize for cells & columns.
        // Then you should consider the below lines. Otherwise, you can ignore them.

        // It is necessary to remeasure itself.
        cell_container.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        cell_textview.requestLayout();
    }

    @Override
    public void onClick(View v) {
        int pos=getAdapterPosition();
        switch (v.getId())
        {
            case R.id.cell_container:
                if(mContext instanceof HeatMapListActivity)
                {
                    ((HeatMapListActivity)mContext).showHeatMapView(pos,cell_textview.getText().toString());
                }
                break;
        }
    }
}

