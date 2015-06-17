package com.onmyway.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.onmyway.R;

/**
 * Created by Marco on 17/06/2015.
 */
public class MessageHelper {
    public static void showToast(Context context, String message, boolean shortDuration){
        Toast.makeText(context, message, shortDuration ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }

    public static void showDialog(Activity context, String title, String message, DialogInterface.OnClickListener onClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.ok_button), onClickListener);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showDialogWithTwoButtons(Activity context, String title, String message,
                                                String positiveButton,
                                                String negativeButton,
                                                DialogInterface.OnClickListener positiveClickListener,
                                                DialogInterface.OnClickListener negativeClickListener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(positiveButton, positiveClickListener)
                .setNegativeButton(negativeButton, negativeClickListener);
        AlertDialog alert = builder.create();
        alert.show();
    }
}