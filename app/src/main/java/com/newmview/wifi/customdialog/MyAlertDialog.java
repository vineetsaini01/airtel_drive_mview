package com.newmview.wifi.customdialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mview.airtel.R;
import com.newmview.wifi.adapter.DialogOptionsListAdapter;
import com.newmview.wifi.bean.AlertOptions;
import com.newmview.wifi.bean.MapModel;
import com.mview.airtel.databinding.AlertDialogLayoutBinding;
import com.mview.airtel.databinding.FormItemsViewBinding;
import com.newmview.wifi.enumtypes.AlertType;
import com.newmview.wifi.other.AlertCategory;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;
import com.newmview.wifi.viewmodel.DialogViewModel;

import java.util.ArrayList;

public class MyAlertDialog extends DialogFragment  {

    private static final String TAG ="MyAlertDialog" ;
    private static AlertDialogInterface mCallback;
    private AlertDialog dialog;
    private AlertDialogLayoutBinding layoutBinding;
    private FormItemsViewBinding formItemsViewBinding;
    private AlertType type;

    public MyAlertDialog() {
    }

    public static MyAlertDialog newInstance(AlertDialogInterface callback) {
        mCallback = callback;
        return new MyAlertDialog();
    }

  /*  public  void dismissDialog() {
dialog.dismiss();
    }*/

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        //get shared ViewModel
        DialogViewModel viewModel = new ViewModelProvider(requireActivity()).get(DialogViewModel.class);

        //get AlertOptions which decide how the shown dialog will look and behave
        final AlertOptions mOptions = viewModel.getOptions().getValue();
        //-----------------------------------LOAD UI------------------------------------------------
        if (mOptions != null) {
             type=mOptions.getType();
            if (type == AlertType.subscriberDetailsAlert || type==AlertType.historyAlert) {

                showSubscriberAlert(mOptions);
                formItemsViewBinding=DataBindingUtil.inflate(LayoutInflater.from(requireContext()),
                        R.layout.form_items_view, null,
                        false);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(requireContext());
                alertBuilder.setView(formItemsViewBinding.getRoot());
                dialog = alertBuilder.create();

                if(type==AlertType.subscriberDetailsAlert)
                {
                   showSubscriberDialog(mOptions,viewModel,type);
                }
                else
                {
                    setCancelable(mOptions.isCancelable());

                    showHistoryAlert(mOptions,viewModel,type);


                }

                return dialog;

            } else {
                layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()),
                        R.layout.alert_dialog_layout, null,
                        false);
                // final View customView = LayoutInflater.from(requireContext()).inflate(R.layout.alert_dialog_layout, null);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(requireContext());
                alertBuilder.setView(layoutBinding.getRoot());
                dialog = alertBuilder.create();
                layoutBinding.okBtn.setText(mOptions.getPositiveButton());
                String negativeText=mOptions.getNegativeButton();
                if(Utils.checkifavailable(negativeText)) {
                    layoutBinding.cancelBtn.setText(mOptions.getNegativeButton());
                }
                else
                {
                    layoutBinding.cancelBtn.setVisibility(View.GONE);

                }
                if(Utils.checkifavailable(mOptions.getPositiveButton()))
                {
                    layoutBinding.okBtn.setText(mOptions.getPositiveButton());
                }
                else
                {
                    layoutBinding.okBtn.setVisibility(View.GONE);
                }

                // This is needed to display my custom shape
          /*  if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }*/

