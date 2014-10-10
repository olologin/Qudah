package com.example.olologin.qudah;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

public class TimePickDialogPreference extends DialogPreference {
    private static final String TAG = "TimePickDialogDialogPreference";
    private static final String TPDPNS = "timepickdialogpreference";
    private static final String DEFAULT_VALUE = "20141008T161300Z";
    private Drawable mMyIcon;
    private Time mTime;
    private TimePicker mTimePicker;
    public TimePickDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.timepick_dialog);

        mTime = new Time();

        setPositiveButtonText(R.string.Ok);
        setNegativeButtonText(R.string.Cancel);

        // Steal the XML dialogIcon attribute's value
        mMyIcon = getDialogIcon();
        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        mTimePicker = (TimePicker) view.findViewById(R.id.TimePickDialogPreferenceTimePicker);
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getContext());
        mTime.parse(preferences.getString(this.getKey(), DEFAULT_VALUE));
        mTimePicker.setIs24HourView(true);
        mTimePicker.setCurrentHour(mTime.hour);
        mTimePicker.setCurrentMinute(mTime.minute);

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == -1) {
            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(getContext());
            mTime.hour = mTimePicker.getCurrentHour();
            mTime.minute = mTimePicker.getCurrentMinute();
            mTime.second = 0;
            //mTime.normalize(false);
            preferences.edit().putString(this.getKey(), mTime.format2445()).commit();
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            mTime.parse(this.getPersistedString(DEFAULT_VALUE));
        } else {
            // Set default state from the XML attribute
            mTime.parse((String) defaultValue);
            persistString(mTime.format2445());
        }
    }

    @Override
    public String toString(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        Time t = new Time();
        t.parse(sp.getString(this.getKey(),DEFAULT_VALUE));
        return String.valueOf(t.hour)+":"+String.valueOf(t.minute);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }
}