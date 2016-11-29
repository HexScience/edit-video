package com.hecorat.editvideo.audio;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hecorat.editvideo.R;

/**
 * Created by TienDam on 11/24/2016.
 */

public class VolumeEditor extends DialogFragment {
    static Context mContext;
    SeekBar mSeekBar;
    TextView mTextView;
    static int mVolume;

    static int MAX_VOLUME = 100;
    public static VolumeEditor newInstance(Context context, int volume) {
        mContext = context;
        mVolume = volume;
        return new VolumeEditor();
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialogfragment_volume_editor, null);
        mSeekBar = (SeekBar) view.findViewById(R.id.seekbar_volume);
        mTextView = (TextView) view.findViewById(R.id.txt_volume_value);
        mSeekBar.setMax(MAX_VOLUME);
        mSeekBar.setProgress(mVolume);
        mSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mTextView.setText(mVolume+"%");
        builder.setView(view);
        builder.setTitle(R.string.volume_editor_title);
        builder.setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((OnVolumeChangedListener) mContext).onVolumeChanged(mVolume);
            }
        });
        builder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        Dialog dialog = builder.create();
        return dialog;
    }

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mTextView.setText(progress+"%");
            mVolume = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    public interface OnVolumeChangedListener {
        void onVolumeChanged(int volume);
    }
}
