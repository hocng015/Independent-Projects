package edu.cse470.reminderapp;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.SystemClock;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderActivity extends AppCompatActivity {

    private TextView dateTimeDisplay;
    private Button dateTimeButton;
    private Calendar calendar;
    private Spinner notificationTimeSpinner;
    private EditText reminderTitleEditText, reminderDetailsEditText;
    private Button saveButton;

    // Add reminderId variable
    private int reminderId;

    private Spinner recurrenceSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_activity);

        dateTimeDisplay = findViewById(R.id.date_time_display);
        dateTimeButton = findViewById(R.id.date_time_button);
        notificationTimeSpinner = findViewById(R.id.notification_time_spinner);
        calendar = Calendar.getInstance();
        reminderTitleEditText = findViewById(R.id.title_edittext);
        reminderDetailsEditText = findViewById(R.id.details_edittext);
        saveButton = findViewById(R.id.save_button);
        recurrenceSpinner = findViewById(R.id.recurrence_time_spinner);

        // Get the reminderId from the intent
        Intent intent = getIntent();
        reminderId = intent.getIntExtra("reminderId", -1);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReminder();
            }
        });

        dateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Get the root view of your layout
        View rootView = findViewById(android.R.id.content).getRootView();

        // Add touch listener
        rootView.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(ReminderActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    View focusedView = getCurrentFocus();
                    if (focusedView instanceof EditText) {
                        hideKeyboard(focusedView);
                        focusedView.clearFocus();
                    }
                }
                return false;
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        // Populate the spinner with the notification frequency options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.notification_frequency, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notificationTimeSpinner.setAdapter(adapter);

        // Populate the spinner with recurrence options
        ArrayAdapter<CharSequence> recurrenceAdapter = ArrayAdapter.createFromResource(this,
                R.array.recurrence_options, android.R.layout.simple_spinner_item);
        recurrenceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recurrenceSpinner.setAdapter(recurrenceAdapter);

        // Load the reminder if reminderId exists
        if (reminderId != -1) {
            ReminderDataSource ds = new ReminderDataSource(this);
            try {
                ds.open();
                Reminder reminder = ds.getSpecificReminder(reminderId);
                ds.close();

                reminderTitleEditText.setText(reminder.getTitle());
                reminderDetailsEditText.setText(reminder.getDetails());
                calendar.setTime(reminder.getDateTime());
                updateDateTimeDisplay();

                // Set the spinner's selection based on the saved notification_time
                int notificationSelectedIndex = getNotificationTimeIndex(reminder.getNotificationTime());
                System.out.println("Notification selected index: " + notificationSelectedIndex);
                notificationTimeSpinner.setSelection(notificationSelectedIndex);

                // Set the spinner's selection based on the saved recurrence_time
                int recurrenceSelectedIndex = getRecurrenceTimeIndex(reminder.getRecurrenceTime());
                System.out.println("Recurrence selected index: " + recurrenceSelectedIndex);
                recurrenceSpinner.setSelection(recurrenceSelectedIndex);
            } catch (Exception e) {
                Toast.makeText(this, "Error retrieving reminder", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private int getNotificationTimeIndex(String notificationTime) {
        String[] notificationTimes = getResources().getStringArray(R.array.notification_frequency);
        for (int i = 0; i < notificationTimes.length; i++) {
            if (notificationTimes[i].equals(notificationTime)) {
                return i;
            }
        }
        return 0; // Default value if not found
    }

    private long convertRecurrenceTimeToMillis(int index) {
        long recurrenceTimeInMillis = 0;
        switch (index) {
            case 0: // None
                recurrenceTimeInMillis = 0;
                break;
            case 1: // Daily
                recurrenceTimeInMillis = 24 * 60 * 60 * 1000;
                break;
            case 2: // Weekly
                recurrenceTimeInMillis = 7 * 24 * 60 * 60 * 1000;
                break;
            case 3: // Monthly
                recurrenceTimeInMillis = 30 * 24 * 60 * 60 * 1000L; // Using a long (L) for larger values
                break;
            case 4: // Yearly
                recurrenceTimeInMillis = 365 * 24 * 60 * 60 * 1000L; // Using a long (L) for larger values
                break;
        }
        return recurrenceTimeInMillis;
    }

    private long convertNotificationTimeToMillis(int index) {
        long notificationTimeInMillis = 0;
        switch (index) {
            case 0:
                notificationTimeInMillis = 5 * 60 * 1000;
                break;
            case 1:
                notificationTimeInMillis = 15 * 60 * 1000;
                break;
            case 2:
                notificationTimeInMillis = 30 * 60 * 1000;
                break;
            case 3:
                notificationTimeInMillis = 1 * 60 * 60 * 1000;
                break;
            case 4:
                notificationTimeInMillis = 2 * 60 * 60 * 1000;
                break;
            case 5:
                notificationTimeInMillis = 24 * 60 * 60 * 1000;
                break;
        }
        return notificationTimeInMillis;
    }

    private int getRecurrenceTimeIndex(String recurrenceTime) {
        String[] recurrenceTimes = getResources().getStringArray(R.array.recurrence_options);
        for (int i = 0; i < recurrenceTimes.length; i++) {
            if (recurrenceTimes[i].equals(recurrenceTime)) {
                return i;
            }
        }
        return 0; // Default value if not found
    }

    private void showDatePickerDialog() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(ReminderActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                showTimePickerDialog();
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private void saveReminder() {
        String title = reminderTitleEditText.getText().toString();
        String details = reminderDetailsEditText.getText().toString();
        String notificationTime = notificationTimeSpinner.getSelectedItem().toString();
        String recurrenceTime = recurrenceSpinner.getSelectedItem().toString();
        Date dateTime = calendar.getTime();

        if (title.isEmpty() || details.isEmpty()) {
            Toast.makeText(this, "Please enter a title and details for your reminder.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set the id to -1 initially
        int id = -1;
        Reminder reminder = new Reminder(id, title, details, dateTime, notificationTime, recurrenceTime);

        ReminderDataSource ds = new ReminderDataSource(this);
        try {
            ds.open();
            if (reminderId == -1) {
                ds.createReminder(reminder);
            } else {
                reminder.setId(reminderId);
                ds.updateReminder(reminder);
            }
            ds.close();

            // Schedule the alarm for the reminder
            scheduleReminderAlarm(reminder);

            // Start ReminderListActivity
            Intent intent = new Intent(ReminderActivity.this, ReminderListActivity.class);
            startActivity(intent);

            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving reminder", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    private void scheduleReminderAlarm(Reminder reminder) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, ReminderAlarmReceiver.class);
        alarmIntent.putExtra("reminderId", reminder.getId());
        alarmIntent.putExtra("title", reminder.getTitle());

        // Use both FLAG_UPDATE_CURRENT and FLAG_IMMUTABLE flags
        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(this, reminder.getId(), alarmIntent, flags);

        // Calculate the notification time prior to the actual set date and time
        String notificationTime = reminder.getNotificationTime();
        int index = getNotificationTimeIndex(notificationTime);
        long notificationTimeInMillis = convertNotificationTimeToMillis(index);

        long alarmTimeInMillis = reminder.getDateTime().getTime() - notificationTimeInMillis;

        // Schedule the alarm to go off at the notification time
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, alarmPendingIntent);
        }

        // Schedule the alarm to go off at the exact datetime of the reminder
        PendingIntent exactAlarmPendingIntent = PendingIntent.getBroadcast(this, reminder.getId() + 100000, alarmIntent, flags);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder.getDateTime().getTime(), exactAlarmPendingIntent);
        }

        // Schedule the recurring alarm, if applicable
        String recurrenceTime = reminder.getRecurrenceTime();
        int recurrenceIndex = getRecurrenceTimeIndex(recurrenceTime);
        long recurrenceTimeInMillis = convertRecurrenceTimeToMillis(recurrenceIndex);

        if (recurrenceTimeInMillis > 0) {
            PendingIntent recurringAlarmPendingIntent = PendingIntent.getBroadcast(this, reminder.getId() + 200000, alarmIntent, flags);
            if (alarmManager != null) {
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, recurrenceTimeInMillis, recurringAlarmPendingIntent);
            }
        }
    }

    private void showTimePickerDialog() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(ReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                updateDateTimeDisplay();
            }
        }, hour, minute, false);

        timePickerDialog.show();
    }

    private void updateDateTimeDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
        String formattedDateTime = dateFormat.format(calendar.getTime());
        dateTimeDisplay.setText(formattedDateTime);
    }
}
