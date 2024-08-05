package com.dashboard.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.evrencoskun.tableview.adapter.AbstractTableAdapter;

import com.mview.airtel.R;
import com.newmview.wifi.bean.Cell;
import com.newmview.wifi.bean.ColumnHeader;
import com.newmview.wifi.bean.RowHeader;
import com.newmview.wifi.customview.MyCellViewHolder;
import com.newmview.wifi.customview.MyColumnHeaderViewHolder;
import com.newmview.wifi.customview.MyRowHeaderViewHolder;
import com.newmview.wifi.other.Utils;

public class TableViewAdapter extends AbstractTableAdapter<ColumnHeader, RowHeader, Cell> {
    Context mContext;
    private AbstractViewHolder holder;
    private Object cellItemModel;
    private int columnPosition;
    private int rowPosition;
    private String filterdValue;

    public TableViewAdapter(Context context) {
        super(context);
        mContext=context;

    }

    /**
     * This is sample CellViewHolder class
     * This viewHolder must be extended from AbstractViewHolder class instead of RecyclerView.ViewHolder.
     */

    @Override
    public AbstractViewHolder onCreateCellViewHolder(ViewGroup parent, int viewType) {
        // Get cell xml layout
        View layout = LayoutInflater.from(mContext).inflate(R.layout.my_cell_layout,
                parent, false);
        // Create a Custom ViewHolder for a Cell item.
        System.out.println("called cell viewholder");
      return new MyCellViewHolder(mContext,layout,filterdValue);
    }

    @Override
    public void onBindCellViewHolder(@NonNull AbstractViewHolder holder, @Nullable Cell cellItemModel, int columnPosition, int rowPosition) {
        this.holder = holder;
        this.cellItemModel = cellItemModel;
        this.columnPosition = columnPosition;
        this.rowPosition = rowPosition;
        Cell cell =  cellItemModel;
        // Get the holder to update cell item text
        MyCellViewHolder viewHolder = (MyCellViewHolder) holder;
        String data=cell.getData().toString();
        int textColor=cell.getTextColor();
       // if(Util.checkifavailable(data)) {
        if(Utils.checkifavailable(data)) {
            if (data.equalsIgnoreCase("null")) {
                    data="";
            }
        }
        else {
            data = "";
        }
            viewHolder.cell_textview.setText(data);
           if(Utils.checkifavailable(filterdValue)) {
                System.out.println("in if 1 "+filterdValue);

                if (data.toUpperCase().contains(filterdValue.toUpperCase())) {

                    System.out.println("in if 2 "+filterdValue);
                    viewHolder.cell_textview.setBackgroundColor(mContext.getResources().getColor(R.color.cpb_grey));

                    if(textColor!=0)
                    {
                        viewHolder.cell_textview.setTextColor(textColor);

                    }
                    else
                    {
                        viewHolder.cell_textview.setTextColor(mContext.getResources().getColor(R.color.white));
                    }
                   // viewHolder.cell_textview.setTextColor(mContext.getResources().getColor(R.color.white));
                } else {
                    System.out.println("in else 1 "+filterdValue);
                    viewHolder.cell_textview.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                    if(textColor!=0)
                    {
                        viewHolder.cell_textview.setTextColor(textColor);
                    }
                    else
                    {
                        viewHolder.cell_textview.setTextColor(mContext.getResources().getColor(R.color.black));
                    }

                }
            }
            else {
                System.out.println("in else 2 "+filterdValue);
                viewHolder.cell_textview.setBackgroundColor(mContext.getResources().getColor(R.color.white));

                System.out.println("text color is "+textColor);
                if(textColor!=0)
                {
                    viewHolder.cell_textview.setTextColor(mContext.getResources().getColor(R.color.app_theme));
                }
                else
                {
                    viewHolder.cell_textview.setTextColor(mContext.getResources().getColor(R.color.black));
                }

            }
      //  }


        // If your TableView should have auto resize for cells & columns.
        // Then you should consider the below lines. Otherwise, you can ignore them.

        // It is necessary to remeasure itself.
          //  viewHolder.cell_container.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT  ;
            // done by swapnil bansal 10/06/2021
           // viewHolder.cell_container.getLayoutParams().height = LinearLayout.LayoutParams.MATCH_PARENT  ;

            viewHolder.cell_textview.requestLayout();

    }
    @Override
    public AbstractViewHolder onCreateColumnHeaderViewHolder(ViewGroup parent, int viewType) {

        // Get Column Header xml Layout
        View layout = LayoutInflater.from(mContext).inflate(R.layout.table_view_column_header_layout, parent, false);

        // Create a ColumnHeader ViewHolder
        return new MyColumnHeaderViewHolder(mContext,layout,getTableView());
    }

