package com.onmyway.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;

import com.onmyway.R;

/**
 * Created by Marco on 15/06/2015.
 */
public class ActivityHelper {
    public static String getCurrentPhoneNumber(Context context)
    {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = tm.getLine1Number();

        PreferencesHelper.setPhoneNumber(context, phoneNumber);

        return phoneNumber;
    }

    public static Toolbar setActionBar(ActionBarActivity activity){
        Toolbar toolbar = (Toolbar)activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        return toolbar;
    }
}
