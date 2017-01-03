package com.hecorat.azplugin2.donate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.interfaces.DialogClickListener;

/**
 * Created by bkmsx on 1/3/2017.
 */

public class DialogAskDonate extends DialogFragment {
    static DialogClickListener mCallback;

    public static DialogAskDonate newInstance(DialogClickListener listener){
        mCallback = listener;
        return new DialogAskDonate();
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.dialog_ask_premium_title);
        builder.setMessage(R.string.dialog_ask_premium_message);
        builder.setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCallback.onPositiveClick(DialogClickListener.ASK_DONATE);
            }
        });

        builder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCallback.onNegativeClick(DialogClickListener.ASK_DONATE);
            }
        });

        return builder.create();
    }
}
