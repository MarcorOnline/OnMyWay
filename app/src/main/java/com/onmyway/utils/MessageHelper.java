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
    private void showToast(Context context, String message, boolean shortDuration){
        Toast.makeText(context, message, shortDuration ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }

    public static void ShowDialog(Activity context, String title, String message, DialogInterface.OnClickListener onClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.ok_button), onClickListener);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
