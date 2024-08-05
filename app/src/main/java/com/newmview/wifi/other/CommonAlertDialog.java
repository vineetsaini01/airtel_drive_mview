package com.newmview.wifi.other;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.databinding.DataBindingUtil;

import com.mview.airtel.R;
import com.newmview.wifi.SlidingTab.MyTabControl;
import com.mview.airtel.databinding.LinkSpeedStatsLayoutBinding;
import com.mview.airtel.databinding.MapActionLayoutBinding;
import com.mview.airtel.databinding.StatsLayoutBinding;
import com.mview.airtel.databinding.StatsLs5gLayoutBinding;
import com.newmview.wifi.helper.AllInOneAsyncTaskForNetwork;
import com.newmview.wifi.helper.RequestResponse;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.mView_HealthStatus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import yuku.ambilwarna.AmbilWarnaDialog;

public class CommonAlertDialog {


    private static final String TAG = "CommonAlertDialog";
    private Dialog dialog;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public static void  showPopup(View v, Context context,int menu, Interfaces.onMenuButtonClickListener buttonClickListener) {
        PopupMenu popupMenu = null;
        if(v!=null) {
            popupMenu = new PopupMenu(context, v, Gravity.BOTTOM, 0, R.style.OverflowMenuStyle);
        }
    //    MenuPopupHelper menuHelper = new MenuPopupHelper(getContext(), (MenuBuilder) menu.getMenu(), v);

        popupMenu.getMenuInflater().inflate(menu, popupMenu.getMenu());

        MenuBuilder menuBuilder =new MenuBuilder(context);
        MenuInflater inflater = new MenuInflater(context);
        inflater.inflate(menu, menuBuilder);
        MenuPopupHelper optionsMenu = new MenuPopupHelper(context, menuBuilder, v);
        optionsMenu.setForceShowIcon(true);
        optionsMenu.show();
// Set Item Click Listener
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                buttonClickListener.onMenuButtonClicked(item);
                return true;
            }

            @Override
            public void onMenuModeChange(@NonNull MenuBuilder menu) {

            }
        });
