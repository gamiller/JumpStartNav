package edu.dartmouth.cs.gracemiller.jumpstartnav.AlarmHandlers;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;

import java.io.IOException;

import edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes.Alarm;
import edu.dartmouth.cs.gracemiller.jumpstartnav.Model.AlarmEntryDbHelper;

/**
 * Created by TAlbarran on 3/2/16.
 */
public class AlarmPlayer {

    // declare instance variables
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    // constructor to prepare services
    public AlarmPlayer(Context context, int id) {

        // instantiate vibrator service
        this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        // grab the filename from alarm
        AlarmEntryDbHelper helper = new AlarmEntryDbHelper(context);
        Alarm alarm = helper.fetchAlarmByIndex((long) id);
        String filename = alarm.getmRingToneFile();

        // initialize media player
        mediaPlayer = new MediaPlayer();


        // if it is a default ringtone
        if (alarm.getDefaultIndex() == 3) {
            try {

                // reset media player and set loop sound
                mediaPlayer.reset();
                mediaPlayer.setLooping(true);

                // set data source to uri and set audio output
                mediaPlayer.setDataSource(context, Uri.parse(filename));
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                // prepare and start the mediaplayer
                mediaPlayer.prepare();
                startSound();

            } catch (IOException e) {
                e.printStackTrace();
            }

            // if custom
        } else {
            try {
                // reset, set looping, set filepath, set audio stream
                mediaPlayer.reset();
                mediaPlayer.setLooping(true);
                mediaPlayer.setDataSource(filename);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();

                // start playing the soujnd
                startSound();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //start the vibration and sound to play until task is accomplished
    public void startSound() {

        try {
            //start playing ringtone alarm
            this.mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();

        }

        // set pattern
        long[] pattern = {0, 100, 1000};

        //repeat the pattern
        this.vibrator.vibrate(pattern, 0);
    }

    // stop the media player
    public void stopSound() {
        this.mediaPlayer.stop();
        this.mediaPlayer.release();
        this.vibrator.cancel();
    }
}
