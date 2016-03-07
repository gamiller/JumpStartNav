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
    Uri notification;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    public AlarmPlayer(Context context, int id) {
        this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        AlarmEntryDbHelper helper = new AlarmEntryDbHelper(context);
        Alarm alarm = helper.fetchAlarmByIndex((long) id);
        String filename = alarm.getmRingToneFile();

        mediaPlayer = new MediaPlayer();

        Log.d("alarm default index", "default index is " + alarm.getDefaultIndex());
        if (alarm.getDefaultIndex() == 3) {
            try {

                mediaPlayer.reset();
                mediaPlayer.setLooping(true);

//                Uri soundUri = Uri.parse(alarm.getmRingToneFile());
//                this.mediaPlayer = (MediaPlayer.create(context, soundUri));
                mediaPlayer.setDataSource(context, Uri.parse(filename));
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                Log.d("default", "default default");
//                mediaPlayer.setDataSource(context,Uri.parse(filename));
//                mediaPlayer.prepareAsync();
//                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        mediaPlayer.start();
//                        startSound();
//                    }
//                });

                mediaPlayer.prepare();
                startSound();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                mediaPlayer.reset();
                mediaPlayer.setLooping(true);
                mediaPlayer.setDataSource(filename);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
                startSound();
                Log.d("custom", "custom custom");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //start the vibration and sound to play until task is accomplished
    public void startSound() { //(Context context,String dataSource, int defaultIndex){
        //ringtone/vibration

        try {
            //get the uri of the ringtone to be played
            //create the media player, set the audio stream
//            mediaPlayer = new MediaPlayer();
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

//            if (defaultIndex == 3) {
//                notification = RingtoneManager.getDefaultUri(defaultIndex);
//                notification = Uri.parse(dataSource);
//                mediaPlayer.setDataSource(context, notification);
//            } else {
//                mediaPlayer.setDataSource(dataSource);
//            }

            //start the media player
//            this.mediaPlayer.prepare();
            this.mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();

        }

        // Get instance of Vibrator from current Context
//        vibrator= (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        //create the pattern of vibration
        // Start without a delay, Vibrate for 100 millisecond, Sleep for 100 milliseconds
        long[] pattern = {0, 100, 1000};

        //repeat the pattern from the beginning
        this.vibrator.vibrate(pattern, 0);
    }

    public void stopSound() {
        this.mediaPlayer.stop();
        this.mediaPlayer.release();
        this.vibrator.cancel();
    }
}
