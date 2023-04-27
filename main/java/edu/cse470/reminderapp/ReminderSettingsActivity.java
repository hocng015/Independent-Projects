package edu.cse470.reminderapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.view.View;
import android.content.Intent;

public class ReminderSettingsActivity extends AppCompatActivity {

    private ImageButton viewAllRemindersButton;
    private ImageButton settingsButton;
    private Switch notificationSwitch;
    private RadioGroup sortOrderRadioGroup;
    private RadioButton chronologicalRadio;
    private RadioButton reverseChronologicalRadio;

    private SharedPreferences sharedPreferences;
    private static final String PREFERENCES_NAME = "ReminderSettings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        sharedPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);

        viewAllRemindersButton = findViewById(R.id.view_all_reminders_button);
        settingsButton = findViewById(R.id.settings_button);
        notificationSwitch = findViewById(R.id.notification_switch);
        sortOrderRadioGroup = findViewById(R.id.sort_order_radiogroup);
        chronologicalRadio = findViewById(R.id.chronological_radio);
        reverseChronologicalRadio = findViewById(R.id.reverse_chronological_radio);

        loadSettings();

        viewAllRemindersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReminderSettingsActivity.this, ReminderListActivity.class);
                startActivity(intent);
            }
        });

        settingsButton.setEnabled(false);

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notifications_enabled", isChecked);
            editor.apply();
        });

        sortOrderRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (checkedId == R.id.chronological_radio) {
                editor.putString("sort_order", "chronological");
            } else if (checkedId == R.id.reverse_chronological_radio) {
                editor.putString("sort_order", "reverse_chronological");
            }
            editor.apply();
        });
    }

    private void loadSettings() {
        boolean notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true);
        notificationSwitch.setChecked(notificationsEnabled);

        String sortOrder = sharedPreferences.getString("sort_order", "chronological");
        if (sortOrder.equals("chronological")) {
            chronologicalRadio.setChecked(true);
        } else if (sortOrder.equals("reverse_chronological")) {
            reverseChronologicalRadio.setChecked(true);
        }
    }
}
