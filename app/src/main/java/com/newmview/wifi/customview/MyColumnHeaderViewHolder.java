package com.newmview.wifi.customview;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evrencoskun.tableview.ITableView;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractSorterViewHolder;
import com.evrencoskun.tableview.sort.SortState;
import com.mview.airtel.R;
import com.newmview.wifi.bean.ColumnHeader;


public class MyColumnHeaderViewHolder extends AbstractSorterViewHolder {
    private static final String LOG_TAG = MyColumnHeaderViewHolder.class.getSimpleName();

    @NonNull
    public final LinearLayout column_header_container;
    @NonNull
    public final TextView column_header_textview;
    @NonNull
    public final ImageButton column_header_sortButton;
    @Nullable
    public final ITableView tableView;
    private final ImageButton column_header_asc_sortButton;
    private final Context context;

    public MyColumnHeaderViewHolder(Context context, @NonNull View itemView, @Nullable ITableView tableView) {
        super(itemView);
        this.tableView = tableView;
        this.context=context;
        column_header_textview = itemView.findViewById(R.id.column_header_textView);
        column_header_container = itemView.findViewById(R.id.column_header_container);
        column_header_sortButton = itemView.findViewById(R.id.column_header_sortButton);
        column_header_asc_sortButton=itemView.findViewById(R.id.column_header_asc_sortButton);

        // Set click listener to the sort button
        column_header_sortButton.setOnClickListener(mSortButtonClickListener);
        column_header_textview.setOnClickListener(mHeaderClickListener);
        column_header_asc_sortButton.setOnClickListener(mSortAscButtonClickListener);

    }

    /**
     * This method is calling from onBindColumnHeaderHolder on TableViewAdapter
     */
    public void setColumnHeader(@Nullable ColumnHeader columnHeader) {
        column_header_textview.setText(String.valueOf(columnHeader.getData()));

        // If your TableView should have auto resize for cells & columns.
        // Then you should consider the below lines. Otherwise, you can remove them.

        // It is necessary to remeasure itself.
        column_header_container.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        column_header_textview.requestLayout();
    }
    @NonNull
    private View.OnClickListener mHeaderClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            tableView.sortColumn(getAdapterPosition(), SortState.UNSORTED);
        }
    };

    @NonNull
    private View.OnClickListener mSortButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            column_header_textview.setBackgroundColor(context.getResources().getColor(R.color.app_theme));
            column_header_textview.setTextColor(context.getResources().getColor(R.color.white));
            // BY SWAPNIL 11/25/2022
          //  tableView.sortColumn(getAdapterPosition(), SortState.DESCENDING);
            tableView.sortRowHeader(SortState.DESCENDING);
            /*if(context instanceof GraphDetailsActivity)
            {
                ((GraphDetailsActivity)context).showResetSortingButton();
            }*/
         /*  if (getSortState() == SortState.ASCENDING) {
                tableView.sortColumn(getAdapterPosition(), SortState.DESCENDING);
            } else if (getSortState() == SortState.DESCENDING) {
                tableView.sortColumn(getAdapterPosition(), SortState.ASCENDING);
            } else {
                // Default one
                tableView.sortColumn(getAdapterPosition(), SortState.DESCENDING);
            }*/

        }
    };
    private View.OnClickListener mSortAscButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            column_header_textview.setBackgroundColor(context.getResources().getColor(R.color.app_theme));
            // BY SWAPNIL 11/25/2022
            //tableView.sortColumn(getAdapterPosition(), SortState.ASCENDING);
            tableView.sortRowHeader(SortState.ASCENDING);
            column_header_textview.setTextColor(context.getResources().getColor(R.color.white));
           /* if(context instanceof GraphDetailsActivity)
            {
                ((GraphDetailsActivity)context).showResetSortingButton();
            }*/
        }
    };

    @Override
    public void onSortingStatusChanged(@NonNull SortState sortState) {
        Log.e(LOG_TAG, " + onSortingStatusChanged : x:  " + getAdapterPosition() + " old state " + getSortState() + " current state : " + sortState + " visiblity: " +column_header_sortButton.getVisibility());
        super.onSortingStatusChanged(sortState);
        // It is necessary to remeasure itself.
        column_header_container.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        // controlSortState(sortState);
        Log.e(LOG_TAG, " - onSortingStatusChanged : x:  " + getAdapterPosition() + " old state " + getSortState() + " current state : " + sortState + " visiblity: " + column_header_sortButton.getVisibility());
        column_header_textview.requestLayout();
        column_header_sortButton.requestLayout();
        column_header_container.requestLayout();
        itemView.requestLayout();
    }


    private void controlSortState(@NonNull SortState sortState) {
        if (sortState == SortState.ASCENDING) {
            // column_header_sortButton.setVisibility(View.VISIBLE);
            column_header_sortButton.setImageResource(R.drawable.down);

        } else if (sortState == SortState.DESCENDING) {
            // column_header_sortButton.setVisibility(View.VISIBLE);
            column_header_sortButton.setImageResource(R.drawable.up);
        } else {
            // column_header_sortButton.setVisibility(View.INVISIBLE);
        }
    }
}
