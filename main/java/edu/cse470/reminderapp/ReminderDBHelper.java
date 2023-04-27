package edu.cse470.reminderapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ReminderDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "reminders.db";
    private static final int DATABASE_VERSION = 2;

    // Database creation SQL statement
    private static final String CREATE_TABLE_REMINDER =
            "CREATE TABLE reminder (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "title TEXT NOT NULL, details TEXT, "
                    + "datetime INTEGER, notification_time TEXT, recurrence_time TEXT);";

    public ReminderDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_REMINDER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ReminderDBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        if (oldVersion == 1 && newVersion == 2) {
            db.execSQL("ALTER TABLE reminder ADD COLUMN recurrence_time TEXT");
        } else {
            db.execSQL("DROP TABLE IF EXISTS reminder");
            onCreate(db);
        }
    }
}
