package edu.cse470.reminderapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ReminderListActivity extends AppCompatActivity {

    private FloatingActionButton addReminderFAB;
    private ImageButton viewAllRemindersButton;
    private ImageButton settingsButton;
    private RecyclerView reminderList;
    private ReminderAdapter reminderAdapter;
    private ArrayList<Reminder> reminders;
    private static final String CHANNEL_ID = "reminder_notification_channel";
    private SearchView searchView;

    private SharedPreferences sharedPreferences;
    private static final String PREFERENCES_NAME = "ReminderSettings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_list_activity);

        addReminderFAB = findViewById(R.id.add_reminder_fab);
        viewAllRemindersButton = findViewById(R.id.view_all_reminders_button);
        settingsButton = findViewById(R.id.settings_button);
        Switch s = findViewById(R.id.switchDelete);
        searchView = findViewById(R.id.searchView);

        // Disable view_all_reminders_button
        viewAllRemindersButton.setEnabled(false);
        settingsButton.setEnabled(true);

        // Initialize sharedPreferences object
        sharedPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);

        addReminderFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReminderListActivity.this, ReminderActivity.class);
                startActivity(intent);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReminderListActivity.this, ReminderSettingsActivity.class);
                startActivity(intent);
            }
        });

        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                boolean status = compoundButton.isChecked();
                reminderAdapter.setDelete(status);
                reminderAdapter.notifyDataSetChanged();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                reminderAdapter.getFilter().filter(newText);
                return false;
            }
        });

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH; // Set importance to IMPORTANCE_HIGH
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 100});

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        ReminderDataSource ds = new ReminderDataSource(this);
        try {
            ds.open();
            String sortOrder = sharedPreferences.getString("sort_order", "chronological");
            boolean deleteEnabled = sharedPreferences.getBoolean("delete_enabled", false);
            reminders = ds.getAllReminders(sortOrder);
            ds.close();

            if (reminders.size() > 0) {
                reminderList = findViewById(R.id.reminder_list);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                reminderList.setLayoutManager(layoutManager);
                reminderAdapter = new ReminderAdapter(reminders, this);
                reminderAdapter.setDelete(deleteEnabled);
                reminderList.setAdapter(reminderAdapter);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error retrieving reminders", Toast.LENGTH_LONG).show();
        }
    }
}
