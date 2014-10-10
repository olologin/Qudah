package com.example.olologin.qudah;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String LOG_TAG = "SettingsActivity";
    private static final int PICK_AUDIO = 1;
    private SharedPreferences preferences;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
        PreferenceManager.setDefaultValues(this, R.xml.pref, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Audio file preference
        findPreference("select_audio").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select mp3 file"), PICK_AUDIO);
                return true;
            }
        });
        updateSummary(preferences, "select_audio");
        updateSummary(preferences, "time");
        updateSummary(preferences, "sound_volume");
        updateSummary(preferences, "time_variation");
        updateSummary(preferences, "alarm_time");
    }
    @Override
    protected void onStart() {
        preferences.registerOnSharedPreferenceChangeListener(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp,
                                          String key) {
        if (key.equals("time")){
            AlarmHelper.cancelAlarm(getApplicationContext());
            AlarmHelper.setAlarm(getApplicationContext());
        } else if (key.equals("isAlarmEnabled")) {
            if(sp.getBoolean(key, false)){

                AlarmHelper.setAlarm(getApplicationContext());
            }
            else {
                AlarmHelper.cancelAlarm(getApplicationContext());
            }
        } else if (key.equals("isIconHidden")) {}
        else {
            updateSummary(sp, key);
        }
    }

    private void updateSummary(SharedPreferences sp, String key) {
        Preference p;
        if(key.equals("alarm_time")){
            p = findPreference("isAlarmEnabled");
            if(sp.getBoolean("isAlarmEnabled", false)){
                p.setSummary(sp.getString("alarm_time", "Ooops, something went wrong"));
            }
            else {
                p.setSummary("Alarm disabled");
            }
        } else if (key.equals("sound_volume") || key.equals("time_variation")){
            p = findPreference(key);
            p.setSummary(String.valueOf(sp.getInt(key, 100)));
        } else if(key.equals("select_audio")){
            p = findPreference(key);
            p.setSummary(sp.getString(key, "android.resource://com.example.olologin.qudah/raw/qudah_sound"));
        } else if(key.equals("time")){
            p = findPreference(key);
            Time t = new Time();
            t.parse(sp.getString(key, "Ooops, something went wrong"));
            p.setSummary(t.format("%H:%M"));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent audioReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, audioReturnedIntent);

        if(requestCode != PICK_AUDIO)
            Log.d(LOG_TAG, "Unknown resultcode");
        switch (resultCode) {
            case RESULT_OK: {
                Uri selectedAudio = audioReturnedIntent.getData();
                preferences.edit().putString("select_audio", selectedAudio.toString()).commit();
                break;
            }
            case RESULT_CANCELED :
                Log.d(LOG_TAG, "Audio canceled! ");
                break;
            default:
                Log.d(LOG_TAG, "Another problem");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_reset:
                AlertDialog.Builder d = new AlertDialog.Builder(this);
                d.setMessage("Do you want to reset preferences?");
                d.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        preferences.edit().clear().commit();
                        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.pref, true);
                        finish();

                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                    }
                });
                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                d.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
