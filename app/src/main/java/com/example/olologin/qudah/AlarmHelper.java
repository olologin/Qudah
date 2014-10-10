package com.example.olologin.qudah;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.Time;

import java.util.Random;

/**
 * Created by user on 10/10/14.
 */
public class AlarmHelper {
    public static Time setAlarm(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Time time = new Time();
        time.parse(sp.getString("time", ""));

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Receiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        Random r = new Random();
        int randomMinute = r.nextInt(sp.getInt("time_variation", 0));
        Time alarmTime = getNextAlarmTime(context, time, randomMinute);
        am.set(AlarmManager.RTC_WAKEUP, alarmTime.toMillis(false), pi);

        sp.edit().putString("alarm_time", alarmTime.format("Next alarm %Y/%m/%d %H:%M:%S")).commit();
        return alarmTime;
    }

    public static Time getNextAlarmTime(Context context, Time time, int randomMinute) {
        Time now = new Time();
        Time alarmTime = new Time();
        now.setToNow();
        alarmTime.set(now);
        alarmTime.set(now.second, time.minute, time.hour, now.monthDay, now.month, now.year);
        alarmTime.normalize(false);
        alarmTime.minute += randomMinute;
        alarmTime.normalize(false);
        if (alarmTime.before(now)){
            alarmTime.monthDay += 1;
            alarmTime.normalize(false);
        }

        return alarmTime;
    }

    public static void cancelAlarm(Context context){
        Intent i = new Intent(context, Receiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pi);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString("alarm_time", "").commit();
    }
}