                Log.i(TAG, "Type of laert is " + type + " list " + mOptions.getList());
                if(Utils.checkifavailable(mOptions.getTitle())){
                    layoutBinding.titleIv.setVisibility(View.VISIBLE);
                    layoutBinding.titleIv.setText(mOptions.getTitle());

                }
                else
                {
                    layoutBinding.titleIv.setVisibility(View.GONE);

                }
                if (type == AlertType.floorPlanAlert || type == AlertType.labelsAlert || type == AlertType.drawAlert) {

                    layoutBinding.buttonsLayout.setVisibility(View.GONE);
                    DialogOptionsListAdapter dialogOptionsListAdapter = new
                            DialogOptionsListAdapter(requireContext(),
                            mOptions.getList(), mCallback, type, mOptions.getAlternativeText(),mOptions.getHistory());
                    layoutBinding.optionsRV.setAdapter(dialogOptionsListAdapter);
                    layoutBinding.optionsRV.setLayoutManager(new LinearLayoutManager(requireContext()));
                }  else {
                    layoutBinding.optionsRV.setVisibility(View.GONE);


                    if (type == AlertType.confirmationAlertDialog || type==AlertType.otpAlert || type==AlertType.dynamicAlert) {
                        layoutBinding.buttonsLayout.setVisibility(View.VISIBLE);
                        if(type==AlertType.otpAlert)
                        {
layoutBinding.editText.setVisibility(View.VISIBLE);
layoutBinding.editText.setHint("OTP");
layoutBinding.okBtn.setText("OK");
layoutBinding.cancelBtn.setVisibility(View.GONE);
                        }
                    } else {

                        layoutBinding.buttonsLayout.setVisibility(View.GONE);


                    }
                }
                setCancelable(mOptions.isCancelable());


                //if desc field of AlertOptions is empty then hide it
                String desc = mOptions.getMainText();
                Log.i(TAG, "Desc is " + desc);
                if (desc.isEmpty()) {
                    layoutBinding.descTv.setVisibility(View.GONE);
                } else {
                    layoutBinding.descTv.setVisibility(View.VISIBLE);
                    layoutBinding.descTv.setText(desc);
                }


                viewModel.isCanceled().observe(this, cancel -> {
                    if (cancel) {
                        dismiss();
                    }
                });
                layoutBinding.okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
Object details=null;
                  if(type==AlertType.otpAlert)
                  {
                      String otp=layoutBinding.editText.getText().toString();
                      details=otp;
                     // mOptions.setAlternativeText(otp);

                  }

                      mCallback.alertDialogPositiveButtonClicked(type, details);



                    }
                });
                layoutBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertType type = mOptions.getType();
                        mCallback.alertDialogNegativeButtonClicked(type);
                    }
                });

                return dialog;
            }
        }
        return null;
    }

    private void showHistoryAlert(AlertOptions mOptions, DialogViewModel viewModel, AlertType type) {
        {
            viewModel.isCanceled().observe(this, cancel -> {
                if (cancel) {
                    dismiss();
                }
            });


            formItemsViewBinding.idEt.setVisibility(View.GONE);
            formItemsViewBinding.historyTitleTv.setText("Enter your respective text");
            formItemsViewBinding.nameEt.setHint("Add here..");

            ArrayList<String> historyValues=new ArrayList<>();
            Log.i(TAG,"History list "+mOptions.getHistory());
            if(mOptions.getHistory()!=null) {
                for (int i = 0; i < mOptions.getHistory().size(); i++) {
/*
                            if(i==0)
                            {
                                String tag=mOptions.getHistory().get(0).getTag();
                                if(Utils.checkifavailable(tag)) {
                                    if (tag.equalsIgnoreCase("flat_type"))
                                    {
                                        formItemsViewBinding.historyTitleTv.setText("Enter Flat Type");
                                        formItemsViewBinding.nameEt.setHint("Flat Type");

                                    }
                                }

                            }
*/
                    String name = mOptions.getHistory().get(i).getValue();
                    if(!Utils.checkIfListContainsString(historyValues,name))
                        historyValues.add(name);
                    Log.i(TAG,"Sub name "+name);

                }
                if( historyValues.size()>0) {
                    formItemsViewBinding.nameEt.setText(historyValues.get(0));
                    formItemsViewBinding.nameEt.setAdapter(new
                            ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, historyValues));
                    formItemsViewBinding.nameEt.setThreshold(0);
                }
                formItemsViewBinding.doneBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name=formItemsViewBinding.nameEt.getText().toString();

                        if(Utils.checkifavailable(name))
                        {
                            mCallback.listOptionClicked(name);
                        }
                        else
                        {
                            Utils.showToast(requireContext(), Constants.ENTER_TEXT);
                        }
                    }
                });

            }
        }
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        if(type==AlertType.subscriberDetailsAlert)
        mCallback.finishActivity();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
      /*  if(type==AlertType.subscriberDetailsAlert)
        mCallback.finishActivity();*/

    }

    private void showSubscriberDialog(AlertOptions mOptions, DialogViewModel viewModel, AlertType type) {
        setCancelable(isCancelable());


        viewModel.isCanceled().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean cancel) {
                Log.i(TAG,"Call finish.."+cancel);
               if (cancel) {
                   dismiss();
                  //  mCallback.finishActivity();
                }

            }
        });

