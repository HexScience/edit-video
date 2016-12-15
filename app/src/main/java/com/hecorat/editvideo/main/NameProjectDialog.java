package com.hecorat.editvideo.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.hecorat.editvideo.R;
import com.hecorat.editvideo.helper.Utils;

/**
 * Created by Bkmsx on 12/15/2016.
 */

public class NameProjectDialog extends DialogFragment {
    static MainActivity mActivity;

    EditText mEdtNameProject;

    public static NameProjectDialog newInstance(MainActivity activity) {
        mActivity = activity;
        return new NameProjectDialog();
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Name Project");

        View view = LayoutInflater.from(mActivity).inflate(R.layout.edt_name_project, null);
        mEdtNameProject = (EditText) view.findViewById(R.id.edt_name_project);
        mEdtNameProject.setText(Utils.getDefaultName());

        builder.setView(view);
        builder.setPositiveButton("Create Project", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mActivity.setLayoutFragmentVisible(false);
                mActivity.mProjectName = mEdtNameProject.getText().toString();
                long id = mActivity.mProjectTable.insertValue( mActivity.mProjectName,
                        System.currentTimeMillis()+"");
                mActivity.mProjectId = (int) id;
                mActivity.resetActivity();
                mActivity.hideStatusBar();
            }
        });

        builder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mActivity.hideStatusBar();
            }
        });
        return builder.create();
    }
}
