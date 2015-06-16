package com.onmyway.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.onmyway.R;

public class PreferencesHelper {
    public static String getPhoneNumber(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(context.getString(R.string.phoneNumberKey), null);
    }

    public static void setPhoneNumber(Context context, String phoneNumber){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.phoneNumberKey), phoneNumber);
        editor.commit();
    }

    public static String getStatus(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(context.getString(R.string.userStatusKey), null);
    }

    public static void setStatus(Context context, String status){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.userStatusKey), status);
        editor.commit();
    }
}
