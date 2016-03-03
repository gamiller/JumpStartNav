//package edu.dartmouth.cs.gracemiller.jumpstartnav.AlarmHandlers;
//
//import android.content.Context;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Vibrator;
//
///**
// * Created by TAlbarran on 3/2/16.
// */
//public class AlarmPlayer {
//        static MediaPlayer mediaPlayer;
//        static Vibrator vibrator;
//        Uri notification;
//
//
//    //start the vibration and sound to play until task is accomplished
//    public void startSound(Context context,String dataSource, int defaultIndex){
//        //ringtone/vibration
//
//        try {
//            //get the uri of the ringtone to be played
//            //create the media player, set the audio stream
//            mediaPlayer = new MediaPlayer();
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
//            if (dataSource.equals("default")) {
//                notification = RingtoneManager.getDefaultUri(defaultIndex);
//                mediaPlayer.setDataSource(context, notification);
//            } else {
//                mediaPlayer.setDataSource(dataSource);
//            }
//
//            //start the media player
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//
//        // Get instance of Vibrator from current Context
//        vibrator= (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//
//        //create the pattern of vibration
//        // Start without a delay, Vibrate for 100 millisecond, Sleep for 100 milliseconds
//        long[] pattern = {0, 100, 100};
//
//        //repeat the pattern from the beginning
//        vibrator.vibrate(pattern, 0);
//
//    }
//
//    public void stopSound() {
//        mediaPlayer.stop();
//        mediaPlayer.release();
//        vibrator.cancel();
//    }
//
//
//
//
//}
