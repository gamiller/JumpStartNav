package edu.dartmouth.cs.gracemiller.jumpstartnav.AlarmHandlers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by TAlbarran on 3/2/16.
 */
public class AlarmScheduler {

    // add alarm to scheduler
    public static void setAlarm(Context context, int id, Calendar dateTime) {

        // create intent and store id
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("id",id);
        Log.d("id", "the id is " + id);
        Log.d("date", "the date is " + dateTime.toString());

        //create pending intent
        PendingIntent pi = PendingIntent.getBroadcast(context, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT); //set pending intent to call EMAAlarmReceiver.


        // add for next day if alarm was already set
        if(dateTime.getTimeInMillis() < System.currentTimeMillis()) {
            dateTime.add(Calendar.DATE, 1);
        }

        // create alarm manager and set alarm with pending intent
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,dateTime.getTimeInMillis(),pi);

    }

    // delete alarm from scheduler
    public static void deleteAlarm(Context context, int id) {
        // create intent for receiver
        Intent intent = new Intent(context, AlarmReceiver.class);

        // pending intent to start alarm
        PendingIntent deleteIntent = PendingIntent.getBroadcast(context, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT); //set pending intent to call EMAAlarmReceiver.

        // cancel alarm
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(deleteIntent);

    }

}
