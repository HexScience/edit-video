package com.hecorat.azplugin2.dialogfragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.interfaces.DialogClickListener;
import com.hecorat.azplugin2.main.Constants;

/**
 * Created by bkmsx on 1/3/2017.
 */

public class DialogConfirm extends DialogFragment {
    DialogClickListener mCallback;
    int mType;
    Context mContext;
    String mDetail;

    public static DialogConfirm newInstance(Context context,
                                            DialogClickListener listener,
                                            int type, String detail) {
        DialogConfirm dialogConfirm = new DialogConfirm();
        dialogConfirm.mContext = context;
        dialogConfirm.mCallback = listener;
        dialogConfirm.mType = type;
        dialogConfirm.mDetail = detail;
        return dialogConfirm;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        DialogData dialogData = getDialogData();
        builder.setIcon(dialogData.iconId);
        builder.setTitle(dialogData.titleId);

        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_confirm_layout, null);
        TextView textView = (TextView) view.findViewById(R.id.text_view);
        textView.setText(mContext.getString(dialogData.messageId));
        builder.setView(view);

        if (mType != DialogClickListener.SAVE_PROJECT) {
            builder.setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mCallback.onPositiveClick(mType, mDetail);
                }
            });
        }

        if (mType != DialogClickListener.WARNING_DURATION_GIF &&
                mType != DialogClickListener.SAVE_PROJECT) {
            builder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mCallback.onNegativeClick(mType);
                }
            });
        }

        if (mType == DialogClickListener.SAVE_PROJECT){
            builder.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mCallback.onPositiveClick(mType, mDetail);
                }
            });
            builder.setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mCallback.onNegativeClick(mType);
                }
            });

            builder.setNeutralButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        }

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    private DialogData getDialogData() {
        DialogData dialogData = new DialogData();

        switch (mType) {
            case DialogClickListener.ASK_DONATE:
                dialogData.iconId = R.drawable.ic_dialog_donate;
                int messageId;
                int titleId;
                switch (mDetail){
                    case Constants.EVENT_ACTION_DIALOG_FROM_NEW_TRIM:
                        titleId = R.string.dialog_ask_premium_title;
                        messageId = R.string.dialog_ask_premium_trim_message;
                        break;
                    case Constants.EVENT_ACTION_DIALOG_FROM_NEW_GIF:
                        titleId = R.string.dialog_ask_premium_title;
                        messageId = R.string.dialog_ask_premium_gif_message;
                        break;
                    case Constants.EVENT_ACTION_DIALOG_FROM_WATERMARK:
                        titleId = R.string.dialog_ask_premium_remove_watermark_title;
                        messageId = R.string.dialog_ask_premium_remove_watermark_message;
                        break;
                    default:
                        titleId = R.string.dialog_ask_premium_title;
                        messageId = R.string.dialog_ask_premium_remove_watermark_message;
                        break;
                }
                dialogData.titleId = titleId ;
                dialogData.messageId = messageId;
                break;
            case DialogClickListener.DELETE_VIDEO:
                dialogData.iconId = R.drawable.ic_delete_2;
                dialogData.titleId = R.string.dialog_title_delete_video;
                dialogData.messageId = R.string.dialog_msg_delete_video;
                break;
            case DialogClickListener.DELETE_IMAGE:
                dialogData.iconId = R.drawable.ic_delete_2;
                dialogData.titleId = R.string.dialog_title_delete_image;
                dialogData.messageId = R.string.dialog_msg_delete_image;
                break;
            case DialogClickListener.DELETE_TEXT:
                dialogData.iconId = R.drawable.ic_delete_2;
                dialogData.titleId = R.string.dialog_title_delete_text;
                dialogData.messageId = R.string.dialog_msg_delete_text;
                break;
            case DialogClickListener.DELETE_AUDIO:
                dialogData.iconId = R.drawable.ic_delete_2;
                dialogData.titleId = R.string.dialog_title_delete_audio;
                dialogData.messageId = R.string.dialog_msg_delete_audio;
                break;
            case DialogClickListener.DELETE_PROJECT:
                dialogData.iconId = R.drawable.ic_delete_2;
                dialogData.titleId = R.string.dialog_title_delete_project;
                dialogData.messageId = R.string.dialog_msg_delete_project;
                break;
            case DialogClickListener.OVERWRITE_FILE:
                dialogData.iconId = R.drawable.ic_overwrite;
                dialogData.titleId = R.string.dialog_title_overwrite_file;
                dialogData.messageId = R.string.dialog_msg_overwrite_file;
                break;
            case DialogClickListener.WARNING_DURATION_GIF:
                dialogData.iconId = R.drawable.ic_gif_duration;
                dialogData.titleId = R.string.dialog_title_gif_duration_warning;
                dialogData.messageId = R.string.dialog_msg_gif_duration_warning;
                break;
            case DialogClickListener.SAVE_PROJECT:
                dialogData.iconId = R.drawable.ic_save;
                dialogData.titleId = R.string.dialog_title_save_project;
                dialogData.messageId = R.string.dialog_msg_save_project;
                break;
        }

        return dialogData;
    }

    private class DialogData {
        int iconId;
        int titleId, messageId;
    }
}
