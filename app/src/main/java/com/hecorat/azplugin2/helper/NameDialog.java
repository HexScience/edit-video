package com.hecorat.azplugin2.helper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.hecorat.azplugin2.R;

/**
 * Created by Bkmsx on 12/15/2016.
 */

public class NameDialog extends DialogFragment {
    Context mContext;
    String mInitText;

    int mType;

    EditText mEdtName;

    DialogClickListener mCallBack;

    public static final int SAVE_PROJECT = 0;
    public static final int RENAME = 1;
    public static final int CREATE_PROJECT = 2;

    public static NameDialog newInstance(Context context, DialogClickListener listener, int type, String initText) {
        NameDialog nameDialog = new NameDialog();
        nameDialog.mContext = context;
        nameDialog.mType = type;
        nameDialog.mInitText = initText;
        nameDialog.mCallBack = listener;
        return nameDialog;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(getTitle());

        View view = LayoutInflater.from(mContext).inflate(R.layout.edt_name_project, null);
        mEdtName = (EditText) view.findViewById(R.id.edt_name_project);
        mEdtName.setText(mInitText);
        if(!TextUtils.isEmpty(mInitText)) mEdtName.setSelection(mInitText.length());

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
        String title = "";
        switch (mType) {
            case CREATE_PROJECT:
            case SAVE_PROJECT:
                title = getString(R.string.dialog_title_new_project_name);
                break;
            case RENAME:
                title = getString(R.string.dialog_title_rename_project);
                break;
            default: break;
        }
        return title;
    }

    private String getPositiveBtnText() {
        return getString(R.string.ok_btn);
    }

    public interface DialogClickListener {
        void onPositiveClick(String name, int type);
        void onNegativeClick();
    }
}
