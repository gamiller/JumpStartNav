package edu.dartmouth.cs.gracemiller.jumpstartnav.AlarmHandlers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import edu.dartmouth.cs.gracemiller.jumpstartnav.Classes.Alarm;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.AlarmEntryDbHelper;
import edu.dartmouth.cs.gracemiller.jumpstartnav.SpeechTextActivity;

/**
 * Created by TAlbarran on 3/2/16.
 */
public class AlarmReceiver extends BroadcastReceiver {

    //Receive broadcast
    @Override
    public void onReceive(final Context context, Intent intent) {
        int id = intent.getIntExtra("id",0);
        startAlarmUnlock(context, id);
    }

    // start the activity for alarm
    private void startAlarmUnlock(Context context, int id) {
        Class classtype = null;
        int alarmType,index;
        String dataSource;

        //get alarm and data from db
        AlarmEntryDbHelper helper = new AlarmEntryDbHelper(context);
        Alarm onAlarm = helper.fetchAlarmByIndex((long) id);
        alarmType = onAlarm.getmAlarmType();
        dataSource = onAlarm.getmRingToneFile();
        index = onAlarm.getDefaultIndex();

        //start playing the sound
        //AlarmPlayer player = new AlarmPlayer();
        //player.startSound(context,dataSource,index);

        /*
        Start the designated activity
         */
        // start math
        if (alarmType == 0) {
            //classtype = MathActivity.class;
        //start movement
        } else if (alarmType == 1) {
            //classtype = MovementActivity.class;
            //start speech to text
        } else if (alarmType == 2) {
            classtype = SpeechTextActivity.class;
        }

        Intent unlockIntent = new Intent(context,classtype);
        unlockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(unlockIntent);

    }
}

