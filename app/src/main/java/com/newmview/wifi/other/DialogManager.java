package com.newmview.wifi.other;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.newmview.wifi.bean.AlertOptions;
import com.newmview.wifi.bean.HistoryModel;
import com.newmview.wifi.bean.SubscriberModel;
import com.newmview.wifi.customdialog.MyAlertDialog;
import com.newmview.wifi.enumtypes.AlertType;
import com.newmview.wifi.viewmodel.DialogViewModel;

import java.util.ArrayList;

public class DialogManager {

        private static final String TAG ="DialogManager" ;

        /**
 * @param owner if out of Activity usually it is enough to just call 'this' or 'ActivityName.this'.
 *              if initialized in a fragment always call requireActivity() instead
 * @return MyAlertDialogViewModel which holds alert dialogs options
 */
public static DialogViewModel initializeViewModel(ViewModelStoreOwner owner) {
        return new ViewModelProvider(owner).get(DialogViewModel.class);
        }

/**
 * @param result
 * @param callback  usually this, or ActivityName.this. Has to be class which extends any FragmentActivity
 * @param type      the AlertType which defines what kind of alert to show
 * @param viewModel the MyAlertDialogViewModel instance of the activity of where this function is called
 * @param context   If called inside of a fragment, put requireActivity()
 * @param title
 * @param detailsList
 * @param history
 * @param positiveBtn
 * @param negativeButton
 */
public static void showMyDialog(MyAlertDialog.AlertDialogInterface callback,
                                AlertType type,
                                DialogViewModel viewModel,
                                Context context,
                                String desc, String title, ArrayList<SubscriberModel> detailsList,
                                ArrayList<HistoryModel> history, String positiveBtn, String negativeButton) {
        MyAlertDialog dialog = MyAlertDialog.newInstance(callback);
        Log.i(TAG,"Sending type "+type);
        viewModel.setOptions(AlertOptions.create(type,desc,title,detailsList,history,positiveBtn,negativeButton));
        viewModel.showDialog(); //important otherwise won't show
        if(!dialog.isRemoving())
        {
              try {
                      dialog.show(((FragmentActivity) context).getSupportFragmentManager(), type.toString());
              }
              catch (Exception e)
              {
                      e.printStackTrace();
              }
      }
       // viewModel.setDialogDismissListener()

        }
        }
