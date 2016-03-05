package edu.dartmouth.cs.gracemiller.jumpstartnav.AlarmHandlers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by TAlbarran on 3/2/16.
 */
public class AlarmScheduler {

    // how to handle unique id
    public static void setAlarm(Context context, int id, Calendar dateTime) {

        Log.d("adding alarm", "adding alarm");

        // create intent and store id
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("id",id);

        //create pending intent
        PendingIntent pi = PendingIntent.getBroadcast(context, id, intent,
                PendingIntent.FLAG_CANCEL_CURRENT); //set pending intent to call EMAAlarmReceiver.


        // WHAT IS THIS FOR??
        if(dateTime.getTimeInMillis() < System.currentTimeMillis()) {
            dateTime.add(Calendar.DATE, 1);
        }

        // create alarm manager and set alarm with pending intent
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, dateTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi);

        int i,sum;
        i=0;
        sum=0;
        while (i != -1) {
            AlarmManager.AlarmClockInfo info = alarmManager.getNextAlarmClock();
            if (info == null) {
                i = -1;
            } else {
                sum++;
            }
        }

        Toast.makeText(context,sum + " alarms have been added",Toast.LENGTH_SHORT);
    }

    public static void deleteAlarm(Context context, int id) {
        Intent intent = new Intent(context, AlarmReceiver.class);

        PendingIntent deleteIntent = PendingIntent.getBroadcast(context, id, intent,
                PendingIntent.FLAG_CANCEL_CURRENT); //set pending intent to call EMAAlarmReceiver.

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(deleteIntent);

        int i,sum;
        i=0;
        sum=0;
        while (i != -1) {
            AlarmManager.AlarmClockInfo info = alarmManager.getNextAlarmClock();
            if (info == (null)) {
                i = -1;
            } else {
                sum++;
            }
        }

        Toast.makeText(context, sum + " alarms left", Toast.LENGTH_SHORT);

    }

}
