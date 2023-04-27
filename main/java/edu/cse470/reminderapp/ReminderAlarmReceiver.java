package edu.cse470.reminderapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class ReminderAlarmReceiver extends BroadcastReceiver {
    public static final String REMINDER_NOTIFICATION_CHANNEL_ID = "reminder_notification_channel";
    private static final String TAG = "ReminderAlarmReceiver";
    private static final String PREFERENCES_NAME = "ReminderSettings";

    @Override
    public void onReceive(Context context, Intent intent) {
        int reminderId = intent.getIntExtra("reminderId", -1);
        String title = intent.getStringExtra("title");
        Log.d(TAG, "Alarm triggered, preparing notification");

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        boolean notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true);

        if (notificationsEnabled) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Notification notification = new NotificationCompat.Builder(context, REMINDER_NOTIFICATION_CHANNEL_ID)
                    .setContentTitle("Reminder")
                    .setContentText(title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .build();

            notificationManager.notify(reminderId, notification);
        }
    }

}
