//package edu.dartmouth.cs.gracemiller.jumpstartnav.AlarmHandlers;
//
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//
//import java.util.Calendar;
//
///**
// * Created by TAlbarran on 3/2/16.
// */
//public class AlarmScheduler {
//
//    // how to handle unique id
//    public static void setAlarm(Context context, int id, Calendar dateTime) {
//
//        // create intent and store id
//        Intent intent = new Intent(context, AlarmReceiver.class);
//        intent.putExtra("id",id);
//
//        //create pending intent
//        PendingIntent pi = PendingIntent.getBroadcast(context, id, intent,
//                PendingIntent.FLAG_CANCEL_CURRENT); //set pending intent to call EMAAlarmReceiver.
//
//
//        // WHAT IS THIS FOR??
//        if(dateTime.getTimeInMillis() < System.currentTimeMillis()) {
//            dateTime.add(Calendar.DATE, 1);
//        }
//
//        // create alarm manager and set alarm with pending intent
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, dateTime.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, pi);
//    }
//
//    public static void deleteAlarm(Context context, int id) {
//        Intent intent = new Intent(context, AlarmReceiver.class);
//
//        PendingIntent deleteIntent = PendingIntent.getBroadcast(context, id, intent,
//                PendingIntent.FLAG_CANCEL_CURRENT); //set pending intent to call EMAAlarmReceiver.
//
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.cancel(deleteIntent);
//    }
//
//}
