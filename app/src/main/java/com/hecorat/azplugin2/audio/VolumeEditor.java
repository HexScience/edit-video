package com.hecorat.azplugin2.audio;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.main.Constants;
import com.hecorat.azplugin2.main.MainActivity;

/**
 * Created by TienDam on 11/24/2016.
 */

public class VolumeEditor extends DialogFragment {
    MainActivity mActivity;
    SeekBar mSeekBar;
    TextView mTextView;
    static int mVolume;

    public static VolumeEditor newInstance(MainActivity activity, int volume) {
        VolumeEditor volumeEditor = new VolumeEditor();
        volumeEditor.mActivity = activity;
        mVolume = volume;
        return volumeEditor;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View view = LayoutInflater.from(mActivity).inflate(R.layout.dialogfragment_volume_editor, null);
        mSeekBar = (SeekBar) view.findViewById(R.id.seekbar_volume);
        mTextView = (TextView) view.findViewById(R.id.txt_volume_value);
        mSeekBar.setMax(Constants.MAX_VOLUME);
        mSeekBar.setProgress(mVolume);
        mSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        String text = mVolume + "%";
        mTextView.setText(text);
        builder.setView(view);
        builder.setTitle(R.string.volume_editor_title);
        builder.setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mActivity.onVolumeChanged(mVolume);
            }
        });
        builder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mActivity.hideStatusBar();
            }
        });
        builder.setOnKeyListener(onKeyListener);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            String text = progress + "%";
            mTextView.setText(text);
            mVolume = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    DialogInterface.OnKeyListener onKeyListener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK){
                mActivity.hideStatusBar();
            }
            return false;
        }
    };
}
