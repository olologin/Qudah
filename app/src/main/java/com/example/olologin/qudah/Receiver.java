package com.example.olologin.qudah;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;

import java.io.IOException;

public class Receiver extends BroadcastReceiver {
    public Receiver() {
    }

    private static String secretNumber = "*#";

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if(intent.getAction() == Intent.ACTION_NEW_OUTGOING_CALL) {
            if (secretNumber.equals(intent.getStringExtra(intent.EXTRA_PHONE_NUMBER))) {
                setResultData(null);
                PackageManager p = context.getPackageManager();
                if(sp.getBoolean("isIconHidden", false))
                {
                    ComponentName componentName = new ComponentName(context, com.example.olologin.qudah.SettingsActivity.class);
                    p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                    sp.edit().putBoolean("isIconHidden", false).commit();
                }
                else {
                    ComponentName componentName = new ComponentName(context, com.example.olologin.qudah.SettingsActivity.class); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
                    p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    sp.edit().putBoolean("isIconHidden", true).commit();
                }
            }
            return;
        }

        if(intent.getAction() == Intent.ACTION_BOOT_COMPLETED){
            AlarmHelper.setAlarm(context);
            return;
        }

        Uri audioURI =
                Uri.parse(sp.getString("select_audio", "android.resource://com.example.olologin.qudah/raw/qudah_sound"));
        int volume = sp.getInt("sound_volume", 100);
        MediaPlayer mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, audioURI);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    AlarmHelper.setAlarm(context);
                }
            });
            mMediaPlayer.setVolume((float)0.01*volume, (float)0.01*volume);
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