/*
        optionsMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item){
                buttonClickListener.onMenuButtonClicked(item);

                Intent i = new Intent(context, WifiHeatMapActivity.class);
                switch (item.getItemId()){
                    case R.id.bhkOne:
                        i.putExtra("HOUSE","bhkOne");
                        break;

                    case R.id.bhkTwo:
                        i.putExtra("HOUSE","bhkTwo");
                        break;
                    case R.id.bhkThree:
                        i.putExtra("HOUSE","bhkThree");
                        break;
                    case R.id.bhkFour:
                        i.putExtra("HOUSE","bhkFour");
                        break;


                    default:
                        return false;
                }



                return true;
            }
        });
*/

       // popupMenu.show();
       // menuHelper.show();
    }


    public static void displayPromptForEnablingGPS(final Context activity)
    {

        final AlertDialog.Builder builder =  new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "This app requires to turn  your GPS on!";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }
    public static  void getColorFromColorPickerDialog(Context context , ArrayList<Integer> colorsList,
                                                      Interfaces.ChangeColorListener colorListener) {

        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(context, R.color.black_color,
                true, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog ambilWarnaDialog, int color) {
           Log.i(TAG,"Returned color from ok button "+color);

            colorListener.changeColor(color,"");

            }

            @Override
            public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {


            }
        });
        ambilWarnaDialog.show();


    }

    public static void showResultAlertDialog(String test, final Context context, float avg, String result, int dialogview, String s,
                                             final String lat, final String lon, final String type) {
        System.out.println("in dialog ");
        if (Utils.checkContext(context)) {
            final Dialog dialog = new Dialog(context, R.style.AlertDialogTheme);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(dialogview, null);
            TextView resultTv = view.findViewById(R.id.finalResult);
            Button yes = view.findViewById(R.id.yes);
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.showReportIssueAlert(type, context, lat, lon, Constants.UL_DL_TEST_DONE);
                    dialog.dismiss();
                }
            });
            Button no = view.findViewById(R.id.no);
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    sendIssue("NONE",type);

                    dialog.dismiss();
              //      ((AppCompatActivity) context).onBackPressed();
                }
            });

            {
                switch (test) {
                    case "webtest":
                        TextView textView = view.findViewById(R.id.avglatencyresult);
                        String val = String.format("%.2f", avg);
                        textView.setText(val + " ms");

                        if (result != null) {
                            resultTv.setText(String.format("Network Latency : %s  ", result));
                        }
                        dialog.setContentView(view);
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.show();
                        break;
                    case "videotest":
                        if (result != null) {
                            resultTv.setText(String.format("Video Streaming Quality : %s  ", result));

                        }
                        TextView vid_dur = (TextView) view.findViewById(R.id.vid_dur);
                        TextView bffering_no = (TextView) view.findViewById(R.id.buffering_no);
                        TextView buff_time = (TextView) view.findViewById(R.id.buffer_time);
                        TextView play_time = (TextView) view.findViewById(R.id.playtime);

                        vid_dur.setText(String.valueOf(mView_HealthStatus.mySpeedTest.video.videoDuration));
                        bffering_no.setText(String.valueOf(mView_HealthStatus.mySpeedTest.video.noOfBuffering));
                        buff_time.setText(String.valueOf(mView_HealthStatus.mySpeedTest.video.totalBuferingTime));
                        play_time.setText(String.valueOf(mView_HealthStatus.mySpeedTest.video.totalPlayTime));
                        dialog.setContentView(view);
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.show();
                        break;
                    case "Upload Test":
                        TextView resultname = (TextView) view.findViewById(R.id.testname);
                        resultname.setText("Upload Test Result");
                        if (result != null) {
                            resultTv.setText(String.format("Upload Speed : " + result));
                        }
                        dialog.setContentView(view);
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.show();
                        break;
                    case "Download Test":
try {
    TextView resultname1 = (TextView) view.findViewById(R.id.testname);
    resultname1.setText("Download Test Result");
    if (result != null) {
        resultTv.setText(String.format("Download Speed : " + result));
    }

    dialog.setContentView(view);
    dialog.setCanceledOnTouchOutside(true);
    dialog.show();
}
catch (Exception e)
{
    e.printStackTrace();
}

                        break;
                }


            }


        }


    }

    private static void sendIssue(String msg, String type) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", msg);
            jsonObject.put("type",type);

            JSONArray reportArray=new JSONArray();
            reportArray.put(jsonObject);
            RequestResponse.sendEvent(reportArray, AllInOneAsyncTaskForNetwork.AsyncTaskPurpose.REPORT_ISSUE_EVT,"report_issue");
            //   WebService.API_sendReportIssue(lat, lon, "NONE", type);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public static void showAlert(String title, String message,Context context) {

        final AlertDialog.Builder builder =  new AlertDialog.Builder(context);
     if(Utils.checkifavailable(title))
      builder.setTitle(title);

     if(Utils.checkifavailable(message))
      builder.setMessage(message);

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {

                                d.dismiss();

                                if(context instanceof MyTabControl)
                                {
                                    ((MyTabControl) context).finish();

                                };
                            }
                        });

        builder.create().show();
    }

    public static void showCustomPopup(View anchorView, boolean showLinkSpeedHmButton, int layout, Context context, Interfaces.PopupButtonClickListener popupButtonClickListener)
    {
        LayoutInflater inflater = (LayoutInflater)
               context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(layout, null);
        MapActionLayoutBinding actionLayoutBinding = DataBindingUtil.inflate(inflater, layout, null,
                false);
       // actionLayoutBinding.
        PopupWindow popup = new PopupWindow(context);
        popup.setContentView(actionLayoutBinding.getRoot());
        // popupWindow.setBackgroundDrawable(null);


        popup.setWidth(600);
        popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popup.setFocusable(true);
        popup.setBackgroundDrawable(new BitmapDrawable());


/*
view.setFocusable(false);
popupWindow.setTouchable(true)*/

        popup.setElevation(5f);

        actionLayoutBinding.floorPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                popupButtonClickListener.onButtonClicked(v);
            }
        });
        actionLayoutBinding.mapTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                popupButtonClickListener.onButtonClicked(v);
            }
        });
        actionLayoutBinding.walkMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                popupButtonClickListener.onButtonClicked(v);
            }
        });
        actionLayoutBinding.lsMapTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                popupButtonClickListener.onButtonClicked(v);
            }
        });
        actionLayoutBinding.deletePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                popupButtonClickListener.onButtonClicked(v);
            }
        });
        actionLayoutBinding.lsMapTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                popupButtonClickListener.onButtonClicked(v);
            }
        });
      /* if(showLinkSpeedHmButton)
        {
            actionLayoutBinding.lsMapTv.setVisibility(VISIBLE);
        }
        else
        {
            actionLayoutBinding.lsMapTv.setVisibility(GONE);
        }*/

        popup.showAtLocation(anchorView, Gravity.BOTTOM, 0,
                anchorView.getBottom()+30);
        popup.showAsDropDown(anchorView);
        /*popup.showAtLocation(menu_Iv,Gravity.TOP|Gravity.RIGHT, menu_Iv.getRight()-30,
                menu_Iv.getTop()-20);*/

     //   Context wrapper = new ContextThemeWrapper(SurfaceViewVideoPlayer.this, R.style.popupMenuStyle);


    }
    public  void dismissAlert()
    {
        if(dialog!=null)
        {
            dialog.dismiss();
        }
    }


    public  void showStatsAlert(Context context, ArrayList<Double> percentList, ArrayList<String> lsPercentList,
                                      Interfaces.DismissDialogInterface dismissDialogInterface) {
       dialog = new Dialog(context, R.style.AlertDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DecimalFormat decimalFormat=new DecimalFormat("0.0");
        String y_text="Fair";
        String lg_text="Good";
        String dg_tet="Excellent";
        String red_text="Poor";
       // View view = inflater.inflate(R.layout.stats_layout, null);
        int layout=0;
        if(percentList!=null)
        {
            layout=R.layout.stats_layout;
            StatsLayoutBinding statsLayoutBinding = DataBindingUtil.inflate(inflater, layout, null,
                    false);
            dialog.setContentView(statsLayoutBinding.getRoot());
            statsLayoutBinding.darkGreenTv.setText(decimalFormat.format(percentList.get(2)) + "% (" + dg_tet + ")");
            statsLayoutBinding.lightGreenTv.setText(decimalFormat.format(percentList.get(1)) + "% (" + lg_text + ")");
            statsLayoutBinding.yellowTv.setText(decimalFormat.format(percentList.get(0)) + "% (" + y_text + ")");
            statsLayoutBinding.redTv.setText(decimalFormat.format(percentList.get(3)) + "% (" + red_text + ")");
            statsLayoutBinding.dismissBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        else {
            if (WifiConfig.getConnectedWifiDetails().getFrequencyBandwidth() == 5) {
                layout = R.layout.stats_ls_5g_layout;
                StatsLs5gLayoutBinding statsLayoutBinding = DataBindingUtil.inflate(inflater, layout, null,
                        false);
                dialog.setContentView(statsLayoutBinding.getRoot());
                statsLayoutBinding.darkGreenTv.setText(decimalFormat.format(Double.parseDouble(lsPercentList.get(2)))+ "% (" + dg_tet + ")");
                statsLayoutBinding.lightGreenTv.setText(decimalFormat.format(Double.parseDouble(lsPercentList.get(1)) )+ "% (" + lg_text + ")");
                statsLayoutBinding.yellowTv.setText(decimalFormat.format(Double.parseDouble(lsPercentList.get(0))) + "% (" + y_text + ")");
                statsLayoutBinding.redTv.setText(decimalFormat.format(Double.parseDouble(lsPercentList.get(3)))+ "% (" + red_text + ")");
                statsLayoutBinding.dismissBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            } else if (WifiConfig.getConnectedWifiDetails().getFrequencyBandwidth() == 2.4) {
                layout = R.layout.link_speed_stats_layout;
                LinkSpeedStatsLayoutBinding statsLayoutBinding = DataBindingUtil.inflate(inflater, layout, null,
                        false);
                dialog.setContentView(statsLayoutBinding.getRoot());
                statsLayoutBinding.darkGreenTv.setText(lsPercentList.get(2)+ "% (" + dg_tet + ")");
                statsLayoutBinding.lightGreenTv.setText(lsPercentList.get(1) + "% (" + lg_text + ")");
                statsLayoutBinding.yellowTv.setText(lsPercentList.get(0) + "% (" + y_text + ")");
                statsLayoutBinding.redTv.setText(lsPercentList.get(3)+ "% (" + red_text + ")");
                statsLayoutBinding.dismissBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }

        }
       // dialog.setContentView(statsLayoutBinding.getRoot());
       /* StatsLayoutBinding statsLayoutBinding = DataBindingUtil.inflate(inflater, layout, null,
                false);*/
      //  dialog.setContentView(statsLayoutBinding.getRoot());

       /* if(percentList!=null) {
            statsLayoutBinding.darkGreenTv.setText(decimalFormat.format(percentList.get(2)) + "% (" + dg_tet + ")");
            statsLayoutBinding.lightGreenTv.setText(decimalFormat.format(percentList.get(1)) + "% (" + lg_text + ")");
            statsLayoutBinding.yellowTv.setText(decimalFormat.format(percentList.get(0)) + "% (" + y_text + ")");
            statsLayoutBinding.redTv.setText(decimalFormat.format(percentList.get(3)) + "% (" + red_text + ")");
        }
        else if(lsPercentList!=null)
        {
            statsLayoutBinding.darkGreenTv.setText(lsPercentList.get(2)+ "% (" + dg_tet + ")");
            statsLayoutBinding.lightGreenTv.setText(lsPercentList.get(1) + "% (" + lg_text + ")");
            statsLayoutBinding.yellowTv.setText(lsPercentList.get(0) + "% (" + y_text + ")");
            statsLayoutBinding.redTv.setText(lsPercentList.get(3)+ "% (" + red_text + ")");
        }
        statsLayoutBinding.dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });*/
        dialog.show();

    }

    public static void showFloorTypeDialog() {
    }
}
