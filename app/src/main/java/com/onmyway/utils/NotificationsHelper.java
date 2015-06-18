package com.onmyway.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import com.onmyway.MapActivity;
import com.onmyway.R;
import com.onmyway.model.Notification;

import java.util.Random;

public class NotificationsHelper
{
    private static final Random random = new Random();
    private static final String placeholder = "$user";

    public static void ShowNotificationInNotificationCenter(Notification n, Context context, String myPhoneNumber, String appointmentId)
    {
        //prepare notification title and content
        String title;
        String content;

        if (n.getSubjectPhoneNumber().equals(myPhoneNumber))
        {
            title = n.getTitle();
            content = n.getContent();
        } else
        {
            String userName = ContactsHelper.getContactDisplayNameByNumber(
                    context.getContentResolver(),
                    n.getSubjectPhoneNumber());

            if (StringUtils.isNullOrWhiteSpaces(userName))
                userName = n.getSubjectPhoneNumber();

            title = n.getTitle().replace(placeholder, userName);
            content = n.getContent().replace(placeholder, userName);
        }

        int iconResource;

        switch (n.getType())
        {
            default:
            case Notification.TYPE_None:
            case Notification.TYPE_Arrived:
                iconResource = R.drawable.icon;
                break;
            case Notification.TYPE_Urgent:
                iconResource = R.drawable.icon_yellow;
                break;
            case Notification.TYPE_VeryUrgent:
                iconResource = R.drawable.icon_red;
                break;
        }
        //create Android notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(iconResource)
                        .setContentTitle(title)
                        .setContentText(content);

        //set click event
        Intent notificationIntent = new Intent(context, MapActivity.class);
        notificationIntent.putExtra("appointmentId", appointmentId);
        PendingIntent notificationPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(notificationPendingIntent);

        int mNotificationId = random.nextInt();
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    public static void ShowDialog(Notification n, String myPhoneNumber, Activity context)
    {
        //prepare notification title and content
        String title;
        String content;

        if (n.getSubjectPhoneNumber().equals(myPhoneNumber))
        {
            title = n.getTitle();
            content = n.getContent();
        } else
        {
            String userName = ContactsHelper.getContactDisplayNameByNumber(
                    context.getContentResolver(),
                    n.getSubjectPhoneNumber());

            if (StringUtils.isNullOrWhiteSpaces(userName))
                userName = n.getSubjectPhoneNumber();

            title = n.getTitle().replace(placeholder, userName);
            content = n.getContent().replace(placeholder, userName);
        }

        MessageHelper.showDialog(context, title, content, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
    }
}
