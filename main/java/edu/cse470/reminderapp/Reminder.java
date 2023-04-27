package edu.cse470.reminderapp;
import java.util.Date;

public class Reminder {
    private final String recurrenceTime;
    private int id;
    private String title;
    private String details;
    private Date dateTime;
    private String notificationTime;

    public Reminder(int id, String title, String details, Date dateTime, String notificationTime, String recurrenceTime) {
        this.id = id;
        this.title = title;
        this.details = details;
        this.dateTime = dateTime;
        this.notificationTime = notificationTime;
        this.recurrenceTime = recurrenceTime;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDetails() {
        return this.details;
    }

    public Date getDateTime() {
        return this.dateTime;
    }

    public String getNotificationTime() {
        return this.notificationTime;
    }

    public String getRecurrenceTime(){ return this.recurrenceTime;}
}
