package com.example.olologin.qudah;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by user on 10/8/14.
 */
public class VolumePickDialogPreference extends SeekBarDialogPreference {

    private MediaPlayer mMediaPlayer;

    public VolumePickDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onBindDialogView(View view) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getContext());
        //TODO
        Uri audioURI =
                Uri.parse(preferences.getString("select_audio", "android.resource://com.example.olologin.qudah/raw/qudah_sound"));
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(getContext(), audioURI);
            mMediaPlayer.prepare();
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onBindDialogView(view);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
    }

    // Below methods from OnSeekBarChangeListener
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        super.onProgressChanged(seekBar, i, b);
        mMediaPlayer.setVolume((float)0.01*super.mProgress, (float)0.01*super.mProgress);
    }
}
