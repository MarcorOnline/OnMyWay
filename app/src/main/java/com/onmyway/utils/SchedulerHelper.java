package com.onmyway.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.onmyway.services.AlarmReceiver;

import org.joda.time.DateTime;

import java.util.Calendar;

public class SchedulerHelper {
    public static void scheduleAlarmService(Context context, Calendar startTime){
        // Retrieve a PendingIntent that will perform a broadcast
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        int interval = 30000;   //30 seconds

        manager.setRepeating(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), interval, pendingIntent);

        //TODO togliere
        Toast.makeText(context, "Scheduled", Toast.LENGTH_SHORT).show();
    }

    public static void cancelAlarmService(Context context){
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
    }
}
