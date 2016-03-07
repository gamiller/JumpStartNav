package edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes;

import android.media.RingtoneManager;

import java.util.Calendar;

/**
 * An Alarm class to hold information associated with an alarm
 */
public class Alarm {

    private long id;
    private Calendar mDateTime;    // When the alarm happens
    private int mAlarmType;         //if you wake up to jumpjacks, speechtotext, or voicerecording
    private String mRingToneFile;   //string of name of file for ringtone
    private int mActive;            //0 if not active, 1 if active
    private String mReminder;       //reminder for the alarm
    private int defaultIndex;       //0 if default alarm, 1 if custom

    public Alarm() {
        this.id = 0;
        this.mDateTime = Calendar.getInstance();
        this.mAlarmType = 0;
        this.mActive = 0;
        this.mReminder = "";
        this.mRingToneFile = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
        this.defaultIndex = 3;
    }

    //getters and setters
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

    public int getmAlarmType() {
        return mAlarmType;
    }

    public void setmAlarmType(int mAlarmType) {
        this.mAlarmType = mAlarmType;
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
