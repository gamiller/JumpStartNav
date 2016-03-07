package edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes;

/**
 * Recording class to hold information about the recording
 */
public class Recording {

    int id;
    String fileName;    //name of the file associated with recording
    String alarmName;   //name of the alarm given by the user

    public Recording() {
        this.id = 0;
        this.fileName = null;
        this.alarmName = null;
    }

    //getters and setters
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