/*
                    viewModel.isCanceled().observe(
                            this, cancel -> {
                        if (cancel) {
                            mCallback.finishActivity();
                        }
                    });
*/

        ArrayList<String> subscriberNamesList=new ArrayList<>();
        ArrayList<String> subscriberIdList=new ArrayList<>();
        Log.i(TAG,"Subscriber list "+mOptions.getDetailsList());
        if(mOptions.getDetailsList()!=null) {
            for (int i = 0; i < mOptions.getDetailsList().size(); i++) {
                String name = mOptions.getDetailsList().get(i).getSubscriberName();
                if(!Utils.checkIfListContainsString(subscriberNamesList,name))
                    subscriberNamesList.add(name);
                Log.i(TAG,"Sub name "+name);

            }
            for (int i = 0; i < mOptions.getDetailsList().size(); i++) {
                String id = mOptions.getDetailsList().get(i).getSubscriberId();
                if(!Utils.checkIfListContainsString(subscriberIdList,id))
                    subscriberIdList.add(mOptions.getDetailsList().get(i).getSubscriberId());
                Log.i(TAG,"Sub id "+id);

            }
            if(subscriberNamesList.size()>0 && subscriberIdList.size()>0) {
                formItemsViewBinding.nameEt.setText(subscriberNamesList.get(0));
                formItemsViewBinding.idEt.setText(subscriberIdList.get(0));
                formItemsViewBinding.idEt.setAdapter(new
                        ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, subscriberIdList));
                formItemsViewBinding.idEt.setThreshold(0);
                formItemsViewBinding.nameEt.setAdapter(new
                        ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, subscriberNamesList));
                formItemsViewBinding.nameEt.setThreshold(0);
            }
            formItemsViewBinding.doneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name=formItemsViewBinding.nameEt.getText().toString();
                    String id=formItemsViewBinding.idEt.getText().toString();
                    if(Utils.checkifavailable(name) && Utils.checkifavailable(id))
                    {
                        MapModel mapModel=new MapModel();
                        mapModel.setSubscriberName(name);
                        mapModel.setSubscriberId(id);
                        mCallback.alertDialogPositiveButtonClicked(type,mapModel);
                    }
                    else
                    {
                        Utils.showToast(requireContext(), Constants.SUBSCRIBER_DETAILS_DESCRIPION);
                    }
                }
            });

        }
    }

    private void showSubscriberAlert(AlertOptions mOptions) {
    }

    /**
     * @param type takes AlertType which is unique for every alert dialog of same purpose
     * @return AlertCategory (currently: primary, warning or success)
     */
    private AlertCategory getAlertCategory(AlertType type) {
        //category success
        if (type == AlertType.exampleSuccess) {
            return AlertCategory.success;
        }
        //category primary
        if (type == AlertType.dynamicAlert || type == AlertType.examplePrimary) {
            return AlertCategory.primary;
        } else {
            //category warning
            return AlertCategory.warning;
        }
    }

    public interface AlertDialogInterface {
        void alertDialogPositiveButtonClicked(AlertType type, Object details);

        void alertDialogNegativeButtonClicked(AlertType type);
        void listOptionClicked(String text);

        void finishActivity();
    }
}
