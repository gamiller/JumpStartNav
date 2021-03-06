package edu.dartmouth.cs.gracemiller.jumpstartnav.AlarmHandlers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes.Alarm;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.AlarmEntryDbHelper;
import edu.dartmouth.cs.gracemiller.jumpstartnav.View.MathAlarmActivity;
import edu.dartmouth.cs.gracemiller.jumpstartnav.View.MovementActivity;
import edu.dartmouth.cs.gracemiller.jumpstartnav.View.SpeechTextActivity;

/**
 * Created by TAlbarran on 3/2/16.
 */
public class AlarmReceiver extends BroadcastReceiver {
    String NUM_CORR = "numberCorrect";
    String NUM_LEFT = "numberLeft";
    String NUM_WRONG = "numberWrong";

    //Receive broadcast
    @Override
    public void onReceive(final Context context, Intent intent) {
        // grab id and pass along
        int id = intent.getIntExtra("id",0);
        startAlarmUnlock(context, id);
    }

    // start the activity for alarm
    private void startAlarmUnlock(Context context, int id) {
        Class classtype = null;
        int alarmType;

        //get alarm and data from db
        AlarmEntryDbHelper helper = new AlarmEntryDbHelper(context);
        Alarm onAlarm = helper.fetchAlarmByIndex((long) id);
        alarmType = onAlarm.getmAlarmType();
        helper.close();


        /*
        Start the designated activity
         */
        // start math
        if (alarmType == 0) {
            classtype = MathAlarmActivity.class;
        //start movement
        } else if (alarmType == 1) {
            classtype = MovementActivity.class;
            //start speech to text
        } else if (alarmType == 2) {
            classtype = SpeechTextActivity.class;
        }


        // create intent to start activity
        Intent unlockIntent = new Intent(context,classtype);
        unlockIntent.putExtra(NUM_CORR, 0);
        unlockIntent.putExtra(NUM_WRONG, 0);
        unlockIntent.putExtra(NUM_LEFT, 3);
        unlockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // put alarm id in intent
        unlockIntent.putExtra("id",id);
        context.startActivity(unlockIntent);

    }
}

