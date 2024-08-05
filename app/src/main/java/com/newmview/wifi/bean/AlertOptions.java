package com.newmview.wifi.bean;

import android.util.Log;

import androidx.annotation.DrawableRes;

import com.newmview.wifi.enumtypes.AlertType;
import com.newmview.wifi.other.Config;

import java.util.ArrayList;

import static com.newmview.wifi.other.Config.getFloorTypes;


/**
 * Class which holds the options for MyAlertDialog
 */
public class AlertOptions {
    private static final String TAG ="AlertOptions" ;
    private final String title;
    private final String text;
    private  String positiveButton,negativeButton;
    private  ArrayList<HistoryModel> history;
    private  ArrayList<SubscriberModel> detailsList;
    private  String alternativeText;
    private final String mainText;
    private Object details;
    private final int icon;

    public AlertOptions(
            String title,
            String text,
            String alternativeText,
            String mainText,
            @DrawableRes int icon,
            boolean isCancelable,
            ArrayList<String> optionsList,
            AlertType type, ArrayList<HistoryModel> history, ArrayList<SubscriberModel> detailsList, String positiveBtn, String negativeButton) {
        this.title = title;
        this.text = text;
        this.alternativeText = alternativeText;
        this.mainText = mainText;
        this.icon = icon;
        this.isCancelable = isCancelable;
        this.type = type;
        this.list=optionsList;
        this.detailsList=detailsList;
        this.history=history;
        this.positiveButton=positiveBtn;
        this.negativeButton=negativeButton;
    }

    public ArrayList<String> getList() {
        return list;
    }

    private final boolean isCancelable;

    public void setList(ArrayList<String> list) {
        this.list = list;
    }
    public void setAlternativeText(String text)
    {
        this.alternativeText=text;
    }

    private final AlertType type;
    private   ArrayList<String> list;


    public ArrayList<SubscriberModel> getDetailsList() {
        return detailsList;
    }


    public String getPositiveButton() {
        return positiveButton;
    }

    public String getNegativeButton() {
        return negativeButton;
    }

    public static AlertOptions create(AlertType type, String desc, String title,
                                      ArrayList<SubscriberModel> detailsList, ArrayList<HistoryModel> history, String positiveBtn, String negativeButton) {
        Log.i(TAG,"Sending type "+type);
        switch (type) {
            case floorPlanAlert:
                return new AlertOptions(
                        "Choose Floor Type",
                        "",
                        "Add some floor type.",
                        "",
                       -1,
                        true,getFloorTypes(),
                        type,history,null, positiveBtn, negativeButton);
            case testResultAlert:
                return new AlertOptions(
                        title,
                        "",
                        "",
                        desc,
                        -1,
                        true,null,
                        type, null,null, positiveBtn, negativeButton);
            case confirmationAlertDialog:
                return new AlertOptions(
                        title,
                        "",
                        "",
                        desc,
                        -1,
                        true,null,
                        type, null,null,positiveBtn,negativeButton);
            case dynamicAlert:
                return new AlertOptions(
                        title,
                        "",
                        "",
                        desc,
                        -1,
                        true,null,
                        type, null,null,positiveBtn,negativeButton);

            case labelsAlert:
                return new AlertOptions(
                        "Choose Your Label",
                        "",
                        "Add some label.",
                        "",
                        -1,
                        true, Config.getLabels(),
                        type, history,null, positiveBtn, negativeButton);
            case subscriberDetailsAlert:
                return new AlertOptions(
                        " ",
                        "",
                        " ",
                        "",
                        -1,
                        true,
                        null,
                        type,
                        null,
                        detailsList, positiveBtn, negativeButton);
            case historyAlert:
                return new AlertOptions(
                        " ",
                        "",
                        " ",
                        "",
                        -1,
                        true,
                        null,
                        type,
                        history,
                        null, positiveBtn, negativeButton);
            case otpAlert:
                return new AlertOptions(
                    title,
                    "",
                    "",
                    desc,
                    -1,
                    true, null,
                    type,
                        null,null, positiveBtn, negativeButton);

        }

        return new AlertOptions(
                "",
                "",
                "",
                "",
                -1,
                false,null,
                AlertType.dynamicAlert, null,null, positiveBtn, negativeButton);

    }


    public AlertType getType() {
        return type;
    }

    public int getIcon() {
        return icon;
    }

    public String getAlternativeText() {
        return alternativeText;
    }

    public String getMainText() {
        return mainText;
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCancelable() {
        return isCancelable;
    }


    public ArrayList<HistoryModel> getHistory() {
        return history;
    }
}