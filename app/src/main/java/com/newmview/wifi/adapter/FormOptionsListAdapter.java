/*
package com.visionairtel.wifi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.visionairtel.wifi.R;
import com.visionairtel.wifi.application.MviewApplication;
import com.visionairtel.wifi.bean.AlertOptions;
import com.visionairtel.wifi.bean.MapModel;
import com.visionairtel.wifi.customdialog.MyAlertDialog;
import com.visionairtel.wifi.database.DB_handler;
import com.visionairtel.wifi.databinding.AlertDialogLayoutBinding;
import com.visionairtel.wifi.enumtypes.AlertType;
import com.visionairtel.wifi.helper.Interfaces;
import com.visionairtel.wifi.other.Utils;

import java.util.ArrayList;

public class FormOptionsListAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final MyAlertDialog.AlertDialogInterface callback;
    private final AlertType type;
    private final String alternativeText;
    private final AlertDialogLayoutBinding layoutBinding;
    private ArrayList<String> list = new ArrayList<>();
    private View v;
    private AlertOptions mOptions;


    public FormOptionsListAdapter(Context context, ArrayList<String> list,
                                  MyAlertDialog.AlertDialogInterface callback,
                                  AlertType type, String alternativeText, AlertOptions options, AlertDialogLayoutBinding layoutBinding) {
        this.context = context;
        this.list = list;
        this.callback = callback;
        this.type = type;
        this.alternativeText = alternativeText;
        mOptions=options;
        this.layoutBinding=layoutBinding;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_items_view, parent, false);
        return new OptionListVh(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (list != null) {
            if (list.size() > 0) {
                String hint = list.get(position);
                if (Utils.checkifavailable(hint)) {

                    ((OptionListVh) holder).editText.setHint(hint);
                }

            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class OptionListVh extends RecyclerView.ViewHolder {
        DB_handler db = new DB_handler(MviewApplication.ctx);
        public EditText editText;

        public OptionListVh(View v) {
            super(v);
            editText = v.findViewById(R.id.editText);
        layoutBinding.doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos=getAdapterPosition();
                String name=null;
                String id=null;

                    layoutBinding.doneBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(pos==0) name=editText.getText().toString();
                            if(pos==1) id=editText.getText().toString();
                            if(Utils.checkifavailable(name) && Utils.checkifavailable(id))
                            {
                            MapModel mapModel=new MapModel();
                            mapModel.setSubscriberName(name);
                            mapModel.setSubscriberId(id);
                            callback.sendDetails
                        }
                    });

            }
        });
        }

    }
}
*/
