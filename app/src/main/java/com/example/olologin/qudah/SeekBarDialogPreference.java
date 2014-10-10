package com.example.olologin.qudah;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * @hide
 */
public class SeekBarDialogPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "SeekBarDialogPreference";
    private static final String SBDPNS = "seekbardialogpreference";
    private static int DEFAULT_VALUE = 100;
    private Drawable mMyIcon;
    private int mMaxValue;
    private int mMinValue;
    private int mInterval;
    protected int mProgress;
    private String mUnitsLeft = "";
    private String mUnitsRight = "";
    private TextView seekBarPrefValue;

    public SeekBarDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.seekbar_dialog);

        mMaxValue = attrs.getAttributeIntValue(SBDPNS, "max", -1);
        mMinValue = attrs.getAttributeIntValue(SBDPNS, "min", -1);
        mInterval = attrs.getAttributeIntValue(SBDPNS, "interval", -1);
        mUnitsLeft = attrs.getAttributeValue(SBDPNS, "leftUnits");
        mUnitsRight = attrs.getAttributeValue(SBDPNS, "rightUnits");

        setPositiveButtonText(R.string.Ok);
        setNegativeButtonText(R.string.Cancel);


        // Steal the XML dialogIcon attribute's value
        mMyIcon = getDialogIcon();
        setDialogIcon(null);
    }

    //TODO Правильно написать всё
    // Когда это вызывается? Когда можно сохранять переменные?
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        final ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        final TextView unitsLeft = (TextView) view.findViewById(R.id.seekBarPrefUnitsLeft);
        final TextView unitsRight = (TextView) view.findViewById(R.id.seekBarPrefUnitsRight);
        final SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekbar);
        seekBarPrefValue = (TextView) view.findViewById(R.id.seekBarPrefValue);
        unitsRight.setText(mUnitsRight);
        unitsLeft.setText(mUnitsLeft);

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getContext());

        seekBar.setMax(mMaxValue - mMinValue);
        seekBar.setOnSeekBarChangeListener(this);

        mProgress = preferences.getInt(this.getKey(), DEFAULT_VALUE);
        seekBar.setProgress(mProgress);

        if (mMyIcon != null) {
            iconView.setImageDrawable(mMyIcon);
        } else {
            iconView.setVisibility(View.GONE);
        }
    }

    protected SeekBar getSeekBar(View dialogView) {
        return (SeekBar) dialogView.findViewById(R.id.seekbar);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        //mMediaPlayer.release();
        if (which == -1) {
            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(getContext());
            preferences.edit().putInt(this.getKey(), mProgress).commit();
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            mProgress = this.getPersistedInt(DEFAULT_VALUE);
        } else {
            // Set default state from the XML attribute
            mProgress = (Integer) defaultValue;
            persistInt(mProgress);

        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, DEFAULT_VALUE);
    }

    // Below methods from OnSeekBarChangeListener
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        mProgress = seekBar.getProgress();
        seekBarPrefValue.setText(String.valueOf(mProgress));
    }

    @Override
    public String toString(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        return String.valueOf(sp.getInt(this.getKey(),DEFAULT_VALUE))+mUnitsRight;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}