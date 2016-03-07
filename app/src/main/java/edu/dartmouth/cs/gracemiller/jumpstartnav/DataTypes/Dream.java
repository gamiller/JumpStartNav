package edu.dartmouth.cs.gracemiller.jumpstartnav.DataTypes;

import java.util.Calendar;

/**
 * Created by TAlbarran on 3/3/16.
 */
public class Dream {

    private int id;
    private String dream;
    private Calendar date;
    private String dreamName;

    public Dream() {
        this.id = 0;
        this.dream = "";
        this.date = Calendar.getInstance();
        this.date.setTimeInMillis(System.currentTimeMillis());
    }

    public String getDreamName() {
        return dreamName;
    }

    public void setDreamName(String dreamName) {
        this.dreamName = dreamName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDream() {
        return dream;
    }

    public void setDream(String dream) {
        this.dream = dream;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }
}
