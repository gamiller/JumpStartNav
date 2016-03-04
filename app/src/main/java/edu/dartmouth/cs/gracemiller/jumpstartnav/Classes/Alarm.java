package edu.dartmouth.cs.gracemiller.jumpstartnav.Classes;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.RingtoneManager;
import java.util.Calendar;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by TAlbarran on 3/2/16.
 */
public class Alarm {

    private long id;
    private Calendar mDateTime;    // When does this alarm happen

    private int mMathQs;
    private int mMoveNum;
    private int mAlarmType;
    private int mRingtoneType;
    private String mRingToneFile;
    private int mActive;
    private String mReminder;
    private int defaultIndex;

    public Alarm() {

        this.id = 0;
        this.mDateTime = Calendar.getInstance();

        this.mMathQs = 3;
        this.mMoveNum = 5;
        this.mAlarmType = 0;
        this.mRingtoneType = 0;
        this.mActive = 0;
        this.mReminder = null;
        this.mRingToneFile = "default";
        this.defaultIndex = RingtoneManager.TYPE_ALARM;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Calendar getmDateTime() {
        return mDateTime;
    }

    public void setmDateTime(Calendar mDateTime) {
        this.mDateTime = mDateTime;
    }

    public int getmMathQs() {
        return mMathQs;
    }

    public void setmMathQs(int mMathQs) {
        this.mMathQs = mMathQs;
    }

    public int getmMoveNum() {
        return mMoveNum;
    }

    public void setmMoveNum(int mMoveNum) {
        this.mMoveNum = mMoveNum;
    }

    public int getmAlarmType() {
        return mAlarmType;
    }

    public void setmAlarmType(int mAlarmType) {
        this.mAlarmType = mAlarmType;
    }

    public int getmRingtoneType() {
        return mRingtoneType;
    }

    public void setmRingtoneType(int mRingtoneType) {
        this.mRingtoneType = mRingtoneType;
    }

    public String getmRingToneFile() {
        return mRingToneFile;
    }

    public void setmRingToneFile(String mRingToneFile) {
        this.mRingToneFile = mRingToneFile;
    }

    public int getmActive() {
        return mActive;
    }

    public void setmActive(int mActive) {
        this.mActive = mActive;
    }

    public String getmReminder() {
        return mReminder;
    }

    public void setmReminder(String mReminder) {
        this.mReminder = mReminder;
    }

    public int getDefaultIndex() {
        return defaultIndex;
    }

    public void setDefaultIndex(int defaultIndex) {
        this.defaultIndex = defaultIndex;
    }


}