    @Override
    public void onBindColumnHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable
            ColumnHeader columnHeaderItemModel, int columnPosition) {
        ColumnHeader columnHeader = (ColumnHeader) columnHeaderItemModel;

        MyColumnHeaderViewHolder columnHeaderViewHolder = (MyColumnHeaderViewHolder) holder;
        columnHeaderViewHolder.column_header_textview.setText(columnHeader.getData().toString());
       // columnHeaderViewHolder.column_header_textview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.up_grey,0,R.drawable.down_grey,0);

/*
        columnHeaderViewHolder.column_header_textview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int[] textLocation = new int[2];
                    columnHeaderViewHolder.column_header_textview.getLocationOnScreen(textLocation);

                    if (event.getRawX() <=  textLocation[0] +
                            columnHeaderViewHolder.column_header_textview.getTotalPaddingLeft()) {
                        SortState sortState = TableViewAdapter.this.getTableView()
                                .getSortingStatus(columnPosition);
                        if (sortState != SortState.ASCENDING) {
                            TableViewAdapter.this.getTableView().sortColumn(columnPosition, SortState.ASCENDING);
                        }
                       else

                        {
                            Util.showToast(mContext,"Already in ascending order according to this column!");
                        }
                        return true;
                    }


                    if (event.getRawX() >= textLocation[0] +  columnHeaderViewHolder.column_header_textview.getWidth() -
                            columnHeaderViewHolder.column_header_textview.getTotalPaddingRight()){
                        SortState sortState = TableViewAdapter.this.getTableView()
                                .getSortingStatus(columnPosition);
                        if (sortState == SortState.ASCENDING) {
                            TableViewAdapter.this.getTableView().sortColumn(columnPosition, SortState.DESCENDING);
                        }
                        else

                        {
                            Util.showToast(mContext,"Already in descending order according to this column!");
                        }
                        return true;
                    }
                }
                return true;
            }
        });
*/

        // If your TableView should have auto resize for cells & columns.
        // Then you should consider the below lines. Otherwise, you can ignore them.

        // It is necessary to remeasure itself.

        //created by swapnil bansal 10/06/2021
       // columnHeaderViewHolder.column_header_container.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
      //  columnHeaderViewHolder.column_header_container.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
        columnHeaderViewHolder.column_header_textview.requestLayout();

    }

    @Override
    public AbstractViewHolder onCreateRowHeaderViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(mContext).inflate(R.layout.table_view_row_header_layout, parent, false);
        return new MyRowHeaderViewHolder(layout);
    }

    @Override
    public void onBindRowHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable RowHeader rowHeaderItemModel, int rowPosition) {
        RowHeader rowHeader = (RowHeader) rowHeaderItemModel;
        MyRowHeaderViewHolder rowHeaderViewHolder = (MyRowHeaderViewHolder) holder;
        rowHeaderViewHolder.row_header_textview.setText(rowHeader.getData().toString());
        rowHeaderViewHolder.row_header_container.getLayoutParams().height=LinearLayout.LayoutParams.WRAP_CONTENT;
    }
    @NonNull
    @Override
    public View onCreateCornerView(@NonNull ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(R.layout.table_view_corner_layout, null);
    }

    @Override
    public int getColumnHeaderItemViewType(int columnPosition) {
        return 0;
    }

    @Override
    public int getRowHeaderItemViewType(int rowPosition) {
        return 0;
    }

    @Override
    public int getCellItemViewType(int columnPosition) {
        return 0;
    }

    public void setFilter(boolean b, String val) {
        this.filterdValue=val;

    }
}
