package com.hecorat.editvideo.export;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.hecorat.editvideo.R;
import com.hecorat.editvideo.main.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by TienDam on 11/29/2016.
 */

public class ConfirmExport extends DialogFragment{
    static MainActivity mActivity;
    public static ConfirmExport newInstance(MainActivity activity){
        mActivity = activity;
        return new ConfirmExport();
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setIcon(R.drawable.ic_confirm_export);
        builder.setTitle(R.string.confirm_export_title);
        final View view = LayoutInflater.from(mActivity).inflate(R.layout.choose_quality_export, null);
        builder.setView(view);
        final EditText editText = (EditText) view.findViewById(R.id.edt_output_name);
        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.quality_groupradio);
        String defaultName = new SimpleDateFormat("yy_MM_dd_HH_mm_ss", Locale.getDefault())
                .format(new Date(System.currentTimeMillis()));
        editText.setText(defaultName);
        builder.setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int id = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) view.findViewById(id);
                int quality = Integer.parseInt(radioButton.getTag().toString());
                String name = editText.getText().toString();
                mActivity.exportVideo(name, quality);
                mActivity.hideStatusBar();
            }
        });
        builder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mActivity.hideStatusBar();
            }
        });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
