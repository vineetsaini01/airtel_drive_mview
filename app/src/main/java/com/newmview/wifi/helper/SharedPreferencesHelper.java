package com.newmview.wifi.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private static final String PREFS_NAME = "AppLaunchPrefs";
    private static final String KEY_APP_LAUNCHED = "app_launched";
    private static final String KEY_USER_TYPE = "user_type";
    private static final String KEY_ID = "id";
    private static final String KEY_SENDER_NUMBER = "sender_num";
    private static final String KEY_RECEIVER_NUMBER = "receiver_num";
    private static final String KEY_RULE_CONDITION = "rule_condition";
    private static final String KEY_THIRD_NUMBER = "third_party";
    private static final String KEY_CALL_TIME = "call_time";
    private static final String KEY_SUBSCRIPTION_ID = "sub_id";
    private static final String KEY_SUBSCRIPTION_ID_2 = "sub_id_two";
    private static final String KEY_SUBSCRIPTION_ID_1 = "sub_id_one";
    private static final String KEY_PUBLIC_IP = "public_ip";







    private SharedPreferences sharedPreferences;

    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setAppLaunched(boolean launched) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_APP_LAUNCHED, launched);
        editor.apply();
    }

    public boolean isAppLaunched() {
        return sharedPreferences.getBoolean(KEY_APP_LAUNCHED, false);
    }

    public void setUserType(String userType) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_TYPE, userType);
        editor.apply();
    }

    public String getUserType() {
        return sharedPreferences.getString(KEY_USER_TYPE, null);
    }

    public void setId(String id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ID, id);
        editor.apply();
    }
    public String getId() {
        return sharedPreferences.getString(KEY_ID, null);
    }


    public String getSenderNum() {
        return sharedPreferences.getString(KEY_SENDER_NUMBER, null);
    }

    public void setSenderNum(String senderNum) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SENDER_NUMBER, senderNum);
        editor.apply();
    }

    public String getReceiverNum() {
        return sharedPreferences.getString(KEY_RECEIVER_NUMBER, null);
    }

    public void setReceiverNum(String receiverNum) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_RECEIVER_NUMBER, receiverNum);
        editor.apply();
    }

    public String getRuleCondition() {
        return sharedPreferences.getString(KEY_RULE_CONDITION, null);
    }

    public void setRuleCondition(String ruleCondition) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_RULE_CONDITION, ruleCondition);
        editor.apply();
    }

    public String getThirdPartyNum() {
        return sharedPreferences.getString(KEY_THIRD_NUMBER, null);
    }

    public void setThirdPartyNum(String thirdPartyNum) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_THIRD_NUMBER, thirdPartyNum);
        editor.apply();
    }

    public String getDateTime() {
        return sharedPreferences.getString(KEY_CALL_TIME, null);
    }

    public void setDateTime(String callTime) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_CALL_TIME, callTime);
        editor.apply();
    }

    public String getSubId() {
        return sharedPreferences.getString(KEY_SUBSCRIPTION_ID, null);
    }

    public void setSubId(String subId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SUBSCRIPTION_ID, subId);
        editor.apply();
    }
    public String getSubId1() {
        return sharedPreferences.getString(KEY_SUBSCRIPTION_ID_1, null);
    }

    public void setSubId1(String subId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SUBSCRIPTION_ID_1, subId);
        editor.apply();
    }
    public String getSubId2() {
        return sharedPreferences.getString(KEY_SUBSCRIPTION_ID_2, null);
    }

    public void setSubId2(String subId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SUBSCRIPTION_ID_2, subId);
        editor.apply();
    }

    public String getPublicIp() {
        return sharedPreferences.getString(KEY_PUBLIC_IP, null);
    }

    public void setPublicIp(String publicIp) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PUBLIC_IP, publicIp);
        editor.apply();
    }


    public void clearPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void clearUserValue() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_TYPE);
        editor.remove(KEY_ID);
        editor.remove(KEY_SENDER_NUMBER);
        editor.remove(KEY_THIRD_NUMBER);
        editor.remove(KEY_RECEIVER_NUMBER);
        editor.apply();
    }
}