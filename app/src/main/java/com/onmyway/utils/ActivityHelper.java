package com.onmyway.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;

/**
 * Created by Marco on 15/06/2015.
 */
public class ActivityHelper {
    public static String getCurrentPhoneNumber(Activity activity)
    {
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getLine1Number();
    }

    public static void changeActionBarColor(ActionBarActivity activity){
        //activity.getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000ff")));
    }
}
