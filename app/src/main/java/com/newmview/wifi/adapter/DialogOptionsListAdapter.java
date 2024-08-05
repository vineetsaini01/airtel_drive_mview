package com.newmview.wifi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mview.airtel.R;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.bean.HistoryModel;
import com.newmview.wifi.customdialog.MyAlertDialog;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.enumtypes.AlertType;
import com.newmview.wifi.other.Utils;

import java.util.ArrayList;

public class DialogOptionsListAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final MyAlertDialog.AlertDialogInterface callback;
    private final AlertType type;
    private final String alternativeText;
    private final ArrayList<HistoryModel> history;
    private ArrayList<String> list=new ArrayList<>();
    private View v;


    public DialogOptionsListAdapter(Context context, ArrayList<String> list,
                                    MyAlertDialog.AlertDialogInterface callback, AlertType type, String alternativeText, ArrayList<HistoryModel> history) {
        this.context=context;
        this.list=list;
        this.callback=callback;
        this.type=type;
        this.alternativeText=alternativeText;
        this.history=history;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_view, parent, false);
        return new OptionListVh(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
if(list!=null)
{
    if(list.size()>0)
    {
        String title=list.get(position);
        if(Utils.checkifavailable(title))
        {

                ((OptionListVh) holder).titleTv.setText(title);
        }

    }
}
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class OptionListVh extends RecyclerView.ViewHolder implements View.OnClickListener {
        DB_handler db=new DB_handler(MviewApplication.ctx);
        public TextView titleTv;
        public OptionListVh(View v) {
            super(v);
            titleTv=v.findViewById(R.id.titleTv);

            titleTv.setOnClickListener(this);

    }

        @Override
        public void onClick(View v) {
            int pos=getAdapterPosition();
            if(v instanceof TextView)
            {
             //   Utils.showToast(context,"Pos is "+pos);
                if(pos==DialogOptionsListAdapter.this.list.size()-1)
                {
                    callback.listOptionClicked(((TextView) v).getText().toString());

/*
                    Utils.showAlert(true, DialogOptionsListAdapter.this.alternativeText,
                            context, new Interfaces.DialogButtonClickListener() {
                                @Override
                                public void onPositiveButtonClicked(String text) {
                                    if(callback!=null) {
                                        callback.listOptionClicked(text);
                                    }
                                }

                                @Override
                                public void onNegativeButtonClicked(String text) {

                                }

                                @Override
                                public void onDialogDismissed(String text) {

                                }
                            });
*/
                }
                else
                {
                    if(callback!=null)
                        callback.listOptionClicked(((TextView) v).getText().toString());

                }

            }

        }
    }
}
