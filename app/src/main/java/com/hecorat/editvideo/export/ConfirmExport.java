package com.hecorat.editvideo.export;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.hecorat.editvideo.R;
import com.hecorat.editvideo.main.MainActivity;

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
        builder.setMessage(R.string.confirm_export_msg);
        builder.setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mActivity.exportVideo();
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
