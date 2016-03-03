package edu.dartmouth.cs.gracemiller.jumpstartnav.Classes;

/**
 * Created by TAlbarran on 3/2/16.
 */
public class Recording {
    int id;
    String fileName;
    String alarmName;

    public Recording() {
        this.id = 0;
        this.fileName = null;
        this.alarmName = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }
}