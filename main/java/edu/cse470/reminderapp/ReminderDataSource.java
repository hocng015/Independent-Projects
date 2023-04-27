package edu.cse470.reminderapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Date;

import java.util.ArrayList;

public class ReminderDataSource {

    private SQLiteDatabase database;
    private ReminderDBHelper dbHelper;

    public ReminderDataSource(Context context) {
        dbHelper = new ReminderDBHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean createReminder(Reminder reminder) {
        boolean didSucceed = false;
        try {
            ContentValues initialValues = new ContentValues();

            initialValues.put("title", reminder.getTitle());
            initialValues.put("details", reminder.getDetails());
            initialValues.put("datetime", reminder.getDateTime().getTime());
            initialValues.put("notification_time", reminder.getNotificationTime());
            initialValues.put("recurrence_time", reminder.getRecurrenceTime());

            didSucceed = database.insert("reminder", null, initialValues) > 0;
        } catch (Exception e) {
            // Do nothing - will return false if there is an exception
        }
        return didSucceed;
    }

    public boolean updateReminder(Reminder reminder) {
        boolean didSucceed = false;
        try {
            int id = reminder.getId();
            ContentValues updateValues = new ContentValues();

            updateValues.put("title", reminder.getTitle());
            updateValues.put("details", reminder.getDetails());
            updateValues.put("datetime", reminder.getDateTime().getTime());
            updateValues.put("notification_time", reminder.getNotificationTime());
            updateValues.put("recurrence_time", reminder.getRecurrenceTime());

            didSucceed = database.update("reminder", updateValues, "_id=" + id, null) > 0;
        } catch (Exception e) {
            // Do nothing - will return false if there is an exception
        }
        return didSucceed;
    }

    public ArrayList<Reminder> getAllReminders(String sortOrder) {
        ArrayList<Reminder> reminders = new ArrayList<>();
        try {
            String query = "SELECT * FROM reminder";
            if (sortOrder.equals("chronological")) {
                query += " ORDER BY datetime ASC";
            } else if (sortOrder.equals("reverse_chronological")) {
                query += " ORDER BY datetime DESC";
            }
            Cursor cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex("_id");
                    int titleIndex = cursor.getColumnIndex("title");
                    int detailsIndex = cursor.getColumnIndex("details");
                    int dateTimeIndex = cursor.getColumnIndex("datetime");
                    int notificationTimeIndex = cursor.getColumnIndex("notification_time");
                    int recurrenceTimeIndex = cursor.getColumnIndex("recurrence_time");

                    if (idIndex != -1 && titleIndex != -1 && detailsIndex != -1 && dateTimeIndex != -1 && notificationTimeIndex != -1 && recurrenceTimeIndex != -1) {
                        Reminder reminder = new Reminder(
                                cursor.getInt(idIndex),
                                cursor.getString(titleIndex),
                                cursor.getString(detailsIndex),
                                new Date(cursor.getLong(dateTimeIndex)),
                                cursor.getString(notificationTimeIndex),
                                cursor.getString(recurrenceTimeIndex)
                        );
                        reminders.add(reminder);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            reminders = new ArrayList<>();
        }
        return reminders;
    }

    public Reminder getSpecificReminder(int id) {
        Reminder reminder = null;
        String query = "SELECT * FROM reminder WHERE _id =" + id;
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("_id");
            int titleIndex = cursor.getColumnIndex("title");
            int detailsIndex = cursor.getColumnIndex("details");
            int dateTimeIndex = cursor.getColumnIndex("datetime");
            int notificationTimeIndex = cursor.getColumnIndex("notification_time");
            int recurrenceTimeIndex = cursor.getColumnIndex("recurrence_time");

            if (idIndex != -1 && titleIndex != -1 && detailsIndex != -1 && dateTimeIndex != -1 && notificationTimeIndex != -1 && recurrenceTimeIndex != -1) {
                reminder = new Reminder(
                        cursor.getInt(idIndex),
                        cursor.getString(titleIndex),
                        cursor.getString(detailsIndex),
                        new Date(cursor.getLong(dateTimeIndex)),
                        cursor.getString(notificationTimeIndex),
                        cursor.getString(recurrenceTimeIndex)
                );
            }
        }
        cursor.close();
        return reminder;
    }


    public boolean deleteReminder(int id) {
        boolean didDelete = false;
        try {
            didDelete = database.delete("reminder", "_id=" + id, null) > 0;
        } catch (Exception e) {
            // Do nothing - return value already set to false
        }
        return didDelete;
    }
}

