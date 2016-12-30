package com.hecorat.azplugin2.helper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.hecorat.azplugin2.R;

/**
 * Created by Bkmsx on 12/15/2016.
 */

public class NameDialog extends DialogFragment {
    static Context mContext;
    static String mInitText;

    static int mType;

    EditText mEdtName;

    DialogClickListener mCallBack;

    public static final int CREATE_PROJECT = 0;
    public static final int RENAME = 1;

    public static NameDialog newInstance(Context context, int type, String initText) {
        mContext = context;
        mType = type;
        mInitText = initText;
        return new NameDialog();
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(getTitle());

        View view = LayoutInflater.from(mContext).inflate(R.layout.edt_name_project, null);
        mEdtName = (EditText) view.findViewById(R.id.edt_name_project);
        mEdtName.setText(mInitText);

        builder.setView(view);
        builder.setPositiveButton(getPositiveBtnText(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            mCallBack.onPositiveClick(mEdtName.getText().toString(), mType);
            }
        });

        builder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            mCallBack.onNegativeClick();
            }
        });
        return builder.create();
    }

    private String getTitle() {
        switch (mType) {
            case CREATE_PROJECT:
                return getString(R.string.dialog_title_create_project);
            case RENAME:
                return getString(R.string.dialog_title_rename);
        }
        return "";
    }

    private String getPositiveBtnText() {
        switch (mType) {
            case CREATE_PROJECT:
                return getString(R.string.positive_btn_text_create_project);
            case RENAME:
                return getString(R.string.ok_btn);
        }
        return "";
    }

    public void setOnClickListener(DialogClickListener listener) {
        mCallBack = listener;
    }

    public interface DialogClickListener {
        void onPositiveClick(String name, int type);
        void onNegativeClick();
    }
}
