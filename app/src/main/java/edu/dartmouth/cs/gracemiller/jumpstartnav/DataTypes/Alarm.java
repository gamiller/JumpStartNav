package edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes;

import android.media.RingtoneManager;

import java.util.Calendar;

/**
 * Created by TAlbarran on 3/2/16.
 */
public class Alarm {

    private long id;
    private Calendar mDateTime;    // When does this alarm happen

    private int mAlarmType;
    private String mRingToneFile;
    private int mActive;
    private String mReminder;
    private int defaultIndex;

    public Alarm() {

        this.id = 0;
        this.mDateTime = Calendar.getInstance();

        this.mAlarmType = 0;
        this.mActive = 0;
        this.mReminder = "";
        this.mRingToneFile = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
        this.defaultIndex = 3;
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
